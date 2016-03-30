package com.game.draco.app.chat.config;

import com.game.draco.GameContext;
import com.game.draco.app.chat.ChannelType;

import lombok.Data;
import sacred.alliance.magic.domain.GoodsBase;
import sacred.alliance.magic.util.Log4jManager;

public @Data class ChatLimitConfig {
	
	private byte type;//频道类型
	private int spaceTime;//说话间隔时间(秒)
	private int roleLevel;//角色等级
	private int goodsId;//消耗道具ID
    private int voiceGoodsId ;
	
	private ChannelType channelType;
	//private String goodsName;//消耗道具名称
    //private String voiceGoodsName ;


	
	public boolean checkAndInit(String fileInfo){
		boolean result = true;
		String info = fileInfo + " type=" + this.type + ",";
		this.channelType = ChannelType.getChannelType(this.type);
		if(null == this.channelType){
			this.checkFail(info + "the type is not exist!");
			result = false;
		}
		if(this.spaceTime < 0){
			this.checkFail(info + "the spaceTime is error!");
			result = false;
		}
		if(this.roleLevel < 0){
			this.checkFail(info + "the roleLevel is error!");
			result = false;
		}

        if(!this.isRightGoods(info,this.goodsId) ){
            result = false ;
        }
        if(!this.isRightGoods(info,this.voiceGoodsId) ){
            result = false ;
        }
		return result;
	}

    private boolean isRightGoods(String info,int goodsId){
        if(goodsId < 0){
            this.checkFail(info + "the goodsId is error!");
            return false ;
        }
        if(goodsId > 0){
            GoodsBase goodsBase = GameContext.getGoodsApp().getGoodsBase(goodsId);
            if(null == goodsBase){
                this.checkFail(info + "this goods is not exist! goodsId=" + goodsId);
                return false ;
            }
        }
        return true ;
    }

	
	private void checkFail(String info){
		Log4jManager.CHECK.error(info);
		Log4jManager.checkFail();
	}
	
}
