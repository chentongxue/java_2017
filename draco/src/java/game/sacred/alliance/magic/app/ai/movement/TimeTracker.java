package sacred.alliance.magic.app.ai.movement;

public class TimeTracker {

	private long expiryTime;//过期时间
	
    public TimeTracker(long expiry){
    	expiryTime = expiry;
    }
    
    public void update(long diff) {
    	expiryTime -= diff; 
    }
    
    public boolean passed() {
    	return (expiryTime <= 0);
    }
    
    public void reset(long interval) {
    	expiryTime = interval; 
    }
    
    public long getExpiry() {
    	return expiryTime;
    }
	
}
