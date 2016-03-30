package com.game.draco.app.pet.config;

import lombok.Data;
import sacred.alliance.magic.util.KeySupport;
import sacred.alliance.magic.util.Log4jManager;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.util.Wildcard;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.app.chat.ChannelType;

public @Data class PetStarUpConfig implements KeySupport<String> {
	
	private byte quality;// 宠物品质
	private byte star;// 当前星级
	private String broadcastInfo;// 升星广播
	private int shadowNumber;// 升到下一星级所需的碎片数
	private byte skillLevel;// 技能等级

	@Override
	public String getKey() {
		return quality + "_" + star;
	}
	
	public void init(String fileInfo) {
		String info = fileInfo + this.star + ":";
		if (this.star < 0) {
			this.checkFail(info + "star is config error!");
		}
		if (this.quality < 0) {
			this.checkFail(info + "quality is config error!");
		}
		if (this.shadowNumber < 0) {
			this.checkFail(info + "shadowNumber is config error!");
		}
		if (this.skillLevel <= 0) {
			this.checkFail(info + "skillLevel is config error!");
		}
	}
	
	private void checkFail(String info) {
		Log4jManager.CHECK.error(info);
		Log4jManager.checkFail();
	}
	
	/**
	 * 获取世界广播
	 * @param role
	 * @param petId
	 * @return
	 */
	public String getBroadcastTips(RoleInstance role, int petId){
		if(Util.isEmpty(this.broadcastInfo)){
			return "" ;
		}
		String defColor = ChannelType.Publicize_Personal.getColor();
		// 根据玩家阵营赋予玩家名称不同颜色
		String roleName = Util.getColorRoleName(role, ChannelType.Publicize_Personal);
		// 根据物品品质赋予物品名称不同颜色
		String goodsName = Wildcard.getChatGoodsName(petId, ChannelType.Publicize_Personal);
		// 星级信息
		String starInfo = String.valueOf(star) + Util.getColor(defColor);
		// 广播信息
		String message = this.broadcastInfo.replace(Wildcard.Role_Name, roleName).replace(Wildcard.GoodsName, goodsName).replace(Wildcard.Star, starInfo);
		return message;
	}
	
}
