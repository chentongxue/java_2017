package sacred.alliance.magic.debug.action;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import platform.message.item.MercuryRecordItem;
import platform.message.response.C5401_MercurySearchRespMessage;
import sacred.alliance.magic.app.social.config.SocialIntimateConfig;
import sacred.alliance.magic.app.team.Team;
import sacred.alliance.magic.base.AttributeType;
import sacred.alliance.magic.base.RoleInfoType;
import sacred.alliance.magic.base.StorageType;
import sacred.alliance.magic.constant.Cat;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.core.action.ActionSupport;
import sacred.alliance.magic.domain.DiscountDbInfo;
import sacred.alliance.magic.domain.GoodsBase;
import sacred.alliance.magic.domain.GoodsEquipment;
import sacred.alliance.magic.domain.RoleGoods;
import sacred.alliance.magic.domain.RoleLevelup;
import sacred.alliance.magic.domain.RolePayRecord;
import sacred.alliance.magic.domain.RoleSocialRelation;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.vo.AbstractRole;
import sacred.alliance.magic.vo.MapInstance;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.mail.domain.Mail;
import com.game.draco.app.quest.Quest;
import com.game.draco.app.quest.domain.RoleQuestLogInfo;
import com.game.draco.app.skill.domain.RoleSkillStat;
import com.game.draco.debug.message.item.BagItem;
import com.game.draco.debug.message.item.BaseItem;
import com.game.draco.debug.message.item.DiscountInfoItem;
import com.game.draco.debug.message.item.EquipItem;
import com.game.draco.debug.message.item.MailInfoItem;
import com.game.draco.debug.message.item.MercuryInfoItem;
import com.game.draco.debug.message.item.MountInfo;
import com.game.draco.debug.message.item.PropertyItem;
import com.game.draco.debug.message.item.RelationItem;
import com.game.draco.debug.message.item.SkillItem;
import com.game.draco.debug.message.item.TaskItem;
import com.game.draco.debug.message.item.TeamItem;
import com.game.draco.debug.message.item.UserPayRecordItem;
import com.game.draco.debug.message.request.C10020_FindRoleInfoReqMessage;
import com.game.draco.debug.message.response.C10020_FindRoleInfoRespMessage;
import com.game.draco.message.item.AuctionMyShelfItem;
import com.game.draco.message.item.GoodsLiteNamedItem;

public class LookRoleAction extends ActionSupport<C10020_FindRoleInfoReqMessage>{
	
	private static List<AttributeType> attributeList = new ArrayList<AttributeType>();
	
	@Override
	public Message execute(ActionContext arg0, C10020_FindRoleInfoReqMessage reqMessage) {
		C10020_FindRoleInfoRespMessage resp = new C10020_FindRoleInfoRespMessage();
		try {
			String rolename = reqMessage.getRoleName();
			String type = reqMessage.getType();
			String[] types = type.split(",");
			RoleInstance role = GameContext.getUserRoleApp().getRoleByRoleName(rolename);
			//角色基本信息，都已经查出来了，就直接给赋值吧
			resp.setBaseItem(this.getBaseItem(role));
			for (String str : types) {
				RoleInfoType infoType = RoleInfoType.get(Integer.valueOf(str));
				if (null == infoType) {
					continue;
				}
				if(RoleInfoType.property == infoType){
					resp.setPropertyItems(this.getProperty(role));
				}else if(RoleInfoType.equip == infoType){
					resp.setEquipItem(this.getEquip(role));
				}else if(RoleInfoType.bag == infoType){
					resp.setBagItems(this.getBag(role));
				}else if(RoleInfoType.skill == infoType){
					resp.setSkillItems(this.getSkill(role));
				}else if(RoleInfoType.friend == infoType){
					resp.setFrienditems(this.getFriendList(role));
				}else if(RoleInfoType.task == infoType){
					resp.setTaskItems(this.getTask(role));
				}else if(RoleInfoType.team == infoType){
					resp.setTeamItems(this.getTeam(role));
				}else if(RoleInfoType.mount == infoType){
					resp.setMountInfo(this.getMountInfo(role));
				}else if(RoleInfoType.mercury == infoType){
					resp.setMercuryInfoList(this.getMercuryInfoList(role));
				}else if(RoleInfoType.userpay == infoType){
					resp.setUserPayRecord(this.getUserPayRecord(role));
				}else if(RoleInfoType.discount == infoType){
					resp.setDiscountInfoList(this.getDiscountInfoList(role));
				}else if(RoleInfoType.mailList == infoType){
					resp.setMailList(this.getMailList(role));
				}else if(RoleInfoType.warehouse == infoType){
					resp.setWarehouseItems(this.getWarehouse(role));
				}
			}
			return resp;
		}catch(Exception e){
			logger.error("LookRoleAction error: ",e);
			return resp;
		}
	}
	
