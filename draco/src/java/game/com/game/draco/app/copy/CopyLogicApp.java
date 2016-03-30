package com.game.draco.app.copy;

import java.util.List;

import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.core.Service;
import sacred.alliance.magic.vo.MapInstance;
import sacred.alliance.magic.vo.Point;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.app.AppSupport;
import com.game.draco.app.copy.config.AttrConfig;
import com.game.draco.app.copy.config.CopyBuyConfig;
import com.game.draco.app.copy.config.CopyConfig;
import com.game.draco.app.copy.config.CopyMapConfig;
import com.game.draco.app.copy.config.CopyMapRoleRule;
import com.game.draco.app.copy.domain.RoleCopyCount;
import com.game.draco.app.copy.vo.CopyBuyNumResult;
import com.game.draco.app.copy.vo.CopyRaidsResult;
import com.game.draco.app.union.domain.Union;
import com.game.draco.message.item.AttriTypeValueItem;
import com.game.draco.message.item.GoodsLiteItem;
import com.game.draco.message.response.C0256_CopyPanelRespMessage;

public interface CopyLogicApp extends Service, AppSupport{
	
	/**
	 * 根据副本ID获取副本配置
	 * @param copyId
	 * @return
	 */
	public CopyConfig getCopyConfig(short copyId);
	
	/**
	 * 获取副本掉落信息
	 * @param copyId
	 * @return
	 */
	public List<Integer> getCopyFalls(short copyId);
	
	/**
	 * 获取副本掉落信息 属性
	 * @param copyId
	 * @return
	 */
	public List<AttrConfig> getCopyAttrList(short copyId);
	
	/**
	 * 进入副本
	 * @param role
	 * @param copyId
	 */
	public Result enterCopy(RoleInstance role, short copyId);
	
	/**
	 * 确定进入组队副本
	 * @param role 角色
	 * @param param 参数
	 */
	public void teamCopyCreateConfirm(RoleInstance role, String param);
	
	/**
	 * 会长是否开启公会副本
	 * @param union
	 * @return
	 */
	public boolean hadCreateUnionInstance(Union union);
	
	/**
	 * 获取进入失败传送点
	 * @return
	 */
	public Point getFailurePoint();
	
	/**
	 * 是否通关,能进入下一层
	 * @param instance
	 * @return
	 */
	public String isCopyPass(RoleInstance role);
	
	/**
	 * 是否通关,能进入下一层
	 * @param instance
	 * @return
	 */
	public boolean isCopyPass(MapInstance instance);
	
	/**
	 * 获取副本地图配置
	 * @param mapId
	 * @return
	 */
	public CopyMapConfig getMapConfig(String mapId);
	
	/**
	 * 获取副本面板信息
	 * @param role
	 * @param selectCopyId
	 * @return
	 */
	public C0256_CopyPanelRespMessage getCopyPanelRespMessage(RoleInstance role, short selectCopyId, byte copyType);
	
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
	public int getCopyMaxCount(RoleInstance role,short copyId);
	
	/**
	 * 购买副本次数
	 * @param copyId
	 * @return
	 */
	public CopyBuyNumResult copyBuyNum(RoleInstance role,short copyId,byte confirm);
	
	/**
	 * 获得购买数据
	 * @param key
	 * @return
	 */
	public CopyBuyConfig getCopyBuyConfig(String key);
	
	/**
	 * 副本重置前记录一键追回信息
	 * @param role
	 * @param copyCount
	 */
	public void onCopyCountDataReset(RoleInstance role,RoleCopyCount copyCount);
	
	/**
	 * 获取副本计数信息
	 * @param roleId
	 * @param copyId
	 * @return
	 */
	public RoleCopyCount getRoleCopyCount(String roleId, short copyId);
	
	/**
	 * 副本扫荡
	 * @param role
	 * @param copyId
	 * @return
	 */
	public CopyRaidsResult raidsCopy(RoleInstance role, short copyId);
	
	/**
	 * 获取当前参与英雄或组队副本总次数
	 * @param role
	 * @param type
	 * @return
	 */
	public int getCopyTypeCurCount(RoleInstance role, byte type);
	
	/**
	 * 副本次数是否足够
	 * @param role 角色
	 * @param copyConfig 副本配置
	 * @return
	 */
	public boolean isEnterCountEnough(RoleInstance role, CopyConfig copyConfig);
	
	/**
	 * 处理通关逻辑
	 * @param role
	 * @param copyConfig
	 */
	public void copyPass(RoleInstance role, CopyConfig copyConfig);
	
	/**
	 * 副本是否显示购买次数
	 * @param role
	 * @param copyId
	 * @return
	 */
	public byte copyShowBuyNumber(short copyId);
	
	/**
	 * 副本是否可扫荡
	 * @param role
	 * @param copyId
	 * @return
	 */
	public byte copyShowRaids(RoleInstance role, short copyId);
	
	/**
	 * PUSH副本结算面板
	 * @param role
	 * @param status
	 * @param pop
	 * @param type
	 * @param goodsLiteList
	 * @param attriTypeValueList
	 * @param info
	 */
	public void pushCopySettlementMessage(RoleInstance role, byte status, byte pop, byte type, List<GoodsLiteItem> goodsLiteList,
			List<AttriTypeValueItem> attriTypeValueList, String info);
	
}
