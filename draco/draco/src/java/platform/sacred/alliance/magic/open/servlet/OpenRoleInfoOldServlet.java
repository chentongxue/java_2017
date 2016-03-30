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

import sacred.alliance.magic.open.bean.OpenRoleInfoResp;
import sacred.alliance.magic.open.constant.OpenState;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.vo.RoleInstance;

import com.alibaba.fastjson.JSON;
import com.game.draco.GameContext;

public class OpenRoleInfoOldServlet extends HttpServlet {
	
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
			OpenRoleInfoResp roleInfo = this.getOpenRoleInfoResp(request);
			String respStr = JSON.toJSONString(roleInfo);
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
	
	private OpenRoleInfoResp getOpenRoleInfoResp(HttpServletRequest request){
		OpenRoleInfoResp info = new OpenRoleInfoResp();
		try {
			int appId = Integer.valueOf(request.getParameter("appId"));
			int serverId = Integer.valueOf(request.getParameter("serverId"));
			int channelId = Integer.valueOf(request.getParameter("channelId"));
			String channelUid = request.getParameter("channelUid");
			String roleName = URLDecoder.decode(request.getParameter("roleName"), "UTF-8");
			long timestamp = Long.valueOf(request.getParameter("timestamp"));
			String sign = request.getParameter("sign");
			if(appId <=0 || serverId <= 0 || channelId <= 0 || Util.isEmpty(channelUid) || Util.isEmpty(roleName) || timestamp <= 0 || Util.isEmpty(sign)){
				info.setState(OpenState.PARAM_ERROR);
				info.setMsg("parameter error");
				return info;
			}
			//验证签名
			String secretkey = GameContext.getOpenSecretkeyConfig().getSecretkey(channelId);
			String signature = this.getSignature(appId, channelId, serverId, channelUid, roleName, timestamp, secretkey);
			if(!signature.equals(sign)){
				info.setState(OpenState.SIGN_FAIL);
				info.setMsg("signture fail");
				return info;
			}
			RoleInstance role = GameContext.getUserRoleApp().getRoleByRoleName(roleName);
			if(null == role){
				info.setState(OpenState.ROLE_NAME_NOT_EXIST);
				info.setMsg("roleName error");
				return info;
			}
			if(!role.getChannelUserId().equals(channelUid)){
				info.setState(OpenState.ROLE_NAME_CHANNELUID_NOT_MATCH);
				info.setMsg("roleName or channelUid error");
				return info;
			}
			info.setState(OpenState.SUCCESS);
			info.setMsg("success");
			info.setRoleId(role.getRoleId());
			info.setRoleName(role.getRoleName());
			info.setSex(role.getSex());
			info.setCamp(role.getCampId());
			info.setCareer(role.getCareer());
			info.setLevel(role.getLevel());
			return info;
		} catch (Exception e) {
			this.logger.error(this.getClass().getName() + ".getOpenRoleInfoResp error: ", e);
			info.setState(OpenState.ERROR);
			info.setMsg("error");
			return info;
		}
	}
	
	/**
	 * 签名算法
	 * 按参数名的首字母顺序排序，将参数值用#连接
	 * 拼接的字符串按约定加密方式(MD5)加密
	 * @param appId
	 * @param channelId
	 * @param serverId
	 * @param channelUid
	 * @param roleName
	 * @param timestamp
	 * @param secretkey
	 * @return
	 */
	private String getSignature(int appId, int channelId, int serverId, String channelUid, String roleName, long timestamp, String secretkey){
		TreeMap<String,String> paramMap = new TreeMap<String,String>();
		paramMap.put("appId", String.valueOf(appId));
		paramMap.put("channelId", String.valueOf(channelId));
		paramMap.put("serverId", String.valueOf(serverId));
		paramMap.put("channelUid", channelUid);
		paramMap.put("roleName", roleName);
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
