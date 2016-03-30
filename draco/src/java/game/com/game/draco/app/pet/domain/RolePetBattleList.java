package com.game.draco.app.pet.domain;

import java.util.Date;
import java.util.List;

import lombok.Data;

public @Data class RolePetBattleList {

	private String roleId;
	private List<String> battleRoleList;// 宠物掠夺对手列表
	private Date operateDate = new Date();

}
