package sacred.alliance.magic.app.ai.movement;

import sacred.alliance.magic.scheduler.job.MapLoop;
import sacred.alliance.magic.vo.Point;

public class DestinationHolderImpl implements DestinationHolder {

	long TRAVELLER_UPDATE_INTERVAL = MapLoop.loopSleepMillis;//取主循环的时间
	
	int DEFAULT_MOVE_SPEED = 80;
	
	TimeTracker tracker = new TimeTracker(TRAVELLER_UPDATE_INTERVAL);
	
	long totalTravelTime;//移动需要总时间
	long timeElapsed;//移动已经花费时间
    String mapId;//要移动到的地图id
    int fromX, fromY;//移动起始地点
    int destX, destY;//移动目的地点
	
    /**
     * 当前所在地点
     */
	private Point getLocationNow() {
		int x,y;
	    if( hasArrived() ){
	        x = destX;
	        y = destY;
	    }else{
	        double percent_passed = (double)timeElapsed / (double)totalTravelTime;
	        x = (int)(fromX + ((destX - fromX) * percent_passed));
	        y = (int)(fromY + ((destY - fromY) * percent_passed));
	        /*System.out.println("未到达!!x=" + x + " y=" + y
							+ " percent_passed=" + percent_passed 
							+ " i_timeElapsed=" + i_timeElapsed
							+ " i_totalTravelTime=" + i_totalTravelTime);*/
	    }
	    return new Point(mapId, x, y);
	}

	/**
	 * 获得目的地点
	 */
	public Point getDestination() {
		return new Point(mapId, destX, destY);
	}

	/**
	 * 获得与目的地地点的距离
	 */
	public int getDestinationDiff(Point from, Point to) {
		return Math.abs(from.getX()-to.getX())+Math.abs(from.getY()-to.getY());
	}

	public boolean hasLastTracker(){
		return (totalTravelTime - timeElapsed) >= tracker.getExpiry();
	}
	
	
	/***
	 * 获得总时间
	 */
	public long getTotalTravelTime() {
		return totalTravelTime;
	}

	/**
	 * 是否已经到达
	 */
	public boolean hasArrived() {
		return (totalTravelTime <= 0 || timeElapsed >= totalTravelTime); 
	}

	private void resetUpdate() {
		 tracker.reset(TRAVELLER_UPDATE_INTERVAL); 
	}

	public long setDestination(Traveller traveller, Point dest, int speed) {
		mapId = dest.getMapid();
		fromX = traveller.getPositionX();
	    fromY = traveller.getPositionY();
	    destX = dest.getX();
	    destY = dest.getY();
	    if (fromX == dest.getX() && fromY == dest.getY()){
	        return 0;
	    }
	    return startTravel(traveller, speed);
	}
	
	public void setDestPoint(Traveller traveller) {
		mapId = traveller.getTraveller().getMapId();
		fromX = traveller.getPositionX();
	    fromY = traveller.getPositionY();
	    destX = fromX;
	    destY = fromY;
	}
	
	/**
	 * 设置目的地
	 * 返回距离
	 */
	public long setDestination(Traveller traveller, Point dest) {
	    return setDestination(traveller, dest, traveller.getTraveller().getSpeed());
	}

	/**
	 * 开始移动
	 */
	private long startTravel(Traveller traveller, double speed) {
//	    int dx = Math.abs(destX - fromX);
//	    int dy = Math.abs(destY - fromY);
//	    double dist = dx + dy;
	    double dist = Point.math_DistPointPoint(destX - fromX ,destY - fromY);
	    if(dist==0)return 0;
//	    double speed = traveller.speed();
	    if(speed<=0){
	        speed = DEFAULT_MOVE_SPEED;
	    }
	    totalTravelTime = (long)((dist/speed)*1000);
	    timeElapsed = 0;
	    traveller.moveTo(destX, destY, totalTravelTime, speed);
	    return totalTravelTime;
	}

	/**
	 * 更新移动
	 * 返回是否已经移动了
	 */
	public boolean updateTraveller(Traveller traveller, long diff) {
		
	    tracker.update(diff);
	    timeElapsed += diff;
	    
	    if( tracker.passed()||!hasLastTracker()){
	        resetUpdate();
	        Point point = getLocationNow();
	        int x = point.getX();
	        int y = point.getY();
//	        System.out.println("x="+x+";y="+y);
	        if( traveller.getPositionX() != x || traveller.getPositionY() != y ){
		        /*System.out.println("!!!!!!!!!!!!!!! 当前x=" + x + " 当前y="
						+ y + " 原来x=" + traveller.getTraveller().getMapX()
						+ " 原来y=" + traveller.getTraveller().getMapY()
						+ " 目标x="+destX +" 目标y"+destY + "  " +  
						traveller.getTraveller().getAi().getStateMachine().getCurrent().getStateType().getName());*/
	            traveller.relocation(mapId, x, y,totalTravelTime - timeElapsed);
	        }
	        return true;
	    }
	    return false;
	}

}
