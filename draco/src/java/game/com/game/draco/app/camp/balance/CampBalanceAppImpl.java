package com.game.draco.app.camp.balance;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sacred.alliance.magic.app.goods.GoodsOperateBean;
import sacred.alliance.magic.app.goods.behavior.result.GoodsResult;
import sacred.alliance.magic.base.AnnouncementType;
import sacred.alliance.magic.base.BindingType;
import sacred.alliance.magic.base.BroadcastState;
import sacred.alliance.magic.base.CampChangeOpenType;
import sacred.alliance.magic.base.OutputConsumeType;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.base.XlsSheetNameType;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.domain.GoodsBase;
import sacred.alliance.magic.domain.SysAnnouncement;
import sacred.alliance.magic.util.DateUtil;
import sacred.alliance.magic.util.Log4jManager;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.util.XlsPojoUtil;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.camp.balance.config.CampChangeConfig;
import com.game.draco.app.camp.balance.config.CampRecommendConfig;
import com.game.draco.app.camp.balance.domain.CampBoom;
import com.game.draco.app.camp.balance.domain.CampLevel;
import com.game.draco.app.npc.domain.NpcInstance;
import com.game.draco.base.CampType;
import com.game.draco.message.item.CampBalanceBoomItem;
import com.game.draco.message.item.CampBalanceOpenBoomItem;
import com.game.draco.message.item.GoodsLiteNamedItem;
import com.game.draco.message.item.NpcFunctionItem;
import com.game.draco.message.push.C1533_CampBalanceCampNotifyMessage;
import com.game.draco.message.push.C1534_CampBalanceToSelectNotifyMessage;
import com.game.draco.message.response.C1530_CampBalanceRespMessage;
import com.game.draco.message.response.C1531_CampBalanceOpenRespMessage;
import com.google.common.collect.Lists;

public class CampBalanceAppImpl implements CampBalanceApp {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private Map<Byte, CampBoom> campBoomMap = new LinkedHashMap<Byte, CampBoom>();
	private static final int DEFAULT_BOOM = 500 ;
	private Map<Byte,AtomicInteger> campLevelMap = new ConcurrentHashMap<Byte,AtomicInteger>();
	private CampChangeConfig campChangeConfig;
	private CampRecommendConfig campRecommendConfig;
	private boolean changeCampOpen = false;
	private byte maxBoomCamp = -1;
	
	
	@Override
	public void start() {
		initCampBoom();
		initCampLevel();
		initCampRecommendConfig();
		initCampChangeConfig();
		initCampChange();
	}
	
	//加载阵营记录
	private void initCampBoom() {
		try{
			List<CampBoom> selectList = GameContext.getBaseDAO().selectAll(CampBoom.class);
			if(!Util.isEmpty(selectList)) {
				for(CampBoom cb : selectList){
					if(null == cb){
						continue ;
					}
					this.campBoomMap.put(cb.getCampId(), cb);
				}
			}
			for(CampType ct : CampType.values()){
				if(!ct.isRealCamp()){
					continue;
				}
				if(this.campBoomMap.containsKey(ct.getType())){
					continue ;
				}
				byte campId = ct.getType();
				CampBoom campBoom = new CampBoom();
				campBoom.setCampId(campId);
				//插入数据库
				GameContext.getBaseDAO().insert(campBoom);
				campBoomMap.put(campId, campBoom);
			}
		}catch(Exception e){
			logger.error("CampBalanceApp.initCamp error: ", e);
			Log4jManager.checkFail();
			Log4jManager.CHECK.error("CampBalanceApp.initCamp error: ", e);
		}
	}
	
	private void initCampLevel(){
		try{
			int minLevel = 0 ;
			if(null != this.campRecommendConfig){
				minLevel = this.campRecommendConfig.getEffectRoleLevel() ;
			}
			List<CampLevel> list = GameContext.getRoleDAO().getCampLevelList(minLevel);
			if(!Util.isEmpty(list)){
				for(CampLevel cLevel : list) {
					if(null == cLevel){
						continue;
					}
					campLevelMap.put(cLevel.getCampId(), new AtomicInteger(cLevel.getNum()));
				}
			}
			
			for(CampType camp : CampType.values()){
				if(!camp.isRealCamp()){
					continue;
				}
				byte campId = camp.getType();
				if(!campLevelMap.containsKey(campId)){
					campLevelMap.put(campId, new AtomicInteger(0));
				}
			}
		}catch(Exception e){
			logger.error("CampBalanceApp.initCampLevel error",e);
		}
	}
	
