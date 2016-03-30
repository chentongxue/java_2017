package com.game.draco.app.tower.action;

import com.game.draco.GameContext;
import com.game.draco.message.request.C2552_TowerGateInfoReqMessage;
import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;


public class TowerGateInfoAction extends BaseAction<C2552_TowerGateInfoReqMessage> {

    @Override
    public Message execute(ActionContext context, C2552_TowerGateInfoReqMessage reqMsg) {
        RoleInstance role = this.getCurrentRole(context) ;
        if(null == role){
            return null ;
        }
        return GameContext.getTowerApp().getTowerGateInfoMessage(role,reqMsg.getGate());
    }
}
