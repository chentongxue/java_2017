package sacred.alliance.magic.app.goods.behavior;

import com.game.draco.GameContext;
import com.game.draco.app.buff.BuffAddResult;
import com.game.draco.app.buff.BuffDetail;
import com.game.draco.app.buff.RoleExpBuff;
import com.game.draco.message.request.C0514_GoodsConfirmApplyReqMessage;

import sacred.alliance.magic.app.goods.behavior.param.AbstractParam;
import sacred.alliance.magic.app.goods.behavior.param.UseGoodsParam;
import sacred.alliance.magic.app.goods.behavior.result.UseResult;
import sacred.alliance.magic.base.OutputConsumeType;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.domain.GoodsBase;
import sacred.alliance.magic.domain.GoodsExp;
import sacred.alliance.magic.domain.RoleGoods;
import sacred.alliance.magic.vo.RoleInstance;

public class UseGoodsExp extends AbstractGoodsBehavior{
	private final short CONFIRM_CMDID = new C0514_GoodsConfirmApplyReqMessage().getCommandId();
	
	public UseGoodsExp(){
		this.behaviorType = GoodsBehaviorType.Use;
	}
	
	private String canUse(RoleInstance role,GoodsExp goodsExp){
		int lvlimit = goodsExp.getLvLimit();
		short triggerBuffId = goodsExp.getTriggerBuffId();
		int triggerBuffLv = goodsExp.getTriggerBuffLv();
		if(lvlimit > role.getLevel()){
			return GameContext.getI18n().getText(TextId.ROLE_LEVEl_SHORTAGE);
		}
		if(triggerBuffId <= 0 || triggerBuffLv <= 0){
			return GameContext.getI18n().getText(TextId.SYSTEM_ERROR);
		}
		return null ;
	}
	
	@Override
	public Result operate(AbstractParam param) {
		UseGoodsParam useParam = (UseGoodsParam)param ;
		RoleGoods roleGoods = useParam.getRoleGoods();
		int goodsId = roleGoods.getGoodsId();
		GoodsBase goodsBase = GameContext.getGoodsApp().getGoodsBase(goodsId);
		RoleInstance role = param.getRole();
		if(roleGoods == null || role == null || goodsBase == null){
			return new Result().setInfo(GameContext.getI18n().getText(TextId.NO_GOODS));
		}
		GoodsExp exp = (GoodsExp)goodsBase;
		String canResult = this.canUse(role, exp);
		if(canResult != null){
			return new Result().setInfo(canResult);
		}
		boolean isConfirm = useParam.isConfirm();
		BuffAddResult useResult = this.useEffered(role, exp, roleGoods,isConfirm);
		if(useResult.isSuccess()){
			GameContext.getUserGoodsApp().deleteForBagByRoleGoods(role, 
					roleGoods, 1, OutputConsumeType.double_exp_scroll_use);
			return useResult;
		}
		if(useResult.isMustConfirm()){
			//需要二次确认
			UseResult result = new UseResult();
			result.setConfirmInfo(roleGoods.getId());
			result.setMustConfirm(true);
			result.setConfirmCmdId(CONFIRM_CMDID);
			result.success();
			//info放到success后面,success会清除info信息
			result.setInfo(useResult.getInfo());
			return result ;
		}
		return useResult;
	}
	

	private BuffAddResult useEffered(RoleInstance role, GoodsExp exp,
			RoleGoods roleGoods,boolean isConfirm){
		short triggerBuffId = exp.getTriggerBuffId();
		int triggerBuffLv = exp.getTriggerBuffLv();
		RoleExpBuff buff = (RoleExpBuff) GameContext.getBuffApp().getBuff(triggerBuffId);
		if(isConfirm){
			return GameContext.getUserBuffApp().addForceBuffStat(role, 
					role,triggerBuffId,buff.getPersistTime(),triggerBuffLv);
		}
		return GameContext.getUserBuffApp().addBuffStat(role, 
				role,triggerBuffId,buff.getPersistTime(), triggerBuffLv);
	}

}