	private void initCampRecommendConfig(){
		String fileName = XlsSheetNameType.camp_balance_recommend.getXlsName();
		String sheetName = XlsSheetNameType.camp_balance_recommend.getSheetName();
		try{
			String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
			List<CampRecommendConfig> list = XlsPojoUtil.sheetToList(sourceFile, sheetName, CampRecommendConfig.class);
			if(Util.isEmpty(list)){
				Log4jManager.checkFail();
				Log4jManager.CHECK.error("not any config: sourceFile = "+fileName +" sheetName ="+sheetName);
				return;
			}
			this.campRecommendConfig = list.get(0);
			if(null == this.campRecommendConfig) {
				Log4jManager.checkFail();
				Log4jManager.CHECK.error("loadExel CampRecommendConfig error: not config CampRecommendConfig");
				return;
			}
			campRecommendConfig.init();
		}catch (Exception e) {
			Log4jManager.checkFail();
			Log4jManager.CHECK.error("loadExel error: sourceFile = " + fileName + "sheetName = " + sheetName, e);
		}
	}
	
	private void initCampChangeConfig(){
		String fileName = XlsSheetNameType.camp_balance_change.getXlsName();
		String sheetName = XlsSheetNameType.camp_balance_change.getSheetName();
		try{
			String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
			List<CampChangeConfig> list = XlsPojoUtil.sheetToList(sourceFile, sheetName, CampChangeConfig.class);
			if(Util.isEmpty(list)){
				Log4jManager.checkFail();
				Log4jManager.CHECK.error("not any config: sourceFile = "+fileName +" sheetName ="+sheetName);
				return;
			}
			this.campChangeConfig = list.get(0);
			if(null == this.campChangeConfig) {
				Log4jManager.checkFail();
				Log4jManager.CHECK.error("loadExel CampChangeConfig error: not config CampChangeConfig");
				return;
			}
			campChangeConfig.init();
		}catch (Exception e) {
			Log4jManager.checkFail();
			Log4jManager.CHECK.error("loadExel error: sourceFile = " + fileName + "sheetName = " + sheetName, e);
		}
	}
	
	private void initCampChange(){
		try{
			int maxBoom = 0;
			int minBoom = Integer.MAX_VALUE;
			byte maxCampId = -1;
			for(CampBoom campBoom : this.campBoomMap.values()){
				if(null == campBoom){
					continue;
				}
				byte campId = campBoom.getCampId();
				int boom = campBoom.getBoom();
				if(boom > maxBoom) {
					maxBoom = boom;
					maxCampId = campId;
				}
				if(boom < minBoom) {
					minBoom = boom;
				}
			}
			
			maxBoom+=DEFAULT_BOOM;
			minBoom+=DEFAULT_BOOM;
			this.maxBoomCamp = maxCampId;
			if((int)((double)maxBoom/minBoom*10000) >= campChangeConfig.getOpenChangeRate()) {
				this.changeCampOpen = true;
				broadcastChangeCampOpen();
			}else{
				this.changeCampOpen = false;
			}
		}catch(Exception e){
			logger.error("initCampChange error",e);
		}
	}
	
	private void broadcastChangeCampOpen(){
		/*try{
			Date beginDate = new Date();
			Date endDate = DateUtil.addHours(beginDate, this.campChangeConfig.getBroadcastHour());
			String content = this.campChangeConfig.getBroadcast();
			int gapTime = this.campChangeConfig.getTimeGap();
			int state = BroadcastState.open.getType();
			SysAnnouncement annou = new SysAnnouncement(content,beginDate,endDate,gapTime,state,AnnouncementType.SYS.getType());
			GameContext.getAnnounceApp().insertAnnounce(annou);
		}catch(Exception e){
			logger.error("CampBalanceApp.broadcastChangeCampOpen error",e);
		}*/
	}
	
