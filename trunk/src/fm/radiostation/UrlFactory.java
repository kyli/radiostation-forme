package fm.radiostation;

import java.io.IOException;
import java.util.Vector;

import javax.microedition.io.HttpConnection;

import net.rim.device.api.servicebook.ServiceBook;
import net.rim.device.api.servicebook.ServiceRecord;
import net.rim.device.api.system.CoverageInfo;
import net.rim.device.api.system.WLANInfo;


/**
 * @author Marcus Watkins (marcus@versatilemonkey.com)
 * @version 1.0 (June 24, 2009) 
 * http://www.versatilemonkey.com<p><p>
 * 
 * This code is public domain, do with it whatever you wish.
 * <p>
 * This class aims to simplify HTTP requests on the BlackBerry platform. It could be extended
 * for other uses (socket, specifically), but that exercise is left up to the reader.<p>
 * Use of this class will require signing your application using RIM supplied signing keys.
 * <p>
 * The BlackBerry platform provides a multitude of different transports for network access. These include WiFi, BES, BIS, WAP2 and Direct TCP.
 * <p>
 * Not all transports are available on all devices, carriers or service plans. Ordinarily an application must determine on its own which 
 * transports are available for a given device, and attempt to connect via them in order.
 * 
 * <p>
 * Notes about the various transports from my experience:
 * <p>
 * <b>Wifi:</b>
 * <p>
 * Wifi is the least cost to the user and is also the fasted by orders of magnitude
 * <p><p>
 * <b>BES/BIS:</b>
 * <p>
 * - These are largely the same, except BES goes through the user's corporate network and may be subject to corporate firewalls<p>
 * - BES/BIS are generally offered as unlimited use to anyone with a BlackBerry specific data plan<p>
 * - There is usually a limit imposed on the size of files that can be retrieved (usually 5mb, but can be as low as 100kb)<p>
 * <p>
 * <b>Direct TCP and WAP2:</b><p>
 * - These transports use carrier data plans which are sometimes billed at the same rate as BES/BIS, but sometimes are billed
 * separately. It is possible for users to be on unlimited data plans via BES/BIS but be charged per MB for TCP and WAP2. I
 * have never seen the reverse, however.<p>
 * - Some carriers do not have limits on the file size, others will timeout if you request a file over their limit (instead of a 413 error or similar)<p>
 * - Some carriers have intermediary proxies that will alter the content of returned files (wrapping them in carrier specific content, for example)<p>
 * <p>
 * <p>
 * Good luck, I hope this makes networking on BlackBerry easier for you.
 * <p>
 * <strong>Modified by kaiyi li, 4 Oct. 2009.</strong> 
 * <p>
 * In stead of generating the http connection that is appropriate for the connection availability, the connection url 
 * is generated with {@link UrlFactory#appendRimConnectionParam(String, String)}.
 * <p>
 * The Type name is changed from <code>HttpConnectionFactory</code> to <code>UrlFactory</code>
 */
public class UrlFactory {

	
	/**
	 * Specifies that only wifi should be used
	 */
	public static final int TRANSPORT_WIFI = 1;
	
	/**
	 * Specifies that only BES (also known as MDS or corporate servers)
	 */
	public static final int TRANSPORT_BES = 2;
	
	/**
	 * Specifies that only BIS should be used (Basically RIM hosted BES)
	 */
	public static final int TRANSPORT_BIS = 4;
	
	/**
	 * Specifies that TCP should be used (carrier transport)
	 */
	public static final int TRANSPORT_DIRECT_TCP = 8;
	
	/**
	 * Specifies that WAP2 should be used (carrier transport)
	 */
	public static final int TRANSPORT_WAP2 = 16;
	
	/**
	 * Equivalent to: TRANSPORT_WIFI | TRANSPORT_BES | TRANSPORT_BIS | TRANSPORT_DIRECT_TCP | TRANSPORT_WAP2
	 */
	public static final int TRANSPORTS_ANY = TRANSPORT_WIFI | TRANSPORT_BES | TRANSPORT_BIS | TRANSPORT_DIRECT_TCP | TRANSPORT_WAP2;
	
