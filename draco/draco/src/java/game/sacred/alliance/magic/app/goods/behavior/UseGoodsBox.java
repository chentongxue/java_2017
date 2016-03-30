package sacred.alliance.magic.app.goods.behavior;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import sacred.alliance.magic.app.goods.GoodsOperateBean;
import sacred.alliance.magic.app.goods.behavior.param.AbstractParam;
import sacred.alliance.magic.app.goods.behavior.param.UseGoodsParam;
import sacred.alliance.magic.app.goods.behavior.result.GoodsResult;
import sacred.alliance.magic.base.AttributeType;
import sacred.alliance.magic.base.BroadcastType;
import sacred.alliance.magic.base.OperatorType;
import sacred.alliance.magic.base.OutputConsumeType;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.domain.GoodsBase;
import sacred.alliance.magic.domain.GoodsBox;
import sacred.alliance.magic.domain.RoleGoods;
import sacred.alliance.magic.util.Converter;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;

public class UseGoodsBox extends AbstractGoodsBehavior{
	private final int showCount = 1; //表示：开几个宝箱时，显示获得列表
	public UseGoodsBox(){
		this.behaviorType = GoodsBehaviorType.Use;
	}

	@Override
	public GoodsResult operate(AbstractParam param) {
		UseGoodsParam useGoodsParam = (UseGoodsParam)param;
		RoleInstance role = useGoodsParam.getRole();
		RoleGoods boxGoods = useGoodsParam.getRoleGoods();
		int useCount = useGoodsParam.getUseCount();
		
		GoodsResult result = new GoodsResult();
		if(useCount <= 0 || useCount > boxGoods.getCurrOverlapCount()){
			return result.setInfo(GameContext.getI18n().getText(TextId.Sys_Param_Error));
		}
		
		
		List<GoodsOperateBean> awardsList = new ArrayList<GoodsOperateBean>();
		for(int index = 0 ; index < useCount; index++){
			String errorMsg = this.condition(role, boxGoods, 1);
			if(errorMsg != null){
				return result.setInfo(errorMsg);
			}
			List<GoodsOperateBean> list = this.openGoodsBox(role, boxGoods);
			if(list == null){
				break ;
			}
			awardsList.addAll(list);
		}
		
		GoodsBox goodsBox = this.getGoodsBox(boxGoods.getGoodsId());
		if(useCount <= showCount){
			sendGoodsBoxInfo(role, awardsList, goodsBox);
		}
		
		this.broadcast(role.getRoleName(), awardsList, boxGoods.getGoodsId());
		return result.setResult(GoodsResult.SUCCESS);
	}
	
	
	private String condition(RoleInstance role, RoleGoods boxGoods, int userCount){
		GoodsBox goodsBox = this.getGoodsBox(boxGoods.getGoodsId());
		if(goodsBox == null){
			return GameContext.getI18n().getText(TextId.NO_GOODS);
		}
		
		if(goodsBox.getLvLimit() > role.getLevel()){
			return GameContext.getI18n().messageFormat(TextId.USE_GOODS_BOX_LEVEL_NOT_ENOUGH,goodsBox.getLvLimit());
		}
		
		int currOverlapCount = boxGoods.getCurrOverlapCount();
		if(userCount > currOverlapCount){
			return GameContext.getI18n().getText(TextId.USE_GOODS_BOX_GOODS_NOT_ENOUGH);
		}
		
		int freeGoodsGridCount = role.getRoleBackpack().freeGridCount();
		int needGoodsGridCount = goodsBox.getNeedGoodsGridCount();
		
		if(freeGoodsGridCount < needGoodsGridCount){
			return GameContext.getI18n().messageFormat(TextId.USE_GOODS_BOX_GRID_NOT_ENOUGH, needGoodsGridCount);
		}
		
		//判断是否有钥匙
		int keyId = goodsBox.getKeyId();
		if(keyId > 0){
			boolean existKey = GameContext.getUserGoodsApp().isExistGoodsForBag(role, keyId);
			if(!existKey){
				GoodsBase key = GameContext.getGoodsApp().getGoodsBase(keyId);
				if(null == key){
					return GameContext.getI18n().getText(TextId.USE_GOODS_BOX_KEY_NOT_ENOUGH);
				}
				return GameContext.getI18n().messageFormat(TextId.USE_GOODS_BOX_NAME_KEY_NOT_ENOUGH, key.getName());
			}
		}
		
		return null;
	}
	
	
	private List<GoodsOperateBean> openGoodsBox(RoleInstance role,RoleGoods boxGoods){
		int goodsId = boxGoods.getGoodsId();
		GoodsBox goodsBox = this.getGoodsBox(goodsId);
		if(goodsBox == null){
			return null;
		}
		
		int keyId = goodsBox.getKeyId();
		Map<Integer, Integer> delMap = new HashMap<Integer, Integer>();
		if(keyId >0){
			delMap.put(keyId, 1);
		}
		
		List<GoodsOperateBean> goodsList = goodsBox.getGoodsList();
		GoodsResult result = GameContext.getUserGoodsApp().addDelGoodsForBag(
				role, goodsList,OutputConsumeType.treasure_box_output, 
				boxGoods, 1, delMap, OutputConsumeType.treasure_box_use);
		if(!result.isSuccess()){
			return null;
		}
		 
		 if(goodsBox.getGoldMoney() > 0){
			 GameContext.getUserAttributeApp().changeRoleMoney(role, 
					 AttributeType.goldMoney,OperatorType.Add,goodsBox.getGoldMoney(),
					 OutputConsumeType.treasure_box_output);
		 }
		 if(goodsBox.getSilverMoney() > 0 ){
			 GameContext.getUserAttributeApp().changeRoleMoney(role, 
					 AttributeType.silverMoney,OperatorType.Add,goodsBox.getSilverMoney(),
					 OutputConsumeType.treasure_box_output);
		 }
		 if(goodsBox.getBindingGoldMoney() > 0){
			 GameContext.getUserAttributeApp().changeRoleMoney(role, 
					 AttributeType.bindingGoldMoney,OperatorType.Add,goodsBox.getBindingGoldMoney(),
					 OutputConsumeType.treasure_box_output);
		 }
		
		 role.getBehavior().notifyAttribute();
		 
		 return goodsList;
	}
	
	
	private void sendGoodsBoxInfo(RoleInstance role, List<GoodsOperateBean> goodsList, GoodsBox goodsBox){
		Converter.pushIncomeMessage(role, goodsList, goodsBox.getBindingGoldMoney(),
				 goodsBox.getSilverMoney(), goodsBox.getGoldMoney());
		/*
		 BoxInfoNotifyMessage push = new BoxInfoNotifyMessage();
		 if(!Util.isEmpty(goodsList)){
			 GoodsBase goodsBase;
			 for(GoodsOperateBean bean : goodsList){
				 int goodsId = bean.getGoodsId();
				 goodsBase = GameContext.getGoodsApp().getGoodsBase(goodsId);
				 if(goodsBase == null){
					 continue;
				 }
				 BoxInfoItem item = new BoxInfoItem();
				 item.setGoodsName(goodsBase.getName());
				 item.setNum((short)bean.getAddNum());
				 push.getList().add(item);
			 }
		 }
		 			
		 if(goodsBox.getGoldMoney() > 0 ){
			 BoxInfoItem item = new BoxInfoItem();
			 item.setGoodsName(AttributeType.goldMoney.getName());
			 item.setNum((short)goodsBox.getGoldMoney());
			 push.getList().add(item);
		 }
		 if(goodsBox.getSilverMoney() > 0 ){
			 	int showGold = goodsBox.getSilverMoney()/100;
	    		int showSilver = goodsBox.getSilverMoney()%100;
	    		if(showGold > 0){
	    			 BoxInfoItem item = new BoxInfoItem();
					 item.setGoodsName("金币");
					 item.setNum((short)showGold);
					 push.getList().add(item);
	    		}
	    		if(showSilver > 0){
	    			 BoxInfoItem item = new BoxInfoItem();
	    			 item.setGoodsName("银币");
					 item.setNum((short)showSilver);
					 push.getList().add(item);
	    		}
		 }
		 if(goodsBox.getBindingGoldMoney() > 0 ){
			 BoxInfoItem item = new BoxInfoItem();
			 item.setGoodsName(AttributeType.bindingGoldMoney.getName());
			 item.setNum((short)goodsBox.getBindingGoldMoney());
			 push.getList().add(item);
		 }
		 
	
		 if(0 < push.getList().size()){
			role.getBehavior().sendMessage(push);
		 }
	*/}

	
	private GoodsBox getGoodsBox(int goodsId){
		try{
			GoodsBox goodsBox = (GoodsBox)GameContext.getGoodsApp().getGoodsBase(goodsId);
			return goodsBox;
		}catch(Exception e){
			return null;
		}
	}
	
	private void broadcast(String roleName, List<GoodsOperateBean> list, int boxId){
		try{
			if(Util.isEmpty(list)){
				return;
			}
			for(GoodsOperateBean bean : list) {
				if(null == bean){
					continue;
				}
				int goodsId = bean.getGoodsId();
				GameContext.getBroadcastApp().broadCast(roleName, goodsId, String.valueOf(boxId), BroadcastType.box);
			}
		}catch(Exception e){
			logger.error("UseGoodsBox.broadcast error:",e);
		}
	}
}
