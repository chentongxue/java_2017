package sacred.alliance.magic.app.pk;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sacred.alliance.magic.app.count.type.CountType;
import sacred.alliance.magic.app.goods.behavior.result.GoodsResult;
import sacred.alliance.magic.base.AttributeType;
import sacred.alliance.magic.base.BindingType;
import sacred.alliance.magic.base.OperatorType;
import sacred.alliance.magic.base.OutputConsumeType;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.base.RolePkStatus;
import sacred.alliance.magic.base.RoleType;
import sacred.alliance.magic.base.XlsSheetNameType;
import sacred.alliance.magic.channel.EmptyChannelSession;
import sacred.alliance.magic.component.id.IdFactory;
import sacred.alliance.magic.component.id.IdType;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.core.channel.ChannelSession;
import sacred.alliance.magic.domain.GoodsBase;
import sacred.alliance.magic.domain.RoleGoods;
import sacred.alliance.magic.domain.RoleLevelup;
import sacred.alliance.magic.util.Log4jManager;
import sacred.alliance.magic.util.ProbabilityMachine;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.util.XlsPojoUtil;
import sacred.alliance.magic.vo.AbstractRole;
import sacred.alliance.magic.vo.MapInstance;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.buff.Buff;
import com.game.draco.app.buff.stat.BuffStat;
import com.game.draco.app.chat.ChannelType;
import com.game.draco.app.chat.ChatSysName;
import com.game.draco.app.mail.domain.Mail;
import com.game.draco.app.mail.type.MailSendRoleType;
import com.game.draco.app.npc.domain.NpcInstance;
import com.game.draco.app.team.Team;
import com.game.draco.message.internal.C0081_PkFallInternalMessage;
import com.game.draco.message.internal.C0082_PkAttackerInternalMessage;
import com.game.draco.message.item.DeathNotifySelfItem;
import com.game.draco.message.item.GoodsLiteNamedItem;
import com.game.draco.message.item.NpcFunctionItem;
import com.game.draco.message.push.C0003_TipNotifyMessage;
import com.game.draco.message.push.C0401_ForceTypeNotifyMessage;
import com.game.draco.message.push.C1112_RoleColorNotifyMessage;
import com.game.draco.message.push.C1522_PkStatusNotifyMessage;
import com.game.draco.message.push.C1525_PkDeathNotifySelfMessage;
import com.game.draco.message.request.C1526_PkRemoveBuffReqMessage;

public class PkAppImpl implements PkApp{
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private static final ChannelSession emptyChannelSession = new EmptyChannelSession();
	private Map<Integer, PkKillConfig> killConfigMap = new HashMap<Integer, PkKillConfig>();
	private PkConfig pkConfig;
	private int maxKillNum = 0;//最大杀人数，大于此人数的，取此人数对应的PkKillConfig
	private static int PERCENT = 10000;
	private final short REMOVE_BUFF_CMDID = new C1526_PkRemoveBuffReqMessage().getCommandId() ; 
	private final static long DEFAULT_COLOR = Long.parseLong("FFFFFFFF",16) ;
	private final static String DEFAULT_COLOR_STR = "FFFFFFFF";
	
	@Override
	public void start() {
		initKillConfig();
		initPkConfig();
	}
	
	private void initKillConfig(){
		String fileName = XlsSheetNameType.pk_kill_config.getXlsName();
		String sheetName = XlsSheetNameType.pk_kill_config.getSheetName();
		try{
			String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
			List<PkKillConfig> list = XlsPojoUtil.sheetToList(sourceFile, sheetName, PkKillConfig.class);
			if(Util.isEmpty(list)){
				Log4jManager.checkFail();
				Log4jManager.CHECK.error("not any config: sourceFile = "+fileName +" sheetName ="+sheetName);
				return;
			}
			
			for(PkKillConfig config : list) {
				if(null == config){
					continue;
				}
				if(config.getMaxKillNum() > maxKillNum){
					maxKillNum = config.getMaxKillNum();
				}
				for(int i=config.getMinKillNum(); i<=config.getMaxKillNum(); i++){
					if(!killConfigMap.containsKey(i)){
						killConfigMap.put(i, config);
					}
				}
			}
		}catch(Exception ex){
			Log4jManager.checkFail();
			Log4jManager.CHECK.error("loadExel error: sourceFile = " + fileName + "sheetName = " + sheetName, ex);
		}
	}
	
