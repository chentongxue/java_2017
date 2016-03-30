package sacred.alliance.magic.app.menu.func;

import sacred.alliance.magic.app.menu.MenuFunc;
import sacred.alliance.magic.app.menu.MenuIdType;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.message.item.MenuItem;
import com.game.draco.message.request.C2451_OperateActiveListReqMessage;

public class MenuOperateActiveFunc extends MenuFunc{

	public MenuOperateActiveFunc() {
		super(MenuIdType.Operate_Active);
	}

	@Override
	protected MenuItem createMenuItem(RoleInstance role) {
		if(this.menuConfig == null){
			//没有达到等级
			return null ;
		}
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
		return new C2451_OperateActiveListReqMessage();
	}

}
