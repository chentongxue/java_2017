package sacred.alliance.magic.http.service;


import com.game.draco.GameContext;
import sacred.alliance.magic.app.goods.Util;
import sacred.alliance.magic.channel.http.HttpContext;
import sacred.alliance.magic.channel.http.service.SimpleHttpService;

public class ExitService extends SimpleHttpService{
	private static final String KEY = "533221a22d32f8370c594ab14695d" ;

	@Override
	public String getStringBody(HttpContext context) {
		this.setDefaultContentType("text/plain; charset=utf-8");
		if(GameContext.isOfficialServer()){
			//正式服务器不允许
			return "forbid" ;
		}
		String key = context.getRequest().getParameter("_k");
		if(Util.isEmpty(key) || !key.equals(KEY)){
			return "forbid" ;
		}
		//!!!!!! shutdown
		System.exit(0);
		return "success";
	}

}
