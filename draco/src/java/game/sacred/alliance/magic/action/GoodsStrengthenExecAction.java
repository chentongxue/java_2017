package sacred.alliance.magic.action;

import sacred.alliance.magic.app.goods.behavior.AbstractGoodsBehavior;
import sacred.alliance.magic.app.goods.behavior.GoodsBehaviorType;
import sacred.alliance.magic.app.goods.behavior.param.StrengthenParam;
import sacred.alliance.magic.app.goods.behavior.result.StrengthenResult;
import sacred.alliance.magic.base.GoodsType;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.base.StorageType;
import sacred.alliance.magic.constant.RespTypeStatus;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.domain.GoodsEquipment;
import sacred.alliance.magic.domain.RoleGoods;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.message.request.C0541_GoodsStrengthenExecReqMessage;
import com.game.draco.message.response.C0541_GoodsStrengthenExecRespMessage;

/**
 * 装备衍生：执行强化 541, 一键强化，金币不足会提示【金币不足弹板】
 */
public class GoodsStrengthenExecAction extends BaseAction<C0541_GoodsStrengthenExecReqMessage>{
	
	@Override
	public Message execute(ActionContext context, C0541_GoodsStrengthenExecReqMessage reqMsg) {
		
		byte positionType = reqMsg.getPositionType();
		String instanceId = reqMsg.getInstanceId();
		int targetId = reqMsg.getTargetId() ;
		RoleInstance role = this.getCurrentRole(context);
		
		C0541_GoodsStrengthenExecRespMessage respMsg = new C0541_GoodsStrengthenExecRespMessage();
		respMsg.setStatus(RespTypeStatus.FAILURE);
		respMsg.setTargetId(targetId);
		
		try{
			StorageType storageType = StorageType.get(positionType);
			RoleGoods equipGoods = GameContext.getUserGoodsApp().getRoleGoods(role, storageType, instanceId,targetId);
			if(equipGoods == null){
				respMsg.setInfo(this.getText(TextId.NO_GOODS));
				return respMsg;
			}
			int preStengLevel = equipGoods.getStrengthenLevel() ;
			GoodsEquipment equip = GameContext.getGoodsApp().getGoodsTemplate(GoodsEquipment.class, equipGoods.getGoodsId());
			AbstractGoodsBehavior goodsBehavior = GoodsType.get(equip.getGoodsType()).getGoodsBehavior(GoodsBehaviorType.Strengthen);
			StrengthenParam param = new StrengthenParam(role);
			param.setEquipGoods(equipGoods);
			param.setOperateType(StrengthenParam.STRENGTHEN_EXEC);
			param.setTargetId(targetId);
			
			Result result = goodsBehavior.operate(param);
//			if(result.isIgnore()){
//				return null;
//			}//客户端循环
			if(!result.isSuccess()){
				respMsg.setInfo(result.getInfo());
				return respMsg ;
			}
			StrengthenResult stResult = (StrengthenResult)result;
			int starNumChanged = stResult.getStarNumChanged() ;
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
			int postStengLevel = equipGoods.getStrengthenLevel() ;
			respMsg.setStrengthenLevel((byte)postStengLevel);
			respMsg.setLvChanged((byte)starNumChanged);
			//获得装备的当前绑定类型
			//respMsg.setBindType(equipGoods.getBind());
			respMsg.setInstanceId(instanceId);
			respMsg.setPositionType(positionType);
			//更新属性
			if(preStengLevel != postStengLevel){
				respMsg.setAttriList(GameContext.getEquipApp().getStrengthenAttriDifferent(equipGoods,
						equip,postStengLevel-preStengLevel));
			}
			return respMsg;
		}catch(Exception e){
			logger.error("GoodsDeriveStrengthenExeAction error", e);
			respMsg.setStatus(RespTypeStatus.FAILURE);
			respMsg.setInfo(this.getText(TextId.SYSTEM_ERROR));
			return respMsg;
		}
	}

}
