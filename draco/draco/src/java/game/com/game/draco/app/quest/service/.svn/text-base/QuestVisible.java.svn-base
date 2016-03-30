package com.game.draco.app.quest.service;

import java.util.HashSet;
import java.util.Set;

import lombok.Data;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sacred.alliance.magic.constant.Cat;
import sacred.alliance.magic.util.Util;

public @Data class QuestVisible {
	
	private int questId;//任务ID
	private String registerChannel;//注册渠道ID 用逗号分隔
	private String loginChannel;//登录渠道ID 用逗号分隔
	private String vipLevel;//VIP等级限制 用逗号分隔
	private Set<Integer> vipLevelSet = new HashSet<Integer>();
	private Set<Integer> regChannelSet = new HashSet<Integer>();
	private Set<Integer> loginChannelSet = new HashSet<Integer>();
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	/**
	 * 初始化数据
	 */
	public void init(){
		this.analysis(this.registerChannel, this.regChannelSet);
		this.analysis(this.loginChannel, this.loginChannelSet);
		this.analysis(this.vipLevel, this.vipLevelSet);
	}
	
	/**
	 * 解析限制参数
	 * @param parameter
	 * @param set
	 */
	private void analysis(String parameter, Set<Integer> set){
		try {
			if(Util.isEmpty(parameter)){
				return;
			}
			String[] params = parameter.split(Cat.comma);
			for(String item : params){
				if(Util.isEmpty(item)){
					continue;
				}
				set.add(Integer.valueOf(item));
			}
		} catch (Exception e) {
			this.logger.error("QuestVisible analysis error: ", e);
		}
	}
	
}
