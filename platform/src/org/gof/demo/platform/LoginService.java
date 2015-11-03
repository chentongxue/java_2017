package org.gof.demo.platform;

import org.apache.commons.lang3.StringUtils;
import org.gof.core.Port;
import org.gof.core.Service;
import org.gof.core.gen.proxy.DistrClass;
import org.gof.core.gen.proxy.DistrMethod;
import org.gof.core.support.log.LogCore;
import org.gof.demo.platform.sdk.Login;

@DistrClass
public class LoginService extends Service {
	
	public LoginService(Port port) {
		super(port);
	}

	@Override
	public Object getId() {
		return C.SERV_LOGIN;
	}
	
	/**
	 * 登陆验证
	 * @param loginJSON
	 */
	@DistrMethod
	public void check(String userIdentity, String token) {
		
		LogCore.core.info("Account:[" + userIdentity + "];  Token:[" + token+"].");
		
		//如果token为空，就正确
		if(StringUtils.isEmpty(token)){
			port.returns(true);
			return ;
		}
		
		//如果账号为空，直接返回
		if(StringUtils.isEmpty(userIdentity)){
			port.returns(false);
			return ;
		}
		
		int check = Login.checkToken(userIdentity, token);
		port.returns(check == Login.SUCCESS);
		
	}
}
