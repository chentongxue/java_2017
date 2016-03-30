//package sacred.alliance.magic.debug.action;
//
//import java.util.ArrayList;
//import java.util.Collection;
//import java.util.Collections;
//import java.util.Comparator;
//import java.util.List;
//
//import com.game.draco.GameContext;
//import com.game.draco.debug.message.item.FactionInfoItem;
//import com.game.draco.debug.message.request.C10061_FactionInfoListReqMessage;
//import com.game.draco.debug.message.response.C10061_FactionInfoListRespMessage;
//
//import sacred.alliance.magic.core.Message;
//import sacred.alliance.magic.core.action.ActionContext;
//import sacred.alliance.magic.core.action.ActionSupport;
//import sacred.alliance.magic.domain.Faction;
//import sacred.alliance.magic.util.Util;
//
//public class FactionInfoListAction extends ActionSupport<C10061_FactionInfoListReqMessage>{
//	
//	@Override
//	public Message execute(ActionContext context, C10061_FactionInfoListReqMessage reqMsg) {
//		C10061_FactionInfoListRespMessage resp = new C10061_FactionInfoListRespMessage();
//		try{
//			//门派名称参数，如果为空表示查询所有门派
//			String name = reqMsg.getName();
//			Collection<Faction> factionList;
//			if(Util.isEmpty(name)){
//				factionList = GameContext.getFactionApp().getFactionMap().values();
//			}else{
//				factionList = GameContext.getFactionApp().getFactionListByName(name);
//			}
//			if(Util.isEmpty(factionList)){
//				return resp;
//			}
//			List<FactionInfoItem> factionInfoList = new ArrayList<FactionInfoItem>();
//			for(Faction faction : factionList){
//				if(null == faction){
//					continue;
//				}
//				FactionInfoItem item = new FactionInfoItem();
//				item.setFactionId(faction.getFactionId());
//				item.setFactionName(faction.getFactionName());
//				item.setFactionLevel(faction.getFactionLevel());
//				item.setLeaderId(faction.getLeaderId());
//				item.setLeaderName(faction.getLeaderName());
//				item.setCreatDate(faction.getCreateDate());
//				item.setFactionDesc(faction.getFactionDesc());
//				item.setMemberNum(faction.getMemberNum());
//				item.setMaxMemberNum(faction.getMaxMemberNum());
//				item.setContribution(faction.getContribution());
//				item.setIntegral(faction.getIntegral());
//				item.setMaxIntegral(faction.getMaxIntegral());
//				item.setResource(faction.getResource());
//				factionInfoList.add(item);
//			}
//			this.sortFactionList(factionInfoList);
//			resp.setFactionInfoList(factionInfoList);
//			return resp;
//		}catch(Exception e){
//			this.logger.error("FactionInfoListAction error: ", e);
//			return resp;
//		}
//	}
//	
//	private void sortFactionList(List<FactionInfoItem> factionInfoList){
//		Collections.sort(factionInfoList, new Comparator<FactionInfoItem>() {
//			public int compare(FactionInfoItem info1, FactionInfoItem info2) {
//				if(info1.getContribution() > info2.getContribution()) {
//					return -1;
//				}
//				if(info1.getContribution() < info2.getContribution()) {
//					return 1;
//				}
//				return 0;
//			}
//		});
//	}
//
//}
