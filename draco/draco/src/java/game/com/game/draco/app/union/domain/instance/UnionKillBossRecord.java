package com.game.draco.app.union.domain.instance;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.Set;

import lombok.Data;

import org.python.google.common.collect.Sets;

public @Data class UnionKillBossRecord {
	
	//公会ID
	private String unionId;
	
	//数据
	private byte [] data;
	
	/**
	 * 读取击杀BOSS数据
	 * @return
	 */
	public Set<String> parseKillBossData() {
		Set<String> setKillBoss = Sets.newHashSet();
		try {
			DataInputStream in = new DataInputStream(new ByteArrayInputStream(data));
			int roleDpsSizr = in.readInt();
			for(int k=0;k<roleDpsSizr;k++){
//				UnionBoss record = new UnionBoss();
//				record.setBossId(in.readUTF());
//				record.setBossName(in.readUTF());
//				record.setCreateTime(in.readLong());
//				record.setRoleId(in.readInt());
//				record.setRoleName(in.readUTF());
				setKillBoss.add(in.readUTF());
			}
			in.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return setKillBoss;
	}

	/**
	 * 存储击杀BOSS数据
	 * @param bossList
	 * @return
	 */
	public void buildKillBossData(Set<String> setKillBoss) {
		try {
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			DataOutputStream out = new DataOutputStream(bout);
			if(setKillBoss != null && !setKillBoss.isEmpty()){
				out.writeInt(setKillBoss.size());
				for(String bossId : setKillBoss){
//					out.writeUTF(boss.getBossId());
//					out.writeUTF(boss.getBossName());
//					out.writeLong(boss.getCreateTime());
//					out.writeInt(boss.getRoleId());
//					out.writeUTF(boss.getRoleName());
					out.writeUTF(bossId);
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
