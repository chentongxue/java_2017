package sacred.alliance.magic.app.quickbuy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.game.draco.GameContext;
import com.game.draco.app.shop.config.ShopGoodsConfig;
import com.game.draco.message.push.C0009_GoldOrBindConfirmNotifyMessage;
import com.game.draco.message.request.C2110_QuickBuyReqMessage;

import sacred.alliance.magic.app.goods.GoodsOperateBean;
import sacred.alliance.magic.app.goods.behavior.result.GoodsResult;
import sacred.alliance.magic.base.OutputConsumeType;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.constant.Status;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.domain.GoodsBase;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.vo.RoleInstance;

public class QuickBuyAppImpl implements QuickBuyApp {
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	/**
	 * 构建参数
	 * @param cmdId
	 * @param moneyType
	 * @return
	 */
	private String builParameter(short cmdId){
		return String.valueOf(cmdId);
	}
	
	@Override
	public short getCommandIdByParameter(String parameter) {
		if(Util.isEmpty(parameter)){
			return 0;
		}
		return Short.valueOf(parameter);
	}
	
	@Override
	public Set<Short> getCommandIdSet() {
		return GameContext.getQuickBuyConfig().getCommandIdSet();
	}
	
	private QuickBuyResult doProcess(RoleInstance role, List<GoodsOperateBean> goodsList, OutputConsumeType outputConsumeType){
		Map<Integer,Integer> goodsMap = new HashMap<Integer,Integer>();
		if(!Util.isEmpty(goodsList)){
			for(GoodsOperateBean bean : goodsList){
				if(null == bean){
					continue;
				}
				goodsMap.put(bean.getGoodsId(), bean.getGoodsNum());
			}
		}
		return this.doProcess(role, goodsMap, outputConsumeType);
	}

	private QuickBuyResult doProcess(RoleInstance role, Map<Integer,Integer> goodsMap, OutputConsumeType outputConsumeType) {
		QuickBuyResult result = new QuickBuyResult();
		//快速购买没有开启，则直接返回。各模块走正常逻辑
		if(!GameContext.getQuickBuyConfig().isQuickBuyOpen()){
			return result.setNotSupport();
		}
		QuickBuy quickBuy = this.getQuickBuy(goodsMap);
		//是确认购买，扣除相应的金钱
		if(role.isQuickBuyconfirm()){
			if(null == quickBuy){
				return result.setPayFailure(Status.QuickBuy_Param_Error.getTips());
			}
			Message lastMsg = role.getLastTrigQuickBuyReqMessage();
			Message currMsg = role.getCurrCanQuickBuyReqMessage();
			if(null == lastMsg || null == currMsg || lastMsg.getCommandId() != currMsg.getCommandId()){
				//清掉角色身上快速购买的确认标记
				role.setQuickBuyconfirm(false);
				return result.setPayFailure(Status.QuickBuy_Param_Error.getTips());
			}
			return quickBuy.deductMoney(role, outputConsumeType);
		}
		//不是购买
		//判断是否支持快捷购买
		if(null == quickBuy || quickBuy.isQuickBuyGoodsEmpty()){
			return result.setNotSupport();
		}
		//判断道具是否足够
		if(quickBuy.isGoodsEnough(role)){
			return result.setGoodsEnough();
		}
		//道具不足，发快速购买的弹板
		if(this.notifyQuickBuyMessage(role, quickBuy)){
			return result.setSendBuyMessage();
		}
		return result.setNotSupport();
	}
	
	private QuickBuy getQuickBuy(Map<Integer,Integer> goodsMap) {
		try {
			if(Util.isEmpty(goodsMap)){
				return null;
			}
			QuickBuy quickBuy = new QuickBuy();
			List<QuickBuyGoods> quickBuyGoodsList = new ArrayList<QuickBuyGoods>();
			for(Map.Entry<Integer,Integer> entry : goodsMap.entrySet()){
				if(null == entry){
					continue;
				}
				int goodsId = entry.getKey();
				ShopGoodsConfig shopGoods = GameContext.getShopApp().getShopGoods(goodsId);
				//商城中没有此物品，或物品元宝价格<=0。则不支持快速购买。
				if(null == shopGoods){
					return null;
				}
				int goldPrice = shopGoods.getDisPrice();
				if(goldPrice <= 0){
					return null;
				}
				QuickBuyGoods quickGoods = new QuickBuyGoods();
				quickGoods.setGoodsId(goodsId);
				quickGoods.setGoodsNum(entry.getValue());
				quickGoods.setGoldPrice(goldPrice);
				quickBuyGoodsList.add(quickGoods);
			}
			quickBuy.setQuickBuyGoodsList(quickBuyGoodsList);
			return quickBuy;
		} catch (Exception e) {
			this.logger.error("QuickBuyApp.getQuickBuy error: ", e);
			return null;
		}
	}
	
