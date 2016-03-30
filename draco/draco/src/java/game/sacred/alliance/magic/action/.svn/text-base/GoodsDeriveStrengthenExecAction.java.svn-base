package sacred.alliance.magic.action;

import com.game.draco.GameContext;
import com.game.draco.message.request.C0541_GoodsStrengthenExecReqMessage;
import com.game.draco.message.response.C0541_GoodsStrengthenExecRespMessage;

import sacred.alliance.magic.app.goods.behavior.AbstractGoodsBehavior;
import sacred.alliance.magic.app.goods.behavior.GoodsBehaviorType;
import sacred.alliance.magic.app.goods.behavior.param.StrengthenParam;
import sacred.alliance.magic.app.goods.behavior.result.StrengthenResult;
import sacred.alliance.magic.base.GoodsType;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.base.StorageType;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.constant.RespTypeStatus;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.domain.EquStrengthenEffect;
import sacred.alliance.magic.domain.RoleGoods;
import sacred.alliance.magic.vo.RoleInstance;

/**
 * 装备衍生：执行强化 541
 */
public class GoodsDeriveStrengthenExecAction extends BaseAction<C0541_GoodsStrengthenExecReqMessage>{
	private static final byte ERROR = (byte)0;
	
	@Override
	public Message execute(ActionContext context, C0541_GoodsStrengthenExecReqMessage reqMsg) {
		C0541_GoodsStrengthenExecRespMessage respMsg = new C0541_GoodsStrengthenExecRespMessage();
		respMsg.setStatus(ERROR);
		byte strengthentype = reqMsg.getType();
		byte positionType = reqMsg.getPositionType();
		String instanceId = reqMsg.getInstanceId();
		RoleInstance role = this.getCurrentRole(context);
		try{
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
			param.setOperateType(StrengthenParam.STRENGTHEN_EXEC);
			Result result = goodsBehavior.operate(param);
			if(result.isIgnore()){
				return null;
			}
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
			return respMsg;
		}catch(Exception e){
			logger.error("GoodsDeriveStrengthenExeAction ", e);
			respMsg.setStatus(ERROR);
			respMsg.setInfo(this.getText(TextId.SYSTEM_ERROR));
			return respMsg;
		}
	}

}
