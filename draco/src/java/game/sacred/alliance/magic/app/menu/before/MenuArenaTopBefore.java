//package sacred.alliance.magic.app.menu.before;
//
//import com.game.draco.GameContext;
//import com.game.draco.message.item.MenuItem;
//
//import sacred.alliance.magic.app.menu.MenuBefore;
//import sacred.alliance.magic.app.menu.MenuIdType;
//import sacred.alliance.magic.base.Result;
//import sacred.alliance.magic.vo.RoleInstance;
//
//public class MenuArenaTopBefore extends MenuBefore{
//
//	public MenuArenaTopBefore() {
//		super(MenuIdType.arena_top);
//	}
//	
//	@Override
//	protected MenuItem createMenuItem(RoleInstance role) {
//		//有参赛资格的才显示
//		Result result = GameContext.getArenaTopApp().canJoin(role);
//		if(!result.isSuccess()){
//			return null ;
//		}
//		MenuItem item = new MenuItem();
//		item.setStatus((byte)0);
//		item.setActiveBeforeTimes((short)(60*this.menuConfig.getActiveBeforeTimes()));
//		return item ;
//	}
//}
