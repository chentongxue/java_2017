package sacred.alliance.magic.app.menu.func;

import sacred.alliance.magic.app.menu.MenuFunc;
import sacred.alliance.magic.app.menu.MenuIdType;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.message.item.MenuItem;
import com.game.draco.message.request.C0270_CopyLineCurrPanelReqMessage;

public class MenuCopyFunc extends MenuFunc{

	public MenuCopyFunc() {
		super(MenuIdType.Copy);
	}

	
	@Override
	protected MenuItem createMenuItem(RoleInstance role) {
		if(role.getLevel() < this.menuConfig.getRoleLevel()){
			//没有达到等级
			return null ;
		}
		//其他赋值在外面
		MenuItem item = new MenuItem();
		return item;
	}


	@Override
	protected byte getMenuCountNotify(MenuIdType menuType) {
		// TODO Auto-generated method stub
		return 0;
	}


	@Override
	public Message createFuncReqMessage(RoleInstance role) {
		return new C0270_CopyLineCurrPanelReqMessage();
	}

}
