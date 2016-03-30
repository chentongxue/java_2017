package sacred.alliance.magic.app.active.dps;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sacred.alliance.magic.app.active.vo.Active;
import sacred.alliance.magic.base.ActiveStatus;
import sacred.alliance.magic.base.ActiveType;
import sacred.alliance.magic.base.OutputConsumeType;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.base.XlsSheetNameType;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.util.Log4jManager;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.util.Wildcard;
import sacred.alliance.magic.util.XlsPojoUtil;
import sacred.alliance.magic.vo.Point;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.message.push.C0003_TipNotifyMessage;
import com.game.draco.message.push.C2362_ActiveDpsRewardNotifyMessage;

public class ActiveDpsAppImpl implements ActiveDpsApp {
	
	/** KEY=活动ID,VALUE=奖励索引（KEY=角色等级,VALUE=奖励索引） */
	private Map<Short,Map<Integer,List<DpsIndex>>> dpsIndexMap = new HashMap<Short,Map<Integer,List<DpsIndex>>>();
	/** KEY=奖励ID,VALUE=奖励信息 */
	private Map<Integer,DpsReward> rewardMap = new HashMap<Integer,DpsReward>();
	/** KEY=地图ID,VALUE=地图配置信息 */
	private Map<String,DpsMapConfig> mapConfigMap = new HashMap<String,DpsMapConfig>();
	/** 伤害输出突破点配置 */
	private List<DpsHurtPoint> hurtPointList = new ArrayList<DpsHurtPoint>();
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private Map<Short,Active> activeMap = new HashMap<Short,Active>();//BOSSDPS活动
	
	
	@Override
	public void setArgs(Object arg0) {
		
	}

	@Override
	public void start() {
		this.initActive();
		this.loadWarlordsConfig();
	}

	@Override
	public void stop() {
		
	}
	
	/**
	 * 筛选群雄逐鹿活动
	 */
	private void initActive(){
		Collection<Active> list = GameContext.getActiveApp().getAllActive();
		if(Util.isEmpty(list)){
			return;
		}
		for(Active active : list){
			if(ActiveType.BossDps.getType() != active.getType()){
				continue ;
			}
			this.activeMap.put(active.getId(), active);
		}
	}
	
