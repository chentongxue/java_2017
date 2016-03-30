package sacred.alliance.magic.app.menu.before;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import sacred.alliance.magic.app.active.vo.Active;
import sacred.alliance.magic.app.menu.MenuBefore;
import sacred.alliance.magic.app.menu.MenuIdType;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;

public class MenuArena1v1Before extends MenuBefore{

	public MenuArena1v1Before() {
		super(MenuIdType.Arena_1v1);
	}

	
	@Override
	public void execute(JobExecutionContext paramJobExecutionContext)
			throws JobExecutionException {
		
		Active active = this.getActive();
		if(null == active){
			return ;
		}
		for(RoleInstance role : GameContext.getOnlineCenter().getAllOnlineRole()){
			try{
				if(active.isOutDate() || !active.isSuitLevel(role)){
					//活动过期或者角色不符合活动要求的等级范围
					continue ;
				}
				//通知角色x分钟后将开启此活动
				this.refresh(role);
			}catch(Exception ex){
				logger.error("",ex);
			}
		}
	}


	@Override
	protected byte getMenuCountNotify(MenuIdType menuType) {
		return 0;
	}
	
	
}

