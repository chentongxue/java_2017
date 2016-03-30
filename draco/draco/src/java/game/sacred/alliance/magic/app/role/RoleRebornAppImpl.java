package sacred.alliance.magic.app.role;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sacred.alliance.magic.app.goods.Util;
import sacred.alliance.magic.app.quickbuy.QuickCostHelper;
import sacred.alliance.magic.base.AttributeType;
import sacred.alliance.magic.base.ChangeMapResult;
import sacred.alliance.magic.base.OperatorType;
import sacred.alliance.magic.base.OutputConsumeType;
import sacred.alliance.magic.base.RebornNotifyItemType;
import sacred.alliance.magic.base.RebornType;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.base.RoleType;
import sacred.alliance.magic.base.XlsSheetNameType;
import sacred.alliance.magic.constant.ParasConstant;
import sacred.alliance.magic.constant.Status;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.exception.ServiceException;
import sacred.alliance.magic.domain.GoodsBase;
import sacred.alliance.magic.util.Converter;
import sacred.alliance.magic.util.Log4jManager;
import sacred.alliance.magic.util.Wildcard;
import sacred.alliance.magic.util.XlsPojoUtil;
import sacred.alliance.magic.vo.AbstractRole;
import sacred.alliance.magic.vo.MapInstance;
import sacred.alliance.magic.vo.Point;
import sacred.alliance.magic.vo.RebornPointDetail;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.npc.domain.NpcInstance;
import com.game.draco.base.CampType;
import com.game.draco.message.item.DeathNotifySelfItem;
import com.game.draco.message.item.NpcBodyItem;
import com.game.draco.message.item.RoleBodyItem;
import com.game.draco.message.push.C0003_TipNotifyMessage;
import com.game.draco.message.push.C0608_DeathNotifySelfMessage;
import com.game.draco.message.push.C0609_DeathReliveNotifySelfMessage;
import com.game.draco.message.request.C2002_RoleRebornConfirmReqMessage;
import com.game.draco.message.response.C0204_MapUserEntryNoticeRespMessage;
import com.google.common.collect.Maps;

public class RoleRebornAppImpl implements RoleRebornApp {

	private Logger logger = LoggerFactory.getLogger(this.getClass());
	private List<RebornMode> rebornModes;
	private String dieInfo;// 死亡提示信息
	private Map<String, RebornPointDetail> rebornMap = Maps.newHashMap();
	private static final short REBORN_CONFIRM_CMD = new C2002_RoleRebornConfirmReqMessage()
			.getCommandId();

	@Override
	public RebornMode getRebornMode(RebornType rt) {
		for (RebornMode rm : rebornModes) {
			if (rm.getId() == rt.getId()) {
				return rm;
			}
		}
		return null;
	}

	@Override
	public Result skillReborn(AbstractRole dieRole, AbstractRole cureRole,
			int cureHp, int cureMp) {
		Point targetPoint = null;
		if (!cureRole.getMapInstance().getInstanceId()
				.equals(dieRole.getMapInstance().getInstanceId())) {
			targetPoint = dieRole.getCurrentPoint();
		} else {
			targetPoint = cureRole.getCurrentPoint();
		}
		dieRole.getBehavior().changeAttribute(AttributeType.curHP,
				OperatorType.Equal, Math.max(1, cureHp));
		dieRole.getBehavior().changeAttribute(AttributeType.curMP,
				OperatorType.Equal, cureMp);
		dieRole.getBehavior().notifyAttribute();
		this.teleport(dieRole, targetPoint);
		dieRole.getHasSendDeathMsg().compareAndSet(true, false);
		Result result = new Result();
		return result.success();
	}

	/**
	 * 瞬移（同地图死亡角色复活）
	 */
	private void teleport(AbstractRole dieRole, Point targetPoint) {
		// 更新死亡者的位置
		dieRole.setMapX(targetPoint.getX());
		dieRole.setMapY(targetPoint.getY());
		// 通知死亡者位置发生变化
		C0609_DeathReliveNotifySelfMessage notifyMessage = new C0609_DeathReliveNotifySelfMessage();
		notifyMessage.setX((short) targetPoint.getX());
		notifyMessage.setY((short) targetPoint.getY());
		dieRole.getBehavior().sendMessage(notifyMessage);
		// 地图内广播角色进入
		this.notifyMessage(dieRole);
	}

