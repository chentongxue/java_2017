package sacred.alliance.magic.app.ai.movement;

import sacred.alliance.magic.vo.Point;

public interface DestinationHolder {

	public long setDestination(Traveller traveller, Point dest);
	
    public long setDestination(Traveller traveller, Point dest, int speed);
	
    Point getDestination();
    
//    boolean updateExpired();
    
//    void resetUpdate(long t);
    
    public long getTotalTravelTime();
    
//    boolean hasDestination();
    
    public int getDestinationDiff(Point from, Point to);
    
    public boolean hasArrived();
    
    boolean updateTraveller(Traveller traveller, long diff);
    
//    long startTravel(Traveller traveller, boolean sendMove);
    
//    Point getLocationNow();
    public void setDestPoint(Traveller traveller);
}
