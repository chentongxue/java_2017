package com.game.draco.app.npc.domain;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import sacred.alliance.magic.base.RoleType;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.vo.AbstractRole;
import sacred.alliance.magic.vo.MapInstance;
import sacred.alliance.magic.vo.Point;

import com.game.draco.app.npc.refresh.NpcRefreshTask;
import com.game.draco.app.npc.type.NpcActionType;
import com.game.draco.app.npc.type.NpcRankType;


/**   
*    
* 项目名称：easter   
* 类名称：NpcInstance   
* 修改备注：   
* @version    
*    
*/
public class NpcInstance extends AbstractRole{
	
	public NpcInstance(){
		this.roleType = RoleType.NPC;
	}
	
	public NpcInstance(RoleType roleType) {
		this.roleType = roleType;
	}
	
	private int resid;

	private String npcid;
	
	//private String npcname;
	
	private NpcRankType rank;
	
	private NpcTemplate npc;
	
	private NpcActionType npcActionType;
	
	private Date dieTime ; //NPC死亡时间
	/**出生点序列*/
	private int npcBornDataIndex = -1 ;
	/**死亡后当前重生间隔*/
	private int currentInterval = 0 ;
	
	private AbstractRole ownerInstance ;
	
	//private byte pal = -1 ;
	//private byte mmp = -1 ;
	
	private long attackedTime;
	
	//刷新规则
	private NpcRefreshTask npcRefreshTask;
	private int speakCount ; //喊话次数
	private long lastSpeakTime ;//上次喊话时间
	private short[] bossState = null ;
	private long[] timeRecords = null ;
	private byte summonType;    //召唤类型
	private int summonId;       //是否是召唤出来的怪
	private String summonRoleId;//召唤者ID
	private int thinkArea;
	
	//npc消失时间
	private long disappearTime;
	/**
	 * 当前攻击者的数目
	 */
	private int effectAttackerNum = 0 ;
	private Set<Integer> allAttacker = new HashSet<Integer>() ;
	
	//npc是否被改变外形
	private boolean changeShape;

	public byte getSummonType(){
		return summonType;
	}

	public void setSummonType(byte summonType){
		this.summonType  = summonType;
	}
	
	public int getEffectAttackerNum() {
		return effectAttackerNum;
	}

	public void setEffectAttackerNum(int effectAttackerNum) {
		this.effectAttackerNum = effectAttackerNum;
	}

	public Set<Integer> getAllAttacker() {
		return allAttacker;
	}

	public void setAllAttacker(Set<Integer> allAttacker) {
		this.allAttacker = allAttacker;
	}

	public void resetAttackerInfo(){
		this.effectAttackerNum = 0 ;
		this.allAttacker.clear();
	}
	
	public long getLastSpeakTime() {
		return lastSpeakTime;
	}

	public void setLastSpeakTime(long lastSpeakTime) {
		this.lastSpeakTime = lastSpeakTime;
	}

	public void addSpeakCount(){
		speakCount ++ ;
	}
	
	public int getSpeakCount() {
		return speakCount;
	}

	public void setSpeakCount(int speakCount) {
		this.speakCount = speakCount;
	}

	public NpcTemplate getNpc() {
		return npc;
	}

	public void setNpc(NpcTemplate npc) {
		this.npc = npc;
	}

	public NpcRankType getRank() {
		return rank;
	}

	public void setRank(NpcRankType rank) {
		this.rank = rank;
	}
	
	public boolean isChangeShape() {
		return changeShape;
	}

	public void setChangeShape(boolean changeShape) {
		this.changeShape = changeShape;
	}
	
	public String getNpcid() {
		return npcid;
	}

	public void setNpcid(String npcid) {
		this.npcid = npcid;
	}

	public String getNpcname() {
        if(null == this.npc){
            return "" ;
        }
		return this.npc.getNpcname();
	}


