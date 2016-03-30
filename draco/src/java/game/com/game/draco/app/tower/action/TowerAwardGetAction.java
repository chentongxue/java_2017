package com.game.draco.app.tower.action;

import com.game.draco.GameContext;
import com.game.draco.base.ExecutorBean;
import com.game.draco.message.request.C2557_TowerAwardGetReqMessage;
import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.core.executor.annotation.ExecutorMapping;
import sacred.alliance.magic.vo.RoleInstance;


@ExecutorMapping(name= ExecutorBean.userOrderedExecutor)
public class TowerAwardGetAction extends BaseAction<C2557_TowerAwardGetReqMessage> {
    @Override
    public Message execute(ActionContext context, C2557_TowerAwardGetReqMessage reqMsg) {
        RoleInstance role = this.getCurrentRole(context);
        if(null == role){
            return null ;
        }
        return GameContext.getTowerApp().recvAward(role,reqMsg.getGate(),reqMsg.getStar());
    }
}
