package sacred.alliance.magic.app.config;

public class PlatformConfig extends PropertiesConfig {
	
	/**
	 * 计费中心的充值参数地址
	 * @return
	 */
	public String getFeeCenterChargeArgsUrl(){
		return this.getConfig("fee.center.charge.args.url");
	}
	
	/**
	 * 获取激活码验证的HTTP地址
	 * @return
	 */
	public String getActiveCodeHttpUrl(){
		return this.getConfig("active.code.verify.url");
	}
	
	/**
	 * 获取接收BUG问题的HTTP地址（客服平台）
	 * @return
	 */
	public String getBugInfoHttpUrl(){
		return this.getConfig("customer.buginfo.url");
	}
	
	/**
	 * 排行榜的HTTP地址
	 * @return
	 */
	public String getLogServerAddr(){
		return this.getConfig("rankLogAppAddr");
	}
	
	public String getInviteHttpUrl(){
		return this.getConfig("invite.url");
	}
	
	/**
	 * 验证码HTTP地址
	 * @return
	 */
	public String getDoorDogAddr(){
		return this.getConfig("doorDog.url");
	}
	
	public String getSsdbHost(){
		return this.getConfig("ssdb.host");
	}
	
	public int getSsdbPort(){
		return Integer.parseInt(this.getConfig("ssdb.port"));
	}
	
	public String getVoice2TextUrl(){
		return this.getConfig("listen.voice2text.url");
	}
	
	@Override
	public String getName() {
		return this.getClass().getSimpleName();
	}
	
	
}
