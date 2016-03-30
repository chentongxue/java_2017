package com.game.draco.app.union;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;

import lombok.Getter;
import lombok.Setter;

import org.python.google.common.collect.Lists;
import org.python.google.common.collect.Maps;
import org.python.google.common.collect.Sets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sacred.alliance.magic.app.map.data.MapConfig;
import sacred.alliance.magic.base.OutputConsumeType;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.domain.GoodsBase;
import sacred.alliance.magic.util.DateUtil;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.util.Wildcard;
import sacred.alliance.magic.vo.MapInstance;
import sacred.alliance.magic.vo.Point;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.chat.ChannelType;
import com.game.draco.app.chat.ChatSysName;
import com.game.draco.app.mail.domain.MailAttriBean;
import com.game.draco.app.mail.type.MailSendRoleType;
import com.game.draco.app.npc.domain.NpcInstance;
import com.game.draco.app.npc.domain.NpcTemplate;
import com.game.draco.app.union.config.UnionActivityInfo;
import com.game.draco.app.union.config.UnionDpsGroupRank;
import com.game.draco.app.union.config.UnionDpsResult;
import com.game.draco.app.union.config.UnionDropConf;
import com.game.draco.app.union.config.UnionDropGroup;
import com.game.draco.app.union.config.UnionMail;
import com.game.draco.app.union.config.UnionVipReward;
import com.game.draco.app.union.config.instance.UnionInstance;
import com.game.draco.app.union.domain.TempDkp;
import com.game.draco.app.union.domain.Union;
import com.game.draco.app.union.domain.UnionMember;
import com.game.draco.app.union.domain.auction.Auction;
import com.game.draco.app.union.domain.auction.GoodsItem;
import com.game.draco.app.union.domain.instance.RoleDps;
import com.game.draco.app.union.domain.instance.UnionActivityBossRecord;
import com.game.draco.app.union.domain.instance.UnionKillBossRecord;
import com.game.draco.app.union.domain.instance.UnionRoleDpsRecord;
import com.game.draco.message.item.GoodsLiteNamedItem;
import com.game.draco.message.push.C2762_UnionStatisticsRespMessage;

public class UnionInstanceAppImpl implements UnionInstanceApp{
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Getter @Setter //公会副本boss计算 <公会ID，<活动ID,Map<groupId,Map<角色ID,副本数据>>>>
	//private Map<String,Map<Byte,Map<Byte,Map<Integer,RoleDps>>>> roleDpsRecordMap = Maps.newConcurrentMap();
	private Map<String,List<UnionRoleDpsRecord>> roleDpsRecordMap = Maps.newConcurrentMap();
	
	@Getter @Setter  //<公会ID，<活动ID,Map<groupId,BOSS数据>>>
	private Map<String,List<UnionActivityBossRecord>> bossRecordMap = Maps.newConcurrentMap();
	
	@Getter @Setter //<公会ID，Set<bossId>>
	private Map<String,Set<String>> killBossMap = Maps.newConcurrentMap();
	
	public final static float TEN_THOUSAD_F = 10000.f;
	
	/**
	 * 初始化副本状态
	 */
	@Override
	public void initEvolve(){
		try{
			List<UnionActivityBossRecord> bossRecordList = GameContext.getBaseDAO().selectAll(UnionActivityBossRecord.class);
			List<UnionRoleDpsRecord> roleDpsRecordList = GameContext.getBaseDAO().selectAll(UnionRoleDpsRecord.class);
			if(!Util.isEmpty(bossRecordList)){
				for(UnionActivityBossRecord record : bossRecordList){
					//添加BOSS数据
					addBossRecordMap(record);
				}
			}
			
			if(!Util.isEmpty(roleDpsRecordList)){
				for(UnionRoleDpsRecord record : roleDpsRecordList){
					List<UnionRoleDpsRecord> list = null;
					record.parseRoleDpsData();
					if(roleDpsRecordMap.containsKey(record.getUnionId())){
						list = roleDpsRecordMap.get(record.getUnionId());
					}
					
					if(Util.isEmpty(list)){
						list = Lists.newArrayList();
						roleDpsRecordMap.put(record.getUnionId(), list);
					}
					
					list.add(record);
				}
			}
		}catch(Exception e){
			logger.error("initEvolve",e);
		}
	}
	
