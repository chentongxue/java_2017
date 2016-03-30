//package sacred.alliance.magic.action;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import com.game.draco.GameContext;
//import com.game.draco.message.item.FactionRecordItem;
//import com.game.draco.message.request.C1705_FactionRecordReqMessage;
//import com.game.draco.message.response.C0002_ErrorRespMessage;
//import com.game.draco.message.response.C1705_FactionRecordRespMessage;
//
//import sacred.alliance.magic.base.FactionRecordType;
//import sacred.alliance.magic.constant.Status;
//import sacred.alliance.magic.core.Message;
//import sacred.alliance.magic.core.action.ActionContext;
//import sacred.alliance.magic.domain.Faction;
//import sacred.alliance.magic.domain.FactionRecord;
//import sacred.alliance.magic.util.DateUtil;
//import sacred.alliance.magic.util.Util;
//import sacred.alliance.magic.vo.RoleInstance;
//
//public class FactionRecordListAction extends BaseAction<C1705_FactionRecordReqMessage> {
//
//	@Override
//	public Message execute(ActionContext context, C1705_FactionRecordReqMessage reqMsg) {
//		RoleInstance role = this.getCurrentRole(context);
//		Faction faction = GameContext.getFactionApp().getFaction(role);
//		if(null == faction){
//			return new C0002_ErrorRespMessage(reqMsg.getCommandId(),Status.Faction_Not_Exist.getTips());
//		}
//		int pageNum = reqMsg.getPageNum();
//		if(pageNum <= 0){
//			pageNum = 1;
//		}
//		int rows = reqMsg.getPageSize();
//		int startRow = (pageNum-1)*rows;//查询开始的行
//		C1705_FactionRecordRespMessage resp = new C1705_FactionRecordRespMessage();
//		String factionId = faction.getFactionId();
//		List<FactionRecord> rList = GameContext.getFactionFuncApp().getFactionRecord(factionId, startRow, rows);
//		if(Util.isEmpty(rList)){
//			return resp;
//		}
//		//判断是否有下一页
//		byte haveNext = rList.size() < rows ? (byte)0 : (byte)1;
//		resp.setNextPage(haveNext);
//		resp.setPageNum((short) pageNum);
//		List<FactionRecordItem> factionList = new ArrayList<FactionRecordItem>();
//		for(FactionRecord factionRecord : rList){
//			if(null == factionRecord){
//				continue;
//			}
//			FactionRecordItem item = new FactionRecordItem();
//			item.setRecordTime(DateUtil.getTimeByDate(factionRecord.getCreateTime()));
//			FactionRecordType type = FactionRecordType.get(factionRecord.getType());
//			if(type != null) {
//				item.setRecord(factionRecord.getFactionRecord());
//			}
//			factionList.add(item);
//		}
//		resp.setFactionRecordList(factionList);
//		return resp;
//	}
//}
