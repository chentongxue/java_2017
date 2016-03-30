package com.game.draco.app.operate.domain;

import com.baidu.bjf.remoting.protobuf.FieldType;
import com.baidu.bjf.remoting.protobuf.annotation.Protobuf;
import sacred.alliance.magic.util.Util;

import com.game.draco.GameContext;

import lombok.Data;

/**
 * 数据库中存储运营活动结构
 */
public @Data class RoleOperateActive {
	
	// 数据库主键
	public static final String ROLEID = "roleId";
	public static final String ACTIVEID = "activeId";
	
	// 数据库对应字段
    @Protobuf(fieldType = FieldType.STRING,order = 1)
	protected String roleId;
    @Protobuf(fieldType = FieldType.INT32,order = 2)
	protected int activeId;
    @Protobuf(fieldType = FieldType.INT32,order = 3)
	protected byte activeType;

	private byte[] data;
	
	// 入库操作标志字段
	protected boolean insertDB = false;
	protected boolean updateDB = false;
	
	/**
	 * 入库操作
	 */
	public void updateDB() {
		if (this.isInsertDB()) {
			GameContext.getBaseDAO().insert(this.getRoleOperateActive());
            this.setInsertDB(false);
			return;
		}
		if (this.isUpdateDB()) {
			GameContext.getBaseDAO().update(this.getRoleOperateActive());
            this.setUpdateDB(false);
		}
	}
	
	/**
	 * 获取入库对象
	 * @return
	 */
	private RoleOperateActive getRoleOperateActive() {
		RoleOperateActive active = new RoleOperateActive();
		active.setRoleId(this.getRoleId());
		active.setActiveId(this.getActiveId());
		active.setActiveType(this.getActiveType());
		active.setData(this.serialization());
		return active;
	}

    public <T extends RoleOperateActive> T createEntity(Class<T> clazz){
        if(null == this.data){
            return null ;
        }
        return Util.decode(this.data,clazz);
    }
	
	/**
	 * 序列化对象
	 * @return
	 */
	public byte[] serialization() {
        return Util.encode(this);
	}


	/**
	 * 删除数据库中该条记录
	 */
	public void deleteDB() {
		GameContext.getBaseDAO().delete(RoleOperateActive.class, ROLEID, this.roleId, ACTIVEID, this.activeId);
	}
	
}
