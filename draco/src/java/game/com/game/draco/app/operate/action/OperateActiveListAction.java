package com.game.draco.app.operate.action;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.operate.OperateActive;
import com.game.draco.message.item.OperateActiveItem;
import com.game.draco.message.request.C2451_OperateActiveListReqMessage;
import com.game.draco.message.response.C2451_OperateActiveListRespMessage;
import com.google.common.collect.Lists;

public class OperateActiveListAction extends BaseAction<C2451_OperateActiveListReqMessage> {

	@Override
	public Message execute(ActionContext context, C2451_OperateActiveListReqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context);
		if (null == role) {
			return null;
		}
		C2451_OperateActiveListRespMessage resp = new C2451_OperateActiveListRespMessage();
		List<OperateActive> activeList = GameContext.getOperateActiveApp().getAllOperateActive(role);
		if (Util.isEmpty(activeList)) {
			// 如果没有运营活动
			return resp;
		}
		List<OperateActiveItem> operateActiveList = Lists.newArrayList();
		for (OperateActive active : activeList) {
			if (null == active) {
				continue;
			}
			OperateActiveItem item = new OperateActiveItem();
			item.setActiveId(active.getOperateActiveId());
			item.setActiveName(active.getOperateActiveName());
			item.setStatus(active.getOperateActiveStatus(role));
			operateActiveList.add(item);
		}
		this.sortOperateActiveList(operateActiveList);
		resp.setActiveId(this.getDefaultActiveId(reqMsg.getActiveId(), operateActiveList));
		resp.setOperateActiveList(operateActiveList);
		return resp;
	}
	
	private int getDefaultActiveId(int activeId, List<OperateActiveItem> operateActiveList) {
		if (0 != activeId && this.haveOperateActive(activeId, operateActiveList)) {
			return activeId;
		}
		return operateActiveList.get(0).getActiveId();
	}
	
	private boolean haveOperateActive(int activeId, List<OperateActiveItem> operateActiveList) {
		for (OperateActiveItem item : operateActiveList) {
			if (null == item) {
				continue;
			}
			if (item.getActiveId() == activeId) {
				return true;
			}
		}
		return false;
	}
	
	private void sortOperateActiveList(List<OperateActiveItem> operateActiveList) {
		Collections.sort(operateActiveList, new Comparator<OperateActiveItem>() {
			@Override
			public int compare(OperateActiveItem item1, OperateActiveItem item2) {
				if (item1.getStatus() > item2.getStatus()) {
					return 1;
				}
				if (item1.getStatus() < item2.getStatus()) {
					return -1;
				}
				if (item1.getActiveId() > item2.getActiveId()) {
					return 1;
				}
				if (item1.getActiveId() < item2.getActiveId()) {
					return -1;
				}
				return 0;
			}
		});
	}

}
