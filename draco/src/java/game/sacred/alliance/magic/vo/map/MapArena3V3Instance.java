package sacred.alliance.magic.vo.map;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import sacred.alliance.magic.app.arena.ArenaMatch;
import sacred.alliance.magic.app.arena.ArenaType;
import sacred.alliance.magic.app.arena.BattleResult;
import sacred.alliance.magic.app.arena.config.ScoreResult;
import sacred.alliance.magic.app.chest.ChestRefreshInfo;
import sacred.alliance.magic.app.map.Map;
import sacred.alliance.magic.base.AttributeType;
import sacred.alliance.magic.base.OperatorType;
import sacred.alliance.magic.base.OutputConsumeType;
import sacred.alliance.magic.base.RoleType;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.domain.GoodsHero;
import sacred.alliance.magic.util.DateUtil;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.vo.AbstractRole;
import sacred.alliance.magic.vo.MapInstanceEvent;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.hero.domain.RoleHero;
import com.game.draco.app.mail.type.MailSendRoleType;
import com.game.draco.message.item.AngelChestInfoItem;
import com.game.draco.message.item.Arean3V3ResultItem;
import com.game.draco.message.item.DeathNotifySelfItem;
import com.game.draco.message.item.HeroBattleItem;
import com.game.draco.message.push.C0208_CopyRemainTimeNotifyMessage;
import com.game.draco.message.push.C3864_Arena3v3ResultNotifyMessage;
import com.game.draco.message.response.C2372_ActiveAngelChestNewRespMessage;
import com.google.common.collect.Lists;

public class MapArena3V3Instance extends MapArenaInstance{
	
	private java.util.Map<String,Integer> killedRoleMap = new ConcurrentHashMap<String,Integer>();
	private C3864_Arena3v3ResultNotifyMessage team1Message = null;
	private C3864_Arena3v3ResultNotifyMessage team2Message = null;
	private List<Arean3V3ResultItem> team1ItemList = new ArrayList<Arean3V3ResultItem>();
	private List<Arean3V3ResultItem> team2ItemList = new ArrayList<Arean3V3ResultItem>();
	private short buffId = 0;
	private Date startDate = new Date();
	private Set<String> roleDeathSet = new HashSet<String>();
	private MapBoxSupport mapBox ;
	private int refreshIndex = 0;
	private Date refreshDate ;//开始时间(宝箱)

	public MapArena3V3Instance(Map map, ArenaMatch match) {
		super(map, match);
		buffId = GameContext.getArena3V3App().getBuffId(match.getConfig().getArenaType());
		mapBox = new MapBoxSupport(this,OutputConsumeType.active_3v3_chest,
				OutputConsumeType.active_3v3_chest,GameContext.getI18n().getText(TextId.ARENA_3V3_CONTEXT),
				MailSendRoleType.Arena3v3) ;
		refreshDate = new Date();
	}
	
	@Override
    protected void enter(AbstractRole role){
		super.enter(role);
		//增加buff
		if(buffId > 0){
			GameContext.getUserBuffApp().addBuffStat(role, role, buffId, 1);
		}
		this.notifyMapRemainTime();
		//super 已有
		//this.perfectBody(role);
		if(null != this.mapBox){
			mapBox.enter(role);
		}
	}
	
	private void notifyMapRemainTime(){
		int maxTime = this.match.getConfig().getMaxBattleTime();
		if(maxTime < 0){
			return;
		}
		//剩余时间（秒）
		int time = maxTime - DateUtil.getSecondMargin(this.startDate);
		if(time < 0){
			time = 0;
		}
		C0208_CopyRemainTimeNotifyMessage message = new C0208_CopyRemainTimeNotifyMessage();
		message.setType((byte) 0);
		message.setTime(time);
		this.broadcastMap(null, message);
	}
	
	@Override
	protected List<DeathNotifySelfItem> rebornOptionFilter(RoleInstance role){
		return null ;
	}

	@Override
	protected void reward() {
		try {
			super.reward();
			sendResultMessage();
		} catch (Exception ex) {
			logger.error("", ex);
		}
	}
	
