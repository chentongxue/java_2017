package com.game.draco.app.talent;

import sacred.alliance.magic.app.attri.AttriBuffer;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.app.AppSupport;
import com.game.draco.app.talent.domain.RoleTalent;
import com.game.draco.app.talent.vo.RoleTrainTalentResult;
import com.game.draco.message.response.C2816_RoleTalentListRespMessage;
import com.game.draco.message.response.C2817_RoleTalentRefRespMessage;
import com.game.draco.message.response.C2819_RoleTrainTalentInfoRespMessage;

public interface RoleTalentApp extends AppSupport{
	
	//角色升级触发
	void onRoleLevelUp(RoleInstance role);

	//培养
	RoleTrainTalentResult trainTalent(RoleInstance role, byte type);
	
	//天赋列表
	C2816_RoleTalentListRespMessage sendC2816_RoleTalentListRespMessage(RoleInstance role);
	
	//天赋详情列表
	C2819_RoleTrainTalentInfoRespMessage sendC2819_RoleTrainTalentInfoRespMessage(RoleInstance role);
	
	//保存培养数据
	Result saveTempTalent(RoleInstance role);

	C2817_RoleTalentRefRespMessage sendC2817_RoleTalentRefRespMessage(RoleInstance role,RoleTalent temp);
	
	RoleTalent getRoleTalent(int roleId);
	
	AttriBuffer getAttriBuffer(RoleInstance role);
}
