package sacred.alliance.magic.action;

import com.game.draco.GameContext;
import com.game.draco.message.request.C1181_SummonReqMessage;
import com.game.draco.message.response.C1181_SummonRespMessage;

import sacred.alliance.magic.app.summon.Summon;
import sacred.alliance.magic.app.summon.vo.SummonResult;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.constant.Status;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

public class SummonAction extends BaseAction<C1181_SummonReqMessage> {

	@Override
	public Message execute(ActionContext context, C1181_SummonReqMessage req) {
		C1181_SummonRespMessage resp = new C1181_SummonRespMessage();
		RoleInstance role = this.getCurrentRole(context);
		if(null == role) {
			return null;
		}
		Summon summon = GameContext.getSummonApp().getSummonById(req.getSummonId());
		if(null == summon){
			return null;
		}
		SummonResult status = GameContext.getSummonApp().canSummon(role, summon, true);
		if(status.isIgnore()){
			return null;
		}
		if(!status.isSuccess()){
			resp.setType((byte)0);
			resp.setInfo(status.getStatus().getTips());
			return resp;
		}
		//扣除消耗和添加物品
		Result result = GameContext.getSummonApp().summon(role, summon);
		if(!result.isSuccess()){
			resp.setType((byte)0);
			resp.setInfo(result.getInfo());
			return resp;
		}
		//兑换成功
		resp.setType((byte)1);
		//修个role身上数据
		summon.updateDbInfo(role);
		return resp;
	}
}
