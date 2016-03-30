package sacred.alliance.magic.app.config;

public class TokenSecretkeyConfig extends PropertiesConfig {
	
	public String getUcMd5Secretkey(){
		return this.getConfig("uc.token.md5.secret.key");
	}
	
	public String getUcRsaPrivatekey(){
		return this.getConfig("uc.token.rsa.private.key");
	}
	
	public int getAppTokenValidTimeMillis() {
		try {
			String value = this.getConfig("uc.token.valid.time.millis");
			return Integer.valueOf(value);
		} catch (Exception e) {
			return 300000;
		}
	}
	
	/**
	 * 是否打开用户中心验证
	 * 游戏自己验证失败时，是否需要连接用户中心进行验证
	 * @return
	 */
	public boolean isToUcVerifyOpen(){
		String var = this.getConfig("to.uc.verify.open");
		return null != var && var.equals("1");
	}
	
	@Override
	public String getName() {
		return this.getClass().getSimpleName();
	}
	
}
