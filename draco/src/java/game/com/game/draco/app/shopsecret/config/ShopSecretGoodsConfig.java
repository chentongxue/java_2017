package com.game.draco.app.shopsecret.config;

import com.game.draco.app.chat.ChannelType;

import sacred.alliance.magic.util.KeySupport;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.util.Wildcard;
import sacred.alliance.magic.vo.RoleInstance;
import lombok.Data;

@Data
public class ShopSecretGoodsConfig  implements KeySupport<String>{
	private int shopItemId;
	private String poolId;
	private short minRoleLevel;
	private short maxRoleLevel;
	private int goodsId;
	private byte bind;
	private int num;
	private byte payType;
	private int price;
	private int weight;
	private String broadcastInfo;
	
	@Override
	public String getKey() {
		return String.valueOf(shopItemId);
	}
	
	public boolean fitRole(RoleInstance role) {
		if(role != null && role.getLevel()>= minRoleLevel && role.getLevel() <= maxRoleLevel){
			return true;
		}
		return false;
	}
	
	/**
	 * 世界广播
	 * @param role
	 * @return
	 */
	public String getBroadCastTips(RoleInstance role) {
		if (Util.isEmpty(broadcastInfo)) {
			return "";
		}
		String roleName = Util.getColorRoleName(role, ChannelType.Publicize_Personal);
		String goodsName = Wildcard.getChatGoodsName(goodsId, ChannelType.Publicize_Personal);
		String message = broadcastInfo.replace(Wildcard.Role_Name, roleName).replace(Wildcard.GoodsName, goodsName);
		return message;
	}
	
}