	/**
	 * 加载群雄逐鹿配置
	 */
	private void loadWarlordsConfig(){
		//如果没有DPS活动，则不需要加载相关的配置
		if(Util.isEmpty(this.activeMap)){
			return;
		}
		String fileName= "";
		String sheetName = "";
		try{
			String xlsPath = GameContext.getPathConfig().getXlsPath();
			//①加载奖励
			fileName = XlsSheetNameType.active_dps_reward_detail.getXlsName();
			sheetName = XlsSheetNameType.active_dps_reward_detail.getSheetName();
			List<DpsReward> rewardList = XlsPojoUtil.sheetToList(xlsPath + fileName, sheetName, DpsReward.class);
			for(DpsReward reward : rewardList){
				if(null == reward){
					continue;
				}
				Result result = reward.checkAndInit();
				if(!result.isSuccess()){
					Log4jManager.CHECK.error("load excel error:fileName=" + fileName + ",sheetName=" + sheetName + "." + result.getInfo());
					Log4jManager.checkFail();
					continue;
				}
				this.rewardMap.put(reward.getRewardId(), reward);
			}
			//②加载奖励索引配置
			fileName = XlsSheetNameType.active_dps_index.getXlsName();
			sheetName = XlsSheetNameType.active_dps_index.getSheetName();
			List<DpsIndex> indexList = XlsPojoUtil.sheetToList(xlsPath + fileName, sheetName, DpsIndex.class);
			String info = "load excel error:fileName=" + fileName + ",sheetName=" + sheetName + ".";
			for(DpsIndex config : indexList){
				if(null == config){
					continue;
				}
				short activeId = config.getActiveId();
				Active active = this.activeMap.get(activeId);
				if(null == active){
					Log4jManager.CHECK.error(info + "activeId=" + activeId + "");
					Log4jManager.checkFail();
					continue;
				}
				Result result = config.checkAndInit();
				if(!result.isSuccess()){
					Log4jManager.CHECK.error(info + result.getInfo());
					Log4jManager.checkFail();
					continue;
				}
				int rewardId = config.getRewardId();
				if(!this.rewardMap.containsKey(rewardId)){
					Log4jManager.CHECK.error(info + "rewardId=" + rewardId + ",it's not exist.");
					Log4jManager.checkFail();
					continue;
				}
				Map<Integer,List<DpsIndex>> map = this.dpsIndexMap.get(activeId);
				if(null == map){
					map = new HashMap<Integer,List<DpsIndex>>();
					this.dpsIndexMap.put(activeId, map);
				}
				int level = config.getRoleLevel();
				if(!map.containsKey(level)){
					map.put(level, new ArrayList<DpsIndex>());
				}
				map.get(level).add(config);
			}
			//验证DPS奖励等级是否和活动等级相匹配
			for(Active active : this.activeMap.values()){
				if(null == active){
					continue;
				}
				short activeId = active.getId();
				Map<Integer,List<DpsIndex>> indexMap = this.dpsIndexMap.get(activeId);
				if(Util.isEmpty(indexMap)){
					Log4jManager.CHECK.error("load active_dps.xls error: activeId=" + activeId + ",it's not config dps_index reward.");
					Log4jManager.checkFail();
					continue;
				}
				int minLevel = active.getMinLevel();
				int maxLevel = active.getMaxLevel();
				for(int level =  minLevel; level <= maxLevel; level++){
					if(!indexMap.containsKey(level)){
						Log4jManager.CHECK.error("load active_dps.xls error: activeId=" + activeId + ",roleLevel=" + level + ",it's not config rewardId.");
						Log4jManager.checkFail();
					}
				}
			}
			//③加载地图配置
			fileName = XlsSheetNameType.active_dps_map_config.getXlsName();
			sheetName = XlsSheetNameType.active_dps_map_config.getSheetName();
			List<DpsMapConfig> mapList = XlsPojoUtil.sheetToList(xlsPath + fileName, sheetName, DpsMapConfig.class);
			for(DpsMapConfig config : mapList){
				if(null == config){
					continue;
				}
				Result result = config.checkAndInit();
				if(!result.isSuccess()){
					Log4jManager.CHECK.error("load excel error:fileName=" + fileName + ",sheetName=" + sheetName + "." + result.getInfo());
					Log4jManager.checkFail();
					continue;
				}
				this.mapConfigMap.put(config.getMapId(), config);
			}
			//加载消耗配置
			fileName = XlsSheetNameType.active_dps_hurt_Point.getXlsName();
			sheetName = XlsSheetNameType.active_dps_hurt_Point.getSheetName();
			this.hurtPointList = XlsPojoUtil.sheetToList(xlsPath + fileName, sheetName, DpsHurtPoint.class);
			for(DpsHurtPoint point : this.hurtPointList){
				if(null == point){
					continue;
				}
				Result result = point.checkAndInit();
				if(!result.isSuccess()){
					Log4jManager.CHECK.error("load excel error:fileName=" + fileName + ",sheetName=" + sheetName + "." + result.getInfo());
					Log4jManager.checkFail();
					continue;
				}
			}
			this.sortHurtPointList();
		}catch(Exception e){
			Log4jManager.CHECK.error("load excel error:fileName=" + fileName + ",sheetName=" + sheetName + ".", e);
			Log4jManager.checkFail();
		}
	}
	
	/**
	 * 伤害输出排序
	 */
	private void sortHurtPointList(){
		Collections.sort(this.hurtPointList, new Comparator<DpsHurtPoint>(){
			@Override
			public int compare(DpsHurtPoint ht1, DpsHurtPoint ht2) {
				if(ht1.getHurtPoint() < ht2.getHurtPoint()){
					return -1;
				}
				if(ht1.getHurtPoint() > ht2.getHurtPoint()){
					return 1;
				}
				return 0;
			}
		});
	}
	
	@Override
	public void checkReset(RoleInstance role, Active active) {
		
	}
	
	private String getText(String textId){
		return GameContext.getI18n().getText(textId);
	}

	@Override
	public Message getActiveDetail(RoleInstance role, Active active) {
		//不符合活动条件，提示活动未开启
		if(!active.isSuitLevel(role) || !active.isTimeOpen()){
			role.getBehavior().sendMessage(new C0003_TipNotifyMessage(this.getText(TextId.Active_Not_Open)));
			return null ;
		}
		Point point = GameContext.getActiveDpsApp().getEnterMapPoint(active.getId());
		if(null == point){
			role.getBehavior().sendMessage(new C0003_TipNotifyMessage(this.getText(TextId.Active_Enter_Map_Point_Null)));
			return null ;
		}
		//进入DPS地图
		try {
			GameContext.getUserMapApp().changeMap(role, point);
		} catch (Exception e) {
			logger.error("",e);
		}
		return null ;
	}