	private void initPkConfig(){
		String fileName = XlsSheetNameType.pk_config.getXlsName();
		String sheetName = XlsSheetNameType.pk_config.getSheetName();
		try{
			String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
			List<PkConfig> list = XlsPojoUtil.sheetToList(sourceFile, sheetName, PkConfig.class);
			if(Util.isEmpty(list)){
				Log4jManager.checkFail();
				Log4jManager.CHECK.error("loadExel error : sourceFile = "+fileName +" sheetName ="+sheetName+",the system will shutdown .... ");
				return;
			}
			pkConfig = list.get(0);
			if(null == pkConfig) {
				Log4jManager.checkFail();
				Log4jManager.CHECK.error("loadExel error : sourceFile = "+fileName +" sheetName ="+sheetName+",the system will shutdown .... ");
				return;
			}
		}catch (Exception e) {
			logger.error("CampApp.initCampConfig error:", e);
			Log4jManager.checkFail();
			Log4jManager.CHECK.error("loadExel error : sourceFile = "+fileName +" sheetName ="+sheetName+",the system will shutdown .... ",e);
		}
	}
	
	@Override
	public void killPlayer(AbstractRole attacker, RoleInstance victim, List<DeathNotifySelfItem> optionList) {
		//攻击者逻辑
		this.attackerLogic(attacker, victim);
		
		//死亡者逻辑
		this.victimLogic(attacker, victim, optionList);
	}
	
	@Override
	public void internalAttackerLogic(RoleInstance attacker, RoleInstance victim){
		try{
			if(attacker.getPkStatus() != RolePkStatus.MASSACRE.getType()) {
				return;
			}
			
			short protectBuffId = this.pkConfig.getProtectBuffId();
			
			int killNum = attacker.getRoleCount().getRoleTimesToInt(CountType.KillCount);//getKillCount();
			killNum++;
			attacker.getRoleCount().changeTimes(CountType.KillCount, killNum);//setKillCount(killNum);
			
			if(!victim.hasBuff(protectBuffId)){
				int todayKillNum = attacker.getRoleCount().getRoleTimesToInt(CountType.TodayKillCount);//.getTodayKillCount();
				todayKillNum++;
				attacker.getRoleCount().changeTimes(CountType.TodayKillCount, todayKillNum);//setTodayKillCount(todayKillNum);
			}
			
			PkKillConfig kConfig = getPkKillConfig(killNum);
			if(null == kConfig){
				return;
			}
			
			short buffId = this.pkConfig.getBuffId();
			if(buffId > 0){
				//加buff
				this.addAttackerBuff(attacker, buffId, kConfig.getBuffLevel());
			}
			//通知玩家杀人数
			String str = MessageFormat.format(pkConfig.getKillMsg(), killNum) ;
			C0003_TipNotifyMessage message = new C0003_TipNotifyMessage(str);
			attacker.getBehavior().sendMessage(message);
			//杀人数达到一定数量全服喊话
			String broadcastStr = kConfig.getBroadcast();
			if(!Util.isEmpty(broadcastStr)){
				MapInstance mapInstance = attacker.getMapInstance();
				if(null != mapInstance){
					String mapName = mapInstance.getMap().getMapConfig().getMapdisplayname();
					String msg = MessageFormat.format(broadcastStr, attacker.getRoleName(), mapName, killNum);							
					GameContext.getChatApp().sendSysMessage(ChatSysName.System, ChannelType.Publicize_Personal, msg, null, null);
				}
			}
			
			String color = kConfig.getColor();
			if(!Util.isEmpty(color)){
				this.notifyColor(attacker, color);
			}
		}catch(Exception e){
			logger.error("PkApp.attackerLogic error",e);
		}
	}
	
	private void attackerLogic(AbstractRole attacker, RoleInstance victim){
		if(null == attacker || attacker.getRoleType() != RoleType.PLAYER) {
			return;
		}
		RoleInstance role = (RoleInstance)attacker;
		//删除死者的物品
		C0082_PkAttackerInternalMessage internalReqMsg = new C0082_PkAttackerInternalMessage();
		internalReqMsg.setAttacker(role);
		internalReqMsg.setVictim(victim);
		GameContext.getUserSocketChannelEventPublisher().publish(role.getUserId(), internalReqMsg, emptyChannelSession, true);
	}
	
