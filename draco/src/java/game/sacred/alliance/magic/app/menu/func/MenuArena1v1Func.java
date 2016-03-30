package sacred.alliance.magic.app.menu.func;

import sacred.alliance.magic.app.active.vo.Active;
import sacred.alliance.magic.app.menu.MenuIdType;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.message.push.C0003_TipNotifyMessage;
import com.game.draco.message.request.C3859_Arean1V1ingDetailReqMessage;

public class MenuArena1v1Func extends MenuAbstractActiveFunc{

	public MenuArena1v1Func() {
		super(MenuIdType.Arena_1v1);
	}


	@Override
	public Message createFuncReqMessage(RoleInstance role) {
		Active active = this.getActive();
		//不符合活动条件，提示活动未开启
		if(!active.isSuitLevel(role) || !active.isTimeOpen()){
			return new C0003_TipNotifyMessage(this.getText(TextId.Active_Not_Open));
		}
		return new C3859_Arean1V1ingDetailReqMessage();
	}
}
