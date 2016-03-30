package sacred.alliance.magic.action;

import sacred.alliance.magic.constant.Status;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.domain.GoodsBase;
import sacred.alliance.magic.domain.RoleGoods;
import sacred.alliance.magic.vo.RoleInstance;
import sacred.alliance.magic.vo.RoleShape;

import com.game.draco.GameContext;
import com.game.draco.message.item.GoodsBaseItem;
import com.game.draco.message.item.GoodsSysEquipmentItem;
import com.game.draco.message.request.C1102_RoleShowReqMessage;
import com.game.draco.message.response.C0002_ErrorRespMessage;
import com.game.draco.message.response.C1102_RoleShowRespMessage;

public class RoleShowAction extends BaseAction<C1102_RoleShowReqMessage> {

	@Override
	public Message execute(ActionContext context, C1102_RoleShowReqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context);
		if(null == role){
			return null ;
		}
		
		try {
			// 获得目标角色
			String reqRoleId = String.valueOf(reqMsg.getRoleId());
			if (role.getRoleId().equals(reqMsg.getRoleId())) {
				return new C0002_ErrorRespMessage(reqMsg.getCommandId(), Status.Role_Targ_Is_Self.getTips());
			}
			RoleInstance target = GameContext.getOnlineCenter().getRoleInstanceByRoleId(reqRoleId);
			if (target == null) {
				return new C0002_ErrorRespMessage(reqMsg.getCommandId(), Status.Role_Targ_Offline.getTips());
			}
			C1102_RoleShowRespMessage respMsg = new C1102_RoleShowRespMessage();
			respMsg.setRoleId(target.getIntRoleId());
			respMsg.setRoleName(target.getRoleName());
			respMsg.setCamp(target.getCampId());
			respMsg.setLevel((byte)target.getLevel());
			//战斗力
			respMsg.setBattleScore(target.getBattleScore());
			//外形
			RoleShape info = GameContext.getUserRoleApp().getRoleShape(target.getRoleId());
			respMsg.setEquipResId((short) info.getEquipResId());
			respMsg.setClothesResId((short) info.getClothesResId());
			respMsg.setWingResId((short) info.getWingResId());
			
			//点灯//TODO:
			/*byte[] indexs = target.getEquipEffectIndex();
			short[] iconIds = new short[indexs.length];
			for (int i = 0; i < indexs.length; i++) {
				MedalType medalType = MedalType.get(i);
				MedalConfig config = GameContext.getMedalApp()
						.getMedalConfig(medalType, indexs[i]);
				if (null != config) {
					iconIds[i] = (short) config.getIconId();
				} else {
					iconIds[i] = GameContext.getMedalApp()
							.getDefaultIcon(medalType);
				}
			}
			respMsg.setStrengthen(iconIds[MedalType.QiangHua.getType()]);
			respMsg.setMosaic(iconIds[MedalType.XiangQian.getType()]);
			respMsg.setRecating(iconIds[MedalType.XiLian.getType()]);*/
			
			//装备
			for(RoleGoods roleGoods : target.getEquipBackpack().getAllGoods()){
				GoodsBaseItem item = getGoodsBaseItem(roleGoods);
				if(item == null){
					continue;
				}
				GoodsSysEquipmentItem gsei = new GoodsSysEquipmentItem();
				gsei.setBaseItem(item);
				gsei.setGoodsInstanceId(roleGoods.getId());
				gsei.setIndex((byte)roleGoods.getGridPlace());
				respMsg.getEquipList().add(gsei);
			}
			respMsg.setRoleId(reqMsg.getRoleId());
			respMsg.setLevel((byte)target.getLevel());
			return respMsg;
		} catch (Exception e) {
			logger.error("", e);
			return new C0002_ErrorRespMessage(reqMsg.getCommandId(), this.getText(TextId.SYSTEM_ERROR));
		}
	
	}

	private GoodsBaseItem getGoodsBaseItem(RoleGoods roleGoods){
		
		if(roleGoods == null){
			return null;
		}
		
		GoodsBase goodsBase = GameContext.getGoodsApp().getGoodsBase(roleGoods.getGoodsId());
		if(goodsBase == null){
			return null;
		}
		
		return goodsBase.getGoodsBaseInfo(roleGoods);
	}

}
