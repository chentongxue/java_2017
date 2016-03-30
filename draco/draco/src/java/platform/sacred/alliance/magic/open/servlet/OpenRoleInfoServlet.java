package sacred.alliance.magic.open.servlet;

import java.io.IOException;
import java.net.URLDecoder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import sacred.alliance.magic.util.HttpUtil;
import sacred.alliance.magic.vo.RoleInstance;

import com.alibaba.fastjson.JSON;
import com.game.draco.GameContext;
import com.mofun.open.base.state.StateCode;
import com.mofun.open.message.request.GameRoleInfoReqMessage;
import com.mofun.open.message.response.GameRoleInfoRespMessage;

public class OpenRoleInfoServlet extends BaseHttpServlet {
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		this.doPost(request, response);
	}
	
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		try {
			request.setCharacterEncoding("UTF-8");
			String postData = HttpUtil.readPostData(request);
			String decode_body = URLDecoder.decode(postData, "UTF-8");
			GameRoleInfoReqMessage reqMsg = JSON.parseObject(decode_body, GameRoleInfoReqMessage.class);
			GameRoleInfoRespMessage resp = this.getGameRoleInfoRespMessage(reqMsg);
			this.output(response, JSON.toJSONString(resp));
		} catch (Exception e) {
			this.logger.error(this.getClass().getName() + ".doPost error: ", e);
		}
	}
	
	private GameRoleInfoRespMessage getGameRoleInfoRespMessage(GameRoleInfoReqMessage reqMsg){
		GameRoleInfoRespMessage resp = new GameRoleInfoRespMessage();
		try {
			String roleName = reqMsg.getRoleName();
			String channelUid = reqMsg.getChannelUid();
			RoleInstance role = GameContext.getUserRoleApp().getRoleByRoleName(roleName);
			if(null == role){
				resp.setState(StateCode.RoleName_Not_Exist.getState());
				resp.setMsg(StateCode.RoleName_Not_Exist.getMsg());
				return resp;
			}
			if(!role.getChannelUserId().equals(channelUid)){
				resp.setState(StateCode.RoleName_ChannelUid_Not_Match.getState());
				resp.setMsg(StateCode.RoleName_ChannelUid_Not_Match.getMsg());
				return resp;
			}
			resp.setState(StateCode.SUCCESS.getState());
			resp.setMsg(StateCode.SUCCESS.getMsg());
			resp.setRoleId(role.getRoleId());
			resp.setRoleName(role.getRoleName());
			resp.setSex(role.getSex());
			resp.setCamp(role.getCampId());
			resp.setCareer(role.getCareer());
			resp.setLevel(role.getLevel());
			return resp;
		} catch (Exception e) {
			this.logger.error(this.getClass().getName() + ".execute error: ", e);
			resp.setState(StateCode.ERROR.getState());
			resp.setMsg(StateCode.ERROR.getMsg());
			return resp;
		}
	}
	
}
