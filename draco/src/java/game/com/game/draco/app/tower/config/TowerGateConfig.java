package com.game.draco.app.tower.config;

import com.game.draco.GameContext;
import lombok.Data;
import sacred.alliance.magic.app.map.data.MapConfig;
import sacred.alliance.magic.base.MapLogicType;
import sacred.alliance.magic.util.KeySupport;

public
@Data
class TowerGateConfig extends TowerAward implements KeySupport<Short> {

    private short gate;
    private String gateName;
    private String mapId;
    private int enterX;
    private int enterY;

    @Override
    public Short getKey() {
        return gate;
    }

    public void init() {
        super.init();
        MapConfig mapConfig = GameContext.getMapApp().getMapConfig(this.mapId);
        if (null == mapConfig || !mapConfig.changeLogicType(MapLogicType.tower)) {
            checkFail("tower config error,mapId=" + mapId + " not exist or logicType is not tower");
        }

    }

    @Override
    public String getErrorPrefixTips() {
        return "TowerGateConfig config error gate=" + gate + " ";
    }

}