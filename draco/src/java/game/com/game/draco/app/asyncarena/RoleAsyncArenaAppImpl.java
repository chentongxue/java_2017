package com.game.draco.app.asyncarena;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import lombok.Getter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sacred.alliance.magic.app.goods.GoodsOperateBean;
import sacred.alliance.magic.app.goods.behavior.result.AddGoodsBeanResult;
import sacred.alliance.magic.app.menu.MenuIdType;
import sacred.alliance.magic.base.AttributeType;
import sacred.alliance.magic.base.BindingType;
import sacred.alliance.magic.base.ChallengeResultType;
import sacred.alliance.magic.base.MapLogicType;
import sacred.alliance.magic.base.OperatorType;
import sacred.alliance.magic.base.OutputConsumeType;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.constant.Cat;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.domain.GoodsBase;
import sacred.alliance.magic.util.DateUtil;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.vo.Point;
import sacred.alliance.magic.vo.RoleInstance;

import com.alibaba.fastjson.JSON;
import com.game.draco.GameContext;
import com.game.draco.app.asyncarena.config.AsyncBuy;
import com.game.draco.app.asyncarena.config.AsyncGroup;
import com.game.draco.app.asyncarena.config.AsyncGroupReward;
import com.game.draco.app.asyncarena.config.AsyncMap;
import com.game.draco.app.asyncarena.config.AsyncRankReward;
import com.game.draco.app.asyncarena.config.AsyncRefresh;
import com.game.draco.app.asyncarena.config.AsyncReward;
import com.game.draco.app.asyncarena.config.AsyncSort;
import com.game.draco.app.asyncarena.domain.AsyncArenaRole;
import com.game.draco.app.asyncarena.domain.AsyncBattleInfo;
import com.game.draco.app.asyncarena.vo.AsyncArenaBattleScoreType;
import com.game.draco.app.asyncarena.vo.RoleAsyncRankRewardResult;
import com.game.draco.app.asyncarena.vo.RoleAsyncRefResult;
import com.game.draco.app.asyncpvp.domain.AsyncPvpRoleAttr;
import com.game.draco.app.asyncpvp.vo.AsyncPvpBattleInfo;
import com.game.draco.app.hero.domain.RoleHero;
import com.game.draco.app.mail.type.MailSendRoleType;
import com.game.draco.message.item.AsyncArenaRewardItem;
import com.game.draco.message.item.AsyncArenaRoleBuyChallengeItem;
import com.game.draco.message.item.AsyncArenaTargetItem;
import com.game.draco.message.item.TargetItem;
import com.game.draco.message.push.C2622_RoleAsyncArenaSummarizeRespMessage;
import com.game.draco.message.response.C2627_AsyncArenaBuyNumRespMessage;
import com.google.common.collect.Maps;

public class RoleAsyncArenaAppImpl implements RoleAsyncArenaApp {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	private Lock lock = new ReentrantLock();

	public final static float TEN_ASYNC_F = 10000.f;
	
	public final static int NUM = 14;

	// 异步竞技场分组数据 <角色ID，<对战方角色ID，对战方数据>
	@Getter
	private Map<Integer, Map<Integer, AsyncBattleInfo>> roleBattleMap = Maps.newConcurrentMap();

	// 异步竞技场分组数据 <Integer,<组ID(ABCDE)，对战方角色ID>>
	@Getter
	private Map<Integer, Map<Byte, List<Integer>>> roleGroupMap = Maps	.newConcurrentMap();

	// 角色竞技场数据 <角色ID，角色数据> 也可做排行榜数据
	@Getter
	private Map<Integer, AsyncArenaRole> roleAsyncArenaMap = Maps.newConcurrentMap();

	@Override
	public int onLogin(RoleInstance role, Object context) {
		GameContext.getRoleAsyncArenaApp().initRoleAsyncArena(role);
		return 1;
	}

	@Override
	public int onLogout(RoleInstance role, Object context) {
		clearAsyncArenaMap(role,true);
		return 1;
	}
	
	@Override
	public int onCleanup(String roleId, Object context) {
		int rId = Integer.parseInt(roleId);
		roleBattleMap.remove(rId);
		roleGroupMap.remove(rId);
		roleAsyncArenaMap.remove(rId);
		return 1;
	}

	/**
	 * 竞技场排行帮排序
	 * 
	 * @param list
	 * @return
	 */
	private List<AsyncArenaRole> rankComparator(List<AsyncArenaRole> list) {
		// 排序
		Collections.sort(list, new Comparator<AsyncArenaRole>() {
			@Override
			public int compare(AsyncArenaRole h1, AsyncArenaRole h2) {
				if (h1.getNowHonor() + h1.getHistoryHonor() > h2.getNowHonor()
						+ h2.getHistoryHonor()) {
					return -1;
				} 
				
				if (h1.getNowHonor() + h1.getHistoryHonor() < h2.getNowHonor()
						+ h2.getHistoryHonor()) {
					return 1;
				} 
				
				return 0;
			}
		});
		return list;
	}

