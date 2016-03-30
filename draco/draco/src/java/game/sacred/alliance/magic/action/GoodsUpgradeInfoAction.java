package sacred.alliance.magic.action;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import sacred.alliance.magic.app.goods.behavior.AbstractGoodsBehavior;
import sacred.alliance.magic.app.goods.behavior.GoodsBehaviorType;
import sacred.alliance.magic.app.goods.behavior.param.EquipUpgradeParam;
import sacred.alliance.magic.app.goods.behavior.result.EquipUpgradeResult;
import sacred.alliance.magic.app.goods.derive.EquipUpgradeConfig;
import sacred.alliance.magic.base.GoodsType;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.base.StorageType;
import sacred.alliance.magic.constant.RespTypeStatus;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.domain.GoodsBase;
import sacred.alliance.magic.domain.GoodsEquipment;
import sacred.alliance.magic.domain.RoleGoods;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.message.item.GoodsLiteNamedItem;
import com.game.draco.message.push.C0003_TipNotifyMessage;
import com.game.draco.message.request.C0545_GoodsUpgradeInfoReqMessage;
import com.game.draco.message.response.C0545_GoodsUpgradeInfoRespMessage;

public class GoodsUpgradeInfoAction extends BaseAction<C0545_GoodsUpgradeInfoReqMessage>{

	@Override
	public Message execute(ActionContext context, C0545_GoodsUpgradeInfoReqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context);
		if(null == role){
			return null ;
		}
		byte bagType = reqMsg.getBagType();
		String goodsInstanceId = reqMsg.getGoodsInstanceId();
		
		C0545_GoodsUpgradeInfoRespMessage respMsg = new C0545_GoodsUpgradeInfoRespMessage();
		respMsg.setStatus(RespTypeStatus.FAILURE);
		try {
			StorageType storageType = StorageType.get(bagType);
			RoleGoods equipGoods = GameContext.getUserGoodsApp().getRoleGoods(role, storageType, goodsInstanceId);
			if(equipGoods == null){
				return new C0003_TipNotifyMessage(this.getText(TextId.NO_GOODS));
			}
			GoodsType goodsType = GameContext.getGoodsApp().getGoodsType(equipGoods.getGoodsId());
			AbstractGoodsBehavior goodsBehavior = goodsType.getGoodsBehavior(GoodsBehaviorType.EquipUpgrade);
			
			EquipUpgradeParam param = new EquipUpgradeParam(role);
			//获得信息
			param.setParamType(EquipUpgradeParam.PARAM_INFO);
			param.setBagType(bagType);
			param.setGoodsInstanceId(goodsInstanceId);
			Result result = goodsBehavior.operate(param);
			if(!result.isSuccess()){
				respMsg.setInfo(result.getInfo());
				return respMsg ;
			}
			EquipUpgradeResult upgradeResult = (EquipUpgradeResult)result ;
			GoodsEquipment target = upgradeResult.getTargetEquipment();
			EquipUpgradeConfig config = upgradeResult.getEquipUpgradeConfig();
			//目标物品
			GoodsLiteNamedItem targetGoodsInfo = target.getGoodsLiteNamedItem();
			targetGoodsInfo.setNum((short) 1);
			respMsg.setTargetGoodsInfo(targetGoodsInfo);
			//消耗游戏币
			respMsg.setFee(config.getGameMoney());
			//消耗材料
			List<GoodsLiteNamedItem> consumeGoods = new ArrayList<GoodsLiteNamedItem>();
			Map<Integer, Integer> materialMap = config.getMaterialMap();
			for(Map.Entry<Integer, Integer> entry : materialMap.entrySet()){
				int goodsId = entry.getKey();
				int num = entry.getValue() ;
				GoodsBase material = GameContext.getGoodsApp().getGoodsBase(goodsId);
				if(null == material){
					continue;
				}
				GoodsLiteNamedItem consumeItem = material.getGoodsLiteNamedItem();
				//设置数量
				consumeItem.setNum((short) num);
				consumeGoods.add(consumeItem);
			}
			respMsg.setConsumeGoods(consumeGoods);
			respMsg.setStatus(RespTypeStatus.SUCCESS);
		}catch(Exception ex){
			logger.error("GoodsUpgradeInfoAction",ex);
			respMsg.setInfo(this.getText(TextId.SYSTEM_ERROR));
		}
		return respMsg ;
	}

}