	private List<PropertyItem> getProperty(RoleInstance role){
		List<PropertyItem> items = new ArrayList<PropertyItem>();
		for (AttributeType attributeType : attributeList) {
			int attValue = role.get(attributeType);
			StringBuffer value = new StringBuffer();
			value.append(attValue);
			PropertyItem proItem = new PropertyItem();
			proItem.setName(attributeType.getName());
			if (attributeType == AttributeType.exp) {
				RoleLevelup roleLevelup = GameContext.getAttriApp().getLevelup(role.getLevel());
				value.append("/").append(roleLevelup.getUpExp());
			}
			// 生命:2000/2100
			if (AttributeType.curHP == attributeType) {
				value.append("/").append(role.getMaxHP());
			}
			// 法力:1900/2000
			if (AttributeType.curMP == attributeType) {
				value.append("/").append(role.getMaxMP());
			}
			proItem.setValue(value.toString());
			items.add(proItem);
		}
		return items;
	}
	
	private List<EquipItem> getEquip(RoleInstance role){
		List<EquipItem> items = new ArrayList<EquipItem>();
		Collection<RoleGoods> list = null;
		if(GameContext.getOnlineCenter().getRoleInstanceByRoleId(role.getRoleId()) != null){
			list = role.getEquipBackpack().getAllGoods();
		}else{
			list = GameContext.getBaseDAO().selectList(RoleGoods.class,"roleId",role.getRoleId(), "storageType", StorageType.equip.getType() + "");
		}
		for (RoleGoods roleGoods : list) {
			EquipItem item = new EquipItem();
			GoodsEquipment equ = null;
			try{
				equ = (GoodsEquipment) GameContext.getGoodsApp().getGoodsBase(roleGoods.getGoodsId());
			}catch(Exception e){
				this.logger.error("LookRoleAction getEquip error: ", e);
				return items;
			}
			item.setGoodsId(roleGoods.getId());
			item.setEquipName(equ.getName());
			//item.setEquipDurable(roleGoods.getCurrDurable());
			item.setPlace(equ.getEquipslotType());
			item.setBind((byte) equ.getBindType());
			items.add(item);
		}
		return items;
	}
	
	private List<BagItem> getBag(RoleInstance role){
		Collection<RoleGoods> list = null;
		if(GameContext.getOnlineCenter().getRoleInstanceByRoleId(role.getRoleId()) != null){
			list = role.getRoleBackpack().getAllGoods();
		}else{
			list = GameContext.getBaseDAO().selectList(RoleGoods.class,"roleId",role.getRoleId(), "storageType", StorageType.bag.getType() + "");
		}
		return getBagItems(list);
	}
	
	private List<SkillItem> getSkill(RoleInstance role){
		List<SkillItem> items = new ArrayList<SkillItem>();
		List<RoleSkillStat> skillList = GameContext.getUserSkillApp().selectRoleSkillList(role.getRoleId());
		for(RoleSkillStat skill : skillList){
			SkillItem item = new SkillItem();
			item.setSkillId(skill.getSkillId());
			item.setSkillName(GameContext.getSkillApp().getSkill(skill.getSkillId()).getName());
			item.setLevel((byte)skill.getSkillLevel());
			items.add(item);
		}
		return items;
	}
	
