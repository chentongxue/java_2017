package com.game.draco.app.camp.balance;

import com.game.draco.app.npc.npcfunction.NpcFunctionSupport;
import com.game.draco.message.request.C1530_CampBalanceReqMessage;
import com.game.draco.message.response.C1531_CampBalanceOpenRespMessage;
import com.game.draco.message.response.C1530_CampBalanceRespMessage;

import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.core.Service;
import sacred.alliance.magic.vo.RoleInstance;

public interface CampBalanceApp extends Service, NpcFunctionSupport{
	
	public static final short CAMP_BALANCE_CMDID = new C1530_CampBalanceReqMessage().getCommandId();
	
	public void roleLevelUp(RoleInstance role);
	
	public byte getRecommendCamp();
	
	public boolean isChangeOpen();
	
	public C1530_CampBalanceRespMessage getCampBalanceMessage(RoleInstance role);
	
	public C1531_CampBalanceOpenRespMessage getCampBalanceOpenMessage(RoleInstance role);
	
	public Result changeCamp(RoleInstance role, byte campId);
	
	public Result selectCamp(RoleInstance role, byte campId); 
	
	public void changeCampBoom(byte winCampId);
	
	public boolean pushToSelectCampMessage(RoleInstance role) ;
	
}
