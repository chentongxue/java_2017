package com.game.draco.app.goblin;

import java.util.Date;
import java.util.List;

import sacred.alliance.magic.core.Service;
import sacred.alliance.magic.vo.MapGoblinContainer;
import sacred.alliance.magic.vo.MapInstance;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.app.goblin.config.GoblinBaseConfig;
import com.game.draco.app.goblin.config.GoblinLocationConfig;
import com.game.draco.app.goblin.config.GoblinSecretConfig;
import com.game.draco.app.goblin.vo.GoblinJumpPointInfo;
import com.game.draco.app.goblin.vo.GoblinSecretBossTemplate;
import com.game.draco.app.npc.domain.NpcInstance;
import com.game.draco.message.item.GoodsLiteItem;

public interface GoblinApp extends Service {
	
	/**
	 * 活动是否开启
	 * @return
	 */
	public boolean isOpen();
	
	/**
	 * 是否在活动时间内
	 * @return
	 */
	public boolean isOnGoblinActive();
	
	/**
	 * 获得世界BOSS基础配置
	 * @return
	 */
	public GoblinBaseConfig getGoblinBaseConfig();
	
	/**
	 * 获得面板上展示的物品列表
	 * @return
	 */
	public List<GoodsLiteItem> getPanelShowGoodsList();
	
	/**
	 * 活动开始，在配置的地图中刷新哥布林
	 */
	public void refreshGoblin();
	
	/**
	 * 哥布林死亡
	 * @param mapId
	 */
	public void goblinDeath(NpcInstance goblin);
	
	/**
	 * 在某个地图上刷新哥布林
	 */
	public void refreshSignGoblinOrJumpPoint(MapInstance mapInstance);
	
	/**
	 * 获得哥布林密境配置
	 * @param date
	 * @return
	 */
	public GoblinSecretConfig getGoblinSecretConfig(Date date);
	
	/**
	 * 获得密境中哥布林刷新位置配置
	 * @return
	 */
	public GoblinLocationConfig getGoblinLocationConfig(String bossId);
	
	/**
	 * 哥布林活动结束
	 */
	public void goblinActiveEnd();
	
	/**
	 * 获得哥布林地图容器
	 * @return
	 */
	public MapGoblinContainer getMapGoblinContainer();
	
	/**
	 * 设置玩家的跳转点
	 * @param roleId
	 * @param pointKey
	 */
	public void setRoleSecretPointKey(String roleId, String pointKey);
	
	/**
	 * 获得玩家的跳转点
	 * @param roleId
	 * @return
	 */
	public String getRoleSecretPointKey(String roleId);
	
	/**
	 * 保存密境BOSS信息
	 * @param npcTemplate
	 */
	public void setGoblinTemplate(String key, GoblinSecretBossTemplate npcTemplate);
	
	/**
	 * 获得密境BOSS信息
	 * @return
	 */
	public GoblinSecretBossTemplate getGoblinTemplate(String key);
	
	/**
	 * 删除单个跳转点
	 * @param point
	 */
	public void removeSignJumpPoint(String mapInstanceId);
	
	/**
	 * 获得密境对应的跳转点
	 * @param mapId
	 * @return
	 */
	public GoblinJumpPointInfo getGoblinJumpPointInfo(String mapId);
	
	/**
	 * 发放击杀密境中BOSS奖励
	 * @param role
	 * @param goblin
	 */
	public void giveSecretBossReward(RoleInstance role, NpcInstance goblin);

}
