package com.game.draco.app.camp.war.vo;

import java.util.concurrent.atomic.AtomicInteger;

import lombok.Data;
import lombok.Getter;
import sacred.alliance.magic.vo.AbstractRole;

public @Data class MatchInfo {
	private static AtomicInteger KEY_GEN = new AtomicInteger(0);
	@Getter private String key ;
	private long createTime = 0 ;
	//!!!很重要在创建Arena容器的时候需要将值赋进来
	private String containerId = "" ;
	
	private AbstractRole role1 ;
	private ApplyInfo apply1 ;
	private AbstractRole role2 ;
	private ApplyInfo apply2 ;
	
	
	
	private MatchInfo(){
		this.key = String.valueOf(KEY_GEN.incrementAndGet());
		this.createTime = System.currentTimeMillis();
	}
	
	public void destroy(){
		this.role1 = null ;
		this.role2 = null ;
		this.apply1.setMatch(null);
		this.apply2.setMatch(null);
		this.apply1 = null ;
		this.apply2 = null ;
	}
	
	public static MatchInfo create(AbstractRole role1,ApplyInfo apply1,
			AbstractRole role2,ApplyInfo apply2){
		MatchInfo match = new MatchInfo();
		match.setRole1(role1);
		match.setApply1(apply1);
		match.setRole2(role2);
		match.setApply2(apply2);
		//将appInfo设置为已经取消，免得重复匹配
		apply1.setCancel(true);
		apply2.setCancel(true);
		apply1.setMatch(match);
		apply2.setMatch(match);
		return match ;
	}
	
}
