package com.game.draco.component.ssdb.pool;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

import com.game.draco.component.ssdb.SSDB;

public class SSDBPool extends Pool<SSDB> {

	public SSDBPool(final GenericObjectPoolConfig poolConfig, String host,
			int port, int timeout) {
		super(poolConfig, new SSDBFactory(host, port, timeout));
	}
	
	public SSDBPool(String host,int port, int timeout) {
		super(new GenericObjectPoolConfig(), new SSDBFactory(host, port, timeout));
	}
}
