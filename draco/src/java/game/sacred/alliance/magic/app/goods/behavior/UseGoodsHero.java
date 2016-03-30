package sacred.alliance.magic.app.goods.behavior;

import sacred.alliance.magic.app.goods.behavior.param.AbstractParam;
import sacred.alliance.magic.app.goods.behavior.param.UseGoodsParam;
import sacred.alliance.magic.app.goods.behavior.result.GoodsResult;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.domain.GoodsHero;
import sacred.alliance.magic.domain.RoleGoods;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;

public class UseGoodsHero extends AbstractGoodsBehavior{
	public UseGoodsHero(){
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
		GoodsHero goodsHero = GameContext.getGoodsApp().getGoodsTemplate(GoodsHero.class, goodsId);
		if(goodsHero == null){
			return result.setInfo(GameContext.getI18n().getText(TextId.ERROR_DATA));
		}
		String errorMsg = this.condition(role, roleGoods,goodsHero);
		if(null != errorMsg){
			return result.setInfo(errorMsg);
		}
		try {
			return GameContext.getHeroApp().useHeroGoods(role, roleGoods,useGoodsParam.isConfirm());
		} catch (Exception e) {
			logger.error("useHeroGoods error",e);
			result.setInfo(GameContext.getI18n().getText(TextId.Sys_Error));
			return result ;
		}
	}
	
	
	private String condition(RoleInstance role, RoleGoods boxGoods, GoodsHero goodsHero){
		int lvLimit = goodsHero.getLvLimit() ;
		if(lvLimit > role.getLevel()){
			return GameContext.getI18n().messageFormat(TextId.USE_GOODS_LVLIMIT_TIPS,lvLimit);
		}
		return null;
	}
	
	
}