	@Override
	public boolean pushToSelectCampMessage(RoleInstance role) {
		int level = role.getLevel();
		if (level >= this.campRecommendConfig.getSelectCampRoleLevel()
				&& -1 == role.getCampId()) {
			// push 选阵营面板
			C1534_CampBalanceToSelectNotifyMessage notifyMsg = new C1534_CampBalanceToSelectNotifyMessage();
			byte campId = this.getRecommendCamp();
			notifyMsg.setRecommendCampId(campId);
			notifyMsg.setTips(this.campRecommendConfig.getTips());
			if( -1 != campId){
				GoodsBase gb = GameContext.getGoodsApp().getGoodsBase(this.campRecommendConfig.getGoodsId());
				if(null != gb){
					GoodsLiteNamedItem goodsItem = gb.getGoodsLiteNamedItem();
					goodsItem.setBindType((byte)this.campRecommendConfig.getBindType());
					notifyMsg.setGoodsItem(goodsItem);
				}
			}
			// 设置推荐的阵营ID
			role.setRecommendCampId(campId);
			GameContext.getMessageCenter().sendByRoleId(null, role.getRoleId(),
					notifyMsg);
			return true ;
		}
		return false ;
	}
	
	@Override
	public void roleLevelUp(RoleInstance role){
		if(null == role){
			return ;
		}
		try{
			int level = role.getLevel() ;
			if(level == this.campRecommendConfig.getEffectRoleLevel()){
				this.campLevelMap.get(role.getCampId()).addAndGet(level);
			}else{
				this.campLevelMap.get(role.getCampId()).incrementAndGet();
			}
			//push选阵营面板
			this.pushToSelectCampMessage(role);
		}catch(Exception e){
			logger.error("CampBalanceApp.roleLevel error",e);
		}
	}
	
	@Override
	public byte getRecommendCamp(){
		byte minLevelCamp = -1;
		if(null == this.campRecommendConfig){
			return minLevelCamp;
		}
		try {
			if(!this.campRecommendConfig.isOpen()){
				return minLevelCamp;
			}
			int maxLevel = 0;
			int minLevel = Integer.MAX_VALUE;
			byte minCampId = -1;
			int totalLevel = 0 ;
			for(byte campId : this.campLevelMap.keySet()){
				AtomicInteger atomicLevel = this.campLevelMap.get(campId);
				int level = atomicLevel.get();
				totalLevel += level ;
				if(level > maxLevel) {
					maxLevel = level;
				}
				if(level < minLevel) {
					minLevel = level;
					minCampId = campId;
				}
			}
			if(totalLevel > 0 && totalLevel < this.campRecommendConfig.getOpenTotalLevel()){
				//没有达到开启条件
				return minLevelCamp ;
			}
			if(minLevel <=0){
				minLevel = 1 ;
			}
			if((int)((double)maxLevel/minLevel*10000) >= campRecommendConfig.getOpenLevelRate()) {
				minLevelCamp = minCampId;
			}
		} catch (Exception e) {
			logger.error("CampBalanceApp.getRecommendCamp error",e);
		}
		return minLevelCamp;
	}
	
	
	@Override
	public void setArgs(Object arg0) {
		
	}

	@Override
	public void stop() {
		
	}
	
	private String getText(String textId){
		return GameContext.getI18n().getText(textId);
	}

	@Override
	public List<NpcFunctionItem> getNpcFunction(RoleInstance role, NpcInstance npc) {
		/*List<NpcFunctionItem> functionList = new ArrayList<NpcFunctionItem>();
		if(null == this.campChangeConfig){
			return functionList;
		}
		String npcId = this.campChangeConfig.getNpcId();
		if(Util.isEmpty(npcId) || !npcId.equals(npc.getNpcid())){
			return functionList;
		}
		NpcFunctionItem item = new NpcFunctionItem();
		item.setCommandId(CAMP_BALANCE_CMDID);
		item.setTitle(this.getText(TextId.CAMP_BALANCE_NPC_TITLE));
		functionList.add(item);
		return functionList;*/
		return null ;
	}

	@Override
	public boolean isChangeOpen(){
		//return this.campChangeConfig.isOpen() && this.changeCampOpen;
		return false ;
	}
	
