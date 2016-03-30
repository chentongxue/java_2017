package sacred.alliance.magic.open.servlet;

import java.io.IOException;
import java.net.URLDecoder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.open.constant.OpenResult;
import sacred.alliance.magic.util.HttpUtil;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.vo.RoleInstance;

import com.alibaba.fastjson.JSON;
import com.game.draco.GameContext;
import com.mofun.open.base.state.StateCode;
import com.mofun.open.message.request.CdkeyTakeReqMessage;
import com.mofun.open.message.response.DefaultRespMessage;

public class OpenCdkeyTakeServlet extends BaseHttpServlet {
	
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		try {
			request.setCharacterEncoding("UTF-8");
			String postData = HttpUtil.readPostData(request);
			String decode_body = URLDecoder.decode(postData, "UTF-8");
			CdkeyTakeReqMessage reqMsg = JSON.parseObject(decode_body, CdkeyTakeReqMessage.class);
			this.execute(reqMsg, response);
		} catch (Exception e) {
			this.logger.error(this.getClass().getName() + ".doPost error: ", e);
		}
	}
	
	private void execute(CdkeyTakeReqMessage reqMsg, HttpServletResponse response){
		try {
			OpenResult result = this.takeCdkey(reqMsg);
			StateCode stateCode = result.getStateCode();
			String msg = result.getInfo();
			if(Util.isEmpty(msg)){
				msg = stateCode.getMsg();
			}
			DefaultRespMessage resp = new DefaultRespMessage();
			resp.setState(stateCode.getState());
			resp.setMsg(msg);
			this.output(response, JSON.toJSONString(resp));
		} catch (Exception e) {
			this.logger.error(this.getClass().getName() + ".execute error: ", e);
		}
	}
	
	private OpenResult takeCdkey(CdkeyTakeReqMessage reqMsg){
		OpenResult result = new OpenResult();
		try {
			String roleName = reqMsg.getRoleName();
			String activeCode = reqMsg.getActiveCode();
			RoleInstance role = GameContext.getUserRoleApp().getRoleByRoleName(roleName);
			if(null == role){
				return result.failure(StateCode.RoleName_Not_Exist);
			}
			Result take_result = GameContext.getGiftCodeApp().takeCdkey(role,activeCode);
			if(!take_result.isSuccess()){
				return result.failure(StateCode.Cdkey_Take_Fail, take_result.getInfo());
			}
			return result.success();
		} catch (Exception e) {
			this.logger.error(this.getClass().getName() + ".getOpenRoleInfoResp error: ", e);
			return result.failure();
		}
	}
	
}
