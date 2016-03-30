package com.game.draco.app.medal;

import java.util.List;

import sacred.alliance.magic.app.attri.AttriBuffer;
import sacred.alliance.magic.base.AttributeType;
import sacred.alliance.magic.core.Service;
import sacred.alliance.magic.domain.RoleGoods;
import sacred.alliance.magic.vo.AbstractRole;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.app.AppSupport;
import com.game.draco.app.medal.config.MedalConfig;
import com.game.draco.app.medal.vo.MedalRoleData;
import com.game.draco.message.item.MedalIconItem;
import com.game.draco.message.response.C0521_MedalListRespMessage;

public interface MedalApp extends Service, AppSupport{
	
	public AttriBuffer getAttriBuffer(AbstractRole player);
	
	public void updateMedal(RoleInstance role, MedalType medalType, RoleGoods roleGoods);
	
	public void updateMedal(RoleInstance role, AttributeType attrType);
	
	public short[] getRoleMedalEffects(RoleInstance role);
	
	public MedalConfig getMedalConfig(MedalType medalType, int index);
	
	public String getMedalName(MedalType medalType);
	
	public short getDefaultIcon(MedalType medalType);
	
	public MedalRoleData getMedalRoleData(String roleId);
	
	public C0521_MedalListRespMessage getC0521_MedalListRespMessage(RoleInstance role);
	
	public List<MedalIconItem> getMedalList(String roleId);
	
	public int getEquipslotEffectNum();
	
}
