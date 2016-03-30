package com.game.draco.app.levelgift;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import lombok.Data;
import sacred.alliance.magic.app.goods.GoodsOperateBean;
import sacred.alliance.magic.app.goods.behavior.result.GoodsResult;
import sacred.alliance.magic.base.OutputConsumeType;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.base.XlsSheetNameType;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.domain.GoodsBase;
import sacred.alliance.magic.domain.RoleCount;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.util.XlsPojoUtil;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.levelgift.config.LevelGiftConfig;
import com.game.draco.message.item.GoodsLiteItem;
import com.game.draco.message.item.LevelGiftItem;
import com.game.draco.message.response.C2402_LevelGiftListRespMessage;
import com.google.common.collect.Maps;

/**
  冲级奖励活动
 */
public @Data class LevelGiftAppImpl implements LevelGiftApp{
	private String CAT = "," ;
	private Map<String,LevelGiftConfig> configMap = Maps.newLinkedHashMap();
	
	@Override
	public void start() {
		this.loadLevelingConfig();
	}
	
	@Override
	public void setArgs(Object arg0) {
	}
	
	@Override
	public void stop() {
		
	}
	
	private LevelGiftConfig getLevelGiftConfig(int level){
		if(null == this.configMap){
			return null ;
		}
		return this.configMap.get(String.valueOf(level));
	}
	
	/**
	 * 加载冲级活动的配置
	 */
	private void loadLevelingConfig(){
		String fileName = XlsSheetNameType.level_gift.getXlsName();
		String sheetName = XlsSheetNameType.level_gift.getSheetName();
		String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
		Map<String,LevelGiftConfig> map = Maps.newLinkedHashMap();
		List<LevelGiftConfig> list = XlsPojoUtil.sheetToList(sourceFile, sheetName, LevelGiftConfig.class);
		if(Util.isEmpty(list)){
			this.configMap = map ;
			return ;
		}
		this.sortByLevel(list);
		for(LevelGiftConfig leveling : list){
			// 初始化冲级奖励
			leveling.init();
			map.put(String.valueOf(leveling.getLevel()), leveling);
		}
		this.configMap = map ;
	}
	
	/**
	 * 按等级升序
	 */
	private void sortByLevel(List<LevelGiftConfig> list){
		Collections.sort(list, new Comparator<LevelGiftConfig>(){
			@Override
			public int compare(LevelGiftConfig leveling1, LevelGiftConfig leveling2) {
				int level1 = leveling1.getLevel();
				int level2 = leveling2.getLevel();
				if(level1 < level2){
					return -1;
				}
				if(level1 > level2){
					return 1;
				}
				return 0;
			}
		});
	}
	

	private String getText(String textId){
		return GameContext.getI18n().getText(textId);
	}
	
	/** 设置已领取的等级奖励 */
	private void setReceiveRewards(RoleCount count, int level){
		if(level <= 0){
			return ;
		}
		if(count == null){
			return ;
		}
		String str = count.getLevelGift() ;
		if(Util.isEmpty(str)){
			str = String.valueOf(level);
		}else{
			str = str + CAT + String.valueOf(level);
		}
		count.setLevelGift(str);
		//实时入库
		GameContext.getCountApp().saveRoleCount(count);
	}
		
	/** 判断该等级是否已领取奖励 */
	private boolean hadReceiveRewards(RoleCount count, int level){
		if(count == null || level <= 0){
			return false;
		}
		String str = count.getLevelGift() ;
		if(Util.isEmpty(str)){
			return false ;
		}
		return (CAT + str + CAT).indexOf(CAT + level + CAT) >=0 ;
	}
	
	
	/** 是否存在可领取奖励 */
	private boolean existCanReward(RoleInstance role) {
		if(Util.isEmpty(configMap)){
			return false;
		}
		RoleCount count = role.getRoleCount() ;
		for(LevelGiftConfig leveling : configMap.values()){
			if(this.getRewardStatus(role, count, leveling) == ReceiveState.can_receive){
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 获取领取状态
	 * @param role
	 * @param logInfo
	 * @param leveling
	 * @return
	 */
	private ReceiveState getRewardStatus(RoleInstance role, RoleCount count, 
			LevelGiftConfig leveling){
		int level = leveling.getLevel();
		if(this.hadReceiveRewards(count, level)){
			return ReceiveState.already_receive;
		}
		if(role.getLevel() >= level){
			return ReceiveState.can_receive;
		}
		return ReceiveState.canot_receive;
	}
	


	@Override
	public Message getLevelGiftListMessage(RoleInstance role) {
		RoleCount count = role.getRoleCount() ;
		C2402_LevelGiftListRespMessage resp = new C2402_LevelGiftListRespMessage();
		List<LevelGiftItem> items = new ArrayList<LevelGiftItem>();
		for(LevelGiftConfig leveling : configMap.values()){
			LevelGiftItem item = new LevelGiftItem();
			item.setState(this.getRewardStatus(role, count, leveling).getType());
			item.setLevel((byte)leveling.getLevel());
			List<GoodsLiteItem> goodsItems = new ArrayList<GoodsLiteItem>();
			for(GoodsOperateBean bean : leveling.getGoodsRewards()){
				if(null == bean){
					continue;
				}
				int goodsId = bean.getGoodsId();
				GoodsBase goodsBase = GameContext.getGoodsApp().getGoodsBase(goodsId);
				if(null == goodsBase){
					continue ;
				}
				GoodsLiteItem goodsItem = goodsBase.getGoodsLiteItem() ;
				goodsItem.setBindType(bean.getBindType().getType());
				//设置数量
				goodsItem.setNum((short)bean.getGoodsNum());
				goodsItems.add(goodsItem);
			}
			item.setGoodsItems(goodsItems);
			items.add(item);
		}
		resp.setItems(items);
		return resp;
	}

	@Override
	public Result takeReward(RoleInstance role,int level) {
		Result result = new Result();
		LevelGiftConfig leveling = this.getLevelGiftConfig(level);
		if(null == leveling){
			return result.setInfo(this.getText(TextId.Active_Leveling_Not_Open));
		}
		// 判断等级是否满足
		int roleLevel = role.getLevel();
		if(roleLevel < level){
			return result.setInfo(this.getText(TextId.Active_Leveling_Not_Open));
		}
		
		RoleCount count = role.getRoleCount() ;
		boolean hadReceive = this.hadReceiveRewards(count, level);
		if(hadReceive){
			return result.setInfo(this.getText(TextId.Active_Leveling_HadReceive));
		}
		
		List<GoodsOperateBean> goodsRewards = leveling.getGoodsRewards();
		GoodsResult goodsResult = GameContext.getUserGoodsApp().addGoodsBeanForBag(role, goodsRewards, 
				OutputConsumeType.level_gift_award);
		if(!goodsResult.isSuccess()){
			return goodsResult;
		}
		// 设置领奖记录
		this.setReceiveRewards(count, level);
		return result.success();
	}
	
}
