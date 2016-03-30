package sacred.alliance.magic.app.arena.action;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.base.ChangeMapResult;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.core.exception.ServiceException;
import sacred.alliance.magic.vo.Point;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.message.push.C0003_TipNotifyMessage;
import com.game.draco.message.request.C3862_ArenaTopEnterReqMessage;

public class ArenaTopEnterAction extends BaseAction<C3862_ArenaTopEnterReqMessage>{

	@Override
	public Message execute(ActionContext context, C3862_ArenaTopEnterReqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context);
		if(null == role){
			return null ;
		}
		Result enterResult = GameContext.getArenaTopApp().canJoin(role);
		if(!enterResult.isSuccess()){
			return new C0003_TipNotifyMessage(enterResult.getInfo());
		}
		//判断是否能进入
		Point p = GameContext.getArenaTopApp().safePoint();
		if(null == p){
			return new C0003_TipNotifyMessage(this.getText(TextId.SYSTEM_ERROR));
		}
		ChangeMapResult result = null ;
		try {
			result = GameContext.getUserMapApp().changeMap(role, p);
		} catch (ServiceException e) {
			e.printStackTrace();
		}
		if(!result.isSuccess()) {
			return new C0003_TipNotifyMessage(this.getText(TextId.SYSTEM_ERROR));
		}
		return null;
	}

}
