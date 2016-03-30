package com.game.draco.app.rune.config;

import java.util.Map;

import lombok.Data;
import sacred.alliance.magic.base.AttributeType;
import sacred.alliance.magic.util.KeySupport;
import sacred.alliance.magic.util.Log4jManager;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.util.Wildcard;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.app.chat.ChannelType;
import com.google.common.collect.Maps;

public @Data
class RuneAttributeConfig implements KeySupport<String> {
	
	private int level;
	private int attriNum;
	private int atkMin;
	private int atkMax;
	private int ritMin;
	private int ritMax;
	private int maxHPMin;
	private int maxHPMax;
	private int breakDefenseMin;
	private int breakDefenseMax;
	private int critAtkMin;
	private int critAtkMax;
	private int hitMin;
	private int hitMax;
	private int dodgeMin;
	private int dodgeMax;
	private int critRitMin;
	private int critRitMax;
	private String broadcastInfo;
	
	private Map<Byte,int[]> attriValueMap = Maps.newHashMap();
	
	@Override
	public String getKey() {
		return level + "_" + attriNum;
	}
	
	public Map<Byte,int[]> getAttriValue(){
		return attriValueMap;
	}

	public void init(String fileInfo) {
		String info = fileInfo + this.level + ":";
		if (this.level < 0) {
			this.checkFail(info + "level is config error!");
		}
		if (this.attriNum < 0) {
			this.checkFail(info + "attrinum is config error!");
		}
		if (this.atkMin < 0) {
			this.checkFail(info + "atkMin is config error!");
		}
		if (this.atkMax < 0) {
			this.checkFail(info + "atkMax is config error!");
		}
		if (this.ritMin < 0) {
			this.checkFail(info + "ritMin is config error!");
		}
		if (this.ritMax < 0) {
			this.checkFail(info + "ritMax is config error!");
		}
		if (this.maxHPMin < 0) {
			this.checkFail(info + "MaxHPMin is config error!");
		}
		if (this.maxHPMax < 0) {
			this.checkFail(info + "MaxHPMax is config error!");
		}
		if (this.breakDefenseMin < 0) {
			this.checkFail(info + "breakDefense is config error!");
		}
		if (this.breakDefenseMax < 0) {
			this.checkFail(info + "breakDefense is config error!");
		}
		if (this.critAtkMin < 0) {
			
			this.checkFail(info + "critMin is config error!");
		}
		if (this.critAtkMax < 0) {
			this.checkFail(info + "critMax is config error!");
		}
		if (this.hitMin < 0) {
			this.checkFail(info + "hitMin is config error!");
		}
		if (this.hitMax < 0) {
			this.checkFail(info + "hitMax is config error!");
		}
		if (this.dodgeMin < 0) {
			this.checkFail(info + "dodgeMin is config error!");
		}
		if (this.dodgeMax < 0) {
			this.checkFail(info + "dodgeMax is config error!");
		}
		if (this.critRitMin < 0) {
			this.checkFail(info + "critRitMin is config error!");
		}
		if (this.critRitMax < 0) {
			this.checkFail(info + "critRitMinMax is config error!");
		}
		
		attriValueMap.put(AttributeType.atk.getType(), new int[]{this.atkMin,this.atkMax});
		attriValueMap.put(AttributeType.critAtk.getType(), new int[]{this.critAtkMin,this.critAtkMax});
		attriValueMap.put( AttributeType.critRit.getType(),new int[]{this.critRitMin,this.critRitMax});
		attriValueMap.put(AttributeType.dodge.getType(), new int[]{this.dodgeMin,this.dodgeMax});
		attriValueMap.put(AttributeType.hit.getType(), new int[]{this.hitMin,this.hitMax});
		attriValueMap.put(AttributeType.maxHP.getType(), new int[]{this.maxHPMin,this.maxHPMax});
		attriValueMap.put(AttributeType.breakDefense.getType(), new int[]{this.breakDefenseMin,this.breakDefenseMax});
		attriValueMap.put(AttributeType.rit.getType(), new int[]{this.ritMin,this.ritMax});
	}
	
	public String getBroadCastTips(RoleInstance role) {
		if (Util.isEmpty(broadcastInfo)) {
			return "";
		}
		String roleName = Util.getColorRoleName(role, ChannelType.Publicize_Personal);
		String levelInfo = String.valueOf(this.level) + Util.getColor(ChannelType.Publicize_Personal.getColor());
		String attrType = String.valueOf(this.attriNum) + Util.getColor(ChannelType.Publicize_Personal.getColor());
		String message = this.broadcastInfo.replace(Wildcard.Role_Name, roleName).replace(Wildcard.Number, levelInfo).replace(Wildcard.AttrType, attrType);
		return message;
	}

	private void checkFail(String info) {
		Log4jManager.CHECK.error(info);
		Log4jManager.checkFail();
	}

}
