//package sacred.alliance.magic.app.menu.before;
//
//import org.quartz.JobExecutionContext;
//import org.quartz.JobExecutionException;
//
//import com.game.draco.GameContext;
//
//import sacred.alliance.magic.app.active.vo.Active;
//import sacred.alliance.magic.app.menu.MenuBefore;
//import sacred.alliance.magic.app.menu.MenuIdType;
//import sacred.alliance.magic.vo.RoleInstance;
//
//public class MenuFactionWarBefore extends MenuBefore{
//
//	public MenuFactionWarBefore() {
//		super(MenuIdType.faction_war);
//	}
//	
//	@Override
//	public void execute(JobExecutionContext paramJobExecutionContext)
//			throws JobExecutionException {
//		
//		Active active = this.getActive();
//		if(null == active){
//			return ;
//		}
//		for(RoleInstance role : GameContext.getOnlineCenter().getAllOnlineRole()){
//			try{
//				if(active.isOutDate() || !active.isSuitLevel(role)){
//					//活动过期或者角色不符合活动要求的等级范围
//					continue ;
//				}
//				//通知角色x分钟后将开启此活动
//				this.refresh(role);
//			}catch(Exception ex){
//				logger.error("",ex);
//			}
//		}
//		//菜单出来的时候创建门派战
//		GameContext.getFactionWarApp().createWar();
//	}
//}
