package com.game.draco.app.pet.vo;

import java.util.List;

import com.game.draco.app.asyncpvp.domain.AsyncPvpRoleAttr;

import lombok.Data;
import sacred.alliance.magic.base.Result;

public @Data class PetPvpInfoListResult extends Result {
	private List<AsyncPvpRoleAttr> pvpRoleAttrList;
}
