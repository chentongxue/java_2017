package sacred.alliance.magic.util;

import com.game.draco.GameContext;
import sacred.alliance.magic.constant.LoopConstant;
import sacred.alliance.magic.scheduler.job.LoopCount;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 出自 taobao.code 性能更高的系统时间类，适用于精度不是特别高的场景 
 * 另外：参考System.currentTimeMillis and System.nanoTime
 * 
 * System.currentTimeMillis
 * Linux调用gettimeofday，需要切换到内核态
 */
public class SystemTimer {

	private static final ScheduledExecutorService executor = Executors
			.newSingleThreadScheduledExecutor();

	private static final long tickUnit = Long.parseLong(System.getProperty(
			"notify.systimer.tick", "50"));

	private static LoopCount openDayLoopCount = new LoopCount(2*1000) ;
	private static volatile long time = System.currentTimeMillis();
	private static volatile int gameOpenDays = calGameOpenDays() ;

	public static int gameOpenDays(){
		return gameOpenDays ;
	}

	private static int calGameOpenDays(){
		return (int)((time - GameContext.getGameStartDate().getTime())
				/ DateUtil.ONE_DAY_MILLIS) + 1;
	}

	private static class TimerTicker implements Runnable {
		@Override
		public void run() {
			time = System.currentTimeMillis();
			if(openDayLoopCount.isReachCycle()){
				gameOpenDays = calGameOpenDays() ;
			}
		}
	}

	static {
		executor.scheduleAtFixedRate(new TimerTicker(), tickUnit, tickUnit,
				TimeUnit.MILLISECONDS);
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				executor.shutdown();
			}
		});
	}

	/**
	 * method that returns currentTimeMillis
	 * 
	 * @return
	 * @see
	 */
	public static long currentTimeMillis() {
		return time;
	}
}