	private List<TaskItem> getTask(RoleInstance role){
		List<TaskItem> items = new ArrayList<TaskItem>();
		try{
			for(RoleQuestLogInfo info : role.getQuestLogMap().values()){
				TaskItem item = new TaskItem();
				item.setTaskId(info.getQuestId());
				Quest quest = GameContext.getQuestApp().getQuest(info.getQuestId());
				if(quest == null){
					continue;
				}
				item.setTaskName(quest.getQuestName());
				item.setPhase(info.getPhase());
				items.add(item);
			}
			return items;
		}catch(Exception e){
			this.logger.error("LookRoleAction getTask error: ", e);
			return items;
		}
	}
	
	private List<TeamItem> getTeam(RoleInstance role){
		List<TeamItem> items = new ArrayList<TeamItem>();
		Team team = role.getTeam();
		if(team == null){
			return items;
		}
		Collection<AbstractRole> roles= team.getMembers();
		for(AbstractRole roleTemp:roles){
			TeamItem item = new TeamItem();
			item.setRoleName(((RoleInstance)roleTemp).getRoleName());
			if(team.isLeader(roleTemp)){
				item.setPosition((byte)1);
			}
			items.add(item);
		}
		return items;
	}
	
	private BaseItem getBaseItem(RoleInstance role){
		BaseItem item = new BaseItem();
		item.setOnline(GameContext.getOnlineCenter().isOnlineByRoleId(
				role.getRoleId()) ? (byte) 1 : (byte) 0);
		item.setUserId(role.getUserId());
		item.setUserName(role.getUserName());
		item.setChannelId(role.getChannelId());
		item.setChannelUserId(role.getChannelUserId());
		item.setRoleId(role.getRoleId());
		item.setRoleName(role.getRoleName());
		item.setSex(role.getSex());
		item.setCampId(role.getCampId());
		item.setCareer((byte)role.getCareer());
		item.setLevel((byte)role.getLevel());
		item.setMapId(role.getMapId());
		item.setMapName(GameContext.getMapApp().getMap(role.getMapId()).getMapConfig().getMapdisplayname());
		item.setLineType(role.getLineId());
		item.setMapX((short)role.getMapX());
		item.setMapY((short)role.getMapY());
		item.setTotalOnlineTime(role.getHistoryOnlineTime());
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		item.setCreateTime(format.format(role.getCreateTime()));
		if(role.getFrozenEndTime()!=null){
			item.setFrozenEndTime(format.format(role.getFrozenEndTime()));
		}else{
			item.setFrozenEndTime("-");
		}
		item.setForbidType((byte)role.getForbidType());
		if(role.getForbidEndTime()!=null){
			item.setForbidEndTime(format.format(role.getForbidEndTime()));
		}else{
			item.setForbidEndTime("-");
		}
		return item;
	}
	
	
	/**
	 * 获取坐骑信息
	 * @param role
	 * @return
	 */
	private MountInfo getMountInfo(RoleInstance role){
		
		String roleId = role.getRoleId();
		//TODO
		//RoleMount mount = null;
		//在线从角色身上取，不在线从库中查询
		/*if(GameContext.getOnlineCenter().isOnlineByRoleId(roleId)){
			mount = role.getRoleMount();
		}else{
			mount = GameContext.getUserMountApp().selectRoleMount(roleId);
		}*/
		
		return null ;
		/*MountInfo info = new MountInfo();
		info.setRoleId(roleId);
		info.setAtkLevel(mount.getAtkLevel());
		info.setAtkRate(mount.getCriAtkRate());
		info.setBattleScore(mount.getBattleScore());
		info.setCriAtkLevel(mount.getCriAtkLevel());
		info.setCriAtkRate(mount.getCriAtkRate());
		info.setCriRitLevel(mount.getCriRitLevel());
		info.setCriRitRate(mount.getCriRitRate());
		info.setDodgeLevel(mount.getDodgeLevel());
		info.setDodgeRate(mount.getDodgeRate());
		info.setFreshDate(mount.getFreshDate());
		info.setHitLevel(mount.getHitLevel());
		info.setHitRate(mount.getHitRate());
		info.setLastTrainDate(mount.getLastTrainDate());
		info.setMaxHpLevel(mount.getMaxHpLevel());
		info.setMaxHpRate(mount.getMaxHpRate());
		info.setMaxMpLevel(mount.getMaxMpLevel());
		info.setMaxMpRate(mount.getMaxMpRate());
		info.setMountLevel(mount.getMountLevel());
		info.setMountResId(mount.getMountResId());
		info.setMountStatus(mount.getMountStatus());
		return info;*/
	}
	