	@Override
	public ActiveStatus getActiveStatus(RoleInstance role, Active active) {
		if(!active.isTimeOpen() || !active.isSuitLevel(role)){
			return ActiveStatus.NotOpen;
		}
		return ActiveStatus.CanAccept;
	}

	@Override
	public boolean isOutDate(Active active) {
		return active.isOutDate();
	}
	
	private DpsReward getDpsReward(int rewardId){
		return this.rewardMap.get(rewardId);
	}
	
	@Override
	public DpsMapConfig getDpsMapConfig(String mapId) {
		return this.mapConfigMap.get(mapId);
	}
	
	@Override
	public List<DpsHurtPoint> getHurtPointList() {
		return this.hurtPointList;
	}
	
	@Override
	public Active getActiveByMapId(String mapId) {
		return this.activeMap.get(this.getActiveId(mapId));
	}
	
	/**
	 * 根据地图ID获取相应的活动ID
	 * @param mapId
	 * @return
	 */
	private short getActiveId(String mapId){
		DpsMapConfig mapConfig = this.getDpsMapConfig(mapId);
		if(null == mapConfig){
			return 0;
		}
		return mapConfig.getActiveId();
	}

	/**
	 * 获取DPS奖励索引
	 * @param activeId 活动ID
	 * @param index 名次
	 * @param level 角色等级
	 * @return
	 */
	private DpsIndex getDpsIndex(short activeId, int index, int level){
		Map<Integer,List<DpsIndex>> map = this.dpsIndexMap.get(activeId);
		if(Util.isEmpty(map)){
			return null;
		}
		List<DpsIndex> list = map.get(level);
		if(Util.isEmpty(list)){
			return null;
		}
		for(DpsIndex dpsIndex : list){
			if(null == dpsIndex){
				continue;
			}
			if(dpsIndex.isWithin(index)){
				return dpsIndex;
			}
		}
		return null;
	}
	
	@Override
	public void sendReward(short activeId, RoleInstance role, int index, long hurt,int npcLevel) {
		try {
			if(!this.dpsIndexMap.containsKey(activeId)){
				return;
			}
			DpsIndex dpsIndex = this.getDpsIndex(activeId, index, npcLevel);
			if(null == dpsIndex){
				return;
			}
			DpsReward reward = this.getDpsReward(dpsIndex.getRewardId());
			if(null == reward){
				return;
			}
			//邮件发奖
			this.sendRewardMail(role, reward, index, hurt);
			//活动结束，显示奖励面板
			C2362_ActiveDpsRewardNotifyMessage message = new C2362_ActiveDpsRewardNotifyMessage();
			message.setIndex((short) index);
			message.setDepValue((int) hurt);
			message.setAttrList(reward.getShowAttrList());
			message.setGoodsList(reward.getShowGoodsList());
			role.getBehavior().sendMessage(message);
		} catch (Exception e) {
			this.logger.error(this.getClass().getName() + ".sendReward error: ", e);
		}
	}
	
	/**
	 * 通过邮件发奖励
	 * @param role
	 * @param reward 奖励配置
	 * @param index 名次
	 * @param hurt 伤害值
	 */
	private void sendRewardMail(RoleInstance role, DpsReward reward, int index, long hurt){
		try {
			if(null == role || null == reward){
				return;
			}
			String content = reward.getContent();
			if(!Util.isEmpty(content)){
				content = content.replace(Wildcard.Index, String.valueOf(index))
				.replace(Wildcard.Hurt, String.valueOf(hurt));
			}
			//异步发邮件
			GameContext.getMailApp().sendMailAsync(role.getRoleId(), reward.getTitle(), content, 
					reward.getSenderName(), OutputConsumeType.active_dps_award_mail.getType(), 
					0, reward.getSilverMoney(), reward.getExp(), reward.getRewardGoodsList());
		} catch (Exception e) {
			this.logger.error(this.getClass().getName() + ".sendRewardMail", e);
		}
	}

	@Override
	public Point getEnterMapPoint(short activeId) {
		for(DpsMapConfig config : this.mapConfigMap.values()){
			if(null == config){
				continue;
			}
			if(activeId == config.getActiveId()){
				return config.getMapPoint();
			}
		}
		return null;
	}

	@Override
	public boolean getActiveHint(RoleInstance role, Active active) {
		return false;
	}
	
}
