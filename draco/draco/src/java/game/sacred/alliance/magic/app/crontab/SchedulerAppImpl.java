package sacred.alliance.magic.app.crontab;

import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.Trigger;

import sacred.alliance.magic.util.Log4jManager;

public class SchedulerAppImpl implements SchedulerApp{

	private Scheduler scheduler ;
	private boolean autoStartup ;
	
	public void setScheduler(Scheduler scheduler) {
		this.scheduler = scheduler;
	}
	
	

	public void setAutoStartup(boolean autoStartup) {
		this.autoStartup = autoStartup;
	}



	@Override
	public void setArgs(Object arg0) {
		
	}

	@Override
	public void start() {
		if(autoStartup){
			//spring已经自启
			return ;
		}
		try {
			scheduler.start();
		} catch (Exception e) {
			Log4jManager.CHECK.error("start SchedulerException ",e);
			Log4jManager.checkFail();
		}
	}

	@Override
	public void stop() {
		
	}
	
	/*public boolean addToScheduler(Trigger trigger){
		try {
			return this.doAddTriggerToScheduler(trigger);
		} catch (SchedulerException e) {
			this.logger.error("addTriggerToScheduler error",e);
		}
		return false ;
	}

	 private boolean doAddTriggerToScheduler(Trigger trigger)
	    throws SchedulerException {
		boolean triggerExists = this.scheduler.getTrigger(trigger.getName(),
				trigger.getGroup()) != null;
		if(triggerExists){
			this.logger.error("found existing trigger,name=" + trigger.getName() + " group=" + trigger.getGroup());
			return false ;
		}
		try {
			this.scheduler.scheduleJob(trigger);
			return true ;
		} catch (ObjectAlreadyExistsException ex) {
			this.logger.error("Unexpectedly found existing trigger, assumably due to cluster race condition: "
							+ ex.getMessage() + " - can safely be ignored");
		}
		return false;
	}
*/
	@Override
	public void addToScheduler(JobDetail jobDetail, Trigger trigger) throws Exception {
		this.scheduler.scheduleJob(jobDetail, trigger);
	}

}
