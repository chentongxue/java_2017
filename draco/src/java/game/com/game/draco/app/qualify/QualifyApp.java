package com.game.draco.app.qualify;

import java.util.List;
import java.util.Map;

import sacred.alliance.magic.app.attri.AttriBuffer;
import sacred.alliance.magic.base.ChallengeResultType;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.core.Service;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.app.AppSupport;
import com.game.draco.app.asyncpvp.domain.AsyncPvpRoleAttr;
import com.game.draco.app.asyncpvp.vo.AsyncPvpBattleInfo;
import com.game.draco.app.hero.domain.RoleHero;
import com.game.draco.app.qualify.config.QualifyBaseConfig;
import com.game.draco.app.qualify.config.QualifyGiftConfig;
import com.game.draco.app.qualify.domain.ChallengeRecord;
import com.game.draco.app.qualify.domain.QualifyRank;
import com.game.draco.app.qualify.domain.QualifyRobot;
import com.game.draco.message.item.HeroBattleItem;

public interface QualifyApp extends Service, AppSupport {

	/**
	 * 获得角色的排名
	 * @param role
	 * @return rank
	 */
	public short getRoleRank(RoleInstance role);
	
	/**
	 * 获得角色的排名
	 * @param roleId
	 * @return rank
	 */
	public short getRoleRank(String roleId);
	
	/**
	 * 获得挑战对手列表
	 * @param role
	 * @return List<RoleQualify>
	 */
	public List<QualifyRank> getChallengeOpponents(RoleInstance role);
	
	/**
	 * 获得下次发奖信息
	 * @return String
	 */
	public String getNextGiveGiftStrInfo();
	
	/**
	 * 获得下次免费挑战的CD时间
	 * @param role
	 * @return
	 */
	public int getChallengeCDTime(RoleInstance role);
	
	/**
	 * 剩余挑战次数
	 * @param role
	 * @return
	 */
	public byte getRemainChallengeTimes(RoleInstance role);
	
	/**
	 * 每天可以挑战多少次
	 * @return
	 */
	public byte getMaxChallengeTimes(RoleInstance role);
	
	/**
	 * 获得荣誉商店的ID
	 * @return
	 */
	public String getShopId();
	
	/**
	 * 获得角色排行信息
	 * @param role
	 * @return
	 */
	public QualifyRank getQualifyRank(RoleInstance role);
	
	/**
	 * 获得角色排行信息
	 * @param role
	 * @return
	 */
	public QualifyRank getQualifyRank(String roleId);
	
	/**
	 * 获得排位奖励配置
	 * @param rank
	 * @param level
	 * @return QualifyGiftConfig
	 */
	public QualifyGiftConfig getRankQualifyGiftConfig(int rank, int level);
	
	/**
	 * 购买挑战次数
	 * @param role
	 * @return
	 */
	public Result buyChallengeTimes(RoleInstance role, String isConfirm);
	
	/**
	 * 获得挑战CD时间
	 * @param role
	 * @return
	 */
	public String getChallengeCDStrTime(RoleInstance role);
	
	/**
	 * 挑战PVP
	 * @param role
	 * @return
	 */
	public Result qualifyChallenge(RoleInstance role, String targetRoleId, boolean isConfirm);
	
	/**
	 * 处理挑战结果
	 * @param role
	 * @param battleInfo
	 * @param type
	 */
	public void challengeOver(RoleInstance role, AsyncPvpBattleInfo battleInfo, ChallengeResultType type, boolean sendMessage);
	
	/**
	 * 获得排行榜页数
	 * @return
	 */
	public byte getMaxRankPage();
	
	/**
	 * 根据页数获得排行榜信息
	 * @param page
	 * @return
	 */
	public List<QualifyRank> getRoleQualifyList(short page);
	
	/**
	 * 将排行榜信息保存到数据库
	 */
	public void saveQualifyRankInfo();
	
	/**
	 * 获得对战记录
	 * @param role
	 * @return
	 */
	public List<ChallengeRecord> getChallengeRecordList(RoleInstance role);
	
	/**
	 * 获得排位赛的规则说明
	 * @return
	 */
	public String getQualifyRankDesc();
	
	/**
	 * 封装英雄列表
	 * @param roleHeroList
	 * @return
	 */
	public List<HeroBattleItem> getQualifyHeroList(QualifyRank qualifyRank);
	
	/**
	 * 封装出战英雄
	 * @param roleHeroList
	 * @return
	 */
	public List<HeroBattleItem> getQualifyBattleHero(QualifyRank qualifyRank);
	
	/**
	 * 获得机器人信息
	 * @param roleId
	 * @return
	 */
	public QualifyRobot getQualifyRobot(String roleId);
	
	/**
	 * 获得挑战对手的ID
	 * @param roleId
	 * @return
	 */
	public String getChallengeOpponentId(String roleId);
	
	/**
	 * 获得机器人的英雄列表
	 * @param roleId
	 * @return
	 */
	public List<RoleHero> getRobotHeroList(String roleId);
	
	/**
	 * 获得机器人出战英雄属性
	 * @param roleId
	 * @param heroId
	 * @return
	 */
	public AttriBuffer getRobotHeroBuffer(String roleId, int heroId);
	
	/**
	 * 获得机器人的人物属性
	 * @param roleId
	 * @return
	 */
	public AsyncPvpRoleAttr getRobotAsyncPvpRoleAttr(String roleId);
	
	/**
	 * 获得排位赛基本配置
	 * @return
	 */
	public QualifyBaseConfig getQualifyBaseConfig();
	
	/**
	 * 获得排位赛的所有列表
	 * @return
	 */
	public Map<Short, QualifyRank> getQualifyRankMap();
	
	/**
	 * 发放排行奖励
	 * @param roleQualify
	 */
	public void giveQualifyRankGift(QualifyRank roleQualify);
	
}
