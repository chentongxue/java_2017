package com.game.draco.app.richman;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sacred.alliance.magic.app.goods.GoodsOperateBean;
import sacred.alliance.magic.app.goods.behavior.result.GoodsResult;
import sacred.alliance.magic.base.AttributeType;
import sacred.alliance.magic.base.MapLogicType;
import sacred.alliance.magic.base.OperatorType;
import sacred.alliance.magic.base.OutputConsumeType;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.base.XlsSheetNameType;
import sacred.alliance.magic.constant.Cat;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.domain.GoodsBase;
import sacred.alliance.magic.util.Log4jManager;
import sacred.alliance.magic.util.RandomUtil;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.util.XlsPojoUtil;
import sacred.alliance.magic.vo.AbstractRole;
import sacred.alliance.magic.vo.MapInstance;
import sacred.alliance.magic.vo.Point;
import sacred.alliance.magic.vo.RoleInstance;
import sacred.alliance.magic.vo.map.MapRichManInstance;

import com.game.draco.GameContext;
import com.game.draco.app.mail.type.MailSendRoleType;
import com.game.draco.app.richman.config.RichManBox;
import com.game.draco.app.richman.config.RichManCard;
import com.game.draco.app.richman.config.RichManConfig;
import com.game.draco.app.richman.config.RichManDicePrice;
import com.game.draco.app.richman.config.RichManEvent;
import com.game.draco.app.richman.config.RichManMapEvent;
import com.game.draco.app.richman.config.RichManRandomCard;
import com.game.draco.app.richman.config.RichManState;
import com.game.draco.app.richman.domain.RoleRichMan;
import com.game.draco.app.richman.vo.RichManCheckResult;
import com.game.draco.app.richman.vo.RichManDiceType;
import com.game.draco.app.richman.vo.RichManEventType;
import com.game.draco.app.richman.vo.RichManRoleBehavior;
import com.game.draco.app.richman.vo.RichManRoleStat;
import com.game.draco.app.richman.vo.RichManStateType;
import com.game.draco.app.richman.vo.event.RichManEventBeAttacked;
import com.game.draco.app.richman.vo.event.RichManEventCouponAdd;
import com.game.draco.app.richman.vo.event.RichManEventCouponMul;
import com.game.draco.app.richman.vo.event.RichManEventGetCard;
import com.game.draco.app.richman.vo.event.RichManEventLogic;
import com.game.draco.app.richman.vo.event.RichManEventMeetGod;
import com.game.draco.app.richman.vo.event.RichManEventMove;
import com.game.draco.app.richman.vo.event.RichManEventNone;
import com.game.draco.app.richman.vo.event.RichManEventTrun;
import com.game.draco.message.item.RichManCardItem;
import com.game.draco.message.item.RichManEventItem;
import com.game.draco.message.item.RichManMapGridItem;
import com.game.draco.message.item.RichManRoleStatItem;
import com.game.draco.message.push.C0007_ConfirmationNotifyMessage;
import com.game.draco.message.request.C2652_RichManRoleDiceReqMessage;
import com.game.draco.message.response.C2652_RichManRoleDiceRespMessage;
import com.game.draco.message.response.C2658_RichManRoleStatRespMessage;
import com.game.draco.message.response.C2660_RichManRoleHeadAnimDelRespMessage;
import com.game.draco.message.response.C2661_RichManMapDataRespMessage;
import com.google.common.collect.Lists;

public class RichManAppImpl implements RichManApp {
	private final static byte GRID_NUM = 64;
	private final static byte MAX_DICE_VALUE = 6;
	private final static byte MIN_DICE_VALUE = 1;
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	private Map<Integer, RichManEvent> allEventMap = null;
	private Map<Byte, RichManMapEvent> allMapEventMap = null;
	private List<Integer> randomEventIdList = Lists.newArrayList();
	private RichManConfig richManConfig;
	private Map<Integer, RichManDicePrice> allDicePriceMap = null;
	private Map<Integer, RichManBox> allBoxMap = null;
	private List<RichManMapEvent> mapNoneEventList = Lists.newArrayList();
	private List<Integer> cardIdList = Lists.newArrayList();
	private Map<Integer, RichManCard> allCardMap = null;
	private Map<Byte, RichManState> allStateMap = null;
	private RichManRandomCard randomCardConfig = null;

	@Override
	public void setArgs(Object arg0) {

	}

	@Override
	public void start() {
		this.loadAllEvent();
		this.loadAllMapEvent();
		this.loadRandomEventId();
		this.loadConfig();
		this.loadStatConfig();
		this.loadCards();
		this.loadDicePrice();
		this.loadBoxConfig();
		this.loadRandomCard();
	}

	@Override
	public void stop() {

	}
	
	/**
	 * 加载所有事件配置
	 */
	private void loadAllEvent() {
		String fileName = "";
		String sheetName = "";
		try {
			fileName = XlsSheetNameType.richman_event.getXlsName();
			sheetName = XlsSheetNameType.richman_event.getSheetName();
			String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
			allEventMap = XlsPojoUtil.sheetToGenericMap(sourceFile, sheetName, RichManEvent.class);
			
			if(Util.isEmpty(allEventMap)) {
				Log4jManager.CHECK.error("not config the richman event,file=" + sourceFile + " sheet=" + sheetName);
				Log4jManager.checkFail();
			}
			
			for(Entry<Integer, RichManEvent> entry : this.allEventMap.entrySet()) {
				RichManEvent event = entry.getValue();
				if(null == event) {
					continue;
				}
				Result result = event.init();
				if(!result.isSuccess()) {
					Log4jManager.CHECK.error("richmanApp.loadAllEvent() error, " + result.getInfo());
					Log4jManager.checkFail();
					continue;
				}
			}
			
		} catch (Exception ex) {
			Log4jManager.CHECK.error("loadExel error : sourceFile = "
					+ fileName + " sheetName =" + sheetName, ex);
			Log4jManager.checkFail();
		}
	}
	
