package com.game.draco.app.title;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sacred.alliance.magic.app.attri.AttriBuffer;
import sacred.alliance.magic.app.attri.AttriItem;
import sacred.alliance.magic.base.GoodsType;
import sacred.alliance.magic.constant.Status;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.domain.GoodsBase;
import sacred.alliance.magic.domain.GoodsTitle;
import sacred.alliance.magic.util.Converter;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.util.Wildcard;
import sacred.alliance.magic.vo.AbstractRole;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.chat.ChannelType;
import com.game.draco.app.chat.ChatSysName;
import com.game.draco.app.npc.domain.NpcInstance;
import com.game.draco.app.title.domain.TitleRecord;
import com.game.draco.message.internal.C0070_TitleTimeoutExecInternalMessage;
import com.game.draco.message.item.TitleWearingItem;
import com.game.draco.message.push.C0003_TipNotifyMessage;
import com.game.draco.message.push.C2345_TitleAddOrUpBroadcastNotifyMessage;
import com.game.draco.message.push.C2346_TitleCancelBroadcastNotifyMessage;
import com.game.draco.message.response.C2342_TitleActivateRespMessage;

public class TitleAppImpl implements TitleApp {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	/**
	 * 称号分类列表
	 */
	private Map<Integer, TitleCategory> titleCategoryMap = new LinkedHashMap<Integer, TitleCategory>();

	private TitleRecord getSameCategoryCurrTitle(RoleInstance role,
			TitleRecord doingTitle) {
		List<TitleRecord> currTitleList = role.getCurrTitleList();
		if (Util.isEmpty(currTitleList)) {
			return null;
		}
		GoodsTitle doingTemplate = GameContext.getGoodsApp().getGoodsTemplate(
				GoodsTitle.class, doingTitle.getTitleId());
		if (null == doingTemplate) {
			return null;
		}
		for (TitleRecord title : currTitleList) {
			GoodsTitle titleTemplate = GameContext.getGoodsApp()
					.getGoodsTemplate(GoodsTitle.class, title.getTitleId());
			if (null == titleTemplate) {
				continue;
			}
			if (titleTemplate.getGroupId() == doingTemplate.getGroupId()) {
				return title;
			}
		}
		return null;
	}

	private int getCurrTitleNum(RoleInstance role) {
		List<TitleRecord> currTitleList = role.getCurrTitleList();
		if (null == currTitleList) {
			return 0;
		}
		return currTitleList.size();
	}

	@Override
	public Status activateTitle(RoleInstance role, int titleId) {
		TitleRecord title = role.getTitleRecord(titleId);
		if (null == title) {
			return Status.Title_Goods_Null;
		}
		if (title.isActivate()) {
			return Status.Title_Is_Activated;
		}
		if (title.isTimeout()) {
			return Status.Title_Is_Timeout;
		}
		// 获得同类型的已装配的称号
		TitleRecord sameCategoryTitle = this.getSameCategoryCurrTitle(role,
				title);
		int titleNum = this.getCurrTitleNum(role);
		if (null == sameCategoryTitle && titleNum >= MAX_ACTIVATE_TITLE_NUM) {
			// 当前激活的称号已经满了
			return Status.Title_Current_IsFull;
		}
		// 替换的称号撤下
		boolean recalc = role.removeCurrentTitle(sameCategoryTitle);
		// 激活称号
		role.addCurrentTitle(title);
		// 计算2称号的属性差异
		this.affect(role, title, recalc ? sameCategoryTitle : null);
		this.notifyOnTitleSuccess(role, title, sameCategoryTitle);
		return Status.SUCCESS;
	}

	private void notifyOnTitleSuccess(RoleInstance role, TitleRecord onTitle,
			TitleRecord offTitle) {
		try {
			GoodsTitle goodsTitle = GameContext.getGoodsApp().getGoodsTemplate(
					GoodsTitle.class, onTitle.getTitleId());
			if (null == goodsTitle) {
				return;
			}
			int replaceTitleId = 0;
			if (null != offTitle) {
				replaceTitleId = offTitle.getTitleId();
			}
			C2342_TitleActivateRespMessage selfMsg = new C2342_TitleActivateRespMessage();
			selfMsg.setReplaceTitleId(replaceTitleId);
			TitleWearingItem item = Converter.getTitleWearingItem(goodsTitle);
			selfMsg.setItem(item);
			selfMsg.setType(Status.SUCCESS.getInnerCode());
			selfMsg.setInfo(GameContext.getI18n().messageFormat(TextId.Title_Activate_Success_Tip, goodsTitle.getName()));

			C2345_TitleAddOrUpBroadcastNotifyMessage otherMsg = new C2345_TitleAddOrUpBroadcastNotifyMessage();
			otherMsg.setRoleId(role.getIntRoleId());
			otherMsg.setReplaceTitleId(replaceTitleId);
			otherMsg.setItem(item);

			role.getBehavior().sendMessage(selfMsg);
			role.getMapInstance().broadcastMap(role, otherMsg);
		} catch (Exception ex) {
			logger.error("", ex);
		}
	}