	private void addAttackerBuff(RoleInstance role, short buffId, int buffLevel){
		try{
			Buff buff = GameContext.getBuffApp().getBuff(buffId);
			if(null == buff){
				return;
			}
			BuffStat buffStat = role.getBuffStat(buffId);
			int effectTime = buff.getPersistTime();
			if(null != buffStat){
				effectTime += buffStat.getRemainTime();
			}
			//！！！！策划要求改为所有时间累加！！！！
			GameContext.getUserBuffApp().addBuffStat(role, role, buffId, effectTime, buffLevel);
		}catch(Exception e){
			logger.error("PkApp.addAttackerBuff",e);
		}
	}
	
	private void victimLogic(AbstractRole attacker, RoleInstance victim, List<DeathNotifySelfItem> optionList){
		try{
			if(null == victim){
				return;
			}
			//给死者加不计数buff
			this.addProtectBuff(attacker, victim);
			
			//给死者惩罚
			this.punish(attacker, victim, optionList);
		}catch(Exception e){
			logger.error("PkApp.attackerLogic error",e);
		}
	}
	
	private void addProtectBuff(AbstractRole attacker, RoleInstance victim){
		if(null == attacker || attacker.getRoleType() != RoleType.PLAYER) {
			return;
		}
		
		RoleInstance role = (RoleInstance)attacker;
		if(role.getPkStatus() != RolePkStatus.MASSACRE.getType()) {
			return;
		}
		//给死者添加不计数buff
		short protectBuffId = this.pkConfig.getProtectBuffId();
		if(protectBuffId > 0){
			//不能直接加保护buff，因为人已经死了，加不上，存在人身上，复活的时候添加
			victim.setProtectBuffId(protectBuffId);
		}
	}
	
	/**
	 * 惩罚
	 */
	private void punish(AbstractRole attacker, RoleInstance victim, List<DeathNotifySelfItem> optionList){
		
		if(victim.getPkStatus() != RolePkStatus.MASSACRE.getType()){
			GameContext.getRoleRebornApp().notifySelfDeath(victim, attacker, optionList);
			return;
		}
		
		//死者是开了屠杀模式的
		int killNum = victim.getRoleCount().getRoleTimesToInt(CountType.KillCount);//.getKillCount();
		PkKillConfig kConfig = getPkKillConfig(killNum);
		if(null == kConfig){
			GameContext.getRoleRebornApp().notifySelfDeath(victim, attacker, optionList);
			return;
		}
		//没有惩罚时，弹出正常的死亡面板
		if(noPunish(kConfig)) {
			GameContext.getRoleRebornApp().notifySelfDeath(victim, attacker, optionList);
			return;
		}
		
		//随机是否掉落，如果掉落则判断是否有替身，如果没有替身，掉落物品
		GoodsLiteNamedItem item = new GoodsLiteNamedItem();
		PkFallResult result = this.fallGoods(attacker, victim, kConfig);
		if(result.isSuccess()){
			//有掉落
			int goodsId = result.getGoodsId();
			GoodsBase gb = GameContext.getGoodsApp().getGoodsBase(goodsId);
			if(null != gb){
				item = gb.getGoodsLiteNamedItem();
			}
			item.setNum((short)result.getGoodsNum());
			item.setBindType(result.getBindType());
		}
		
		//如果是消耗了替身物品，就不扣经验和钱了，直接发送死亡面板
		int consumeGoodsId = this.pkConfig.getConsumeGoodsId();
		if(consumeGoodsId == result.getGoodsId()){
			deathNotify(attacker, victim, 0, 0, optionList, item);
			return;
		}
		
		boolean notify = false;
		int reduceMoney = 0;
		int reduceExp = 0;
		//减少钱
		int moneyRate = kConfig.getMoneyRate();
		if(moneyRate > 0 && moneyRate < PERCENT){
			reduceMoney = (int)(victim.getSilverMoney() * ((double)moneyRate/PERCENT));
			if(reduceMoney > 0){
				GameContext.getUserAttributeApp().changeAttribute(victim, AttributeType.gameMoney, OperatorType.Decrease, reduceMoney, OutputConsumeType.pk_punish);
				notify = true;
			}
		}
		//减少经验
		int expRate = kConfig.getExpRate();
		if(expRate > 0 && expRate < PERCENT){
			RoleLevelup roleLevelup = GameContext.getAttriApp().getLevelup(victim.getLevel());
			int upExp = roleLevelup.getUpExp();
			int roleExp = victim.getExp();
			reduceExp = (int)(upExp * ((double)expRate/PERCENT));
			int realReduceExp = roleExp > reduceExp ? reduceExp : roleExp;
			if(reduceExp > 0){
				GameContext.getUserAttributeApp().changeAttribute(victim, AttributeType.exp, OperatorType.Decrease, realReduceExp, OutputConsumeType.pk_punish);
				notify = true;
			}
		}
		
		if(notify){
			victim.getBehavior().notifyAttribute();
		}
		
		deathNotify(attacker, victim, reduceMoney, reduceExp, optionList, item);
	}
	
