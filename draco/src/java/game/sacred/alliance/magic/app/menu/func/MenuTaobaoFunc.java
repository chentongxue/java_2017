package sacred.alliance.magic.app.menu.func;

import com.game.draco.message.request.C1907_CompassListReqMessage;
import sacred.alliance.magic.app.menu.MenuFunc;
import sacred.alliance.magic.app.menu.MenuIdType;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.message.item.MenuItem;
import com.game.draco.message.request.C1908_CompassDisplayReqMessage;

public class MenuTaobaoFunc extends MenuFunc{

	public MenuTaobaoFunc() {
		super(MenuIdType.Taobao);
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
		return  new C1907_CompassListReqMessage();
	}

}
