package com.game.draco.app.union.domain.instance;

import java.util.Set;

import lombok.Data;
import sacred.alliance.magic.util.Util;

import com.baidu.bjf.remoting.protobuf.FieldType;
import com.baidu.bjf.remoting.protobuf.annotation.Protobuf;

public @Data class UnionKillBossRecord {
	
	//公会ID
	private String unionId;
	
	//数据
	private byte [] data;

    @Protobuf(fieldType = FieldType.STRING,order = 1)
    private Set<String> killBoss ;
	
	/**
	 * 读取击杀BOSS数据
	 * @return
	 */
	public void postFormDatabase() {
        UnionKillBossRecord init = Util.decode(this.data,UnionKillBossRecord.class) ;
        if(null == init){
            return ;
        }
        this.data = init.getData() ;
	}

	/**
	 * 存储击杀BOSS数据
	 * @return
	 */
	public void preToDatabase() {
		this.data = Util.encode(this) ;
	}
}
