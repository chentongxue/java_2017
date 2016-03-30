package com.game.draco.app.union.domain.instance;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import lombok.Data;

import org.python.google.common.collect.Lists;

public @Data class UnionRoleDpsRecord implements java.io.Serializable{
	
	private static final long serialVersionUID = 1L;
	
	//公会ID
	private String unionId;
	
	//活动ID
	private byte activityId;
	
	//组Id
	private byte groupId;
	
	//角色数据
	private byte [] data;
	
	/**
	 * 读取某个BOSS中，所有角色DPS数据
	 * @param data
	 */
	public List<RoleDps> parseRoleDpsData() {
		List<RoleDps> roleDpsList = Lists.newArrayList();
		try {
			DataInputStream in = new DataInputStream(new ByteArrayInputStream(data));
			int roleDpsSizr = in.readInt();
			for(int k=0;k<roleDpsSizr;k++){
				RoleDps record = new RoleDps();
				record.setActivityId(in.readByte());
				record.setGroupId(in.readByte());
				record.setDps(in.readInt());
				record.setRoleId(in.readInt());
				record.setRoleName(in.readUTF());
				record.setUnionId(in.readUTF());
				roleDpsList.add(record);
			}
			in.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return roleDpsList;
	}

	/**
	 * 存储某个BOSS中，所有角色DPS数据
	 * @param roleDpsMap
	 * @return
	 */
	public void buildRoelDpsData(Map<Integer,RoleDps> roleDpsMap) {
		try {
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			DataOutputStream out = new DataOutputStream(bout);
			if(roleDpsMap != null && !roleDpsMap.isEmpty()){
				out.writeInt(roleDpsMap.size());
				for(Entry<Integer,RoleDps> roleDps : roleDpsMap.entrySet()){
					out.writeByte(roleDps.getValue().getActivityId());
					out.writeByte(roleDps.getValue().getGroupId());
					out.writeInt(roleDps.getValue().getDps());
					out.writeInt(roleDps.getValue().getRoleId());
					out.writeUTF(roleDps.getValue().getRoleName());
					out.writeUTF(roleDps.getValue().getUnionId());
				}
			}
			out.flush();
			out.close();
			data = bout.toByteArray();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
