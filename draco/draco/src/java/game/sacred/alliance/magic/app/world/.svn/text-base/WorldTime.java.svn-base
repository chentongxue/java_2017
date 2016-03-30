package sacred.alliance.magic.app.world;

public class WorldTime {
	
	public WorldTime(){
		
	}

	/*static*/ long currentWorldTime = System.currentTimeMillis();
	
	/*static*/ long time_diff;
	
	/*static*/ long now;
	
	public /*static*/ long getTimeDiff(){
		return time_diff;
	}

        public long getCurrentWorldTime(){
            return currentWorldTime ;
        }
        
	public /*static*/ void reset(){
		now = System.currentTimeMillis();
		time_diff = now - currentWorldTime;
		currentWorldTime = now;
		
	}
	
	
}
