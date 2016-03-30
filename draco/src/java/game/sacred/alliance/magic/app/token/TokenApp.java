package sacred.alliance.magic.app.token;

import com.game.draco.message.request.C4999_UserLoginSafeReqMessage;

public interface TokenApp {
	
	/**
	 * 验证登录
	 * @param reqMsg
	 * @return
	 */
	public TokenResult loginCheck(C4999_UserLoginSafeReqMessage reqMsg);
	
}
