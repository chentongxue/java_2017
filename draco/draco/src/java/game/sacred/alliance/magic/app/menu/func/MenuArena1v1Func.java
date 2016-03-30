package sacred.alliance.magic.app.menu.func;

import sacred.alliance.magic.app.menu.MenuIdType;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.message.request.C3859_Arean1V1ingDetailReqMessage;

public class MenuArena1v1Func extends MenuAbstractActiveFunc{

	public MenuArena1v1Func() {
		super(MenuIdType.Arena_1v1);
	}

	@Override
	protected byte getMenuCountNotify(MenuIdType menuType) {
		return 0;
	}

	@Override
	public Message createFuncReqMessage(RoleInstance role) {
		return new C3859_Arean1V1ingDetailReqMessage();
	}
}