	@Override
	public C1530_CampBalanceRespMessage getCampBalanceMessage(RoleInstance role){
		try {
			C1530_CampBalanceRespMessage resp = new C1530_CampBalanceRespMessage();
			resp.setDesc(this.campChangeConfig.getDesc());
			
			List<CampBalanceBoomItem> items = new ArrayList<CampBalanceBoomItem>();
			for(CampBoom campBoom : campBoomMap.values()){
				CampBalanceBoomItem item = new CampBalanceBoomItem();
				item.setCampId(campBoom.getCampId());
				item.setBoom(campBoom.getBoom() + DEFAULT_BOOM);
				items.add(item);
			}
			resp.setItems(items);
			return resp;
		} catch (Exception e) {
			logger.error("CampBalanceApp.getCampBalanceRespMessage error",e);
		}
		return null;
	}
	
	@Override
	public C1531_CampBalanceOpenRespMessage getCampBalanceOpenMessage(RoleInstance role){
		try {
			C1531_CampBalanceOpenRespMessage resp = new C1531_CampBalanceOpenRespMessage();
			resp.setDesc(this.campChangeConfig.getOpenDesc());
			byte roleCamp = role.getCampId();
			List<CampBalanceOpenBoomItem> items = new ArrayList<CampBalanceOpenBoomItem>();
			for(CampBoom campBoom : campBoomMap.values()){
				byte boomCamp = campBoom.getCampId();
				CampBalanceOpenBoomItem item = new CampBalanceOpenBoomItem();
				item.setCampId(campBoom.getCampId());
				item.setBoom(campBoom.getBoom() + DEFAULT_BOOM);
				if(boomCamp == maxBoomCamp){
					item.setType(CampChangeOpenType.MAX.getType());
					items.add(item);
					continue;
				}
				if(boomCamp == roleCamp){
					item.setType(CampChangeOpenType.SELF.getType());
					items.add(item);
					continue;
				}
				item.setType(CampChangeOpenType.CAN_USE.getType());
				items.add(item);
			}
			resp.setItems(items);
			
			GoodsBase gb = GameContext.getGoodsApp().getGoodsBase(this.campChangeConfig.getGoodsId());
			if(null != gb) {
				GoodsLiteNamedItem goodsItem = gb.getGoodsLiteNamedItem() ;
				goodsItem.setNum((short)this.campChangeConfig.getNum());
				goodsItem.setBindType(BindingType.already_binding.getType());
				resp.setGoodsItem(goodsItem);
			}
			return resp;
		} catch (Exception e) {
			logger.error("CampBalanceApp.getCampBalanceRespMessage error",e);
		}
		return null;
	}
	
	@Override
	public Result selectCamp(RoleInstance role, byte campId){
		Result result = new Result();
		byte roleCampId = role.getCampId() ;
		if(-1 != roleCampId){
			result.setInfo(this.getText(TextId.CAMP_BALANCE_SELECT_CAMP_NOT_NEED));
			return result ;
		}
		CampType camp = CampType.get(campId);
		if(null == camp || !camp.isRealCamp()){
			result.setInfo(this.getText(TextId.ERROR_INPUT));
			return result ;
		}
		byte recommend = role.getRecommendCampId() ;
		if(campId == recommend && -1 != recommend){
			//给用户发送推荐奖励
			GoodsResult gr = GameContext.getUserGoodsApp().addGoodsForBag(role,
					this.campRecommendConfig.getGoodsId(), 1,
					BindingType.get(this.campRecommendConfig.getBindType()), 
					OutputConsumeType.camp_balance_select_camp) ;
			if(!gr.isSuccess()){
				//发邮件
				 List<GoodsOperateBean> goodsList = Lists.newArrayList();
				 goodsList.add(new GoodsOperateBean(this.campRecommendConfig.getGoodsId(),
						 1,this.campRecommendConfig.getBindType()));
				GameContext.getMailApp().sendMail(role.getRoleId(), 
						this.getText(TextId.CAMP_BALANCE_RECOMMEND_MAIL_TITLE), 
						this.getText(TextId.CAMP_BALANCE_RECOMMEND_MAIL_TITLE), 
						this.getText(TextId.SYSTEM), 
						OutputConsumeType.camp_balance_select_camp.getType(), 
						goodsList);
			}
		}
		role.setCampId(campId);
		result.success();
		result.setInfo(GameContext.getI18n().messageFormat(TextId.CAMP_BALANCE_SELECT_SUCCESS,camp.getName()));
		return result ;
	}
	