	/**
	 * 初始化战力分组数据
	 * @param role
	 */
	private void initAsyncArenaFilterGroup(RoleInstance role) {
		try {
			//获得分组数据
			Map<Byte,AsyncGroup> groupMap = GameContext.getAsyncArenaApp().getAsyncGroupMap();
			if(groupMap != null && !groupMap.isEmpty()){
				Map<Byte,List<Integer>> gMap = Maps.newConcurrentMap();
				for(Entry<Byte,AsyncGroup> group : groupMap.entrySet()){
					List<Integer> setRoleId = new ArrayList<Integer>();
					//大于
					float gt = 0;
					if(group.getValue().getGt() != -1){
						gt = (group.getValue().getGt()  / TEN_ASYNC_F )* role.getBattleScore();
					}
					//小于
					float lt = 0;
					if(group.getValue().getLt() != -1){
						lt = (group.getValue().getLt()  / TEN_ASYNC_F ) * role.getBattleScore();
					}
					String gtr = "";
					String ltr = "";
					if(gt != 0){
						gtr = String.valueOf((int)gt);
					}
					if(lt != 0){
						ltr = String.valueOf((int)lt);
					}
					Map<String,String> data = GameContext.getRoleAsyncArenaStorage().getRoleBattleScores(null, gtr,ltr,300);
					if(data != null && !data.isEmpty()){
						for(Entry<String,String> d : data.entrySet()){
							if(d.getValue() == null || Integer.parseInt(d.getKey()) == role.getIntRoleId()){
								continue;
							}
							if(gMap.containsKey(group.getKey())){
								List<Integer> roleId = gMap.get(group.getKey());
								roleId.add(Integer.parseInt(d.getKey()));
							}else{
								setRoleId.add(Integer.parseInt(d.getKey()));
							}
							gMap.put(group.getKey(),setRoleId);
						}
					}
				}
				if(gMap != null && !gMap.isEmpty()){
					roleGroupMap.put(role.getIntRoleId(),gMap);
				}
			}
		}catch (Exception ex) {
			logger.error("SSDBStorage.getRoleBattleScores error ", ex);
		}
	}

//	@Override
//	public NpcInstance createAsyncArenaNpc(RoleInstance role, int targetRoleId,
//			MapInstance mapInstance) {
//		NpcInstance npcInstance = null;
//		try {
//			Map<Integer, AsyncBattleInfo> targetBattleMap = roleBattleMap
//					.get(role.getIntRoleId());
//			if (null == targetBattleMap) {
//				return npcInstance;
//			}
//			if (!roleBattleMap.containsKey(targetRoleId)) {
//				return npcInstance;
//			}
//			AsyncPvpRoleAttr attr = GameContext.getAsyncPvpApp().getAsyncPvpRoleAttr(String.valueOf(targetRoleId));
//			AsyncMap asyncMap = GameContext.getAsyncArenaApp().getAsyncMap();
//			npcInstance = NpcInstanceFactroy.createAsyncPvpNpcInstance(attr,	asyncMap.getMapId(), asyncMap.getTargetMapX(),
//					asyncMap.getTargetMapY());
//			npcInstance.setMapInstance(mapInstance);
//			npcInstance.setNpcBornDataIndex(-1);
//			mapInstance.addAbstractRole(npcInstance);
//			// 通知
//			for (RoleInstance ri : mapInstance.getRoleList()) {
//				C0204_MapUserEntryNoticeRespMessage message = new C0204_MapUserEntryNoticeRespMessage();
//				message.setItem(Converter.getAsyncPvpRoleBodyItem(npcInstance
//						.getRoleId(), attr, (short) asyncMap
//						.getTargetMapX(), (short) asyncMap
//						.getTargetMapY()));
//				GameContext.getMessageCenter()
//						.send("", ri.getUserId(), message);
//			}
//		} catch (Exception e) {
//			logger.error("LadderApp.createLadderNpc error:", e);
//			return null;
//		}
//		return npcInstance;
//	}

	/**
	 * 是否需要初始化分组
	 * @param role
	 * @return
	 */
	private boolean isInitGroup(RoleInstance role){
		if(roleGroupMap.containsKey(role.getIntRoleId())){
			return false;
		}else{
			return true;
		}
	}
	
	/**
	 * 是否需要初始化对战
	 * @param role
	 * @return
	 */
	private boolean isInitBattleInfo(RoleInstance role){
		if(roleBattleMap.containsKey(role.getIntRoleId())){
			return false;
		}else{
			return true;
		}
	}
	
	/**
	 * 是否要清空数据
	 * @param role
	 * @return
	 */
	private boolean isDelRoleAsyncArena(RoleInstance role){
		if(roleAsyncArenaMap.containsKey(role.getIntRoleId())){
			AsyncArenaRole asyncArenaRole = roleAsyncArenaMap.get(role.getIntRoleId());
			//判断是否为同一天
			if(!DateUtil.sameDay(new Date(asyncArenaRole.getRefTime()),new Date(System.currentTimeMillis()))){
				return true;
			}else{
				return false;
			}
		}
		return false;
	}

