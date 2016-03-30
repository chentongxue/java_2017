package com.game.draco.app.quest.poker.config;

import lombok.Data;
import sacred.alliance.magic.util.Log4jManager;

import com.game.draco.app.quest.poker.PokerThreeType;

public @Data class QuestPokerAwardRatioConfig {
	
	public static final float Base_Ratio = 10000f;
	
	private int type;//牌型
	private String name;//名称
	private int ratio;//奖励倍率
	private short resId;//牌型资源ID
	private byte color1;//扑克花色(0/1/2/3=黑桃/红桃/梅花/方片)
	private byte number1;//扑克数字(0/1/2...11/12)表示(2/3/4...Q/K/A)
	private byte color2;
	private byte number2;
	private byte color3;
	private byte number3;
	
	public void init(String fileInfo){
		String info = fileInfo + "type = " + this.type + ",";
		if(null == PokerThreeType.get(this.type)){
			this.checkFail(info + "this type is not exist.");
		}
		if(this.ratio <= 0){
			this.checkFail(info + "ratio is error.");
		}
		if(this.resId < 0){
			this.checkFail(info + "resId is error.");
		}
		this.checkSamplePoker(info, this.color1, this.number1);
		this.checkSamplePoker(info, this.color2, this.number2);
		this.checkSamplePoker(info, this.color3, this.number3);
	}
	
	private void checkSamplePoker(String errorInfo, byte color, byte number){
		if(color < 0 || color > 3){
			this.checkFail(errorInfo + "color = " + color + ",it's error!");
		}
		if(number < 0 || number > 12){
			this.checkFail(errorInfo + "number = " + color + ",it's error!");
		}
	}
	
	private void checkFail(String info){
		Log4jManager.CHECK.error(info);
		Log4jManager.checkFail();
	}
	
	public float getRealRatio(){
		return this.ratio / Base_Ratio;
	}
	
}
