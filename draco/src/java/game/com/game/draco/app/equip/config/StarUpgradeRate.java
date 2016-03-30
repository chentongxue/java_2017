package com.game.draco.app.equip.config;


import lombok.Data;
import sacred.alliance.magic.util.KeySupport;

public @Data
class StarUpgradeRate implements KeySupport<String>{

    private byte quality;
    private byte star ;

    private short rate ;
    private short showRate ;

    @Override
    public String getKey() {
        return this.quality + "_" + this.star ;
    }
}