	/**
	 * 初始化BOSS击杀记录
	 */
	@Override
	public void initKillBossRecord(){
		List<UnionKillBossRecord> killBossList = GameContext.getBaseDAO().selectAll(UnionKillBossRecord.class);
		if(Util.isEmpty(killBossList)){
			return;
		}
		for(UnionKillBossRecord record : killBossList){
            //反序列化
            record.postFormDatabase() ;
			addKillBossRecord(record.getUnionId(),record.getKillBoss());
		}
	}
	
	
	/**
	 * 添加BOSS数据
	 * @param record
	 */
	private void addBossRecordMap(UnionActivityBossRecord record){
		try{
			if(Util.isEmpty(record.getUnionId())){
				return ;
			}
			List<UnionActivityBossRecord> list = bossRecordMap.get(record.getUnionId());
			if(Util.isEmpty(list)){
				list = Lists.newArrayList();
				bossRecordMap.put(record.getUnionId(), list);
			}
			list.add(record);
			
		}catch(Exception e){
			logger.error("addBossRecordMap",e);
		}
	}
	
	/**
	 * 添加角色DPS数据
	 * @param record
	 */
	private void addRoleDpsRecordMap(RoleInstance role,RoleDps record,byte activityId,byte groupId) {
		List<UnionRoleDpsRecord> recordList = null;
		boolean flag = false;
		if (roleDpsRecordMap.containsKey(role.getUnionId())) {
			recordList = roleDpsRecordMap.get(role.getUnionId());
			for(UnionRoleDpsRecord dpsRecord : recordList){
				if(dpsRecord.getActivityId() == activityId && dpsRecord.getGroupId() == groupId){
					dpsRecord.getRoleDpsMap().put(record.getRoleId(), record);
					dpsRecord.setLastTime(System.currentTimeMillis());
					flag = true;
				}
			}
		} 
		
		if(Util.isEmpty(recordList)){
			recordList = Lists.newArrayList();
			roleDpsRecordMap.put(role.getUnionId(), recordList);
		}
		
		if(!flag){
			UnionRoleDpsRecord roleDpsRecord = new UnionRoleDpsRecord();
			roleDpsRecord.setActivityId(activityId);
			roleDpsRecord.setUnionId(role.getUnionId());
			roleDpsRecord.setGroupId(groupId);
			roleDpsRecord.getRoleDpsMap().put(record.getRoleId(), record);
			roleDpsRecord.setLastTime(System.currentTimeMillis());
			recordList.add(roleDpsRecord);
		}
	}
	
	/**
	 * 添加DPS
	 * @param roleId
	 * @param bossId
	 * @param dps
	 */
	@Override
	public void addDps(int roleId,byte activityId,byte groupId,int dps){
		RoleInstance role = GameContext.getOnlineCenter().getRoleInstanceByRoleId(String.valueOf(roleId));
		if(!role.hasUnion()){
			return;
		}
		RoleDps record = new RoleDps();
		record.setDps(dps);
		record.setRoleId(roleId);
		record.setRoleName(role.getRoleName());
		addRoleDpsRecordMap(role,record,activityId,groupId);
	}
	
	/**
	 * 重置活动
	 * @param activityId
	 */
	@Override
	public void resetActivity(byte activityId,byte week){
		try{
			//<公会ID，<活动ID,Map<groupId,Map<角色ID,副本数据>>>>
			for(Entry<String,List<UnionRoleDpsRecord>> roleDpsList : roleDpsRecordMap.entrySet()){
				Iterator<UnionRoleDpsRecord> iter = roleDpsList.getValue().iterator();
				while(iter.hasNext()){
					UnionRoleDpsRecord record = iter.next();
					if(record.getActivityId() != activityId){
						continue;
					}
					long weekTime = DateUtil.weekRunTime(new Date(), week);
					if(record.getLastTime() < weekTime){
						continue;
					}
					iter.remove();
					GameContext.getBaseDAO().delete(UnionRoleDpsRecord.class, "unionId", record.getUnionId(),"activityId",activityId);
				}
			}
	
			//<公会ID，<活动ID,Map<groupId,BOSS数据>>>
			for(Entry<String,List<UnionActivityBossRecord>> activityBossRecordMap : bossRecordMap.entrySet()){
				Iterator<UnionActivityBossRecord> iter = activityBossRecordMap.getValue().iterator();
				while(iter.hasNext()){
					UnionActivityBossRecord record = iter.next();
					if(record.getActivityId() != activityId){
						continue;
					}
					long weekTime = DateUtil.weekRunTime(new Date(), week);
					if(record.getLastTime() < weekTime){
						continue;
					}
					iter.remove();
					GameContext.getBaseDAO().delete(UnionActivityBossRecord.class, "unionId", record.getUnionId(),"activityId",activityId);
				}
			}
		}catch(Exception e){
			logger.error("resetActivity",e);
		}
	}
	
