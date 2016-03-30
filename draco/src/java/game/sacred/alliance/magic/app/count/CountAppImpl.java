package sacred.alliance.magic.app.count;

import java.util.Date;

import sacred.alliance.magic.app.active.vo.Active;
import sacred.alliance.magic.app.arena.ArenaType;
import sacred.alliance.magic.app.carnival.CarnivalType;
import sacred.alliance.magic.app.count.type.CountType;
import sacred.alliance.magic.base.AttributeType;
import sacred.alliance.magic.base.OutputConsumeType;
import sacred.alliance.magic.domain.RoleCount;
import sacred.alliance.magic.util.DateUtil;
import sacred.alliance.magic.util.Log4jManager;
import sacred.alliance.magic.util.LongTool;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.base.AppType;

public class CountAppImpl implements CountApp {

	
	@Override
	public void updateTaobao(RoleInstance role, short id, int num) {
		role.getRoleCount().updateTaobao(id, num);
		//处理淘宝排行榜
		GameContext.getRankApp().updateTaobao(role, id);
	}

	@Override
	public void setArgs(Object arg0) {

	}

	@Override
	public void start() {

	}

	@Override
	public void stop() {

	}
	/**
	 * 上线读取角色信息
	 */
	@Override
	public int onLogin(RoleInstance role, Object context) {
		RoleCount rc = GameContext.getBaseDAO().selectEntity(RoleCount.class, RoleCount.ROLE_ID, role.getRoleId());
		if(null == rc){
			rc = new RoleCount();
			rc.setRoleId(role.getRoleId());
			rc.setExistRecord(false);//标记数据库无此记录
			rc.setRole(role);
		}else{
			rc.setRole(role);
			rc.parseDataBase();
			rc.setExistRecord(true);
			rc.resetDay();
		}
		role.setRoleCount(rc);
		return 1;
	}

	
	
	@Override
	public int onLogout(RoleInstance role, Object context) {
		try {
			RoleCount rc = role.getRoleCount();
			/* 下线累计今天的在线时间, 上线如果不是同一天清除昨天的登录时间*/
			Date lastLoginTime = role.getLastLoginTime();
			Date now = new Date();
			if(DateUtil.sameDay(lastLoginTime, now)){
				int secondsAdd = DateUtil.dateDiffSecond(lastLoginTime, now);
				secondsAdd += rc.getRoleTimesToInt(CountType.ToDayOnlineTimeSeconds);
				rc.changeTimes(CountType.ToDayOnlineTimeSeconds,secondsAdd);
			}
			this.saveRoleCount(rc);
		} catch (Exception ex) {
			GameContext.getCountApp().offlineLog(role);
			Log4jManager.OFFLINE_ERROR_LOG.error(
					"countApp.offline error,roleId=" + role.getRoleId() + ",userId="
							+ role.getUserId(), ex);
			return 0;
		}
		
		return 1;
	}
	
	@Override
	public int onCleanup(String roleId, Object context) {
		return 0;
	}
	
	public void saveRoleCount(RoleCount rc){
		if(null == rc){
			return;
		}
		//必须调用
		rc.resetDay();
		rc.buildDatabase();
		if(rc.isExistRecord()){
			GameContext.getBaseDAO().update(rc);//
		}else{
			GameContext.getBaseDAO().insert(rc);
			rc.setExistRecord(true);
		}
	}
	
	@Override
	public void offlineLog(RoleInstance role) {
		try{
			RoleCount rc = role.getRoleCount();
			if(null == rc){
				return;
			}
			Log4jManager.OFFLINE_COUNT_DB_LOG.info(rc.getSelfInfo());
		}catch(Exception e){
		}
	}

	@Override
	public void updateRoleBuy(RoleInstance role,int buyValue, OutputConsumeType outputConsumeType){
		GameContext.getOperateActiveApp().onConsume(role, buyValue, outputConsumeType);
		/*
		//开服十天嘉年华
		GameContext.getCarnivalApp().roleDataCount(role, buyValue, 0, CarnivalType.Role_Consume);*/
	}

	@Override
	public void updateRolePay(RoleInstance chargeRole,int payValue){
		if(null == chargeRole){
			return ;
		}
		GameContext.getOperateActiveApp().onPay(chargeRole, payValue, OutputConsumeType.user_prepaid);
		/*
		if(null != chargeRole){
			//开服十天嘉年华
			GameContext.getCarnivalApp().roleDataCount(chargeRole, payValue, 0, CarnivalType.Role_Recharge);
		}*/
		GameContext.getVipApp().addDiamands(chargeRole.getIntRoleId(), payValue);
	}