	/**
	 * 加载地图上事件配置
	 */
	private void loadAllMapEvent() {
		String fileName = "";
		String sheetName = "";
		try {
			fileName = XlsSheetNameType.richman_map_event.getXlsName();
			sheetName = XlsSheetNameType.richman_map_event.getSheetName();
			String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
			allMapEventMap = XlsPojoUtil.sheetToGenericLinkedMap(sourceFile, sheetName, RichManMapEvent.class);
			if(Util.isEmpty(allMapEventMap)){
				Log4jManager.CHECK.error("not config the richman map event,file=" + sourceFile + " sheet=" + sheetName);
				Log4jManager.checkFail();
			}
			
			byte maxGridId = 0;
			for(Entry<Byte, RichManMapEvent> entry : allMapEventMap.entrySet()) {
				RichManMapEvent mapEvent = entry.getValue();
				int eventId = mapEvent.getEventId();
				RichManEvent event = this.allEventMap.get(eventId);
				if(null == event) {
					Log4jManager.CHECK.error("map event config eventId=" + eventId + "not exist in allEvent,file="
							+ sourceFile + " sheet=" + sheetName);
					Log4jManager.checkFail();
					continue;
				}
				mapEvent.setRichManEvent(event);
				byte gridId = mapEvent.getGridId();
				if(gridId > maxGridId) {
					maxGridId = gridId;
				}
				//将地图上的无事件记录下来
				if(event.getEventType() == RichManEventType.None) {
					this.mapNoneEventList.add(mapEvent);
				}
			}
			//检测数据
			if(maxGridId != GRID_NUM - 1 && allMapEventMap.size() != GRID_NUM) {
				Log4jManager.CHECK.error("map event config grid num must be " + GRID_NUM + ",file="
						+ sourceFile + " sheet=" + sheetName);
				Log4jManager.checkFail();
			}
		} catch (Exception ex) {
			Log4jManager.CHECK.error("loadExel error : sourceFile = "
					+ fileName + " sheetName =" + sheetName, ex);
			Log4jManager.checkFail();
		}
	}
	
	/**
	 * 加载随机事件id
	 */
	private void loadRandomEventId() {
		String fileName = XlsSheetNameType.richman_random_event_ids.getXlsName();
		String sheetName = XlsSheetNameType.richman_random_event_ids.getSheetName();
		try {
			String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
			List<String> list = XlsPojoUtil.sheetToStringList(sourceFile, sheetName);
			if(null == list) {
				Log4jManager.CHECK.error("richMapApp randomEventId confg error ,file=" + sourceFile + " sheet=" + sheetName);
				Log4jManager.checkFail();
				return ;
			}
			for(String str : list) {
				int eventId = Integer.parseInt(str);
				if(!allEventMap.containsKey(eventId)) {
					Log4jManager.CHECK.error("richMapApp confg error ,randomEventId=" + eventId 
							+ " not exist,file=" + sourceFile + " sheet=" + sheetName);
					Log4jManager.checkFail();
					continue ;
				}
				this.randomEventIdList.add(eventId);
			}
		} catch (Exception ex) {
			Log4jManager.CHECK.error("loadExel error : sourceFile = "
					+ fileName + " sheetName =" + sheetName, ex);
			Log4jManager.checkFail();
		}
	}
	
	/**
	 * 加载大富翁基础配置
	 */
	private void loadConfig() {
		String fileName = XlsSheetNameType.richman_config.getXlsName();
		String sheetName = XlsSheetNameType.richman_config.getSheetName();
		try {
			String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
			richManConfig = XlsPojoUtil.getEntity(sourceFile, sheetName, RichManConfig.class);
			if(null == richManConfig) {
				Log4jManager.CHECK.error("richMapApp not config richManConfig,file="
						+ sourceFile + " sheet=" + sheetName);
				Log4jManager.checkFail();
			}
			String mapId = richManConfig.getMapId();
			sacred.alliance.magic.app.map.Map map = GameContext.getMapApp().getMap(mapId);
			if(null == map){
				Log4jManager.checkFail();
				Log4jManager.CHECK.error("not any config: The map is not exist.mapId = " + mapId);
			}
			//将地图逻辑修改为大富翁类型
			map.getMapConfig().setLogictype((byte) MapLogicType.richman.getType());
		} catch (Exception ex) {
			Log4jManager.CHECK.error("loadExel error : sourceFile = "
					+ fileName + " sheetName =" + sheetName, ex);
			Log4jManager.checkFail();
		}
	}
	
	/**
	 * 加载道具卡配置
	 */
	private void loadCards() {
		String fileName = XlsSheetNameType.richman_card_ids.getXlsName();
		String sheetName = XlsSheetNameType.richman_card_ids.getSheetName();
		try {
			String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
			Map<Integer, RichManCard> allCardMap = XlsPojoUtil.sheetToGenericMap(sourceFile, sheetName, RichManCard.class);
			if(Util.isEmpty(allCardMap)) {
				Log4jManager.CHECK.error("richMapApp cardId confg error ,file=" + sourceFile + " sheet=" + sheetName);
				Log4jManager.checkFail();
				return ;
			}
			for(Entry<Integer, RichManCard> entry : allCardMap.entrySet()) {
				RichManCard card = entry.getValue();
				if(null == card) {
					continue ;
				}
				int goodsId = card.getGoodsId();
				GoodsBase gt = GameContext.getGoodsApp().getGoodsBase(goodsId);
				if(null == gt) {
					Log4jManager.CHECK.error("richMapApp confg error ,cardId=" + goodsId 
							+ " not exist,file=" + sourceFile + " sheet=" + sheetName);
					Log4jManager.checkFail();
					continue ;
				}
				card.init();
				cardIdList.add(goodsId);
				//状态
				byte stateId = card.getStateId();
				if(stateId > 0 && null == this.allStateMap.get(stateId)) {
					Log4jManager.CHECK.error("richMapApp confg error ,cardId=" + goodsId 
							+ ",stateId=" + stateId + " not exist,file=" + sourceFile + " sheet=" + sheetName);
					Log4jManager.checkFail();
					continue ;
				}
			}
			this.allCardMap = allCardMap;
		} catch (Exception ex) {
			Log4jManager.CHECK.error("loadExel error : sourceFile = "
					+ fileName + " sheetName =" + sheetName, ex);
			Log4jManager.checkFail();
		}
	}

