package sacred.alliance.magic.app.menu;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import org.quartz.CronTrigger;
import org.quartz.Job;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.Scheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sacred.alliance.magic.app.active.vo.Active;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.Service;
import sacred.alliance.magic.util.Log4jManager;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.message.item.MenuItem;
import com.game.draco.message.response.C0151_MenuUpdateRespMessage;
import com.game.draco.message.response.C0152_MenuRemoveRespMessage;

public abstract class MenuFunc implements Job,Service {
	
	protected final Logger logger = LoggerFactory.getLogger(this.getClass());
	private static AtomicInteger jobIndex = new AtomicInteger(0);
	protected MenuConfig menuConfig;
	protected MenuIdType menuType ;
	protected Set<Integer> refreshLevelSet = new HashSet<Integer>();//需要刷新的等级
	
	public MenuFunc(MenuIdType menuType){
		this.menuType = menuType ;
		this.menuConfig = GameContext.getMenuApp().getMenuConfig(menuType);
		this.initRefreshLevelSet();
	}
	
	
	public MenuIdType getMenuType(){
		return this.menuType ;
	}
	
	/**
	 * 初始化刷新等级Set
	 * 只初始化活动中角色的等级上下限
	 * 其他类型的菜单须在子类中重写
	 */
	protected void initRefreshLevelSet(){
			try {
				Active active = GameContext.getActiveApp().getActive((short)this.menuConfig.getActiveId());
				if(null != active){
					//存在获得相关等级按照活动配置
					this.refreshLevelSet.add(Integer.valueOf(active.getMinLevel()));
					this.refreshLevelSet.add(Integer.valueOf(active.getMaxLevel()+1));
					return ;
				}
				if(this.menuConfig.getRoleLevel() > 1){
					this.refreshLevelSet.add(this.menuConfig.getRoleLevel());
				}
			} catch (RuntimeException e) {
				this.logger.error("menu.initRefreshLevelSet error: ", e);
			}
	}
	
	
	/**
	 * 获取可在助手面板显示的功能信息
	 * @param role
	 * @return
	 */
	public final MenuItem getMenuItem(RoleInstance role){
		MenuItem item = this.createMenuItem(role);
		if(null == item){
			return null ;
		}
		item.setMenuType((byte)menuConfig.getMenuType());
		item.setMenuId(menuConfig.getMenuId());
		item.setEffectId((short)this.menuConfig.getEffectId());
		item.setIconId((short)this.menuConfig.getIconId());
		item.setPriority(this.menuConfig.getPriority());
		item.setMenuLevel(menuConfig.getMenuLevel());
		item.setSuperiorMenuId(menuConfig.getSuperiorMenuId());
		item.setActiveId(menuConfig.getActiveId());
		if(item.getActiveBeforeTimes() <= 0 ){
			//到期时间为0时,状态一律为可用
			item.setStatus((byte)1);
		}
		return item ;
	}
	
	protected abstract MenuItem createMenuItem(RoleInstance role) ;
	
	
	public abstract Message createFuncReqMessage(RoleInstance role) ;
	
	protected Active getActive(){
		if(this.menuConfig.getActiveId() <=0){
			return null ;
		}
		short activeId = (short)this.menuConfig.getActiveId();
		return GameContext.getActiveApp().getActive(activeId);
	}
	
	
	public List<String> getCronExpression(){
		Active active = this.getActive();
		if(null == active){
			return null ;
		}
		return active.getCronExpression();
	}
	
	
	/**
	 * 给角色发消息
	 * @param role
	 * @param message
	 */
	protected void sendMessage(RoleInstance role, Message message){
		if(null == role || null == message){
			return;
		}
		GameContext.getMessageCenter().sendSysMsg(role, message);
	}
	
	/**
	 * 通知移除助手
	 * @param role
	 */
	protected void notifyRemove(RoleInstance role){
		C0152_MenuRemoveRespMessage message = new C0152_MenuRemoveRespMessage();
		message.setMenuId(this.menuConfig.getMenuId());
		this.sendMessage(role, message);
	}
	
	/**
	 * 通知更新助手信息
	 * @param role
	 */
	protected void notifyUpdate(RoleInstance role, MenuItem menuItem){
		C0151_MenuUpdateRespMessage message = new C0151_MenuUpdateRespMessage();
		message.setItem(menuItem);
		this.sendMessage(role, message);
	}
	
	/**
	 * 刷新
	 * @param role
	 */
	public void refresh(RoleInstance role){
		MenuItem menuItem = this.getMenuItem(role);
		if(null == menuItem){
			this.notifyRemove(role);
			return;
		}
		this.notifyUpdate(role, menuItem);
	}
	
	@Override
	public void execute(JobExecutionContext jobContext) throws JobExecutionException {
		for(RoleInstance role : GameContext.getOnlineCenter().getAllOnlineRole()){
			try{
				this.refresh(role);
			}catch(Exception ex){
				logger.error("",ex);
			}
		}
	}
	
	
	@Override
	public void start(){
		List<String> ces = this.getCronExpression();
		if(Util.isEmpty(ces)){
			return ;
		}
		for(String ce : ces){
			if(Util.isEmpty(ce)){
				continue ;
			}
			Class clazz = this.getClass();
			try {
				int index = jobIndex.getAndDecrement();
				JobDetail jobDetail = new JobDetail("menu_job_" + index,
						Scheduler.DEFAULT_GROUP, clazz);
				CronTrigger trigger = new CronTrigger("menu_cron_" + index, null, ce);
				GameContext.getSchedulerApp()
						.addToScheduler(jobDetail, trigger);
				logger.info("register menu scheduler success,class=" + clazz.getName() + " cronExpression=" + ce);
			}catch(Exception ex){
				Log4jManager.CHECK.error("register menu scheduler exception,class=" + clazz.getName() + " cronExpression=" + ce,ex);
				Log4jManager.checkFail();
			}
		}
	}
	
	@Override
	public void stop(){
		
	}
	
	@Override
	public void setArgs(Object arg0) {
		
	}
	
	/**
	 * 角色升级时刷新助手
	 * @param role
	 */
	public void refreshByUpgrade(RoleInstance role){
		if(!this.needRefreshByUpgrade(role)){
			return;
		}
		this.refresh(role);
	}
	
	/**
	 * 角色升级时是否需要刷新助手
	 * @param role
	 * @return
	 */
	protected boolean needRefreshByUpgrade(RoleInstance role){
		/*if(this.menuConfig.getActiveId() <=0){
			return this.menuConfig.getRoleLevel() > 1 
	        && role.getLevel() == this.menuConfig.getRoleLevel() ;
		}*/
		return this.refreshLevelSet.contains(role.getLevel());
	}
	
	protected String getText(String textId){
		return GameContext.getI18n().getText(textId);
	}
	
}
