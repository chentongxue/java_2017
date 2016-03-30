package sacred.alliance.magic.app.goods.behavior.derive;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.game.draco.GameContext;

import sacred.alliance.magic.app.goods.behavior.GoodsBehaviorType;
import sacred.alliance.magic.app.goods.behavior.param.AbstractParam;
import sacred.alliance.magic.app.goods.behavior.param.UseGoodsParam;
import sacred.alliance.magic.app.goods.behavior.result.GoodsResult;
import sacred.alliance.magic.app.treasure.TreasurePosResult;
import sacred.alliance.magic.base.OutputConsumeType;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.domain.GoodsBase;
import sacred.alliance.magic.domain.GoodsTreasure;
import sacred.alliance.magic.domain.RoleGoods;
import sacred.alliance.magic.vo.RoleInstance;

public class GoodsTreasureIdentify extends GoodsTreasureBehavior {
	private Logger log = LoggerFactory.getLogger(this.getClass());
	
	public GoodsTreasureIdentify(){
		this.behaviorType = GoodsBehaviorType.treasureIdentify;
	}
	
	@Override
	public Result operate(AbstractParam param) {
		GoodsResult result = new GoodsResult();
		result.failure();
		try{
			UseGoodsParam useGoodsParam = (UseGoodsParam)param;
			RoleGoods roleGoods = useGoodsParam.getRoleGoods();
			RoleInstance role = useGoodsParam.getRole();
			int goodsId = roleGoods.getGoodsId();
			GoodsBase goodsBase = GameContext.getGoodsApp().getGoodsBase(goodsId);
			if(roleGoods == null || role == null || goodsBase == null){
				return result.setInfo(GameContext.getI18n().getText(TextId.NO_GOODS));
			}
			GoodsTreasure treasure = (GoodsTreasure)goodsBase;
			//判断是否已经有坐标信息
			TreasurePosResult posResult = this.handleWrongPoint(role, roleGoods);
			if(!posResult.isSuccess()){
				return result.setInfo(GameContext.getI18n().getText(TextId.TREASURE_IDENTIFY_FAILE));
			}
			
			String[] params = GoodsTreasure.parseOtherParams(roleGoods.getOtherParm());
			//已经鉴定过
			if(treasure.hasIdentify(params)){
				return result.setInfo(GameContext.getI18n().getText(TextId.TREASURE_HAS_IDENTIFY));
			}
			if(treasure.getIdentifyGoodsId()<=0){
				//没有配置鉴定道具
				this.doIdentify(role,roleGoods);
				result.success();
				return result ;
			}
			/*if(!(role.getRoleBackpack().existGoods(treasure.getIdentifyGoodsId()))){
				result.setInfo(treasure.getLackIdenGoodInfo());
				return result ;
			}*/
			
			//扣除物品
			GoodsResult goodsResult = GameContext.getUserGoodsApp().deleteForBagByGoodsId(role, 
					treasure.getIdentifyGoodsId(),OutputConsumeType.treasure_map_identify);
			if(goodsResult.isSuccess()){
				this.doIdentify(role,roleGoods);
				result.setResult(goodsResult.getResult());
				return result;
			}
			//道具不存在
			result.setInfo(treasure.getLackIdenGoodInfo());
			return result ;
		}catch(Exception e){
			log.error("", e);
		}
		return result.setInfo(GameContext.getI18n().getText(TextId.SYSTEM_ERROR));
	}
	
}