	private List<MercuryInfoItem> getMercuryInfoList(RoleInstance role){
		List<MercuryInfoItem> mercuryInfoList = new ArrayList<MercuryInfoItem>();
		try {
			C5401_MercurySearchRespMessage message = GameContext.getAuctionApp().queryRoleAuctionList(role.getRoleId());
			List<MercuryRecordItem> records = message.getRecords();
			if(Util.isEmpty(records)){
				return mercuryInfoList;
			}
			for(MercuryRecordItem record : records){
				AuctionMyShelfItem shelfInfo = GameContext.getAuctionApp().convertMyShelfItem(record);
				if(null == shelfInfo){
					continue;
				}
				MercuryInfoItem item = new MercuryInfoItem();
				item.setType(shelfInfo.getType());
				item.setSecondType(shelfInfo.getSecondType());
				item.setId(shelfInfo.getId());
				item.setPrice(shelfInfo.getPrice());
				item.setPriceType(shelfInfo.getPriceType());
				item.setResId(shelfInfo.getResId());
				item.setLvLimit(shelfInfo.getLvLimit());
				item.setEffectTime(shelfInfo.getEffectTime());
				GoodsLiteNamedItem goodsInfo = shelfInfo.getGoodsInfo();
				if(null != goodsInfo){
					item.setGoodsId(goodsInfo.getGoodsId());
					item.setGoodsName(goodsInfo.getGoodsName());
					item.setGoodsImageId(goodsInfo.getGoodsImageId());
					item.setGoodsLevel(goodsInfo.getGoodsLevel());
					item.setQualityType(goodsInfo.getQualityType());
					item.setBindType(goodsInfo.getBindType());
					item.setNum(goodsInfo.getNum());
				}
				mercuryInfoList.add(item);
			}
			return mercuryInfoList;
		} catch (Exception e) {
			this.logger.error(this.getClass().getName() + ".getMercuryInfoList error: ", e);
			return mercuryInfoList;
		}
	}
	
	private UserPayRecordItem getUserPayRecord(RoleInstance role){
		try {
			RolePayRecord record = null;
			if(GameContext.getOnlineCenter().isOnlineByRoleId(role.getRoleId())){
				record = role.getRolePayRecord();
			}else{
				record = GameContext.getBaseDAO().selectEntity(RolePayRecord.class, "userId", role.getUserId());
			}
			if(null == record){
				return null;
			}
			UserPayRecordItem recordItem = new UserPayRecordItem();
			recordItem.setConsumeMoney(record.getConsumeMoney());
			recordItem.setCurrMoney(record.getCurrMoney());
			recordItem.setLastUpTime(record.getLastUpTime());
			recordItem.setPayGold(record.getPayGold());
			recordItem.setTotalMoney(record.getTotalMoney());
			return recordItem;
		} catch (RuntimeException e) {
			this.logger.error(this.getClass().getName() + ".getUserPayRecord error: ", e);
			return null;
		}
	}
	
