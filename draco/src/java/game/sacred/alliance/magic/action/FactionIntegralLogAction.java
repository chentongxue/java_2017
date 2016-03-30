//package sacred.alliance.magic.action;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import com.game.draco.GameContext;
//import com.game.draco.message.item.FactionIntegralLogItem;
//import com.game.draco.message.request.C1723_FactionIntegralLogReqMessage;
//import com.game.draco.message.response.C1723_FactionIntegralLogRespMessage;
//
//import sacred.alliance.magic.base.FactionIntegralLogType;
//import sacred.alliance.magic.core.Message;
//import sacred.alliance.magic.core.action.ActionContext;
//import sacred.alliance.magic.domain.FactionIntegralLog;
//import sacred.alliance.magic.util.Util;
//import sacred.alliance.magic.vo.RoleInstance;
//
//public class FactionIntegralLogAction extends BaseAction<C1723_FactionIntegralLogReqMessage> {
//
//	@Override
//	public Message execute(ActionContext context, C1723_FactionIntegralLogReqMessage reqMsg) {
//		RoleInstance role = this.getCurrentRole(context);
//		if(null == role){
//			return null;
//		}
//		int pageNum = reqMsg.getPageNum();
//		if(pageNum <= 0){
//			pageNum = 1;
//		}
//		int rows = reqMsg.getPageRecordNum();//需要查询的行数
//		int startRow = (pageNum-1)*rows;//查询开始的行
//		FactionIntegralLogType integralLogType = FactionIntegralLogType.All;
//		byte reqType = reqMsg.getType();
//		if(1 == reqType){//消耗
//			integralLogType = FactionIntegralLogType.Consume;
//		}else if(2 == reqType){//收入
//			integralLogType = FactionIntegralLogType.Income;
//		}
//		List<FactionIntegralLog> list = GameContext.getFactionApp().getFactionIntegralLogList(role.getFactionId(), startRow, rows, integralLogType);
//		C1723_FactionIntegralLogRespMessage resp = new C1723_FactionIntegralLogRespMessage();
//		resp.setPageNum((short) pageNum);
//		if(Util.isEmpty(list)){
//			return resp;
//		}
//		//判断是否有下一页
//		byte haveNext = list.size() < rows ? (byte)0 : (byte)1;
//		resp.setType(haveNext);
//		List<FactionIntegralLogItem> integralLogList = new ArrayList<FactionIntegralLogItem>();
//		for(FactionIntegralLog log : list){
//			if(null == log){
//				continue;
//			}
//			FactionIntegralLogItem item = new FactionIntegralLogItem();
//			item.setLogTime(log.getIntegralLogTime());
//			item.setLogInfo(log.getIntegralLogContent());
//			integralLogList.add(item);
//		}
//		resp.setIntegralLogList(integralLogList);
//		return resp;
//	}
//
//}
