package sacred.alliance.magic.app.active.siege;

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
import sacred.alliance.magic.app.goods.GoodsOperateBean;
import sacred.alliance.magic.app.map.data.MapConfig;
import sacred.alliance.magic.base.ActiveStatus;
import sacred.alliance.magic.base.ActiveType;
import sacred.alliance.magic.base.MapLogicType;
import sacred.alliance.magic.base.OutputConsumeType;
import sacred.alliance.magic.base.XlsSheetNameType;
import sacred.alliance.magic.component.id.IdFactory;
import sacred.alliance.magic.component.id.IdType;
import sacred.alliance.magic.constant.Status;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.util.Log4jManager;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.util.XlsPojoUtil;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.mail.domain.Mail;
import com.game.draco.app.mail.type.MailSendRoleType;

public class ActiveSiegeAppImpl implements ActiveSiegeApp {
	//避免多次领取奖励加一个标识buff
	private final short flagBuffId = 9106 ;
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private List<SiegeFailAwardRate> awardRateList = new ArrayList<SiegeFailAwardRate>();
	private Map<String,List<SiegeWinAward>> winAwardMap = new HashMap<String,List<SiegeWinAward>>();
	private Map<String,List<SiegeFailAward>> failAwardMap = new HashMap<String,List<SiegeFailAward>>();
	private Map<String,SiegeMapConfig> siegeMapConfigMap = new HashMap<String,SiegeMapConfig>();
	
	private Map<Short,Active> activeMap = new HashMap<Short,Active>();
	

	private void initActive(){
		Collection<Active> list = GameContext.getActiveApp().getAllActive();
		if(Util.isEmpty(list)){
			return;
		}
		for(Active active : list){
			if(active.getType() == ActiveType.Siege.getType()){
				activeMap.put(active.getId(), active);
				continue ;
			}
		}
	}
	
	@Override
	public void winAward(Active active, Collection<RoleInstance> roleList){
		if(roleList.size() == 0){
			return ;
		}
		for(RoleInstance role : roleList){
			this.sendWinMail(role);
		}
	}
	
	@Override
	public void failAward(Active active, Collection<RoleInstance> roleList,int min,int max){
		if(roleList.size() == 0){
			return ;
		}
		SiegeFailAwardRate sfar = this.getSiegeFailAwardRate(min, max);
		if(null == sfar){
			return ;
		}
		for(RoleInstance role : roleList){
			this.sendFailMail(role,sfar);
		}
		
	}
	
	private SiegeWinAward getSiegeWinAward(String mapId,int level){
		List<SiegeWinAward> winAwardList = winAwardMap.get(mapId);
		if(null == winAwardList || 0 == winAwardList.size()){
			return null;
		}
		for(SiegeWinAward swa : winAwardList){
			if(swa.isLevel(level)){
				return swa;
			}
		}
		return null;
		
	}
	
	private SiegeFailAwardRate getSiegeFailAwardRate(int min,int max){
		if(null == awardRateList || 0 == awardRateList.size()){
			return null;
		}
		for(SiegeFailAwardRate swar : awardRateList){
			if(swar.isBoosHpPercent(min, max)){
				return swar;
			}
		}
		return null;
	}
	
	private SiegeFailAward getSiegeFailAward(String mapId , int level){
		List<SiegeFailAward> failAwardList = failAwardMap.get(mapId);
		if(null == failAwardList || 0 == failAwardList.size()){
			return null;
		}
		for(SiegeFailAward sfa : failAwardList){
			if(sfa.isLevel(level)){
				return sfa;
			}
		}
		return null;
		
	}
	
	private void sendFailMail(RoleInstance role,SiegeFailAwardRate sfar){
		try{
			if(null == sfar){
				return  ;
			}
			if(role.hasBuff(flagBuffId)){
				return ;
			}
			SiegeFailAward failAward = this.getSiegeFailAward(role.getMapId(),role.getLevel());
			if(null == failAward){
				return ;
			}
			boolean change = false;
			Mail mail = new Mail(IdFactory.getInstance().nextId(IdType.MAIL));
			int exp = failAward.getExp(sfar.getExpAddRate());
			int silverMoney = failAward.getSilverMoney(sfar.getMoneyAddRate());
			int bindGold = failAward.getBindGold(sfar.getBindAddRate());
			if(exp > 0){
				change = true;
				mail.setExp(exp);
			}
			if(silverMoney > 0){
				change = true;
				mail.setSilverMoney(silverMoney);
			}
			if(bindGold > 0){
				change = true;
				mail.setBindGold(bindGold);
			}
			List<GoodsOperateBean> list = failAward.getAddGoodsList();
			if(null != list && 0 != list.size()){
				change = true;
			}
			if(!change){
				return ;
			}
			mail.setRoleId(role.getRoleId());
			mail.setTitle(GameContext.getI18n().getText(TextId.ACTIVE_SIEGE_MAIL_TITLE));
			mail.setSendRole(MailSendRoleType.Siege.getName());
			mail.setSendSource(OutputConsumeType.siege_output.getType());
			for(GoodsOperateBean agb:list){
				mail.addMailAccessory(agb.getGoodsId(), agb.getGoodsNum(), agb.getBindType());
			}
			GameContext.getMailApp().sendMail(mail);
			//加标识buff
			GameContext.getUserBuffApp().addBuffStat(role, role, flagBuffId, 1);
		}catch(Exception e){
			logger.error("",e);
		}
	}
	
