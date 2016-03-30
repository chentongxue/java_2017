package sacred.alliance.magic.open.servlet;

import java.io.PrintWriter;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class BaseHttpServlet extends HttpServlet {
	
	protected Logger logger = LoggerFactory.getLogger(this.getClass());
	
	protected void output(HttpServletResponse response, String respStr){
		PrintWriter out = null;
		try{
			response.setContentType("text/html; charset=utf-8");
			out = response.getWriter();
			out.print(respStr);
			out.flush();
		}catch(Exception e){
			this.logger.error(this.getClass().getName() + " error: ", e);
		}finally{
			if(null != out){
				out.close();
			}
		}
	}
	
}
