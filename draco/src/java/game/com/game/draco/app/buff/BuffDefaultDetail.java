package com.game.draco.app.buff;

import java.util.List;

import lombok.Data;

import org.python.google.common.collect.Lists;

import com.game.draco.app.buff.domain.BHurtC;

public @Data class BuffDefaultDetail implements BuffDetail{

	private short buffId ;
	private int level ;
	private String desc ;
	private List<BHurtC> bHurtList = Lists.newArrayList();
	@Override
	public void init() {
		
	}
	@Override
	public void check() {
		
	}
	@Override
	public Buff newBuff() {
		return null;
	}
	
}
