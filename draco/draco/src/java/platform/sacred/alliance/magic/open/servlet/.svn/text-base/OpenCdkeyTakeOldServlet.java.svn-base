package sacred.alliance.magic.open.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.util.TreeMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.open.bean.OpenStateResp;
import sacred.alliance.magic.open.constant.OpenState;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.vo.RoleInstance;

import com.alibaba.fastjson.JSON;
import com.game.draco.GameContext;

public class OpenCdkeyTakeOldServlet extends HttpServlet {
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		this.doPost(request, response);
	}
	
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		PrintWriter out = null;
		try{
			OpenStateResp state = this.getOpenStateResp(request);
			String respStr = JSON.toJSONString(state);
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
	
	private OpenStateResp getOpenStateResp(HttpServletRequest request) {
		OpenStateResp resp = new OpenStateResp();
		try {
			int appId = Integer.valueOf(request.getParameter("appId"));
			int serverId = Integer.valueOf(request.getParameter("serverId"));
			int channelId = Integer.valueOf(request.getParameter("channelId"));
			String roleName = URLDecoder.decode(
					request.getParameter("roleName"), "UTF-8");
			String activeCode = request.getParameter("activeCode");
			long timestamp = Long.valueOf(request.getParameter("timestamp"));
			String sign = request.getParameter("sign");
			if (appId <= 0 || serverId <= 0 || channelId <= 0
					|| Util.isEmpty(activeCode) || Util.isEmpty(roleName)
					|| timestamp <= 0 || Util.isEmpty(sign)) {
				resp.setState(OpenState.PARAM_ERROR);
				resp.setMsg("parameter error");
				return resp;
			}
			// 验证签名
			String secretkey = GameContext.getOpenSecretkeyConfig()
					.getSecretkey(channelId);
			String signature = this.getSignature(appId, channelId, serverId,
					roleName, activeCode, timestamp, secretkey);
			if (!signature.equals(sign)) {
				resp.setState(OpenState.SIGN_FAIL);
				resp.setMsg("signture fail");
				return resp;
			}
			RoleInstance role = GameContext.getUserRoleApp().getRoleByRoleName(
					roleName);
			if (null == role) {
				resp.setState(OpenState.ROLE_NAME_NOT_EXIST);
				resp.setMsg("roleName error");
				return resp;
			}
			Result result = GameContext.getGiftCodeApp().takeCdkey(role,
					activeCode);
			if (!result.isSuccess()) {
				resp.setState(OpenState.CDKEY_TAKE_FAIL);
				resp.setMsg(result.getInfo());
				return resp;
			}
			resp.setState(OpenState.SUCCESS);
			resp.setMsg("success");
			return resp;
		} catch (Exception e) {
			this.logger.error(this.getClass().getName()
					+ ".getOpenRoleInfoResp error: ", e);
			resp.setState(OpenState.ERROR);
			resp.setMsg("error");
			return resp;
		}
	}
	
	/**
	 * 签名算法
	 * 按参数名的首字母顺序排序，将参数值用#连接
	 * 拼接的字符串按约定加密方式(MD5)加密
	 * @param appId
	 * @param channelId
	 * @param serverId
	 * @param roleName
	 * @param activeCode
	 * @param timestamp
	 * @param secretkey
	 * @return
	 */
	private String getSignature(int appId, int channelId, int serverId, String roleName, String activeCode, long timestamp, String secretkey){
		TreeMap<String,String> paramMap = new TreeMap<String,String>();
		paramMap.put("appId", String.valueOf(appId));
		paramMap.put("channelId", String.valueOf(channelId));
		paramMap.put("serverId", String.valueOf(serverId));
		paramMap.put("roleName", roleName);
		paramMap.put("activeCode", activeCode);
		paramMap.put("timestamp", String.valueOf(timestamp));
		StringBuffer buffer = new StringBuffer();
		String pound = "";
		for(String var : paramMap.values()){
			buffer.append(pound).append(var);
			pound = "#";
		}
		buffer.append(pound);
		buffer.append(secretkey);
		return GameContext.md5.getMD5(buffer.toString());
	}
	
}