	private void sendWinMail(RoleInstance role){
		try{
			if(role.hasBuff(flagBuffId)){
				return ;
			}
			SiegeWinAward winAward = this.getSiegeWinAward(role.getMapId(),role.getLevel());
			if(null == winAward){
				return ;
			}
			boolean change = false;
			Mail mail = new Mail(IdFactory.getInstance().nextId(IdType.MAIL));
			if(winAward.getExp() > 0){
				change = true;
				mail.setExp(winAward.getExp());
			}
			if(winAward.getSilverMoney() > 0){
				change = true;
				mail.setSilverMoney(winAward.getSilverMoney());
			}
			if(winAward.getBindGold() > 0){
				change = true;
				mail.setBindGold(winAward.getBindGold());
			}
			List<GoodsOperateBean> list = winAward.getAddGoodsList();
			if(null != list && 0 != list.size()){
				change = true;
			}
			if(!change){
				return ;
			}
			mail.setRoleId(role.getRoleId());
			mail.setTitle(Status.Active_Mail_Title.getTips());
			mail.setSendRole(MailSendRoleType.Siege.getName());
			mail.setSendSource(OutputConsumeType.siege_output.getType());
			for(GoodsOperateBean agb:list){
				mail.addMailAccessory(agb.getGoodsId(), agb.getGoodsNum(), agb.getBindType());
			}
			GameContext.getMailApp().sendMail(mail);
			//加标识buff
			GameContext.getUserBuffApp().addBuffStat(role, role, flagBuffId, 1);
		}catch(Exception e){
			logger.error("",e);
		}
	}
	
	@Override
	public void setArgs(Object arg0) {

	}

	@Override
	public void start() {
		this.loadAllSiegeConfig();
	}

	@Override
	public void stop() {

	}
	
	private void loadAllSiegeConfig(){
		this.initActive();
		//如果没配置活动，就不需要加载相关的配置表了
		if(Util.isEmpty(this.activeMap)){
			return;
		}
		this.loadFailAwardRate();
		this.loadFailAward();
		this.loadWinAward();
		this.loadMapActive();
	}
	
	private void loadWinAward() {
		String path = GameContext.getPathConfig().getXlsPath();
		String fileName = XlsSheetNameType.siege_win_award.getXlsName();
		String sheetName = XlsSheetNameType.siege_win_award.getSheetName();
		String sourceFile = path + fileName;
		List<SiegeWinAward> winAwardList = XlsPojoUtil.sheetToList(sourceFile, sheetName,SiegeWinAward.class);
		if (winAwardList == null || winAwardList.size() <= 0) {
			Log4jManager.CHECK.error("load SiegeWinAward is null");
			Log4jManager.checkFail();
			return;
		}
		for(SiegeWinAward swa : winAwardList){
			swa.init();
			if(!winAwardMap.containsKey(swa.getMapId())){
				winAwardMap.put(swa.getMapId(), new ArrayList<SiegeWinAward>());
			}
			winAwardMap.get(swa.getMapId()).add(swa);
		}
	}
	
	private void loadFailAward() {
		String path = GameContext.getPathConfig().getXlsPath();
		String fileName = XlsSheetNameType.siege_fail_award.getXlsName();
		String sheetName = XlsSheetNameType.siege_fail_award.getSheetName();
		String sourceFile = path + fileName;
		List<SiegeFailAward> failAwardList = XlsPojoUtil.sheetToList(sourceFile, sheetName,SiegeFailAward.class);
		if (failAwardList == null || failAwardList.size() <= 0) {
			Log4jManager.CHECK.error("load SiegeFailAward is null");
			Log4jManager.checkFail();
			return;
		}
		for(SiegeFailAward sfa : failAwardList){
			sfa.init();
			if(!failAwardMap.containsKey(sfa.getMapId())){
				failAwardMap.put(sfa.getMapId(), new ArrayList<SiegeFailAward>());
			}
			failAwardMap.get(sfa.getMapId()).add(sfa);
		}
	}
	
