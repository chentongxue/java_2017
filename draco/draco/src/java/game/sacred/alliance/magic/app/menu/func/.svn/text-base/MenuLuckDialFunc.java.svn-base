package sacred.alliance.magic.app.menu.func;

import sacred.alliance.magic.app.menu.MenuFunc;
import sacred.alliance.magic.app.menu.MenuIdType;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.message.item.MenuItem;
import com.game.draco.message.request.C1907_CompassListReqMessage;
import com.game.draco.message.request.C1908_CompassDisplayReqMessage;
import com.game.draco.message.request.C1915_LuckyBoxDisplayReqMessage;
import com.game.draco.message.request.C2650_RichManMapEnterReqMessage;

public class MenuLuckDialFunc extends MenuFunc{

	public MenuLuckDialFunc() {
		super(MenuIdType.LuckDial);
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
	protected byte getMenuCountNotify(MenuIdType menuType) {
		return 0;
	}


	@Override
	public Message createFuncReqMessage(RoleInstance role) {
		C1908_CompassDisplayReqMessage message = new C1908_CompassDisplayReqMessage();
		message.setId((short)1);
		return message;
	}

}
