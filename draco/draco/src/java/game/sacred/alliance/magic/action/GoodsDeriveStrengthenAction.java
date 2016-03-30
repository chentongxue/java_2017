package sacred.alliance.magic.action;

import com.game.draco.GameContext;
import com.game.draco.message.item.GoodsLiteNamedItem;
import com.game.draco.message.request.C0540_GoodsStrengthenReqMessage;
import com.game.draco.message.response.C0540_GoodsStrengthenRespMessage;

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
import sacred.alliance.magic.domain.EquStrengthenstar;
import sacred.alliance.magic.domain.GoodsEquipment;
import sacred.alliance.magic.domain.RoleGoods;
import sacred.alliance.magic.vo.RoleInstance;

/**
 * 装备衍生：请求强化信息
 */
public class GoodsDeriveStrengthenAction extends BaseAction<C0540_GoodsStrengthenReqMessage>{

	@Override
	public Message execute(ActionContext context, C0540_GoodsStrengthenReqMessage reqMsg) {
		C0540_GoodsStrengthenRespMessage resp = new C0540_GoodsStrengthenRespMessage();
		resp.setStatus(RespTypeStatus.FAILURE);
		try{
			RoleInstance role = this.getCurrentRole(context);
			if(null == role){
				return null ;
			}
			
			byte positionType = reqMsg.getPositionType();
			String instanceId = reqMsg.getInstanceId();
			
			StorageType storageType = StorageType.get(positionType);
			RoleGoods equipGoods = GameContext.getUserGoodsApp().getRoleGoods(role, storageType, instanceId);
			if(equipGoods == null){
				resp.setInfo(this.getText(TextId.NO_GOODS));
				return resp;
			}
			
			GoodsType goodsType = GameContext.getGoodsApp().getGoodsType(equipGoods.getGoodsId());
			AbstractGoodsBehavior goodsBehavior = goodsType.getGoodsBehavior(GoodsBehaviorType.Strengthen);
			
			StrengthenParam param = new StrengthenParam(role);
			param.setEquipGoods(equipGoods);
			param.setOperateType(StrengthenParam.STRENGTHEN_INFO);
			
			Result result = goodsBehavior.operate(param);
			if(!result.isSuccess()){
				resp.setInfo(result.getInfo());
				return resp ;
			}
			
			StrengthenResult stResult = (StrengthenResult)result;
			//构建-540
			return this.buildStrengthenRespMesage(role, equipGoods, stResult.getGoodsTemplate());
		}catch(Exception e){
			logger.error("GoodsDeriveStrengthenAction ", e);
			resp.setStatus(RespTypeStatus.FAILURE);
			resp.setInfo(this.getText(TextId.SYSTEM_ERROR));
		}
		return resp ;
	}

	
	private C0540_GoodsStrengthenRespMessage buildStrengthenRespMesage(RoleInstance role, 
			RoleGoods equRG, GoodsEquipment goodsTemplate) {
		C0540_GoodsStrengthenRespMessage respMsg = new C0540_GoodsStrengthenRespMessage();
		respMsg.setStatus(RespTypeStatus.FAILURE);
		try {
			int targetStrengThenNum = equRG.getStarNum() + 1;
			int qualityType = goodsTemplate.getQualityType();
			EquStrengthenstar strengthenObj = GameContext.getGoodsApp()
					.getStrengthenstar(targetStrengThenNum);
			if (null == strengthenObj) {
				respMsg.setStatus(RespTypeStatus.FAILURE);
				respMsg.setInfo(GameContext.getI18n().getText(TextId.GOODS_MAX_STRENGTHEN_LV));
				return respMsg;
			}
			respMsg.setFee(strengthenObj.getSilverMoney());
			respMsg.setMaxRelegation((byte)strengthenObj.getMaxRelegation());
			
			if(strengthenObj.getMaterialId() > 0){
				GoodsLiteNamedItem mitem = strengthenObj.getMaterialGoods().getGoodsLiteNamedItem();
				mitem.setNum((short)strengthenObj.getMaterialNum());
				respMsg.setMaterialItem(mitem);
			}
			if(strengthenObj.getStoneId()>0){
				GoodsLiteNamedItem sitem = strengthenObj.getStoneGoods().getGoodsLiteNamedItem();
				sitem.setNum((short)strengthenObj.getStoneNum());
				respMsg.setStoneItem(sitem);
			}
			//成功率从配置中获取
			respMsg.setSuccessRate(strengthenObj.getShowHitProb());
			respMsg.setStatus(RespTypeStatus.SUCCESS);
			// 根据装备当前强化等级+1、装备品质，获得强化属性提升信息
			EquStrengthenEffect strengthenEffect = GameContext.getGoodsApp()
					.getStrengthenEffect(targetStrengThenNum, qualityType);
			if (null == strengthenEffect) {
				return respMsg;
			}
			respMsg.setNextAddRate1(strengthenEffect.getAddRate1());
			respMsg.setNextAddRate2(strengthenEffect.getAddRate2());
			return respMsg;
		} catch (Exception e) {
			logger.error("buildStrengthenRespMesage ", e);
			return respMsg;
		}
	}
}
