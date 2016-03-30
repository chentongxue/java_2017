package sacred.alliance.magic.app.menu.func;

import sacred.alliance.magic.app.menu.MenuFunc;
import sacred.alliance.magic.app.menu.MenuIdType;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.message.item.MenuItem;
import com.game.draco.message.request.C1920_DailyPlayPeqMessage;
import com.game.draco.message.request.C2520_AccumulateLoginReqMessage;

public class MenuActiveAccumulateLoginFunc extends MenuFunc{

	public MenuActiveAccumulateLoginFunc() {
		super(MenuIdType.AccumulateLogin);
	}

	
	@Override
	protected MenuItem createMenuItem(RoleInstance role) {
		if(role.getLevel() < this.menuConfig.getRoleLevel()){
			//没有达到等级
			return null ;
		}
		MenuItem item = new MenuItem();
		//其他赋值在外面
		return item;
	}
	
	@Override
	public Message createFuncReqMessage(RoleInstance role) {
		if(GameContext.getAccumulateLoginApp().hasAward(role)){
			return new C2520_AccumulateLoginReqMessage();
		}
		return new C1920_DailyPlayPeqMessage() ;
	}



}
