package sacred.alliance.magic.app.goods.behavior;

import sacred.alliance.magic.app.goods.behavior.param.AbstractParam;
import sacred.alliance.magic.app.goods.behavior.param.UseGoodsParam;
import sacred.alliance.magic.app.goods.behavior.result.GoodsResult;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.domain.RoleGoods;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.pet.domain.GoodsPet;

public class UseGoodsPet extends AbstractGoodsBehavior {
	
	public UseGoodsPet() {
		this.behaviorType = GoodsBehaviorType.Use;
	}

	@Override
	public Result operate(AbstractParam param) {
		UseGoodsParam useGoodsParam = (UseGoodsParam)param;
		RoleInstance role = useGoodsParam.getRole();
		RoleGoods roleGoods = useGoodsParam.getRoleGoods();
		int useCount = 1;
		
		GoodsResult result = new GoodsResult();
		if(useCount <= 0 || useCount > roleGoods.getCurrOverlapCount()){
			return result.setInfo(GameContext.getI18n().getText(TextId.ERROR_INPUT));
		}
		int goodsId = roleGoods.getGoodsId();
		GoodsPet goodsPet = GameContext.getGoodsApp().getGoodsTemplate(GoodsPet.class, goodsId);
		if(null == goodsPet) {
			return result.setInfo(GameContext.getI18n().getText(TextId.ERROR_DATA));
		}
		String errorMsg = this.condition(role, roleGoods,goodsPet);
		if(null != errorMsg){
			return result.setInfo(errorMsg);
		}
		//使用并删除物品
		try {
			return GameContext.getPetApp().usePetGoods(role, roleGoods, useGoodsParam.isConfirm());
		} catch (Exception e) {
			logger.error("usePetGoods error",e);
			result.setInfo(GameContext.getI18n().getText(TextId.ERROR_DATA));
			return result ;
		}
	}
	
	private String condition(RoleInstance role, RoleGoods petGoods, GoodsPet goodsPet) {
		int lvLimit = goodsPet.getLvLimit() ;
		if(lvLimit > role.getLevel()){
			return GameContext.getI18n().messageFormat(TextId.USE_GOODS_LVLIMIT_TIPS,lvLimit);
		}
		return null;
	}

}
