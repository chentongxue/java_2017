package com.game.draco.app.union;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import sacred.alliance.magic.base.OperatorType;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.core.exception.ServiceException;
import sacred.alliance.magic.util.ListPageDisplay;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.app.AppSupport;
import com.game.draco.app.union.domain.Union;
import com.game.draco.app.union.domain.UnionMember;
import com.game.draco.app.union.domain.UnionRecord;
import com.game.draco.app.union.type.UnionPositionType;
import com.game.draco.app.union.type.UnionPowerType;
import com.game.draco.app.union.vo.ChangeActivityResult;
import com.game.draco.message.request.C2766_UnionSummonReqMessage;
import com.game.draco.message.response.C1727_UnionDonateInfoRespMessage;
import com.game.draco.message.response.C1728_UnionDonateRespMessage;
import com.game.draco.message.response.C2753_UnionInfoRespMessage;
import com.game.draco.message.response.C2754_UnionActivityListRespMessage;
import com.game.draco.message.response.C2755_UnionBossListRespMessage;
import com.game.draco.message.response.C2757_UnionRoleDpsListRespMessage;

public interface UnionApp extends AppSupport{
	
	public static final short UnionSummonPanelReqCmdId = new C2766_UnionSummonReqMessage().getCommandId();
	
	/**
	 * 初始化公会数据
	 */
	void initUnion();
	
	/**
	 * 根据公会ID查找公会
	 * @param UnionId
	 * @return
	 */
	Union getUnion(String unionId);
	
	/**
	 * 根据角色对象查找公会
	 * @param role
	 * @return
	 */
	Union getUnion(RoleInstance role);
	
	/**
	 * 验证是否满足公会创建条件
	 * @param role
	 * @return
	 */
	Result checkCondition(RoleInstance role);
	
	/**
	 * 创建公会
	 * @param role
	 * @param UnionName
	 * @return
	 */
	Result createUnion(RoleInstance role,String unionName, String unionDesc) throws ServiceException;
	
	/**
	 * 获取公会列表
	 * @param currPage 当前页码
	 * @param size 每页数目
	 * @return
	 */
	ListPageDisplay<Union> getUnionList(int currPage, int size);
	
	/**
	 * 申请加入公会
	 * @param role
	 * @param UnionId
	 * @return
	 */
	Result applyJoinUnion(RoleInstance role,String unionId);
	
	/**
	 * 获取申请加入公会的请求列表
	 * @param UnionId
	 * @param currPage
	 * @param size
	 * @return
	 */
	List<UnionMember> getApplyJoinList(String unionId);
	
	/**
	 * 接受加入公会请求
	 * @param leader
	 * @param UnionId
	 * @param roleId
	 * @return
	 */
	Result acceptApplyJoin(RoleInstance leader, int roleId) throws ServiceException;
	
	/**
	 * 拒绝加入公会请求
	 * @param leader
	 * @param UnionId
	 * @param roleId
	 * @return
	 */
	Result refuseApplyJoin(RoleInstance leader, int roleId);
	
	/**
	 * 获取公会的所有帮众（未排序）
	 * @param UnionId
	 * @return
	 */
	Map<Integer,UnionMember> getUnionMemberMap(String unionId);
	
	/**
	 * 获取公会的全部帮众列表（排序）
	 * @param UnionId
	 * @return
	 */
	List<UnionMember> getUnionMemberList(String unionId);
	
	/**
	 * 获取在线的公会成员
	 * @param UnionInstance
	 * @return
	 */
	Collection<RoleInstance> getAllOnlineUnionMember(Union union);
	
	/**
	 * 退出公会
	 * @param role
	 * @return
	 */
	Result exitUnion(RoleInstance role) throws ServiceException;
	
	/**
	 * 踢出公会成员
	 * @param leader
	 * @param roleId
	 * @return
	 */
	Result removeUnionMember(RoleInstance leader, int roleId) throws ServiceException;
	
	/**
	 * 升级公会成员职位
	 * @param leader
	 * @param roleId
	 * @return
	 * @throws ServiceException
	 */
	Result levelUpUnionMember(RoleInstance leader, int roleId) throws ServiceException;
	
