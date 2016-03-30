package com.game.draco.message.internal;


import lombok.Data;
import sacred.alliance.magic.vo.RoleInstance;

public @Data
class C0058_TowerPassedInternalMessage extends InternalMessage{

    public C0058_TowerPassedInternalMessage(){
        this.commandId = 58 ;
    }
    private RoleInstance role ;
}
