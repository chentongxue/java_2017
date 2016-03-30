package sacred.alliance.magic.action;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import sacred.alliance.magic.app.goods.Util;
import sacred.alliance.magic.app.goods.behavior.AbstractGoodsBehavior;
import sacred.alliance.magic.app.goods.behavior.GoodsBehaviorType;
import sacred.alliance.magic.app.goods.behavior.param.StrengthenParam;
import sacred.alliance.magic.app.goods.behavior.result.StrengthenResult;
import sacred.alliance.magic.base.GoodsType;
import sacred.alliance.magic.base.OneKeyOpType;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.base.StorageType;
import sacred.alliance.magic.constant.RespTypeStatus;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.domain.EquStrengthenEffect;
import sacred.alliance.magic.domain.GoodsBase;
import sacred.alliance.magic.domain.RoleGoods;
import sacred.alliance.magic.util.Converter;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.message.item.GoodsLiteNamedItem;
import com.game.draco.message.request.C0575_GoodsStrengthenOneKeyExecReqMessage;
import com.game.draco.message.response.C0541_GoodsStrengthenExecRespMessage;
import com.game.draco.message.response.C0575_GoodsStrengthenOneKeyExecRespMessage;

public class GoodsStrengthenOneKeyExecAction extends 
        BaseAction<C0575_GoodsStrengthenOneKeyExecReqMessage> {

	@Override
	public Message execute(ActionContext context, C0575_GoodsStrengthenOneKeyExecReqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context);
		if(null == role){
			return null;
		}
		C0541_GoodsStrengthenExecRespMessage respMsg = new C0541_GoodsStrengthenExecRespMessage();
		respMsg.setStatus(RespTypeStatus.FAILURE);
		try{
			byte strengthentype = reqMsg.getType();
			byte positionType = reqMsg.getPositionType();
			String instanceId = reqMsg.getInstanceId();
			StorageType storageType = StorageType.get(positionType);
			RoleGoods equipGoods = GameContext.getUserGoodsApp().getRoleGoods(role, storageType, instanceId);
			if(equipGoods == null){
				respMsg.setInfo(this.getText(TextId.NO_GOODS));
				return respMsg;
			}
			GoodsType goodsType = GameContext.getGoodsApp().getGoodsType(equipGoods.getGoodsId());
			AbstractGoodsBehavior goodsBehavior = goodsType.getGoodsBehavior(GoodsBehaviorType.Strengthen);
			
			StrengthenParam param = new StrengthenParam(role);
			param.setStrengthentype(strengthentype);
			param.setEquipGoods(equipGoods);
			param.setOperateType(StrengthenParam.STRENGTHEN_ONEKEY);
			
			Result result = goodsBehavior.operate(param);
			
			if(!result.isSuccess()){
				respMsg.setInfo(result.getInfo());
				return respMsg ;
			}
			StrengthenResult stResult = (StrengthenResult)result;
			int starNumChanged = stResult.getStarNumChanged() ;
			//强化执行过一次才用退-541协议
			if(stResult.getExecCount() > 0){
				respMsg.setStatus(RespTypeStatus.SUCCESS);
				if(starNumChanged > 0){
					respMsg.setInfo(this.getText(TextId.GOODS_STRENGTHEN_INCR) + "+" + starNumChanged);
				}else if(starNumChanged == 0) {
					respMsg.setInfo(this.getText(TextId.GOODS_STRENGTHEN_NOT_CHANGE));
				}else{
					//强化等级降低
					respMsg.setInfo(this.getText(TextId.GOODS_STRENGTHEN_REDUCE) + starNumChanged);
				}
				//获得装备当前的强化等级
				respMsg.setStrengthenLevel((byte)equipGoods.getStarNum());
				//获得装备的当前加成百分比
				EquStrengthenEffect effect = GameContext.getGoodsApp().getStrengthenEffect(
						equipGoods.getStarNum(), stResult.getGoodsTemplate().getQualityType());
				respMsg.setAddRate1(effect.getAddRate1());
				respMsg.setAddRate2(effect.getAddRate2());
				//获得装备的当前绑定类型
				respMsg.setBindType(equipGoods.getBind());
				respMsg.setInstanceId(instanceId);
				respMsg.setPositionType(positionType);
				role.getBehavior().sendMessage(respMsg);
			}
			
			//返回物品强化成功消息 -575
			if(starNumChanged > 0){
				C0575_GoodsStrengthenOneKeyExecRespMessage resp575 = new C0575_GoodsStrengthenOneKeyExecRespMessage();
				resp575.setStrengthenLevel((byte)equipGoods.getStarNum());
				resp575.setFeeSum(stResult.getFee());
				Map<Integer, Integer> consumIdAndNums = stResult.getConsumeIdAndNum();
				int exCount = stResult.getExecCount();
				if(!Util.isEmpty(consumIdAndNums)){
					List<GoodsLiteNamedItem> consumGoods = new ArrayList<GoodsLiteNamedItem>();
					for(Entry<Integer, Integer> entry : consumIdAndNums.entrySet()){
						int itemId = entry.getKey();
						GoodsBase gb = GameContext.getGoodsApp().getGoodsBase(itemId);
						if(null == gb){
							continue;
						}
						GoodsLiteNamedItem consumGood = gb.getGoodsLiteNamedItem() ;
						consumGood.setNum((short)(entry.getValue().shortValue() * exCount));
						consumGoods.add(consumGood);
					}
					resp575.setConsumGoods(consumGoods);
				}
				return resp575;
			}
			//缺少游戏币和道具,并且商城不卖
			Map<Integer, Integer> consumeIdAndNums = stResult.getConsumeIdAndNum();
		    return Converter.buildOneKeyOpFailMessage(OneKeyOpType.STRENGTHEN, stResult.getFee(), consumeIdAndNums);
			
		}catch (Exception e){
			logger.error("", e);
		}
		return null;
	}

}
