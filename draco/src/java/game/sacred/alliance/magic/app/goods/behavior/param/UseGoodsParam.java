package sacred.alliance.magic.app.goods.behavior.param;

import lombok.Data;
import sacred.alliance.magic.domain.RoleGoods;
import sacred.alliance.magic.vo.RoleInstance;

/**
 * 吃药物品参数类
 * @author Wang.K
 *
 */
public @Data class UseGoodsParam extends AbstractParam{
	
	private RoleGoods roleGoods;
	private int useCount = 1; //批量使用数目
	//是否二次确认使用
	private boolean confirm = false ;
	//称号激活
	private boolean activate = false;
	
	public UseGoodsParam(RoleInstance role) {
		super(role);
	}
	
}