	/**
	 * 降级公会职位
	 * @param leader
	 * @param roleId
	 * @return
	 * @throws ServiceException
	 */
	Result demotionUnionMember(RoleInstance leader, int roleId) throws ServiceException;
	
	
	/**
	 * 查找公会成员
	 * 角色登录的时候从数据库中查找
	 * @param role
	 * @return
	 * @throws ServiceException
	 */
	UnionMember searchUnionMember(String unionId,int roleId);
	
	/**
	 * 获取公会成员信息
	 * @param role
	 * @return
	 */
	UnionMember getUnionMember(String unionId,int roleId);
	
	/**
	 * 会长禅让
	 * @param role
	 * @param roleId
	 * @return
	 */
	Result demisePresident(RoleInstance leader, int roleId) throws ServiceException;
	
	/**
	 * 邀请角色加入公会
	 * @param leader
	 * @param role
	 * @return
	 */
	Result inviteJoinUnionValid(RoleInstance leader, RoleInstance role);
	
	/**
	 * 修改公会宗旨
	 * @param role
	 * @param desc
	 * @return
	 */
	Result modifyUnionDesc(RoleInstance role, String desc);
	
	/**
	 * 是否公会PVP地图
	 * @param mapId
	 * @return
	 */
	boolean isUnionPvpMap(String mapId);
	
	/**
	 * 公会人气
	 * @param role
	 * @param operatorType
	 * @param value
	 * @return
	 */
	Result changeUnionPopularity(String unionId,OperatorType operatorType, int value,int memberId);
	
	/**
	 * 公会人气
	 * @param role
	 * @param operatorType
	 * @param value
	 * @return
	 */
	void changeActivityPopularity(String unionId,int value,String activityName);
	
	/**
	 * 弹劾
	 * @param role
	 * @param goodsNum
	 * @return
	 */
	Result impeach(RoleInstance role);
	
	/**
	 * 根据公会名查找公会（支持模糊查询）
	 * @param UnionName 模糊的公会名称
	 * @return
	 */
	Collection<Union> getUnionListByName(String unionName);
	
	/**
	 * 获取公会的全部帮众列表（按在线，贡献排序）
	 * @param frList
	 * @return
	 */
	List<UnionMember> getUnionMemberListByOnline(String unionId);
	
	/**
	 * 获取公会排行列表
	 * @param size
	 * @return
	 */
	List<Union> getUnionRankList(int size);
	
	/**
	 * 获取权限列表
	 * @param role
	 * @return
	 */
	int getUnionPosition(RoleInstance role) ;
	
	/**
	 * 改变职位
	 * @param UnionInstance
	 */
	void changePosition(String unionId,UnionPositionType positionTpye,int roleId,boolean flag);

	/**
	 * 获取职位
	 * @param roleId
	 * @param UnionId
	 * @return
	 */
	int getUnionMemberPosition(String roleId, String unionId);
	
	/**
	 * 获取第一公会
	 * @return
	 */
	Union getFirstUnion();
	
	/**
	 * 根据阵营排名第一的公会
	 * @param campId
	 * @return
	 */
	Union getCampFirstUnion(byte campId);
	
	/**
	 * 加入公会
	 * @param roleId
	 * @param operRole
	 * @return
	 */
	Result joinUnion(int roleId, RoleInstance operRole);

	/**
	 * 获得公会ID
	 * @param roleId
	 * @return
	 */
	String getUnionId(int roleId);
	
	/**
	 * 公会总人数
	 * @param unionLevel
	 * @return
	 */
	int getUnionDataAllNum(int unionLevel);
	
	/**
	 * 公会最大人气
	 * @param unionLevel
	 * @return
	 */
	int getUnionDataMaxPopualrity(int unionLevel);
	
	/**
	 * 公会职位人数
	 * @param positionType
	 * @return
	 */
	int getUnionPositionNum(byte positionType,int unionLevel);

	/**
	 * 公会排序 按人气
	 * @param list
	 */
	void sortUnion(List<Union> list);

