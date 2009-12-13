package fm.radiostation;

/**
 * class that implements this interface will eligible of becoming notified with
 * events relating to communication with last.fm web services. according to the
 * state change event given as parameter, the class can execute the appropriate
 * program logic.
 * 
 * @see ServiceEvent
 * @author kaiyi
 * 
 */
public interface ServiceEventListener {

	/**
	 * service state changed, the event is the enumerated states as found in
	 * {@link ServiceEvent}
	 * 
	 * @param event enumerated states as found in {@link ServiceEvent}
	 */
	void serviceStateChanged(ServiceEvent event);
}