	private void calctTitleAttr(RoleInstance role, TitleRecord onTitle,
			TitleRecord offTitle) {
		try {
			AttriBuffer attriBuffer = AttriBuffer.createAttriBuffer();
			if (null != offTitle) {
				GoodsTitle offTemplate = GameContext.getGoodsApp()
						.getGoodsTemplate(GoodsTitle.class,
								offTitle.getTitleId());
				attriBuffer.append(offTemplate.getAttriItemList());
				attriBuffer.reverse();
			}
			if (null != onTitle) {
				GoodsTitle onTemplate = GameContext.getGoodsApp()
						.getGoodsTemplate(GoodsTitle.class,
								onTitle.getTitleId());
				attriBuffer.append(onTemplate.getAttriItemList());
			}
			if (attriBuffer.isEmpty()) {
				return;
			}
			// 重新计算属性(去掉/增加当前称号的属性影响)
			GameContext.getUserAttributeApp()
					.changeAttribute(role, attriBuffer);
			role.getBehavior().notifyAttribute();
		} catch (Exception ex) {
			logger.error("", ex);
		}
	}

	@Override
	public AttriBuffer getAttriBuffer(AbstractRole player) {
		try {
			RoleInstance role = (RoleInstance) player;
			List<TitleRecord> currTitleList = role.getCurrTitleList();
			if (Util.isEmpty(currTitleList)) {
				return null;
			}
			List<AttriItem> itemList = new ArrayList<AttriItem>();
			for (TitleRecord currTitle : currTitleList) {
				GoodsTitle goodsTitle = GameContext.getGoodsApp()
						.getGoodsTemplate(GoodsTitle.class,
								currTitle.getTitleId());
				if (null == goodsTitle) {
					continue;
				}
				itemList.addAll(goodsTitle.getAttriItemList());
			}
			AttriBuffer attriBuffer = AttriBuffer.createAttriBuffer();
			attriBuffer.append(itemList);
			return attriBuffer;
		} catch (Exception e) {
			logger.error("", e);
		}
		return null;
	}

	@Override
	public Status addTitle(RoleInstance role, GoodsTitle goodsTitle,
			boolean isActivate) {
		if (!goodsTitle.isAllowLevel(role.getLevel())) {
			return Status.Title_Level_Err;
		}
		TitleRecord tr = this.getRoleTitle(role, goodsTitle.getId());
		// 不存在则直接添加
		if (null == tr) {
			tr = new TitleRecord();
			tr.setTitleId(goodsTitle.getId());
			tr.setDueTime(new Date());
			tr.addDueTimeMinute(goodsTitle.getDeadline());
			tr.setRoleId(role.getRoleId());
			tr.setUseDate(new Date());
			role.addTitle(tr);
			// 实时入库
			tr.insert();

			this.packTipNotifyMessage(role, GameContext.getI18n().messageFormat(
					TextId.TITLE_GET_TIPS, goodsTitle.getName()));

			this.addTitleBroadcast(role, goodsTitle);
			if (isActivate) {
				this.activateTitle(role, tr.getTitleId());
			}
			return Status.SUCCESS;
		}
		if (goodsTitle.isPermanent()) {
			return Status.Title_Exist;
		}
		tr.addDueTimeMinute(goodsTitle.getDeadline());
		// 实时入库
		tr.update();
		if (isActivate) {
			this.activateTitle(role, tr.getTitleId());
		}
		this.packTitleActivateOrRenewal(role, goodsTitle);
		return Status.SUCCESS;
	}

	// 称号激活或续费的提示信息
	private void packTitleActivateOrRenewal(RoleInstance role, GoodsTitle gt) {
		if (gt.getDeadline() <= 0) {
			return;
		}
		packTipNotifyMessage(role, GameContext.getI18n().messageFormat(
				TextId.TITLE_ADD_TIME_TIPS, gt.getName(), gt.getDeadline()));
	}

	// 获得称号广播
	private void addTitleBroadcast(RoleInstance role, GoodsTitle gt) {
		try {
			if (!gt.isAllowBroadcast()) {
				return;
			}
			String message = gt.getBroadcastInfo();
			if (Util.isEmpty(message)) {
				return;
			}
			message = message.replace(Wildcard.Title_Role_Name, role
					.getRoleName());
			GameContext.getChatApp().sendSysMessage(ChatSysName.System,
					ChannelType.Publicize_Personal, message, null, null);
		} catch (Exception e) {
			this.logger.error("：", e);
		}
	}

