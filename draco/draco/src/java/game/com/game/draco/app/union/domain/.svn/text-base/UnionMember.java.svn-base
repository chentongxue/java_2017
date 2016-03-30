package com.game.draco.app.union.domain;

import java.util.Date;

import lombok.Data;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sacred.alliance.magic.constant.Cat;
import sacred.alliance.magic.util.DateUtil;
import sacred.alliance.magic.util.Log4jManager;

import com.game.draco.app.union.type.UnionPositionType;

public @Data class UnionMember {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	//角色ID
	private int roleId;
	
	//公会ID
	private String unionId;
	
	//角色名称
	private String roleName;
	
	//等级
	private int level;

	//职业
	private byte occupation;
	
	//公会职务
	private byte position;
	
	//加入公会时间
	private long createTime;
	
	//最后一次下线时间
	private long offlineTime;
	
	//贡献度
	private int dkp;
	
	//帐户ID
	private String userId;
	
	public void offlineLog(){
		StringBuffer sb = new StringBuffer();
		sb.append(roleId);
		sb.append(Cat.pound);
		sb.append(unionId);
		sb.append(Cat.pound);
		sb.append(roleName);
		sb.append(Cat.pound);
		sb.append(position);
		sb.append(Cat.pound);
		sb.append(DateUtil.getTimeByDate(new Date(createTime)));
		sb.append(Cat.pound);
		sb.append(DateUtil.getTimeByDate(new Date(offlineTime)));
		sb.append(Cat.pound);
		sb.append(dkp);
		sb.append(Cat.pound);
		Log4jManager.UNION_LOG.info(sb.toString());
	}
	
	/**
	 * 根据职位获得职位的昵称
	 * @param type
	 * @param nickName
	 */
	public String getPositionNick(byte type){
		return UnionPositionType.getPosition(type).getName();
	}
	
	/**
	 * 获得帮众的职位
	 * @return
	 */
	public UnionPositionType getPositionType(){
		return UnionPositionType.getPosition(position);
	}
	
}
