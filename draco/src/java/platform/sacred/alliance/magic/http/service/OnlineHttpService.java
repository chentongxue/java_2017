package sacred.alliance.magic.http.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.game.draco.GameContext;

import sacred.alliance.magic.channel.http.HttpContext;
import sacred.alliance.magic.channel.http.service.SimpleHttpService;

public class OnlineHttpService extends SimpleHttpService{
	private static final Logger logger = LoggerFactory.getLogger(OnlineHttpService.class);

	@Override
	public String getStringBody(HttpContext context) {
		return String.valueOf(GameContext.getOnlineCenter().onlineUserSize());
	}
	
}
