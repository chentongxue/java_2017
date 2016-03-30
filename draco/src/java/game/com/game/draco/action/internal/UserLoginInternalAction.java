package com.game.draco.action.internal;

import java.net.InetSocketAddress;
import java.util.List;

import org.apache.mina.core.session.IoSession;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.app.onlinecenter.OnlineCenter;
import sacred.alliance.magic.app.token.AccountToken;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.channel.mina.MinaChannelSession;
import sacred.alliance.magic.constant.Status;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.core.channel.ChannelSession;
import sacred.alliance.magic.util.RandomUtil;
import sacred.alliance.magic.util.SessionUtil;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.login.UserInfo;
import com.game.draco.message.internal.C0056_UserLoginInternalMessage;
import com.game.draco.message.push.C0108_ForcedExitMessage;
import com.game.draco.message.request.C4999_UserLoginSafeReqMessage;
import com.game.draco.message.response.C4999_UserLoginSafeRespMessage;
import com.google.common.collect.Lists;

public class UserLoginInternalAction extends BaseAction<C0056_UserLoginInternalMessage>{
	
	private RoleInstance getCurrServerRole(List<RoleInstance> roleList,int serverId){
		if(1 == roleList.size()){
			return roleList.get(0) ;
		}
		for(RoleInstance role : roleList){
			if(role.getCreateServerId() == serverId){
				return role ;
			}
		}
		return roleList.get(0) ;
	}

	@Override
	public Message execute(ActionContext context, C0056_UserLoginInternalMessage internalMsg) {
		C4999_UserLoginSafeRespMessage respMsg = new C4999_UserLoginSafeRespMessage();
		respMsg.setType(Status.Role_FAILURE.getInnerCode());
		respMsg.setInfo(Status.Role_FAILURE.getTips());
		try {
			//!!!!判断iosession是否链接,否则不执行
			ChannelSession session = context.getSession();
			IoSession ioSession = ((MinaChannelSession)session).getIoSession();
			if(null == ioSession || !ioSession.isConnected()){
				return respMsg ;
			}
			AccountToken accountToken = internalMsg.getAccountToken();
			C4999_UserLoginSafeReqMessage reqMsg = internalMsg.getUserReqMsg();
			String userId = internalMsg.getUserId();
			long currentIoId = ioSession.getId();
			
			OnlineCenter onlineCenter = GameContext.getOnlineCenter();
			Long oldIo = onlineCenter.getIoId(userId);
			if(null != oldIo){
				//判断io是否存在
				IoSession oldIoSession = GameContext.getMinaServer().getAcceptor().getManagedSessions().get(oldIo);
				if(null != oldIoSession){
					//已经有登录的情况,必须将前一链接断开
					GameContext.getOnlineCenter().closeIoSession(oldIo);
					// 重复登陆
					respMsg.setInfo(Status.Role_Login_OffLine.getTips());
					return respMsg;
				}else{
					//io其实已经不存在了,本次用户是可以继续登录操作的
					onlineCenter.removeIoId(userId);
				}
			}
			
			//验证同一IP登陆数量
			String ipInfo = "";
			InetSocketAddress remoteAddr = (InetSocketAddress)(ioSession.getRemoteAddress());
			if(null != remoteAddr ){
				ipInfo = remoteAddr.getAddress().getHostAddress();
			}
			Result result = GameContext.getDoorDogApp().canUserLogin(ipInfo);
			if(!result.isSuccess()){
				C0108_ForcedExitMessage exitResp = new C0108_ForcedExitMessage();
				exitResp.setInfo(result.getInfo());
				ioSession.write(exitResp).awaitUninterruptibly(1000);
				ioSession.close(true);
				return null;
			}
			
			int createdRoleNum = 0 ;
			// 获得角色相关信息
			List<RoleInstance> allRoleList = GameContext.getRoleService().selectAllByUserId(userId) ;
			//当前服务器的角色
			List<RoleInstance> currServerRoleList = null ;
			int loginServerId = Integer.parseInt(reqMsg.getServerId());
			
			if(GameContext.getEnvConfig().isCloseServerIdLimit()){
				//关闭则取全部
				currServerRoleList = allRoleList ;
			}else if(!Util.isEmpty(allRoleList)){
				//只取当前服务器
				currServerRoleList = Lists.newArrayList() ;
				for(RoleInstance it : allRoleList){
					if(it.getCreateServerId() != loginServerId){
						continue ;
					}
					currServerRoleList.add(it);
				}
			}
			
			if (!Util.isEmpty(currServerRoleList)){
				RoleInstance role = this.getCurrServerRole(currServerRoleList, loginServerId);
				respMsg.setRoleId(role.getIntRoleId());
				createdRoleNum = currServerRoleList.size();
			}else{
				//可选英雄
				respMsg.setHeroList(GameContext.getRoleBornApp().getBornHeroInfoList());
			}
			
			boolean encryptionFlag = GameContext.getParasConfig().getEncryptionFlag();
			// 获得randomKey
			// 随机数为0时不进行加密
			int randomInt = 0;
			if(encryptionFlag){
				randomInt = RandomUtil.randomInt(1, 255);
			}
			
			GameContext.getMinaSecurity().setRandomKey(
					((MinaChannelSession) session).getIoSession(),
					(byte) randomInt);
			respMsg.setRandomKey((byte) randomInt);
			
			//!!! 放到最后
			//将此用户ID,IO放入在线中心
			UserInfo userInfo = new UserInfo();
			//服务器id
			userInfo.setLoginServerId(loginServerId);
			//用户ID
			userInfo.setUserId(userId);
			//用户名
			userInfo.setUserName(userId);// 用户中心没有userName, 用userId代替
			//注册渠道ID
			userInfo.setRegChannelId(accountToken.getChannelId());
			//资源类型
			userInfo.setResType(reqMsg.getResType());
			userInfo.setUserRegTime(accountToken.getCreateTime());
			//登录渠道ID
			userInfo.setLoginChannelId(reqMsg.getChannelId());
			//渠道用户ID
			userInfo.setChannelUserId(accountToken.getChannelUserId());
			//渠道访问token
			userInfo.setChannelAccessToken(accountToken.getChannelAccessToken());
			//渠道刷新token
			userInfo.setChannelRefreshToken(accountToken.getChannelRefreshToken());
			userInfo.setLoginOsType(String.valueOf(reqMsg.getEmulatorType()));
			userInfo.setCreatedRoleNum(createdRoleNum);
			session.setAttribute(SessionUtil.USER_INFO_KEY, userInfo);
			
			onlineCenter.addIoId(userId, currentIoId);
			//协议版本号
			respMsg.setProtoVersion(GameContext.PROTO_VERSION);
			respMsg.setType(Status.Role_SUCCESS.getInnerCode());
			respMsg.setInfo(Status.Role_SUCCESS.getTips());
			return respMsg;
		} catch (Exception ex) {
			logger.error("",ex);
		}
		return respMsg;
	}
	
	
	

}
