package sacred.alliance.magic.app.doordog;

import lombok.Data;

public @Data class RoleDoorDogInfo {

	/**
	 * 是否已经通过doorDog验证,默认为已经通过
	 */
	private boolean passDoorDog = true ;
	/**
	 * 通过验证码问题的次数
	 */
	private byte passDoorDogTimes = 0 ;
	private String doorDogAnswer = null ;
	private byte doorDogCount = 0 ;
	private long lastGenQuestionTime = 0;
	//验证码 只有在subType=2(客服答题)是doorDogQuestion ！= null
	private Question doorDogQuestion;
}
