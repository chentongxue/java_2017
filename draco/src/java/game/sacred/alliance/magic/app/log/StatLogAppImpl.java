package sacred.alliance.magic.app.log;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sacred.alliance.magic.app.goods.GoodsOperateBean;
import sacred.alliance.magic.app.goods.behavior.param.StrengthenParam;
import sacred.alliance.magic.app.goods.behavior.result.StrengthenResult;
import sacred.alliance.magic.app.log.vo.StatRoleGoodsRecord;
import sacred.alliance.magic.app.map.data.MapConfig;
import sacred.alliance.magic.app.quickbuy.QuickBuyGoods;
import sacred.alliance.magic.base.AttributeType;
import sacred.alliance.magic.base.OutputConsumeType;
import sacred.alliance.magic.base.RoleType;
import sacred.alliance.magic.domain.GoodsBase;
import sacred.alliance.magic.domain.GoodsEquipment;
import sacred.alliance.magic.domain.RoleGoods;
import sacred.alliance.magic.domain.RoleLevelDistribution;
import sacred.alliance.magic.util.DateUtil;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.vo.AbstractRole;
import sacred.alliance.magic.vo.MapInstance;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.mail.domain.Mail;
import com.game.draco.app.mail.domain.MailAccessory;
import com.game.draco.app.mail.type.MailLogType;
import com.game.draco.app.npc.domain.NpcInstance;
import com.game.draco.app.npc.domain.NpcTemplate;
import com.game.draco.app.quest.Quest;
import com.game.draco.app.quest.QuestAward;
import com.game.log.DefaultLog;
import com.game.log.actor.game.ActivityPart;
import com.game.log.actor.game.BossDeath;
import com.game.log.actor.game.ClientInfo;
import com.game.log.actor.game.Compass;
import com.game.log.actor.game.EquipStreng;
import com.game.log.actor.game.GoldMoneyDayRemain;
import com.game.log.actor.game.GoodsFall;
import com.game.log.actor.game.LogoutGoodsInfo;
import com.game.log.actor.game.MailSendInfo;
import com.game.log.actor.game.RoleConsume;
import com.game.log.actor.game.RoleDeath;
import com.game.log.actor.game.RoleExpRecord;
import com.game.log.actor.game.RoleGoodsRecord;
import com.game.log.actor.game.RoleKillMonster;
import com.game.log.actor.game.RoleLevelNumber;
import com.game.log.actor.game.RoleLevelUp;
import com.game.log.actor.game.RoleLogin;
import com.game.log.actor.game.RoleLogout;
import com.game.log.actor.game.RoleMoneyRecord;
import com.game.log.actor.game.RoleOnline;
import com.game.log.actor.game.RoleOnlineTime;
import com.game.log.actor.game.RolePay;
import com.game.log.actor.game.RoleQuest;
import com.game.log.actor.game.RoleQuickBuy;
import com.game.log.actor.game.RoleRegister;
import com.game.log.actor.game.RoleShopBuy;
import com.game.log.actor.game.Task;
import com.game.log.actor.game.TradingRecord;
import com.game.log.actor.game.UserValid;
import com.game.log.app.LogApp;
import com.game.log.app.LogAppImpl;
import com.game.log.core.AutoLoadLog;
import com.game.log.core.Log4jBuilder;

public class StatLogAppImpl implements StatLogApp{
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private String log4jPath = "" ;
	private String logDataPath = "" ;
	private LogApp app = new LogAppImpl();
	private static final String targetName = "log4j-stat.properties";
	private static final String pkgName = "game";
	
	/**
	 * 统计日志执行
	 * 基本属性赋值
	 * @param role 角色对象
	 * @param logInfo 日志对象
	 */
	private void execute(RoleInstance role, DefaultLog logInfo){
		try {
			logInfo.setGameId(String.valueOf(GameContext.getAppId()));
			logInfo.setServerId(String.valueOf(GameContext.getServerId()));
			if(null != role){
				logInfo.setUserId(role.getUserId());
				logInfo.setUserName(role.getUserName());
				logInfo.setUserRegTime(role.getUserRegTime());
				logInfo.setRoleRegTime(role.getCreateTime());
				logInfo.setChannelId(role.getChannelId());
				logInfo.setRegChannelId(role.getRegChannelId());
			}
			//执行
			logInfo.execute();
		} catch (Exception e) {
			this.logger.error("StatLogApp.execute", e);
		}
	}
	