	/**
	 * 加载普通掷骰子的阶梯价格
	 */
	private void loadDicePrice() {
		String fileName = XlsSheetNameType.richman_dice_price.getXlsName();
		String sheetName = XlsSheetNameType.richman_dice_price.getSheetName();
		try {
			String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
			allDicePriceMap = XlsPojoUtil.sheetToGenericMap(sourceFile, sheetName, RichManDicePrice.class);
			if(Util.isEmpty(allDicePriceMap)){
				Log4jManager.CHECK.error("not config the richman dice price,file=" + sourceFile + " sheet=" + sheetName);
				Log4jManager.checkFail();
			}
		} catch (Exception ex) {
			Log4jManager.CHECK.error("loadExel error : sourceFile = "
					+ fileName + " sheetName =" + sheetName, ex);
			Log4jManager.checkFail();
		}
	}
	
	private void loadBoxConfig() {
		String fileName = XlsSheetNameType.richman_box_config.getXlsName();
		String sheetName = XlsSheetNameType.richman_box_config.getSheetName();
		try {
			String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
			this.allBoxMap = XlsPojoUtil.sheetToGenericMap(sourceFile, sheetName, RichManBox.class);
			if(Util.isEmpty(this.allBoxMap)){
				Log4jManager.CHECK.error("not config the richman box,file=" + sourceFile + " sheet=" + sheetName);
				Log4jManager.checkFail();
			}
			for(Entry<Integer, RichManBox> boxEntry : this.allBoxMap.entrySet()) {
				RichManBox box = boxEntry.getValue();
				if(null == box) {
					continue;
				}
				box.init();
				Map<Integer, Integer> weightMap = box.getWeightMap();
				for(Entry<Integer, Integer> entry : weightMap.entrySet()) {
					int eventId = entry.getKey();
					if(this.allEventMap.containsKey(eventId)) {
						continue;
					}
					Log4jManager.CHECK.error("boxId= " + box.getBoxId() + " eventId= " + eventId +" not exist,file=" 
							+ sourceFile + " sheet=" + sheetName);
					Log4jManager.checkFail();
				}
			}
		}catch (Exception ex) {
			Log4jManager.CHECK.error("loadExel error : sourceFile = "
					+ fileName + " sheetName =" + sheetName, ex);
			Log4jManager.checkFail();
		}
	}
	
	/**
	 * 加载状态配置(瘫痪,保护)
	 */
	private void loadStatConfig() {
		String fileName = XlsSheetNameType.richman_stat_config.getXlsName();
		String sheetName = XlsSheetNameType.richman_stat_config.getSheetName();
		try {
			String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
			this.allStateMap = XlsPojoUtil.sheetToGenericMap(sourceFile, sheetName, RichManState.class);
			if(Util.isEmpty(this.allStateMap)){
				Log4jManager.CHECK.error("not config stat,file=" + sourceFile + " sheet=" + sheetName);
				Log4jManager.checkFail();
			}
			
			for(Entry<Byte, RichManState> entry : this.allStateMap.entrySet()) {
				RichManState stateConfig = entry.getValue();
				if(null == stateConfig) {
					continue ;
				}
				stateConfig.init();
			}
			
		}catch (Exception ex) {
			Log4jManager.CHECK.error("loadExel error : sourceFile = "
					+ fileName + ", sheetName =" + sheetName, ex);
			Log4jManager.checkFail();
		}
	}
	
	private void loadRandomCard() {
		String fileName = XlsSheetNameType.richman_random_card.getXlsName();
		String sheetName = XlsSheetNameType.richman_random_card.getSheetName();
		try {
			String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
			this.randomCardConfig = XlsPojoUtil.getEntity(sourceFile, sheetName, RichManRandomCard.class);
			if(null == this.randomCardConfig) {
				Log4jManager.CHECK.error("not config RichManRandomCard,file=" + sourceFile + " sheet=" + sheetName);
				Log4jManager.checkFail();
				return ;
			}
			this.randomCardConfig.init();
		} catch (Exception ex) {
			Log4jManager.CHECK.error("loadExel error : sourceFile = "
					+ fileName + ", sheetName =" + sheetName, ex);
			Log4jManager.checkFail();
		}
	}

	@Override
	public int onLogin(RoleInstance role, Object context) {
		try {
			RoleRichMan rrm = GameContext.getBaseDAO().selectEntity(RoleRichMan.class, RoleRichMan.ROLE_ID, role.getIntRoleId());
			if(null == rrm) {
				rrm = new RoleRichMan();
				rrm.setRoleId(role.getIntRoleId());
				rrm.setExistRecord(false);
			}
			else {
				rrm.setExistRecord(true);
			}
			this.postFromStore(rrm);
			GameContext.getUserRichManApp().addRoleRichMan(rrm);
		} catch (Exception ex) {
			logger.error("richMapApp.login() error, roleId= " + role.getRoleId(), ex);
			return 0;
		}
		return 1;
	}