	private List<DiscountInfoItem> getDiscountInfoList(RoleInstance role){
		List<DiscountInfoItem> discountInfoList = new ArrayList<DiscountInfoItem>();
		try {
			Map<Integer, DiscountDbInfo> dbInfoMap = null;
			if(GameContext.getOnlineCenter().isOnlineByRoleId(role.getRoleId())){
				dbInfoMap = role.getDiscountDbInfo();
			}else{
				dbInfoMap = GameContext.getActiveDiscountApp().loadRoleActiveDiscount(role.getUserId());
			}
			if(Util.isEmpty(dbInfoMap)){
				return discountInfoList;
			}
			for(DiscountDbInfo dbinfo : dbInfoMap.values()){
				if(null == dbinfo){
					continue;
				}
				DiscountInfoItem item = new DiscountInfoItem();
				item.setActiveId(dbinfo.getActiveId());
				item.setOperateDate(dbinfo.getOperateDate());
				item.setCurDayTotal(dbinfo.getCurDayTotal());
				item.setTotalValue(dbinfo.getTotalValue());
				item.setMeetCond10Count(dbinfo.getMeetCond10Count());
				item.setMeetCond1Count(dbinfo.getMeetCond1Count());
				item.setMeetCond2Count(dbinfo.getMeetCond2Count());
				item.setMeetCond3Count(dbinfo.getMeetCond3Count());
				item.setMeetCond4Count(dbinfo.getMeetCond4Count());
				item.setMeetCond5Count(dbinfo.getMeetCond5Count());
				item.setMeetCond6Count(dbinfo.getMeetCond6Count());
				item.setMeetCond7Count(dbinfo.getMeetCond7Count());
				item.setMeetCond8Count(dbinfo.getMeetCond8Count());
				item.setMeetCond9Count(dbinfo.getMeetCond9Count());
				item.setReward10Count(dbinfo.getMeetCond10Count());
				item.setReward1Count(dbinfo.getReward1Count());
				item.setReward2Count(dbinfo.getReward2Count());
				item.setReward3Count(dbinfo.getReward3Count());
				item.setReward4Count(dbinfo.getReward4Count());
				item.setReward5Count(dbinfo.getReward5Count());
				item.setReward6Count(dbinfo.getReward6Count());
				item.setReward7Count(dbinfo.getReward7Count());
				item.setReward8Count(dbinfo.getReward8Count());
				item.setReward9Count(dbinfo.getReward9Count());
				discountInfoList.add(item);
			}
			return discountInfoList;
		} catch (Exception e) {
			this.logger.error(this.getClass().getName() + ".getUserPayRecord error: ", e);
			return discountInfoList;
		}
	}
	
	private List<RelationItem> getFriendList(RoleInstance role){
		List<RelationItem> friendList = new ArrayList<RelationItem>();
		try {
			String selfRoleId = role.getRoleId();
			for(RoleSocialRelation relation : GameContext.getSocialApp().getFriendList(role)){
				if(null == relation){
					continue;
				}
				RelationItem item = new RelationItem();
				String otherRoleId = relation.getOtherRoleId(selfRoleId);
				item.setRoleId(Integer.valueOf(otherRoleId));
				item.setRoleName(relation.getRoleName(otherRoleId));
				item.setSex(relation.getSex(otherRoleId));
				item.setCamp(relation.getCamp(otherRoleId));
				item.setCareer(relation.getCareer(otherRoleId));
				item.setIntimate(relation.getIntimate());
				SocialIntimateConfig config = relation.getIntimateConfig();
				if(null != config){
					item.setIntimatelevel((byte) config.getLevel());
					item.setMaxIntimate(config.getMaxIntimate());
				}
				RoleInstance otherRole = GameContext.getOnlineCenter().getRoleInstanceByRoleId(otherRoleId);
				if(null != otherRole){
					//角色名从在线角色上取
					item.setRoleName(otherRole.getRoleName());
					item.setOnline((byte) 1);
					item.setRoleLevel((byte) otherRole.getLevel());
					item.setCamp(otherRole.getCampId());
					item.setCareer(otherRole.getCareer());
					MapInstance mapInstance = otherRole.getMapInstance();
					if(null != mapInstance){
						item.setMapName(mapInstance.getMap().getMapConfig().getMapdisplayname());
					}
				}
				friendList.add(item);
			}
		} catch (RuntimeException e) {
			this.logger.error(this.getClass().getName() + ".getFriendList error: ", e);
		}
		return friendList;
	}
	