	private void loadFailAwardRate() {
		String path = GameContext.getPathConfig().getXlsPath();
		String fileName = XlsSheetNameType.siege_award_add_rate.getXlsName();
		String sheetName = XlsSheetNameType.siege_award_add_rate.getSheetName();
		String sourceFile = path + fileName;
		awardRateList = XlsPojoUtil.sheetToList(sourceFile, sheetName,SiegeFailAwardRate.class);
		if (awardRateList == null || awardRateList.size() <= 0) {
			Log4jManager.CHECK.error("load SiegeFailAwardRate is null");
			Log4jManager.checkFail();
			return;
		}
		this.initAwardRateList(awardRateList);
	}
	
	/**
	 * 初始化后添加到awardRateList中
	 */
	private void initAwardRateList(List<SiegeFailAwardRate> awardRateList){
		// 升序排序(>升序排序<降序排序)
		Collections.sort(awardRateList, new Comparator<SiegeFailAwardRate>() {
			public int compare(SiegeFailAwardRate sfar, SiegeFailAwardRate sfar2) {
				if (sfar.getBossHp() > sfar2.getBossHp()) {
					return 1;
				} else {
					return -1;
				}
			}
		});
		int start_hp = 1;//开始值
		int before_hp = 0;//前
		int before_start_hp = 0;//前一开始值
		for(int i=0;i<awardRateList.size();i++){
			if(before_hp == awardRateList.get(i).getBossHp()){
				awardRateList.get(i).init(before_start_hp);
				continue ;
			}
			awardRateList.get(i).init(start_hp);
			before_start_hp = start_hp;
			before_hp = awardRateList.get(i).getBossHp();
			start_hp = awardRateList.get(i).getBossHp()+1;
		}
	}
	
	private void loadMapActive() {
		String path = GameContext.getPathConfig().getXlsPath();
		String fileName = XlsSheetNameType.siege_mapid_active.getXlsName();
		String sheetName = XlsSheetNameType.siege_mapid_active.getSheetName();
		String sourceFile = path + fileName;
		List<SiegeMapConfig> amList = XlsPojoUtil.sheetToList(sourceFile, sheetName,SiegeMapConfig.class);
		if (amList == null || amList.size() <= 0) {
			Log4jManager.CHECK.error("load ActiveMap is null");
			Log4jManager.checkFail();
			return;
		}
		for(SiegeMapConfig am : amList){
			String mapId = am.getMapId();
			siegeMapConfigMap.put(am.getMapId(), am);
			MapConfig cm = GameContext.getMapApp().getMapConfig(mapId);
			if(null == cm){
				Log4jManager.CHECK.error("mapId:" + mapId + " is null");
				Log4jManager.checkFail();
				continue ;
			}
			cm.setLogictype((byte) MapLogicType.siege.getType());
		}
		amList.clear();
		amList = null;
	}
	
	

	
	@Override
	public void checkReset(RoleInstance role, Active active) {
		
	}

	@Override
	public Message getActiveDetail(RoleInstance role, Active active) {
		return active.getDefaultPanelDetailMessage(role);
	}

	@Override
	public ActiveStatus getActiveStatus(RoleInstance role, Active active) {
		if(!active.isTimeOpen() || !active.isSuitLevel(role)){
			return ActiveStatus.NotOpen;
		}
		return ActiveStatus.CanAccept;
	}

	public List<SiegeFailAwardRate> getAwardRateList() {
		return awardRateList;
	}

	public void setAwardRateList(List<SiegeFailAwardRate> awardRateList) {
		this.awardRateList = awardRateList;
	}

	public Map<String, List<SiegeWinAward>> getWinAwardMap() {
		return winAwardMap;
	}

	public void setWinAwardMap(Map<String, List<SiegeWinAward>> winAwardMap) {
		this.winAwardMap = winAwardMap;
	}

	public Map<String, List<SiegeFailAward>> getFailAwardMap() {
		return failAwardMap;
	}

	public void setFailAwardMap(Map<String, List<SiegeFailAward>> failAwardMap) {
		this.failAwardMap = failAwardMap;
	}

	public Logger getLogger() {
		return logger;
	}

	@Override
	public Active getActive(short activeId) {
		return activeMap.get(activeId);
	}
	
	@Override
	public SiegeMapConfig getSiegeMapConfig(String mapId) {
		if(Util.isEmpty(siegeMapConfigMap) || Util.isEmpty(mapId)){
			return null ;
		}
		return siegeMapConfigMap.get(mapId) ;
	}

	public Map<Short, Active> getActiveMap() {
		return activeMap;
	}

	public void setActiveMap(Map<Short, Active> activeMap) {
		this.activeMap = activeMap;
	}


	@Override
	public boolean isOutDate(Active active) {
		return active.isOutDate();
	}

	@Override
	public boolean getActiveHint(RoleInstance role, Active active) {
		return false;
	}

}
