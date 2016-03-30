//package sacred.alliance.magic.action;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import com.game.draco.GameContext;
//import com.game.draco.message.item.FactionItem;
//import com.game.draco.message.request.C1702_FactionListReqMessage;
//import com.game.draco.message.response.C1702_FactionListRespMessage;
//
//import sacred.alliance.magic.core.Message;
//import sacred.alliance.magic.core.action.ActionContext;
//import sacred.alliance.magic.domain.Faction;
//import sacred.alliance.magic.util.ListPageDisplay;
//import sacred.alliance.magic.util.Util;
//import sacred.alliance.magic.vo.RoleInstance;
//
//public class FactionListAction extends BaseAction<C1702_FactionListReqMessage> {
//
//	@Override
//	public Message execute(ActionContext context, C1702_FactionListReqMessage reqMsg) {
//		RoleInstance role = this.getCurrentRole(context);
//		C1702_FactionListRespMessage resp = new C1702_FactionListRespMessage();
//		ListPageDisplay<Faction> result = GameContext.getFactionApp().getFactionList(reqMsg.getPageNum(), reqMsg.getPageSize());
//		List<FactionItem> factionList = new ArrayList<FactionItem>();
//		if(Util.isEmpty(result.getList())){
//			return resp;
//		}
//		String factionId = role.getFactionId();
//		for(Faction faction : result.getList()){
//			if(null == faction){
//				continue;
//			}
//			FactionItem item = new FactionItem();
//			item.setFactionId(faction.getFactionId());
//			item.setFactionName(faction.getFactionName());
//			item.setFactionLevel(faction.getFactionLevel());
//			item.setCreatDate(faction.getCreateTime());
//			item.setLeaderName(faction.getLeaderName());
//			item.setMemberNum((short) faction.getMemberNum());
//			item.setMaxMemberNum((short) faction.getMaxMemberNum());
//			item.setContribution(faction.getContribution());
//			item.setFactionDesc(faction.getFactionDesc());
//			item.setCamp(faction.getFactionCamp());
//			if(null != factionId && faction.getFactionId().equals(factionId)){
//				item.setSelfFaction((byte) 1);
//			}
//			
//			factionList.add(item);
//		}
//		resp.setTotalPageNum((short)result.getTotalPages());
//		resp.setFactionList(factionList);
//		return resp;
//	}
//
//}
