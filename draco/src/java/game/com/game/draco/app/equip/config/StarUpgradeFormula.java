package com.game.draco.app.equip.config;

import com.game.draco.app.chat.ChannelType;
import com.game.draco.base.CampType;

import lombok.Data;
import sacred.alliance.magic.base.QualityType;
import sacred.alliance.magic.util.KeySupport;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.util.Wildcard;
import sacred.alliance.magic.vo.RoleInstance;

public @Data class StarUpgradeFormula extends FormulaSupport implements KeySupport<String>{

	private int goodsId	;
	private byte quality;
	private byte star ;
	private String broadcastInfo;// 升星成功广播
	private int heroLevel ;
	
	private StarUpgradeFormula nextConf ;
	
	
	public static String genKey(int goodsId,int quality,int star){
		return goodsId + "_" + quality + "_" + star ;
	}
	
	@Override
	public String getKey() {
		return genKey(this.goodsId,this.quality,this.star);
	}
	
	/**
	 * 获取世界广播
	 * @param role
	 * @return
	 */
	public String getBroadcastTips(RoleInstance role){
		if(Util.isEmpty(this.broadcastInfo)){
			return "" ;
		}
		String defColor = ChannelType.Publicize_Personal.getColor();
		// 根据玩家阵营赋予玩家名称不同颜色
		String roleName = Util.getColorRoleName(role, ChannelType.Publicize_Personal);
		// 根据物品品质赋予物品名称不同颜色
		String goodsName = Wildcard.getChatGoodsName(goodsId, quality, ChannelType.Publicize_Personal);
		// 品质信息
		String qualityInfo = Util.getColorString(QualityType.get(quality).getName(), QualityType.get(quality).getColor(), defColor);
		// 星级信息
		String starInfo = String.valueOf(star) + Util.getColor(defColor);
		// 广播
		String message = this.broadcastInfo.replace(Wildcard.Role_Name, roleName).replace(Wildcard.GoodsName, goodsName).replace(Wildcard.Quality, qualityInfo).replace(
				Wildcard.Star, starInfo);
		return message;
	}

}
