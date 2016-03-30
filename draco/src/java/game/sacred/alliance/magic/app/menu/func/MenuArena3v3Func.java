package sacred.alliance.magic.app.menu.func;

import com.game.draco.GameContext;
import sacred.alliance.magic.app.active.vo.Active;
import sacred.alliance.magic.app.menu.MenuIdType;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.message.push.C0003_TipNotifyMessage;
import com.game.draco.message.request.C3863_Arena3V3DetailReqMessage;

public class MenuArena3v3Func extends MenuAbstractActiveFunc{

	public MenuArena3v3Func() {
		super(MenuIdType.arena_3v3);
	}

	@Override
	public Message createFuncReqMessage(RoleInstance role) {
		try {
			Active active = this.getActive();
			//不符合活动条件，提示活动未开启
			if(!active.isSuitLevel(role) || !active.isTimeOpen()){
				return new C0003_TipNotifyMessage(this.getText(TextId.Active_Not_Open));
			}
			if(GameContext.getArena3V3App().isOpenDarkDoor()){
				return null;
			}
			C3863_Arena3V3DetailReqMessage notifyMsg = new C3863_Arena3V3DetailReqMessage();
			return notifyMsg ;
		} catch (Exception e) {
			this.logger.error(this.getClass().getName() + ".createFuncReqMessage error : ", e);
		}
		return null;
	}
	
}
