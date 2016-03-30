package sacred.alliance.magic.open.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.channel.http.HttpContext;
import sacred.alliance.magic.channel.http.service.SimpleHttpService;
import sacred.alliance.magic.open.constant.OpenResult;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.vo.RoleInstance;

import com.alibaba.fastjson.JSON;
import com.game.draco.GameContext;
import com.mofun.open.base.state.StateCode;
import com.mofun.open.message.request.CdkeyTakeReqMessage;
import com.mofun.open.message.response.DefaultRespMessage;

public class OpenCdkeyTakeHttpService extends SimpleHttpService{
	private static final Logger logger = LoggerFactory.getLogger(OpenCdkeyTakeHttpService.class);

	private String encoding = "UTF-8" ;
	
	@Override
	public String getStringBody(HttpContext context) {
		
		try{
			String json = new String(context.getRequest().getBody(),encoding) ;
			CdkeyTakeReqMessage message = JSON.parseObject(json, CdkeyTakeReqMessage.class);
			if(null == message){
				return null ;
			}
			String roleName = message.getRoleName();
			String activeCode = message.getActiveCode();
			if(roleName != null && activeCode != null){
				DefaultRespMessage defaultRespMessage = openTakeCdkey(roleName, activeCode);
				return JSON.toJSONString(defaultRespMessage);
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private DefaultRespMessage openTakeCdkey(String roleName, String activeCode) {
		try {
			OpenResult result = takeCdkey(roleName,activeCode);
			StateCode stateCode = result.getStateCode();
			String msg = result.getInfo();
			if(Util.isEmpty(msg)){
				msg = stateCode.getMsg();
			}
			DefaultRespMessage resp = new DefaultRespMessage();
			resp.setState(stateCode.getState());
			resp.setMsg(msg);
			return resp;
		} catch (Exception e) {
			return null;
		}
	}
	
	private OpenResult takeCdkey(String roleName,String activeCode){
		OpenResult result = new OpenResult();
		try {
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