	private void killTitleBroadcast(AbstractRole attacker, RoleInstance victim,
			TitleRecord currTitle) {
		try {
			if (null == currTitle) {
				return;
			}
			GoodsTitle gt = GameContext.getGoodsApp().getGoodsTemplate(
					GoodsTitle.class, currTitle.getTitleId());
			if (null == gt) {
				return;
			}
			if (!gt.isKillAllowBroadcast()) {
				return;
			}
			String attackerName = "";
			if (attacker instanceof RoleInstance
					&& gt.isAttackerRoleBroadcast()) {
				RoleInstance role = (RoleInstance) attacker;
				attackerName = role.getRoleName();
			} else if (attacker instanceof NpcInstance
					&& gt.isAttackerNpcBroadcast()) {
				NpcInstance npc = (NpcInstance) attacker;
				attackerName = npc.getNpcname();
			} else {
				return;
			}
			String message = gt.getKillBroadcastInfo();
			if (Util.isEmpty(message)) {
				return;
			}
			message = message.replace(Wildcard.Title_Attacker_Name,
					attackerName);
			message = message.replace(Wildcard.Title_Victim_Name, victim
					.getRoleName());
			GameContext.getChatApp().sendSysMessage(ChatSysName.System,
					ChannelType.Publicize_Personal, message, null, null);
		} catch (Exception e) {
			this.logger.error("：", e);
		}
	}

	@Override
	public void killTitleBroadcast(AbstractRole attacker, RoleInstance victim) {
		try {
			List<TitleRecord> titleList = victim.getCurrTitleList();
			if (null == titleList) {
				return;
			}
			for (TitleRecord record : titleList) {
				GoodsTitle gt = GameContext.getGoodsApp().getGoodsTemplate(
						GoodsTitle.class, record.getTitleId());
				if (null == gt) {
					continue;
				}
				this.killTitleBroadcast(attacker, victim, record);
			}
		} catch (Exception e) {
			this.logger.error("：", e);
		}
	}

	private void packTipNotifyMessage(RoleInstance role, String info) {
		try {
			C0003_TipNotifyMessage notify = new C0003_TipNotifyMessage();
			notify.setMsgContext(info);
			role.getBehavior().sendMessage(notify);
		} catch (Exception e) {
			logger.error("", e);
		}
	}

	@Override
	public Status cancelTitle(RoleInstance role, int titleId) {
		GoodsTitle goodsTitle = GameContext.getGoodsApp().getGoodsTemplate(
				GoodsTitle.class, titleId);
		if (null == goodsTitle) {
			return Status.Title_Goods_Null;
		}
		TitleRecord title = role.getTitleRecord(titleId);
		if (null == title) {
			return Status.Title_No_Activated;
		}
		if(!role.removeCurrentTitle(title)){
			return Status.SUCCESS;
		}
		this.affect(role, null, title);
		// 广播地图
		try {
			C2346_TitleCancelBroadcastNotifyMessage message = new C2346_TitleCancelBroadcastNotifyMessage();
			message.setRoleId(role.getIntRoleId());
			message.setTitleId(titleId);
			role.getMapInstance().broadcastMap(role, message);
		} catch (Exception e) {
			logger.error("", e);
		}
		return Status.SUCCESS;
	}

	@Override
	public int onLogin(RoleInstance role, Object context) {
		//玩家登录时初始化称号信息
		try {
			List<TitleRecord> trList = GameContext.getBaseDAO().selectList(
					TitleRecord.class, TitleRecord.ROLE_ID, role.getRoleId());
			if (null == trList) {
				return 1;
			}
			for (TitleRecord tr : trList) {
				role.addTitle(tr);
				if (!tr.isActivate()) {
					continue;
				}
				if (tr.isTimeout()) {
					// 取消激活状态
					tr.cancel();
					// 存入数据库
					tr.update();
					continue;
				}
				// 放入已激活列表
				role.addCurrentTitle(tr);
			}
		}catch(Exception ex){
			logger.error("initTitle error",ex);
			return 0;
		}
		return 1;
	}

	@Override
	public boolean isExistTitle(RoleInstance role, int titleId) {
		return null != this.getRoleTitle(role, titleId);
	}

	@Override
	public TitleRecord getRoleTitle(RoleInstance role, int titleId) {
		Map<Integer, TitleRecord> trMap = role.getTitleMap();
		if (null == trMap || 0 == trMap.size()) {
			return null;
		}
		return trMap.get(titleId);
	}

	@Override
	public boolean isExistEffectiveTitle(RoleInstance role, int titleId) {
		TitleRecord tr = this.getRoleTitle(role, titleId);
		return (null != tr && !tr.isTimeout());
	}

