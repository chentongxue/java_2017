package com.game.draco.app.unionbattle;

import java.text.MessageFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import lombok.Getter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sacred.alliance.magic.app.active.vo.Active;
import sacred.alliance.magic.app.goods.GoodsOperateBean;
import sacred.alliance.magic.base.OperatorType;
import sacred.alliance.magic.base.OutputConsumeType;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.core.exception.ServiceException;
import sacred.alliance.magic.domain.GoodsBase;
import sacred.alliance.magic.util.DateUtil;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.vo.Point;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.mail.domain.MailAttriBean;
import com.game.draco.app.mail.type.MailSendRoleType;
import com.game.draco.app.team.LeaveTeam;
import com.game.draco.app.union.FunType;
import com.game.draco.app.union.domain.Union;
import com.game.draco.app.union.domain.UnionMember;
import com.game.draco.app.union.domain.auction.Auction;
import com.game.draco.app.union.domain.auction.GoodsItem;
import com.game.draco.app.unionbattle.config.UnionIntegral;
import com.game.draco.app.unionbattle.config.UnionIntegralMail;
import com.game.draco.app.unionbattle.config.UnionIntegralNpc;
import com.game.draco.app.unionbattle.config.UnionIntegralRewGroup;
import com.game.draco.app.unionbattle.config.UnionIntegralReward;
import com.game.draco.app.unionbattle.domain.UnionIntegralInfo;
import com.game.draco.app.unionbattle.domain.UnionIntegralRank;
import com.game.draco.app.unionbattle.domain.UnionIntegralState;
import com.game.draco.app.unionbattle.type.IntegralBattleStateType;
import com.game.draco.app.unionbattle.type.IntegralBattleSuccessType;
import com.game.draco.message.push.C0004_TipTitleNotifyMessage;
import com.game.draco.message.push.C2549_UnionIntegralJoinStateNotifyMessage;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

public class UnionIntegralBattleAppImpl implements UnionIntegralBattleApp{
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	//<轮数，List<公会对战记录>>
	@Getter
	private Map<Integer,List<UnionIntegralState>> stateMap = Maps.newConcurrentMap();
	
	//<公会ID,公会积分>
	@Getter
	private Map<String,UnionIntegralRank> integralMap = Maps.newConcurrentMap();
	
	//<公会ID,目标NPCId>
	@Getter
	private Map<String,String> targetMap = Maps.newConcurrentMap();
	
	//场次奖励计数
	@Getter
	private volatile UnionIntegralInfo unionIntegralInfo;
	
	private UnionIntegral getIntegral(){
		return GameContext.getUnionIntegralBattleDataApp().getIntegral();
	}
	
	@Override
	public boolean isTimeOpen(){
		Active active = GameContext.getActiveApp().getActive(getIntegral().getActiveId());
		if(null != active){
			return active.isTimeOpen();
		}
		return false;
	}

	@Override
	public List<UnionIntegralState> getUnionIntegralStateRecordList(int round) {
		
		List<UnionIntegralState> list = stateMap.get(round);
		
		if(Util.isEmpty(list)){
			return null;
		}
		Collections.sort(list,stateRecordComparator);
		return list;
	}
	
	Comparator<UnionIntegralState> stateRecordComparator = new Comparator<UnionIntegralState>(){
		@Override
		public int compare(UnionIntegralState h1, UnionIntegralState h2) {
			if(h1.getRound() < h2.getRound()){
				return -1;
			}
			if(h1.getRound() > h2.getRound()){
				return 1;
			}
			if(h1.getGroupId() < h2.getGroupId()){
				return -1;
			}
			if(h1.getGroupId() > h2.getGroupId()){
				return 1;
			}
			if(h1.getGrid() < h2.getGrid()){
				return -1;
			}
			if(h1.getGrid() > h2.getGrid()){
				return 1;
			}
			return 0;
		}
	} ;
	
	Comparator<UnionIntegralRank> rankComparator = new Comparator<UnionIntegralRank>(){
		@Override
		public int compare(UnionIntegralRank h1, UnionIntegralRank h2) {
			Union union = GameContext.getUnionApp().getUnion(h1.getUnionId());
			Union union2 = GameContext.getUnionApp().getUnion(h2.getUnionId());
			
			if(union == null){
				return 0;
			}
			
			if(union2 == null){
				return 0;
			}
			
			if(h1.getIntegral() > h2.getIntegral()){
				return -1;
			}
			if(h1.getIntegral() < h2.getIntegral()){
				return 1;
			}
			if(union.getUnionLevel() > union2.getUnionLevel()){
				return -1;
			}
			if(union.getUnionLevel() < union2.getUnionLevel()){
				return 1;
			}
			if(union.getCreateTime() < union2.getCreateTime()){
				return -1;
			}
			if(union.getCreateTime() > union2.getCreateTime()){
				return 1;
			}
			return 0;
		}
	} ;
	
