package com.game.draco.component.ssdb;

import com.game.draco.component.ssdb.pool.SSDBPool;

public class SSDBUtil {

	private SSDBPool pool  ;
	
	public SSDBPool getPool() {
		return pool;
	}

	public void setPool(SSDBPool pool) {
		this.pool = pool;
	}

	public SSDB getSSDB(){
		return pool.getResource() ;
	}
	
	public void returnSSDB(SSDB ssdb){
		if(null == ssdb){
			return ;
		}
		pool.returnResource(ssdb);
	}
}
