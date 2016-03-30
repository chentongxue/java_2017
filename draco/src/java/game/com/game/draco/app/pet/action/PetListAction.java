package com.game.draco.app.pet.action;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.message.item.PetListItem;
import com.game.draco.message.request.C1651_PetListReqMessage;
import com.game.draco.message.response.C1651_PetListRespMessage;

public class PetListAction extends BaseAction<C1651_PetListReqMessage> {

	@Override
	public Message execute(ActionContext context, C1651_PetListReqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context);
		if (null == role) {
			return null;
		}
		List<PetListItem> list = GameContext.getPetApp().getPetList(role);
		this.sort(list);
		C1651_PetListRespMessage resp = new C1651_PetListRespMessage();
		resp.setPetList(list);
		return resp;
	}
	
	private void sort(List<PetListItem> petList){
		Collections.sort(petList, comparator);
	}
	
	//排序
	private Comparator<PetListItem> comparator = new Comparator<PetListItem>() {
		@Override
		public int compare(PetListItem r1, PetListItem r2) {
			if (r1.getStatus() < r2.getStatus()) {
				return 1;
			}
			if (r1.getStatus() > r2.getStatus()) {
				return -1;
			}
			if (r1.getQuality() < r2.getQuality()) {
				return 1;
			}
			if (r1.getQuality() > r2.getQuality()) {
				return -1;
			}
			if (r1.getStar() < r2.getStar()) {
				return 1;
			}
			if (r1.getStar() > r2.getStar()) {
				return -1;
			}
			return 0;
		}
	};

}
