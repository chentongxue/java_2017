package sacred.alliance.magic.action;

import sacred.alliance.magic.app.goods.behavior.AbstractGoodsBehavior;
import sacred.alliance.magic.app.goods.behavior.GoodsBehaviorType;
import sacred.alliance.magic.app.goods.behavior.param.StrengthenParam;
import sacred.alliance.magic.app.goods.behavior.result.StrengthenResult;
import sacred.alliance.magic.app.goods.derive.EquipStrengthenConfig;
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
import com.game.draco.app.vip.type.VipPrivilegeType;
import com.game.draco.message.item.GoodsLiteNamedItem;
import com.game.draco.message.request.C0540_GoodsStrengthenReqMessage;
import com.game.draco.message.response.C0540_GoodsStrengthenRespMessage;

/**
 * 装备衍生：请求强化信息
 */
public class GoodsStrengthenAction extends BaseAction<C0540_GoodsStrengthenReqMessage>{

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
			int targetId = reqMsg.getTargetId() ;
			
			StorageType storageType = StorageType.get(positionType);
			RoleGoods equipGoods = GameContext.getUserGoodsApp().getRoleGoods(role, storageType, instanceId,targetId);
			if(equipGoods == null){
				resp.setInfo(this.getText(TextId.NO_GOODS));
				return resp;
			}
			
			GoodsType goodsType = GameContext.getGoodsApp().getGoodsType(equipGoods.getGoodsId());
			AbstractGoodsBehavior goodsBehavior = goodsType.getGoodsBehavior(GoodsBehaviorType.Strengthen);
			
			StrengthenParam param = new StrengthenParam(role);
			param.setEquipGoods(equipGoods);
			param.setOperateType(StrengthenParam.STRENGTHEN_INFO);
			param.setTargetId(targetId);
			
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
			int targetStrengThenNum = equRG.getStrengthenLevel() + 1;
			//int qualityType = goodsTemplate.getQualityType();
			EquipStrengthenConfig strengthenObj = GameContext.getGoodsApp()
					.getEquipStrengthenConfig(targetStrengThenNum);
			if (null == strengthenObj) {
				respMsg.setStatus(RespTypeStatus.FAILURE);
				respMsg.setInfo(GameContext.getI18n().getText(TextId.GOODS_MAX_STRENGTHEN_LV));
				return respMsg;
			}
			respMsg.setFee(strengthenObj.getGameMoney());
			//respMsg.setMaxRelegation((byte)strengthenObj.getMaxRelegation());
			
			if(strengthenObj.getMaterialId() > 0){
				GoodsLiteNamedItem mitem = strengthenObj.getMaterialGoods().getGoodsLiteNamedItem();
				mitem.setNum((short)strengthenObj.getMaterialNum());
				respMsg.setMaterialItem(mitem);
			}
			/*if(strengthenObj.getStoneId()>0){
				GoodsLiteNamedItem sitem = strengthenObj.getStoneGoods().getGoodsLiteNamedItem();
				sitem.setNum((short)strengthenObj.getStoneNum());
				respMsg.setStoneItem(sitem);
			}*/
			//成功率从配置中获取
			respMsg.setAttriList(GameContext.getEquipApp().getStrengthenAttriDifferent(equRG,goodsTemplate,1));
			respMsg.setSuccessRate(strengthenObj.getShowHitProb());
			//开启vip等级
			respMsg.setOneKeyVipLevel((byte)GameContext.getVipApp().getOpenVipLevel(VipPrivilegeType.EQUIP_ONEKEY_STRENGTHEN.getType(), ""));
			respMsg.setStatus(RespTypeStatus.SUCCESS);
			return respMsg;
		} catch (Exception e) {
			logger.error("buildStrengthenRespMesage ", e);
			return respMsg;
		}
	}
}
