package com.game.draco.app.equip.action;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.app.goods.behavior.AbstractGoodsBehavior;
import sacred.alliance.magic.app.goods.behavior.GoodsBehaviorType;
import sacred.alliance.magic.app.goods.behavior.param.EquipUpgradeStarParam;
import sacred.alliance.magic.base.GoodsType;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.message.request.C0561_EquipUpgradeStarReqMessage;
import com.game.draco.message.response.C0561_EquipUpgradeStarRespMessage;

public class EquipUpgradeStarAction extends BaseAction<C0561_EquipUpgradeStarReqMessage>{

	@Override
	public Message execute(ActionContext context,
			C0561_EquipUpgradeStarReqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context);
		if(null == role){
			return null ;
		}
		EquipUpgradeStarParam param = new EquipUpgradeStarParam(role);
		param.setBagType(reqMsg.getBagType());
		param.setGoodsInstanceId(reqMsg.getGoodsInstanceId());
		param.setTargetId(reqMsg.getTargetId());
		
		AbstractGoodsBehavior behavior = GoodsType.GoodsEquHuman.getGoodsBehavior(GoodsBehaviorType.EquipUpgradeStar);
		Result result = behavior.operate(param);
		if(result.isIgnore()){
			return null;
		}
		C0561_EquipUpgradeStarRespMessage respMsg = new C0561_EquipUpgradeStarRespMessage();
		respMsg.setStatus(result.getResult());
		respMsg.setInfo(result.getInfo());
		//respMsg.setBagType(reqMsg.getBagType());
		//respMsg.setGoodsInstanceId(reqMsg.getGoodsInstanceId());
		//respMsg.setTargetId(reqMsg.getTargetId());
		return respMsg;
	}

}
