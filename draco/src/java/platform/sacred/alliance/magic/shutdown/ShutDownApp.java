package sacred.alliance.magic.shutdown;

import sacred.alliance.magic.core.Service;

public interface ShutDownApp extends Service{
	
	public boolean isRefuseRequest();
	
	public void setRefuseRequest();
	
	public void setAcceptRequest();
	
}