	@Override
	public void refAsyncArenaFilterBattleVip(RoleInstance role,boolean isVip,boolean isDel) {
		lock.lock();
		try{
			List<AsyncSort> sortList = GameContext.getAsyncArenaApp().getAsyncSortList();
			//总人数
			long total = getRoleAsyncArenaRanking();
			//我的排名
			int ranking = getRoleBattleScoreRanking(role);
			if(ranking == 0){
				ranking = 1;
			}else if(ranking == -1){
				ranking = (int)total;
			}
			if(roleGroupMap.containsKey(role.getIntRoleId())){
				Map<Byte, List<Integer>> goup = roleGroupMap.get(role.getIntRoleId());
				if(sortList != null && !sortList.isEmpty()){
					String [] sortArr = null;
					//数量为X*8(向上取整)，最多5个
					long rnum = Math.min(5, (total*8));
					
					Map<Integer, AsyncBattleInfo> asyncBattleInfoMap = null;
					if(isVip){
						//刷新剩余未挑战胜利的对手，已挑战胜利的对手不会刷新
						asyncBattleInfoMap = roleBattleMap.get(role.getIntRoleId());
						if(isDel){
							clearRoleAsyncArenaBattleMap(role,asyncBattleInfoMap);
							initAsyncArenaFilterGroup(role);
							goup = roleGroupMap.get(role.getIntRoleId()); 
						}
					}else{
						asyncBattleInfoMap = new HashMap<Integer, AsyncBattleInfo>();
					}
					List<Integer> targetRoleIdList = null;
					int ruleNum = 0;
					int refNum = 5;
					for(AsyncSort sort : sortList){
						if(asyncBattleInfoMap.size() >= NUM){
							break;
						}
						ruleNum++;
						if(ruleNum == sortList.size()){
							refNum =NUM;
						}
						sortArr = sort.getSort().split(",");
						
						int i=0;
						for(;i<sortArr.length;i++){
							if(goup.containsKey(Byte.parseByte(sortArr[i]))){
								targetRoleIdList = goup.get(Byte.parseByte(sortArr[i]));
								if(targetRoleIdList.isEmpty()){
									continue;
								}
								int count = 0;
								for(;count<refNum;count++){
									if(asyncBattleInfoMap.size() >= NUM){
										break;
									}
									if(targetRoleIdList.isEmpty()){
										break;
									}
									int size = targetRoleIdList.size();
									int randInt = 0;
									if(size > rnum){
										randInt = Util.randomInt(0,(int)rnum);
									}else{
										randInt = Util.randomInt(0,size-1);
									}
									int targetRoleId = targetRoleIdList.get(randInt);
									if(asyncBattleInfoMap.containsKey(targetRoleId)){
										targetRoleIdList.remove(randInt);
										continue;
									}
									AsyncBattleInfo targetBattleInfo = new AsyncBattleInfo();
									targetBattleInfo.setTroleId(targetRoleId);
									asyncBattleInfoMap.put(targetRoleId, targetBattleInfo);
									targetRoleIdList.remove(randInt);
								}
							}
						}
					}
					roleBattleMap.put(role.getIntRoleId(),asyncBattleInfoMap);
				}
			}
		}catch(Exception e){
			logger.error("refAsyncArenaFilterBattleVip",e);
		}finally{
			lock.unlock();
		}
		
	}
	
	
	/**
	 * 获得总人数
	 * @return
	 */
	private long getRoleAsyncArenaRanking(){
		return GameContext.getRoleAsyncArenaStorage().getTotalRanking();
	}
	
	@Override
	public int getRoleBattleScoreRanking(RoleInstance role){
		//如果一共N条记录,分数最高的返回0,分数最低的返回 N-1 返回数值[0-N),-1表示不存在
		return GameContext.getAsyncPvpApp().getRoleBattleScoreRanking(role.getRoleId());
	}
	
	/**
	 * VIP刷新数据清掉未挑战胜利的数据
	 */
	private void clearRoleAsyncArenaBattleMap(RoleInstance role,Map<Integer,AsyncBattleInfo> asyncBattleMap){
		Set<Integer> clearTargetRoleId = new HashSet<Integer>();
		for(Entry<Integer,AsyncBattleInfo> battleInfo : asyncBattleMap.entrySet()){
			if(battleInfo.getValue().getState() == (byte)0){
				if(battleInfo.getValue().getIsSu() == (byte)0){
					clearTargetRoleId.add(battleInfo.getKey());
				}
			}
		}
		for(Integer id : clearTargetRoleId){
			asyncBattleMap.remove(id);
		}
	}
	
	/**
	 * 清除角色分组对战数据
	 */
	private void clearAsyncArenaMap(RoleInstance role,boolean isLeaveGame){
		roleBattleMap.remove(role.getIntRoleId());
		roleGroupMap.remove(role.getIntRoleId());
		if(isLeaveGame){
			roleAsyncArenaMap.remove(role.getIntRoleId());
		}
	}
	
	@Override
	public Map<Integer, AsyncBattleInfo> getRoleAsyncBattleInfo(RoleInstance role) {
		return roleBattleMap.get(role.getIntRoleId());
	}

	/**
	 * 初始化数据
	 */
	@Override
	public void initAutoRoleAsyncArena(RoleInstance role,AsyncArenaRole asyncArenaRole) {
		try{
			if(isDelRoleAsyncArena(role)){
				if(roleAsyncArenaMap.containsKey(role.getIntRoleId())){
					dailyUpdateAsyncArenaByRole(asyncArenaRole);
				}
				clearAsyncArenaMap(role,false);
			}
			
			if(!isInitGroup(role)){
				return;
			}
			initAsyncArenaFilterGroup(role);
			
			if(!isInitBattleInfo(role)){
				return;
			}
			refAsyncArenaFilterBattleVip(role,false,false);
			
			Map<Integer, AsyncBattleInfo> asyncBattleInfoMap = roleBattleMap.get(role.getIntRoleId());
			if(asyncBattleInfoMap != null && !asyncBattleInfoMap.isEmpty()){
				if(asyncArenaRole == null){
					asyncArenaRole = new AsyncArenaRole();
					//初始化免费次数
					byte freeNum = GameContext.getAsyncArenaApp().freeNum();
					asyncArenaRole.setChallengeNum(freeNum);
					asyncArenaRole.setRoleId(role.getIntRoleId());
					asyncArenaRole.setRefTime(System.currentTimeMillis());
				}
				saveOrUpdRoleAsyncArena(role,asyncArenaRole);
			}
		}catch(Exception e){
			logger.error("initAutoRoleAsyncArena",e);
		}
	}

