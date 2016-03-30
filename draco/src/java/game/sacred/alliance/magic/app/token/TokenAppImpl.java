package sacred.alliance.magic.app.token;

import java.util.TimeZone;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import platform.message.request.C5202_UserTokenVerifyReqMessage;
import platform.message.response.C5202_UserTokenVerifyRespMessage;
import sacred.alliance.magic.base.OsType;
import sacred.alliance.magic.component.id.IdFactory;
import sacred.alliance.magic.constant.Cat;
import sacred.alliance.magic.constant.Status;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.util.Base64Util;
import sacred.alliance.magic.util.RSAUtil;
import sacred.alliance.magic.util.StringUtil;
import sacred.alliance.magic.util.Util;

import com.alibaba.fastjson.JSON;
import com.game.draco.GameContext;
import com.game.draco.message.request.C4999_UserLoginSafeReqMessage;

public class TokenAppImpl implements TokenApp {
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private static final String CAT = "::";
	
	
	/**
	 * !!!! 游戏与客户端登录协议密钥,不要改动
	 */
	private static final String GAME_SECRET_KEY = "5geb8512a1241ff1c06eff3a58fbdafe9b" ;
	
	private boolean isPassProtocolSign(C4999_UserLoginSafeReqMessage reqMsg){
		//判断协议加密
		String sign = reqMsg.getSign();
		if(Util.isEmpty(sign)){
			return false ;
		}
		StringBuilder buffer = new StringBuilder();
		buffer.append(reqMsg.getCommandId()).append(Cat.pound);
		buffer.append(StringUtil.initString(reqMsg.getTokenId())).append(Cat.pound);
		buffer.append(StringUtil.initString(reqMsg.getResType())).append(Cat.pound);
		buffer.append(reqMsg.getEmulatorType()).append(Cat.pound);
		buffer.append(StringUtil.initString(reqMsg.getVersionNumber())).append(Cat.pound);
		buffer.append(reqMsg.getChannelId()).append(Cat.pound);
		buffer.append(reqMsg.getLangType()).append(Cat.pound);
		buffer.append(reqMsg.getProtoVersion()).append(Cat.pound);
		buffer.append(reqMsg.getUnixFormatTime()).append(Cat.pound);
		buffer.append(StringUtil.initString(reqMsg.getServerId())).append(Cat.pound);
		buffer.append(StringUtil.initString(reqMsg.getRemark1())).append(Cat.pound);
		buffer.append(StringUtil.initString(reqMsg.getRemark2())).append(Cat.pound);
		buffer.append(GAME_SECRET_KEY);
		return GameContext.md5.getMD5(buffer.toString()).equals(reqMsg.getSign());
	}
	
	
	@Override
	public TokenResult loginCheck(C4999_UserLoginSafeReqMessage reqMsg){
		TokenResult result = new TokenResult();
		result.failure();
		
		if(!IdFactory.getInstance().hasAllowLogin()){
			result.setInfo(Status.SYS_NO_LOGIN.getTips());
			return result ;
		}
		//如果连接数大于服务器设定的最大人数则提示服务器已满
		int fullSize = GameContext.getAreaServerClient().getServerStatus().getFullSize();
		//用连接数代替在线用户数
		int onlineSize = GameContext.getMinaServer().getAcceptor().getManagedSessionCount();
		if(onlineSize > fullSize){
			result.setInfo(Status.Role_Login_Full_Size.getTips());
			return result;
		}
		//登录
		String tokenId = reqMsg.getTokenId();
        if (null == tokenId || 0 == tokenId.length()) {
            //直接提示失败
        	result.setInfo(Status.Role_Login_Token_Error.getTips());
            return result;
        }
        //判断协议版本
        if(reqMsg.getProtoVersion()<GameContext.PROTO_VERSION){
        	result.setInfo(Status.Role_Login_Proto_Low.getTips());
            return result;
        }
        //根据系统类型，判断是否可登录
        OsType osType = OsType.get(reqMsg.getEmulatorType());
        if(!GameContext.getAreaServerNotifyApp().isAllowLogin(osType)){
        	result.setInfo(GameContext.getI18n().getText(TextId.Role_Login_OsType_Not_Allow));
            return result;
        }
        //判断渠道号是否允许登录
        if(!GameContext.getAreaServerNotifyApp().isAllowChannelLogin(reqMsg.getChannelId())){
        	result.setInfo(GameContext.getI18n().getText(TextId.Role_Operate_Illegal));
        	return result;
        }
        //判断协议加密
        if(!this.isPassProtocolSign(reqMsg)){
        	result.setInfo(GameContext.getI18n().getText(TextId.UserLogin_NotPassProtocolSign));
        	return result;
        }
		return this.loginVerify(tokenId, reqMsg.getResType());
	}
	
