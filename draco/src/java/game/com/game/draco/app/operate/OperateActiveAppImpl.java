package com.game.draco.app.operate;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sacred.alliance.magic.base.OutputConsumeType;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.hint.vo.HintType;
import com.game.draco.app.operate.vo.OperateActiveType;
import com.game.draco.message.push.C2453_OperateActiveHintRuleRespMessage;
import com.game.draco.message.response.C0002_ErrorRespMessage;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

public class OperateActiveAppImpl implements OperateActiveApp {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private Map<Integer, OperateActive> operateActiveMap = Maps.newLinkedHashMap();

	@Override
	public List<OperateActive> getAllOperateActive(RoleInstance role) {
		List<OperateActive> operateActiveList = Lists.newArrayList();
		try {
			if (Util.isEmpty(this.operateActiveMap)) {
				return null;
			}
			for (OperateActive active : this.operateActiveMap.values()) {
				// 活动是否显示到运营活动列表
				if (!active.isShow(role)) {
					continue;
				}
				operateActiveList.add(active);
			}
		} catch (Exception e) {
			this.logger.error("OperateActiveAppImpl.getAllOperateActive error: ", e);
		}
		return operateActiveList;
	}

	@Override
	public Message getOperateActiveDetail(RoleInstance role, int activeId) {
		OperateActive active = this.getOperateActive(activeId);
		if (null == active) {
			C0002_ErrorRespMessage message = new C0002_ErrorRespMessage();
			message.setInfo(GameContext.getI18n().getText(TextId.ERROR_INPUT));
			return message;
		}
		return active.getOperateActiveDetail(role);
	}

	@Override
	public void setArgs(Object arg0) {
	}

	@Override
	public void start() {
	}

	@Override
	public void stop() {
	}

	@Override
	public synchronized Result registerOperateActive(Collection<OperateActive> activeList, OperateActiveType operateActiveType) {
		Result result = new Result();
		if (Util.isEmpty(activeList)) {
			return result.success();
		}
		try {
			result = this.initOperateActiveMap(activeList, operateActiveType);
		} catch (Exception ex) {
			logger.error("OperateActiveAppImpl.registerOperateActive error!" + operateActiveType.getName());
			return result;
		}
		return result;
	}

	private Result initOperateActiveMap(Collection<OperateActive> activeList, OperateActiveType operateActiveType) {
		Result result = new Result();
		boolean repeat = false;
		StringBuffer buffer = new StringBuffer();
		Map<Integer, OperateActive> map = Maps.newHashMap();
		for (OperateActive active : activeList) {
			if (null == active) {
				continue;
			}
			// 验证集合中是否有重复的活动ID
			if (map.containsKey(active.getOperateActiveId())) {
				repeat = true;
				buffer.append(
						"OperateActive have same activeId : " + active.getOperateActiveId() + "activeName" + active.getOperateActiveName() + ";The Same ActiveName"
								+ map.get(active.getOperateActiveId()).getOperateActiveName()).append("\n");
				continue;
			}
			map.put(active.getOperateActiveId(), active);
		}
		// 如果活动列表存在重复ID
		if (repeat) {
			result.setInfo(buffer.toString());
			return result;
		}
		// 如果当前活动列表为空，直接赋值
		if (Util.isEmpty(this.operateActiveMap)) {
			this.operateActiveMap = this.sortOperateActive(map);
			return result.success();
		}
		// 将当前活动列表中不是该类的活动放入Map
		for (OperateActive active : this.operateActiveMap.values()) {
			if (null == active) {
				continue;
			}
			if (active.getOperateActiveType() == operateActiveType) {
				continue;
			}
			if (map.containsKey(active.getOperateActiveId())) {
				repeat = true;
				buffer.append("OperateActive have same ID : " + active.getOperateActiveId()).append("\n");
				continue;
			}
			map.put(active.getOperateActiveId(), active);
		}
		// 如果活动列表存在重复ID
		if (repeat) {
			result.setInfo(buffer.toString());
			return result;
		}
		// 活动排序
		this.operateActiveMap = this.sortOperateActive(map);
		return result.success();
	}

	/**
	 * 活动列表排序
	 * @param map
	 * @return
	 */
	private Map<Integer, OperateActive> sortOperateActive(Map<Integer, OperateActive> map) {
		List<OperateActive> activeList = Lists.newArrayList();
		activeList.addAll(map.values());
		Collections.sort(activeList, new Comparator<OperateActive>() {
			@Override
			public int compare(OperateActive active1, OperateActive active2) {
				if (active1.getOperateActiveType().getType() > active2.getOperateActiveType().getType()) {
					return 1;
				}
				if (active1.getOperateActiveType().getType() < active2.getOperateActiveType().getType()) {
					return -1;
				}
				if (active1.getOperateActiveId() > active2.getOperateActiveId()) {
					return 1;
				}
				if (active1.getOperateActiveId() < active2.getOperateActiveId()) {
					return -1;
				}
				return 0;
			}
		});
		// 将排序后的活动放到map中
		Map<Integer, OperateActive> activeMap = Maps.newLinkedHashMap();
		for (OperateActive active : activeList) {
			if (null == active) {
				continue;
			}
			activeMap.put(active.getOperateActiveId(), active);
		}
		return activeMap;
	}

	@Override
	public void onConsume(RoleInstance role, int rmbMoneyValue, OutputConsumeType outputConsumeType) {
		if (Util.isEmpty(this.operateActiveMap)) {
			return;
		}
		for (OperateActive active : this.operateActiveMap.values()) {
			if (null == active) {
				continue;
			}
			// 如果活动未开启
			if (!active.isOpen(role)) {
				continue;
			}
			active.onConsume(role, rmbMoneyValue, outputConsumeType);
		}
	}

	@Override
	public void onPay(RoleInstance role, int rmbMoneyValue, OutputConsumeType outputConsumeType) {
		if (Util.isEmpty(this.operateActiveMap)) {
			return;
		}
		for (OperateActive active : this.operateActiveMap.values()) {
			if (null == active) {
				continue;
			}
			// 如果活动未开启
			if (!active.isOpen(role)) {
				continue;
			}
			active.onPay(role, rmbMoneyValue, outputConsumeType);
		}
	}

	@Override
	public OperateActive getOperateActive(int activeId) {
		return this.operateActiveMap.get(activeId);
	}

	@Override
	public void pushHintChange(RoleInstance role, int activeId, byte status) {
		C2453_OperateActiveHintRuleRespMessage resp = new C2453_OperateActiveHintRuleRespMessage();
		resp.setActiveId(activeId);
		resp.setStatus(status);
		GameContext.getMessageCenter().sendSysMsg(role, resp);
	}

	@Override
	public Set<HintType> getHintTypeSet(RoleInstance role) {
		if (this.hasHint(role)) {
			Set<HintType> set = Sets.newHashSet();
			set.add(HintType.operate);
			return set;
		}
		return null;
	}

	@Override
	public boolean hasHint(RoleInstance role) {
		if (Util.isEmpty(this.operateActiveMap)) {
			return false;
		}
		for (OperateActive active : this.operateActiveMap.values()) {
			if (null == active) {
				continue;
			}
			if (active.hasHint(role)) {
				return true;
			}
		}
		return false;
	}

}