	/**
	 * Equivalent to: TRANSPORT_WIFI | TRANSPORT_BES | TRANSPORT_BIS
	 */
	public static final int TRANSPORTS_AVOID_CARRIER = TRANSPORT_WIFI | TRANSPORT_BES | TRANSPORT_BIS;
	
	/**
	 * Equivalent to: TRANSPORT_DIRECT_TCP | TRANSPORT_WAP2
	 */
	public static final int TRANSPORTS_CARRIER_ONLY = TRANSPORT_DIRECT_TCP | TRANSPORT_WAP2;
	
	/**
	 * The default order in which selected transports will be attempted
	 * 
	 */
	public static final int DEFAULT_TRANSPORT_ORDER[] = { TRANSPORT_WIFI, TRANSPORT_BES, TRANSPORT_BIS, TRANSPORT_DIRECT_TCP, TRANSPORT_WAP2 };
	
	private static final int TRANSPORT_COUNT = DEFAULT_TRANSPORT_ORDER.length;
	
	private static ServiceRecord srMDS[], srBIS[], srWAP2[], srWiFi[];
	private static boolean serviceRecordsLoaded = false;

	public static boolean forceWifi;
	
	private int curIndex = 0;
	private int curSubIndex = 0;
	private int transports[];
	private int lastTransport = 0;
	
	/**
	 * Creates a factory specifying the target http/https url and mask containing which transports should be allowed (default order) 
	 * 
	 * This method converts allowedTransports into an array of transports to use arranging
	 * the included transports in the order specified by {@link #DEFAULT_TRANSPORT_ORDER}
	 * Once the translation is complete it is equivalent to calling:
	 * <p>
	 * <code>
	 * HttpConnectionFactory( String url, String extraParameters, int transportPriority[] );
	 * </code>
	 * <p>
	 * But only the transports matching the input mask are included in the array.
	 * 
	 * @param url See {@link #HttpConnectionFactory(String, String, int[])}
	 * @param extraParameters See {@link #HttpConnectionFactory(String, String, int[])}
	 * @param allowedTransports A set of transports that should be allowed, for example, to set only wifi and BES use: TRANSPORT_WIFI | TRANSPORT_BES
	 */
	public UrlFactory( int allowedTransports ) {
		this( transportMaskToArray( allowedTransports ) );
	}
	
	/**
	 * Creates a factory specifying the target http/https url and ordered list of transports to attempt
	 * 
	 * This method constructs an <code>HttpConnectionFactory</code> for the URL specified, using any extra connection parameters
	 * specified in <code>extraParemeters</code> that will follow the order of transports specified in <code>transportPriority</code>
	 * <p>
	 * Transports not in <code>transportPriority</code> are not used in any order. The order of transports attempted will follow the order the
	 * are presented in <code>transportPriority</code>
	 * 
	 * <code>extraParameters</code> are additional parameters you want added to the connection string, each must be preceded by a semicolon. These are some useful ones:<p>
	 * ConnectionTimeout=120000 (2 minute connection timeout)<p>
	 * EncryptRequired=true (No idea what this does)<p>
	 * Example: ";ConnectionTimeout=120000;EncryptRequired=true"
	 * See http://www.blackberryforums.com/developer-forum/113137-undocumented-connector-open-parameters.html for more info.
	 * 
	 * @param url The url to generate the HttpConnection for (only http and https)
	 * @param extraParameters Extra parameters that will get appended to the end of the final url used in {@link #javax.microedition.io.Connector.open( String ) }
	 * @param transportPriority An array of transports in the order they should be attempted
	 */
	public UrlFactory( int transportPriority[] ) {
		if( !serviceRecordsLoaded ) {
			loadServiceBooks( false );
		}
		transports = transportPriority;
	}
	
	
	/**
	 * Generates an url by appending the appropriate rim connection parameters
	 * using the next available transport according to the order specified
	 * during factory creation to the url specified in factory creation. See the
	 * class description for details on use.
	 * 
	 * @return An HttpConnection using the next transport configured during factory creation
	 * @throws NoMoreTransportsException No more transports are available to use
	 */
	public String appendRimConnectionParam(String url, String extraParam) {
		String param = null;
		int curTransport = 0;
		curIndex = 0;
		while( param == null && curIndex < transports.length ) {
			curTransport = transports[curIndex];
			if (forceWifi) {
				curTransport = TRANSPORT_WIFI;
			}
			switch( curTransport ) {
				case TRANSPORT_WIFI:
					curIndex++;
					curSubIndex = 0;
					try { param = getWifiConnection(); } catch( Exception e ) { }
					break;
				case TRANSPORT_BES:
					curIndex++;
					curSubIndex = 0;
					try { param = getBesConnection(); } catch( Exception e ) { }
					break;
				case TRANSPORT_BIS:
					while( param == null ) {
						try { 
							param = getBisConnection( curSubIndex++ );
						} 
						catch( NoMoreTransportsException e ) { 
							curIndex++;
							curSubIndex = 0;
							break;
						}
						catch( Exception e ) { }
					}
					break;
				case TRANSPORT_DIRECT_TCP:
					curIndex++;
					try { param = getTcpConnection(); } catch( Exception e ) { }
					break;
				case TRANSPORT_WAP2:
					while( param == null ) {
						try {
							param = getWap2Connection( curSubIndex++ );
						}
						catch( NoMoreTransportsException e ) {
							curIndex++;
							curSubIndex = 0;
							break;
						}
						catch( Exception e ) { }
					}
					break;
			}
		}
		if( param != null ) {
			url+=param;
		}
		if (extraParam != null) {
			url+=extraParam;
		}
		lastTransport = curTransport;
		
		return url;
	}
	
