package sacred.alliance.magic.app.goods.behavior.derive;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import sacred.alliance.magic.app.chat.ChannelType;
import sacred.alliance.magic.app.chat.ChatSysName;
import sacred.alliance.magic.app.goods.GoodsOperateBean;
import sacred.alliance.magic.app.goods.behavior.AbstractGoodsBehavior;
import sacred.alliance.magic.app.goods.behavior.GoodsBehaviorType;
import sacred.alliance.magic.app.goods.behavior.param.AbstractParam;
import sacred.alliance.magic.app.goods.behavior.param.MixParam;
import sacred.alliance.magic.app.goods.behavior.result.GoodsResult;
import sacred.alliance.magic.base.BindingType;
import sacred.alliance.magic.base.OutputConsumeType;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.domain.MixFormula;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.util.Wildcard;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;

public class GoodsMix extends AbstractGoodsBehavior{
	
	public GoodsMix(){
		this.behaviorType = GoodsBehaviorType.Mix;
	}
	
	@Override
	public GoodsResult operate(AbstractParam param) {
		MixParam mixParam = (MixParam)param;
		int goodsId = mixParam.getGoodsId();
		int mixNum = mixParam.getMixNum();
		RoleInstance role = mixParam.getRole();
		
		GoodsResult result = new GoodsResult();
		
		if(mixNum <= 0 || goodsId<=0){
			return result.setInfo(GameContext.getI18n().getText(TextId.ERROR_INPUT));
		}
		Map<Integer, MixFormula> allMixFormulaMap = GameContext.getGoodsApp().getAllMixFormula();
		MixFormula formula = allMixFormulaMap.get(goodsId);
		//没有找到相关合成配方
		if(null == formula){
			result.setInfo(GameContext.getI18n().getText(TextId.ERROR_INPUT));
			return result ;
		}
		//判断背包是否有空格
		if(role.getRoleBackpack().isFull()){
			result.setInfo(GameContext.getI18n().getText(TextId.GEM_MIX_FULL_BAGINFO));
			return result ;
		}
		//宝石材料
		int srcId = formula.getSrcId();
		if(srcId<=0){
			//这种情况在此不会出现,系统启动的时候已经判断
			result.setInfo(GameContext.getI18n().getText(TextId.ERROR_DATA));
			return result ;
		}
		if(role.getSilverMoney() < formula.getFee() * mixNum){
			//钱币够
			result.setInfo(GameContext.getI18n().getText(TextId.NOT_ENOUGH_GAME_MONEY));
			return result ;
		}

		int roleGemNum = role.getRoleBackpack().countByGoodsId(srcId);
		//总共需要消耗的宝石数
		int totalNeedGemNum = mixNum *  formula.getSrcNum();
		if(roleGemNum < totalNeedGemNum){
			//材料不够
			result.setInfo(GameContext.getI18n().getText(TextId.NOT_ENOUGH_GEM_MATERIAL));
			return result ;
		}
		//消耗的材料
		Map<Integer,Integer> delMap = new HashMap<Integer,Integer>();
		delMap.put(srcId, totalNeedGemNum);
		//合成的宝石都是绑定的
		List<GoodsOperateBean> addList = new ArrayList<GoodsOperateBean>();
		addList.add(new GoodsOperateBean(goodsId, mixNum, BindingType.already_binding));
		//删除,添加物品
		GoodsResult goodsRes = GameContext.getUserGoodsApp().addDelGoodsForBag(role, addList, OutputConsumeType.gem_synthesis, delMap, OutputConsumeType.gem_synthesis_consume);
		if(!goodsRes.isSuccess()){
			return goodsRes;
		}
		//扣钱
		GoodsDeriveSupport.payMoney(role, formula.getFee() * mixNum,OutputConsumeType.gem_synthesis_consume);
		//世界广播
		this.broadcast(role, formula);
		result.setInfo(GameContext.getI18n().getText(TextId.GOODS_MIX_SUCCESS));
		return result.success();
	}
	
	private void broadcast(RoleInstance role, MixFormula mixFormula){
		try{
			String broadcastMsg = mixFormula.getBroadcast();
			if(Util.isEmpty(broadcastMsg)) {
				return;
			}
			String goodsContent = Wildcard.getChatGoodsContent(mixFormula.getTargetId(), ChannelType.Publicize_Personal);
			String message = MessageFormat.format(broadcastMsg,role.getRoleName(),goodsContent);							
			GameContext.getChatApp().sendSysMessage(ChatSysName.System, ChannelType.Publicize_Personal, message, null, null);
		}catch(Exception e){
			logger.error("EquipUpgrade.broadcast",e);
		}
	}
}