	@Override
	public int onLogout(RoleInstance role, Object context) {
		try {
			RoleRichMan rrm = GameContext.getUserRichManApp().getRoleRichMan(role.getIntRoleId());
			if(null == rrm) {
				return 0;
			}
			this.preToStore(rrm);
			if(rrm.isExistRecord()) {
				GameContext.getBaseDAO().update(rrm);
			}else {
				GameContext.getBaseDAO().insert(rrm);
			}
		} catch (Exception ex) {
			logger.error("richMapApp.logout() error, roleId= " + role.getRoleId(), ex);
			return 0; 
		}
		return 1;
	}
	
	@Override
	public int onCleanup(String roleId, Object context) {
		// TODO Auto-generated method stub
		return 0;
	}
	
	private void postFromStore(RoleRichMan richMan) {
		Map<Integer, Byte> map = Util.parseIntegerByteMap(richMan.getRandomEventInfo());
		richMan.setRandomEventMap(map);
	}
	
	private void preToStore(RoleRichMan rrm) {
		rrm.setRandomEventInfo(Util.kvMapToString(rrm.getRandomEventMap()));
	}

	@Override
	public int getTotalCoupon(int roleId) {
		RoleRichMan rrm = GameContext.getUserRichManApp().getRoleRichMan(roleId);
		if(null == rrm) {
			return 0;
		}
		return rrm.getTotalCoupon();
	}

	@Override
	public void setTotalCoupon(int roleId, int coupon) {
		RoleRichMan rrm = GameContext.getUserRichManApp().getRoleRichMan(roleId);
		if(null == rrm) {
			return ;
		}
		rrm.setTotalCoupon(coupon);
	}
	
	@Override
	public int getTodayCoupon(int roleId) {
		RoleRichMan rrm = GameContext.getUserRichManApp().getRoleRichMan(roleId);
		if(null == rrm) {
			return 0;
		}
		return rrm.getTodayCoupon();
	}
	
	@Override
	public void setTodayCoupon(int roleId, int coupon) {
		RoleRichMan rrm = GameContext.getUserRichManApp().getRoleRichMan(roleId);
		if(null == rrm) {
			return ;
		}
		rrm.setTodayCoupon(coupon);
	}

	@Override
	public Result enterMap(RoleInstance role) {
		Result result = this.canEnter(role);
		if(!result.isSuccess()) {
			return result;
		}
		try {
			//切换地图 
			Point targetPoint = new Point(this.richManConfig.getMapId(), 0 , 0);
			GameContext.getUserMapApp().changeMap(role, targetPoint);
			return result.success();
		} catch (Exception ex) {
			logger.error("richManApp.join() error, ", ex);
		}
		return result;
	}
	
	private Result canEnter(RoleInstance role) {
		Result result = new Result();
		RoleRichMan rrm = GameContext.getUserRichManApp().getRoleRichMan(role.getIntRoleId());
		if(rrm.getCurJoinNum() >= this.richManConfig.getJoinNum()) {
			result.setInfo(this.getText(TextId.Richman_join_num_no_enough));
			return result;
		}
		return result.success();
	}
	
	private void incrJoinNum(RoleInstance role) {
		RoleRichMan rrm = GameContext.getUserRichManApp().getRoleRichMan(role.getIntRoleId());
		if(null == rrm) {
			return ;
		}
		rrm.incrJoinNum();
	}

	@Override
	public Map<Byte, RichManMapEvent> getAllMapEvent() {
		return this.allMapEventMap;
	}

	@Override
	public byte getMapGridNum() {
		return GRID_NUM;
	}

	@Override
	public RichManEvent getRichManEvent(int eventId) {
		return this.allEventMap.get(eventId);
	}

	@Override
	public List<RichManCardItem> getRichManCardItemList() {
		if(Util.isEmpty(this.allCardMap)) {
			return null;
		}
		List<RichManCardItem> itemList = new ArrayList<RichManCardItem>();
		for(Entry<Integer, RichManCard> entry : this.allCardMap.entrySet()) {
			RichManCard card = entry.getValue();
			if(null == card) {
				continue ;
			}
			int goodsId = card.getGoodsId();
			GoodsBase gb = GameContext.getGoodsApp().getGoodsBase(goodsId);
			if(null == gb) {
				continue ;
			}
			RichManCardItem item = new RichManCardItem();
			item.setGoodsId(goodsId);
			item.setGoodsImageId(gb.getImageId());
			item.setGoodsName(gb.getName());
			item.setTargetNum(card.getTargetNum());
			item.setTargetType(card.getTargetType());
			itemList.add(item);
		}
		return itemList;
	}

	@Override
	public byte getFreeDiceDoubleNum(int roleId) {
		RoleRichMan rrm  = GameContext.getUserRichManApp().getRoleRichMan(roleId);
		int remainNum = richManConfig.getFreeDiceDoubleNum() - rrm.getDiceDoubleNum();
		if(remainNum <= 0) {
			return 0;
		}
		return (byte)remainNum;
	}

	@Override
	public byte getFreeDiceNormalNum(int roleId) {
		RoleRichMan rrm  = GameContext.getUserRichManApp().getRoleRichMan(roleId);
		int remainNum = richManConfig.getFreeDiceNum() - rrm.getDiceNormalNum();
		if(remainNum <= 0) {
			return 0;
		}
		return (byte)remainNum;
	}

	@Override
	public byte getFreeDiceRemoteNum(int roleId) {
		RoleRichMan rrm  = GameContext.getUserRichManApp().getRoleRichMan(roleId);
		int remainNum = richManConfig.getFreeDiceRemoteNum() - rrm.getDiceRemoteNum();
		if(remainNum <= 0) {
			return 0;
		}
		return (byte)remainNum;
	}

