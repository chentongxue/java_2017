package sacred.alliance.magic.app.goods.behavior;

import com.game.draco.GameContext;

import sacred.alliance.magic.app.goods.behavior.param.AbstractParam;
import sacred.alliance.magic.app.goods.behavior.param.UseGoodsParam;
import sacred.alliance.magic.base.OutputConsumeType;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.constant.Status;
import sacred.alliance.magic.domain.GoodsTitle;
import sacred.alliance.magic.domain.RoleGoods;
import sacred.alliance.magic.vo.RoleInstance;

public class UseGoodsTitle extends AbstractGoodsBehavior{

	public UseGoodsTitle(){
		this.behaviorType = GoodsBehaviorType.Use;
	}
	
	@Override
	public Result operate(AbstractParam param) {
		UseGoodsParam useParam = (UseGoodsParam)param;
		RoleGoods roleGoods = useParam.getRoleGoods();
		RoleInstance role = useParam.getRole();
		
		GoodsTitle title = GameContext.getGoodsApp().getGoodsTemplate(GoodsTitle.class, roleGoods.getGoodsId());

		Result result = new Result();
		if(title == null){
			return result.setInfo(Status.GOODS_NO_FOUND.getTips());
		}
		
		Status status = GameContext.getTitleApp().addTitle(role, title,true /*useParam.isActivate()*/);
		if(!status.isSuccess()){
			return result.setInfo(status.getTips());
		}
		
		result = GameContext.getUserGoodsApp()
			.deleteForBagByInstanceId(role, roleGoods.getId(),1,OutputConsumeType.goods_use);
		
		return result;
		
	}

}