	@Override
	public Result changeCamp(RoleInstance role, byte campId){
		Result result = new Result();
		byte oldCampId = role.getCampId();
		try {
			CampType camp = CampType.get(campId);
			if(null == camp || !camp.isRealCamp()){
				result.setInfo(this.getText(TextId.ERROR_INPUT));
				return result ;
			}
			result = this.canChangeCamp(role, campId);
			if(!result.isSuccess()){
				return result;
			}
			
			/**
			 * 快速购买
			 */
			Result res = GameContext.getQuickBuyApp().doQuickBuy(role,  this.campChangeConfig.getGoodsId(), 
					this.campChangeConfig.getNum(), OutputConsumeType.camp_balance_change_camp, null);
			if(res.isIgnore()){
				return res;
			}
			if(!res.isSuccess()){
				return res;
			}
			
			role.setCampId(campId);
			GameContext.getRoleDAO().changeRoleCamp(role.getRoleId(), campId);
			
			String roleId = role.getRoleId();
			
			//通知阵营改变
			notifyCamp(role, campId);
			//排行榜相关
			GameContext.getRankApp().roleChangeCampOffRank(roleId, oldCampId);
			result.success();
			result.setInfo(GameContext.getI18n().messageFormat(TextId.CAMP_BALANCE_SELECT_SUCCESS,camp.getName()));
		} catch (Exception e) {
			role.setCampId(oldCampId);
			result.failure();
			result.setInfo(this.getText(TextId.Role_FAILURE));
			logger.error("CampBalanceApp.changeCamp",e);
		}
		return result;
	}
	
	private void notifyCamp(RoleInstance role, byte campId){
		try {
			C1533_CampBalanceCampNotifyMessage msg = new C1533_CampBalanceCampNotifyMessage();
			msg.setCampId(campId);
			msg.setRoleId(role.getIntRoleId());
			role.getMapInstance().broadcastMap(role, msg);
		} catch (Exception e) {
			this.logger.error("CampBalanceApp.notifyCamp error: ", e);
		}
	}
	
	private Result canChangeCamp(RoleInstance role, byte campId){
		Result result = new Result();
		if(!isChangeOpen()){
			return result.setInfo(this.getText(TextId.CAMP_BALANCE_CHANGE_NOT_OPEN));
		}
		if(role.getLevel() < this.campChangeConfig.getLevel()){
			return result.setInfo(GameContext.getI18n().messageFormat(TextId.CAMP_BALANCE_CHANGE_LEVEL_NOT_ENOUGH, this.campChangeConfig.getLevel()));
		}
		if(role.hasUnion()){
			return result.setInfo(this.getText(TextId.CAMP_BALANCE_CHANGE_HAS_FACTION));
		}
		if(campId == this.maxBoomCamp){
			return result.setInfo(this.getText(TextId.CAMP_BALANCE_CHANGE_NOT_MIN_CAMP));
		}
		
		byte roleCampId = role.getCampId();
		if(roleCampId == campId){
			return result.setInfo(this.getText(TextId.CAMP_BALANCE_CHANGE_SAME_CAMP));
		}
		return result.success();
	}
	
	@Override
	public void changeCampBoom(byte winCampId){
		try{
			for(CampBoom cb : this.campBoomMap.values()){
				if(null == cb){
					continue;
				}
				if(cb.getCampId() == winCampId){
					cb.setBoom(cb.getBoom() + this.campChangeConfig.getWinBoom());
					GameContext.getBaseDAO().update(cb);
					continue;
				}
				cb.setBoom(cb.getBoom() + this.campChangeConfig.getLoseBoom());
				GameContext.getBaseDAO().update(cb);
			}
			
			this.initCampChange();
		}catch(Exception e){
			logger.error("CampBalanceApp.changeCampBoom error",e);
		}
	}
}