	private boolean notifyQuickBuyMessage(RoleInstance role, QuickBuy quickBuy) {
		Message currMsg = role.getCurrCanQuickBuyReqMessage();
		if(null == quickBuy || null == currMsg){
			return false;
		}
		//将当前请求消息缓存下来，为快速购买确认的时候使用
		role.setLastTrigQuickBuyReqMessage(currMsg);
		
		int payGoldMoney = quickBuy.getPayGoldMoney(role);
		//已经设置自动二次确认的，不发二次确认消息，直接执行购买逻辑
		if(role.isQuickBuyAutoConfirm(payGoldMoney)){
			//标记为是确认购买
			role.setQuickBuyconfirm(true);
			//将快速购买需要的message放入执行器中,必须可重复(排队执行)
			role.getBehavior().addCumulateEvent(currMsg);
			return true;
		}
		short currCmdId = currMsg.getCommandId();//当前请求消息的命令字
		short quickBuyCommandId = new C2110_QuickBuyReqMessage().getCommandId();//快速购买协议的命令字
		
		C0009_GoldOrBindConfirmNotifyMessage confirmMsg = new C0009_GoldOrBindConfirmNotifyMessage();
		confirmMsg.setInfo(quickBuy.getConfirmMsgInfo(role));
		confirmMsg.setGoldType((byte) 0);
		if(role.getGoldMoney() >= payGoldMoney){
			confirmMsg.setCmdId(quickBuyCommandId);
			confirmMsg.setParam(this.builParameter(currCmdId));
			confirmMsg.setGoldType((byte) 1);
		}else {
			//元宝不足让客户端打开充值界面
			confirmMsg.setCmdId(QuickCostHelper.Charge_CmdId);
		}
		confirmMsg.setAlertSet((byte) 1);
		
		role.getBehavior().sendMessage(confirmMsg);
		return true;
	}
	
	@Override
	public Result doQuickBuy(RoleInstance role, int goodsId, int goodsNum,
			OutputConsumeType outputType, String notEnoughGoodsTips) {
		Map<Integer,Integer> goodsMap = new HashMap<Integer,Integer>();
		if(goodsId > 0 && goodsNum > 0){
			goodsMap.put(goodsId, goodsNum);
		}
		//快捷购买逻辑
		QuickBuyResult quickBuyResult = this.doProcess(role, goodsMap, outputType);
		if(quickBuyResult.isIgnore()){
			return quickBuyResult;
		}
		//不支持快速购买，或者是道具足够
		if(quickBuyResult.isNotSupport() || quickBuyResult.isGoodsEnough()){
			
			GoodsBase goodsBase = GameContext.getGoodsApp().getGoodsBase(goodsId);
			boolean flag = GameContext.getUserGoodsApp().isExistGoodsForBag(role, goodsId);
			if(!flag){
				Result result = new Result();
				result.setInfo(GameContext.getI18n().messageFormat(TextId.GOODS_NO_ENOUGH_NAME,goodsBase.getName()));
				return result;
			}
			
			// 扣除物品
			GoodsResult goodsResult = GameContext.getUserGoodsApp()
					.deleteForBag(role, goodsId, goodsNum, outputType);
			//需要替换提示信息
			if(!Util.isEmpty(notEnoughGoodsTips) && !goodsResult.isSuccess()){
				goodsResult.setInfo(notEnoughGoodsTips) ;
			}
			return goodsResult;
		}
		//快速购买支付失败，或已经发确认购买消息，或付费成功
		return quickBuyResult;
	}
	
	@Override
	public Result doQuickBuy(RoleInstance role, List<GoodsOperateBean> goodsList, 
			OutputConsumeType outputType, String notEnoughGoodsTips) {
		//快捷购买逻辑
		QuickBuyResult quickBuyResult = this.doProcess(role, goodsList, outputType);
		if(quickBuyResult.isIgnore()){
			return quickBuyResult;
		}
		//不支持快速购买，或者是道具足够
		if(quickBuyResult.isNotSupport() || quickBuyResult.isGoodsEnough()){
			// 扣除物品
			GoodsResult goodsResult = GameContext.getUserGoodsApp().deleteForBean(role, goodsList, outputType);
			//需要替换提示信息
			if(!Util.isEmpty(notEnoughGoodsTips) && !goodsResult.isSuccess()){
				goodsResult.setInfo(notEnoughGoodsTips) ;
			}
			return goodsResult;
		}
		//快速购买支付失败，或已经发确认购买消息，或付费成功
		return quickBuyResult;
	}

	@Override
	public Result doQuickBuy(RoleInstance role, Map<Integer, Integer> goodsMap,
			OutputConsumeType outputType, String notEnoughGoodsTips) {
		//快捷购买逻辑
		QuickBuyResult quickBuyResult = this.doProcess(role, goodsMap, outputType);
		if(quickBuyResult.isIgnore()){
			return quickBuyResult;
		}
		//不支持快速购买，或者是道具足够
		if(quickBuyResult.isNotSupport() || quickBuyResult.isGoodsEnough()){
			// 扣除物品
			GoodsResult goodsResult = GameContext.getUserGoodsApp()
					.deleteForBagByMap(role, goodsMap, outputType);
			//需要替换提示信息
			if(!Util.isEmpty(notEnoughGoodsTips) && !goodsResult.isSuccess()){
				goodsResult.setInfo(notEnoughGoodsTips) ;
			}
			return goodsResult;
		}
		//快速购买支付失败，或已经发确认购买消息，或付费成功
		return quickBuyResult;
	}
	
}
