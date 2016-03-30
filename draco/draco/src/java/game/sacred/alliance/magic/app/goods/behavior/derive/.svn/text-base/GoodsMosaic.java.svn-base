package sacred.alliance.magic.app.goods.behavior.derive;

import com.game.draco.GameContext;
import com.game.draco.app.medal.MedalType;

import sacred.alliance.magic.app.attri.AttriBuffer;
import sacred.alliance.magic.app.goods.MosaicHole;
import sacred.alliance.magic.app.goods.RoleGoodsHelper;
import sacred.alliance.magic.app.goods.behavior.AbstractGoodsBehavior;
import sacred.alliance.magic.app.goods.behavior.GoodsBehaviorType;
import sacred.alliance.magic.app.goods.behavior.param.AbstractParam;
import sacred.alliance.magic.app.goods.behavior.param.MosaicParam;
import sacred.alliance.magic.app.goods.behavior.result.MosaicHoleResult;
import sacred.alliance.magic.base.AttributeType;
import sacred.alliance.magic.base.BindingType;
import sacred.alliance.magic.base.OperatorType;
import sacred.alliance.magic.base.OutputConsumeType;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.base.StorageType;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.constant.Status;
import sacred.alliance.magic.domain.GoodsEquipment;
import sacred.alliance.magic.domain.GoodsGem;
import sacred.alliance.magic.domain.MosaicConfig;
import sacred.alliance.magic.domain.RoleGoods;
import sacred.alliance.magic.vo.RoleInstance;

public class GoodsMosaic extends AbstractGoodsBehavior{

	public GoodsMosaic(){
		this.behaviorType = GoodsBehaviorType.Mosaic;
	}
	
	@Override
	public MosaicHoleResult operate(AbstractParam param) {
		MosaicParam mosaicParam = (MosaicParam)param;
		RoleGoods equGoods = mosaicParam.getEquipGoods();
		RoleGoods gemGoods = mosaicParam.getGemGoods();
		RoleInstance role = mosaicParam.getRole();
		return this.mosaicConfirm(role, equGoods, gemGoods);
	}
	
	private MosaicHoleResult mosaicConfirm(RoleInstance role, RoleGoods equGoods, RoleGoods gemGoods/*byte bagType, String equId, String gemId*/) {
		MosaicHoleResult result = this.mosaicCondition(role, equGoods, gemGoods);
		if(!result.isSuccess()){
			return result ;
		}
		RoleGoods equ = result.getRoleGoods();
		
		byte bagType = (byte)equ.getStorageType();
		this.doMosaic(role, bagType, result);
		return result;
	}
	
	private MosaicHoleResult doMosaic(RoleInstance role, byte bagType, MosaicHoleResult result){
		result.failure();
		RoleGoods equ = result.getRoleGoods();
		RoleGoods gem = result.getRoleGem() ;
		GoodsGem gemTemplate = result.getGemTemplate() ;
		int matchHoleId = result.getMatchHoleId();
		
		//删除宝石
		Result delResult = GameContext.getUserGoodsApp()
				.deleteForBagByInstanceId(role, gem.getId(), 1, OutputConsumeType.gem_set);
	
		if(!delResult.isSuccess()){
			result.setInfo(delResult.getInfo());
			return result;
		}
		//扣除游戏币
		if(result.getMosaicMoney() >0){
			GameContext.getUserAttributeApp().changeRoleMoney(role, AttributeType.silverMoney, OperatorType.Decrease, 
					result.getMosaicMoney(), OutputConsumeType.gem_set);
			role.getBehavior().notifyAttribute();
		}
		
		if(RoleGoodsHelper.hadBind(gem) || !RoleGoodsHelper.hadBind(equ)){
			//宝石绑定,装备未绑定的情况下,需要将此装备设置为绑定
			equ.setBind(BindingType.already_binding.getType());
			gem.setBind(BindingType.already_binding.getType());
		}
		BindingType bindType = BindingType.get(gem.getBind());
		//修改镶嵌信息
		MosaicHole hole = new MosaicHole();
		hole.setBindType(bindType);
		hole.setGoodsId(gemTemplate.getId());
		//镶嵌到装备上
		equ.getMosaicHoles()[matchHoleId] = hole ;
		
		//镶嵌不会更改基本属性
		//装备属性的修改,通过响应消息返回,客户端自己修改,不用服务器重新PUSH整个装备的基本信息
		
		//装备在身上/金身的物品,需要重新计算属性
		boolean on = (StorageType.equip.getType() == bagType);
		if(on){
			//重新计算属性
			AttriBuffer buffer = AttriBuffer.createAttriBuffer();
			buffer.append(gemTemplate.getAttriItemList());
			boolean onEquipBag = StorageType.equip.getType() == bagType;
			GameContext.getUserAttributeApp().changeAttribute(role,buffer);
			role.getBehavior().notifyAttribute();
			if(onEquipBag){
				//更新装备特效
				GameContext.getMedalApp().updateMedal(role, MedalType.XiangQian,equ);
			}
		}
		result.success();
		return result;
	}
	