	/**
	 * 设置Boss死亡状态 发奖
	 * @param unionId
	 * @param bossId
	 * @param state
	 */
	@Override
	public void setBossState(String unionId,byte activityId,byte groupId,byte state,int maxHp){
		addBossRecord(unionId,activityId,groupId,state,maxHp);
		addRoleDps(unionId,activityId,groupId);
		calculateReward(unionId,groupId,activityId);
	}
	
	private void sendC2762_UnionStatisticsRespMessage(RoleInstance role,List<GoodsItem> itemList,int killDkp,int rankDkp){
		C2762_UnionStatisticsRespMessage respMsg = new C2762_UnionStatisticsRespMessage();
		respMsg.setKillDkp(killDkp);
		respMsg.setRankDkp(rankDkp);
		List<GoodsLiteNamedItem> goodsLiteNamedItemList = new ArrayList<GoodsLiteNamedItem>();
		if(!Util.isEmpty(itemList)){
			for(GoodsItem item : itemList){
				GoodsLiteNamedItem goodsItem = new GoodsLiteNamedItem();
				GoodsBase goodsBase = GameContext.getGoodsApp().getGoodsBase(item.getGoodsId());
				goodsItem.setGoodsId(item.getGoodsId());
				goodsItem.setGoodsName(goodsBase.getName());
				goodsItem.setGoodsImageId(goodsBase.getImageId());
				goodsItem.setNum(item.getGoodsNum());
				goodsItem.setBindType(item.getGoodsBinded());
				goodsItem.setGoodsLevel((byte)goodsBase.getLevel());
				goodsLiteNamedItemList.add(goodsItem);
			}
		}
		respMsg.setGoodsList(goodsLiteNamedItemList);
		role.getBehavior().sendMessage(respMsg);
	}
	
	/**
	 * 保存角色DPS数据
	 * @param unionId
	 * @param activity
	 * @param bossId
	 */
	private void addRoleDps(String unionId,byte activityId,byte groupId){
		Map<Integer, RoleDps> roleDpsRecordMap = getUnionRoleDpsMap(unionId, activityId, groupId);
		if(Util.isEmpty(roleDpsRecordMap)){
			return;
		}
		UnionRoleDpsRecord record = new UnionRoleDpsRecord();
		record.setActivityId(activityId);
		record.setGroupId(groupId);
		record.setUnionId(unionId);
		record.setLastTime(System.currentTimeMillis());
		record.setRoleDpsMap(roleDpsRecordMap);
		record.buildRoleDpsData();
		GameContext.getBaseDAO().saveOrUpdate(record);
	}
	
	/**
	 * 获得Boss状态
	 */
	@Override
	public byte getInsBossState(String unionId,byte activityId,byte groupId){
		List<UnionActivityBossRecord> list = bossRecordMap.get(unionId);
		if(Util.isEmpty(list)){
			return 0;
		}
		for(UnionActivityBossRecord record : list){
			if(record.getActivityId() == activityId && record.getGroupId() == groupId){
				return record.getState();
			}
		}
		return 0;
	}
	
	/**
	 * 初始化boss数据
	 * @param unionId
	 * @param activityId
	 * @param bossId
	 * @param state
	 * @return
	 */
	private void addBossRecord(String unionId,byte acticityId,byte groupId,byte state,int maxHp){
		try{
			UnionActivityBossRecord record = new UnionActivityBossRecord();
			record.setGroupId(groupId);
			record.setActivityId(acticityId);
			record.setState(state);
			record.setUnionId(unionId);
			record.setBossHp(maxHp);
			record.setLastTime(System.currentTimeMillis());
			GameContext.getBaseDAO().saveOrUpdate(record);
			addBossRecordMap(record);
		}catch(Exception e){
			logger.error("saveOrUpdBossRecord",e.getMessage());
		}
	}
	