	@Override
	// 1:复活点复活或战斗副本复活(不和客户端交互，系统调用) 2:原地复活 3:原地活力复活 4:原地超强复活 1：
	public Result roleReborn(RoleInstance role, byte type)
			throws ServiceException {
		try {
			Result result = new Result();
			// 自动补充会导致人物血量 > 0
			// 原因：人物在战斗中，自动补充判断人没死，获取任务角色对象，开始加血操作
			// 发现角色对象被战斗时锁住了，则等待战斗释放锁。
			// 而人物在战斗中死亡了，自动补充获得锁后，给人物加血。
			// 导致人物无法复活。 by wang.k
			// if(role.getCurHP() > 0) {
			// result.setInfo("复活失败!");
			// result.setType((byte)0);
			// return result;
			// }
			byte rebornTypeValue = RebornNotifyItemType.get(type)
					.getRebornType();
			RebornType rebornType = RebornType.get(rebornTypeValue);
			if (null == rebornType /* || RebornType.skill == rebornType */) {
				return result.setInfo(Status.Reborn_Illegality.getTips());
			}
			RebornMode mode = this.getRebornMode(rebornType);
			if (null == mode) {
				return result.setInfo(Status.Reborn_Illegality.getTips());
			}
			MapInstance mapInstance = role.getMapInstance();
			if (null == mapInstance) {
				return result.setInfo(Status.Reborn_Illegality.getTips());
			}
			// 判断当前地图是否允许此复活方式
			if (rebornType == RebornType.place
					&& !canRebornPlace(mapInstance.getMap().getMapId(),
							role.getCampId())) {
				return result.setInfo(GameContext.getI18n().getText(
						TextId.Reborn_Map_Can_Not_Place));
			}

			Point targetPoint = mapInstance.getRebornPoint(role, rebornType);
			if (null == targetPoint) {
				return result.setInfo(Status.Reborn_Not_Have_Point.getTips());
			}

			if (rebornType == RebornType.rebornPoint) {
				return reborn(role, mode, targetPoint);
			}

			/*
			 * // VIP次数是否已用完 int vipTotalCount =
			 * GameContext.getRoleVipApp().getPrivCount
			 * (role,PrivType.FreeReborn); if (vipTotalCount < 0) { // VIP复活无限制
			 * return this.unlimitVipReborn(role, mode, targetPoint); } int
			 * useCount = GameContext.getRoleVipApp().getUsedCount(role,
			 * PrivType.FreeReborn) ; if (vipTotalCount > 0 && vipTotalCount >
			 * useCount) { //VIP复活 return this.vipReborn(role, mode,
			 * targetPoint, vipTotalCount, useCount); }
			 */
			// 物品传送
			int goodsId = mode.getGoodsId();
			if (goodsId > 0 && role.getRoleBackpack().existGoods(goodsId)) {
				// 道具传送
				return this.toolsReborn(role, mode, targetPoint, goodsId);
			}

			// 提示扣钱二次确认
			role.getBehavior().sendMessage(
					this.triggerCostMessage(role, mode.getGoldMoney()));
			return result;
		} catch (Exception e) {
			throw new ServiceException(
					"RebornApplicationImpl.roleReborn() exception", e);
		}
	}

	private RebornPointDetail getRebornPointDetail(String mapId, int campId) {
		if (Util.isEmpty(this.rebornMap)) {
			return null;
		}
		String key = mapId + "_" + campId;
		return this.rebornMap.get(key);
	}

	private void notifyMessage(AbstractRole role) {
		try {
			MapInstance instance = role.getMapInstance();
			if (null == instance) {
				return;
			}
			// 地图内广播进入地图信息
			for (RoleInstance other : instance.getRoleList()) {
				if (other.getRoleId().equals(role.getRoleId())) {
					continue;
				}
				C0204_MapUserEntryNoticeRespMessage pushMsg = new C0204_MapUserEntryNoticeRespMessage();
				if (role.getRoleType() == RoleType.PLAYER) {
					RoleBodyItem item = Converter.getRoleBodyItem(
							(RoleInstance) role, other);
					pushMsg.setItem(item);
				} else if (role.getRoleType() == RoleType.NPC) {
					NpcBodyItem item = Converter.getNpcBodyItem(
							(NpcInstance) role, other);
					pushMsg.setItem(item);
				}
				GameContext.getMessageCenter().send("", other.getUserId(),
						pushMsg);
			}
		} catch (Exception e) {
			logger.error("", e);
		}
	}

