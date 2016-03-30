package com.game.draco.app.tower.config;

import lombok.Data;
import sacred.alliance.magic.constant.Cat;
import sacred.alliance.magic.util.KeySupport;

public
@Data
class TowerStarAwardConfig extends TowerAward implements KeySupport<String> {

    private short gate;
    private byte star;

    @Override
    public String getErrorPrefixTips() {
        return "TowerStarAwardConfig config error,gate=" + gate + " star=" + star + " ";
    }

    @Override
    public String getKey() {
        return gate + Cat.underline + star;
    }

    public void init() {
        super.init();
    }


}