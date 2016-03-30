package com.game.draco.app.copy.line;

import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.core.Service;
import sacred.alliance.magic.vo.RoleInstance;
import sacred.alliance.magic.vo.map.MapCopyLineContainer;

import com.game.draco.app.AppSupport;
import com.game.draco.app.copy.config.CopyMapRoleRule;
import com.game.draco.app.copy.line.config.CopyLineConfig;
import com.game.draco.message.response.C0270_CopyLinePanelRespMessage;

public interface CopyLineApp extends Service, AppSupport {
	
	/** 最小章节ID */
	public static final byte minChapterId = 1;
	/** 最小副本索引 */
	public static final byte minCopyIndex = 1;
	
	/**
	 * 获取章节副本的地图容器
	 * @return
	 */
	public MapCopyLineContainer getMapContainer();
	
	/**
	 * 打开章节副本面板
	 * @param role
	 * @param chapterId 章节ID
	 * @return
	 */
	public C0270_CopyLinePanelRespMessage getCopyLinePanelRespMessage(RoleInstance role, byte chapterId);
	
	/**
	 * 进入章节副本
	 * @param role
	 * @param copyId
	 */
	public void enterCopy(RoleInstance role, short copyId);
	
	/**
	 * 副本是否可进入
	 * @param role
	 * @param copyId
	 * @return
	 */
	public Result canEnter(RoleInstance role, short copyId);
	
	/**
	 * 领取星级奖励
	 * @param role
	 * @param chapterId
	 * @return
	 */
	public Result takeAward(RoleInstance role, byte chapterId);
	
	/**
	 * 获取章节副本配置
	 * @param copyId
	 * @return
	 */
	public CopyLineConfig getCopyLineConfig(short copyId);
	
	/**
	 * 获取适合角色的刷怪匹配关系
	 * @param role 角色
	 * @param mapId 地图ID
	 * @return
	 */
	public CopyMapRoleRule getCopyMapRoleRule(RoleInstance role, String mapId);
	
	/**
	 * 副本通关逻辑 更新评分
	 * @param role
	 * @param copyId 副本ID
	 * @param starScore 评分星级
	 */
	public void disposeCopyPass(RoleInstance role, short copyId, byte starScore);
	
}
