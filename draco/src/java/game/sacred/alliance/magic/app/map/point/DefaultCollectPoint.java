package sacred.alliance.magic.app.map.point;
import com.game.draco.GameContext;
import com.game.draco.message.item.CollectPointIdItem;
import com.game.draco.message.item.CollectPointIdItem2;
import com.game.draco.message.push.C0606_CollectPointNotifyMessage;
import com.game.draco.message.response.C0602_FallListRespMessage;
import com.game.draco.message.response.C0603_FallPickupRespMessage;

import sacred.alliance.magic.base.CollectPointNotifyType;
import sacred.alliance.magic.base.FallOptType;
import sacred.alliance.magic.base.FallRespType;
import sacred.alliance.magic.base.PointType;
import sacred.alliance.magic.constant.Cat;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.constant.RespTypeStatus;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.vo.AbstractRole;
import sacred.alliance.magic.vo.RoleInstance;

public abstract class DefaultCollectPoint<T extends AbstractRole> extends CollectablePoint<T> {
	public void busyAction(RoleInstance role,FallOptType fallOptType) {
		// 通知用户目标忙
		/*ErrorRespMessage busyMsg = new ErrorRespMessage();
		busyMsg.setReqCmdId(new FallListReqMessage().getCommandId());
		busyMsg.setInfo("目标正忙");*/
		
		if(fallOptType == FallOptType.FALL_LIST){
			C0602_FallListRespMessage busyMsg = new C0602_FallListRespMessage(FallRespType.error.getType(),GameContext.getI18n().getText(TextId.COLLECT_POINT_BUSY_MSG));
			GameContext.getMessageCenter().send("",
					role.getUserId(), busyMsg);
		}
		if(fallOptType == FallOptType.FALL_PK){
			C0603_FallPickupRespMessage busyMsg = new C0603_FallPickupRespMessage();
			busyMsg.setStatus(RespTypeStatus.FAILURE);
			busyMsg.setInfo(GameContext.getI18n().getText(TextId.COLLECT_POINT_BUSY_MSG));
			GameContext.getMessageCenter().send("",role.getUserId(), busyMsg);
		}
		
	}

	public void canotAction(RoleInstance role,FallOptType fallOptType, String info) {
		// 通知用户目标忙
		if(fallOptType == FallOptType.FALL_LIST){
			C0602_FallListRespMessage busyMsg = new C0602_FallListRespMessage(FallRespType.error.getType(),info);
			GameContext.getMessageCenter().send("",
					role.getUserId(), busyMsg);
		}
		if(fallOptType == FallOptType.FALL_PK){
			C0603_FallPickupRespMessage busyMsg = new C0603_FallPickupRespMessage();
			busyMsg.setStatus(RespTypeStatus.FAILURE);
			busyMsg.setInfo(info);
			GameContext.getMessageCenter().send("",role.getUserId(), busyMsg);
		}
		/*ErrorRespMessage busyMsg = new ErrorRespMessage();
		busyMsg.setReqCmdId(new FallListReqMessage().getCommandId());
		busyMsg.setInfo(info);
		GameContext.getMessageCenter().send("",
				role.getUserId(), busyMsg);*/
	}

	public void disappearAction(RoleInstance role,FallOptType fallOptType,PointType type) {
		// 通知地图用户此采集点消失
		C0606_CollectPointNotifyMessage notify = new C0606_CollectPointNotifyMessage();
		notify.setType(CollectPointNotifyType.Disappear.getType());
		CollectPointIdItem idItem = new CollectPointIdItem();
		CollectPointIdItem2 item2 = new CollectPointIdItem2();
		item2.setInstanceIds(instanceId);
		item2.setCanPick(GameContext.getI18n().getText(TextId.COLLECT_POINT_DISAPPEAR));
		item2.setCollectType((byte)type.getType());
		item2.setDisplayFlag((byte)0);
		idItem.getList().add(item2);
		notify.setItem(idItem);
		role.getMapInstance().broadcastMap(null, notify);
		// 从地图中删除此采集点,放入刷新列表
		role.getMapInstance().removeCollectPoint(instanceId);
		
		if(fallOptType == FallOptType.FALL_LIST){
			C0602_FallListRespMessage busyMsg = new C0602_FallListRespMessage(FallRespType.fall.getType(),"");
			GameContext.getMessageCenter().send("",
					role.getUserId(), busyMsg);
		}
		if(fallOptType == FallOptType.FALL_PK){
			C0603_FallPickupRespMessage busyMsg = new C0603_FallPickupRespMessage();
			busyMsg.setStatus(RespTypeStatus.SUCCESS);
			busyMsg.setInfo("");
			GameContext.getMessageCenter().send("",role.getUserId(), busyMsg);
		}
	}
	/**
	 * 根据ID结构判断点类型
	 * 
	 * @param entryId
	 * @return
	 */
	public PointType getPointType(String entryId) {
		PointType def = PointType.Unknow;
		if (Util.isEmpty(entryId) || entryId.indexOf(Cat.underline) <= 0) {
			return def;
		}
		int type = Integer.parseInt(entryId.split(Cat.underline)[0]);
		return PointType.get(type);
	}
}
