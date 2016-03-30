package com.game.draco.app.tower.domain;


import lombok.Data;

/**
 * 当前所在的层
 */
public @Data
class TowerCurrLayer {
    private short gateId ;
    private byte layerId ;
}
