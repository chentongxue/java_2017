package sacred.alliance.magic.app.goods.behavior;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sacred.alliance.magic.app.goods.behavior.param.AbstractParam;
import sacred.alliance.magic.base.Result;

public abstract class AbstractGoodsBehavior {
	public final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	protected GoodsBehaviorType behaviorType;
	
	public abstract Result operate(AbstractParam param);
	
	public GoodsBehaviorType getBehaviorType() {
		return behaviorType;
	}
	
}
