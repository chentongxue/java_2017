package sacred.alliance.magic.app.chat;

import com.game.draco.GameContext;

import lombok.Data;
import sacred.alliance.magic.domain.GoodsBase;
import sacred.alliance.magic.util.Log4jManager;

public @Data class ChatLimitConfig {
	
	private byte type;//频道类型
	private int spaceTime;//说话间隔时间(秒)
	private int roleLevel;//角色等级
	private int goodsId;//消耗道具ID
	
	private ChannelType channelType;
	private String goodsName;//消耗道具名称
	
	public boolean checkAndInit(String fileInfo){
		boolean result = true;
		String info = fileInfo + "type=" + this.type + ",";
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
		if(this.goodsId < 0){
			this.checkFail(info + "the goodsId is error!");
			result = false;
		}
		if(this.goodsId > 0){
			GoodsBase goodsBase = GameContext.getGoodsApp().getGoodsBase(this.goodsId);
			if(null == goodsBase){
				this.checkFail(info + "this goods is not exist!");
				result = false;
			}
			this.goodsName = goodsBase.getName();
		}
		return result;
	}
	
	private void checkFail(String info){
		Log4jManager.CHECK.error(info);
		Log4jManager.checkFail();
	}
	
}