	@Override
	public List<DeathNotifySelfItem> getRebornOption(RoleInstance role) {
		List<DeathNotifySelfItem> items = new ArrayList<DeathNotifySelfItem>();
		DeathNotifySelfItem item = null;
		for (RebornMode mode : rebornModes) {
			item = new DeathNotifySelfItem();
			byte modeId = (byte) mode.getId();
			if (RebornType.place.getId() == modeId
					&& canRebornPlace(role.getMapId(), role.getCampId())) {
				// 原地复活
				item.setType(RebornNotifyItemType.place.getType());
			} else if (RebornType.rebornPoint.getId() == modeId) {
				Point targetPoint = null;
				RebornPointDetail detail = null;
				MapInstance mapInstance = role.getMapInstance();
				if (null != mapInstance) {
					targetPoint = mapInstance.getRebornPoint(role,
							RebornType.rebornPoint);
					detail = this.getRebornPointDetail(
									mapInstance.getMap().getMapId(), role.getCampId());
				} else {
					targetPoint = role.getCopyBeforePoint();
				}
				Point rolePoint = role.getCurrentPoint();
				boolean sameMap = targetPoint.inSameMap(rolePoint);
				if (sameMap) {
					item.setType(RebornNotifyItemType.rebornSameMap.getType());
				} else {
					item.setType(RebornNotifyItemType.rebornNotSameMap
							.getType());
				}
				if (null != detail) {
					item.setCountDownTime(detail.getCountDownTime());
				}
			}
			items.add(item);
		}
		return items;
	}

	@Override
	public void notifySelfDeath(RoleInstance role, AbstractRole attacker) {
		this.notifySelfDeath(role, attacker, this.getRebornOption(role));
	}

	@Override
	public void notifySelfDeath(RoleInstance role, AbstractRole attacker,
			List<DeathNotifySelfItem> optionList) {
		C0608_DeathNotifySelfMessage selfMsg = new C0608_DeathNotifySelfMessage();
		selfMsg.setItems(optionList);
		if (attacker != null) {
			String attackerName = attacker.getRoleName();
			String info = this.dieInfo.replace(Wildcard.Role_Name,
					role.getRoleName()).replace(Wildcard.Attacker_Name,
					attackerName);
			selfMsg.setInfo(info);
		}
		role.getBehavior().sendMessage(selfMsg);
		/*
		 * if(RoleType.PLAYER == attacker.getRoleType()){
		 * this.sendChatMessage(role, (RoleInstance) attacker); }
		 */
	}

	/**
	 * 死亡时给PK双方发送系统消息
	 * 
	 * @param role
	 * @param attacker
	 */
	/*
	 * private void sendChatMessage(RoleInstance role, RoleInstance attacker) {
	 * StringBuffer roleStrBuffer = new StringBuffer(); StringBuffer
	 * attackerStrBuffer = new StringBuffer();
	 * roleStrBuffer.append("[\\C]FFe60011[C]" + attacker.getRoleName());
	 * roleStrBuffer.append("[\\C]FFFFFFFF[C]  杀死了你");
	 * attackerStrBuffer.append("你杀死了[\\C]FFe60011[C]" + role.getRoleName() +
	 * "[\\C]FFFFFFFF[C]"); try{
	 * GameContext.getChatApp().sendSysMessage(ChatSysName.System,
	 * ChannelType.Private, roleStrBuffer.toString(), null, role);
	 * GameContext.getChatApp().sendSysMessage(ChatSysName.System,
	 * ChannelType.Private, attackerStrBuffer.toString(), null, attacker);
	 * }catch(Exception e) { logger.error("", e); } }
	 */

	@Override
	public void setArgs(Object arg0) {

	}

	@Override
	public void start() {
		this.loadRebornPointDetail();
		this.loadRebornMode();
		this.loadDieInfo();
	}
	

	@Override
	public void stop() {

	}
	