	private List<MailInfoItem> getMailList(RoleInstance role){
		List<MailInfoItem> mailList = new ArrayList<MailInfoItem>();
		try {
			List<Mail> list = GameContext.getMailApp().selectMailListForDebug(role.getRoleId());
			for(Mail mail : list){
				if(null == mail){
					continue;
				}
				MailInfoItem item = new MailInfoItem();
				item.setMailId(mail.getMailId());
				item.setRoleId(mail.getRoleId());
				item.setGold(mail.getGold());
				item.setBindGold(mail.getBindGold());
				item.setSilverMoney(mail.getSilverMoney());
				item.setExp(mail.getExp());
				item.setContribute(mail.getContribute());
				item.setZp(mail.getZp());
				//item.setMagicSoul(mail.getMagicSoul());
				item.setLook(mail.getLook());
				item.setSendTime(mail.getSendTime());
				item.setContent(mail.getContent());
				item.setTitle(mail.getTitle());
				item.setSendRole(mail.getSendRole());
				item.setExistGoods(mail.getExistGoods());
				item.setSendSource(mail.getSendSource());
				item.setContentId(mail.getContentId());
				item.setPayGold(mail.getPayGold());
				item.setFreeze(mail.getFreeze());
				mailList.add(item);
			}
		} catch (RuntimeException e) {
			this.logger.error(this.getClass().getName() + ".getMailList error: ", e);
		}
		return mailList;
	}
	
	private List<BagItem> getWarehouse(RoleInstance role){
		Collection<RoleGoods> list = null;
		if(GameContext.getOnlineCenter().getRoleInstanceByRoleId(role.getRoleId()) != null &&
				null != role.getWarehousePack()){
			list = role.getWarehousePack().getAllGoods();
		}
		if(Util.isEmpty(list)){
			list = GameContext.getBaseDAO().selectList(RoleGoods.class,"roleId",role.getRoleId(), "storageType", StorageType.warehouse.getType() + "");
		}
		return getBagItems(list);
	}
	
	private List<BagItem> getBagItems(Collection<RoleGoods> list){
		List<BagItem> items = new ArrayList<BagItem>();
		if(Util.isEmpty(list)){
			return items;
		}
		Map<String,BagItem> map = new HashMap<String,BagItem>();
		for(RoleGoods roleGoods : list){
			int goodsId = roleGoods.getGoodsId();
			GoodsBase goodsBase = GameContext.getGoodsApp().getGoodsBase(goodsId);
			if(null == goodsBase){
				continue;
			}
			byte bind = roleGoods.getBind();
			String key = goodsId + Cat.underline + bind;
			BagItem item = map.get(key);
			if(null == item){
				item = new BagItem();
				item.setGoodsId(roleGoods.getId());
				item.setGoodsName(goodsBase.getName());
				item.setNum(roleGoods.getCurrOverlapCount());
				item.setBind(bind);
				map.put(key, item);
			}else{
				item.setNum(item.getNum() + roleGoods.getCurrOverlapCount());
			}
		}
		items.addAll(map.values());
		return items;
	}
	
	static{
		attributeList.add(AttributeType.exp);//当前经验/升级经验
		attributeList.add(AttributeType.curHP);//当前生命值 / 生命值上限
		attributeList.add(AttributeType.curMP);//当前法力值 / 法力值上限
		attributeList.add(AttributeType.goldMoney);//元宝
		attributeList.add(AttributeType.bindingGoldMoney);//银条
		attributeList.add(AttributeType.silverMoney);//银币
		attributeList.add(AttributeType.honor);//荣誉值
		attributeList.add(AttributeType.potential);//真气值
		attributeList.add(AttributeType.residueGoldMoney);//消费总元宝
		attributeList.add(AttributeType.residueBindingMoney);//消费总绑元
		attributeList.add(AttributeType.battleScore);//战斗力
		attributeList.add(AttributeType.hit);//命中值
		attributeList.add(AttributeType.dodge);//闪避值
		attributeList.add(AttributeType.critAtk);//暴击值
		attributeList.add(AttributeType.atk);//攻击力
		attributeList.add(AttributeType.rit);//防御力
		attributeList.add(AttributeType.critRit);//暴击抵抗
		attributeList.add(AttributeType.sacredAtk);//神圣伤害
		attributeList.add(AttributeType.critAtkProb);//暴击伤害倍率
		attributeList.add(AttributeType.expAddRate);//加经验系数
		attributeList.add(AttributeType.expMultRate);//乘经验系数
	}
}
