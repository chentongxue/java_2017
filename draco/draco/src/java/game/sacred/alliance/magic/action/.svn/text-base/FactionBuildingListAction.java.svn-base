//package sacred.alliance.magic.action;
//
//import java.util.List;
//import java.util.Map;
//
//import com.game.draco.GameContext;
//import com.game.draco.app.exchange.domain.ExchangeMenu;
//import com.game.draco.message.item.ExchangeChildItem;
//import com.game.draco.message.request.C1712_FactionStoreReqMessage;
//import com.game.draco.message.response.C0002_ErrorRespMessage;
//import com.game.draco.message.response.C1401_ExchangeNumericDetailRespMessage;
//
//import sacred.alliance.magic.base.FactionBuildFuncType;
//import sacred.alliance.magic.constant.TextId;
//import sacred.alliance.magic.constant.Status;
//import sacred.alliance.magic.core.Message;
//import sacred.alliance.magic.core.action.ActionContext;
//import sacred.alliance.magic.domain.Faction;
//import sacred.alliance.magic.domain.FactionBuild;
//import sacred.alliance.magic.vo.RoleInstance;
//
//public class FactionBuildingListAction extends BaseAction<C1712_FactionStoreReqMessage> {
//	
//	@Override
//	public Message execute(ActionContext context, C1712_FactionStoreReqMessage reqMsg) {
//		RoleInstance role = this.getCurrentRole(context);
//		Faction faction = GameContext.getFactionApp().getFaction(role);
//		if(null == faction){
//			return new C0002_ErrorRespMessage(reqMsg.getCommandId(),this.getText(TextId.FACTION_NOT_HAVE_FACTION));
//		}
//		Map<Integer,FactionBuild> factionBulidMap = faction.getBuildingMap();
//		if(null == factionBulidMap) {
//			return new C0002_ErrorRespMessage(reqMsg.getCommandId(),Status.Faction_Build_Not_Exist.getTips());
//		}
//		FactionBuild factionBuild = GameContext.getFactionFuncApp().getFactionBuildByType(role, FactionBuildFuncType.Faction_Store);
//		if(null == factionBuild) {
//			return new C0002_ErrorRespMessage(reqMsg.getCommandId(),Status.Faction_Build_Not_Exist.getTips());
//		}
//		
//		int menuId = factionBuild.getFunction();
//		C1401_ExchangeNumericDetailRespMessage resp = new C1401_ExchangeNumericDetailRespMessage();
//		if(null == role){
//			return null;
//		}
//		List<ExchangeChildItem> childList = GameContext.getExchangeApp().getChildList(role, menuId);
//		if(null == childList){
//			return null;
//		}
//		ExchangeMenu menu = (ExchangeMenu) GameContext.getExchangeApp().getAllMenuMap().get(menuId);
//		resp.setName(menu.getName());
//		resp.setChildList(childList);
//		return resp;
//	}
//}
