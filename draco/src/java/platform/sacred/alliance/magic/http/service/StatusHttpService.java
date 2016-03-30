package sacred.alliance.magic.http.service;

import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.game.draco.GameContext;

import sacred.alliance.magic.channel.http.HttpContext;
import sacred.alliance.magic.channel.http.service.SimpleHttpService;
import sacred.alliance.magic.util.DateUtil;
import sacred.alliance.magic.vo.MapCopyContainer;
import sacred.alliance.magic.vo.MapInstance;
import sacred.alliance.magic.vo.MapLineContainer;

public class StatusHttpService extends SimpleHttpService{
	
	//private static final Logger logger = LoggerFactory.getLogger(StatusHttpService.class);

	private final static String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss" ;
	private final static String CRLF = "\r\n" ;
	
	public StatusHttpService(){
		this.setDefaultContentType("text/plain; charset=utf-8");
	}
	
	@Override
	public String getStringBody(HttpContext context) {
		try {
			StringBuilder sb = new StringBuilder();
			sb.append("appId: " + GameContext.getAppId()).append(CRLF);
			sb.append("serverId: " + GameContext.getServerId()).append(CRLF);
			sb.append(
					"systemStartTime: "
							+ DateUtil.date2Str(GameContext.systemStartTime,
									DATE_FORMAT)).append(CRLF);
			sb.append(
					"gameStartDate: "
							+ DateUtil.date2Str(GameContext.gameStartDate,
									DATE_FORMAT)).append(CRLF);
			sb.append(
					"onlinesize: "
							+ GameContext.getOnlineCenter().onlineUserSize())
					.append(CRLF);
			sb.append(
					"connection: "
							+ GameContext.getMinaServer().getAcceptor()
									.getManagedSessionCount()).append(CRLF);
			Collection<MapInstance> allMap = GameContext.getMapApp()
					.getAllMapInstance();
			Collection<MapCopyContainer> allCopyContainer = GameContext
					.getMapApp().getCopyContainerMap().values();
			Collection<MapLineContainer> allMapLineContaner = GameContext
					.getMapApp().getLineContainerMap().values();
			sb.append("mapSize: " + allMap.size()).append(CRLF);
			sb.append("copyContainer: " + allCopyContainer.size()).append(CRLF);
			sb.append("lineContaner: " + allMapLineContaner.size())
					.append(CRLF);
			return sb.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
}
