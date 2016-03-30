package com.game.draco.app.tower.domain;


import lombok.Data;

import java.util.Date;

public
@Data
class RoleTowerGate implements java.lang.Comparable {

    public static final String ROLE_ID = "roleId";
    private static final int One = 1;
    /**
     * 每一层的星占4位保存,最大允许8层
     * 1: 0,1,2,3
     * 2: 4,5,6,7
     * 3: 8,9,10,11
     * 4: 12,13,14,15
     */
    private static final int STAR_POS = 4;
    private static final int MAX_LAYER = 4;

    private String roleId;
    private short gateId;
    /**
     * 每层获得的星
     */
    private int star;
    /**
     * 领奖状态
     */
    private int starAwardState;

    /**
     * 重置状态
     * 负数 未重置
     * 0 已经重置
     * 其他： 重置并且层已打过（位运算）
     */
    private byte resetStatus = -1;

    public boolean isReseted() {
        return this.resetStatus >= 0;
    }

    public void updatePassedWhenReseted(int layerId){
        if(!this.isReseted()){
            return ;
        }
        this.resetStatus = (byte)setIndexValueOne(this.resetStatus,layerId) ;
    }


    public boolean isResetAndPassed(int layerId) {
        if (resetStatus <= 0) {
            return false;
        }
        return 1 == getIndexValue(this.resetStatus, layerId);
    }

    public boolean isResetAndAllPassed() {
        if (resetStatus <= 0) {
            return false;
        }
        for (int i = 0; i < MAX_LAYER; i++) {
            if (1 != getIndexValue(this.resetStatus, i)) {
                return false;
            }
        }
        return true;
    }

    public boolean isPassed() {
        return this.getLayerStar(MAX_LAYER) >= 0;
    }

    public byte getMaxLayer() {
        for (int layerId = MAX_LAYER; layerId >= 1; layerId--) {
            int star = this.getLayerStar(layerId);
            if (star >= 0) {
                return (byte) layerId;
            }
        }
        return 1;
    }

    public byte totalStar() {
        int ret = 0;
        for (int layerId = 1; layerId <= MAX_LAYER; layerId++) {
            int star = this.getLayerStar(layerId);
            if(star < 0){
                continue;
            }
            ret += star ;
        }
        return (byte) ret;
    }

    public int getLayerStar(int layerId) {
        int start = (layerId - 1) * STAR_POS;
        int index = -1;
        int retStar = 0;
        for (int i = start; i < start + STAR_POS; i++) {
            index++;
            int value = getIndexValue(this.star, i);
            if (1 != value) {
                continue;
            }
            retStar = setIndexValueOne(retStar, index);
        }
        retStar = retStar - 1;
        return (retStar <= 0) ? -1 : retStar;
    }

    /**
     * @param layerId
     * @param star    [0-4]
     */
    public void updateLayerStar(int layerId, int star) {
        int newStar = star + 1;
        int start = (layerId - 1) * STAR_POS;
        int index = -1;
        for (int i = start; i < start + STAR_POS; i++) {
            index++;
            int value = getIndexValue(newStar, index);
            if (1 == value) {
                this.star = setIndexValueOne(this.star, i);
            } else {
                this.star = setIndexValueZero(this.star, i);
            }
        }
    }

    /**
     * 某星是否已经领奖
     *
     * @param star
     * @return
     */
    public byte getStarAwardState(int star) {
        return (byte) ((this.starAwardState >> star) & One);
    }

    /**
     * 将某星设置为已经领奖
     *
     * @param star
     */
    public void updateStarAwardState(int star) {
        this.starAwardState = (starAwardState | (One << star));
    }


    /**
     * 取int型变量data的第index位置的值
     *
     * @param data
     * @param index
     * @return
     */
    public static int getIndexValue(int data, int index) {
        return (data >> index) & One;
    }

    /**
     * 将int型变量data的第index位置1
     *
     * @param data
     * @param index
     * @return
     */
    public static int setIndexValueOne(int data, int index) {
        return data | (One << index);
    }

    public static int setIndexValueZero(int data, int index) {
        return data & ~(One << index);
    }


    @Override
    public int compareTo(Object o) {
        if (null == o || !(o instanceof RoleTowerGate)) {
            return 1;
        }
        RoleTowerGate target = (RoleTowerGate) o;
        if (this.getGateId() > target.getGateId()) {
            return 1;
        }
        if (this.getGateId() < target.getGateId()) {
            return -1;
        }
        return this.star - target.getStar();
    }
}
