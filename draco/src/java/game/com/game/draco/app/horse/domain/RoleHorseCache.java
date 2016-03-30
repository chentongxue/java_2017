package com.game.draco.app.horse.domain;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

import com.game.draco.message.item.AttriTypeStrValueItem;

public @Data class RoleHorseCache {
	
	//角色ID
	private int roleId;
	
	//坐骑名字
	private String name;
	
	//坐骑资源id
	private int resId;
	
	//品质
	private byte quality; 
	
	//战斗力
	private int battleScore;
	
	//头像
	private short iconId;
	
//	private List<HorseSkillItem> horseSkillItemList = Lists.newArrayList();
	//属性数据
	private List<AttriTypeStrValueItem> horsePropItem = new ArrayList<AttriTypeStrValueItem>();
	
}