	// 续费条件
	private Status renewalConditions(RoleInstance role, TitleRecord tr) {
		try {
			if (null == tr) {
				return Status.Title_Goods_Null;
			}
			GoodsTitle gt = GameContext.getGoodsApp().getGoodsTemplate(
					GoodsTitle.class, tr.getTitleId());
			if (null == gt) {
				return Status.Title_Goods_Null;
			}
			if (gt.isPermanent()) {
				return Status.Title_Permanent_No_Pay;
			}
		} catch (Exception e) {
			logger.error("", e);
			return Status.FAILURE;
		}
		return Status.SUCCESS;
	}
	
	@Override
	public void timeoutExec(RoleInstance role, List<TitleRecord> timeoutList) {
		if (Util.isEmpty(timeoutList)) {
			return;
		}
		if (!GameContext.getOnlineCenter().isOnlineByRoleId(role.getRoleId())) {
			return;
		}
		for (TitleRecord title : timeoutList) {
			try {
				if (!title.isTimeout()) {
					continue;
				}
				if(!role.removeCurrentTitle(title)){
					continue;
				}
				this.affect(role, null, title);
				// push过期协议(包括自己)
				C2346_TitleCancelBroadcastNotifyMessage message = new C2346_TitleCancelBroadcastNotifyMessage();
				message.setRoleId(role.getIntRoleId());
				message.setTitleId(title.getTitleId());
				role.getBehavior().sendMessage(message);
				role.getMapInstance().broadcastMap(role, message);
			} catch (Exception ex) {
				logger.error("", ex);
			}
		}
	}

	@Override
	public void timeout(RoleInstance role) {
		List<TitleRecord> currTitleList = role.getCurrTitleList();
		if (Util.isEmpty(currTitleList)) {
			return;
		}
		List<TitleRecord> timeoutList = null;
		for (TitleRecord title : currTitleList) {
			try {
				if (!title.isTimeout()) {
					continue;
				}
				if (null == timeoutList) {
					timeoutList = new ArrayList<TitleRecord>();
				}
				timeoutList.add(title);
			} catch (Exception ex) {
				logger.error("", ex);
			}
		}
		if (null == timeoutList) {
			return;
		}
		C0070_TitleTimeoutExecInternalMessage reqMsg = new C0070_TitleTimeoutExecInternalMessage();
		reqMsg.setRole(role);
		reqMsg.setTimeoutList(timeoutList);
		role.getBehavior().addCumulateEvent(reqMsg);
	}

	// 影响到的内容
	private void affect(RoleInstance role, TitleRecord on, TitleRecord off) {
		// 去除此称号属性影响
		this.calctTitleAttr(role, on, off);
		// 更新数据库
		if (null != on) {
			on.update();
		}
		if (null != off) {
			off.update();
		}
	}

	public void initTitleCategory(List<? extends GoodsBase> goodsTitleList) {
		if (Util.isEmpty(goodsTitleList)) {
			this.titleCategoryMap.clear();
			return;
		}
		this.titleCategoryMap.clear();
		for (GoodsBase gb : goodsTitleList) {
			if (null == gb
					|| gb.getGoodsType() != GoodsType.GoodsTitle.getType()) {
				continue;
			}
			GoodsTitle title = (GoodsTitle) gb;
			int categoryId = gb.getSecondType();
			String categoryName = title.getSecondTypeName();
			TitleCategory category = this.titleCategoryMap.get(categoryId);
			if (null == category) {
				category = new TitleCategory();
				category.setCategoryId(categoryId);
				category.setCategoryName(categoryName);
				titleCategoryMap.put(categoryId, category);
			}
			category.getTitleList().add(title);
		}
	}

	public Map<Integer, TitleCategory> getTitleCategoryMap() {
		return this.titleCategoryMap;
	}

	@Override
	public TitleStatus getTitleStatus(RoleInstance role, int titleId) {
		TitleRecord record = this.getRoleTitle(role, titleId);
		if (null == record) {
			return TitleStatus.Lack;
		}
		return record.isActivate() ? TitleStatus.Wear : TitleStatus.Have;
	}

	@Override
	public int onLogout(RoleInstance role, Object context) {
		try {
			// 将已经过期n天的删除?
			Collection<TitleRecord> titleList = role.getTitleMap().values();
			if (Util.isEmpty(titleList)) {
				return 1;
			}
			for (TitleRecord record : titleList) {
				if (!record.isTimeout()) {
					continue;
				}
				GoodsTitle goodsTitle = GameContext
						.getGoodsApp()
						.getGoodsTemplate(GoodsTitle.class, record.getTitleId());
				if (null == goodsTitle || 0 == goodsTitle.getExpireType()) {
					continue;
				}
				// 删除
				record.delete();
			}
		}catch(Exception ex){
			logger.error("roletitle offline error",ex);
			return 0;
		}
		return 1;
	}
	
	@Override
	public int onCleanup(String roleId, Object context) {
		// TODO Auto-generated method stub
		return 0;
	}

}