	/**
	 * Returns the transport used in the connection last returned via {@link #getNextConnection()}
	 * @return the transport used in the connection last returned via {@link #getNextConnection()} or 0 if none
	 */
	public int getLastTransport() {
		return lastTransport;
	}
	
	/**
	 * Generates a connection parameter using the BIS transport if available
	 * 
	 * @param index The index of the service book to use
	 * @return An {@link HttpConnection} if this transport is available, otherwise null
	 * @throws NoMoreTransportsException
	 * @throws IOException throws exceptions generated by {@link getConnection( String transportExtras1, String transportExtras2 )}
	 */
	private String getBisConnection( int index ) throws NoMoreTransportsException, IOException {
		if( index >= srBIS.length ) {
			throw new NoMoreTransportsException();
		}
		ServiceRecord sr = srBIS[index];
		return ";deviceside=false;connectionUID="+sr.getUid();
	}
	/**
	 * Generates a connection parameter using the BES transport if available
	 * 
	 * @return An {@link HttpConnection} if this transport is available, otherwise null
	 * @throws IOException throws exceptions generated by {@link getConnection( String transportExtras1, String transportExtras2 )}
	 */
	private String getBesConnection( ) throws IOException {
		if( CoverageInfo.isCoverageSufficient(CoverageInfo.COVERAGE_MDS) ) {
			return ";deviceside=false";
		}
		return null;
	}
	/**
	 * Generates a connection parameter using the WIFI transport if available
	 * 
	 * @return An {@link HttpConnection} if this transport is available, otherwise null
	 * @throws IOException throws exceptions generated by {@link getConnection( String transportExtras1, String transportExtras2 )}
	 */
	private String getWifiConnection() throws IOException {
		if(  WLANInfo.getWLANState() == WLANInfo.WLAN_STATE_CONNECTED ) {
			
			return ";deviceside=true;interface=wifi";
			
		}
		return null;
	}
	
	/**
	 * Generates a connection parameter using the WAP2 transport if available
	 * 
	 * @param index The index of the service book to use
	 * @return An {@link HttpConnection} if this transport is available, otherwise null
	 * @throws NoMoreTransportsException if index is outside the range of available service books
	 * @throws IOException throws exceptions generated by {@link getConnection( String transportExtras1, String transportExtras2 )}
	 */
	private String getWap2Connection( int index ) throws NoMoreTransportsException, IOException {
		if( index >= srWAP2.length ) {
			throw new NoMoreTransportsException();
		}
		if( CoverageInfo.isCoverageSufficient(CoverageInfo.COVERAGE_DIRECT) ) {
			ServiceRecord sr = srWAP2[index];
			return ";deviceside=true;ConnectionUID="+sr.getUid();
		}
		return null;
	}

