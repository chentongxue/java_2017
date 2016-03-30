package sacred.alliance.magic.filter;

import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sacred.alliance.magic.app.config.SetConfig;
import sacred.alliance.magic.app.doordog.RoleDoorDogInfo;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.core.channel.ChannelSession;
import sacred.alliance.magic.core.filter.Filter;
import sacred.alliance.magic.core.filter.FilterChain;
import sacred.alliance.magic.util.SessionUtil;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.message.internal.InternalMessage;
import com.game.draco.message.push.C0003_TipNotifyMessage;




public class PopedomFilter implements Filter{
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private String nonFilterCommand = "";
	private Set<String> commandSet = new HashSet<String>();
	
	public void setNonFilterCommand(String nonFilterCommand) {
		this.nonFilterCommand = nonFilterCommand;
	}

	public void doFilter(ActionContext context, FilterChain chain)
			throws Exception {
		Message message = context.getMessage();
		if(!this.isFilter(message.getCommandId())){
			//不需要验证权限指令
			chain.doFilter(context);
			return ;
		}
		if(message instanceof InternalMessage){
			//内部命令,不需要验证权限
			chain.doFilter(context);
			return ;
		}
		String userId = this.getUserId(context);
		if(Util.isEmpty(userId)){
			//没有通过登录
			return ;
		}
		//判断验证码功能是否开启
		if(!GameContext.getParasConfig().isOpenDoorDog()){
			//没有开启
			chain.doFilter(context);
			return ;
		}
		//开启判断是否是需要验证的cmd
		if(!this.isCaptchaCmd(message)){
			//不需要验证
			chain.doFilter(context);
			return ;
		}
		//判断是否已经通过严重
		RoleInstance role = GameContext.getOnlineCenter().getRoleInstanceByUserId(userId);
		if(null == role){
			return ;
		}
		RoleDoorDogInfo doorInfo = GameContext.getDoorDogApp().getRoleDoorDogInfo(role.getRoleId());
		if(null != doorInfo && !doorInfo.isPassDoorDog()){
			//告知未通过验证码功能不允许使用此功能
			C0003_TipNotifyMessage msg = new C0003_TipNotifyMessage();
			msg.setMsgContext(GameContext.getI18n().getText(TextId.FORBID_FOR_NOT_PASS_DOORDOG_MESSAGE));
			context.getSession().write(msg);
			return ;
		}
		if(!this.passFlagToVerifyCheck(role)){
			//重新发送验证码
			boolean sendSuccess = GameContext.getDoorDogApp().flagToVerify(role, (byte)-1);
			if(sendSuccess){
				return ;
			}
			//发送验证码没有成功，只有让用户通过
		}
		//通过验证码的,需要判断是否需要再次发送
		chain.doFilter(context);
	}
	
	private boolean passFlagToVerifyCheck(RoleInstance role){
		if(!GameContext.getParasConfig().isOpenCaptchaAtFunc()){
			return true ;
		}
		RoleDoorDogInfo doorInfo = GameContext.getDoorDogApp().getRoleDoorDogInfo(role.getRoleId());
		//判断验证通过次数
		if(null != doorInfo && doorInfo.getPassDoorDogTimes() > 0){
			return true ;
		}
		return GameContext.getDoorDogApp().isWhiteRole(role);
	}
	
	
	private boolean isCaptchaCmd(Message message){
		SetConfig captchaConfig = GameContext.getCaptchaCmdConfig() ;
		if(null == captchaConfig){
			return false ;
		}
		Set<String> set = captchaConfig.getValue();
		if(null == set){
			return false ;
		}
		return set.contains(String.valueOf(message.getCommandId()));
	}
	
	private String getUserId(ActionContext context){
		ChannelSession session = context.getSession();
		return SessionUtil.getUserId(session);
	}
	
	private boolean isFilter(int commandId) {
		/*if (("," + nonFilterCommand + ",").indexOf("," + String.valueOf(commandId) + ",") > -1) {
			return false;
		}
		return true;*/
		return !this.commandSet.contains(String.valueOf(commandId));
	}

	public void init() {
		commandSet.clear();
		String[] strs = this.nonFilterCommand.split(",");
		if(null == strs){
			return ;
		}
		for(String s:strs){
			if(null ==s || 0 == s.trim().length()){
				continue ;
			}
			this.commandSet.add(s.trim());
		}
	}
	
	public void destroy() {
		
	}

}
