package com.game.draco.app.goddess.domain;

import java.util.Date;

import lombok.Data;
import sacred.alliance.magic.base.RoleType;
import sacred.alliance.magic.component.id.IdFactory;
import sacred.alliance.magic.component.id.IdType;
import sacred.alliance.magic.domain.GoodsGoddess;
import sacred.alliance.magic.vo.AbstractRole;
import sacred.alliance.magic.vo.MapInstance;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;

public @Data class RoleGoddess extends AbstractRole{
	
	public RoleGoddess() {
		this.roleType = RoleType.GODDESS;
		try{
			this.setRoleId(IdFactory.getInstance().nextId(IdType.GODDESS));
		}catch(Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public final static String MASTER_ID = "masterId";
	private int goddessId; 
	private int level = 1;
	private int curExp;
	private String masterId ;
	/**
	 * 阶数
	 */
	private byte grade;
	/**
	 * 当前祝福值
	 */
	private short curBless;
	/**
	 * 上次进化时间
	 */
	private Date evolutionDate;
	/**
	 * 缠绵次数
	 */
	private short curLingerNum;
	
	/**
	 * 虚弱时间
	 */
	private short weakTime;
	
	/**
	 * 女神技能str串
	 * id:lv,id:lv
	 */
	private String skills = "" ;
	
	private byte onBattle;
	
	/**
	 * 是否在数据库中存在
	 */
	private boolean existDb  = false ;
	
	//变量
	private RoleInstance role;
	private long loginTime;

	/**
	 * 获得实例ID
	 * @param role
	 * @return
	 */
	public int getGoddessInstanceId(){
		return this.getIntRoleId();
	}
	
	@Override
	public int getBaseMaxMp() {
		return 0;
	}

	@Override
	public String getRoleName() {
		GoodsGoddess t = GameContext.getGoodsApp().getGoodsTemplate(GoodsGoddess.class, this.goddessId);
		if (null == t) {
			return "";
		}
		return t.getName();
	}

	@Override
	public boolean isSlow() {
		return false;
	}
	
	@Override
	public boolean isDeath(){
		return false ;
	}
	
	@Override
	public MapInstance getMapInstance() {
		return this.role.getMapInstance();
	}
	
	/**
	 * 返回剩余缠绵次数
	 */
	public short getRemainLingerNum() {
		return (short)(this.level - this.curLingerNum);
	}
}