	@Override
	public List<RoleDps> getBossDpsRank(String unionId,byte activityId,byte groupId){
		Map<Integer,RoleDps> dpsMap = this.getUnionRoleDpsMap(unionId, activityId, groupId);
		List<RoleDps> list = Lists.newArrayList();
		if(Util.isEmpty(dpsMap)){
			return list;
		}
		list.addAll(dpsMap.values());
		sortRoleDps(list);
		return list;
	}
	
	@Override
	public void sortRoleDps(List<RoleDps> list){
		Collections.sort(list, new Comparator<RoleDps>() {
			public int compare(RoleDps info1, RoleDps info2) {
				if(info1.getDps() > info2.getDps()){
					return -1;
				}
				if(info1.getDps() < info2.getDps()){
					return 1;
				}
				return 0;
			}
		});
	}
	
	@Override
	public Map<Integer, RoleDps> getUnionRoleDpsMap(String unionId,
			byte activityId, byte groupId) {
		if(!roleDpsRecordMap.containsKey(unionId)){
			return null;
		}
		List<UnionRoleDpsRecord> recordList = roleDpsRecordMap.get(unionId);
		for(UnionRoleDpsRecord record : recordList){
			if(record.getActivityId() == activityId && record.getGroupId() == groupId){
				return record.getRoleDpsMap();
			}
		}
		return null;
	}

	/**
	 * 发送奖励
	 * @param groupId
	 * @param activityId
	 * @param dpsList
	 * @param type 0公会 1组队
	 */
	@Override
	public void calculateReward(String unionId,byte groupId,byte activityId){
		Set<UnionDpsResult> bossArr = GameContext.getUnionDataApp().getUnionDpsResult(groupId);
		int rank = 0;
		Map<Integer,TempDkp> tempMap = Maps.newHashMap();
		for(UnionDpsResult boss : bossArr){
			UnionInstance unionInstance = GameContext.getUnionDataApp().getUnionInstance(activityId);
			if(unionInstance == null){
				continue;
			}
			NpcTemplate npc = GameContext.getNpcApp().getNpcTemplate(boss.getBossId());
			List<RoleDps> dpsList = getBossDpsRank(unionId,activityId,groupId);
			
			List<UnionDpsGroupRank> dpsGroupList = GameContext.getUnionDataApp().getUnionDpsGroupRank(unionInstance.getRankGroupId());
			UnionDpsResult dpsResult = GameContext.getUnionDataApp().getUnionDpsResultByBossId(boss.getBossId());
			
			if(!Util.isEmpty(dpsList)){
				int pNum = dpsList.size();
				Set<Integer> roleSet = Sets.newHashSet();
				for(RoleDps roleDps : dpsList){
					int killDkp = 0,rankDkp = 0;
					roleSet.add(roleDps.getRoleId());
					UnionActivityBossRecord bossRecord = getBossRecord(unionId,groupId,activityId);
					rank++;
					if(bossRecord != null){
						long bossHp = (long)(bossRecord.getBossHp() * dpsResult.getHarmPercent() / TEN_THOUSAD_F);
						if(roleDps.getDps() >= bossHp){
							killDkp += dpsResult.getKillBossDkp(); 
							for(UnionDpsGroupRank dpsRank : dpsGroupList){
								if(rank >= dpsRank.getRankBefore() && rank <= dpsRank.getRankEnd()){
									rankDkp+= dpsRank.getRewardDkp();
									break;
								}
							}
						}
						TempDkp dkp = new TempDkp();
						dkp.setKillDkp(killDkp);
						dkp.setRoleId(roleDps.getRoleId());
						dkp.setRankDkp(rankDkp);
						tempMap.put(roleDps.getRoleId(), dkp);
						
						UnionMember member = GameContext.getUnionApp().getUnionMember(unionId,roleDps.getRoleId());
						if(member != null){
							sendMail((byte)0,killDkp,rankDkp,member.getRoleId(),rank,npc.getNpcname(),0);
						}
					}
				}
				
				if(!Util.isEmpty(tempMap)){
					
					int goodsNum = getRewardGoodsNum(pNum);
					if(goodsNum > 0){
						
						List<GoodsItem> itemList = Lists.newArrayList();
						UnionDropGroup dropGroupProb;
						if(!Util.isEmpty(dpsResult.getDropgroupId())){
							String [] gId = dpsResult.getDropgroupId().split(",");
							for(String id : gId){
								List<UnionDropGroup> dropGroupElement = GameContext.getUnionDataApp().getUnionDropGroup(Integer.parseInt(id));
								int table  [] = new int[dropGroupElement.size()] ; 
								for(int i =0 ; i <dropGroupElement.size() ;i++ ){
									dropGroupProb = dropGroupElement.get(i);
									table[i] = dropGroupProb.getProbability();
								}
								int index = Util.getProbabilityIndexByTable(table);
								UnionDropGroup dropGroup = dropGroupElement.get(index);
								
								for(int k=0;k<goodsNum;k++){
									GoodsItem item = new GoodsItem();
									item.setGoodsInstanceId(GameContext.getGoodsApp().newGoodsInstanceId());
									item.setGoodsId(dropGroup.getGoodsId());
									item.setGoodsBinded(dropGroup.getGoodsBind());
									item.setGoodsNum(dropGroup.getGoodsNum());
									itemList.add(item);
								}
							}
							Auction auction = GameContext.getUnionAuctionApp().packagingAuction(unionId,activityId, groupId, itemList,roleSet);
							GameContext.getUnionAuctionApp().addAuctionGoods(unionId, auction);
							for(Entry<Integer,TempDkp> temp : tempMap.entrySet()){
								if(GameContext.getOnlineCenter().isOnlineByRoleId(String.valueOf(temp.getKey()))){
									RoleInstance role = GameContext.getOnlineCenter().getRoleInstanceByRoleId(String.valueOf(temp.getKey()));
									sendC2762_UnionStatisticsRespMessage(role, itemList, temp.getValue().getKillDkp(), temp.getValue().getRankDkp());
								}
							}
						}
					}
				}
			}
			//给公会所有人发送奖励
			NpcTemplate bossNpc = GameContext.getNpcApp().getNpcTemplate(boss.getBossId());
			List<UnionMember> memberList = GameContext.getUnionApp().getUnionMemberList(unionId);
			if(!Util.isEmpty(memberList)){
				for(UnionMember member : memberList){
					byte vipLevel = GameContext.getVipApp().getVipLevel(String.valueOf(member.getRoleId()));
					UnionVipReward vipReward = GameContext.getUnionDataApp().getUnionVipReward(vipLevel);
					long joinTime = vipReward.getJoinTime() * DateUtil.ONE_DAY_MILLIS;
					if(System.currentTimeMillis() - member.getCreateTime() >= joinTime){
						sendMail((byte)1,0,0,member.getRoleId(),0,bossNpc.getNpcname(),vipReward.getRewardDkp());
					}
				}
			}
			break;
		}
		
		tempMap.clear();
		
	}
	
