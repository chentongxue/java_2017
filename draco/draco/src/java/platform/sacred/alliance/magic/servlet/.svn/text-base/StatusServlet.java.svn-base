package sacred.alliance.magic.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.game.draco.GameContext;

import sacred.alliance.magic.util.DateUtil;
import sacred.alliance.magic.vo.MapCopyContainer;
import sacred.alliance.magic.vo.MapInstance;
import sacred.alliance.magic.vo.MapLineContainer;

public class StatusServlet extends HttpServlet{

	private final static String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss" ;
	public void doGet(HttpServletRequest request, HttpServletResponse response)
	throws IOException, ServletException {
		response.setContentType("text/plain;charset=UTF-8");
		PrintWriter out = new PrintWriter(response.getOutputStream());
		out.println("appId: " + GameContext.getAppId());
		out.println("serverId: " + GameContext.getServerId());
		out.println("systemStartTime: " + DateUtil.date2Str(GameContext.systemStartTime, DATE_FORMAT));
		out.println("gameStartDate: " + DateUtil.date2Str(GameContext.gameStartDate, DATE_FORMAT));
		out.println("onlinesize: " + GameContext.getOnlineCenter().onlineUserSize());
		out.println("connection: " +  GameContext.getMinaServer().getAcceptor().getManagedSessionCount());
		Collection<MapInstance> allMap = GameContext.getMapApp().getAllMapInstance();
		Collection<MapCopyContainer> allCopyContainer = GameContext.getMapApp().getCopyContainerMap().values(); 
		Collection<MapLineContainer> allMapLineContaner = GameContext.getMapApp().getLineContainerMap().values();
		out.println("mapSize: " +  allMap.size());
		out.println("copyContainer: " +  allCopyContainer.size());
		out.println("lineContaner: " +  allMapLineContaner.size());
		out.close(); 
	}
}