	@Override
	public byte getRoundJionNum(int roleId) {
		RoleRichMan rrm  = GameContext.getUserRichManApp().getRoleRichMan(roleId);
		int remainNum = richManConfig.getJoinNum() - rrm.getCurJionNum();
		if(remainNum <= 0) {
			return 0;
		}
		return (byte)remainNum;
	}

	@Override
	public Result roleDice(RoleInstance role, String paramStr) {
		Result result = this.canDice(role);
		if(!result.isSuccess()) {
			return result;
		}
		result.failure();
		
		if(Util.isEmpty(paramStr)) {
			return result.setInfo(this.getText(TextId.SYSTEM_ERROR));
		}
		try {
			String[] params = paramStr.split(Cat.comma);
			if(params.length < 2) {
				return result.setInfo(this.getText(TextId.SYSTEM_ERROR));
			}
			RichManDiceType diceType = RichManDiceType.get(Byte.valueOf(params[0]));
			if(null == diceType) {
				return result.setInfo(this.getText(TextId.Sys_Param_Error));
			}
			byte diceValue = Byte.valueOf(params[1]);
			boolean isConfirm = false;
			if(params.length > 1) {
				isConfirm = Byte.valueOf(params[2]) == 1;
			}
			return this.roleDice(role, diceType, diceValue, isConfirm);
		} catch (Exception ex) {
			logger.error("richmanApp.roleDice() error, ", ex);
		}
		return result;
	}
	
	private Result canDice(RoleInstance role) {
		RichManCheckResult result = this.baseCheck(role);
		if(!result.isSuccess()) {
			result.setInfo(this.getText(TextId.Sys_Param_Error));
			return result;
		}
		result.failure();
		//角色处于坏状态
		RichManRoleStat roleStat = result.getRoleStat();
		if(this.inBadState(roleStat)) {
			return result.setInfo(this.getText(TextId.Richman_dice_lull_time_no_over));
		}
		result.success();
		return result;
	}
	
	private boolean inBadState(RichManRoleStat roleStat) {
		Map<Byte, Long> stateOverTimeMap = roleStat.getStateOverTimeMap();
		if(Util.isEmpty(stateOverTimeMap)) {
			return false ;
		}
		for(Entry<Byte, Long> entry : stateOverTimeMap.entrySet()) {
			byte stateId = entry.getKey();
			RichManState stateConifg = this.allStateMap.get(stateId);
			if(stateConifg.getStateType() == RichManStateType.Lull) {
				return true;
			}
		}
		return false;
	}

	private Result roleDice(RoleInstance role, RichManDiceType diceType, byte diceValue, boolean isConfirm) {
		Result result = new Result();
		int roleId = role.getIntRoleId();
		byte remainDiceNum = getRemainDiceNum(roleId, diceType);
		if(remainDiceNum > 0) {
			this.sendRoleDiceRespMsg(role, diceType, diceValue);
			return result.success();
		}
		//普通掷骰子消费有次数限制
		if(diceType == RichManDiceType.Normal) {
			int feeDiceNum = this.getRoleNormalDiceFeeNum(roleId);
			if(feeDiceNum >= this.richManConfig.getNormalDiceFeeNum()) {
				result.setInfo(GameContext.getI18n().getText(TextId.Richman_normal_dice_fee_num_max));
				return result;
			}
		}
		if(!isConfirm) {
			//如果剩余次数为0则弹二次确认面板
			short cmdId = new C2652_RichManRoleDiceReqMessage().getCommandId();
			String paramStr = diceType.getType() + Cat.comma + diceValue + Cat.comma + "1";
			String info = GameContext.getI18n().messageFormat(TextId.Richman_dice_confirm_info, 
					this.getDiceFee(roleId, diceType), GameContext.getI18n().getText(diceType.getName()));
			this.sendConfirmationNotifyMessage(role, cmdId, paramStr, info);
			return result.success();
		}
		
		int needGold = this.getDiceFee(roleId, diceType);
		if(needGold <= 0) {
			this.sendRoleDiceBehavior(role, diceValue);
			return result.success();
		}
		//【游戏币/潜能/钻石不足弹板】 判断
		Result ar = GameContext.getUserAttributeApp().getEnoughResult(role, AttributeType.goldMoney, needGold);
		if(ar.isIgnore()){
			return ar;
		}
		if(!ar.isSuccess()){
			result.setInfo(this.getText(TextId.Richman_dice_gold_no_enough));
			return result;
		}
//		if(role.getGoldMoney() < needGold) {
//			result.setInfo(this.getText(TextId.Richman_dice_gold_no_enough));
//			return result;
//		}
		//扣除消耗
		GameContext.getUserAttributeApp().changeRoleMoney(role,	AttributeType.goldMoney, 
				OperatorType.Decrease,	needGold, OutputConsumeType.richman_dice_consume);
		this.sendRoleDiceRespMsg(role, diceType, diceValue);
		return result.success();
	}
	
	private String getText(String textId){
		return GameContext.getI18n().getText(textId);
	}
	
	/**
	 * 处理掷骰子成功
	 */
	private void sendRoleDiceRespMsg(RoleInstance role, RichManDiceType diceType, byte diceValue) {
		int realDiceValue = this.getRoleDiceValue(diceType, diceValue);
		//更新掷骰子次数
		this.updateRoleDiceNum(role.getIntRoleId(), diceType);
		this.sendRoleDiceBehavior(role, realDiceValue);
		//掷骰子成功通知
		C2652_RichManRoleDiceRespMessage respMsg = new C2652_RichManRoleDiceRespMessage();
		respMsg.setDiceType(diceType.getType());
		respMsg.setDiceValue(diceValue);
		respMsg.setDiceRemainNum(this.getRemainDiceNum(role.getIntRoleId(), diceType));
		role.getBehavior().sendMessage(respMsg);
	}
	