	private void sendMail(byte type,int killDkp,int randDkp,int roleId,int rank,String bossName,int dkp){
		try{
			MailAttriBean attriBean = new MailAttriBean();
			UnionMail mail = GameContext.getUnionDataApp().getUnionMail(type);
			String title = "";
			String content = "";
			title = mail.getTitle();//MessageFormat.format(mail.getTitle(),bossName);
			if(mail.getType() == 0){
				content = MessageFormat.format(mail.getContent(),bossName,killDkp,rank,randDkp);
				attriBean.setDkp(killDkp+randDkp);
			}else{
				content = MessageFormat.format(mail.getContent(),bossName,dkp);
				attriBean.setDkp(dkp);
			}
			
			//异步发邮件
			GameContext.getMailApp().sendMailAsync(String.valueOf(roleId), title, content, 
					MailSendRoleType.System.getName(), OutputConsumeType.hero_arena_Reward.getType(),null, attriBean);
		}catch(Exception e){
			logger.error("sendMail",e);
		}
	}
	
	/**
	 * 计算人数获得物品个数
	 * @return
	 */
	private int getRewardGoodsNum(int pNum){
		List<UnionDropConf> unionDropConfList = GameContext.getUnionDataApp().getUnionDropConfList();
		if(Util.isEmpty(unionDropConfList)){
			return 0 ;
		}
		int goodsNum = 0;
		for (UnionDropConf dropConf : unionDropConfList) {
			if (pNum >= dropConf.getMin() && pNum <= dropConf.getMax()) {
				int prob = (int) (dropConf.getProb() / TEN_THOUSAD_F);
				if (prob > 100) {
					goodsNum += prob / 100;
					prob = prob % 100;
				}
				Random rand = new Random();
				int randInt = rand.nextInt(101);
				if (randInt > prob) {
					goodsNum++;
				}
			}
		}
		return goodsNum;
	}
	
