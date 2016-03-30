//package sacred.alliance.magic.action;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import com.game.draco.GameContext;
//import com.game.draco.message.item.GoodsLiteNamedItem;
//import com.game.draco.message.request.C1737_FactionActiveReqMessage;
//import com.game.draco.message.response.C1737_FactionActiveRespMessage;
//
//import sacred.alliance.magic.app.faction.FactionActive;
//import sacred.alliance.magic.base.FactionActiveType;
//import sacred.alliance.magic.core.Message;
//import sacred.alliance.magic.core.action.ActionContext;
//import sacred.alliance.magic.domain.Faction;
//import sacred.alliance.magic.domain.GoodsBase;
//import sacred.alliance.magic.vo.RoleInstance;
//
//public class FactionActiveAction extends BaseAction<C1737_FactionActiveReqMessage> {
//	@Override
//	public Message execute(ActionContext context, C1737_FactionActiveReqMessage reqMsg) {
//		C1737_FactionActiveRespMessage resp = new C1737_FactionActiveRespMessage();
//		try{
//			byte type = reqMsg.getType();
//			RoleInstance role = this.getCurrentRole(context);
//			FactionActive factionActive = GameContext.getFactionFuncApp().getFactionActive(type);
//			Faction faction = GameContext.getFactionApp().getFaction(role);
//			if(null == faction){
//				return resp;
//			}
//			List<Integer> goodsList = factionActive.getGooodsIdList();
//			List<GoodsLiteNamedItem> list = new ArrayList<GoodsLiteNamedItem>();
//			for(Integer goodsId : goodsList) {
//				GoodsBase gb = GameContext.getGoodsApp().getGoodsBase(goodsId);
//				if(null == gb) {
//					continue;
//				}
//				GoodsLiteNamedItem item = gb.getGoodsLiteNamedItem() ;
//				list.add(item);
//			}
//			resp.setActiveType(type);
//			resp.setDesc(factionActive.getDesc());
//			resp.setList(list);
//			resp.setOpenLevel((byte)factionActive.getOpenLevel());
//			resp.setCurFactionLevel((byte)faction.getFactionLevel());
//			resp.setParam(factionActive.getParam());
//			if(type == FactionActiveType.Copy.getType() || type == FactionActiveType.Hell_Copy.getType()) {
//				int curCount = GameContext.getCopyLogicApp().getCopyCurrCount(role, factionActive.getParam());
//				int maxCount = GameContext.getCopyLogicApp().getCopyMaxCount(factionActive.getParam());
//				resp.setCurNum((byte)curCount);
//				resp.setMaxNum((byte)maxCount);
//			}
//		} catch (Exception e) {
//			this.logger.error("FactionDemiseAction", e);
//		}
//		return resp;
//	}
//}
