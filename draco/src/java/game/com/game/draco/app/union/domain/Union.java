package com.game.draco.app.union.domain;

import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import lombok.Data;

import org.python.google.common.collect.Maps;
import org.python.google.common.collect.Sets;

import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.constant.Cat;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.core.exception.ServiceException;
import sacred.alliance.magic.domain.GoodsBase;
import sacred.alliance.magic.util.DateUtil;
import sacred.alliance.magic.util.Log4jManager;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.union.type.UnionPositionType;
import com.game.draco.app.union.type.UnionPowerType;
import com.google.common.collect.Lists;

public @Data class Union implements java.io.Serializable{
	
	private static final long serialVersionUID = 1L;

	//公会成员数据<成员ID,成员数据>
	private Map<Integer,UnionMember> unionMemberMap = Maps.newHashMap();
	
	//申请加入公会的角色列表 <角色ID,日期>
	private Map<Integer,UnionMember> applyMap = Maps.newLinkedHashMap();
	
	//日志记录 <公会ID，公会记录>
	private List<UnionRecord> unionRecordList = Lists.newArrayList();
	
	//公会ID
	private String unionId;
	
	//公会名称
	private String unionName;
	
	//公会等级
	private byte unionLevel;
	
	//会长角色ID
	private int leaderId;
	
	//会长名称
	private String leaderName;
	
	//创建时间
	private long createTime;
	
	//公会公告
	private String unionDesc ;
	
	//公会人气
	private int popularity;

	//门派阵营
	private byte unionCamp;
	
	//当前进度
	private int progress;
	
	//成员变化锁
	private Lock memberLock = new ReentrantLock();
	
	//弹劾锁
	private Lock impeachLock = new ReentrantLock();
	
	//升级锁
	private Lock upgradeLock = new ReentrantLock();
	
	//召唤BOSS
	private volatile boolean summonFlag = false;
	
	//召唤BOSS时间
	private long summonTime;
	
	//boss状态 0死 1活
	private boolean bossState;
	
	public void offlineLog(){
		StringBuffer sb = new StringBuffer();
		sb.append(unionId);
		sb.append(Cat.pound);
		sb.append(unionName);
		sb.append(Cat.pound);
		sb.append(unionLevel);
		sb.append(Cat.pound);
		sb.append(leaderId);
		sb.append(Cat.pound);
		sb.append(leaderName);
		sb.append(Cat.pound);
		sb.append(DateUtil.getTimeByDate(new Date(createTime)));
		sb.append(Cat.pound);
		sb.append(unionDesc);
		sb.append(Cat.pound);
		sb.append(popularity);
		sb.append(Cat.pound);
		Log4jManager.UNION_LOG.info(sb.toString());
	}
	
	public UnionMember getUnionMember(int roleId){
		return unionMemberMap.get(roleId);
	}
	
	/**
	 * 获得权限
	 * @param role
	 * @return
	 */
	public Set<UnionPowerType> getPowerTypeSet(RoleInstance role) {
		Set<UnionPowerType> powerSet = Sets.newHashSet();
		UnionMember member = getUnionMember(role.getIntRoleId());
		if(member != null){
			UnionPositionType positionType = UnionPositionType.getPosition(member.getPosition());
			if(null == positionType){
				return powerSet;
			}
			return GameContext.getUnionDataApp().getPowerTypeSet(positionType);
		}
		return powerSet;
	}
	
	/**
	 * 删除成员数据
	 * @param role
	 * @throws ServiceException
	 */
	public void deleteUnionMember(int roleId) throws ServiceException{
		try{
			//从缓存中清除
			unionMemberMap.remove(roleId);
		} catch(Exception e){
			throw new ServiceException("deleteUnionMember exception", e);
		}
	}
	
	
	/**
	 * 能否被弹劾
	 * @return
	 */
	public Result canImpeach(RoleInstance role,int goodsId,int goodsNum){
		Result result = new Result();
		if(!getPowerTypeSet(role).contains(UnionPowerType.Impeach)){
			return result.setInfo(GameContext.getI18n().getText(TextId.UNION_DEMISE_NO_POSITION));
		}
		Map<Integer,UnionMember> memberMap = getUnionMemberMap();
		if(null == memberMap) {
			return result.setInfo(GameContext.getI18n().getText(TextId.UNION_FAILURE));
		}
		UnionMember leader = memberMap.get(leaderId);
		if(null == leader){
			return result.setInfo(GameContext.getI18n().getText(TextId.UNION_IMPEACH_ERROR));
		}
		Date now = new Date();
		int diffDay = DateUtil.dateDiffDay(now, new Date(leader.getOfflineTime()));
		if(diffDay < GameContext.getUnionDataApp().getUnionBase().getImpeachDay()){
			return result.setInfo(GameContext.getI18n().getText(TextId.UNION_IMPEACH_TIME_ERROR));
		}
		
		boolean isHave = GameContext.getUserGoodsApp().isExistGoodsForBag(role, goodsId);
		if(!isHave){
			GoodsBase goodsBase = GameContext.getGoodsApp().getGoodsBase(goodsId);
			result.setInfo(GameContext.getI18n().messageFormat(TextId.UNION_IMPEACH_NOT_GOODS_ERR, goodsNum,goodsBase.getName()));
			return result;
		}
		
		return result.success();
	}
	
	/**
	 * 保存公会成员数据
	 * @param team
	 * @param unionMember
	 */
	public void saveUnionMember(UnionMember unionMember){
		unionMemberMap.put(unionMember.getRoleId(), unionMember);
	}
	
	/**
	 * 排序成员列表
	 */
	public List<UnionMember> getUnionMemberList(){
		List<UnionMember> memberList = Lists.newArrayList();
		memberList.addAll(unionMemberMap.values());
		sortUnionMember(memberList);
		return memberList;
	}
	
	/**
	 * 成员排序 （在线 、职位、等级）
	 */
	public void sortUnionMember(List<UnionMember> memberList){
		Collections.sort(memberList, new Comparator<UnionMember>() {
			public int compare(UnionMember info1, UnionMember info2) {
				boolean flag1 = GameContext.getOnlineCenter().isOnlineByRoleId(String.valueOf(info1.getRoleId()));
				boolean flag2 = GameContext.getOnlineCenter().isOnlineByRoleId(String.valueOf(info2.getRoleId()));
				byte flags1 = flag1 == true ? (byte)1 : (byte)0;
				byte flags2 = flag2 == true ? (byte)1 : (byte)0;
				if(flags1 > flags2){
					return -1;
				}
				if(flags1 < flags2){
					return 1;
				}
				if(info1.getPosition() < info2.getPosition()){
					return -1;
				}
				if(info1.getPosition() > info2.getPosition()){
					return 1;
				}
				if(info1.getLevel() > info2.getLevel()){
					return -1;
				}
				if(info1.getLevel() < info2.getLevel()){
					return 1;
				}
				return 0;
			}
		});
	}
	
	public void saveUnionRecord(UnionRecord record){
		unionRecordList.add(record);
	}
	
	public void sortUnionRecord(){
		Collections.sort(unionRecordList, new Comparator<UnionRecord>() {
			public int compare(UnionRecord info1, UnionRecord info2) {
				if(info1.getCreateTime() >info2.getCreateTime()) {
					return -1;
				}
				return 0;
			}
		});
	}
	
	/**
	 * 排序成员列表
	 */
	public List<UnionRecord> getUnionRecordList(){
		sortUnionRecord();
		return unionRecordList;
	}
	

}
