package sacred.alliance.magic.app.goods.behavior;

import com.game.draco.GameContext;
import com.game.draco.app.quest.Quest;

import sacred.alliance.magic.app.goods.behavior.param.AbstractParam;
import sacred.alliance.magic.app.goods.behavior.param.UseGoodsParam;
import sacred.alliance.magic.app.goods.behavior.result.GoodsResult;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.domain.GoodsTaskprops;
import sacred.alliance.magic.domain.RoleGoods;
import sacred.alliance.magic.vo.RoleInstance;

public class UseGoodsTaskprops extends AbstractGoodsBehavior{

	public UseGoodsTaskprops(){
		this.behaviorType = GoodsBehaviorType.Use;
	}
	
	@Override
	public GoodsResult operate(AbstractParam param) {
		UseGoodsParam useGoodsParam = (UseGoodsParam)param;
		RoleInstance role = useGoodsParam.getRole();
		RoleGoods taskpropGoods = useGoodsParam.getRoleGoods();
		
		GoodsTaskprops goodsTaskprops  = getGoodsTaskprops(taskpropGoods.getGoodsId());
		GoodsResult result = this.condition(role, goodsTaskprops);
		if(!result.isSuccess()){
			return result;
		}
		
		GameContext.getUserQuestApp().questTrigger((RoleInstance) role, goodsTaskprops);
		
		return result.setResult(GoodsResult.SUCCESS);
	}
	
	private GoodsTaskprops getGoodsTaskprops(int goodsId){
		try{
			GoodsTaskprops taskprops = (GoodsTaskprops)GameContext.getGoodsApp().getGoodsBase(goodsId);
			return taskprops;
		}catch(Exception e){
			return null;
		}
	}

	
	private GoodsResult condition(RoleInstance role, GoodsTaskprops goodsTaskprops){
		GoodsResult result = new GoodsResult();
		result.setResult(GoodsResult.FAIL);
		if(goodsTaskprops == null){
			return result.setInfo(GameContext.getI18n().getText(TextId.ERROR_INPUT));
		}
		
		int taskId = goodsTaskprops.getTaskId();
		
		if (0 >= taskId) {
			return result.setInfo(GameContext.getI18n().getText(TextId.ERROR_INPUT));
		}
		Quest quest = GameContext.getQuestApp().getQuest(taskId);
		if (null == quest) {
			return result.setInfo(GameContext.getI18n().getText(TextId.USE_GOODS_TASK_NOT_EXIST));
		}
		int resutlt = GameContext.getUserQuestApp().isCanQuestTrigger((RoleInstance)role, goodsTaskprops);
		if(2 == resutlt){
			return result.setInfo(GameContext.getI18n().getText(TextId.USE_GOODS_TASK_NOT_EXIST));
		}
		if(3 == resutlt){
			return result.setInfo(GameContext.getI18n().getText(TextId.USE_GOODS_TASK_CONDITION_NOT_MEET));
		}
		return result.setResult(GoodsResult.SUCCESS);
	}
}
