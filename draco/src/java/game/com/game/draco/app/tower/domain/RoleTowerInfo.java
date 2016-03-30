package com.game.draco.app.tower.domain;

import com.google.common.collect.Maps;
import lombok.Data;

import java.util.Map;

public
@Data
class RoleTowerInfo {

    private RoleTowerGate maxGate = null;
    private Map<Short, RoleTowerGate> gateMap = Maps.newHashMap();

    public void putRoleTowerGate(RoleTowerGate gate) {
        if (null == gate) {
            return;
        }
        gateMap.put(gate.getGateId(), gate);
        if (null == maxGate) {
            this.maxGate = gate;
            return;
        }
        if (gate.compareTo(maxGate) > 0) {
            this.maxGate = gate;
        }
    }

    public RoleTowerGate getRoleTowerGate(short gateId) {
        return this.getGateMap().get(gateId);
    }
}