	private RichManCheckResult baseCheck(RoleInstance role) {
		RichManCheckResult result = new RichManCheckResult();
		MapInstance mapInstance = role.getMapInstance();
		if(null == mapInstance 
				|| mapInstance.getMap().getMapConfig().getLogictype() != MapLogicType.richman.getType()){
			result.setInfo(this.getText(TextId.Sys_Param_Error));
			return result;
		}
		RichManRoleStat roleStat = ((MapRichManInstance)mapInstance).getRoleStat(role.getIntRoleId());
		if(null == roleStat) {
			result.setInfo(this.getText(TextId.Sys_Param_Error));
			return result;
		}
		result.setRoleStat(roleStat);
		result.setMapInstance((MapRichManInstance)mapInstance);
		result.success();
		return result;
	}
	
	/**
	 * 掷骰子结果放入地图 
	 */
	private void sendRoleDiceBehavior(RoleInstance role, int diceValue) {
		RichManCheckResult result = this.baseCheck(role);
		if(!result.isSuccess()) {
			return ;
		}
		RichManEvent event = new RichManEvent();
		event.setType(RichManEventType.Move.getType());
		event.setEventType(RichManEventType.Move);
		event.setEventValue(diceValue);
		result.getMapInstance().addRoleBehavior(new RichManRoleBehavior(
				role.getIntRoleId(), event));
	}
	
	private void updateRoleDiceNum(int roleId, RichManDiceType diceType) {
		RoleRichMan rrm = GameContext.getUserRichManApp().getRoleRichMan(roleId);
		switch(diceType) {
		case Normal:
			rrm.incrDiceNormalNum();
		case Double:
			rrm.incrDiceDoubleNum();
		case Remote:
			rrm.incrDiceRemoteNum();
		}
	}
	
	private int getRoleDiceValue(RichManDiceType diceType, byte diceValue) {
		switch(diceType) {
		case Normal:
			return RandomUtil.randomInt(MIN_DICE_VALUE, MAX_DICE_VALUE);
		case Double:
			return RandomUtil.randomInt(MIN_DICE_VALUE, MAX_DICE_VALUE * 2);
		case Remote:
			return diceValue;
		default:
			return 0;
		}
	}
	
	private byte getRemainDiceNum(int roleId, RichManDiceType diceType) {
		switch(diceType) {
		case Normal:
			return this.getFreeDiceNormalNum(roleId);
		case Double:
			return this.getFreeDiceDoubleNum(roleId);
		case Remote:
			return this.getFreeDiceRemoteNum(roleId);
		default:
			return (byte)0;
		}
	}
	
	private int getDiceFee(int roleId, RichManDiceType diceType) {
		switch(diceType) {
		case Normal:
			int extraNum =this.getRoleNormalDiceFeeNum(roleId);
			return allDicePriceMap.get(extraNum).getGoldMoney();
		case Double:
			return richManConfig.getDiceDoubleGold();
		case Remote:
			return richManConfig.getDiceRemoteGold();
		default:
			return 0;
		}
	}
	
	private int getRoleNormalDiceFeeNum(int roleId) {
		RoleRichMan rrm  = GameContext.getUserRichManApp().getRoleRichMan(roleId);
		return rrm.getDiceNormalNum() - richManConfig.getFreeDiceNum();
	}
	
	/**
	 * 发送二次确认消息
	 * @param role
	 * @param affirmCmdId
	 * @param affirmParam
	 * @param info
	 */
	private void sendConfirmationNotifyMessage(AbstractRole role, short affirmCmdId, String affirmParam, String info){
		C0007_ConfirmationNotifyMessage message = new C0007_ConfirmationNotifyMessage();
		message.setAffirmCmdId(affirmCmdId);
		message.setAffirmParam(affirmParam);
		message.setInfo(info);
		role.getBehavior().sendMessage(message);
	}

	@Override
	public RichManEventLogic getEventLogic(RichManEventType eventType) {
		switch (eventType) {
		case CouponMul:
			return RichManEventCouponMul.getInstance();
		case CouponAdd:
			return RichManEventCouponAdd.getInstance();
		case Move:
			return RichManEventMove.getInstance();
		case Trun:
			return RichManEventTrun.getInstance();
		case GetCard:
			return RichManEventGetCard.getInstance();
		case GodWealth:
		case GodBadLuck:
			return RichManEventMeetGod.getInstance();
		case BeAttacked:
			return RichManEventBeAttacked.getInstance();
		default:
			return RichManEventNone.getInstance();
		}
	}

	@Override
	public void roleArrived(RoleInstance role) {
		RichManCheckResult result = this.baseCheck(role);
		if(!result.isSuccess()) {
			return ;
		}
		MapRichManInstance mapInstance = result.getMapInstance();
		mapInstance.roleArrived(role);
		//如果有财神,衰神buff
		this.dealRoleWithGodBuff(role, result.getMapInstance());
	}
	
