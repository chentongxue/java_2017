package com.game.draco.app.pet.vo;

import java.util.List;
import java.util.Map;

import lombok.Data;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.domain.RoleGoods;

import com.game.draco.app.pet.domain.RolePet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public @Data class PetSwallowResult extends Result {

	private List<Integer> swallowPetList = Lists.newArrayList();
	private List<RoleGoods> goodsList = Lists.newArrayList();
	private List<RoleGoods> danList = Lists.newArrayList();
	private Map<String, Integer> singleExpMap = Maps.newHashMap();
	private List<RolePet> petList = Lists.newArrayList();
	private RolePet rolePet;
	private byte status;
	private long swallowExp;
	
}
