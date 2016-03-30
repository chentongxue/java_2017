package sacred.alliance.magic.http.service;

import sacred.alliance.magic.channel.http.HttpContext;
import sacred.alliance.magic.channel.http.service.SimpleHttpService;

public class RobotsService extends SimpleHttpService{
	private final String ROBOTS_TEXT = "User-agent: *\r\nDisallow: /" ;
	@Override
	public String getStringBody(HttpContext context) {
		this.setDefaultContentType("text/plain; charset=utf-8");
		return ROBOTS_TEXT;
	}

}