	@Override
	protected void reward(String roleId,BattleResult result,int otherTeamNum,ArenaType arenaType){
		RoleInstance role = GameContext.getOnlineCenter().getRoleInstanceByRoleId(roleId);
		Integer roleLevel = 0 ;
		
		if(null == role){
			roleLevel = this.match.getRolelevels().get(roleId);
			if(null == roleLevel){
				return ;
			}
			//TODO:发送离线邮件
			return ;
		}
		if(result == BattleResult.win){
			role.getRoleArena().incrWin(arenaType);
		}else{
            role.getRoleArena().incrFail(arenaType);
        }
		int killNum = 0;
		if(killedRoleMap.containsKey(roleId)){
			killNum = killedRoleMap.get(roleId);
		}
		ScoreResult scoreResult = GameContext.getArena3V3App().getScoreResult(role, result, match, killNum);
		if(null == scoreResult){
			return;
		}
		
		int addScore = getAddScore(role, scoreResult);
		//添加竞技场积分
		if(addScore >0){
			GameContext.getUserAttributeApp().changeAttribute(role, AttributeType.arena3V3Score, 
					OperatorType.Add, addScore, OutputConsumeType.arena_output);
			role.getRoleArena().incrScore(arenaType, addScore);
			role.getBehavior().notifyAttribute();
		}
		//添加等级
		float level = role.getRoleArena().getArenaLevel3v3() + scoreResult.getArenaLevel();
		level = Math.max(0, level);
		role.getRoleArena().setArenaLevel3v3(level);
		
		Arean3V3ResultItem item = new Arean3V3ResultItem();
		item.setRoleName(role.getRoleName());
		item.setRoleId(role.getIntRoleId());
		item.setArenaLevel((int)scoreResult.getArenaLevel());
		if(this.killedRoleMap.containsKey(roleId)){
			item.setKillNum(this.killedRoleMap.get(roleId));
		}else{
			item.setKillNum(0);
		}
		item.setScore(addScore);
		//获得英雄列表
		List<HeroBattleItem> hero4Client = Lists.newArrayList();
		List<RoleHero> heroList = GameContext.getHeroApp().getRoleSwitchableHeroList(roleId);
		if(!Util.isEmpty(heroList)){
			for(RoleHero hero : heroList){
				HeroBattleItem heroBattleItem = this.getHeroBattleItem(hero);
				if(null == heroBattleItem){
					continue ;
				}
				hero4Client.add(heroBattleItem);
			}
		}
		item.setHeroList(hero4Client);
		
		if(match.isTeam1(roleId)){
			team1ItemList.add(item);
		}else{
			team2ItemList.add(item);
		}
	}
	
	
	
	private HeroBattleItem getHeroBattleItem(RoleHero roleHero) {
		GoodsHero goodsHero = GameContext.getGoodsApp().getGoodsTemplate(GoodsHero.class, roleHero.getHeroId());
		if (null == goodsHero) {
			return null;
		}
		HeroBattleItem item = new HeroBattleItem();
		item.setImageId(goodsHero.getImageId());
		item.setGearId(goodsHero.getGearId());
		item.setSeriesId(goodsHero.getSeriesId());
		item.setLevel((byte) roleHero.getLevel());
		item.setQuality(roleHero.getQuality());
		item.setStar(roleHero.getStar());
		return item;
	}
	
	private int getAddScore(RoleInstance role, ScoreResult scoreResult){
		int arenaScore = scoreResult.getScore();
		int roleScore = role.get(AttributeType.arena3V3Score);
		int maxRoleScore = scoreResult.getMaxRoleScore();
		if(roleScore + arenaScore > maxRoleScore){
			arenaScore = maxRoleScore - roleScore;
		}
		int roleArenaScore = role.getRoleArena().getCycle3v3Score();
		int maxCycleScore = scoreResult.getMaxCycleScore();
		
		int totalScore = roleArenaScore + arenaScore;
		int addScore = totalScore > maxCycleScore ? maxCycleScore - roleArenaScore : arenaScore;
		return addScore;
	}
	
	@Override
	protected void deathDiversity(AbstractRole attacker, AbstractRole victim) {
		String roleId = attacker.getRoleId();
		//增加杀人数
		if(!killedRoleMap.containsKey(roleId)) {
			killedRoleMap.put(roleId, 0);
		}
		int value = this.killedRoleMap.get(roleId);
		value++ ;
		this.killedRoleMap.put(roleId, value);
		super.deathDiversity(attacker, victim);
		roleDeathSet.add(victim.getRoleId());
	}
	