	@Override
	public void initLog4j(String appId,String serverId,String log4jPath,String logDataPath){
		if(isNull(appId)){
			System.out.println("The appId is null");
			System.exit(1);
		}
		if(isNull(serverId)){
			System.out.println("The serverId  is null");
			System.exit(1);
		}
		if(isNull(log4jPath)){
			System.out.println("The log4jPath is null");
			System.exit(1);
		}
		if(isNull(logDataPath)){
			System.out.println("The logDataPath is null");
			System.exit(1);
		}
		Log4jBuilder builder = new Log4jBuilder(appId,serverId,log4jPath,logDataPath,targetName, pkgName);
		builder.createLog4j();
	}
	//TODO:args[]{appId,serverId,log4jPath,logDataPath}
	public static void main(String[] args) {
		if(null == args || args.length < 4){
			System.out.println("The Params is error");
			System.exit(1);
		}
		try{
			String appId = args[0];
			String serverId = args[1];
			String log4jPath = args[2];
			String dataLogPath = args[3];
			String pkgName = args[4];
			LogAppImpl lai = new LogAppImpl();
			lai.initLog4j(appId, serverId,log4jPath,dataLogPath,pkgName);
			try{
				Thread.sleep(3000);
			}catch(Exception e){
				e.printStackTrace();
			}
			
			lai.initAllLogCat(pkgName);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	@Override
	public void mailSendInfoLog(Mail mail , RoleInstance role){
		try{
			MailSendInfo info = new MailSendInfo();
			if(null != role){
				info.setAccRoleName(role.getRoleName());
				info.setAccUserId(role.getUserId());
				info.setAccUserName(role.getUserName());
			}
			info.setAccRoleId(mail.getRoleId());
			info.setSendInfo(mail.getLogContent());
			info.setSendName(mail.getSendRole());
			info.setSendTime(mail.getSendTime());
			info.setEmailId(mail.getMailId());
			info.setSendType(String.valueOf(mail.getSendSource()));
			info.setType(String.valueOf(MailLogType.send.getType()));
			this.execute(role, info);
		}catch(Exception e){
			logger.error("",e);
		}
	}
	
	public void mailSendInfoPickLog(RoleInstance role,Mail mail,List<MailAccessory> maList,
			int exp,int gold,int bindGold,int silverMoney,int contribute,int zp,int magicSoul,int dkp){
		try{
			StringBuffer logContent = new StringBuffer();
			this.packGoods(role, maList, logContent);
			this.packMoneyExp(exp, gold, bindGold, silverMoney, contribute, zp, magicSoul,dkp, logContent);
			
			MailSendInfo info = new MailSendInfo();
			info.setAccRoleName(role.getRoleName());
			info.setAccUserId(role.getUserId());
			info.setAccUserName(role.getUserName());
			info.setAccRoleId(mail.getRoleId());
			info.setSendInfo(logContent.toString());
			info.setSendName(mail.getSendRole());
			info.setSendTime(mail.getSendTime());
			info.setExtractTime(new Date());
			info.setEmailId(mail.getMailId());
			info.setSendType(String.valueOf(mail.getSendSource()));
			info.setType(String.valueOf(MailLogType.pick.getType()));
			this.execute(role, info);
		}catch(Exception e){
			logger.error("",e);
		}
	}
	
	//邮件日志
	private void packGoods(RoleInstance role,List<MailAccessory> maList,StringBuffer logContent){
		if(Util.isEmpty(maList)){
			return ;
		}
		if(null != maList && maList.size() > 0){
			for(MailAccessory ma : maList){
				this.packGoodsBase(logContent, ma);
			}
		}
		
	}
	
	//封装日志
	private void packGoodsBase(StringBuffer logContent,MailAccessory ma){
		try{
			GoodsBase gb = GameContext.getGoodsApp().getGoodsBase(ma.getTemplateId());
			if(null != gb){
				logContent.append("goodsName=");
				logContent.append(gb.getName());
				logContent.append(",");
			}
			if(!Util.isEmpty(ma.getInstanceId())){
				logContent.append("instanceId=");
				logContent.append(ma.getInstanceId());
				logContent.append(",");
			}
			
			logContent.append("goodsId=");
			logContent.append(ma.getTemplateId());
			logContent.append("*");
			logContent.append(ma.getNum());
			logContent.append(",");
			logContent.append("bindType=");
			logContent.append(ma.getBind());
			logContent.append(";");
		}catch(Exception e){
			logger.error("",e);
		}
		
	}
	
	//封装邮件的钱，经验日志
	private void packMoneyExp(int exp,int gold,int bindGold,int silverMoney,int contribute,int zp,int magicSoul,int dkp,StringBuffer logContent){
		try{
			if(gold > 0){
				logContent.append(AttributeType.goldMoney.getName());
				logContent.append("*");
				logContent.append(gold);
				logContent.append(";");
			}
			if(silverMoney > 0){
				logContent.append(AttributeType.gameMoney.getName());
				logContent.append("*");
				logContent.append(silverMoney);
				logContent.append(";");
			}
			if(exp > 0){
				logContent.append(AttributeType.exp.getName());
				logContent.append("*");
				logContent.append(exp);
				logContent.append(";");
			}
//			if(contribute > 0){
//				logContent.append(AttributeType.contribute.getName());
//				logContent.append("*");
//				logContent.append(contribute);
//				logContent.append(";");
//			}
			if(zp > 0){
				logContent.append(AttributeType.potential.getName());
				logContent.append("*");
				logContent.append(zp);
				logContent.append(";");
			}
			if(dkp > 0){
				logContent.append(AttributeType.dkp.getName());
				logContent.append("*");
				logContent.append(dkp);
				logContent.append(";");
			}
			
		}catch(Exception e){
			logger.error("",e);
		}
		
	}
	
	
	@Override
	public void logoutGoodsInfoLog(RoleInstance role) {
		try {
			List<RoleGoods> rgList = new ArrayList<RoleGoods>();
			rgList.addAll(role.getRoleBackpack().getAllGoods());
			if (rgList.size() == 0) {
				return;
			}
			Date offTime = role.getLastOffTime();
			for (RoleGoods rg : rgList) {
				try {
					LogoutGoodsInfo info = new LogoutGoodsInfo();
					info.setAttrVar(rg.getAttrVar());
					info.setBind(String.valueOf(rg.getBind()));
					//info.setCurrDurable(String.valueOf(rg.getCurrDurable()));
					info.setCurrOverlapCount(String.valueOf(rg.getCurrOverlapCount()));
					info.setGoodsId(String.valueOf(rg.getGoodsId()));
					info.setGoodsInstanceId(rg.getId());
					info.setLogoutTime(offTime);
					info.setMosaic(rg.getMosaic());
					info.setOtherParm(rg.getOtherParm());
					//info.setPunched(rg.getPunched());
					info.setRoleId(rg.getRoleId());
					info.setRoleName(role.getRoleName());
					info.setStarNum(String.valueOf(rg.getStrengthenLevel()));
					info.setStorageType(String.valueOf(rg.getStorageType()));
					info.setUserId(role.getUserId());
					info.setUserName(role.getUserName());
					info.setDeadline(rg.getDeadline());
					info.setExpiredTime(rg.getExpiredTime());
					this.execute(role, info);
				} catch (Exception e) {
					logger.error("", e);
				}
			}

		} catch (Exception e) {
			logger.error("", e);
		}
	}
	
	@Override
	public void userValidLog(RoleInstance role){
		try{
			Date now = new Date();
			//1.当天     2.升级到2级,
			if(role.getLevel() != 2 || !DateUtil.sameDay(now, role.getUserRegTime())){
				return ;
			}
			UserValid info = new UserValid();
			info.setEffectTime(now);
			info.setUserId(role.getUserId());
			this.execute(role, info);
		}catch(Exception e){
			logger.error("",e);
		}
		
	}
	
	private boolean isNull(String str){
		if(null == str || 0 == str.trim().length()){
			return true ;
		}
		return false ;
	}

	@Override
	public void initAllLogCat() {
		try{
			AutoLoadLog autoLoadLog = new AutoLoadLog();
			autoLoadLog.appendAllLogCat(pkgName);
		}catch(Exception e){
			logger.error("",e);
		}
	}

	@Override
	public void roleDeathLog(RoleInstance role, AbstractRole acker) {
		try{
			if(null == role || null == acker){
				return ;
			}
			RoleDeath info = new RoleDeath();
			info.setFailId(role.getRoleId());
			info.setKillDate(new Date());
			MapInstance mapInstance = role.getMapInstance();
			if(null != mapInstance){
				MapConfig mc = mapInstance.getMap().getMapConfig();
				info.setKillMapId(mapInstance.getMap().getMapId());
				info.setKillMapName(mc.getMapdisplayname());
			}
			info.setFailLevel(String.valueOf(role.getLevel()));
			if(RoleType.PLAYER == acker.getRoleType()){
				RoleInstance roleAcker = (RoleInstance)acker ;
				info.setType("1");
				info.setOwnnerLevel(String.valueOf(acker.getLevel()));
				info.setOwnnerId(roleAcker.getRoleId());
			}else if(RoleType.NPC == acker.getRoleType()){
				NpcInstance npcAcker = (NpcInstance)acker ;
				info.setType("0");
				info.setOwnnerLevel(String.valueOf(npcAcker.getNpc().getLevel()));
				info.setOwnnerId(npcAcker.getNpc().getNpcid());
			}
			this.execute(role, info);
		}catch(Exception e){
			logger.error("",e);
		}
		
	}
	
	
	@Override
	public void roleLevelUpLog(RoleInstance role,int changeLevel) {
		try{
			RoleLevelUp info = new RoleLevelUp();
			info.setRoleId(role.getRoleId());
			info.setRoleLevel(String.valueOf(role.getLevel()));
			info.setUserId(role.getUserId());
			info.setCareerType(String.valueOf(role.getCareer()));
			info.setOnlineTimeTotal(String.valueOf(role.getLevelUpTime()));
			int frontLevel = role.getLevel() - changeLevel;
			info.setRoleFrontLevel(String.valueOf(frontLevel));
			info.setUserRegTime(role.getUserRegTime());
			info.setUserName(role.getUserName());
			this.execute(role, info);
		}catch(Exception e){
			logger.error("",e);
		}
	}

	@Override
	public void roleLoginLog(RoleInstance role){
		try{
			if(null == role){
				return ;
			}
			RoleLogin info = new RoleLogin();
			//获得相关技能 
			//int[] talents = getSkillPoints(role);
			//info.setExitTalentNum1(String.valueOf(talents[0]));
			//info.setExitTalentNum2(String.valueOf(talents[1]));
			info.setLoginChannel(String.valueOf(role.getChannelId()));
			info.setLoginLevel(String.valueOf(role.getLoginLevel()));
			//info.setLoginVersion(role.getVersion());
			//info.setLoginVersionNo(role.getVersionNo());
			info.setRoleCareer(String.valueOf(role.getCareer()));
			info.setUserId(role.getUserId());
			info.setRoleId(role.getRoleId());
			info.setRoleName(role.getRoleName());
			info.setUserId(role.getUserId());
			info.setRoleRegTime(role.getCreateTime());
			info.setUserRegTime(role.getUserRegTime());
			info.setPay(String.valueOf(role.isPayUser()?0:1));//0：付费,1:未付费
			info.setUserName(role.getUserName());
			info.setIpInfo(role.getLoginIp());
			info.setOstype(role.getOstype());
			this.execute(role, info);
		}catch(Exception e){
			logger.error("",e);
		}
		
	}
	@Override
	public void roleLogoutLog(RoleInstance role) {
		try{
			if(null == role){
				return ;
			}
			Date now = new Date();
			RoleLogout info = new RoleLogout();
			info.setExitLevel(String.valueOf(role.getLevel()));
			info.setExitMapId(role.getMapId());
			info.setExitTime(now);
			info.setLoginTime(role.getLastLoginTime());
			info.setLoginLevel(String.valueOf(role.getLoginLevel()));
			//info.setLoginVersion(role.getVersion());
			//info.setLoginVersionNo(role.getVersionNo());
			info.setRoleCareer(String.valueOf(role.getCareer()));
			info.setRoleId(role.getRoleId());
			info.setRoleName(role.getRoleName());
			info.setUserId(role.getUserId());
			info.setRoleRegTime(role.getCreateTime());
			info.setUserName(role.getUserName());
			this.execute(role, info);
		}catch(Exception e){
			logger.error("",e);
		}
	}
	
	@Override
	public void roleOnlineLog() {
		try{
			RoleOnline ro = new RoleOnline();
			ro.setOnLineNum(GameContext.getOnlineCenter().onlineUserSize());
			ro.setConnection(GameContext.getMinaServer().getAcceptor().getManagedSessionCount());
			this.execute(null, ro);
		}catch(Exception e){
			logger.error("",e);
		}
		
	}

	@Override
	public void roleRegisterLog(RoleInstance role, int roleCount) {
		try{
			RoleRegister rg = new RoleRegister();
			rg.setRoleId(role.getRoleId());
			rg.setRoleName(role.getRoleName());
			rg.setRoleSex(String.valueOf(role.getSex()));
			rg.setRoleCareer((String.valueOf(role.getCareer())));
			rg.setRoleCount(roleCount);
			this.execute(role, rg);
		}catch(Exception e){
			logger.error("",e);
		}
	}
	
	/*@Override
	public void userVIPLog(RoleInstance role,GoodsVipCard gvc,int vipType){
		try{
			UserVIP info = new UserVIP();
			info.setRoleId(role.getRoleId());
			info.setRoleLevel(String.valueOf(role.getLevel()));
			info.setRoleName(role.getRoleName());
			info.setVipCardId(String.valueOf(gvc.getId()));
			info.setVipCardName(gvc.getName());
			info.setVipCardNum(String.valueOf(1));//一次只消耗一个
			info.setVipLevel(String.valueOf(gvc.getLvLimit()));
			info.setVipType(String.valueOf(vipType));
			this.execute(role, info);
		}catch(Exception e){
			logger.error("",e);
		}
		
	}*/
	
	@Override
	public void compassLog(RoleInstance role, short id, int extractionType){
		try{
			Compass info = new Compass();
			info.setCircleType(String.valueOf(id));
			info.setExtractionType(String.valueOf(extractionType));
			info.setRoleId(role.getRoleId());
			info.setRoleLevel(String.valueOf(role.getLevel()));
			info.setRoleName(role.getRoleName());
			this.execute(role, info);
		}catch(Exception e){
			logger.error("",e);
		}
		
	}
	
//	@Override
//	public void offhookLog(RoleInstance role,int hookType){
//		try{
//			Offhook info = new Offhook();
//			info.setCurrDate(new Date());
//			info.setHookType(String.valueOf(hookType));
//			info.setRoleId(role.getRoleId());
//			info.setRoleLevel(String.valueOf(role.getLevel()));
//			info.setRoleName(role.getRoleName());
//			this.execute(role, info);
//			
//		}catch(Exception e){
//			logger.error("",e);
//		}
//	}
	
//	@Override
//	public void mountsLog(RoleInstance role,GoodsMetamorphic goods,int type){
//		try{
//			RoleMount roleMount = role.getRoleMount();
//			if(null == roleMount){
//				return ;
//			}
//			Mounts info = new Mounts();
//			info.setCurrDate(new Date());
//			if(null != goods){
//				info.setMountsId(String.valueOf(goods.getId()));
//				info.setMountsName(goods.getName());
//			}
//			info.setRoleId(role.getRoleId());
//			info.setRoleLevel(String.valueOf(role.getLevel()));
//			info.setRoleName(role.getRoleName());
//			info.setType(String.valueOf(type));
//			info.setMountsLadder(String.valueOf(roleMount.getQuality()));
//			Map<Byte, MountAdvanceAttri> advanceAttris = roleMount.getAdvanceAttris();
//			info.setMountsGenggu(String.valueOf(advanceAttris.get(MountAdvanceAttriType.GENGGU.getType()).getStar()));
//			info.setMountsAccurate(String.valueOf(advanceAttris.get(MountAdvanceAttriType.ACCURATE.getType()).getStar()));
//			info.setMountsSpirt(String.valueOf(advanceAttris.get(MountAdvanceAttriType.SPIRITUALITY.getType()).getStar()));
//			info.setMountsTough(String.valueOf(advanceAttris.get(MountAdvanceAttriType.TOUGH.getType()).getStar()));
//			info.setMountsSoul(String.valueOf(advanceAttris.get(MountAdvanceAttriType.SOUL.getType()).getStar()));
//			this.execute(role, info);
//		}catch(Exception e){
//			logger.error("",e);
//		}
//		
//	}
	
	@Override
	public void bossDeathLog(NpcInstance npc){
		try{
			BossDeath info = new BossDeath();
			info.setBossId(npc.getNpcid());
			info.setBossName(npc.getNpcname());
			this.execute(null, info);
		}catch(Exception e){
			logger.error("",e);
		}
	}
	
	
	@Override
	public void activePartLog(RoleInstance role, short activeId, String activeName, int activeType){
		try{
			ActivityPart info = new ActivityPart();
			info.setActivityId(String.valueOf(activeId));
			info.setActivityName(activeName);
			info.setActivityType(String.valueOf(activeType));
			info.setRoleId(role.getRoleId());
			info.setRoleLevel(String.valueOf(role.getLevel()));
			info.setRoleName(role.getRoleName());
			this.execute(role, info);
		}catch(Exception e){
			logger.error("",e);
		}
	}
	
	
	@Override
	public void equipStrengLog(RoleInstance role,StrengthenParam param,StrengthenResult stResult){
		try{
			RoleGoods rg = param.getEquipGoods();
			int currStarNum = rg.getStrengthenLevel();
			GoodsEquipment eq = GameContext.getGoodsApp().getGoodsTemplate(GoodsEquipment.class, rg.getGoodsId());
			EquipStreng info = new EquipStreng();
			info.setEquipInstanceId(rg.getId());
			info.setEquipLevel(String.valueOf(eq.getLvLimit()));
			//info.setEquipLocationId(String.valueOf(eq.getEquipslotType()));
			info.setEquipModelId(String.valueOf(rg.getGoodsId()));
			info.setEquipName(eq.getName());
			info.setEquipQualityId(String.valueOf(eq.getQualityType()));
			info.setRoleId(role.getRoleId());
			info.setRoleLevel(String.valueOf(role.getLevel()));
			info.setRoleName(role.getRoleName());
			info.setStrengLevel(String.valueOf(currStarNum));
			info.setStrengResult("2");
			//info.setStrengthenType(String.valueOf(param.getStrengthentype()));
			if(stResult.isSuccess()){
				if(stResult.getStarNumChanged()>0){
					info.setStrengResult("1");
					int num = currStarNum - 1;
					info.setStrengLevel(String.valueOf(num));
				}else{
					int num = currStarNum + Math.abs(stResult.getStarNumChanged());
					info.setStrengLevel(String.valueOf(num));
				}
			}
			info.setStrengEndLevel(String.valueOf(currStarNum));
			this.execute(role, info);
		}catch(Exception e){
			logger.error("",e);
		}
		
	}
	
	
	@Override
	public void roleKillMonsterLog(RoleInstance role , NpcInstance npc, int silverMoney){
		try{
			RoleKillMonster info = new RoleKillMonster();
			info.setSilverMoney(silverMoney);
			info.setExp(npc.getExp());
			info.setNpcId(npc.getNpc().getNpcid());
			if(null != role){
				info.setRoleId(role.getRoleId());
				info.setRoleLevel(role.getLevel());
				info.setRoleName(role.getRoleName());
			}
			this.execute(role, info);
		}catch(Exception e){
			logger.error("",e);
		}
	}
	
	@Override
	public void roleTaskLog(RoleInstance role,int questType,int questId,QuestAward award){
		try{
			Quest quest = GameContext.getQuestApp().getQuest(questId);
			Task info = new Task();
			info.setMissionId(String.valueOf(quest.getQuestId()));
			info.setMissionLevel(String.valueOf(quest.getQuestLevel()));
			info.setMissionName(quest.getQuestName());
			info.setMissionOpype(String.valueOf(questType));
			info.setMissionType(String.valueOf(quest.getQuestType().getType()));//任务类型的值
			info.setRoleId(role.getRoleId());
			info.setRoleLevel(String.valueOf(role.getLevel()));
			info.setRoleName(role.getRoleName());
			if(null != award && questType == 1){//表示完成
				info.setMissionExp(String.valueOf(award.getAttrAwardValue(AttributeType.exp)));
			}
			this.execute(role, info);
		}catch(Exception e){
			logger.error("",e);
		}
	}
	
	
	@Override
	public void roleMoneyLog(RoleInstance role,AttributeType attriType,OutputConsumeType ocType,int changeNum,String remark){
		try{
			RoleMoneyRecord info = new RoleMoneyRecord();
			info.setChangeNum(changeNum);
			info.setCurrNum(role.get(attriType));
			info.setMoneyType(attriType.getType());
			info.setRoleId(role.getRoleId());
			info.setRoleLevel(role.getLevel());
			info.setRoleName(role.getRoleName());
			info.setSourceType(ocType.getBigType());
			info.setSystemType(ocType.getType());
			info.setRemark(remark);
			this.execute(role, info);
		}catch(Exception e){
			logger.error("",e);
		}
	}
	
	@Override
	public void rolePayLog(RoleInstance role, int payGold, int payChannelId){
		try{
			if(null == role) {
				return;
			}
			
			int firstPay = role.getRolePayGold() == 0 ? 1 : 0;
			
			RolePay info = new RolePay();
			info.setRoleId(role.getRoleId());
			info.setRoleLevel(role.getLevel());
			info.setRoleName(role.getRoleName());
			info.setPayGold(payGold);
			info.setFirstPay(firstPay);
			info.setPayChannelId(payChannelId);
			this.execute(role, info);
			
		}catch(Exception e){
			logger.error("StatLogApp.rolePayLog error",e);
		}
	}
	
	@Override
	public StatRoleGoodsRecord createRoleGoodsRecord(RoleInstance role, RoleGoods roleGoods, int overlapChangeCount, String remark, OutputConsumeType ocType){
		try{
			StatRoleGoodsRecord record = new StatRoleGoodsRecord();
			record.setCurrDate(new Date());
			record.setRemark(remark);
			if(null != ocType){
				record.setSourceType(ocType.getBigType());
				record.setSystemType(ocType.getType());
			}
			record.setBindType(roleGoods.getBind());
			record.setChangeNum(overlapChangeCount);
			GoodsBase gb = GameContext.getGoodsApp().getGoodsBase(roleGoods.getGoodsId());
			record.setGoodsLevel(gb.getLevel());
			record.setGoodsColor(gb.getQualityType());
			record.setGoodsId(roleGoods.getGoodsId());
			record.setGoodsName(gb.getName());
			record.setGoodsType(gb.getGoodsType());
			record.setGoodsLocation(-1);
			/*//如果是装备则得到此装备的位置
			if(gb.isEquipment()){
				GoodsEquipment ge = (GoodsEquipment)gb;
				record.setGoodsLocation(ge.getEquipslotType());
			}*/
			record.setInstanceId(roleGoods.getId());
			record.setInstanceNum(roleGoods.getCurrOverlapCount());
			return record;
		}catch(Exception e){
			logger.error(this.getClass().getName() + ".createRoleGoodsRecord error: ",e);
		}
		return null;
	}
	
	@Override
	public void roleGoodsLog(RoleInstance role, OutputConsumeType ocType, String remark, List<StatRoleGoodsRecord> list){
		try{
			String roleId = role.getRoleId();
			String roleName = role.getRoleName();
			int roleLevel = role.getLevel();
			String eventId = String.valueOf(System.currentTimeMillis());
			for(StatRoleGoodsRecord record : list){
				RoleGoodsRecord info = new RoleGoodsRecord();
				info.setRoleId(roleId);
				info.setRoleName(roleName);
				info.setRoleLevel(roleLevel);
				info.setSourceType(record.getSourceType());
				info.setSystemType(record.getSystemType());
				if(null != ocType){
					info.setSourceType(ocType.getBigType());
					info.setSystemType(ocType.getType());
				}
				info.setRemark(record.getRemark());
				if(!Util.isEmpty(remark)){
					info.setRemark(remark);
				}
				info.setCurrDate(record.getCurrDate());
				info.setEventId(eventId);
				info.setGoodsId(String.valueOf(record.getGoodsId()));
				info.setGoodsName(record.getGoodsName());
				info.setGoodsType(record.getGoodsType());
				info.setBindType(record.getBindType());
				info.setGoodsLevel(record.getGoodsLevel());
				info.setGoodsLocation(record.getGoodsLocation());
				info.setGoodsColor(record.getGoodsColor());
				info.setChangeNum(record.getChangeNum());
				info.setInstanceId(record.getInstanceId());
				info.setInstanceNum(record.getInstanceNum());
				this.execute(role, info);
			}
		}catch(Exception e){
			logger.error(this.getClass().getName() + ".roleGoodsLog error: ",e);
		}
	}
	
	@Override
	public void roleGoodsLog(RoleInstance role, RoleGoods roleGoods, int overlapChangeCount, String remark, OutputConsumeType ocType) {
		try {
			RoleGoodsRecord info = new RoleGoodsRecord();
			info.setRoleId(role.getRoleId());
			info.setRoleName(role.getRoleName());
			info.setRoleLevel(role.getLevel());
			info.setEventId(String.valueOf(System.currentTimeMillis()));
			info.setCurrDate(new Date());
			info.setRemark(remark);
			if(null != ocType){
				info.setSourceType(ocType.getBigType());
				info.setSystemType(ocType.getType());
			}
			info.setBindType(roleGoods.getBind());
			info.setChangeNum(overlapChangeCount);
			GoodsBase gb = GameContext.getGoodsApp().getGoodsBase(roleGoods.getGoodsId());
			info.setGoodsLevel(gb.getLevel());
			info.setGoodsColor(gb.getQualityType());
			info.setGoodsId(String.valueOf(roleGoods.getGoodsId()));
			info.setGoodsName(gb.getName());
			info.setGoodsType(gb.getGoodsType());
			info.setGoodsLocation(-1);
			/*//如果是装备则得到此装备的位置
			if(gb.isEquipment()){
				GoodsEquipment ge = (GoodsEquipment)gb;
				info.setGoodsLocation(ge.getEquipslotType());
			}*/
			info.setInstanceId(roleGoods.getId());
			info.setInstanceNum(roleGoods.getCurrOverlapCount());
			this.execute(role, info);
		} catch (Exception e) {
			this.logger.error(this.getClass().getName() + ".roleGoodsLog error: ", e);
		}
	}
		
	@Override
	public void roleExpLog(RoleInstance role,int changeNum,String remark,OutputConsumeType ocType){
		try{
			RoleExpRecord info = new RoleExpRecord();
			info.setChangeNum(changeNum);
			info.setRoleId(role.getRoleId());
			info.setRoleLevel(String.valueOf(role.getLevel()));
			info.setRoleName(role.getRoleName());
			info.setSourceType(ocType.getBigType());
			info.setSystemType(ocType.getType());
			info.setTotalExp(String.valueOf(role.get(AttributeType.exp)));
			info.setRemark(remark);
			this.execute(role, info);
		}catch(Exception e){
			logger.error("",e);
		}
		
	}
	@Override
	public void setArgs(Object arg0) {
		
	}

	@Override
	public void start() {
		app.initLog4j(
				String.valueOf(GameContext.getAppId()), 
				String.valueOf(GameContext.getServerId()), 
				this.log4jPath, 
				this.logDataPath,"game");
	}

	@Override
	public void stop() {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void printCat(){
		app.initAllLogCat(pkgName);
	}
	@Override
	public void clientTargetCollect(RoleInstance role, String[] targetInfo) {
		try{
			ClientInfo info = new ClientInfo();
			info.setRoleId(role.getRoleId());
			info.setRoleName(role.getRoleName());
			info.setMsgStrings(targetInfo);
			if(null != role){
				info.setLoginIp(role.getLoginIp());
			}
			this.execute(role, info);
		}catch(Exception e){
			logger.error("",e);
		}
	}
	
	
//	@Override
//	public void factionWarlordsLog(Faction faction, short activeId, int index, Date endTime) {
//		try{
//			FactionWarlords info = new FactionWarlords();
//			info.setSocietyId(faction.getFactionId());
//			info.setSocietyName(faction.getFactionName());
//			info.setSocietyWarId(String.valueOf(activeId));
//			info.setRanking(String.valueOf(index));
//			info.setCreateTime(faction.getCreateDate());
//			info.setCurrDate(new Date());
//			info.setEndTime(endTime);
//			this.execute(null, info);
//		}catch(Exception e){
//			this.logger.error("StatLogApp.warlordsFactionLog error: ", e);
//		}
//	}

	

	@Override
	public void mailDelInfoDelLog(String mailId, RoleInstance role) {
		try{
			MailSendInfo info = new MailSendInfo();
			if(null != role){
				info.setAccRoleName(role.getRoleName());
				info.setAccUserId(role.getUserId());
				info.setAccUserName(role.getUserName());
				info.setAccRoleId(role.getRoleId());
			}
			info.setEmailId(mailId);
			info.setSendType("0");
			info.setType(String.valueOf(MailLogType.del.getType()));
			info.setExtractTime(new Date());
			this.execute(role, info);
		}catch(Exception e){
			logger.error("",e);
		}
	}
	
	@Override
	public void roleOnlineTimeLog(RoleInstance role, Date now) {
		try{
			RoleOnlineTime roleOnlineTime = new RoleOnlineTime();
			if(null != role) {
				roleOnlineTime.setRoleId(role.getRoleId());
				roleOnlineTime.setRoleName(role.getRoleName());
				Date dayLoginTime = role.getDayLoginTime();
				if(null == dayLoginTime) {
					dayLoginTime = new Date();
				}
				int time = (int)DateUtil.dateDiffMinute(now, role.getDayLoginTime());
				roleOnlineTime.setTime(time);
				
				//首次充值日志
				int payUser = role.getRolePayGold() > 0 ? 1 : 0;
				roleOnlineTime.setPayUser(payUser);
				this.execute(role, roleOnlineTime);
			}
		}catch(Exception e){
			logger.error("StatLogApp.roleOnlineTimeLog error:",e);
		}
	}
	
	@Override
	public void allRoleOnlineTimeLog(){
		try{
			Date now = new Date();
			for(RoleInstance role : GameContext.getOnlineCenter().getAllOnlineRole()){
				if(null == role){
					continue;
				}
				this.roleOnlineTimeLog(role, now);
				role.setDayLoginTime(now);
			}
		}catch(Exception e){
			logger.error("StatLogApp.allRoleOnlineTimeLog error:",e);
		}
	}
	
	@Override
	public void roleConsumeLog(RoleInstance role, int consumeGold, OutputConsumeType ocType){
		try{
			if(null == role) {
				return;
			}
			
			int firstConsume = role.getRoleConsumeGold() == 0 ? 1 : 0;
			
			RoleConsume info = new RoleConsume();
			info.setRoleId(role.getRoleId());
			info.setRoleLevel(role.getLevel());
			info.setRoleName(role.getRoleName());
			info.setSourceType(ocType.getBigType());
			info.setSystemType(ocType.getType());
			info.setConsumeGold(consumeGold);
			info.setFirstConsume(firstConsume);
			this.execute(role, info);
		}catch(Exception e){
			logger.error("StatLogApp.roleFirstConsumeLog error",e);
		}
	}
	
	@Override
	public void roleQuickBuyLog(RoleInstance role, List<QuickBuyGoods> quickBuyGoodsList, OutputConsumeType ocType){
		try{
			if(null == role) {
				return;
			}
			
			if(Util.isEmpty(quickBuyGoodsList)) {
				return;
			}
			
			for(QuickBuyGoods quickBuyGoods : quickBuyGoodsList){
				if(null == quickBuyGoods){
					continue;
				}
				//计算需要购买的道具数量
				quickBuyGoods.isRoleGoodsEnough(role);
				int goodsId = quickBuyGoods.getGoodsId();
				GoodsBase gb = GameContext.getGoodsApp().getGoodsBase(goodsId);
				if(null == gb) {
					continue;
				}
				
				RoleQuickBuy info = new RoleQuickBuy();
				info.setRoleId(role.getRoleId());
				info.setRoleLevel(role.getLevel());
				info.setRoleName(role.getRoleName());
				info.setSourceType(ocType.getBigType());
				info.setSystemType(ocType.getType());
				info.setConsumeGold(quickBuyGoods.getPayGoldMoney(role));
				info.setGoodsId(goodsId);
				info.setGoodsName(gb.getName());
				info.setPayGoodsNum(quickBuyGoods.getPayGoodsNum(role));
				info.setDelGoodsNum(quickBuyGoods.getDelRoleGoodsNum(role));
				info.setPrice(quickBuyGoods.getGoldPrice());
				this.execute(role, info);
			}
		}catch(Exception e){
			logger.error("StatLogApp.roleQuickBuyLog error",e);
		}
	}
	
	@Override
	public void roleShopBuy(RoleInstance role, int goodsId, int goodsPrice, int goodsNum, int consumeGold, AttributeType attriType, OutputConsumeType ocType){
		try{
			if(null == role) {
				return;
			}
			
			GoodsBase gb = GameContext.getGoodsApp().getGoodsBase(goodsId);
			if(null == gb) {
				return;
			}
			
			RoleShopBuy info = new RoleShopBuy();
			info.setRoleId(role.getRoleId());
			info.setRoleLevel(role.getLevel());
			info.setRoleName(role.getRoleName());
			info.setConsumeGold(consumeGold);
			info.setGoodsId(goodsId);
			info.setGoodsName(gb.getName());
			info.setGoodsPrice(goodsPrice);
			info.setGoodsNum(goodsNum);
			info.setMoneyType(attriType.getType());
			info.setSourceType(ocType.getBigType());
			info.setSystemType(ocType.getType());
			this.execute(role, info);
		}catch(Exception e){
			logger.error("StatLogApp.roleFirstConsumeLog error",e);
		}
	}

	@Override
	public void roleLevelDistributionLog() {
		try{
			List<RoleLevelDistribution> list = GameContext.getUserRoleApp().getLevelDistributionList();
			for(RoleLevelDistribution item : list){
				RoleLevelNumber logInfo = new RoleLevelNumber();
				logInfo.setLevel(item.getLevel());
				logInfo.setNumber(item.getNumber());
				this.execute(null, logInfo);
			}
		}catch(Exception e){
			logger.error("StatLogApp.roleLevelDistributionLog error",e);
		}
	}

	@Override
	public void tradingLog(long id, String roleId, String targetRoleId, List<RoleGoods> goods, int money, OutputConsumeType ocType) {
		try{
			if(money > 0){
				TradingRecord info = new TradingRecord();
				info.setRoleId(roleId);
				info.setTargetRoleId(targetRoleId);
				info.setTradingId(id);
				info.setMoney(money);
				this.execute(null, info);
			}
			if(Util.isEmpty(goods)){
				return;
			}
			for(RoleGoods roleGoods : goods){
				if(null == roleGoods){
					continue;
				}
				int goodsId = roleGoods.getGoodsId();
				TradingRecord info = new TradingRecord();
				info.setRoleId(roleId);
				info.setTargetRoleId(targetRoleId);
				info.setTradingId(id);
				info.setGoodsId(goodsId);
				info.setGoodsInstanceId(roleGoods.getId());
				GoodsBase gb = GameContext.getGoodsApp().getGoodsBase(goodsId);
				if(null != gb){
					info.setGoodsName(gb.getName());
				}
				info.setGoodsNum(roleGoods.getCurrOverlapCount());
				this.execute(null, info);
			}
		}catch(Exception e){
			logger.error("StatLogApp.tradingLog error",e);
		}
	}
	
	@Override
	public void roleQuest(RoleInstance role, Quest quest, int exp, int silverMoney) {
		try{
			RoleQuest info = new RoleQuest();
			info.setExp(exp);
			info.setSilverMoney(silverMoney);
			info.setQuestId(quest.getQuestId());
			info.setQuestType(quest.getQuestType().getType());
			info.setQuestAcceptType(quest.getQuestAcceptType().getType());
			if(null != role){
				info.setRoleId(role.getRoleId());
				info.setRoleLevel(role.getLevel());
				info.setRoleName(role.getRoleName());
			}
			this.execute(role, info);
		}catch(Exception e){
			logger.error("StatLogApp.roleQuest error",e);
		}
	}

	@Override
	public void goldMoneyDayRemainLog() {
		try{
			long goldMoney = GameContext.getRoleDAO().getGoldMoneyReamin();
			GoldMoneyDayRemain info = new GoldMoneyDayRemain();
			info.setGoldMoney(goldMoney);
			this.execute(null, info);
		}catch(Exception e){
			logger.error("StatLogApp.GoldMoneyDayRemainLog error",e);
		}
	}
	
	@Override
	public void goodsFallLog(RoleInstance role, NpcTemplate npc, List<GoodsOperateBean> goodsList) {
		try{
			if(Util.isEmpty(goodsList)){
				return;
			}
			for(GoodsOperateBean bean : goodsList){
				if(null == bean){
					continue;
				}
				int goodsId = bean.getGoodsId();
				GoodsBase gb = GameContext.getGoodsApp().getGoodsBase(goodsId);
				if(null == gb){
					continue;
				}
				GoodsFall info = new GoodsFall();
				info.setRoleId(role.getRoleId());
				info.setRoleName(role.getRoleName());
				info.setNpcId(npc.getNpcid());
				info.setNpcName(npc.getNpcname());
				info.setGoodsId(goodsId);
				info.setGoodsName(gb.getName());
				info.setGoodsNum(bean.getGoodsNum());
				info.setBindingType(bean.getBindType().getType());
				this.execute(role, info);
			}
		}catch(Exception e){
			logger.error("StatLogApp.goodsFallLog error",e);
		}
	}
}
