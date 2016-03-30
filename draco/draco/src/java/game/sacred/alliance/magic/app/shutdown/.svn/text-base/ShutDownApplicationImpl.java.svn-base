package sacred.alliance.magic.app.shutdown;



public class ShutDownApplicationImpl implements ShutDownApplication {

	//private final Logger logger = LoggerFactory.getLogger(this.getClass());

	private boolean refuseRequest = false ;

	@Override
	public boolean isRefuseRequest() {
		return refuseRequest ;
	}

	@Override
	public synchronized void setAcceptRequest() {
		refuseRequest = false ;
	}

	@Override
	public  synchronized void setRefuseRequest() {
		refuseRequest = true ;
	} 
}