	private boolean noPunish(PkKillConfig kConfig){
		return kConfig.getExpRate() == 0 && kConfig.getMoneyRate() == 0 && kConfig.getFallRate() == 0;
	}
	
	//发送死亡弹板
	private void deathNotify(AbstractRole attacker, RoleInstance victim, int money, int exp, List<DeathNotifySelfItem> optionList, GoodsLiteNamedItem item){
		C1525_PkDeathNotifySelfMessage selfMsg = new C1525_PkDeathNotifySelfMessage();
		selfMsg.setItems(optionList);
		selfMsg.setMoney(money);
		selfMsg.setExp(exp);
		selfMsg.setItem(item);
		if(null != attacker){
			selfMsg.setInfo(MessageFormat.format(this.pkConfig.getDieInfo(), attacker.getRoleName()));
		}
		victim.getBehavior().sendMessage(selfMsg);
	}
	
	private PkFallResult fallGoods(AbstractRole attacker, RoleInstance victim, PkKillConfig kConfig){
		PkFallResult result = new PkFallResult();
		try{
			if(ProbabilityMachine.getRandomNum(1, PERCENT) > kConfig.getFallRate()){
				return result;
			}
			
			int consumeGoodsId = this.pkConfig.getConsumeGoodsId();
			if(victim.getRoleBackpack().countByGoodsId(consumeGoodsId) >= 1){
				GoodsResult goodsResult = GameContext.getUserGoodsApp().deleteForBag(victim, consumeGoodsId, 1, OutputConsumeType.pk_punish);
				if(goodsResult.isSuccess()) {
					result.setGoodsId(consumeGoodsId);
					result.setGoodsNum(1);
					return result;
				}
			}
			
			List<RoleGoods> roleGoodsList = victim.getRoleBackpack().getCanFallGoodsList();
			if(Util.isEmpty(roleGoodsList)){
				return result;
			}
			int index = Util.randomInt(0, roleGoodsList.size());
			RoleGoods roleGoods = roleGoodsList.get(index);
			if(null == roleGoods){
				return result;
			}
			
			int fallNum = roleGoods.getCurrOverlapCount() > kConfig.getFallNum() ? kConfig.getFallNum() : roleGoods.getCurrOverlapCount();
			//删除死者的物品
			C0081_PkFallInternalMessage internalReqMsg = new C0081_PkFallInternalMessage();
			internalReqMsg.setRoleId(victim.getRoleId());
			internalReqMsg.setGoods(roleGoods);
			internalReqMsg.setNum(fallNum);
			GameContext.getUserSocketChannelEventPublisher().publish(victim.getUserId(), internalReqMsg, emptyChannelSession, true);
	
			int fallGoodsId = roleGoods.getGoodsId();
			if(roleGoods.getBind() != BindingType.already_binding.getType() && null != attacker &&
					attacker.getRoleType() == RoleType.PLAYER) {
				//给攻击者发物品
				String mailTitle = MessageFormat.format(this.pkConfig.getMailTitle(), victim.getRoleName());
				String mailContent = MessageFormat.format(this.pkConfig.getMailContent(), victim.getRoleName());
				this.sendGoodsMail(attacker.getRoleId(), fallGoodsId, fallNum, BindingType.get(roleGoods.getBind()), mailTitle, mailContent);
			}
			
			result.setGoodsId(fallGoodsId);
			result.setGoodsNum(fallNum);
			result.setBindType(roleGoods.getBind());
			result.success();
		}catch(Exception e){
			logger.error("PkApp.fallGoods error",e);
		}
		return result;
	}
	
	private void sendGoodsMail(String roleId, int goodsId, int num, BindingType type, String title, String content){
		try {
			Mail mail = new Mail(IdFactory.getInstance().nextId(IdType.MAIL));
			mail.setSendRole(MailSendRoleType.System.getName());
			mail.setTitle(title);
			mail.setContent(content);
			mail.setRoleId(roleId);
			mail.setSendSource(OutputConsumeType.pk_punish.getType());
			mail.addMailAccessory(goodsId, num, type);
			GameContext.getMailApp().sendMail(mail);
		}catch(Exception e){
			logger.error("PkApp.sendGoodsByMail error",e);
		}
	}
	