	/**
	 * 镶嵌条件
	 */
	private MosaicHoleResult mosaicCondition(RoleInstance role, RoleGoods equGoods, RoleGoods gemGoods){
		MosaicHoleResult result = this.condition(role, equGoods);
		if(!result.isSuccess()){
			return result ;
		}
		result.failure();
		if(null == gemGoods ){
			result.setInfo(GameContext.getI18n().getText(TextId.GOODS_NO_EXISTS));
			return result;
		}
		GoodsGem gemTemplate = GoodsDeriveSupport.getGemTemplate(gemGoods.getGoodsId());
		//匹配颜色
		RoleGoods equ = result.getRoleGoods();
		GoodsEquipment equTemplate = GoodsDeriveSupport.getGoodsEquipment(equ.getGoodsId());
		//判断是否装备所需求的宝石类型
		if(equTemplate.getGemType() != gemTemplate.getSecondType()){
			result.setInfo(GameContext.getI18n().getText(TextId.GOODS_MOSAIC_TYPE_NOT_MEET));
			return result;
		}
		MosaicHole[] holes = equ.getMosaicHoles();
		int matchHoleId = -1 ;
		for(int i=0;i<holes.length;i++){
			MosaicHole hole = holes[i];
			if(null == hole){
				//找到第一个空的宝石位
				matchHoleId = i ;
				break ;
			}
		}
		if(-1 == matchHoleId){
			//没有找到空闲的宝石位置
			result.setInfo(GameContext.getI18n().getText(TextId.GOODS_MOSAIC_HOLE_NOT_ENOUGH));
			return result;
		}
		result.success();
		result.setGemTemplate(gemTemplate);
		result.setRoleGem(gemGoods);
		result.setGoodsTemplate(equTemplate);
		result.setMatchHoleId(matchHoleId);
		return result ;
	}
	
	private MosaicHoleResult condition(RoleInstance role, RoleGoods rg){
		MosaicHoleResult result = new MosaicHoleResult();
		if(null == role){
			//用户不在线
			result.setInfo(GameContext.getI18n().getText(TextId.ROLE_NOT_ONLINE));
			return result ;
		}
		//判断游戏币是否足够
		int roleMoney = role.getSilverMoney();
		MosaicConfig config = GameContext.getGoodsApp().getMosaicConfig();
		if(roleMoney < config.getMosaicMoney()){
			result.setInfo(Status.GOODS_DERIVE_MOSAIC_MONEY_LESS.getTips());
			return result ;
		}
		//将需要的钱币放入结果对象
		result.setMosaicMoney(config.getMosaicMoney());
		
		MosaicHole[] holes = rg.getMosaicHoles();
		if(null == holes || 0 == holes.length){
			result.setInfo(GameContext.getI18n().getText(TextId.ERROR_INPUT));
			return result;
		}
		result.success();
		result.setRoleGoods(rg);
		return result ;
	}
}