	private void loadRebornPointDetail(){
		String fileName = "";
		String sheetName = "";
		try {
			fileName = XlsSheetNameType.role_reborn_point.getXlsName();
			sheetName = XlsSheetNameType.role_reborn_point.getSheetName();
			String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
			this.rebornMap = XlsPojoUtil.sheetToGenericMap(sourceFile, sheetName, RebornPointDetail.class);
		} catch (Exception e) {
			Log4jManager.CHECK.error("loadExel error : sourceFile = "+fileName +" sheetName ="+sheetName , e);
			Log4jManager.checkFail();
		}
	}
	
	/** 加载死亡复活方式 */
	private void loadRebornMode() {
		String fileName = XlsSheetNameType.role_reborn_mode.getXlsName();
		String sheetName = XlsSheetNameType.role_reborn_mode.getSheetName();
		String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
		rebornModes = XlsPojoUtil.sheetToList(sourceFile, sheetName,
				RebornMode.class);
		String info = "the reborn mode not config,file:" + fileName + " sheet:"
				+ sheetName;
		if (Util.isEmpty(rebornModes)) {
			Log4jManager.CHECK.error(info);
			Log4jManager.checkFail();
		}
		// 初始化
		for (RebornMode mode : rebornModes) {
			mode.init(info);
		}
		// 验证死亡复活点
		Collection<sacred.alliance.magic.app.map.Map> mapInfos = GameContext
				.getMapApp().getAllMap();
		for (Iterator<sacred.alliance.magic.app.map.Map> it = mapInfos
				.iterator(); it.hasNext();) {
			sacred.alliance.magic.app.map.Map map = it.next();
			for (CampType ct : CampType.values()) {
				// 非真实的阵营，不做验证
				if (!ct.isRealCamp()) {
					continue;
				}
				RebornPointDetail detail = getRebornPointDetail(map.getMapId(),
						ct.getType());
				if (null == detail) {
					Log4jManager.checkFail();
					Log4jManager.CHECK
							.error("RebornPoint config error, mapId:("
									+ map.getMapId() + ") campType:"
									+ ct.getType() + " not config ");
					continue;
				}
				sacred.alliance.magic.app.map.Map rebornMap = GameContext
						.getMapApp().getMap(detail.getRebornMapId());
				if (null == rebornMap) {
					Log4jManager.checkFail();
					Log4jManager.CHECK
							.error("RebornPoint config error, mapId:("
									+ map.getMapId() + ") campType:"
									+ ct.getType() + " map not exist");
				}
			}
		}
	}

	private void loadDieInfo() {
		try {
			String fileName = XlsSheetNameType.role_reborn_die_info
					.getXlsName();
			String sheetName = XlsSheetNameType.role_reborn_die_info
					.getSheetName();
			String sourceFile = GameContext.getPathConfig().getXlsPath()
					+ fileName;
			List<String> infos = XlsPojoUtil.sheetToStringList(sourceFile,
					sheetName);
			if (Util.isEmpty(infos)) {
				Log4jManager.checkFail();
				Log4jManager.CHECK.error("roleReborn die_info config error");
				return;
			}
			this.dieInfo = infos.get(0);
			if (Util.isEmpty(this.dieInfo)) {
				Log4jManager.checkFail();
				Log4jManager.CHECK.error("roleReborn die_info config error");
			}
		} catch (RuntimeException e) {
			Log4jManager.checkFail();
			Log4jManager.CHECK.error("roleReborn die_info config error");
		}
	}

	private Message triggerCostMessage(RoleInstance role, int goldMoney) {
		String goldParam = String.valueOf(AttributeType.goldMoney.getType());
		return QuickCostHelper.getMessage(role, REBORN_CONFIRM_CMD, goldParam,
				REBORN_CONFIRM_CMD, "",
				GameContext.getI18n().getText(TextId.REBORN_FEE), goldMoney,
				ParasConstant.Gold_Bind_Nonsupport_Value);
	}

