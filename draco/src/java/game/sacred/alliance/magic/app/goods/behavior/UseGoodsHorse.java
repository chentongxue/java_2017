package sacred.alliance.magic.app.goods.behavior;

import sacred.alliance.magic.app.goods.behavior.param.AbstractParam;
import sacred.alliance.magic.app.goods.behavior.param.UseGoodsParam;
import sacred.alliance.magic.app.goods.behavior.result.GoodsResult;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.domain.GoodsHorse;
import sacred.alliance.magic.domain.RoleGoods;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.message.push.C0003_TipNotifyMessage;

public class UseGoodsHorse extends AbstractGoodsBehavior{
	
	public UseGoodsHorse(){
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
		GoodsHorse goodsHorse = GameContext.getGoodsApp().getGoodsTemplate(GoodsHorse.class, goodsId);
		if(goodsHorse == null){
			return result.setInfo(GameContext.getI18n().getText(TextId.ERROR_DATA));
		}
		String errorMsg = this.condition(role, roleGoods,goodsHorse);
		if(null != errorMsg){
			return result.setInfo(errorMsg);
		}
		//使用并删除物品
		try {
			return GameContext.getRoleHorseApp().useHorseGoods(role, roleGoods,useGoodsParam.isConfirm());
		} catch (Exception e) {
			logger.error("useHeroGoods error",e);
			result.setInfo(GameContext.getI18n().getText(TextId.ERROR_DATA));
			return result ;
		}
	}
	
	private String condition(RoleInstance role, RoleGoods boxGoods, GoodsHorse goodsHorse){
		int lvLimit = goodsHorse.getLvLimit() ;
		if(lvLimit > role.getLevel()){
			return GameContext.getI18n().messageFormat(TextId.USE_GOODS_LVLIMIT_TIPS,lvLimit);
		}
		
		boolean flag  = GameContext.getRoleHorseApp().isUseGoodsHorse(role.getIntRoleId(),goodsHorse.getHorseId());
		if(!flag){
			return GameContext.getI18n().messageFormat(TextId.HORSE_GOODS_ERROR,lvLimit);
		}
		
		return null;
	}
	
	
}
