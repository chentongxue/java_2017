package sacred.alliance.magic.action;

import java.util.ArrayList;
import java.util.List;

import com.game.draco.GameContext;
import com.game.draco.message.item.ChargeRecordItem;
import com.game.draco.message.request.C2802_ChargeRecordReqMessage;
import com.game.draco.message.response.C2802_ChargeRecordRespMessage;

import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.domain.ChargeRecord;
import sacred.alliance.magic.util.DateUtil;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.vo.RoleInstance;

public class ChargeRecordAction extends BaseAction<C2802_ChargeRecordReqMessage>{
	
	@Override
	public Message execute(ActionContext context, C2802_ChargeRecordReqMessage req) {
		RoleInstance role = this.getCurrentRole(context);
		if(role == null){
			return null;
		}
		C2802_ChargeRecordRespMessage resp = new C2802_ChargeRecordRespMessage();
		int size = GameContext.getChargeConfig().getChargeRecordShowSize();
		List<ChargeRecord> list = GameContext.getChargeApp().getUserChargeRecordList(role, size);
		if(Util.isEmpty(list)){
			return resp;
		}
		boolean isUseMoogameId = GameContext.getChargeApp().isUseMoogameId(role.getChannelId());
		boolean isShowGameMoney = GameContext.getChargeApp().isRecordShowGameMoney(role.getChannelId());
		List<ChargeRecordItem> recordList = new ArrayList<ChargeRecordItem>();
		for(ChargeRecord record : list){
			if(null == record){
				continue;
			}
			ChargeRecordItem item = new ChargeRecordItem();
			item.setStatus(record.getchargeStatus().getName());
			if(isUseMoogameId){
				item.setId(record.getOrderId());
			}else{
				item.setId(record.getChannelOrderId());
			}
			item.setTime(DateUtil.date2Str(record.getRecordTime(), "yyyy-MM-dd HH:mm"));
			item.setMoney(record.getFeeValue());
			if(isShowGameMoney){
				//显示元宝的渠道
				item.setMoney(record.getPayGold());
			}
			recordList.add(item);
		}
		resp.setRecordList(recordList);
		return resp;
	}
	
}
