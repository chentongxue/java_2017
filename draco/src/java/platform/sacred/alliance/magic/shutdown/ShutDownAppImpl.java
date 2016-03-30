package sacred.alliance.magic.shutdown;

public class ShutDownAppImpl implements ShutDownApp {

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

	//
	@Override
	public void setArgs(Object arg0) {
		
	}

	@Override
	public void start() {
		
	}

	@Override
	public void stop() {
		this.setRefuseRequest();
	} 
}