	/**
	 * 添加公会积分
	 */
	@Override
	public void addUnionIntegral(String unionId,int integral,long resetTime,boolean isSaveDB) {
		UnionIntegralRank rank = null;
		if(integralMap.containsKey(unionId)){
			rank = integralMap.get(unionId);
		}else{
			rank = new UnionIntegralRank();
			rank.setUnionId(unionId);
			rank.setResetTime(resetTime);
		}
		
		rank.setOldIntegral(rank.getOldIntegral() + integral);
		
		Date now = new Date();
		if(now.getTime() >= rank.getResetTime()){
			rank.setIntegral(0);
			rank.setResetTime(getResetTime());
		}
		
		rank.setIntegral(rank.getIntegral() + integral);

		integralMap.put(unionId, rank);
		
		if(isSaveDB){
			updateIntegralRank(rank);
		}
	}
	
	private void initIntegralMap(UnionIntegralRank rank){
		Date now = new Date();
		if(now.getTime() >= rank.getResetTime()){
			rank.setIntegral(0);
			rank.setResetTime(getResetTime());
		}
		integralMap.put(rank.getUnionId(), rank);
	}
	
	private long getResetTime(){
		
		UnionIntegral base = getIntegral();
		
		Date now = new Date();
		Date date = new Date(DateUtil.weekRunTime(now,base.getClearBattle()));
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.set(Calendar.HOUR_OF_DAY, 1);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
	
		Date newDate = cal.getTime();
		if(cal.getTimeInMillis() < now.getTime()){
			newDate = DateUtil.addDayToDate(date,7);
			cal.setTime(newDate);
			cal.set(Calendar.HOUR_OF_DAY, 1);
			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.SECOND, 0);
			newDate = cal.getTime();
		}
		return cal.getTime().getTime();
	}
	
	/**
	 * 更新公会状态
	 */
	@Override
	public void updUnionIntegralState(int round,String unionId, byte state,byte integral,boolean isSaveDB) {
		if(!stateMap.containsKey(round)){
			return;
		}
		List<UnionIntegralState> list = stateMap.get(round);
		for(UnionIntegralState integralState : list){
			if(integralState.getUnionId().equals(unionId)){
				integralState.setState(state);
				integralState.setIntegral(integral);
				if(isSaveDB){
					updateIntegralState(integralState);
				}
				return;
			}
		}
	}
	
	private void addUnionIntegralState(int round,int grid,int groupId,String unionId,byte state,boolean isSaveDB) {
		List<UnionIntegralState> list = null;
		
		if(stateMap.containsKey(round)){
			list = stateMap.get(round);
			
		}else{
			list = Lists.newArrayList();
			stateMap.put(round, list);
		}
		UnionIntegralState integralState = new UnionIntegralState();
		integralState.setState(state);
		integralState.setGrid(grid);
		integralState.setGroupId(groupId);
		integralState.setRound(round);
		integralState.setUnionId(unionId);
		list.add(integralState);
		if(isSaveDB){
			updateIntegralState(integralState);
		}
	}
	
	/**
	 * 创建对战分组
	 */
	@Override
	public void createFightList() {
		try{
			resetIntegralState();
			
			//轮数清零
			this.unionIntegralInfo.setOverTime(0);
			
			UnionIntegral base = getIntegral();
			
			List<String> unionList = GameContext.getRankApp().getRankedUnionIds(base.getJoinNum());
			
			if(Util.isEmpty(unionList)){
				unionList = GameContext.getUnionApp().getUnionIdList(base.getJoinNum());
			}
			
			boolean isRandom = false;
			
			if(unionList.size() > 12){
				isRandom = true;
			}
			
			if(isRandom){
				packageList(unionList);
			}
			
			for(int r=0;r<base.getRound();r++){
				Collections.shuffle(unionList);  
				int grid = 0;
				int groupId = 0;
				
				if(!isRandom){
					packageList(unionList);
				}
				for(int i=0;i<unionList.size();i++){
					String unionId = unionList.get(i);
					if(i%3 == 0){
						grid = 0;
						groupId++;
					}
					grid++;
					addUnionIntegralState(r,grid,groupId,unionId,IntegralBattleStateType.nu.getType(),true);
				}
			}
		}catch(Exception e){
			logger.error("createFightList is error",e);
		}
	}
	
	private void packageList(List<String> unionList){
		UnionIntegral base = getIntegral();
		if(unionList.size() < base.getJoinNum()){
			int num = base.getJoinNum() - unionList.size();
			for(int i=0;i<num;i++){
				unionList.add(GameContext.getI18n().getText(TextId.UNION_INTEGRAL_FIGHT_BYE));
			}
		}
	}
	
	@Override
	public List<UnionIntegralRank> getUnionIntegralRankList() {
		List<UnionIntegralRank> list = Lists.newArrayList();
		for(Entry<String,UnionIntegralRank> map : integralMap.entrySet()){
			list.add(map.getValue());
		}
		Collections.sort(list,rankComparator);
		return list;
	}

	@Override
	public void notifyFight() {
		try{
			
			if(Util.isEmpty(stateMap)){
				return;
			}
			
			GameContext.getUnionIntegralBattleApp().getTargetMap().clear();
			
			UnionIntegral base = getIntegral();

			Active active = GameContext.getActiveApp().getActive(base.getActiveId());
			if(!active.isTimeOpen()){
				return;
			}
			
			long overTime = System.currentTimeMillis() + base.getBattleTime()*1000;
			//设置每轮结束时间
			unionIntegralInfo.setOverTime(overTime);
			
			if(getRound() +1 > base.getRound()){
				return;
			}
			
			int round = getRound();
			
			if(round == -1){
				return;
			}
			
			if(!stateMap.containsKey(getRound())){
				return;
			}
			
			Map<Integer,Set<String>> groupMap = getIntegralGroupMap(true);
			
			for(Entry<Integer,Set<String>> group : groupMap.entrySet()){
				boolean flag = false;
				if(group.getValue().size() <= 1){
					flag = true;
				}
				for(String unionId : group.getValue()){
					Union union = GameContext.getUnionApp().getUnion(unionId);
					if(union == null){
						continue;
					}
					if(flag){
						//改成轮空状态
						updUnionIntegralState(getRound(),unionId,IntegralBattleStateType.success.getType(),IntegralBattleStateType.success.getValue(),true);
						//加积分
						addUnionIntegral(unionId, IntegralBattleStateType.success.getValue(),0,true);
						
						alertByeNotify(unionId);
						
						break;
					}
					alertConfirm(group.getKey(),unionId);
				}
			}
				
		}catch(Exception e){
			logger.error("notifyFight",e);
		}
	}
	
	@Override
	public int getRound() {
		try {
			UnionIntegral base = getIntegral();
			Date now = new Date();
			for(int i=0;i<base.getRound();i++){
				Date startDate = base.getStartDateList().get(i);
				Date endDate = base.getEndDateList().get(i);
				if(now.getTime() >= startDate.getTime() && now.getTime() < endDate.getTime()){
					return i;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}
	
	/**
	 * 获得分组数据
	 * @return
	 */
	@Override
	public Map<Integer,Set<String>> getIntegralGroupMap(boolean flag){
		List<UnionIntegralState> stateList = stateMap.get(getRound());
		
		Map<Integer,Set<String>> groupMap = Maps.newHashMap();
		
		for(UnionIntegralState rank : stateList){
			Set<String> unionSet = null;
			if(groupMap.containsKey(rank.getGroupId())){
				unionSet = groupMap.get(rank.getGroupId());
				
			}else{
				unionSet = Sets.newHashSet();
				groupMap.put(rank.getGroupId(), unionSet);
			}
			if(flag){
				Union union = GameContext.getUnionApp().getUnion(rank.getUnionId());
				if(union == null){
					continue;
				}
			}
			unionSet.add(rank.getUnionId());
		}
		return groupMap;
	}
	
	
	
	/**
	 * 发送弹窗
	 * @param groupId
	 * @param unionId
	 */
	private void alertConfirm(int groupId,String unionId){
		Union union = GameContext.getUnionApp().getUnion(unionId);
		if(union == null){
			return;
		}
		
		for(UnionMember member : union.getUnionMemberList()){
			RoleInstance r = GameContext.getOnlineCenter().getRoleInstanceByRoleId(String.valueOf(member.getRoleId()));
			
			if(r == null || !r.hasUnion()){
				continue;
			}
			
			C2549_UnionIntegralJoinStateNotifyMessage confirmMsg = new C2549_UnionIntegralJoinStateNotifyMessage();
			r.getBehavior().sendMessage(confirmMsg);
		}
	}
	
	/**
	 * 发送弹窗 轮空通知
	 * @param groupId
	 * @param unionId
	 */
	private void alertByeNotify(String unionId){
		Union union = GameContext.getUnionApp().getUnion(unionId);
		if(union == null){
			return;
		}
		
		for(UnionMember member : union.getUnionMemberList()){
			RoleInstance r = GameContext.getOnlineCenter().getRoleInstanceByRoleId(String.valueOf(member.getRoleId()));
			
			if(r == null || !r.hasUnion()){
				continue;
			}
			
			C0004_TipTitleNotifyMessage confirmMsg = new C0004_TipTitleNotifyMessage();
			confirmMsg.setMsgContext(GameContext.getI18n().messageFormat(TextId.UNION_INTEGRAL_BYE_ALERT,IntegralBattleStateType.success.getValue()));
			r.getBehavior().sendMessage(confirmMsg);
		}
	}

	@Override
	public Result joinIntegralBattle(RoleInstance role) {
		Result result = new Result();
		
		try {
			
			if(!role.hasUnion()){
				return result.setInfo(GameContext.getI18n().getText(TextId.UNION_NOT));
			}
			
			UnionIntegral base = getIntegral();

			Active active = GameContext.getActiveApp().getActive(base.getActiveId());
			if(!active.isTimeOpen()){
				return result.setInfo(GameContext.getI18n().getText(TextId.UNION_INTEGRAL_ACTIVE_TIME_OUT));
			}
			
			if(this.unionIntegralInfo.getOverTime() < System.currentTimeMillis()){
				return result.setInfo(GameContext.getI18n().getText(TextId.UNION_INTEGRAL_ACTIVE_BATTLE_OVER));
			}
			
			UnionIntegralState integralState = getIntegralState(role);
			List<UnionIntegralState> integralList = getIntegralGroupInfoList(integralState.getGroupId());
			int nuSize = 0; 
			for(UnionIntegralState state : integralList){
				if(state.getState() == IntegralBattleStateType.nu.getType()){
					nuSize++;
				}
				if(state.getState() == IntegralBattleStateType.success.getType()){
					return result.setInfo(GameContext.getI18n().getText(TextId.UNION_INTEGRAL_ACTIVE_BATTLE_OVER));
				}
			}
			
			Map<Integer,Set<String>> groupMap = getIntegralGroupMap(true);
			Set<String> unionSet = groupMap.get(integralState.getGroupId());
			if(unionSet.size() <= 1){
				return result.setInfo(GameContext.getI18n().getText(TextId.UNION_INTEGRAL_BYE_OVER));
			}
			
			if(nuSize == 1){
				return result.setInfo(GameContext.getI18n().getText(TextId.UNION_INTEGRAL_ACTIVE_BATTLE_OVER));
			}
			
			if(role.getTeam() != null){
				role.getTeam().memberLeave(role, LeaveTeam.apply);
			}
			
			UnionIntegralNpc npc = GameContext.getUnionIntegralBattleDataApp().getIntegralNpc(integralState.getGrid());
			Point targetPoint = new Point(base.getMapId(), npc.getMapX(), npc.getMapY());
			GameContext.getUserMapApp().changeMap(role, targetPoint);
			} catch (ServiceException e) {
				e.printStackTrace();
			}
			
		return result;
	}

	/**
	 * 查看玩家所属信息
	 */
	@Override
	public UnionIntegralState getIntegralState(RoleInstance role) {
		if(!role.hasUnion()){
			return null;
		}
		
		List<UnionIntegralState> stateList = stateMap.get(getRound());
		
		if(Util.isEmpty(stateList)){
			return null;
		}
		
		for(UnionIntegralState integralState : stateList){
			if(integralState.getUnionId().equals(role.getUnionId())){
				return integralState;
			}
		}
		return null;
	}
	
	@Override
	public void resetIntegral(){
		Date now = new Date();
		for(Entry<String,UnionIntegralRank> integral : integralMap.entrySet()){
			if(now.getTime() >= integral.getValue().getResetTime()){
				integral.getValue().setIntegral(0);
				integral.getValue().setResetTime(getResetTime());
			}
		}
	}
	
	@Override
	public void delUnion(String unionId){
		integralMap.remove(unionId);
		GameContext.getBaseDAO().delete(UnionIntegralRank.class, "unionId", unionId);
	}
	
	@Override
	public void resetIntegralState(){
		GameContext.getBaseDAO().deleteAll(UnionIntegralState.class);
		stateMap.clear();
	}
	
	private void updateIntegralState(UnionIntegralState integralState){
		GameContext.getBaseDAO().saveOrUpdate(integralState);
	}
	
	private void updateIntegralRank(UnionIntegralRank integral){
		GameContext.getBaseDAO().saveOrUpdate(integral);
	}
	
	@Override
	public void awardIntegral() {
		List<UnionIntegralRank> list = GameContext.getUnionIntegralBattleApp().getUnionIntegralRankList();
		if(Util.isEmpty(list)){
			return;
		}
		
		int i = 0;
		for(UnionIntegralRank rank : list){
			i++;
			UnionIntegralReward reward = getIntegralReward(i,IntegralBattleSuccessType.week.getType());
			if(reward == null){
				continue;
			}
			List<UnionIntegralRewGroup> rewGroupList = GameContext.getUnionIntegralBattleDataApp().getIntegralRewGroupList(reward.getRewGroupId());
			reward(i,rank.getUnionId(),null,rewGroupList,false,false);
		}
		
	}
	
	/**
	 * 添加击杀指挥官物品到拍卖行
	 * @param unionId
	 * @param roleSet
	 * @param itemList
	 */
	private void addUnionReward(String unionId, Set<Integer> roleSet, List<GoodsItem> itemList) {
		if (Util.isEmpty(unionId) || Util.isEmpty(roleSet) || Util.isEmpty(itemList)) {
			return;
		}
		Auction auction = GameContext.getUnionAuctionApp().packagingAuction(unionId, (byte) -1, (byte) -1, itemList, roleSet);
		GameContext.getUnionAuctionApp().addAuctionGoods(unionId, auction);
	}
	
	@Override
	public void reward(int rank,String unionId,String instanceId,List<UnionIntegralRewGroup> rewGroupList,boolean valid,boolean isAuction){
		List<GoodsItem> itemList = Lists.newArrayList();
		List<GoodsOperateBean> goodsList = Lists.newArrayList();
		int rewDkp = 0;
		for(UnionIntegralRewGroup rew : rewGroupList){
			rewDkp += rew.getDkp();
			// 根据概率获得掉落物品
			GoodsItem item = new GoodsItem();
			GoodsBase goodsBase = GameContext.getGoodsApp().getGoodsBase(rew.getGoodsId());
			item.setGoodsInstanceId(GameContext.getGoodsApp().newGoodsInstanceId());
			item.setGoodsId(rew.getGoodsId());
			item.setGoodsBinded(goodsBase.getBindType());
			item.setGoodsNum((byte)rew.getGoodsNum());
			if(!isAuction){
				goodsList.add(new GoodsOperateBean(rew.getGoodsId(), rew.getGoodsNum(),goodsBase.getBindType()));
			}
			itemList.add(item);
		}

		if(!isAuction){
			sendRewardMail(rank,unionId,goodsList,rewDkp);
			return;
		}
		
		Set<Integer> roleSet = awardDkp(unionId,rewDkp,instanceId,valid);
		//添加到公会拍卖行
		addUnionReward(unionId, roleSet, itemList);
	}
	
	private void sendRewardMail(int rank,String unionId,List<GoodsOperateBean> goodsList,int dkp){
		try{
			Union union = GameContext.getUnionApp().getUnion(unionId);
			if(union == null){
				return;
			}
			for(UnionMember member : union.getUnionMemberList()){
				UnionIntegralMail mail = GameContext.getUnionIntegralBattleDataApp().getIntegralMailMap().get(IntegralBattleSuccessType.week.getType());
				MailAttriBean attriBean = new MailAttriBean();
				attriBean.setDkp(dkp);
				//异步发邮件
				GameContext.getMailApp().sendMailAsync(String.valueOf(member.getRoleId()), mail.getTitle(), MessageFormat.format(mail.getContent(),rank), 
						MailSendRoleType.System.getName(), OutputConsumeType.union_integral_battle_role_reward.getType(),goodsList,attriBean);
			}
		}catch(Exception e){
			logger.error("sendMail",e);
		}
	}
	
	/**
	 * 奖励击杀、召唤NPC公会DKP
	 * @param unionId
	 * @param dkp
	 */
	@Override
	public Set<Integer> awardDkp(String unionId,int dkp,String instanceId,boolean valid){
		Set<Integer> roleSet = Sets.newHashSet();
		Union union = GameContext.getUnionApp().getUnion(unionId);
		if(union == null){
			return roleSet;
		}
		for(UnionMember member : union.getUnionMemberList()){
			RoleInstance role = GameContext.getOnlineCenter().getRoleInstanceByRoleId(String.valueOf(member.getRoleId()));
			if(role == null){
				continue;
			}
			if(valid){
				if(!role.getMapInstance().getInstanceId().equals(instanceId)){
					continue;
				}
			}
			roleSet.add(role.getIntRoleId());
			GameContext.getUnionApp().changeMemberDkp(role, dkp, OperatorType.Add, FunType.integralRewardDkp, true);
		}
		return roleSet;
	}
	
	private UnionIntegralReward getIntegralReward(int rank,byte type){
		Map<Byte,List<UnionIntegralReward>> rewardMap = GameContext.getUnionIntegralBattleDataApp().getIntegralRewardMap();
		List<UnionIntegralReward> rewardList = rewardMap.get(type);
		for(UnionIntegralReward reward : rewardList){
			if(rank >= reward.getRankMin() && rank <= reward.getRankMax()){
				return reward;
			}
		}
		return null;
	}

	@Override
	public List<UnionIntegralState> getIntegralGroupInfoList(int groupId) {
		List<UnionIntegralState> stateList = stateMap.get(getRound());
		
		List<UnionIntegralState> groupList = Lists.newArrayList();
		if(Util.isEmpty(stateList)){
			return groupList;
		}
		for(UnionIntegralState integralState : stateList){
			if(integralState.getGroupId() == groupId){
				groupList.add(integralState);
			}
		}
		return groupList;
	}

	@Override
	public void initIntegralBattle() {
		
		unionIntegralInfo = new UnionIntegralInfo();
		
		List<UnionIntegralState> integralStateList = GameContext.getBaseDAO().selectAll(UnionIntegralState.class);
		if(!Util.isEmpty(integralStateList)){
			for(UnionIntegralState state : integralStateList){
				addUnionIntegralState(state.getRound(), state.getGrid(), state.getGroupId(), state.getUnionId(),state.getState(),false);
			}
		}
		
		List<UnionIntegralRank> integralRankList = GameContext.getBaseDAO().selectAll(UnionIntegralRank.class);
		if(!Util.isEmpty(integralRankList)){
			for(UnionIntegralRank rank : integralRankList){
				initIntegralMap(rank);
			}
		}
	}
	
	@Override
	public long getOverTime(){
		return unionIntegralInfo.getOverTime();
	}

	@Override
	public Map<String, UnionIntegralState> getIntegralGroupInfoMap(int groupId) {
		Map<String, UnionIntegralState> integralGroupInfoMap = Maps.newHashMap();
		List<UnionIntegralState> stateList = stateMap.get(getRound());
		
		for(UnionIntegralState integralState : stateList){
			if(integralState.getGroupId() == groupId){
				integralGroupInfoMap.put(integralState.getUnionId(),integralState);
			}
		}
		return integralGroupInfoMap;
	}

	@Override
	public void modifyTargetMap(String unionId, String npcId) {
		targetMap.put(unionId,npcId);
	}

	@Override
	public boolean inIntegtalActive(String unionId) {
		if(Util.isEmpty(unionId) ||Util.isEmpty(stateMap)){
			return false;
		}
		
		UnionIntegral integral = GameContext.getUnionIntegralBattleDataApp().getIntegral();
		Active active = GameContext.getActiveApp().getActive(integral.getActiveId());
		
		boolean isActive = false;

		if(active != null){
			isActive = active.isTimeOpen();
		}
		
		if(!isActive){
			return false;
		}
		
		boolean flag = false;
		for(Entry<Integer,List<UnionIntegralState>> integralStateMap : stateMap.entrySet()){
			for(UnionIntegralState integralState : integralStateMap.getValue()){
				if(integralState.getUnionId().equals(unionId)){
					flag = true;
					break;
				}
			}
			if(flag){
				break;
			}
		}
				
		if(isActive && flag){
			return true;
		}
		
		return false;
	}
	
}