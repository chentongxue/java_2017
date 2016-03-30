package sacred.alliance.magic.scheduler.job;

import java.util.Random;

public class LoopCount implements java.io.Serializable{

	private long cycle = 1*1000 ;
	private int count ;
	private int index = 0 ;
	private long timeDiff = 0 ;
	private static Random ran = new Random();
	
	public LoopCount(long cycle){
		this.cycle = cycle ;
		this.count = MapLoop.multipleOfMainLoop(cycle);
		//作用是避免在同一次轮询时全都执行，下一次轮询时全不执行
		index = ran.nextInt(this.count);
	}
	
	public void reset(){
		this.index = 0 ;
		this.timeDiff = 0 ;
	}
	
	public  boolean isReachCycle(){
		index ++ ;
		boolean isDo = (index >= count) ;
		if(isDo){
			index = 0 ;
		}
		return isDo ;
	}

	public long getCycle() {
		return cycle;
	}

	public int getCount() {
		return count;
	}

	public int getIndex() {
		return index;
	}

	public long getTimeDiff() {
		return timeDiff;
	}

	public void addTimeDiff(long td) {
		this.timeDiff += td ;
	}
	
	public void resetTimeDiff(){
		this.timeDiff = 0 ;
	}
	
}
