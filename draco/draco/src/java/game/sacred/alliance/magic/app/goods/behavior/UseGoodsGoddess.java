package sacred.alliance.magic.app.goods.behavior;

import com.game.draco.GameContext;
import com.game.draco.message.push.C0003_TipNotifyMessage;

import sacred.alliance.magic.app.goods.behavior.param.AbstractParam;
import sacred.alliance.magic.app.goods.behavior.param.UseGoodsParam;
import sacred.alliance.magic.app.goods.behavior.result.GoodsResult;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.domain.GoodsGoddess;
import sacred.alliance.magic.domain.RoleGoods;
import sacred.alliance.magic.vo.RoleInstance;

public class UseGoodsGoddess extends AbstractGoodsBehavior {
	
	public UseGoodsGoddess() {
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
		GoodsGoddess goodsGoddess = GameContext.getGoodsApp().getGoodsTemplate(GoodsGoddess.class, goodsId);
		if(null == goodsGoddess) {
			return result.setInfo(GameContext.getI18n().getText(TextId.ERROR_DATA));
		}
		String errorMsg = this.condition(role, roleGoods,goodsGoddess);
		if(null != errorMsg){
			return result.setInfo(errorMsg);
		}
		//使用并删除物品
		Result useResult = null;
		try {
			useResult = GameContext.getGoddessApp().useGoddessGoods(role, roleGoods);
		} catch (Exception e) {
			logger.error("useGoddessGoods error",e);
			result.setInfo(GameContext.getI18n().getText(TextId.ERROR_DATA));
			return result ;
		}
		if(!useResult.isSuccess()){
			result.setInfo(useResult.getInfo());
			return result ;
		}
		//push通知客户端
		String tips = GameContext.getI18n().messageFormat(TextId.Goddess_goods_use_success_tips,goodsGoddess.getName());
		role.getBehavior().sendMessage(new C0003_TipNotifyMessage(tips));
		
		return result.setResult(GoodsResult.SUCCESS);
	}
	
	private String condition(RoleInstance role, RoleGoods goddessGoods, GoodsGoddess goodsGoddess) {
		//TODO:
		return null;
	}

}
