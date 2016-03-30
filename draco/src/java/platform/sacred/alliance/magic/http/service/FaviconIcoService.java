package sacred.alliance.magic.http.service;

import sacred.alliance.magic.channel.http.HttpContext;
import sacred.alliance.magic.channel.http.service.SimpleHttpService;

public class FaviconIcoService extends SimpleHttpService {

	@Override
	public String getStringBody(HttpContext context) {
		//为了不路由到 default 直接返回空字符串
		return "" ;
	}

}
