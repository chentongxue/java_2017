package com.game.draco.app.buff;

import java.util.List;

import com.game.draco.app.buff.domain.BHurtC;


public  interface BuffDetail {
	
	short getBuffId() ;
	int getLevel() ;
	String getDesc() ;
	List<BHurtC> getBHurtList() ;
	
	void init() ;
	void check() ;
	
	Buff newBuff() ;
	
}
