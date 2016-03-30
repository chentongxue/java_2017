package com.game.draco.component.ssdb.pool;

import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.PooledObjectFactory;
import org.apache.commons.pool2.impl.DefaultPooledObject;

import com.game.draco.component.ssdb.SSDB;

/**
 * PoolableObjectFactory custom impl.
 */
public class SSDBFactory implements PooledObjectFactory<SSDB> {

	private final String host ;
	private final int port;
	private final int timeout;
	
	public SSDBFactory(final String host, final int port, final int timeout) {
		super();
		this.host = host;
		this.port = port;
		this.timeout = timeout;
	}
	    
	@Override
	public void activateObject(PooledObject<SSDB> poolSSDB) throws Exception {
		// TODO Auto-generated method stub
	}

	@Override
	public void destroyObject(PooledObject<SSDB> poolSSDB) throws Exception {
		SSDB ssdb = poolSSDB.getObject() ;
		if(null != ssdb){
			ssdb.close();
		}
	}

	@Override
	public PooledObject<SSDB> makeObject() throws Exception {
		SSDB ssdb = new SSDB(this.host,this.port,this.timeout);
		return new DefaultPooledObject<SSDB>(ssdb);
	}

	@Override
	public void passivateObject(PooledObject<SSDB> poolSSDB) throws Exception {
		// TODO Auto-generated method stub
	}

	@Override
	public boolean validateObject(PooledObject<SSDB> poolSSDB) {
		SSDB ssdb = poolSSDB.getObject() ;
		if(null == ssdb){
			return false ;
		}
		return ssdb.isAvailable() ;
	}
	
}