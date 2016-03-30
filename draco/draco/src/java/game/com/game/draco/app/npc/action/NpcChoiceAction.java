package com.game.draco.app.npc.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.game.draco.app.npc.domain.NpcInstance;
import com.game.draco.message.item.NpcFunctionItem;
import com.game.draco.message.request.C1608_NpcChoiceReqMessage;
import com.game.draco.message.response.C0002_ErrorRespMessage;
import com.game.draco.message.response.C0607_NpcFunctionRespMessage;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.constant.Status;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.vo.RoleInstance;

public class NpcChoiceAction extends BaseAction<C1608_NpcChoiceReqMessage>{
		
	@Override
	public Message execute(ActionContext context, C1608_NpcChoiceReqMessage req) {
		try{
			RoleInstance role = this.getCurrentRole(context);
			if(role == null){
				C0002_ErrorRespMessage erm = new C0002_ErrorRespMessage();
				erm.setReqCmdId(req.getCommandId());
				erm.setInfo(this.getText(TextId.ROLE_NOT_ONLINE));
				return erm;
			}
			String params = req.getParam();
			Map<String,String> paramMap = this.parasParam(params);
			if(role.getMapInstance() == null){
				C0002_ErrorRespMessage erm = new C0002_ErrorRespMessage();
				erm.setReqCmdId(req.getCommandId());
				erm.setInfo(Status.Npc_Map_Not_Exist.getTips());
				return erm;
			}
			NpcInstance npcInstance = role.getMapInstance().getNpcInstance(paramMap.get("roleid"));
			
			if(npcInstance == null){
				C0002_ErrorRespMessage erm = new C0002_ErrorRespMessage();
				erm.setReqCmdId(req.getCommandId());
				erm.setInfo(Status.Npc_Not_Exist.getTips());
				return erm;
			}
			int choiceSize = 0;
			List<NpcFunctionItem> choiceList = npcInstance.getAi().choice(role,(Integer.valueOf(paramMap.get("index"))));
			if(!Util.isEmpty(choiceList)){
				choiceSize = choiceList.size();
			}
			List<NpcFunctionItem> items = new ArrayList<NpcFunctionItem>();
			if(choiceSize>0){
				items.addAll(choiceList);
			}
			C0607_NpcFunctionRespMessage resp = new  C0607_NpcFunctionRespMessage();
			resp.setItems(items);
			resp.setNpcRoleId(npcInstance.getIntRoleId());
			return resp;
		}catch(Exception e){
			logger.error("",e);
			C0002_ErrorRespMessage erm = new C0002_ErrorRespMessage();
			erm.setReqCmdId(req.getCommandId());
			erm.setInfo(this.getText(TextId.SYSTEM_ERROR));
			return erm;
		}
	}
	
	private Map<String,String> parasParam(String params){
		Map<String,String> map = new HashMap<String,String>();
		if(null == params || 0 == params.trim().length()){
			return map ;
		}
		String[] par = params.split("&");
		for(int i=0;i<par.length;i++){
			String param = par[i];
			String[] kv = param.split("=");
			if(kv == null || kv.length != 2){
				continue;
			}
			map.put(kv[0], kv[1]);
		}
		return map;
	}

}