	@Override
	public AsyncArenaRole getRoleAsyncArenaInfo(RoleInstance role) {
		return roleAsyncArenaMap.get(role.getIntRoleId());
	}
	//【游戏币/潜能/钻石不足弹板】 判断
	@Override
	public RoleAsyncRefResult refValidator(RoleInstance role) {
		RoleAsyncRefResult result = new RoleAsyncRefResult();
		
		AsyncArenaRole asyncArenaRole = roleAsyncArenaMap.get(role.getIntRoleId());
		
		//获得刷新本次需要花费
		AsyncRefresh refresh = GameContext.getAsyncArenaApp().getAsyncRefresh((byte)-1, (byte)(asyncArenaRole.getRefNum() + 1));
		if(refresh == null){
			//提示刷新次数已达上限
			result.setInfo(GameContext.getI18n().messageFormat(TextId.ASYNC_ARENA_ROLE_REF_COUNT_ERR));
			return result ;
		}
		
		int price = refresh.getPrice();
		if(price > 0) {
			//【游戏币/潜能/钻石不足弹板】 判断
			Result ar = GameContext.getUserAttributeApp().getEnoughResult(role, AttributeType.goldMoney, price);
			if(ar.isIgnore()){
				result.setIgnore(true);
				return result;
			}
			if(!ar.isSuccess()){
				//提示钻石不足
				result.setInfo(GameContext.getI18n().messageFormat(TextId.ASYNC_ARENA_ROLE_REF_CONSUME_MONEY, price));
				return result ;
			}
		}
		
		//扣除消耗
		GameContext.getUserAttributeApp().changeRoleMoney(role, AttributeType.goldMoney, 
				OperatorType.Decrease, price, OutputConsumeType.async_arena_role_ref_consume_money);
		role.getBehavior().notifyAttribute();
		
		asyncArenaRole.setRefNum((byte)(asyncArenaRole.getRefNum() + 1));
		//全校验完毕调这个方法
		refAsyncArenaFilterBattleVip(role,true,true);
		
		refresh = GameContext.getAsyncArenaApp().getAsyncRefresh((byte)-1, (byte)(asyncArenaRole.getRefNum() + 1));
		if(refresh != null){
			result.setPrice(refresh.getPrice());
		}
		
		List<AsyncArenaTargetItem> targetList = getAsyncArenaTargetItemList(role);
		if(targetList == null){
			new ArrayList<AsyncArenaTargetItem>();
		}
		
		saveOrUpdRoleAsyncArena(role,asyncArenaRole);
		
		result.setTargetItem(targetList);
//		result.setInfo(GameContext.getI18n().messageFormat(TextId.ASYNC_ARENA_ROLE_REF_SUCCESS));
		result.setInfo("");
		result.success();
		return result;
	}
	
	
	/**
	 * 角色异步竞技场数据更新
	 */
	@Override
	public void saveOrUpdRoleAsyncArena(RoleInstance role,AsyncArenaRole asyncArenaRole){
		try{
			validRoleBattltNum(role);
			Map<Integer, AsyncBattleInfo> asyncBattleInfoMap = roleBattleMap.get(asyncArenaRole.getRoleId());
			asyncArenaRole.setTargetData(buildData(asyncBattleInfoMap));
			roleAsyncArenaMap.put(asyncArenaRole.getRoleId(), asyncArenaRole);
			//数据库
			GameContext.getBaseDAO().saveOrUpdate(asyncArenaRole);
			GameContext.getRoleAsyncArenaStorage().saveRoleAsyncArena(String.valueOf(asyncArenaRole.getRoleId()), asyncArenaRole.getHistoryHonor()+asyncArenaRole.getNowHonor());
		}catch(Exception e){
			logger.error("saveOrUpdRoleAsyncArena is error",e);
		}
	}
	
	/**
	 * 角色异步竞技场数据初始化
	 */
	@Override
	public void initRoleAsyncArena(RoleInstance role){
		try{
			if(!roleAsyncArenaMap.containsKey(role.getIntRoleId())){
				if(GameContext.getMenuApp().isOpenFun(role, MenuIdType.Active)){
					initRoleAsyncArenaMap(role);
				}
			}
		}catch(Exception ex){
			logger.error(ex.getMessage());
		}
	}
	
	private void initRoleAsyncArenaMap(RoleInstance role){
		try{
			// 初始化角色数据
			AsyncArenaRole asyncArenaRole = GameContext.getBaseDAO().selectEntity(AsyncArenaRole.class, AsyncArenaRole.ROLE_ID,role.getIntRoleId());
			if(asyncArenaRole != null){
				Map<Integer,AsyncBattleInfo> asyncBattleInfoMap = parseData(asyncArenaRole.getTargetData());
				roleBattleMap.put(asyncArenaRole.getRoleId(),asyncBattleInfoMap);
				roleAsyncArenaMap.put(asyncArenaRole.getRoleId(),asyncArenaRole);
			}
			initAutoRoleAsyncArena(role,asyncArenaRole);
			if(asyncArenaRole != null){
				//存排行榜
				GameContext.getRoleAsyncArenaStorage().saveRoleAsyncArena(role.getRoleId(), asyncArenaRole.getHistoryHonor()+asyncArenaRole.getNowHonor());
			}
		}catch(Exception e){
			logger.error("initRoleAsyncArenaMap",e);
		}
	}
	
