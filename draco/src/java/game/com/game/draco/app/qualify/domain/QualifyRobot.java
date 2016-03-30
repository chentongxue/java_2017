package com.game.draco.app.qualify.domain;

import java.util.List;

import lombok.Data;

import com.google.common.collect.Lists;

public @Data class QualifyRobot {
	
	private String roleId;
	private String roleName;
	private int level;
	private int battleScore;
	
	// 不存储，方便程序取用
	List<Integer> heroIdList = Lists.newArrayList();
	
	public void addHero(int heroId) {
		this.heroIdList.add(heroId);
	}
	
	public int getHeroId1() {
		return this.heroIdList.get(0);
	}
	
	public void setHeroId1(int heroId1) {
		this.heroIdList.add(heroId1);
	}

	public int getHeroId2() {
		if (this.heroIdList.size() < 2) {
			return 0;
		}
		return this.heroIdList.get(1);
	}

	public void setHeroId2(int heroId2) {
		this.heroIdList.add(heroId2);
	}

	public int getHeroId3() {
		if (this.heroIdList.size() < 3) {
			return 0;
		}
		return this.heroIdList.get(2);
	}

	public void setHeroId3(int heroId3) {
		this.heroIdList.add(heroId3);
	}

}
