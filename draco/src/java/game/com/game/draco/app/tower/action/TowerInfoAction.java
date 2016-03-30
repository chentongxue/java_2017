package com.game.draco.app.tower.action;

import com.game.draco.GameContext;
import com.game.draco.message.request.C2551_TowerInfoReqMessage;
import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;


public class TowerInfoAction extends BaseAction<C2551_TowerInfoReqMessage> {

    @Override
    public Message execute(ActionContext context, C2551_TowerInfoReqMessage reqMsg) {
        RoleInstance role = this.getCurrentRole(context) ;
        if(null == role){
            return null ;
        }
        return GameContext.getTowerApp().getTowerInfoMessage(role) ;
    }
}