	@Override
	public void incrArenaFail(RoleInstance role, ArenaType arenaType,int score) {
		if(null == role){
			return ;
		}
		role.getRoleArena().incrFail(arenaType);
		//添加竞技场积分
		if(score > 0){
			role.getRoleArena().incrScore(arenaType, score);
			GameContext.getArena1V1App().syncRealTimeData(role);
			
			if(GameContext.getArena1V1App().isAcitveTimes()){
				//十天嘉年华
				GameContext.getCarnivalApp().roleDataCount(role,score,
						role.getLevel(), CarnivalType.Role_Arena);
			}
		}
		
	}

	@Override
	public void incrArenaJoin(RoleInstance role, ArenaType arenaType) {
		if(null == role){
			return ;
		}
		role.getRoleArena().incrJoin(arenaType);
	}

	@Override
	public void incrArenaWin(RoleInstance role, ArenaType arenaType,int score) {
		if(null == role){
			return ;
		}
		role.getRoleArena().incrWin(arenaType);
		//添加竞技场积分
		if(score > 0){
			role.getRoleArena().incrScore(arenaType, score);
			GameContext.getArena1V1App().syncRealTimeData(role);
			
			if(GameContext.getArena1V1App().isAcitveTimes()){
				//十天嘉年华
				GameContext.getCarnivalApp().roleDataCount(role,score,
						role.getLevel(), CarnivalType.Role_Arena);
			}
		}
	}



	@Override
	public void receiveFlower(RoleInstance receiver, int count) {
		receiver.getRoleCount().updateFlowerNum(count);
	}


	@Override
	public boolean setAlchemyCount(RoleInstance role, byte alchemyNoBreakOutCount,
			String alchemyCountJsonStr) {
		if(alchemyNoBreakOutCount<0){
			return false;
		}
		RoleCount rc = role.getRoleCount();
		rc.changeTimes(CountType.AlchemyCountJsonStr,alchemyCountJsonStr);//setAlchemyCountJsonStr(alchemyCountJsonStr);
		rc.changeTimes(CountType.AlchemyNoBreakOutCount,alchemyNoBreakOutCount);//setAlchemyNoBreakOutCount(alchemyNoBreakOutCount);
		//saveRoleCount(rc);
		return true;
	}

	@Override
	public boolean setLuckyBoxTime(RoleInstance role, int leftTimes, Date luckyBoxLastOpenTime) {
		RoleCount rc = role.getRoleCount();
		rc.changeTimes(CountType.LuckyBoxRefreshTimes, leftTimes);//.setLuckyBoxRefreshTimes(leftTimes);
		rc.changeTimes(CountType.LuckyBoxLastOpenTime,luckyBoxLastOpenTime.getTime());//setLuckyBoxLastOpenTime(luckyBoxLastOpenTime);
		//saveRoleCount(rc);
		return true;
	}
	@Override
	public boolean setLuckyFirstUsed(RoleInstance role) {
		RoleCount rc = role.getRoleCount();
		rc.changeTimes(CountType.LuckyBoxUsedTimes, 1);//setLuckyBoxUsedTimes(1);
		//saveRoleCount(rc);
		return true;
	}
	@Override
	public boolean setLuckyBoxCount(RoleInstance role,
			String luckyBoxCountJsonStr, String luckyBoxPlaceJsonStr) {
		RoleCount rc = role.getRoleCount();
		rc.changeTimes(CountType.LuckyBoxCountJsonStr, luckyBoxCountJsonStr);//setLuckyBoxCountJsonStr(luckyBoxCountJsonStr);
		rc.changeTimes(CountType.LuckyBoxPlaceJsonStr,luckyBoxPlaceJsonStr);//setLuckyBoxPlaceJsonStr(luckyBoxPlaceJsonStr);
		//saveRoleCount(rc);
		return true;
	}

	@Override
	public boolean setAccumulateLoginCount(RoleInstance role, int accumulateLoginAwardReceiveDays,  int accumulateLoginDays) {
		if(accumulateLoginAwardReceiveDays < 0||accumulateLoginDays < 0){
			return false;
		}
		RoleCount rc = role.getRoleCount();
		rc.changeTimes(CountType.AccumulateLoginAwardReceiveDays, accumulateLoginAwardReceiveDays);//setAccumulateLoginAwardReceiveDays(accumulateLoginAwardReceiveDays);
		rc.changeTimes(CountType.AccumulateLoginDays,accumulateLoginDays);//setAccumulateLoginDays(accumulateLoginDays);
//		rc.setDayTime(new Date());
		//saveRoleCount(rc);
		return true;
	}

