package com.game.draco.action.internal;

import com.game.draco.GameContext;
import com.game.draco.message.internal.C0058_TowerPassedInternalMessage;
import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;

public class TowerPassedInternalAction extends BaseAction<C0058_TowerPassedInternalMessage> {

    @Override
    public Message execute(ActionContext context,
                           C0058_TowerPassedInternalMessage reqMsg) {
        GameContext.getTowerApp().towerPassed(reqMsg.getRole());
        return null ;
    }
}