	/*public void setNpcname(String npcname) {
		this.npcname = npcname;
	}*/
	
	public NpcActionType getNpcActionType() {
		return npcActionType;
	}

	public void setNpcActionType(NpcActionType npcActionType) {
		this.npcActionType = npcActionType;
	}

	public Date getDieTime() {
		return dieTime;
	}

	public void setDieTime(Date dieTime) {
		this.dieTime = dieTime;
	}

	public int getNpcBornDataIndex() {
		return npcBornDataIndex;
	}

	public void setNpcBornDataIndex(int npcBornDataIndex) {
		this.npcBornDataIndex = npcBornDataIndex;
	}

	public int getCurrentInterval() {
		return currentInterval;
	}

	public void setCurrentInterval(int currentInterval) {
		this.currentInterval = currentInterval;
	}

	public AbstractRole getOwnerInstance() {
		return ownerInstance;
	}

	public void setOwnerInstance(AbstractRole ownerInstance) {
		this.ownerInstance = ownerInstance;
	}

	/*public byte getPal() {
		return pal;
	}

	public void setPal(byte pal) {
		this.pal = pal;
	}

	public byte getMmp() {
		return mmp;
	}

	public void setMmp(byte mmp) {
		this.mmp = mmp;
	}*/
	public long getAttackedTime() {
		return attackedTime;
	}

	public void setAttackedTime(long attackedTime) {
		this.attackedTime = attackedTime;
	}
	

	public int getResid() {
		return resid;
	}

	public void setResid(int resid) {
		this.resid = resid;
	}


	public NpcRefreshTask getNpcRefreshTask() {
		return npcRefreshTask;
	}
	public void setNpcRefreshTask(NpcRefreshTask npcRefreshTask) {
		this.npcRefreshTask = npcRefreshTask;
	}

	@Override
	public String getRoleName() {
		return this.getNpcname();
	}

	public short[] getBossState() {
		return bossState;
	}

	public void setBossState(short[] bossState) {
		this.bossState = bossState;
	}

	public long[] getTimeRecords() {
		return timeRecords;
	}

	public void setTimeRecords(long[] timeRecords) {
		this.timeRecords = timeRecords;
	}

	public int getSummonId() {
		return summonId;
	}

	public void setSummonId(int summonId) {
		this.summonId = summonId;
	}

	public String getSummonRoleId() {
		return summonRoleId;
	}

	public void setSummonRoleId(String summonRoleId) {
		this.summonRoleId = summonRoleId;
	}
	
	public boolean isSummon(){
		if(this.summonId > 0 && this.summonRoleId != null) {
			return true;
		}
		return false;
	}

	public int getThinkArea() {
		return thinkArea;
	}

	public void setThinkArea(int thinkArea) {
		this.thinkArea = thinkArea;
	}

	@Override
	public byte getAttriGearId() {
		return npc.getGearId();
	}

	@Override
	public byte getAttriSeriesId() {
		return npc.getSeriesId();
	}
	
	public long getDisappearTime() {
		return disappearTime;
	}

	public void setDisappearTime(long disappearTime) {
		this.disappearTime = disappearTime;
	}
	
	/**
	 * 返回召唤者
	 * 如果不是召唤的则返回自己
	 * @return
	 */
	public AbstractRole getMasterRole() {
		return this;
	}
	
	public Point getRebornPoint() {
		if(roleType != RoleType.COPY) {
			return super.getRebornPoint();
		}
		AbstractRole summoner = this.getMasterRole();
		if(null == summoner) {
			return super.getRebornPoint();
		}
		return summoner.getCurrentPoint();
	}
	
	@Override
	public int getColor() {
		int color = super.getColor();
		if(-1 != color || null == this.getNpc()){
			return color ;
		}
		String str = this.getNpc().getColor() ;
		if(Util.isEmpty(str)){
			return -1 ;
		}
		return (int)Long.parseLong(str, 16);
	}
}
