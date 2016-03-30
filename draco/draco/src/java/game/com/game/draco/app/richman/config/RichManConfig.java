package com.game.draco.app.richman.config;

import lombok.Data;

public @Data class RichManConfig {
	private String mapId; //大富翁地图id
	private byte joinNum; //每日进地图次数
	private byte freeDiceNum; //每日免费投骰子次数
	private byte freeDiceRemoteNum; //每日免费遥控骰子次数
	private byte freeDiceDoubleNum; //每日免费2倍骰子次数
	private byte normalDiceFeeNum; //每日付费骰子次数
	private int randomEventRefreshTime; //随机事件刷新周期 单位ms
	private byte randomEventBaseNum; //随机事件基数 X
	private byte diceDoubleGold; //双倍骰子价格
	private byte diceRemoteGold; //遥控骰子价格
	private byte godMoveNum; //财神衰神持续步数
	private int godMinCoupon; //财神(衰神)增加点券下限
	private int godMaxCoupon; //财神(衰神)增加点券上限
}
