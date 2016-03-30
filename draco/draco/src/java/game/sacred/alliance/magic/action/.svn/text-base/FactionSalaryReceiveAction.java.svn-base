//package sacred.alliance.magic.action;
//
//import com.game.draco.GameContext;
//import com.game.draco.message.request.C1738_FactionSalaryReceiveReqMessage;
//import com.game.draco.message.response.C1738_FactionSalaryReceiveRespMessage;
//
//import sacred.alliance.magic.app.faction.FactionActive;
//import sacred.alliance.magic.base.FactionActiveType;
//import sacred.alliance.magic.base.Result;
//import sacred.alliance.magic.constant.Status;
//import sacred.alliance.magic.core.Message;
//import sacred.alliance.magic.core.action.ActionContext;
//import sacred.alliance.magic.domain.Faction;
//import sacred.alliance.magic.vo.RoleInstance;
//
//public class FactionSalaryReceiveAction extends BaseAction<C1738_FactionSalaryReceiveReqMessage> {
//	@Override
//	public Message execute(ActionContext context, C1738_FactionSalaryReceiveReqMessage reqMsg) {
//		C1738_FactionSalaryReceiveRespMessage resp = new C1738_FactionSalaryReceiveRespMessage();
//		try{
//			RoleInstance role = this.getCurrentRole(context);
//			FactionActive factionActive = GameContext.getFactionFuncApp().getFactionActive(FactionActiveType.Salary.getType());
//			Faction faction = GameContext.getFactionApp().getFaction(role);
//			if(null == faction){
//				resp.setInfo(Status.Faction_Not_Exist.getTips());
//				return resp;
//			}
//			Result result = factionActive.condition(faction);
//			if(!result.isSuccess()){
//				resp.setInfo(result.getInfo());
//				return resp;
//			}
//			
//			result = GameContext.getFactionFuncApp().factionSalary(role);
//			if(!result.isSuccess()) {
//				resp.setInfo(result.getInfo());
//				return resp;
//			}
//			resp.setType(Result.SUCCESS);
//		} catch (Exception e) {
//			this.logger.error("FactionDemiseAction", e);
//		}
//		return resp;
//	}
//}
