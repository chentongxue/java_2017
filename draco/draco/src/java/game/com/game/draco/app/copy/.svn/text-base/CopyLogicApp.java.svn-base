package com.game.draco.app.copy;

import java.util.List;

import sacred.alliance.magic.core.Service;
import sacred.alliance.magic.vo.MapInstance;
import sacred.alliance.magic.vo.Point;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.app.union.domain.Union;
import com.game.draco.message.response.C0256_CopyPanelRespMessage;

public interface CopyLogicApp extends Service{
	
	/** 角色登录逻辑 **/
	public void login(RoleInstance role);
	
	/** 根据副本ID获取副本配置 **/
	public CopyConfig getCopyConfig(short copyId);
	
	/** 获取副本掉落信息 **/
	public List<Integer> getCopyFalls(short copyId);
	
	/**
	 * 进入副本
	 * @param role 角色
	 * @param copyId 副本ID
	 */
	public void enterCopy(RoleInstance role, short copyId);
	
	/**
	 * 确定进入组队副本
	 * （创建副本时有队员次数不足，询问队长是否确定进入）
	 * @param role 角色
	 * @param param 参数
	 */
	public void teamCopyCreateConfirm(RoleInstance role, String param);
	
	/** 会长是否开启公会副本 **/
	public boolean hadCreateUnionInstance(Union union);
	
	/**
	 * 获取进入失败传送点
	 * @return
	 */
	public Point getFailurePoint();
	
	/** 是否通关,能进入下一层 **/
	public String isCopyPass(RoleInstance role);
	public boolean isCopyPass(MapInstance instance);
	
	public CopyMapConfig getMapConfig(String mapId);
	
	/**
	 * 获取副本面板信息
	 * @param role
	 * @param selectCopyId
	 * @return
	 */
	public C0256_CopyPanelRespMessage getCopyPanelRespMessage(RoleInstance role, short selectCopyId);
	
	/**
	 * 获取适合角色的刷怪匹配关系
	 * @param role 角色
	 * @param mapId 地图ID
	 * @return
	 */
	public CopyMapRoleRule getCopyMapRoleRule(RoleInstance role, String mapId);
	
	/**
	 * 获取副本今日进入次数
	 * @param role
	 * @param copyId
	 * @return
	 */
	public int getCopyCurrCount(RoleInstance role, short copyId);
	
	/**
	 * 获取副本最大可进入次数
	 * @param copyId
	 * @return
	 */
	public int getCopyMaxCount(short copyId);
	
	/**
	 * 副本中掉线再次登录的处理
	 * @param role
	 */
	public void disposeCopyLostReLogin(RoleInstance role);
	
}