	/**
	 * 获得某个活动中的boss数据
	 * @param unionId
	 * @param groupId
	 * @param activityId
	 * @return
	 */
	private UnionActivityBossRecord getBossRecord(String unionId,byte groupId,byte activityId){
		List<UnionActivityBossRecord> list = bossRecordMap.get(unionId);
		if(Util.isEmpty(list)){
			return null ;
		}
		for(UnionActivityBossRecord record : list){
			if(record.getActivityId() == activityId && record.getGroupId() == groupId){
				return record;
			}
		}
		return null;
	}
	
	/**
	 * 添加BOSS击杀记录
	 */
	@Override
	public void addUnionKillBossRecord(String unionId,String bossId){
		Set<String> setBoss = killBossMap.get(unionId);
        if(null == setBoss){
            setBoss = Sets.newHashSet();
            killBossMap.put(unionId, setBoss);
        }
        setBoss.add(bossId);

		UnionKillBossRecord record = new UnionKillBossRecord();
		record.setUnionId(unionId);
        record.setKillBoss(setBoss);
        //序列化
		record.preToDatabase();
		GameContext.getBaseDAO().saveOrUpdate(record);
	}
	
	/**
	 * 添加BOSS击杀记录
	 */
	private void addKillBossRecord(String unionId,Set<String> setBoss){
        if(Util.isEmpty(setBoss)){
            killBossMap.remove(unionId);
            return ;
        }
		killBossMap.put(unionId, setBoss);
	}
	
	
	
	/**
	 * 获得击杀BOSS记录
	 * @param unionId
	 * @return
	 */
	@Override
	public Set<String> getUnionKillBossRecord(String unionId){
        if(Util.isEmpty(unionId)){
            return Sets.newHashSet();
        }
		Set<String> setBoss = killBossMap.get(unionId);
        if(null == setBoss){
           return Sets.newHashSet();
        }
		return setBoss ;
	}

	@Override
	public Result enterInstance(RoleInstance role, byte activityId) {
		Result result = new Result();
		try{
			Map<Byte,UnionActivityInfo> activityInfoMap = GameContext.getUnionDataApp().getUnionActivityMap();
			UnionActivityInfo activityInfo = null;
			
			Union union = GameContext.getUnionApp().getUnion(role);
			if(union == null){
				return result.setInfo(GameContext.getI18n().getText(TextId.UNION_NULL));
			}
			
			if(activityInfoMap.containsKey(activityId)){
				activityInfo = activityInfoMap.get(activityId);
			}else{
				return result.setInfo(GameContext.getI18n().getText(TextId.UNION_ACTIVITY_ERR));
			}
			
			byte state = GameContext.getUnionApp().getActivityState(role.getUnionId(), activityId);
			if(state == 0){
				return result.setInfo(GameContext.getI18n().messageFormat(TextId.UNION_ACTIVITY_CLOSE, activityInfo.getActivityName()));
			}
			
			UnionInstance instance = GameContext.getUnionDataApp().getUnionInstance(activityId);
			if(instance == null){
				return result.setInfo(GameContext.getI18n().getText(TextId.UNION_ACTIVITY_ERR));
			}
			
			MapInstance mapIn = role.getMapInstance();
			MapConfig mapConfig = mapIn.getMap().getMapConfig();
			if(mapConfig.iscopymode()){
				return result.setInfo(GameContext.getI18n().getText(TextId.UNION_ACTIVITY_INSTANCE));
			}
			
			Point targetPoint = new Point(instance.getMapId(),instance.getMapX(),instance.getMapY());
			GameContext.getUserMapApp().changeMap(role, targetPoint);
			result.success();
		}catch(Exception e){
			logger.error("enterInstance",e);
		}
		return result;
	}

	@Override
	public void onJoinGame(RoleInstance role) {
		
	}
	
	@Override
	public void broadcast(RoleInstance attacker, NpcInstance victim) {
		try {
			String message = GameContext.getI18n().getText(TextId.BROAD_CAST_UNION_BOSS).replace(Wildcard.Role_Name,
					Util.getColorRoleName(attacker, ChannelType.Publicize_Personal)).replace(Wildcard.NpcName, victim.getNpcname());
			GameContext.getChatApp().sendSysMessage(ChatSysName.System, ChannelType.Publicize_Personal, message, null, null);
		} catch (Exception e) {
			logger.error("UnionInstanceAppImpl.broadcast error!", e);
		}
	}
	
}
