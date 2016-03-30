package com.game.draco.app.compass.action;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import com.game.draco.GameContext;
import com.game.draco.message.item.CompassPlaceItem;
import com.game.draco.message.request.C1909_CompassStopPlaceReqMessage;
import com.game.draco.message.response.C1909_CompassStopPlaceRespMessage;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.constant.Status;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;
/**
 * 1909客户端发来抽奖消息，转盘Id，次数count
 * 对于幸运转盘，几率不随着使用次数改变
 * 可以转多次，返回的结果为一个数组
 * @author gaibaoning@moogame.cn
 * @date 2014-3-26 上午11:01:59
 */
public class CompassStopPlaceAction extends BaseAction<C1909_CompassStopPlaceReqMessage> {

	@Override
	public Message execute(ActionContext context, C1909_CompassStopPlaceReqMessage reqMsg) {
		C1909_CompassStopPlaceRespMessage resp = new C1909_CompassStopPlaceRespMessage();
		resp.setType((byte) 0);
		try{
			RoleInstance role = this.getCurrentRole(context);
			short activeId = reqMsg.getId();
			byte count = reqMsg.getCount();
			//判断抽奖条件
			Result result = GameContext.getCompassApp().checkCondition(role, activeId, count);
			if(result.isIgnore()){
				return null ;
			}
			if(!result.isSuccess()){
				resp.setInfo(result.getInfo());
				return resp;
			}
			//获得中奖停止位置
			byte[] arrs = GameContext.getCompassApp().getCompassStopPlace(role, activeId, count);
			if(null == arrs || 0 == arrs.length){
				resp.setInfo(Status.FAILURE.getTips());
				return resp;
			}
			List<CompassPlaceItem> stopPlaceList = new ArrayList<CompassPlaceItem>();
			for(int i=0; i<arrs.length; i++){
				CompassPlaceItem item = new CompassPlaceItem();
				item.setPlace(arrs[i]);
				stopPlaceList.add(item);
			}
			//标记为成功
			resp.setType((byte) 1);
			resp.setStopPlaceList(stopPlaceList);
			return resp;
		}catch(Exception e){
			resp.setInfo(Status.FAILURE.getTips());
			return resp;
		}
		
	}

}
