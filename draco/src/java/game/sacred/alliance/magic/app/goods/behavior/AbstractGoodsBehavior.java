package sacred.alliance.magic.app.goods.behavior;

import com.game.draco.GameContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sacred.alliance.magic.app.goods.GoodsHelper;
import sacred.alliance.magic.app.goods.behavior.param.AbstractParam;
import sacred.alliance.magic.base.Result;

public abstract class AbstractGoodsBehavior {
	public final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	protected GoodsBehaviorType behaviorType;
	
	public abstract Result operate(AbstractParam param);
	
	public GoodsBehaviorType getBehaviorType() {
		return behaviorType;
	}
	
	public static boolean isOnBattleHero(String roleId,int bagType,int targetId) {
		return  GoodsHelper.isOnBattleHero(roleId, bagType, targetId);
	}
	
	public static boolean isOnSwitchHero(String roleId,int bagType,int targetId){
		return GoodsHelper.isOnSwitchHero(roleId, bagType, targetId) ;
	}

	public String getText(String textId){
		return GameContext.getI18n().getText(textId);
	}

	public String messageFormat(String textId,Object ... args){
		return GameContext.getI18n().messageFormat(textId,args) ;
	}
}
