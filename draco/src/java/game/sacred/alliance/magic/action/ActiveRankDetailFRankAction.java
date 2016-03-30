//package sacred.alliance.magic.action;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import com.game.draco.GameContext;
//import com.game.draco.message.item.ActiveRankDetailItem;
//import com.game.draco.message.request.C2320_ActiveRankDetailFRankReqMessage;
//import com.game.draco.message.response.C2319_ActiveRankDetailRespMessage;
//
//import sacred.alliance.magic.app.active.rank.ActiveRankInfo;
//import sacred.alliance.magic.app.rank.domain.RankInfo;
//import sacred.alliance.magic.core.Message;
//import sacred.alliance.magic.core.action.ActionContext;
//import sacred.alliance.magic.vo.RoleInstance;
//
//public class ActiveRankDetailFRankAction extends BaseAction<C2320_ActiveRankDetailFRankReqMessage> {
//
//	@Override
//	public Message execute(ActionContext context, C2320_ActiveRankDetailFRankReqMessage reqMsg) {
//		RoleInstance role = this.getCurrentRole(context);
//		if(null == role){
//			return null;
//		}
//		RankInfo rankItem = GameContext.getRankApp().getRankInfo(reqMsg.getRankId());
//		if(null == rankItem){
//			return null;
//		}
//		RankInfo activeRank = GameContext.getRankApp().getRankInfo(rankItem.getActiveRankId());
//		if(null == activeRank){
//			return null;
//		}
//		ActiveRankInfo arankItem = activeRank.getActiveRankInfo();
//		if(null == arankItem){
//			return null;
//		}
//		List<RankInfo> rankItemList = arankItem.getRankInfoList();
//		if(null == rankItemList || rankItemList.size() == 0){
//			return null;
//		}
//		List<ActiveRankDetailItem> aRankDetailList = new ArrayList<ActiveRankDetailItem>();
//		byte rankType = -1;
//		byte index = -1;
//		for(int i = 0; i < rankItemList.size(); i++){
//			RankInfo rItem = rankItemList.get(i);
//			if(null == rItem){
//				continue;
//			}
//			if(rankType == -1){
//				rankType = rItem.getType();
//			}
//			if(activeRank.getId() == rItem.getId()){
//				index = (byte)i;
//			}
//			ActiveRankInfo aRankItem = rItem.getActiveRankInfo();
//			if(null == aRankItem){
//				continue;
//			}
//			ActiveRankDetailItem aRankDetailItem = new ActiveRankDetailItem();
//			aRankDetailItem.setRankId(rItem.getId());
//			aRankDetailItem.setStatus(GameContext.getActiveRankApp().getRewardStat(role, rItem));
//			aRankDetailItem.setTagResId(rItem.getTagResId());
//			aRankDetailList.add(aRankDetailItem);
//		}
//		
//		C2319_ActiveRankDetailRespMessage respMsg = new C2319_ActiveRankDetailRespMessage();
//		respMsg.setType(rankType);
//		respMsg.setIndex(index);
//		respMsg.setActiveRankDetailList(aRankDetailList);
//		return respMsg;
//	}
//
//}