	private void sendResultMessage(){
		C3864_Arena3v3ResultNotifyMessage message = null;
		for (String roleId : rewardRoleSet) {
			RoleInstance role = GameContext.getOnlineCenter().getRoleInstanceByRoleId(roleId);
			if(null == role){
				continue;
			}
			if(match.isTeam1(roleId)){
				if(null == team1Message){
					team1Message = new C3864_Arena3v3ResultNotifyMessage();
					if(null == this.winTeam){
						team1Message.setBattleResult(BattleResult.fail.getType());
					}else{
						team1Message.setBattleResult(this.winTeam.isMember(roleId)? BattleResult.win.getType() : BattleResult.fail.getType());
					}
					team1Message.setItems(this.team1ItemList);
				}
				message = team1Message;
			}else{
				if(null == team2Message){
					team2Message = new C3864_Arena3v3ResultNotifyMessage();
					if(null == this.winTeam){
						team2Message.setBattleResult(BattleResult.fail.getType());
					}else{
						team2Message.setBattleResult(this.winTeam.isMember(roleId)? BattleResult.win.getType() : BattleResult.fail.getType());
					}
					team2Message.setItems(this.team2ItemList);
				}
				message = team2Message;
			}
			role.getBehavior().sendMessage(message);
		}
	}
	
	protected void getRewardRole(){
		//参赛玩家
		rewardRoleSet = new HashSet<String>();
		rewardRoleSet.addAll(this.match.getTeam1().getAppliers());
		rewardRoleSet.addAll(this.match.getTeam2().getAppliers());
	}
	
	@Override
	public void exitMap(AbstractRole role) {
		super.exitMap(role);
		if(role.getRoleType() != RoleType.PLAYER) {
			return;
		}
		RoleInstance player = (RoleInstance) role;
		if(winFlag.get()){
			GameContext.getArena3V3App().pushArena3v3RespMessage(player);
			return;
		}
		
		if(roleDeathSet.contains(role.getRoleId())){
			return;
		}
		
		ScoreResult scoreResult = GameContext.getArena3V3App().getScoreResult(player, BattleResult.fail, match, 0);
		if(null == scoreResult){
			return;
		}
		//添加等级
		float level = player.getRoleArena().getArenaLevel3v3() + scoreResult.getArenaLevel();
		player.getRoleArena().setArenaLevel3v3(Math.max(0, level));
		String roleId = player.getRoleId();
		match.getTeam1().getAppliers().remove(roleId);
		match.getTeam2().getAppliers().remove(roleId);
	}
	
	@Override
	public void doEvent(RoleInstance role,MapInstanceEvent event){
		if(null == this.mapBox){
			return ;
		}
		this.mapBox.doEvent(role, event);
	}
	
	//刷新宝箱
	@Override
	protected void refresh(){
		List<ChestRefreshInfo> refreshList = mapBox.getRefreshList();
		if(null == refreshList || mapBox.getRefreshSize() == 0){
			return ;
		}
		if(refreshIndex >= refreshList.size()){
			return ;
		}
		int time = DateUtil.getSecondMargin(refreshDate);
		List<AngelChestInfoItem> thisChestList = null ;
		for(int i = refreshIndex ;i < refreshList.size() ; i++){
			ChestRefreshInfo cr = refreshList.get(i);
			if(time < cr.getRefreshTime()){
				break ;
			}
			try {
				List<AngelChestInfoItem> subList = mapBox.refresh(cr);
				if(!Util.isEmpty(subList)){
					if(null == thisChestList){
						thisChestList = new ArrayList<AngelChestInfoItem>();
					}
					thisChestList.addAll(subList);
				}
			}catch(Exception ex){
				logger.error("",ex);
			}
			refreshIndex ++;
		}
		if(Util.isEmpty(thisChestList)){
			return ;
		}
		//广播
		C2372_ActiveAngelChestNewRespMessage respMsg = new C2372_ActiveAngelChestNewRespMessage();
		respMsg.setNewList(thisChestList);
		this.broadcastMap(null, respMsg);
	}

    @Override
    public void notifyRoleAttributeToOther(RoleInstance role,Message message){
        //3v3没有组队概念，全图广播
        broadcastMap(role, message,0) ;
    }
}