	@Override
	public Result changePkStatus(RoleInstance role, byte pkStatus){
		Result result = new Result();
		try{
			MapInstance mapInstance = role.getMapInstance();
			if(null == mapInstance || !mapInstance.roleCanPk()){
				return result.setInfo(GameContext.getI18n().getText(TextId.PK_STATUS_MAP_CAN_NOT_CHANGE));
			}
			
			int openMassacreLevel = this.pkConfig.getOpenLevel();
			if(role.getLevel() < openMassacreLevel){
				return result.setInfo(GameContext.getI18n().messageFormat(TextId.PK_STATUS_CHANGE_LEVEL_NOT_ENOUGH, openMassacreLevel));
			}
			
			byte rolePkStatus = role.getPkStatus();
			if(rolePkStatus == RolePkStatus.MASSACRE.getType() && role.hasBuff(pkConfig.getBuffId())) {
				return result.setInfo(GameContext.getI18n().getText(TextId.PK_STATUS_CAN_NOT_CHANGE));
			}
			boolean hasChange = rolePkStatus == pkStatus ? false : true;
			role.setPkStatus(pkStatus);
			
			if(!hasChange){
				return result.success();
			}
			//通知地图内玩家状态改变
			notifyStatus(role, pkStatus);
			//通知地图内玩家势力改变
			notifyForceRelation(role, mapInstance);
			//队伍通知
			sendTeamMsg(role);
			
			if(pkStatus == RolePkStatus.MASSACRE.getType()) {
				int killNum = role.getRoleCount().getRoleTimesToInt(CountType.KillCount);//.getKillCount();
				PkKillConfig kConfig = getPkKillConfig(killNum);
				if(null == kConfig){
					return result.success();
				}
				short buffId = this.pkConfig.getBuffId();
				if(buffId > 0){
					//加buff
					GameContext.getUserBuffApp().addBuffStat(role, role, buffId, kConfig.getBuffLevel());
				}
				
				notifyColor(role, kConfig.getColor());
//				Team team = role.getTeam();
//				if(null != team && team.getPlayerNum() > 1){
//					role.getTeam().memberLeave(role, LeaveTeam.apply);
//				}
			}
		}catch(Exception e){
			logger.error("PkApp.changePkStatus error",e);
		}
		return result.success();
	}
	
	private void sendTeamMsg(RoleInstance role){
		try {
			if(role.getPkStatus() == RolePkStatus.PEACE.getType()){
				return;
			}
			Team team = role.getTeam();
			if(null == team || team.getPlayerNum() < 1){
				return;
			}
			
			String message = "";
			if(role.getPkStatus() == RolePkStatus.BATTLE.getType()){
				message = this.pkConfig.getBattleMsg();
			}else{
				message = this.pkConfig.getMassacreMsg();
			}
			if(Util.isEmpty(message)){
				return;
			}
			message = MessageFormat.format(message, role.getRoleName());
			GameContext.getChatApp().sendSysMessage(ChatSysName.Team, ChannelType.Team, message, null, team);
		} catch (Exception e) {
			logger.error("PkApp.sendTeamMsg error",e);
		}
	}
	
	private void notifyForceRelation(RoleInstance role, MapInstance mapInstance){
		try {
			for(RoleInstance targetRole : mapInstance.getRoleList()){
				MapInstance instance = targetRole.getMapInstance();
				//不在同一张地图不需要同步
				if(null == instance || !instance.getInstanceId().equals(mapInstance.getInstanceId())){
					continue;
				}
				
				byte forceRelation = targetRole.getForceRelation(role).getType();
				
				//给前队友发自己的势力
				C0401_ForceTypeNotifyMessage msg1 = new C0401_ForceTypeNotifyMessage();
				msg1.setRoleId(role.getIntRoleId());
				msg1.setForceRelation(forceRelation);
				targetRole.getBehavior().sendMessage(msg1);
				//给自己发前队友的势力
				C0401_ForceTypeNotifyMessage msg2 = new C0401_ForceTypeNotifyMessage();
				msg2.setRoleId(targetRole.getIntRoleId());
				msg2.setForceRelation(forceRelation);
				role.getBehavior().sendMessage(msg2);
			}
		} catch (Exception e) {
			this.logger.error("PkApp.notifyForceRelation error: ", e);
		}
	}
	
