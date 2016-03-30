package sacred.alliance.magic.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.game.draco.GameContext;


public class OnlineServlet extends HttpServlet{

	public void doGet(HttpServletRequest request, HttpServletResponse response)
	throws IOException, ServletException {
		response.setContentType("text/plain;charset=UTF-8");
		PrintWriter out = new PrintWriter(response.getOutputStream());
		out.print(GameContext.getOnlineCenter().onlineUserSize());
		out.close(); 
	}
}