	/**
	 * 邀请同意加入公会
	 * @param roleId
	 * @param operRole
	 * @return
	 */
	Result inviteJoinUnion(int roleId, RoleInstance operRole);

	/**
	 * 公会记录
	 * @param unionId
	 * @param startRow
	 * @param rows
	 * @return
	 */
	ListPageDisplay<UnionRecord> getUnionRecordList(String unionId, int startRow, int rows);

	/**
	 * 获得升级所需人气
	 * @param unionId
	 * @return
	 */
	int getUnionUpgradePopularity(String unionId);

	/**
	 * 判断是否可升级
	 * @param unionId
	 * @return
	 */
	Result isUpgrade(RoleInstance role,String unionId);

	/**
	 * 捐献
	 * @param id
	 * @param role
	 * @return
	 */
	C1728_UnionDonateRespMessage donate(RoleInstance role);
	
	/**
	 * 钻石捐献
	 * @param id
	 * @param role
	 * @return
	 */
	C1728_UnionDonateRespMessage gemDonate(RoleInstance role);
	
	/**
	 * 捐献
	 * @param id
	 * @param role
	 * @return
	 */
	C1727_UnionDonateInfoRespMessage getUnionDonateInfo(RoleInstance role);

	/**
	 * 修改角色DKP
	 * @param role
	 * @param value
	 * @param type
	 * @param flag 是否加人气
	 */
	void changeMemberDkp(RoleInstance role, int value,OperatorType operatorType, FunType type,boolean flag);

	/**
	 * 获得所有公会
	 */
	Map<String,Union> getUnionMap();

	/**
	 * 保存成员数据
	 * @param team
	 * @param member
	 */
	void saveOrUpdUnionMember(UnionMember member);
	
	/**
	 * 公会活动列表
	 * @param unionId
	 * @return
	 */
	C2754_UnionActivityListRespMessage sendActivityList(String unionId);

	/**
	 * 公会活动BOSS列表
	 */
	C2755_UnionBossListRespMessage sendBossDpsList(String unionId,byte activityId);
	
	/**
	 * 公会活动角色DPS列表
	 */
	C2757_UnionRoleDpsListRespMessage sendRoleDpsList(String unionId,byte activityId,byte groupId);

	/**
	 *  获得角色DKP
	 * @param unionId
	 * @param roleId
	 * @return
	 */
	int getUnionMemberDkp(String unionId, int roleId);
	
	/**
	 * 活动时间检查
	 */
	void validActivity();
	
	/**
	 * 判断是否过期
	 * @param activityId
	 * @return
	 */
//	boolean isOverActivity(byte activityId);
	
	/**
	 * 修改公会活动状态
	 * @param unionId
	 * @param activityId
	 * @param state
	 * @param consumeType
	 * @return
	 */
	ChangeActivityResult changeActivity(RoleInstance role,byte activityId,byte state,byte consumeType);
	
	/**
	 * 公会详情
	 */
	C2753_UnionInfoRespMessage sendC2753_UnionInfoRespMessage(RoleInstance role);

	/**
	 * 创建公会判断
	 * @param role
	 * @return
	 */
	Result checkCreateUnionCondition(RoleInstance role);

	/**
	 * 获得活动状态
	 * @param unionId
	 * @param activityId
	 * @return
	 */
	byte getActivityState(String unionId, byte activityId);

	/**
	 * 判断是否可以购买
	 * @param roleId
	 * @return
	 */
	boolean isBuy(int roleId);
	
	Result joinUnionTerritory(RoleInstance role);
	
	Result joinUnionSummon(RoleInstance role);
	
	Result openUnionShop(RoleInstance role);
	
	void joinUnionBuff(RoleInstance role);
	
	Result receiveBuff(RoleInstance role);
	
	Result summonNpc(RoleInstance role,boolean isBroadCast,boolean isSystem);
	
	void killSummonNpc(String unionId,String npcName,int groupId,List<String> hatredList);

	Set<UnionPowerType> getPowerTypeSet(RoleInstance role);

	List<String> getUnionIdList(int size);

}