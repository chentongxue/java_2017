package sacred.alliance.magic.app.count;

import java.util.Date;

import sacred.alliance.magic.app.arena.ArenaType;
import sacred.alliance.magic.app.carnival.CarnivalType;
import sacred.alliance.magic.base.OutputConsumeType;
import sacred.alliance.magic.domain.RoleCount;
import sacred.alliance.magic.util.Log4jManager;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;

public class CountAppImpl implements CountApp {
	
	@Override
	public void incrRmQuestSubmitCount(RoleInstance role){
		//RoleCount roleCount = role.getRoleCount() ;
		/*if(RmQuestType.HuangBang == questType){
			roleCount.setQuestHuangbangCount(roleCount.getQuestHuangbangCount() + 1);
			return ;
		}
		if(RmQuestType.Faction == questType){
			roleCount.setQuestFactionCount(roleCount.getQuestFactionCount() + 1) ;
			return ;
		}*/
		//TODO:
	}

	

	@Override
	public void updateTreasureMap(RoleInstance role, byte qualityType) {
		role.getRoleCount().updateTreasureMap(qualityType);
		//处理藏宝图活动排行榜
		GameContext.getRankApp().updateTreasure(role, qualityType);
	}
	
	@Override
	public void updateTaobao(RoleInstance role, short id, int num) {
		role.getRoleCount().updateTaobao(id, num);
		//处理上古法阵活动排行榜
		GameContext.getRankApp().updateTaobao(role, id, num);
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
	public void loadRoleCount(RoleInstance role) {
		RoleCount rc = GameContext.getBaseDAO().selectEntity(RoleCount.class, "roleId", role.getRoleId());
		if(null == rc){
			rc = new RoleCount();
			rc.setRoleId(role.getRoleId());
			rc.setExistRecord(false);//标记数据库无此记录
		}else{
			rc.setExistRecord(true);
			rc.resetDay();
		}
		role.setRoleCount(rc);
	}

	
	@Override
	public void saveRoleCount(RoleInstance role) {
		RoleCount rc = role.getRoleCount();
		this.saveRoleCount(rc);
	}
	
	public void saveRoleCount(RoleCount rc){
		if(null == rc){
			return;
		}
		//必须调用
		rc.resetDay();
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
		/*GameContext.getActiveDiscountApp().updateFeeDiscount(role.getUserId(), buyValue, false, role.getChannelId(),outputConsumeType);
		//开服十天嘉年华
		GameContext.getCarnivalApp().roleDataCount(role, buyValue, 0, CarnivalType.Role_Consume);*/
	}

	@Override
	public void updateRolePay(RoleInstance chargeRole,int payValue){
		/*GameContext.getActiveDiscountApp().updateFeeDiscount(userId, payValue, true, chargeRole.getChannelId(), null);
		if(null != chargeRole){
			//开服十天嘉年华
			GameContext.getCarnivalApp().roleDataCount(chargeRole, payValue, 0, CarnivalType.Role_Recharge);
		}*/
		if(null == chargeRole){
			return ;
		}
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

/*	@Override
	public void incrArenaWin1vn(RoleInstance role) {
		if(null == role){
			return ;
		}
		role.getRoleArena().incrWin1vn();
		//活动排行榜
		//GameContext.getRankApp().updateArena1VN(role);
	}*/

	@Override
	public void incrDayFreeReborn(RoleInstance role) {
		role.getRoleCount().incrDayFreeReborn() ; 
	}

	@Override
	public void incrDayFreeTransport(RoleInstance role) {
		role.getRoleCount().incrDayFreeTransport() ;
	}

	@Override
	public void receiveFlower(RoleInstance receiver, int count) {
		receiver.getRoleCount().updateFlowerNum(count);
	}


	@Override
	public boolean addCompassRewardPoints(RoleInstance role, int points,
			int whichRound) {
		RoleCount rc = role.getRoleCount();
		int lastRound = rc.getWhichRound();
		if(whichRound!=lastRound){
			rc.resetCompassRewardPoints(points,whichRound);
		}else{
			rc.addCompassRewardPoints(points);
		}
		saveRoleCount(rc);
		return true;
	}

	@Override
	public boolean setAlchemyCount(RoleInstance role, byte alchemyNoBreakOutCount,
			String alchemyCountJsonStr) {
		if(alchemyNoBreakOutCount<0){
			return false;
		}
		RoleCount rc = role.getRoleCount();
		rc.setAlchemyCountJsonStr(alchemyCountJsonStr);
		rc.setAlchemyNoBreakOutCount(alchemyNoBreakOutCount);
		rc.setDayTime(new Date());
		saveRoleCount(rc);
		return true;
	}

	@Override
	public boolean setLuckyBoxCount(RoleInstance role, byte luckyBoxUsedTimes,
			String luckyBoxCountJsonStr, String luckyBoxPlaceJsonStr) {
		if(luckyBoxUsedTimes<0)
			return false;
		RoleCount rc = role.getRoleCount();
		rc.setLuckyBoxUsedTimes(luckyBoxUsedTimes);;
		rc.setLuckyBoxCountJsonStr(luckyBoxCountJsonStr);
		rc.setLuckyBoxPlaceJsonStr(luckyBoxPlaceJsonStr);
		rc.setDayTime(new Date());
		saveRoleCount(rc);
		return true;
	} 
}
