package sacred.alliance.magic.app.token;

import java.util.Date;

import lombok.Data;

public @Data class AccountToken{
	
	private int appId;
	private int channelId;
	private int userId;
	private Date createTime;
	private String channelUserId;
	private String channelAccessToken;
	private String channelRefreshToken;
	
	private int timeZone; // 时区(-12 ~ 12)的毫秒数
	private long timeMillis;
	
}
