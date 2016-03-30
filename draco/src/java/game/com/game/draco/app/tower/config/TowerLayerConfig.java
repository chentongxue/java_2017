package com.game.draco.app.tower.config;

import com.game.draco.GameContext;

import com.google.common.collect.Lists;
import sacred.alliance.magic.app.map.MapUtil;
import sacred.alliance.magic.constant.Cat;
import sacred.alliance.magic.util.KeySupport;
import sacred.alliance.magic.util.Log4jManager;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.vo.Point;
import lombok.Data;

import java.util.List;

public @Data class TowerLayerConfig implements KeySupport<String>{

    private final static int COND_NUM = 3 ;
	private short gate;
	private byte layer;
	private int ruleId;
	private String starConditon;
    private List<Integer> condList = Lists.newArrayList() ;

	public void init(){
		if(Util.isEmpty(starConditon)){
			checkFail("tower.config.TowerLayerConfig.init() fail : starConditon is empty");
            return ;
		}
        String[] strs = Util.splitString(starConditon) ;
        for(String str : strs){
            if(!Util.isNumber(str)){
                checkFail("TowerLayerConfig config error,condId=" + str + " gate=" + gate + " layer=" + layer) ;
                continue;
            }
            condList.add(Integer.parseInt(str)) ;
        }
        if(condList.size() != COND_NUM){
            checkFail("TowerLayerConfig config error,cond size must is 3, gate=" + gate + " layer=" + layer) ;
        }
	}
	
	private void checkFail(String errInfo) {
		Log4jManager.CHECK.error(errInfo);
		Log4jManager.checkFail();
	}

	@Override
	public String getKey() {
		return gate + Cat.underline + layer;
	}
}