	/**
	 * Generates a connection parameter using the TCP transport if available
	 * 
	 * @return An {@link HttpConnection} if this transport is available, otherwise null
	 * @throws IOException throws exceptions generated by {@link getConnection( String transportExtras1, String transportExtras2 )}
	 */
	private String getTcpConnection( ) throws IOException {
		if( CoverageInfo.isCoverageSufficient( 1 /* CoverageInfo.COVERAGE_DIRECT */ ) ) {
			return ";deviceside=true";
		}
		return null;
	}
	
	/**
	 * Public method used to reload service books for whatever reason (though I can't think of any)
	 */
	public static void reloadServiceBooks() {
		loadServiceBooks( true );
	}
	
	/**
	 * Loads all pertinent service books into local variables for later use. Called upon first instantiation of the class and upload {@link reloadServiceBooks()}
	 * @param reload Whether to force a reload even if they've already been loaded.
	 */
	private static synchronized void loadServiceBooks( boolean reload ) {
		if( serviceRecordsLoaded && !reload ) {
			return;
		}
		ServiceBook sb = ServiceBook.getSB();
		ServiceRecord[] records = sb.getRecords();
		Vector mdsVec = new Vector();
		Vector bisVec = new Vector();
		Vector wap2Vec = new Vector();
		Vector wifiVec = new Vector();
		
		
        if( !serviceRecordsLoaded ) {
    		for (int i = 0; i < records.length; i++) {
    			ServiceRecord myRecord = records[i];
    			String cid, uid;

    			if (myRecord.isValid() && !myRecord.isDisabled()) {
    				cid = myRecord.getCid().toLowerCase();
    				uid = myRecord.getUid().toLowerCase();
    				// BIS
    				if (cid.indexOf("ippp") != -1 && uid.indexOf("gpmds") != -1) {
    					bisVec.addElement( myRecord );
    				}
    				// WAP1.0: Not implemented.

    				// BES
    				if (cid.indexOf("ippp") != -1 && uid.indexOf("gpmds") == -1) {
    					mdsVec.addElement( myRecord );
    				}
    				// WiFi
    				if (cid.indexOf("wptcp") != -1 && uid.indexOf("wifi") != -1) {
    					wifiVec.addElement( myRecord );
    				}
    				// Wap2
    				if (cid.indexOf("wptcp") != -1 && uid.indexOf("wap2") != -1) {
    					wap2Vec.addElement( myRecord );
    				}
    			}
    		}
    		srMDS = new ServiceRecord[mdsVec.size()];
    		mdsVec.copyInto( srMDS );
    		mdsVec.removeAllElements();
    		mdsVec = null;
    		
    		srBIS = new ServiceRecord[bisVec.size()];
    		bisVec.copyInto( srBIS );
    		bisVec.removeAllElements();
    		bisVec = null;
    		
    		srWAP2 = new ServiceRecord[wap2Vec.size()];
    		wap2Vec.copyInto( srWAP2 );
    		wap2Vec.removeAllElements();
    		wap2Vec = null;
    		
    		srWiFi = new ServiceRecord[wifiVec.size()];
    		wifiVec.copyInto( srWiFi );
    		wifiVec.removeAllElements();
    		wifiVec = null;
    		
    		serviceRecordsLoaded = true;
        }
	}

	/**
	 * Utility methd for converting a mask of transports into an array of transports in default order
	 * @param mask ORed collection of masks, example: <code>TRANSPORT_WIFI | TRANSPORT_BES</code>
	 * @return an array of the transports specified in mask in default order, example: { TRANSPORT_WIFI, TRANSPORT_BES }
	 */
	private static int[] transportMaskToArray( int mask ) {
		if( mask == 0 ) {
			mask = TRANSPORTS_ANY;
		}
		int numTransports = 0;
		for( int i = 0; i < TRANSPORT_COUNT; i++ ) {
			if( ( DEFAULT_TRANSPORT_ORDER[i] & mask ) != 0 ) {
				numTransports++;
			}
		}
		int transports[] = new int[numTransports];
		int index = 0;
		for( int i = 0; i < TRANSPORT_COUNT; i++ ) {
			if( ( DEFAULT_TRANSPORT_ORDER[i] & mask ) != 0 ) {
				transports[index++] = DEFAULT_TRANSPORT_ORDER[i];
			}
		}
		return transports;
	}
	
	private static class NoMoreTransportsException extends Exception { }
}
