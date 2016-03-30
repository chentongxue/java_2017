package sacred.alliance.magic.app.goods.behavior.derive;

import com.game.draco.GameContext;

import sacred.alliance.magic.app.goods.behavior.AbstractGoodsBehavior;
import sacred.alliance.magic.app.treasure.TreasurePosResult;
import sacred.alliance.magic.domain.RoleGoods;
import sacred.alliance.magic.vo.RoleInstance;

public abstract class GoodsTreasureBehavior extends AbstractGoodsBehavior {
	protected void doIdentify(RoleInstance role,RoleGoods roleGoods){
		GameContext.getTreasureApp().doIdentify(role, roleGoods);
	}
	
	protected TreasurePosResult handleWrongPoint(RoleInstance role, RoleGoods roleGoods){
		return GameContext.getTreasureApp().handleWrongPoint(role, roleGoods);
	}
	
	/*protected void doIdentify(RoleInstance role,RoleGoods roleGoods){
		//标识为已经鉴定
		roleGoods.setOtherParm(roleGoods.getOtherParm() + GoodsTreasure.IDENTIFY_FLAG);
		GameContext.getUserGoodsApp().syncSomeGoodsGridMessage(role, roleGoods);
	}
	
	protected TreasurePosResult handleWrongPoint(RoleInstance role, RoleGoods roleGoods){
		TreasurePosResult posResult = new TreasurePosResult();
		GoodsTreasure treasure = (GoodsTreasure)GameContext.getGoodsApp().getGoodsBase(roleGoods.getGoodsId());
		String otherParam = roleGoods.getOtherParm();
		String[] params = null;
		boolean hasIdentify = false;
		Point point = null;
		if(Util.isEmpty(otherParam)){
			point = treasure.createRandomPoint(null);
			//重新生成藏宝点失败
			if(null == point){
				return posResult;
			}
			
		}else{
			params = GoodsTreasure.parseOtherParams(otherParam);
			String mapId = params[0];
			short x = Short.valueOf(params[1]);
			short y = Short.valueOf(params[2]);
			if(!treasure.isRightMapId(mapId)
					|| !GoodsTreasure.existRoadPoint(mapId, x, y) 
					|| GoodsTreasure.nearJumpPoint(mapId, x, y)){
				point = treasure.createRandomPoint(mapId);
				if(null == point){
					return posResult;
				}
			}else{
				//藏宝点合法
				posResult.setResult(Result.SUCCESS);
				posResult.setPosType(TreasurePosResult.POS_LEGAL);
				return posResult;
			}
			hasIdentify = treasure.hasIdentify(params);
		}
		//重新生成藏宝点成功
		String newOtherParam = treasure.createOtherParams(point);
		roleGoods.setOtherParm(newOtherParam);
		if(hasIdentify){
			this.doIdentify(role, roleGoods);
		}
	
		posResult.setResult(Result.SUCCESS);
		posResult.setPosType(TreasurePosResult.POS_CARETE_SUCESS);
		return posResult;
	}*/
}
