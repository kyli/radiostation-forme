package fm.radiostation;

/**
 * this class provides the enumerated states in communication with last.fm web
 * services. it uses a similar implementation as enum is implemented in j2se.
 * 
 * @author kaiyi
 * 
 */
public class ServiceEvent {
	
	/**
	 * radio started playing
	 */
	public static final ServiceEvent RADIO_PLAYED = new ServiceEvent();
	/**
	 * radio stopped
	 */
	public static final ServiceEvent RADIO_STOPPED = new ServiceEvent();
	/**
	 * radio tuned to specified station
	 */
	public static final ServiceEvent RADIO_TUNED = new ServiceEvent();
	/**
	 * playlist successfully fetched
	 */
	public static final ServiceEvent PLAYLIST_FETCHED = new ServiceEvent();
	
	private ServiceEvent() {};
}
