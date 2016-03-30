package com.game.draco.app.medal.action;

import java.util.ArrayList;
import java.util.List;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.app.attri.AttriItem;
import sacred.alliance.magic.base.AttributeType;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.medal.MedalType;
import com.game.draco.app.medal.config.MedalConfig;
import com.game.draco.app.medal.vo.MedalRoleData;
import com.game.draco.message.item.AttriTypeStrValueItem;
import com.game.draco.message.item.MedalDetailItem;
import com.game.draco.message.request.C0522_MedalDetailReqMessage;
import com.game.draco.message.response.C0002_ErrorRespMessage;
import com.game.draco.message.response.C0522_MedalDetailRespMessage;

public class MedalDetailAction extends BaseAction<C0522_MedalDetailReqMessage>{

	private final byte CURRENT = (byte)0;
	private final byte NEXT = (byte)1;
	
	@Override
	public Message execute(ActionContext context, C0522_MedalDetailReqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context);
		if(null == role){
			return null ;
		}
		MedalType medalType = MedalType.get(reqMsg.getType());
		if(null == medalType){
			return new C0002_ErrorRespMessage(reqMsg.getCommandId(),this.getText(TextId.ERROR_INPUT));
		}
		int equipslotEffectNum = GameContext.getMedalApp().getEquipslotEffectNum();
		MedalRoleData rd = GameContext.getMedalApp().getMedalRoleData(role.getRoleId());
		int index = rd.getMedalIndex(medalType);
		C0522_MedalDetailRespMessage respMsg = new C0522_MedalDetailRespMessage();
		respMsg.setMedalName(GameContext.getMedalApp().getMedalName(medalType));
		MedalConfig currentConfig = GameContext.getMedalApp().getMedalConfig(medalType, index);
		List<MedalDetailItem> items = new ArrayList<MedalDetailItem>();
		if(null != currentConfig){
			MedalDetailItem currentItem = new MedalDetailItem();
			currentItem.setShowLv(currentConfig.getShowLv());
			currentItem.setType(CURRENT);
			currentItem.setGoalName(currentConfig.getName());
			if(MedalType.QiangHua == medalType){
				currentItem.setGoalNum(equipslotEffectNum);
				currentItem.setSelfNum(equipslotEffectNum);
			}else if (MedalType.XiangQian == medalType || MedalType.XiLian == medalType){
				currentItem.setGoalNum(currentConfig.getNum());
				currentItem.setSelfNum(currentItem.getGoalNum());
			}else if(medalType.isAttribute()){
				currentItem.setGoalNum(currentConfig.getRelyAttrValue());
				currentItem.setSelfNum(currentItem.getGoalNum());
			}
			currentItem.setAttriItems(this.getDisplayAttriItem(currentConfig.getAttriList()));
			items.add(currentItem);
		}
		MedalConfig nextConfig = GameContext.getMedalApp().getMedalConfig(medalType, index + 1);
		if (null != nextConfig) {
			MedalDetailItem nextItem = new MedalDetailItem();
			nextItem.setType(NEXT);
			nextItem.setShowLv(nextConfig.getShowLv());
			nextItem.setGoalName(nextConfig.getName());
			if (MedalType.QiangHua == medalType) {
				nextItem.setGoalNum(equipslotEffectNum);
				//TODO
				//nextItem.setSelfNum(role.getEquipBackpack().totalEffectStrengthenLevel(nextConfig.getLevel()));
			} else if (MedalType.XiangQian == medalType) {
				nextItem.setGoalNum(nextConfig.getNum());
				//nextItem.setSelfNum(role.getEquipBackpack().totalEffectMosaicLevel(nextConfig.getLevel()));
			} else if (MedalType.XiLian == medalType) {
				nextItem.setGoalNum(nextConfig.getNum());
				//nextItem.setSelfNum(role.getEquipBackpack().totalEffectRecastingQualityNum(nextConfig.getLevel()));
			} else if (medalType.isAttribute()){
				nextItem.setGoalNum(nextConfig.getRelyAttrValue());
				nextItem.setSelfNum(role.get(nextConfig.getRelyAttrType()));
			}
			nextItem.setAttriItems(this.getDisplayAttriItem(nextConfig.getAttriList()));
			items.add(nextItem);
		}
		respMsg.setItems(items);
		return respMsg;
	}
	
	private List<AttriTypeStrValueItem> getDisplayAttriItem(List<AttriItem> items){
		if(Util.isEmpty(items)){
			return null ;
		}
		List<AttriTypeStrValueItem> rets = new ArrayList<AttriTypeStrValueItem>();
		for(AttriItem item : items){
			AttriTypeStrValueItem ret = new AttriTypeStrValueItem();
			ret.setType(item.getAttriTypeValue());
			ret.setValue(AttributeType.formatValue(item.getAttriTypeValue(), item.getValue()));
			rets.add(ret);
		}
		return rets ;
	}

}
