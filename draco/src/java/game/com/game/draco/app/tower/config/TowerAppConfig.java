package com.game.draco.app.tower.config;

import sacred.alliance.magic.util.Log4jManager;
import lombok.Data;

public
@Data
class TowerAppConfig {

    private int defaultResetClearNum;
    private int raidsCd;
    private short maxOpenGate;
    private int rankId ;

    public void init() {
        if (defaultResetClearNum < 0) {
            checkFail(".tower.config.TowerAppConfig.init()fail : defaultClearNum <= 0"
                    + "check tower.xls -> appConfig");
        }
    }

    private void checkFail(String errInfo) {
        Log4jManager.CHECK.error(errInfo);
        Log4jManager.checkFail();
    }
}
