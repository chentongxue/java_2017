package sacred.alliance.magic.open.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sacred.alliance.magic.channel.http.HttpContext;
import sacred.alliance.magic.channel.http.service.SimpleHttpService;
import sacred.alliance.magic.vo.RoleInstance;

import com.alibaba.fastjson.JSON;
import com.game.draco.GameContext;
import com.mofun.open.base.state.StateCode;
import com.mofun.open.message.request.GameRoleInfoReqMessage;
import com.mofun.open.message.response.GameRoleInfoRespMessage;

public class OpenRoleInfoHttpService extends SimpleHttpService{
	private static final Logger logger = LoggerFactory.getLogger(OpenRoleInfoHttpService.class);

	private String encoding = "UTF-8" ;
	
	@Override
	public String getStringBody(HttpContext context) {
		try{
			String json = new String(context.getRequest().getBody(),encoding) ;
			GameRoleInfoReqMessage message = JSON.parseObject(json, GameRoleInfoReqMessage.class);
			if(null == message){
				return null ;
			}
			String roleName = message.getRoleName();;
			String channelUid = message.getChannelUid();;
			if(roleName != null && channelUid != null){
				GameRoleInfoRespMessage roleInfoRespMessage = getRoleInfo(roleName,channelUid);
				return JSON.toJSONString(roleInfoRespMessage);
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private GameRoleInfoRespMessage getRoleInfo(String roleName, String channelUid) {
		GameRoleInfoRespMessage resp = new GameRoleInfoRespMessage();
		try{
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
		}catch(Exception e){
			resp.setState(StateCode.RoleName_Not_Exist.getState());
			resp.setMsg(StateCode.RoleName_Not_Exist.getMsg());
			e.printStackTrace();
		}
		return resp;
	}
	
}
