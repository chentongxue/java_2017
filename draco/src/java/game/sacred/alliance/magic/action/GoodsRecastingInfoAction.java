package sacred.alliance.magic.action;

import java.util.ArrayList;
import java.util.List;

import sacred.alliance.magic.app.attri.AttriItem;
import sacred.alliance.magic.app.goods.derive.EquipRecatingAttrWeightConfig;
import sacred.alliance.magic.app.goods.derive.EquipRecatingConfig;
import sacred.alliance.magic.app.goods.derive.RecatingBoundBean;
import sacred.alliance.magic.base.StorageType;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.constant.RespTypeStatus;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.domain.GoodsBase;
import sacred.alliance.magic.domain.RoleGoods;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.message.item.GoodsLiteNamedItem;
import com.game.draco.message.item.GoodsRecastingInfoItem;
import com.game.draco.message.request.C0554_GoodsRecastingInfoReqMessage;
import com.game.draco.message.response.C0554_GoodsRecastingInfoRespMessage;

public class GoodsRecastingInfoAction extends BaseAction<C0554_GoodsRecastingInfoReqMessage>{

	@Override
	public Message execute(ActionContext context, C0554_GoodsRecastingInfoReqMessage reqMsg) {
		C0554_GoodsRecastingInfoRespMessage respMsg = new C0554_GoodsRecastingInfoRespMessage();
		try{
			RoleInstance role = this.getCurrentRole(context);
			if(null == role){
				return null;
			}
			byte positionType = reqMsg.getPositionType();
			String instanceId = reqMsg.getInstanceId();
			int targetId = reqMsg.getTargetId() ;
			
			StorageType storageType = StorageType.get(positionType);
			RoleGoods equipGoods = GameContext.getUserGoodsApp().getRoleGoods(role, storageType, instanceId,targetId);
			if(equipGoods == null){
				respMsg.setInfo(this.getText(TextId.NO_GOODS));
				return respMsg;
			}
			//GoodsBase gb = GameContext.getGoodsApp().getGoodsBase(equipGoods.getGoodsId());
			EquipRecatingConfig config = GameContext.getGoodsApp().getEquipRecatingConfig(equipGoods.getQuality(),equipGoods.getStar());
			if(null == config){
				//没有洗练配置也让用户看到相关属性
				respMsg.setAttriItems(this.buildItem(equipGoods));
				respMsg.setStatus(RespTypeStatus.SUCCESS);
				return respMsg ;
			}
			respMsg.setMoney(config.getGameMoney());
			respMsg.setGold(config.getGoldMoney());
			int materialId = config.getMaterial() ;
			int materialNum = config.getNum() ;
			if(materialId > 0 && materialNum>0){
				//材料
				GoodsBase material = GameContext.getGoodsApp().getGoodsBase(materialId);
				GoodsLiteNamedItem mitem = material.getGoodsLiteNamedItem();
				mitem.setNum((short)materialNum);
				respMsg.setMaterialItem(mitem);
			}
			respMsg.setLockRatio(GameContext.getGoodsApp().getRquipRecatingLockRatio());
			respMsg.setAttriItems(this.buildItem(equipGoods));
			respMsg.setStatus(RespTypeStatus.SUCCESS);
		}catch(Exception e){
			logger.error("", e);
			respMsg.setStatus(RespTypeStatus.FAILURE);
			respMsg.setInfo(this.getText(TextId.SYSTEM_ERROR));
		}
		return respMsg;
	}

	private List<GoodsRecastingInfoItem> buildItem(RoleGoods equipGoods){
		ArrayList<AttriItem> list = equipGoods.getAttrVarList();
		//GoodsBase gb = GameContext.getGoodsApp().getGoodsBase(equipGoods.getGoodsId());
		List<GoodsRecastingInfoItem> result = new ArrayList<GoodsRecastingInfoItem>();
		for(AttriItem item : list){
			if(null == item){
				continue;
			}
			byte attriType = item.getAttriTypeValue() ;
			EquipRecatingAttrWeightConfig awc = GameContext.getGoodsApp().getEquipRecatingAttrWeightConfig(attriType, 
					equipGoods.getQuality(),equipGoods.getStar());
			if(null == awc){
				continue;
			}
			int value = (int)item.getValue();
			RecatingBoundBean bean = awc.getRecatingBoundBean(value);
			if(null == bean){
				continue;
			}
			GoodsRecastingInfoItem rii = new GoodsRecastingInfoItem();
			rii.setAttriType(attriType);
			rii.setValue(value);
			rii.setMinValue(bean.getMinValue());
			rii.setMaxValue(bean.getMaxValue());
			rii.setQuality((byte) bean.getQualityType());
			result.add(rii);
		}
		return result ;
	}
}