	@Override
	public Result rebornConfirm(RoleInstance role, String confirmInfo) {
		Result result = new Result();
		if (Util.isEmpty(confirmInfo)) {
			return result.setInfo(GameContext.getI18n().getText(
					TextId.ERROR_INPUT));
		}

		RebornMode mode = this.getRebornMode(RebornType.place);
		if (null == mode) {
			return result.setInfo(Status.Reborn_Illegality.getTips());
		}
		MapInstance mapInstance = role.getMapInstance();
		if (null == mapInstance) {
			return result.setInfo(Status.Reborn_Illegality.getTips());
		}
		Point targetPoint = mapInstance.getRebornPoint(role, RebornType.place);
		if (null == targetPoint) {
			return result.setInfo(Status.Reborn_Not_Have_Point.getTips());
		}

		byte attrType = Byte.parseByte(confirmInfo);
		AttributeType moneyType = null;
		int money = 0;
		if (attrType == AttributeType.goldMoney.getType()) {
			moneyType = AttributeType.goldMoney;
			money = mode.getGoldMoney();
		}
		if (null == moneyType) {
			return new Result().setInfo(GameContext.getI18n().getText(
					TextId.ERROR_INPUT));
		}

		String moneyInfo = this.checkMoney(role, moneyType, money);
		if (!Util.isEmpty(moneyInfo)) {
			return result.setInfo(moneyInfo);
		}
		// 可以复活
		return this.moneyReborn(role, moneyType, money, targetPoint, mode);
	}

	private String checkMoney(RoleInstance role, AttributeType moneyType,
			int needMoney) {
		int roleGold = role.getGoldMoney();
		int roleBinding = role.getBindingGoldMoney();
		if (AttributeType.goldMoney == moneyType) {
			// 金条
			if (needMoney < 0) {
				return Status.Sys_Error.getTips();
			}
			if (needMoney > roleGold) {
				return GameContext.getI18n().messageFormat(
						TextId.REBORN_FEE_NOT_ENOUGH, moneyType.getName());
			}
		}
		// 绑金
		if (AttributeType.bindingGoldMoney == moneyType) {
			if (needMoney < 0) {
				return Status.Sys_Error.getTips();
			}
			if (needMoney > roleBinding) {
				return GameContext.getI18n().messageFormat(
						TextId.REBORN_FEE_NOT_ENOUGH, moneyType.getName());
			}
		}
		return null;
	}

	/**
	 * VIP无限制复活
	 * 
	 * @param role
	 * @param mode
	 * @param targetPoint
	 * @return
	 */
	/*
	 * private Result unlimitVipReborn(RoleInstance role, RebornMode mode, Point
	 * targetPoint){ Result result = new Result();
	 * 
	 * result = this.reborn(role, mode, targetPoint); if(!result.isSuccess()) {
	 * return result.setInfo(GameContext.getI18n().getText(TextId.REBORN_FAIL))
	 * ; } role.getRoleCount().incrDayFreeReborn(); this.sendNotifyMessage(role,
	 * GameContext.getI18n().messageFormat( TextId.REBORN_VIP_UNLIMITED,
	 * this.getVipName(role.getVipLevel()))); return result.success() ; }
	 */

	/**
	 * 
	 * @param role
	 * @param text
	 */
	private void sendNotifyMessage(RoleInstance role, String text) {
		if (Util.isEmpty(text)) {
			return;
		}
		// 发送提示信息
		C0003_TipNotifyMessage notifyMsg = new C0003_TipNotifyMessage();
		notifyMsg.setMsgContext(text);
		role.getBehavior().sendMessage(notifyMsg);
	}

	/**
	 * VIP复活
	 * 
	 * @param role
	 * @param mode
	 * @param point
	 * @param vipTotalCount
	 * @param useCount
	 * @return
	 */
	/*
	 * private Result vipReborn(RoleInstance role, RebornMode mode, Point
	 * point,int vipTotalCount,int useCount){ Result result = new Result();
	 * result = this.reborn(role, mode, point); if(!result.isSuccess()) { return
	 * result.setInfo(GameContext.getI18n().getText(TextId.REBORN_FAIL)) ; }
	 * role.getRoleCount().incrDayFreeReborn(); this.sendNotifyMessage(role,
	 * MessageFormat.format(
	 * GameContext.getI18n().getText(TextId.REBORN_VIP_LIMITED),
	 * this.getVipName(role.getVipLevel()), vipTotalCount - useCount -1 ));
	 * return result.success() ; }
	 */

