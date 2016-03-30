package sacred.alliance.magic.action;

import java.net.InetSocketAddress;
import java.util.Date;

import org.apache.mina.core.session.IoSession;

import sacred.alliance.magic.app.config.ParasConfig;
import sacred.alliance.magic.app.set.PublicSetApp;
import sacred.alliance.magic.base.GenderType;
import sacred.alliance.magic.channel.mina.MinaChannelSession;
import sacred.alliance.magic.constant.ParasConstant;
import sacred.alliance.magic.constant.Status;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.core.channel.ChannelSession;
import sacred.alliance.magic.service.RoleService;
import sacred.alliance.magic.util.CheckNameUtil;
import sacred.alliance.magic.util.SessionUtil;
import sacred.alliance.magic.util.StringUtil;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.util.Wildcard;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.login.UserInfo;
import com.game.draco.message.request.C0103_RoleCreateReqMessage;
import com.game.draco.message.response.C0103_RoleCreateRespMessage;

public class RoleCreateAction extends BaseAction<C0103_RoleCreateReqMessage> {
	
	private final byte DEFAULT_CAMP_ID = (byte)-1 ;
	
	private byte getCampId(){
		//正式返回-1
		return DEFAULT_CAMP_ID ;
		//暂时随机
		//int campId = CampType.getRealCampNum();
		//return (byte)RandomUtil.randomInt(campId);
	}
	
	@Override
	public Message execute(ActionContext context, C0103_RoleCreateReqMessage req) {
		C0103_RoleCreateRespMessage resp = new C0103_RoleCreateRespMessage();
		resp.setType(Status.Role_FAILURE.getInnerCode());
		try{
			GenderType genderType = GenderType.getType(req.getSex());
			if(null == genderType || genderType.getType() <0){
				resp.setInfo(Status.Role_Sex_Error.getTips());
				return resp;
			}
			String roleName = req.getRoleName();
			if(Util.isEmpty(roleName)){
				resp.setInfo(Status.Role_Name_Null.getTips());
				return resp;
			}
			roleName = StringUtil.replaceNewLine(roleName);
			
			ParasConfig parasConfig = GameContext.getParasConfig();
			PublicSetApp cpbs = GameContext.getPublicSetApp();
			String info = Status.Role_Create_Name_In_Char.getTips()
							.replace(Wildcard.MinNum, String.valueOf(parasConfig.getMinRoleName()))
							.replace(Wildcard.MaxNum, String.valueOf(cpbs.getMaxRoleNameSize()));
			if(GameContext.getIllegalWordsService().isLow(roleName, parasConfig.getMinRoleName())){
				resp.setInfo(info);
				return resp;
			}
			if(GameContext.getIllegalWordsService().isExceed(roleName, cpbs.getMaxRoleNameSize())){
				resp.setInfo(info);
				return resp;
			}
			
			String illegalChar = GameContext.getIllegalWordsService().findIllegalChar(roleName);
			if(null != illegalChar){
				resp.setInfo(Status.Role_Create_Name_Illegal_Char.getTips() + illegalChar);
				return resp;
			}
			String forbidChar = GameContext.getIllegalWordsService().findForbiddenChar(roleName);
			if(null != forbidChar){
				resp.setInfo(Status.Role_Create_Name_Forbid_Char.getTips() + forbidChar);
				return resp;
			}
			//判断是否有@#号等特殊字符
			if(StringUtil.haveSpecialChar(roleName)){
				resp.setInfo(Status.Role_Create_Name_Has_Illegal_Char.getTips());
				return resp;
			}
			// 判断是否有以s/S+数字以尾
			if(CheckNameUtil.isMatchChangeName(roleName)){
				resp.setInfo(Status.Role_Create_Name_Has_Illegal_Char.getTips());
				return resp;
			}
			
			RoleService roleService = GameContext.getRoleService();
			int count = roleService.sameName(roleName);
			if(1 <= count){
				resp.setInfo(Status.Role_Exist.getTips());
				return resp;
			}
			
			//判断当前帐号下角色个数
			ChannelSession session = context.getSession();
			UserInfo userInfo = SessionUtil.getUserInfo(session);
			int createdRoleNum = userInfo.getCreatedRoleNum() ;
			if(createdRoleNum >= GameContext.getParasConfig().getMaxRoleNum()){
				//创建角色数目已达上限
				resp.setInfo(Status.Role_Created_Num_Full.getTips());
				return resp ;
			}
			
			//判断客户端输入的英雄id是否合法
			int heroId = req.getHeroId() ;
			if(!GameContext.getRoleBornApp().isBornHero(heroId)){
				resp.setInfo(this.getText(TextId.ERROR_INPUT));
				return resp ;
			}
			RoleInstance role = new RoleInstance();
			role.setRoleName(roleName);
			role.setUserId(userInfo.getUserId());
			role.setUserName(userInfo.getUserName());
			role.setSex(genderType.getType());
			role.setCampId(this.getCampId());
			role.setBackpackCapacity(ParasConstant.ROLE_BACKPACK_DEF_NUM);
			role.setLastLoginTime(new Date());
			//角色创建的服务器ID
			role.setCreateServerId(userInfo.getLoginServerId());
			
			roleService.initRole(role,userInfo.getRegChannelId(),heroId);
			
			resp.setType(Status.Role_SUCCESS.getInnerCode());
			resp.setRoleId(role.getIntRoleId());
			
			//设置渠道ID
			role.setRegChannelId(userInfo.getRegChannelId());
			role.setUserRegTime(userInfo.getUserRegTime());
			
			//日志
			GameContext.getStatLogApp().roleRegisterLog(role, createdRoleNum);
			
			//日志
			String ip = "";
			IoSession ioSession = ((MinaChannelSession) session).getIoSession();
			if (null != ioSession && ioSession.isConnected()) {
				InetSocketAddress remoteAddr = (InetSocketAddress)(ioSession.getRemoteAddress());
				if(null != remoteAddr){
					ip = remoteAddr.getAddress().getHostAddress();
				}
			}
			GameContext.getLogApp().activeLog(role, createdRoleNum, ip);
			
			try {
				// 将角色数重新写回session
				userInfo.setCreatedRoleNum(userInfo.getCreatedRoleNum()+1);
			} catch (Exception ex) {
				logger.error("", ex);
			}
			return resp;
		}catch(Exception e){
			logger.error("role create error",e);
			resp.setInfo(Status.Role_FAILURE.getTips());
			return resp;
		}
	}
	
}
