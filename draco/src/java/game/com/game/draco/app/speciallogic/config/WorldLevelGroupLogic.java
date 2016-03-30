package com.game.draco.app.speciallogic.config;

import lombok.Data;
import sacred.alliance.magic.base.AttributeType;

/**
 * 藏宝图逻辑
 * @author zhouhaobing
 *
 */
public @Data class WorldLevelGroupLogic{

	//模板Id
	private int groupId;
	//世界等级
	private int worldLevel;
	//攻击
	private int atk;
	//防御
	private int rit;
	//破防
	private int breakDefense;
	//生命
	private int maxHp;
	
	//闪避
	private int dodge; 
	
	//命中
	private int hit; 
	
	//暴击
	private int critAtk;
	
	//韧性
	private int critRit;


    public int getAttriValue(AttributeType attriType){
        switch(attriType){
            case level :
                return this.worldLevel ;
            case atk :
                return this.atk ;
            case rit :
                return this.rit ;
            case breakDefense:
                return this.breakDefense ;
            case maxHP:
                return this.maxHp ;
            case dodge:
                return this.dodge ;
            case hit:
                return this.hit ;
            case critAtk:
                return this.critAtk ;
            case critRit:
                return this.critRit ;
            default:
                return Integer.MIN_VALUE ;
        }
    }
	
}
