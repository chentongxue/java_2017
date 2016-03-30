package com.game.draco.app.union.action;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.constant.Status;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.util.DateUtil;
import sacred.alliance.magic.util.ListPageDisplay;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.union.domain.Union;
import com.game.draco.app.union.domain.UnionRecord;
import com.game.draco.app.union.type.UnionRecordType;
import com.game.draco.message.item.UnionRecordItem;
import com.game.draco.message.request.C1705_UnionRecordReqMessage;
import com.game.draco.message.response.C0002_ErrorRespMessage;
import com.game.draco.message.response.C1705_UnionRecordRespMessage;

/**
 * 查看公会记录
 * @author mofun030602
 *
 */
public class UnionRecordListAction extends BaseAction<C1705_UnionRecordReqMessage> {

	@Override
	public Message execute(ActionContext context, C1705_UnionRecordReqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context);
		Union union = GameContext.getUnionApp().getUnion(role);
		if(null == union){
			return new C0002_ErrorRespMessage(reqMsg.getCommandId(),Status.Faction_Not_Exist.getTips());
		}
		
		C1705_UnionRecordRespMessage resp = new C1705_UnionRecordRespMessage();
		String unionId = union.getUnionId();
		ListPageDisplay<UnionRecord> rList = GameContext.getUnionApp().getUnionRecordList(unionId,reqMsg.getPageNum(), reqMsg.getPageSize());
		if(Util.isEmpty(rList.getList()) || Util.isEmpty(rList.getList())){
			return resp;
		}
		List<UnionRecordItem> recordList = new ArrayList<UnionRecordItem>();
		for(UnionRecord record : rList.getList()){
			if(record == null){
				continue;
			}
			UnionRecordItem item = new UnionRecordItem();
			item.setRecordTime(DateUtil.getTimeByDate(new Date(record.getCreateTime())));
			UnionRecordType type = UnionRecordType.get(record.getType());
			if(type != null) {
				item.setRecord(record.getUnionRecord());
			}
			recordList.add(item);
		}
		resp.setUnionRecordList(recordList);
		return resp;
	}
}
