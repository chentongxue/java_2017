package com.game.draco.app.tower.type;


public enum TowerGateStatus {

    notPass((byte)0),
    passed((byte)1),
    reseted((byte)2),
    closed((byte)3),
    ;

    private final byte type ;


    private TowerGateStatus(byte type){
        this.type = type ;
    }

    public byte getType() {
        return type;
    }
}
