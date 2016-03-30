//package sacred.alliance.magic.app.goods.behavior;
//
//import java.text.MessageFormat;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import com.game.draco.GameContext;
//import com.game.draco.message.push.C0003_TipNotifyMessage;
//import com.game.draco.message.request.C0514_GoodsConfirmApplyReqMessage;
//
//import sacred.alliance.magic.app.faction.integral.IntegralResult;
//import sacred.alliance.magic.app.faction.integral.UseGoods;
//import sacred.alliance.magic.app.goods.behavior.derive.GoodsTreasureBehavior;
//import sacred.alliance.magic.app.goods.behavior.param.AbstractParam;
//import sacred.alliance.magic.app.goods.behavior.param.UseGoodsParam;
//import sacred.alliance.magic.app.goods.behavior.result.UseResult;
//import sacred.alliance.magic.base.OperatorType;
//import sacred.alliance.magic.base.OutputConsumeType;
//import sacred.alliance.magic.base.Result;
//import sacred.alliance.magic.constant.TextId;
//import sacred.alliance.magic.domain.GoodsFaction;
//import sacred.alliance.magic.domain.RoleGoods;
//import sacred.alliance.magic.vo.RoleInstance;
//
//
//public class UseGoodsFaction extends GoodsTreasureBehavior{
//	private final static short  Confirm_Use_Cmd_Id = new C0514_GoodsConfirmApplyReqMessage().getCommandId();
//	private final static Logger logger = LoggerFactory.getLogger(UseGoodsFaction.class);
//	public UseGoodsFaction(){
//		this.behaviorType = GoodsBehaviorType.Use;
//	}
//	
//	
//	private IntegralResult use(RoleInstance role, GoodsFaction goodsBase,
//			UseGoods ug, boolean isForcedAdd) {
//		IntegralResult integralResult = GameContext.getFactionApp()
//				.changeFactionIntegral(role, OperatorType.Add,
//						goodsBase.getFactionIntegral(), ug, isForcedAdd);
//		if (!integralResult.isSuccess()) {
//			return integralResult ;
//		}
//		GameContext.getFactionApp().changeContributeNum(role, OperatorType.Add,
//				goodsBase.getFactionContribute());
//		return integralResult ;
//	}
//	
//	@Override
//	public Result operate(AbstractParam param) {
//		Result result = new Result();
//		UseGoodsParam useGoodsParam = (UseGoodsParam)param;
//		RoleInstance role = useGoodsParam.getRole();
//		if(!role.hasFaction()){
//			result.setInfo(GameContext.getI18n().getText(TextId.USE_GOODS_FACTION_HAS_NO_FACTION));
//			return result; 
//		}
//		//判断是否有积分
//		RoleGoods goods = useGoodsParam.getRoleGoods();
//		int goodsId = goods.getGoodsId();
//		GoodsFaction goodsBase = GameContext.getGoodsApp().getGoodsTemplate(GoodsFaction.class, goodsId);
//		if(null == goodsBase){
//			result.setInfo(GameContext.getI18n().getText(TextId.Sys_Param_Error));
//			return result; 
//		}
//		if(role.getLevel() < goodsBase.getLvLimit()){
//			result.setInfo(GameContext.getI18n().messageFormat(TextId.USE_GOODS_FACTION_LEVEL_NOT_ENOUGH, goodsBase.getLvLimit()));
//			return result; 
//		}
//		int count = useGoodsParam.getUseCount();
//		if(count > goods.getCurrOverlapCount() || count <=0){
//			result.setInfo(TextId.USE_GOODS_FACTION_NUM_ERROR);
//			return result; 
//		}
//		
//		UseGoods ug = new UseGoods(goodsBase.getId(),1);
//		
//		long con = 0 ;
//		long integral = 0 ;
//		boolean isConfirm = useGoodsParam.isConfirm() ;
//		if(isConfirm){
//			//二次确认只能使用一次
//			count = 1 ;
//		}
//		int successNum = 0 ;
//		IntegralResult integralResult = null ;
//		for(int i=0;i<count;i++){
//			//添加相关积分
//			integralResult = this.use(role, goodsBase, ug, isConfirm);
//			if(integralResult.isSuccess()){
//				 con += goodsBase.getFactionContribute() ;
//				 integral += integralResult.getEffectValue();
//				 successNum ++ ;
//				continue ;
//			}
//			if(integralResult.isMustConfirm()){
//				//必须二次确认
//				UseResult useResult = new UseResult();
//				useResult.setMustConfirm(true);
//				useResult.setConfirmCmdId(Confirm_Use_Cmd_Id);
//				useResult.setInfo(integralResult.getInfo());
//				useResult.setConfirmInfo(goods.getId());
//				useResult.success();
//				return useResult ;
//			}
//			if(!integralResult.isSuccess()){
//				break ;
//			}
//		}
//		if(!integralResult.isSuccess() && successNum <=0){
//			result.setInfo(integralResult.getInfo());
//			return result ;
//		}
//		//扣除物品
//		GameContext.getUserGoodsApp().deleteForBagByInstanceId(role, goods.getId(),successNum, 
//				OutputConsumeType.goods_faction_use);
//		
//		result.success();
//		if(con <=0 && integral <=0){
//			return result ;
//		}
//		try {
//			StringBuffer buffer = new StringBuffer(GameContext.getI18n().getText(TextId.USE_GOODS_FACTION_GAIN));
//			if (con > 0) {
//				buffer.append(con).append(GameContext.getI18n().getText(TextId.USE_GOODS_FACTION_CON)).append(" ");
//			}
//			if (integral > 0) {
//				buffer.append(integral).append(GameContext.getI18n().getText(TextId.USE_GOODS_FACTION_INTEGRAL));
//			}
//			C0003_TipNotifyMessage msg = new C0003_TipNotifyMessage();
//			msg.setMsgContext(buffer.toString());
//			role.getBehavior().sendMessage(msg);
//		}catch(Exception ex){
//			logger.error("",ex);
//		}
//		return result ;
//	}
//}
