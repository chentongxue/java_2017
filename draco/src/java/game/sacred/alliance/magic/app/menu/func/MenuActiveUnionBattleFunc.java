package sacred.alliance.magic.app.menu.func;

import sacred.alliance.magic.app.active.vo.Active;
import sacred.alliance.magic.app.menu.MenuIdType;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.message.push.C0003_TipNotifyMessage;
import com.game.draco.message.request.C2530_UnionBattlePanelReqMessage;

public class MenuActiveUnionBattleFunc extends MenuAbstractActiveFunc{

	public MenuActiveUnionBattleFunc() {
		super(MenuIdType.UnionBattle);
	}


	@Override
	public Message createFuncReqMessage(RoleInstance role) {
		try {
			Active active = this.getActive();
			//不符合活动条件，提示活动未开启
			if(!active.isSuitLevel(role) || !active.isTimeOpen()){
				return new C0003_TipNotifyMessage(this.getText(TextId.Active_Not_Open));
			}
			return new C2530_UnionBattlePanelReqMessage();
		} catch (Exception e) {
			this.logger.error(this.getClass().getName() + ".createFuncReqMessage error : ", e);
		}
		return null;
	}

}