	public void setYesterDayOnlineTimeSeconds(RoleInstance role, int seconds){
		if(seconds <= 0){
			return;
		}
		RoleCount rc = role.getRoleCount();
		rc.changeTimes(CountType.YesterDayOnlineTimeSeconds, seconds);
	}

	@Override
	public void joinApp(RoleInstance role, AppType appType) {
		RoleCount rc = role.getRoleCount();
		long joinApp = rc.getRoleTimesToLong(CountType.JoinApp);//getJoinApp() ;
		joinApp = LongTool.setIndexValueOne(joinApp, appType.getType());
		rc.changeTimes(CountType.JoinApp,joinApp);//setJoinApp(joinApp);
	}
	
//	@Override
//	public void onHookExpDataReset(RoleCount rc) {
//		RoleInstance role = rc.getRole() ;
//		if(null == role){
//			return ;
//		}
//		//判断是否昨天
//		Date yesterday = DateUtil.addDayToDate(new Date(), -1);
//		int hookExp = rc.getTodayHookExp() ;
//		if(!DateUtil.sameDay(rc.getDayTime(), yesterday)){
//			//昨天以前的记录,昨天挂机获得的经验为0
//			hookExp = 0 ;
//		}
//		int maxHookExp = role.get(AttributeType.maxExpHook) ;
//		int exp =  maxHookExp-hookExp ;
//		if(exp <=0){
//			//不可追回
//			return ;
//		}
//		GameContext.getRecoveryApp().saveHungUpRecovery(role,exp,maxHookExp);
//	}
//
//	@Override
//	public void onJoinAppDataReset(RoleCount count) {
//		//调用一键找回接口
//		RoleInstance role = count.getRole() ;
//		if(null == role){
//			return ;
//		}
//		//判断是否昨天
//		Date yesterday = DateUtil.addDayToDate(new Date(), -1);
//		long appValue = count.getJoinApp() ;
//		if(!DateUtil.sameDay(count.getDayTime(), yesterday)){
//			//昨天以前的记录,昨天没有参加任何活动
//			appValue = 0 ;
//		}
//		int yesterdayWeek = DateUtil.getWeek(yesterday);
//		for(AppType appType : AppType.values()){
//			if(1 == LongTool.getIndexValue(appValue, appType.getType())){
//				//昨天已经参加此活动
//				continue ;
//			}
//			Active active = GameContext.getActiveApp().getOnlyOneActive(appType.getActiveType());
//			if(null == active){
//				continue ; 
//			}
//			//判断此活动昨天是否开启
//			if(!active.isWeekActive(yesterdayWeek)){
//				continue ;
//			}
//			switch(appType){
//			case boss_dps://5
//				GameContext.getRecoveryApp().saveBossKillRecovery(role, 1);
//				break ;
//			case camp_war :
//				GameContext.getRecoveryApp().saveCampBattleRecovery(role, 1);
//				break ;
//			case arena_1v1 ://7
//				GameContext.getRecoveryApp().saveArenaRecovery(role, 1);
//				break ;
//			case angel_chest:
//				GameContext.getRecoveryApp().saveAngelChestRecovery(role, 1);
//				break ;
//			}
//		}
//	} 
	
	@Override
	public void onJoinAppDataReset(RoleInstance role,long joinApp,Date date) {
		//调用一键找回接口
		if(null == role){
			return ;
		}
		//判断是否昨天
		Date yesterday = DateUtil.addDayToDate(new Date(), -1);
		long appValue = joinApp;
		if(!DateUtil.sameDay(date, yesterday)){
			//昨天以前的记录,昨天没有参加任何活动
			appValue = 0 ;
		}
		int yesterdayWeek = DateUtil.getWeek(yesterday);
		for(AppType appType : AppType.values()){
			if(1 == LongTool.getIndexValue(appValue, appType.getType())){
				//昨天已经参加此活动
				continue ;
			}
			Active active = GameContext.getActiveApp().getOnlyOneActive(appType.getActiveType());
			if(null == active){
				continue ; 
			}
			//判断此活动昨天是否开启
			if(!active.isWeekActive(yesterdayWeek)){
				continue ;
			}
			switch(appType){
			case boss_dps://5
				GameContext.getRecoveryApp().saveBossKillRecovery(role, 1);
				break ;
			case camp_war :
				GameContext.getRecoveryApp().saveCampBattleRecovery(role, 1);
				break ;
			case arena_1v1 ://7
				GameContext.getRecoveryApp().saveArenaRecovery(role, 1);
				break ;
			case angel_chest:
				GameContext.getRecoveryApp().saveAngelChestRecovery(role, 1);
				break ;
			}
		}
	}
}
