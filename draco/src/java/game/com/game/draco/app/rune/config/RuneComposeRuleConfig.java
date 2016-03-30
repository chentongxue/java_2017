package com.game.draco.app.rune.config;

import lombok.Data;
import sacred.alliance.magic.domain.GoodsBase;
import sacred.alliance.magic.util.KeySupport;
import sacred.alliance.magic.util.Log4jManager;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.util.Wildcard;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.chat.ChannelType;

/**
 * 
 * @author mofun60901
 * 物品合成规则
 */
public @Data class RuneComposeRuleConfig implements KeySupport<Integer>{
	private int srcId;//原料Id
	private int srcNum;//原料数目
	private int targetId;//合成符文的Id
	private int fee;//消耗游戏币
	private String broadcast ;
	//下面非xls配置字段
	private GoodsBase targetGoods ;

	@Override
	public Integer getKey() {
		return this.srcId;
	}
	
	public void init(String fileInfo){
		String info = fileInfo;
		if(this.srcId<0){
			this.checkFail(info + " srcId is config error!");
		}
		if(this.srcNum<0){
			this.checkFail(info + " srcNum is config error!");
		}
		if(this.targetId<0){
			this.checkFail(info + " targetId is config error!");
		}
		if(this.fee<0){
			this.checkFail(info + " fee is config error!");
		}
		this.checkGoods(info, this.srcId,false);
		this.checkGoods(info, this.targetId,true);
	}
	
	private void checkGoods(String info,int goodsId,boolean target){
		GoodsBase goodsBase = GameContext.getGoodsApp().getGoodsBase(goodsId);
		if(null != goodsBase){
			if(target){
				this.targetGoods = goodsBase ;
			}
			return ;
		}
		this.checkFail(info+" the goods template isn't exist,goodsId=" + goodsId);
	}
	
	
	private void checkFail(String info) {
		Log4jManager.CHECK.error(info);
		Log4jManager.checkFail();
	}
	
	public String getBroadCastTips(RoleInstance role) {
		if (Util.isEmpty(this.broadcast)) {
			return "";
		}
		String levelInfo = String.valueOf(this.targetGoods.getLevel()) + Util.getColor(ChannelType.Publicize_Personal.getColor());
		String message = this.broadcast.replace(Wildcard.Role_Name, Util.getColorRoleName(role, ChannelType.Publicize_Personal)).replace(Wildcard.Number,
				levelInfo).replace(Wildcard.GoodsName, Wildcard.getChatGoodsName(this.targetId, ChannelType.Publicize_Personal));
		return message;
	}
}
