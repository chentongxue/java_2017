//package sacred.alliance.magic.action;
//
//import com.game.draco.GameContext;
//import com.game.draco.message.request.C1700_FactionCreateConditionReqMessage;
//import com.game.draco.message.response.C1700_FactionCreateConditionRespMessage;
//
//import sacred.alliance.magic.base.Result;
//import sacred.alliance.magic.core.Message;
//import sacred.alliance.magic.core.action.ActionContext;
//import sacred.alliance.magic.vo.RoleInstance;
//
//public class FactionCreateConditionAction extends BaseAction<C1700_FactionCreateConditionReqMessage> {
//
//	@Override
//	public Message execute(ActionContext context, C1700_FactionCreateConditionReqMessage reqMsg) {
//		C1700_FactionCreateConditionRespMessage resp = new C1700_FactionCreateConditionRespMessage();
//		RoleInstance role = this.getCurrentRole(context);
//		Result result = GameContext.getFactionApp().checkCreateCondition(role);
//		
//		String info = GameContext.getFactionApp().getFactionCreateInfo();
//		if(!result.isSuccess()){
//			info = result.getInfo() + "\n" + info;
//			resp.setInfo(info);
//			return resp;
//		}
//		resp.setInfo(info);
//		resp.setType((byte)1);
//		return resp;
//	}
//
//}
