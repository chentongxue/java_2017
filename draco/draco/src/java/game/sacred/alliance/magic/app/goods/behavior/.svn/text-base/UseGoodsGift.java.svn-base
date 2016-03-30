package sacred.alliance.magic.app.goods.behavior;

import java.text.MessageFormat;
import java.util.List;

import com.game.draco.GameContext;

import sacred.alliance.magic.app.goods.GoodsOperateBean;
import sacred.alliance.magic.app.goods.behavior.param.AbstractParam;
import sacred.alliance.magic.app.goods.behavior.param.UseGoodsParam;
import sacred.alliance.magic.app.goods.behavior.result.GoodsResult;
import sacred.alliance.magic.base.OutputConsumeType;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.domain.GoodsGift;
import sacred.alliance.magic.domain.RoleGoods;
import sacred.alliance.magic.vo.RoleInstance;

public class UseGoodsGift extends AbstractGoodsBehavior{
	//private final Logger logger = LoggerFactory.getLogger(ExchangeAppImpl.class);
	public UseGoodsGift(){
		this.behaviorType = GoodsBehaviorType.Use;
	}

	@Override
	public GoodsResult operate(AbstractParam param) {
		UseGoodsParam useGoodsParam = (UseGoodsParam)param;
		RoleInstance role = useGoodsParam.getRole();
		RoleGoods boxGoods = useGoodsParam.getRoleGoods();
		int useCount = 1;
		
		GoodsResult result = new GoodsResult();
		if(useCount <= 0 || useCount > boxGoods.getCurrOverlapCount()){
			return result.setInfo(GameContext.getI18n().getText(TextId.ERROR_INPUT));
		}
		int goodsId = boxGoods.getGoodsId();
		GoodsGift goodsGift = this.getGoodsGift(goodsId);
		if(goodsGift == null){
			return result.setInfo(GameContext.getI18n().getText(TextId.ERROR_DATA));
		}
		String errorMsg = this.condition(role, boxGoods, 1);
		if(null != errorMsg){
			return result.setInfo(errorMsg);
		}
		
		List<GoodsOperateBean> goodsList = goodsGift.getGoodsList();
		
		result = GameContext.getUserGoodsApp().addDelGoodsForBag(
				role, goodsList,OutputConsumeType.gift_box_output, 
				boxGoods, 1, null,OutputConsumeType.treasure_box_use);
		
		if(!result.isSuccess()){
			return result ;
		}
		return result.setResult(GoodsResult.SUCCESS);
	}
	
	
	private String condition(RoleInstance role, RoleGoods boxGoods, int userCount){
		GoodsGift goodsGift= this.getGoodsGift(boxGoods.getGoodsId());
		if(goodsGift == null){
			return GameContext.getI18n().getText(TextId.NO_GOODS);
		}
		if(goodsGift.getLvLimit() > role.getLevel()){
			return GameContext.getI18n().messageFormat(TextId.USE_GOODS_LVLIMIT_TIPS,goodsGift.getLvLimit());
		}
		int currOverlapCount = boxGoods.getCurrOverlapCount();
		if(userCount > currOverlapCount){
			return GameContext.getI18n().getText(TextId.GOODS_NUM_NOT_ENOUGH) ;
		}
		return null;
	}
	
	
	private GoodsGift getGoodsGift(int goodsId){
		try{
			return (GoodsGift)GameContext.getGoodsApp().getGoodsBase(goodsId);
		}catch(Exception e){
			return null;
		}
	}
}