	@Override
	public void challengeOver(RoleInstance role, AsyncPvpBattleInfo battleInfo, ChallengeResultType type) {
		if(null == role) {
			return ;
		}
		AsyncArenaRole asyncArenaRole = roleAsyncArenaMap.get(role.getIntRoleId());
		try {
			if(null == battleInfo) {
				return ;
			}
			Map<Integer,AsyncBattleInfo> asyncBattleInfoMap = roleBattleMap.get(role.getIntRoleId());
			AsyncBattleInfo targetBattleInfo = asyncBattleInfoMap.get(Integer.parseInt(battleInfo.getTargetRoleId()));
			if(targetBattleInfo != null){
				targetBattleInfo.setState((byte)1);
				targetBattleInfo.setCTime(System.currentTimeMillis());
				if(type == ChallengeResultType.Win) {
					targetBattleInfo.setIsSu((byte)1);
				}
				if(type == ChallengeResultType.Lose) {
					targetBattleInfo.setIsSu((byte)0);
				}
			}
			int honor = 0;
			AsyncReward aysncReward = null;
			AsyncArenaRewardItem item = new AsyncArenaRewardItem();
			//胜利
			if(type == ChallengeResultType.Win) {
				if(asyncArenaRole.getSuccessNum() >=3){
					honor =  + 100 + Math.min(100,10*(asyncArenaRole.getSuccessNum()-2));
				}else{
					honor = 100;
				}
				asyncArenaRole.setNowHonor(honor + asyncArenaRole.getNowHonor());
				asyncArenaRole.setSuccessNum((byte)(asyncArenaRole.getSuccessNum() + 1));
				//获取奖励
				aysncReward = GameContext.getAsyncArenaApp().getAsyncRewardByRoleLevel(role.getLevel()+Cat.underline+1);
				GameContext.getUserAttributeApp().changeRoleMoney(role, AttributeType.gameMoney, OperatorType.Add, aysncReward.getGoldMoney(), OutputConsumeType.async_arena_role_add_money);
				GameContext.getUserAttributeApp().changeAttribute(role, AttributeType.exp,OperatorType.Add , aysncReward.getExp(), OutputConsumeType.async_arena_role_add_exp);
				GameContext.getUserAttributeApp().changeAttribute(role, AttributeType.potential,OperatorType.Add , aysncReward.getZp(), OutputConsumeType.async_arena_role_add_potential);
				GameContext.getUserAttributeApp().changeAttribute(role, AttributeType.honor,OperatorType.Add , aysncReward.getHonor(), OutputConsumeType.async_arena_role_add_honor);
				item.setSuccess((byte)1);
			}
			//失败
			if(type == ChallengeResultType.Lose) {
				honor = (int)(asyncArenaRole.getNowHonor()*0.2f);
				if(asyncArenaRole.getNowHonor() >=  (int)(asyncArenaRole.getNowHonor()*0.2f)){
					asyncArenaRole.setNowHonor(asyncArenaRole.getNowHonor() - (int)(asyncArenaRole.getNowHonor()*0.2f));
				}else{
					asyncArenaRole.setNowHonor(0);
				}
				asyncArenaRole.setSuccessNum((byte)0);
				//获取奖励
				aysncReward = GameContext.getAsyncArenaApp().getAsyncRewardByRoleLevel(role.getLevel()+Cat.underline+0);
				GameContext.getUserAttributeApp().changeRoleMoney(role, AttributeType.gameMoney, OperatorType.Add, aysncReward.getGoldMoney(), OutputConsumeType.async_arena_role_add_money);
				GameContext.getUserAttributeApp().changeAttribute(role, AttributeType.exp,OperatorType.Add , aysncReward.getExp(), OutputConsumeType.async_arena_role_add_exp);
				GameContext.getUserAttributeApp().changeAttribute(role, AttributeType.potential,OperatorType.Add , aysncReward.getZp(), OutputConsumeType.async_arena_role_add_potential);
				GameContext.getUserAttributeApp().changeAttribute(role, AttributeType.honor,OperatorType.Add , aysncReward.getHonor(), OutputConsumeType.async_arena_role_add_honor);
				item.setSuccess((byte)0);
			}
			role.getBehavior().notifyAttribute();
			saveOrUpdRoleAsyncArena(role,asyncArenaRole);
			C2622_RoleAsyncArenaSummarizeRespMessage respMsg = new C2622_RoleAsyncArenaSummarizeRespMessage();
			
			item.setAddScore((short)asyncArenaRole.getNowHonor());
			item.setExp(aysncReward.getExp());
			item.setGoldMoney(aysncReward.getGoldMoney());
			item.setPrestige(aysncReward.getHonor());
			item.setSuccessNum(asyncArenaRole.getSuccessNum());
			respMsg.setItem(item);
			role.getBehavior().sendMessage(respMsg);
		}catch(Exception e){
			logger.error("RoleAsyncArenaApp.challengeOver error: ",e);
		}
	}
	