	private void dealRoleWithGodBuff(RoleInstance role, MapRichManInstance mapInstance) {
		RoleRichMan rrm = GameContext.getUserRichManApp().getRoleRichMan(role.getIntRoleId());
		Map<Integer, Byte> randomEventMap = rrm.getRandomEventMap();
		if(Util.isEmpty(randomEventMap)) {
			return ;
		}
		int value = 0;
		Iterator<Entry<Integer, Byte>> it = randomEventMap.entrySet().iterator();
		while(it.hasNext()) {
			Entry<Integer, Byte> entry = it.next();
			int eventId = entry.getKey();
			byte step = entry.getValue();
			if(step <= 0) {
				continue;
			}
			RichManEvent event = this.allEventMap.get(eventId);
			if(null == event) {
				continue;
			}
			int prefix = 1;
			RichManEventType eventType = event.getEventType();
			if(eventType == RichManEventType.GodBadLuck) {
				prefix = -1;
			}
			//随机点券值
			int rand = RandomUtil.randomInt(this.richManConfig.getGodMinCoupon(),
					this.richManConfig.getGodMaxCoupon());
			value += (prefix * rand);
			//更新步数
			byte newStep = (byte)(step - 1);
			if(newStep != 0) {
				randomEventMap.put(eventId, newStep);
				continue ;
			}
			//随机事件消失
			randomEventMap.remove(eventId);
			//删除玩家头顶动画
			C2660_RichManRoleHeadAnimDelRespMessage animDelRespMsg = new C2660_RichManRoleHeadAnimDelRespMessage();
			animDelRespMsg.setRoleId(role.getIntRoleId());
			RichManEventItem eventItem = new RichManEventItem();
			eventItem.setId((short)eventId);
			eventItem.setAnimId(event.getAnimId());
			List<RichManEventItem> eventItemList = Lists.newArrayList();
			eventItemList.add(eventItem);
			animDelRespMsg.setEventItemList(eventItemList);
			mapInstance.broadcastMap(null, animDelRespMsg);
		}
		
		if(value == 0) {
			return ;
		}
		this.changeRoleToadyCoupon(role, value);
	
	}

	@Override
	public void roleGetGoods(int roleId, int goodsId) {
		try {
			List<GoodsOperateBean> goodsList = Lists.newArrayList();
			GoodsOperateBean goodsBean = new GoodsOperateBean(goodsId, 1);
			goodsList.add(goodsBean);
			
			RoleInstance role = GameContext.getOnlineCenter()
				.getRoleInstanceByRoleId(String.valueOf(roleId));
			//角色不在线则发邮件
			if(null == role) {
				this.sendMail(String.valueOf(roleId), goodsList);
				return ;
			}
			//玩家在线
			GoodsResult goodsResult = GameContext.getUserGoodsApp().addGoodsBeanForBag(role,
					goodsList, OutputConsumeType.richman_event_output);
			if(goodsResult.isSuccess()) {
				return ;
			}
			//放入背包不成功则发邮件
			this.sendMail(String.valueOf(roleId), goodsList);
		}catch (Exception ex) {
			logger.error("richmanApp.roleGetGoods() error, ", ex);
		}
	}
	
	private void sendMail(String roleId, List<GoodsOperateBean> goodsList){
		String title =  GameContext.getI18n().getText(TextId.Richman_mail_title);
		String context = title;
		OutputConsumeType ocType = OutputConsumeType.richman_event_output;
		GameContext.getMailApp().sendMail(roleId, title, context,
				MailSendRoleType.System.getName(), ocType.getType(), goodsList);
	}

	@Override
	public RichManBox getRichManBox(int boxId) {
		return this.allBoxMap.get(boxId);
	}

	@Override
	public RichManConfig getRichManConfig() {
		return this.richManConfig;
	}

	@Override
	public void changeRoleToadyCoupon(RoleInstance role, int value) {
		boolean addOrSub = value > 0;
		GameContext.getUserAttributeApp().changeAttribute(role, AttributeType.todayCoupon,
				addOrSub ? OperatorType.Add : OperatorType.Decrease, Math.abs(value), 
				addOrSub ? OutputConsumeType.richman_event_output : OutputConsumeType.richman_event_consume);
		role.getBehavior().notifyAttribute();
	}

	@Override
	public RichManEvent getRandomEvent() {
		int index = RandomUtil.randomInt(0, this.randomEventIdList.size() -1);
		int eventId = this.randomEventIdList.get(index);
		return this.allEventMap.get(eventId);
	}

	@Override
	public int getRandomCardId() {
		Integer cardId = Util.getWeightCalct(this.randomCardConfig.getWeightMap());
		if(null == cardId) {
			return -1;
		}
		return cardId;
	}

	@Override
	public Result roleUseCard(RoleInstance role, int cardId, int[] targetIds) {
		RichManCheckResult result = this.baseCheck(role);
		if(!result.isSuccess()) {
			return result;
		}
		result.failure();
		RichManCard card = this.allCardMap.get(cardId);
		if(null == card) {
			return result.setInfo(this.getText(TextId.Sys_Param_Error));
		}
		GoodsBase gb = GameContext.getGoodsApp().getGoodsBase(cardId);
		if(null == gb) {
			return result.setInfo(this.getText(TextId.Sys_Param_Error));
		}
		//走快捷购买逻辑
		Result res = GameContext.getQuickBuyApp().doQuickBuy(role, cardId,1,
				OutputConsumeType.richman_use_card_consume, null);
		if(result.isIgnore()){
			return result;
		}
		if(!res.isSuccess()){
			return res;
		}
		
		if(null != targetIds && targetIds.length == 0) {
			return result.success();
		}
		MapRichManInstance mapInstance = result.getMapInstance();
		for(int targetId : targetIds) {
			AbstractRole targetRole = mapInstance.getAbstractRole(String.valueOf(targetId));
			if(null == targetRole) {
				continue;
			}
			//产生大富翁事件
			RichManEvent event = new RichManEvent();
			event.setEventType(RichManEventType.BeAttacked);
			event.setEventValue(cardId);
			event.setEffectId(card.getEffectId());
			RichManRoleBehavior behavior = new RichManRoleBehavior(targetId,
					role.getIntRoleId(), role.getRoleName(), event);
			mapInstance.addRoleBehavior(behavior);
		}
		return result.success();
	}