	/**
	 * 道具复活
	 * 
	 * @param role
	 * @param mode
	 * @param point
	 * @param goodsId
	 * @return
	 */
	private Result toolsReborn(RoleInstance role, RebornMode mode, Point point,
			int goodsId) {
		Result result = new Result();
		result = this.reborn(role, mode, point);
		if (!result.isSuccess()) {
			return result.setInfo(GameContext.getI18n().getText(
					TextId.REBORN_FAIL));
		}
		GameContext.getUserGoodsApp().deleteForBag(role, goodsId, 1,
				OutputConsumeType.rebron_scroll);
		GoodsBase tools = GameContext.getGoodsApp().getGoodsBase(goodsId);
		String text = GameContext.getI18n()
				.getText(TextId.REBORN_DEFAULT_TOOLS);
		if (null != tools) {
			text = GameContext.getI18n().messageFormat(
					TextId.REBORN_DEFAULT_TOOLS, tools.getName());
		}
		this.sendNotifyMessage(role, text);
		return result.success();
	}

	/**
	 * 复活地图是否可以原地复活
	 * 
	 * @param mapId
	 * @param campId
	 * @return
	 */
	private boolean canRebornPlace(String mapId, int campId) {
		RebornPointDetail rebornPointDetail = this.getRebornPointDetail(mapId, campId);
		if (null == rebornPointDetail) {
			return false;
		}
		return rebornPointDetail.isRebornPlace();
	}

	/**
	 * 复活
	 * 
	 * @param role
	 * @param mode
	 * @param targetPoint
	 * @return
	 */
	private Result reborn(RoleInstance role, RebornMode mode, Point targetPoint) {
		Result result = new Result();
		try {
			int rebornHp = (int) (role.getMaxHP() / (float) 100 * mode
					.getHpRate());
			int rebornMp = (int) (role.getMaxMP() / (float) 100 * mode
					.getMpRate());
			role.setCurHP(Math.max(1, rebornHp));
			role.setCurMP(Math.max(1, rebornMp));

			Point rolePoint = role.getCurrentPoint();
			boolean changeMap = !targetPoint.inSameMap(rolePoint);
			if (changeMap) {
				// 需要跳转地图
				ChangeMapResult changeMapResult = GameContext.getUserMapApp()
						.changeMap(role, targetPoint);
				if (!changeMapResult.isSuccess()) {
					role.setCurHP(0);
					return result.setInfo(changeMapResult.getDesc());
				}
			} else {
				this.teleport(role, targetPoint);
			}

			// 添加buff
			for (Short buffId : mode.getBuffIds()) {
				GameContext.getUserBuffApp().addBuffStat(role, role, buffId, 0,
						1);
			}
			role.getBehavior().notifyAttribute();

			// 同步队伍消息
			if (role.hasTeam()) {
				role.getTeam().syschDataNotify();
			}

			// 返回成功
			role.getHasSendDeathMsg().compareAndSet(true, false);

			short protectBuffId = role.getProtectBuffId();
			if (protectBuffId > 0 && !role.hasBuff(protectBuffId)) {
				GameContext.getUserBuffApp().addBuffStat(role, role,
						protectBuffId, 0, 1);
				role.setProtectBuffId((short) 0);
			}
		} catch (Exception ex) {
			logger.error("", ex);
			return result.setInfo(GameContext.getI18n().getText(
					TextId.SYSTEM_ERROR));
		}
		return result.success();
	}

	private Result moneyReborn(RoleInstance role, AttributeType moneyType,
			int cost, Point point, RebornMode mode) {
		Result result = new Result();
		result = this.reborn(role, mode, point);
		if (!result.isSuccess()) {
			return result.setInfo(GameContext.getI18n().getText(
					TextId.REBORN_FAIL));
		}
		// 扣除消耗
		GameContext.getUserAttributeApp().changeRoleMoney(role, moneyType,
				OperatorType.Decrease, cost, OutputConsumeType.reborn);
		role.getBehavior().notifyAttribute();
		// 发送提示信息
		this.sendNotifyMessage(role,
				GameContext.getI18n().getText(TextId.REBORN_SUCCESS));
		return result.success();
	}

	@Override
	public RebornPointDetail getRebornPointDetail(String mapId,
			AbstractRole role) {
		if(null == role){
			return null ;
		}
		return this.getRebornPointDetail(mapId, role.getCampId());
	}
}