	public Map<Integer,AsyncBattleInfo> parseData(byte [] targetData) {
		Map<Integer,AsyncBattleInfo> asyncBattleInfoMap = new HashMap<Integer,AsyncBattleInfo>();
		try {
			asyncBattleInfoMap = Util.deserialization(targetData,Map.class);
			for(Entry<Integer,AsyncBattleInfo> battleInfo : asyncBattleInfoMap.entrySet()){
				String jsonStr = JSON.toJSONString(battleInfo.getValue());
				battleInfo.setValue(JSON.parseObject(jsonStr,AsyncBattleInfo.class));
			}
			return asyncBattleInfoMap;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return asyncBattleInfoMap;
	}


	public byte [] buildData(Map<Integer,AsyncBattleInfo> asyncBattleInfoMap) {
		try {
			return Util.serialization(asyncBattleInfoMap);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	
	}

	@Override
	public void challenge(RoleInstance role, int targetRoleId) {
		try {
			AsyncPvpBattleInfo info = new AsyncPvpBattleInfo();
			info.setRoleId(role.getRoleId());
			info.setRoleName(role.getRoleName());
			info.setTargetRoleId(String.valueOf(targetRoleId));
			AsyncPvpRoleAttr attr = GameContext.getAsyncPvpApp().getAsyncPvpRoleAttr(String.valueOf(targetRoleId));
			info.setTargetRoleName(attr.getRoleName());
			
			GameContext.getAsyncPvpApp().addAsyncPvpBattleInfo(info);
			//切换地图 
			AsyncMap map = GameContext.getAsyncArenaApp().getAsyncMap();
			Point targetPoint = new Point(map.getMapId(), map.getMapX(), map.getMapY());
			GameContext.getUserMapApp().changeMap(role, targetPoint);
		} catch (Exception ex) {
			logger.error("AsyncArenaAppImpl.challenge error", ex);
		}
	}
	
	/**
	 * 是否可以挑战
	 * @param role
	 * @param targetRoleId
	 * @return
	 */
	@Override
	public Result isChallenge(RoleInstance role, int targetRoleId){
		Result result = new Result();
		if(role.getMapInstance().getMap().getMapConfig().getMapLogicType() == MapLogicType.asyncArena){
			return result.setInfo(GameContext.getI18n().getText(TextId.ASYNC_ARENA_CHALLENGE_NOTIN_ERROR));	
		}

		if(roleAsyncArenaMap.containsKey(role.getIntRoleId())){
			AsyncArenaRole asyncArenaRole = roleAsyncArenaMap.get(role.getIntRoleId());
			if(asyncArenaRole != null){
				if(asyncArenaRole.getChallengeNum() <= 0){
					return result.setInfo(GameContext.getI18n().getText(TextId.ASYNC_ARENA_CHALLENGE_COUNT));
				}
			}else{
				return result.setInfo(GameContext.getI18n().getText(TextId.ASYNC_ARENA_CHALLENGE_ERROR));
			}
		}
		
		Map<Integer, AsyncBattleInfo> battleInfoMap = roleBattleMap.get(role.getIntRoleId());
		if(battleInfoMap != null && !battleInfoMap.isEmpty()){
			if(battleInfoMap.containsKey(targetRoleId)){
				AsyncBattleInfo battleInfo = battleInfoMap.get(targetRoleId);
				if(battleInfo.getIsSu() == (byte)1){
					return result.setInfo(GameContext.getI18n().getText(TextId.ASYNC_ARENA_CHALLENGE_ISSU));
				}
			}else{
				return result.setInfo(GameContext.getI18n().getText(TextId.ASYNC_ARENA_CHALLENGE_ERROR));
			}
		}
		GameContext.getRoleAsyncArenaApp().challenge(role,  targetRoleId);

		result.isSuccess();
		return result;
	}
	//消耗不足出现弹板
	@Override
	public C2627_AsyncArenaBuyNumRespMessage buyChallengeNum(RoleInstance role) {
		C2627_AsyncArenaBuyNumRespMessage respMsg = new C2627_AsyncArenaBuyNumRespMessage();
		
		
		AsyncArenaRoleBuyChallengeItem result = new AsyncArenaRoleBuyChallengeItem();
		//角色数据 
		AsyncArenaRole asyncArenaRole = GameContext.getRoleAsyncArenaApp().getRoleAsyncArenaInfo(role);
		
		//免费次数数据
		byte freeNum = GameContext.getAsyncArenaApp().freeNum();
		//暂时没有VIP系统先填-1
		AsyncBuy asyncBuy = GameContext.getAsyncArenaApp().getAsyncBuy((byte)-1,(byte)(asyncArenaRole.getMoneyNum()+freeNum+1));
		
		//旧数据
		AsyncBuy oldAsyncBuy = GameContext.getAsyncArenaApp().getAsyncBuy((byte)-1,(byte)(asyncArenaRole.getMoneyNum()+freeNum));
		
		if(asyncBuy == null){
			//提示游戏币不足
			result.setNum(asyncArenaRole.getChallengeNum());
			result.setExpenditure(oldAsyncBuy.getPrice());
			result.setMsg(GameContext.getI18n().messageFormat(TextId.ASYNC_ARENA_BUY_CHALLENGE_COUNT_VIP));
			respMsg.setItem(result);
			return respMsg ;
		}
		int goldMoney = asyncBuy.getPrice();
		if(goldMoney > 0) {
			//【游戏币/潜能/钻石不足弹板】 判断
			Result ar = GameContext.getUserAttributeApp().getEnoughResult(role, AttributeType.goldMoney, goldMoney);
			if(ar.isIgnore()){//弹板
				return null;
			}
			if(!ar.isSuccess()){//不足
				//提示游戏币不足
				result.setNum(asyncArenaRole.getChallengeNum());
				result.setExpenditure(oldAsyncBuy.getPrice());
				result.setMsg(GameContext.getI18n().messageFormat(TextId.ASYNC_ARENA_BUY_CHALLENGE_COUNT_FAIL, goldMoney));
				respMsg.setItem(result);
				return respMsg ;
			}
		}
		
		//扣除消耗
		GameContext.getUserAttributeApp().changeRoleMoney(role, AttributeType.goldMoney, 
				OperatorType.Decrease, goldMoney, OutputConsumeType.async_arena_role_consume_money);
		role.getBehavior().notifyAttribute();
		
		//购买次数
		asyncArenaRole.setChallengeNum((byte)(asyncArenaRole.getChallengeNum() + 1));
		asyncArenaRole.setMoneyNum((byte)(asyncArenaRole.getMoneyNum() + 1));
		
		saveOrUpdRoleAsyncArena(role,asyncArenaRole);
		
		result.setNum(asyncArenaRole.getChallengeNum());
		result.setExpenditure(asyncBuy.getPrice());
		result.setMsg(GameContext.getI18n().messageFormat(TextId.ASYNC_ARENA_BUY_CHALLENGE_COUNT_SUCCESS, goldMoney));
		
		respMsg.setItem(result);
		return respMsg;
	}

	@Override
	public void dailyUpdateAsyncArenaAll() {
		lock.lock();
		try{
			for(Entry<Integer,AsyncArenaRole> asyncArenaRole : roleAsyncArenaMap.entrySet()) {
				RoleInstance role = GameContext.getOnlineCenter().getRoleInstanceByRoleId(String.valueOf(asyncArenaRole.getKey()));
				if(role != null){
					byte freeNum = GameContext.getAsyncArenaApp().freeNum();
					asyncArenaRole.getValue().setChallengeNum(freeNum);
					asyncArenaRole.getValue().setMoneyNum((byte)0);
					asyncArenaRole.getValue().setRefNum((byte)0);
					asyncArenaRole.getValue().setRefTime(System.currentTimeMillis());
					int historyHonor = asyncArenaRole.getValue().getNowHonor() + asyncArenaRole.getValue().getHistoryHonor();
					asyncArenaRole.getValue().setHistoryHonor(historyHonor);
					asyncArenaRole.getValue().setNowHonor(0);
					initAutoRoleAsyncArena(role,asyncArenaRole.getValue());
				}
			}
		}catch(Exception e){
			logger.error("dailyUpdateAsyncArena err",e);
		}finally{
			 lock.unlock();
		}
	}
	
	@Override
	public void dailyUpdateAsyncArenaByRole(AsyncArenaRole asyncArenaRole) {
		try{
			byte freeNum = GameContext.getAsyncArenaApp().freeNum();
			asyncArenaRole.setChallengeNum(freeNum);
			asyncArenaRole.setMoneyNum((byte)0);
			asyncArenaRole.setRefNum((byte)0);
			asyncArenaRole.setRefTime(System.currentTimeMillis());
			asyncArenaRole.setIsReward((byte)0);
			int historyHonor = asyncArenaRole.getNowHonor() + asyncArenaRole.getHistoryHonor();
			asyncArenaRole.setHistoryHonor(historyHonor);
			asyncArenaRole.setSuccessNum((byte)0);
			asyncArenaRole.setNowHonor(0);
		}catch(Exception e){
			logger.error("dailyUpdateAsyncArena err",e);
		}
	}

	@Override
	public RoleAsyncRankRewardResult rewardRank(RoleInstance role) {
		
		//剩余时间
		Date date = DateUtil.getDateEndTime(new Date(System.currentTimeMillis()));
		int min = DateUtil.dateDiffSecond(new Date(System.currentTimeMillis()),date);
		
		AsyncArenaRole asyncArenaRole = 	getRoleAsyncArenaInfo(role);
		RoleAsyncRankRewardResult result = new RoleAsyncRankRewardResult();
		result.setTime(GameContext.getI18n().getText(TextId.ASYNC_ARENA_ROLE_RESET_INFO));
		if(asyncArenaRole.getIsReward() == (byte)1){
			result.setMsg(GameContext.getI18n().messageFormat(TextId.ASYNC_ARENA_RANK_REWARD_ERROR));
			return result;
		}
		
		boolean flag = sendRankAward(role,asyncArenaRole.getHistoryRanking());
		if(flag){
			asyncArenaRole.setIsReward((byte)1);
			saveOrUpdRoleAsyncArena(role,asyncArenaRole);
		}else{
			result.setMsg(GameContext.getI18n().messageFormat(TextId.ASYNC_ARENA_RANK_REWARD_FAIL));
			return result;
		}
		result.success();
		return result;
	}
	
	/**
	 * 发送排行奖励
	 * @param role
	 * @param lbpItem
	 * @return
	 */
	private boolean sendRankAward(RoleInstance role, int ranking) {
		List<AsyncRankReward> asyncRankRewardList =  GameContext.getAsyncArenaApp().getAsyncRankRewardList();
		List<GoodsOperateBean> goodsOperateBeanList = new ArrayList<GoodsOperateBean>();
		AsyncRankReward rankReward = null;
		if(asyncRankRewardList != null && !asyncRankRewardList.isEmpty()){
			for(AsyncRankReward reward : asyncRankRewardList){
				if(ranking >= reward.getMinRank() && ranking <= reward.getMaxRank()){
					rankReward = reward;
					break;
				}
			}
		}

		if(rankReward != null){
			String [] goodsId = rankReward.getGoodsId().split(",");
			String [] goodsNum = rankReward.getGoodsNum().split(",");
			String [] goodsBind = rankReward.getBindType().split(",");
			if(goodsId != null && goodsId.length > 0){
				int i=0;
				for(;i<goodsId.length;i++){
					GoodsBase goodsBase = GameContext.getGoodsApp().getGoodsBase(Integer.parseInt(goodsId[i]));
					GoodsOperateBean bean = new GoodsOperateBean();
					bean.setGoodsId(goodsBase.getId());
					bean.setGoodsNum(Short.parseShort(goodsNum[i]));
					bean.setBindType(BindingType.get(Byte.parseByte(goodsBind[i])));
					goodsOperateBeanList.add(bean);
				}
			}
			if(goodsOperateBeanList != null && !goodsOperateBeanList.isEmpty()){
				//直接发会弹物品
				AddGoodsBeanResult goodsResult = GameContext.getUserGoodsApp().addSomeGoodsBeanForBag(role, goodsOperateBeanList,OutputConsumeType.async_arena_role_rank_reward);
				// 背包满了则发邮件
				List<GoodsOperateBean> putFailureList = goodsResult.getPutFailureList();
				try {
					if(!Util.isEmpty(putFailureList)){
						String context = GameContext.getI18n().messageFormat(TextId.ASYNC_ARENA_RANK_REWARD_SEND);
						GameContext.getMailApp().sendMail(role.getRoleId(),
									MailSendRoleType.AsyncArena.getName(), 
									context,
									MailSendRoleType.AsyncArena.getName(), 
									OutputConsumeType.async_arena_role_rank_reward_mail
									.getType(),
									putFailureList);
					}
				} catch (Exception e) {
					logger.error("sendRankAward", e);
				}
			}
			
			List<AsyncGroupReward> groupRewardList = GameContext.getAsyncArenaApp().getAsyncGroupRewardList(rankReward.getGroupId());
			if(!Util.isEmpty(groupRewardList)){
				
				for(AsyncGroupReward group : groupRewardList){
					 OutputConsumeType outputConsumeType = null;
					if(AttributeType.gameMoney.ordinal() == group.getAttrType()){
						 outputConsumeType = OutputConsumeType.async_arena_role_add_money;
					}else if(AttributeType.exp.ordinal() == group.getAttrType()){
						 outputConsumeType = OutputConsumeType.async_arena_role_add_exp;
					}else if(AttributeType.potential.ordinal() == group.getAttrType()){
						 outputConsumeType = OutputConsumeType.async_arena_role_add_potential;
					}else if(AttributeType.honor.ordinal() == group.getAttrType()){
						 outputConsumeType = OutputConsumeType.async_arena_role_add_honor;
					}
					GameContext.getUserAttributeApp().changeRoleMoney(role, AttributeType.get(group.getAttrType()), OperatorType.Add, group.getAttrValue(), outputConsumeType);
				}
			}
			role.getBehavior().notifyAttribute();
			return true;
		}
		
		return false;
	}
	
	/**
	 * 获得排行奖励
	 * @param rank
	 * @return
	 */
	@Override
	public AsyncRankReward getAsyncRankReward(int rank){
		//获得排行榜奖励
		List<AsyncRankReward> asyncRankRewardList =  GameContext.getAsyncArenaApp().getAsyncRankRewardList();
		AsyncRankReward rankReward = null;
		if(asyncRankRewardList != null && !asyncRankRewardList.isEmpty()){
			for(AsyncRankReward reward : asyncRankRewardList){
				if(rank >= reward.getMinRank() && rank <= reward.getMaxRank()){
					rankReward = reward;
					break;
				}
			}
		}
		return rankReward;
	}
	
	/**
	 * 获得对战数据
	 * @return
	 */
	@Override
	public List<AsyncArenaTargetItem> getAsyncArenaTargetItemList(RoleInstance role){
		Map<Integer,AsyncBattleInfo> asyncBattleInfoMap = GameContext.getRoleAsyncArenaApp().getRoleAsyncBattleInfo(role);
		List<AsyncArenaTargetItem> targetList = new ArrayList<AsyncArenaTargetItem>();
		for(Entry<Integer,AsyncBattleInfo> battleInfo : asyncBattleInfoMap.entrySet()){
			//对战方数据
			AsyncArenaTargetItem targetItem = new AsyncArenaTargetItem();
			AsyncPvpRoleAttr attr = GameContext.getAsyncPvpApp().getAsyncPvpRoleAttr(String.valueOf(battleInfo.getKey()));
			targetItem.setRoleId(Integer.parseInt(attr.getRoleId()));
			targetItem.setBattleScore(attr.getBattleScore());
			byte q = AsyncArenaBattleScoreType.getQuality(role.getBattleScore(), attr.getBattleScore());
			targetItem.setQuality(q);
			targetItem.setLevel(attr.getLevel());
			targetItem.setRoleName(attr.getRoleName());
			targetItem.setResId(attr.getHeroHeadId());
			targetItem.setState(battleInfo.getValue().getState());
			targetItem.setIsSu(battleInfo.getValue().getIsSu());
			
			targetList.add(targetItem);
		}
		return targetList;
	}

	@Override
	public void validRoleBattltNum(RoleInstance role) {
		Map<Integer,AsyncBattleInfo> asyncBattleInfoMap = getRoleAsyncBattleInfo(role);
		if(asyncBattleInfoMap != null && asyncBattleInfoMap.size() < NUM){
			GameContext.getRoleAsyncArenaApp().refAsyncArenaFilterBattleVip(role, true, false);
		}
	}
	
	@Override
	public List<TargetItem> getTargetItemList (int targetRoleId){
		
		List<TargetItem> itemList = new ArrayList<TargetItem>();
		
		List<RoleHero> targetHeroList = GameContext.getHeroApp().getRoleSwitchableHeroList(String.valueOf(targetRoleId));
		
		if(!Util.isEmpty(targetHeroList)){
			for(RoleHero roleHero : targetHeroList){
				GoodsBase hero = GameContext.getGoodsApp().getGoodsBase(roleHero.getHeroId());
				if(hero == null){
					continue;
				}
				TargetItem item = new TargetItem();		
				item.setName(hero.getName());
				item.setLevel(roleHero.getLevel());
				item.setResId(hero.getImageId());
				item.setQuality(roleHero.getQuality());
				item.setStar(roleHero.getStar());
				itemList.add(item);
			}
		}
		
		return itemList;
	}
	
}