	@Override
	public RichManCard getRichManCard(int cardId) {
		return this.allCardMap.get(cardId);
	}

	@Override
	public void notifyRichManRoleStatOver(RoleInstance role) {
		RichManCheckResult result = this.baseCheck(role);
		if(!result.isSuccess()) {
			return ;
		}
		RichManRoleStat roleStat = result.getRoleStat();
		if(!roleStat.needNotifyTime()) {
			return ;
		}
		long now = System.currentTimeMillis();
		List<RichManRoleStatItem> statItemList = Lists.newArrayList();
		Map<Byte, Long> stateOverTimeMap = roleStat.getStateOverTimeMap();
		Iterator<Entry<Byte, Long>> it = stateOverTimeMap.entrySet().iterator();
		while(it.hasNext()) {
			Entry<Byte, Long> entry = it.next();
			byte stateId = entry.getKey();
			RichManState stateConfig = this.allStateMap.get(stateId);
			if(null == stateConfig) {
				continue ;
			}
			long overTime = entry.getValue();
			long remainLullTime = overTime - now;
			if(remainLullTime > 0) {
				continue ;
			}
			RichManRoleStatItem statItem = new RichManRoleStatItem();
			statItem.setId(stateId);
			statItemList.add(statItem);
			it.remove();
		}
		if(Util.isEmpty(statItemList)) {
			return ;
		}
		//通知客户端
		C2658_RichManRoleStatRespMessage respMsg = new C2658_RichManRoleStatRespMessage();
		respMsg.setStatItemList(statItemList);
		role.getBehavior().sendMessage(respMsg);
		//玩家头顶动画删除广播
		List<RichManEventItem> eventItemList = Lists.newArrayList();
		for(RichManRoleStatItem statItem : statItemList) {
			if(null == statItem) {
				continue;
			}
			byte stateId = statItem.getId();
			RichManState stateConfig = this.allStateMap.get(stateId);
			if(null == stateConfig) {
				continue ;
			}
			RichManEventItem eventItem = new RichManEventItem();
			eventItem.setAnimId(stateConfig.getEffectId());
			eventItem.setId(stateId);
			eventItemList.add(eventItem);
		}
		C2660_RichManRoleHeadAnimDelRespMessage animDelRespMsg = new C2660_RichManRoleHeadAnimDelRespMessage();
		animDelRespMsg.setRoleId(role.getIntRoleId());
		animDelRespMsg.setEventItemList(eventItemList);
		result.getMapInstance().broadcastMap(null, animDelRespMsg);
	}

	@Override
	public RichManState getRichManState(byte stateId) {
		return this.allStateMap.get(stateId);
	}

	@Override
	public void mapGetDataAndEnter(RoleInstance role) {
		MapInstance mapInstance = role.getMapInstance();
		if(null == mapInstance 
				|| mapInstance.getMap().getMapConfig().getLogictype() != MapLogicType.richman.getType()){
			return ;
		}
		
		MapRichManInstance richManMapInstance = (MapRichManInstance)mapInstance;
		int roleId = role.getIntRoleId();
		//给玩家随机一个空事件位置
		int randIndex = Util.randomInRange(0, this.mapNoneEventList.size()-1);
		RichManMapEvent noneMapEvent = this.mapNoneEventList.get(randIndex);
		byte gridId = noneMapEvent.getGridId();
		this.incrJoinNum(role);
		//随机玩家位置
		RichManRoleStat roleStat = new RichManRoleStat();
		roleStat.setGridId(gridId);
		roleStat.setFace(RichManRoleStat.FACE_FORWARD);
		richManMapInstance.addRoleStat(roleId, roleStat);
		richManMapInstance.addRoleIdInGrid(roleId, gridId);
		
		try {
			C2661_RichManMapDataRespMessage respMsg = new C2661_RichManMapDataRespMessage();
			//个人数据
			respMsg.setRemainJoinNum(this.getRoundJionNum(roleId));
			respMsg.setFreeDiceNum(this.getFreeDiceNormalNum(roleId));
			respMsg.setFreeDiceRemoteNum(this.getFreeDiceRemoteNum(roleId));
			respMsg.setFreeDiceDoubleNum(this.getFreeDiceDoubleNum(roleId));
			//card
			respMsg.setCardItemList(this.getRichManCardItemList());
			//地图数据遍历地图格子
			RichManMapEvent[] mapEvent = richManMapInstance.getMapEvent();
			List<RichManMapGridItem> mapGridItemList = Lists.newArrayList();
			for(byte i = 0; i < GRID_NUM; i++) {
				RichManMapGridItem item = new RichManMapGridItem();
				RichManMapEvent rmme = mapEvent[i];
				item.setGridId(i);
				item.setGridX(rmme.getPosX());
				item.setGridY(rmme.getPosY());
				//固定地图事件
				RichManEvent event = rmme.getRichManEvent();
				RichManEventItem eventItem = new RichManEventItem();
				eventItem.setId((short)event.getId());
				eventItem.setAnimId(event.getAnimId());
				item.setEventItem(eventItem);
				//随机事件
				item.setRandomEventItemList(richManMapInstance.getGridRandomEventList(i));
				//其他玩家
				item.setMapRoleItemList(richManMapInstance.getGridRoleItemList(i));
				mapGridItemList.add(item);
			}
			respMsg.setMapGridItemList(mapGridItemList);
			role.getBehavior().sendMessage(respMsg);
			//广播角色进入地图
			richManMapInstance.broadcastRoleEnterExist(role, (byte)0, (byte)gridId);
		} catch (Exception ex) {
			logger.error("mapRichManInstance.enter() error, roleId=" + role.getRoleId(), ex);
		}
	}
	
}
