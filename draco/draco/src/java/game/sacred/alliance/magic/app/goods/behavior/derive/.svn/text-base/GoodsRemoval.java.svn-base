package sacred.alliance.magic.app.goods.behavior.derive;

import com.game.draco.GameContext;
import com.game.draco.app.medal.MedalType;

import sacred.alliance.magic.app.attri.AttriBuffer;
import sacred.alliance.magic.app.goods.MosaicHole;
import sacred.alliance.magic.app.goods.behavior.AbstractGoodsBehavior;
import sacred.alliance.magic.app.goods.behavior.GoodsBehaviorType;
import sacred.alliance.magic.app.goods.behavior.param.AbstractParam;
import sacred.alliance.magic.app.goods.behavior.param.RemovalPunchParam;
import sacred.alliance.magic.app.goods.behavior.result.GoodsResult;
import sacred.alliance.magic.base.AttributeType;
import sacred.alliance.magic.base.BindingType;
import sacred.alliance.magic.base.OperatorType;
import sacred.alliance.magic.base.OutputConsumeType;
import sacred.alliance.magic.base.StorageType;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.constant.Status;
import sacred.alliance.magic.domain.GoodsBase;
import sacred.alliance.magic.domain.GoodsGem;
import sacred.alliance.magic.domain.MosaicConfig;
import sacred.alliance.magic.domain.RoleGoods;
import sacred.alliance.magic.vo.RoleInstance;

public class GoodsRemoval extends AbstractGoodsBehavior{
	
	public GoodsRemoval(){
		this.behaviorType = GoodsBehaviorType.Removal;
	}
	
	@Override
	public GoodsResult operate(AbstractParam param) {
		GoodsResult result = new GoodsResult();
		RemovalPunchParam removalParam = (RemovalPunchParam)param;
		RoleInstance role = removalParam.getRole();
		byte bagType = removalParam.getBagType();
		if(null == role){
			//用户不在线
			result.setInfo(GameContext.getI18n().getText(TextId.ROLE_NOT_ONLINE));
			return result ;
		}
		RoleGoods equipGoods = GoodsDeriveSupport.getRoleGoods(role, removalParam.getBagType(), removalParam.getGoodsId());
		if (null == equipGoods) {
			result.setInfo(GameContext.getI18n().getText(TextId.GOODS_NO_EXISTS));
			return result;
		}
		MosaicHole[] holesArray = equipGoods.getMosaicHoles();
		if(null == holesArray){
			return result.setInfo(GameContext.getI18n().getText(TextId.ERROR_INPUT));
		}
		int holeId = removalParam.getHoleId();
		if(holeId < 0 || holeId >= holesArray.length){
			return result.setInfo(GameContext.getI18n().getText(TextId.ERROR_INPUT));
		}
		//判断游戏币是否足够
		int roleMoney = role.getSilverMoney();
		MosaicConfig config = GameContext.getGoodsApp().getMosaicConfig();
		if(roleMoney < config.getExciseMoney()){
			return result.setInfo(Status.GOODS_DERIVE_REMOVE_GEM_MONEY_LESS.getTips());
		}
		
		result.failure();
		//判断 holeId上是否有镶嵌的相关宝石
		MosaicHole hole = holesArray[holeId];
		if(null == hole){
			//没有镶嵌
			result.setInfo(GameContext.getI18n().getText(TextId.GOODS_REMOVAL_FAIL));
			return result;
		}
		int gemId = hole.getGoodsId();
		BindingType bindType = hole.getBindType();
		if(gemId <=0){
			//没有镶嵌
			result.setInfo(GameContext.getI18n().getText(TextId.GOODS_REMOVAL_FAIL));
			return result;
		}
		//添加拆除的物品
		GoodsResult goodsRes = GameContext.getUserGoodsApp().addGoodsForBag(role, gemId, 1, bindType, OutputConsumeType.gem_discharge);
		if(!goodsRes.isSuccess()){
			return goodsRes;
		}
		//扣除游戏币
		if(config.getExciseMoney() >0){
			GameContext.getUserAttributeApp().changeRoleMoney(role, AttributeType.silverMoney, OperatorType.Decrease, 
					config.getExciseMoney(), OutputConsumeType.gem_discharge_consume);
			role.getBehavior().notifyAttribute();
		}
		//摘除宝石
		holesArray[holeId] = null;
		//发送更新
		//摘除不会更改基本属性
		//装备的影响通过响应消息返回,服务器不需要PUSH装备的整个基本信息
		//this.notifyGoodsInfo(role, equipGoods, bagType);
		boolean on = (StorageType.equip.getType() == bagType);
		
		if(!on){
			result.success();
			return result;
		}
		boolean onEquipBag = StorageType.equip.getType() == bagType;
		//重新计算属性
		GoodsGem gem = this.getGemTemplate(gemId);
		if(null != gem){
			AttriBuffer buffer = AttriBuffer.createAttriBuffer();
			buffer.append(gem.getAttriItemList()).reverse();
			GameContext.getUserAttributeApp().changeAttribute(role,buffer);
			role.getBehavior().notifyAttribute();
		}
		result.success();
		if(onEquipBag){
			//更新装备特效
			GameContext.getMedalApp().updateMedal(role, MedalType.XiangQian,equipGoods);
		}
		return result;
	}
	
	protected GoodsGem getGemTemplate(int id){
		if(id <=0){
			return null ;
		}
		GoodsBase gb = GameContext.getGoodsApp().getGoodsBase(id);
		if(null == gb || !(gb instanceof GoodsGem)){
			return null ;
		}
		return (GoodsGem)gb ;
	}
}
