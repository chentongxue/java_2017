package sacred.alliance.magic.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.continuation.Continuation;
import org.eclipse.jetty.continuation.ContinuationSupport;

import com.game.draco.GameContext;

import sacred.alliance.magic.channel.servlet.AbstractServletUtil;
import sacred.alliance.magic.channel.servlet.ServletContext;
import sacred.alliance.magic.client.http.HttpClientUtil;
import sacred.alliance.magic.util.Util;

public class GameServlet extends HttpServlet {

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		
		final Continuation continuation = ContinuationSupport
				.getContinuation(request);
		if (continuation.isExpired()) {
			return;
		}
		// 设置超时
		continuation.setTimeout(GameContext.getContinuationTimeout());
		// 挂起HTTP连接
		continuation.suspend(response);

		AbstractServletUtil servletUtil = GameContext.getServletStreamUtil();
		String cmdId = request.getHeader(HttpClientUtil.CMD_ID_HTTP_HEAD);
		if(!Util.isEmpty(cmdId)){
			//json结构
			servletUtil = GameContext.getServletJsonUtil();
		}
		ContinuationParameter cp = new ContinuationParameter(continuation);
		servletUtil.doAction(
				new ServletContext(request, response, cp), true);
	}
}
