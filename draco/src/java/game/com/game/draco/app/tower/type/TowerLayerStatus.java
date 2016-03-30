package com.game.draco.app.tower.type;


public enum TowerLayerStatus {

    closed((byte)0),
    passed((byte)1),
    canEnter((byte)2),
    ;

    private final byte type ;


    private TowerLayerStatus(byte type){
        this.type = type ;
    }

    public byte getType() {
        return type;
    }
}