	private void notifyColor(RoleInstance role, String color){
		try {
			int roleColor = role.getColor();
			int intColor = this.getColor(color);
			
			if(roleColor == intColor){
				return;
			}
			
			role.setColor(intColor);
			//发给自己
			C1112_RoleColorNotifyMessage msg = new C1112_RoleColorNotifyMessage();
			msg.setRoleId(role.getIntRoleId());
			msg.setColor(intColor);
			role.getBehavior().sendMessage(msg);
			//广播
			role.getMapInstance().broadcastMap(role, msg);
		} catch (Exception e) {
			this.logger.error("PkApp.notifyColor error: ", e);
		}
	}
	
	private void notifyStatus(RoleInstance role, byte status){
		try {
			C1522_PkStatusNotifyMessage msg = new C1522_PkStatusNotifyMessage();
			msg.setRoleId(role.getIntRoleId());
			msg.setStatus(role.getPkStatus());
			role.getMapInstance().broadcastMap(role, msg);
		} catch (Exception e) {
			this.logger.error("PkApp.notifyStatus error", e);
		}
	}
	
	private int getColor(String color){
		if(Util.isEmpty(color) || color.equals(DEFAULT_COLOR_STR)){
			return (int)DEFAULT_COLOR ;
		}
		return (int)Long.parseLong(color, 16);
	}
	
	@Override
	public PkKillConfig getPkKillConfig(int killNum){
		if(killNum > maxKillNum){
			killNum = maxKillNum;
		}
		return this.killConfigMap.get(killNum);
	}
	
	@Override
	public PkConfig getPkConfig(){
		return this.pkConfig;
	}
	
	private int getColorByKillNum(int killNum){
		try{
			PkKillConfig kConfig = this.getPkKillConfig(killNum);
			if(null == kConfig){
				return (int)DEFAULT_COLOR;
			}
			return (int)this.getColor(kConfig.getColor());
		}catch (Exception e) {
			this.logger.error("PkApp.getColorByKillNum error", e);
		}
		return 0;
	}
	
	@Override
	public int onLogin(RoleInstance role, Object context){
		try{
			if(!role.hasBuff(this.pkConfig.getBuffId())){
				role.getRoleCount().changeTimes(CountType.KillCount, 0);//setKillCount(0);
			}
			role.setColor(this.getColorByKillNum(role.getRoleCount().getRoleTimesToInt(CountType.KillCount)));//.getKillCount()));
			//角色等级未达到开启屠杀等级，强制更新为和平状态
			if(role.getLevel() < this.pkConfig.getOpenLevel()){
				role.setPkStatus(RolePkStatus.PEACE.getType());
			}
		}catch(Exception e){
			logger.error("PkApp.roleLogin error",e);
			return 0;
		}
		
		return 1;
	}
	
	@Override
	public int onLogout(RoleInstance role, Object context) {
		// TODO Auto-generated method stub
		return 0;
	}
	
	@Override
	public int onCleanup(String roleId, Object context) {
		// TODO Auto-generated method stub
		return 0;
	}
	
	@Override
	public void clearKillNum(AbstractRole player){
		try{
			if(null == player || player.getRoleType() != RoleType.PLAYER) {
				return;
			}
			RoleInstance role = (RoleInstance)player;
			role.getRoleCount().changeTimes(CountType.KillCount, 0);//setKillCount(0);
			notifyColor(role, DEFAULT_COLOR_STR);
		}catch(Exception e){
			logger.error("PkApp.clearKillNum error",e);
		}
	}
	
	@Override
	public void changeColorByRemove(RoleInstance role){
		try{
			this.notifyColor(role, DEFAULT_COLOR_STR);
		}catch(Exception e){
			logger.error("",e);
		}
	}
	
	@Override
	public void setArgs(Object arg0) {
		
	}

	@Override
	public void stop() {
		
	}

	@Override
	public List<NpcFunctionItem> getNpcFunction(RoleInstance role,
			NpcInstance npc) {
		List<NpcFunctionItem> functionList = new ArrayList<NpcFunctionItem>();
		String npcId = this.pkConfig.getNpcId();
		if(Util.isEmpty(npcId) || !npcId.equals(npc.getNpcid())){
			return functionList;
		}
		NpcFunctionItem item = new NpcFunctionItem();
		item.setCommandId(REMOVE_BUFF_CMDID);
		item.setTitle(GameContext.getI18n().getText(TextId.PK_NPC_FUNCTION_TITLE));
		functionList.add(item);
		return functionList;
	}
}