	private TokenResult loginVerify(String tokenId, String resType) {
		TokenResult result = new TokenResult();
		try{
			TokenResult verifyRes = this.verifyToken(tokenId);
        	if(verifyRes.isSuccess()){
        		return verifyRes;
        	}
        	if(GameContext.getTokenSecretkeyConfig().isToUcVerifyOpen()){
        		return this.toUserCenterVerify(tokenId, resType);
        	}
    		return result.setInfo(GameContext.getI18n().getText(TextId.Role_Login_Token_Error));
        }catch (Exception e){
        	this.logger.error(this.getClass().getName() + ".loginVerify error: ", e);
        	return result.setInfo(GameContext.getI18n().getText(TextId.Sys_Error));
        }
	}
	
	private TokenResult verifyToken(String tokenId) {
		TokenResult result = new TokenResult();
		try {
			if (Util.isEmpty(tokenId)) {
				return result.failure();
			}
			//使用RSA私钥解密
			String privateKey = GameContext.getTokenSecretkeyConfig().getUcRsaPrivatekey();
			byte[] decryptData = RSAUtil.decryptByPrivateKey(Base64Util.decode(tokenId), privateKey);
			String token = new String(decryptData);
			//截取
			// token = version::jsonStr::md5
			String arrays[] = token.split(CAT);
			if (arrays.length != 3) {
				return result.failure();
			}
			String jsonString = arrays[1];
			String md5Sign = arrays[2];
			// md5 verify
			String signatrue = this.makeMd5Signature(jsonString);
			if (!signatrue.equals(md5Sign)) {
				return result.failure();
			}
			AccountToken userToken = JSON.parseObject(jsonString, AccountToken.class);
			long tokenTime = userToken.getTimeMillis() - userToken.getTimeZone();
			long curTime = System.currentTimeMillis() - TimeZone.getDefault().getRawOffset();
			if (Math.abs(curTime - tokenTime) > GameContext.getTokenSecretkeyConfig().getAppTokenValidTimeMillis()) {
				return result.failure();
			}
			result.setAccountToken(userToken);
			return result.success();
		} catch (Exception e) {
			this.logger.error(this.getClass().getName() + ".verifyToken error: ", e);
			return result.setInfo(GameContext.getI18n().getText(TextId.Role_Login_Token_Expired));
		}
	}
	
	/**
	 * 生成MD5签名
	 * @param tokenStr
	 * @return
	 */
	private String makeMd5Signature(String tokenStr){
		// md5 = jsonStr::key
		String md5Key = GameContext.getTokenSecretkeyConfig().getUcMd5Secretkey();
		return GameContext.md5.getMD5(tokenStr + CAT + md5Key);
	}
	
	/**
	 * 到用户中心验证token
	 * @param tokenId
	 * @param resType
	 * @return
	 */
	private TokenResult toUserCenterVerify(String tokenId, String resType){
		TokenResult result = new TokenResult();
		try{
        	C5202_UserTokenVerifyReqMessage ucReqMsg = new C5202_UserTokenVerifyReqMessage();
            ucReqMsg.setTokenId(tokenId);
            ucReqMsg.setVersion(resType);
            //这个字段用来发产品ID
            ucReqMsg.setGameServerIce(String.valueOf(GameContext.getAppId()));
            C5202_UserTokenVerifyRespMessage ucRespMsg = (C5202_UserTokenVerifyRespMessage)GameContext.getUcClient().sendMessage(ucReqMsg);
    		if(Status.Role_SUCCESS.getInnerCode() != ucRespMsg.getStatus()){
    			//提示过期
    			result.setInfo(Status.Role_Login_Token_Expired.getTips());
    			return result;
    		}
    		AccountToken accountToken = new AccountToken();
    		accountToken.setAppId(GameContext.getAppId());
    		accountToken.setUserId(Integer.parseInt(ucRespMsg.getUserId()));
    		accountToken.setChannelId(ucRespMsg.getChannelId());
    		accountToken.setCreateTime(ucRespMsg.getUserRegTime());
    		accountToken.setChannelUserId(ucRespMsg.getChannelUserId());
    		accountToken.setChannelAccessToken(ucRespMsg.getChannelAccessToken());
    		accountToken.setChannelRefreshToken(ucRespMsg.getChannelRefreshToken());
    		result.setAccountToken(accountToken);
    		return result.success();
        }catch (Exception e){
        	this.logger.error(this.getClass().getName() + ".toUserCenterVerify error: ", e);
        	return result.setInfo(GameContext.getI18n().getText(TextId.Sys_Error));
        }
	}

}
