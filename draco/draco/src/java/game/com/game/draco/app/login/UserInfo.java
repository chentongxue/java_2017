package com.game.draco.app.login;

import java.util.Date;

import lombok.Data;

/**
 * 
 *
 */
public @Data class UserInfo {

	private int currRoleId = 0  ;
	private String userId ;
	private int regChannelId ;
	private int loginChannelId ;
	private String userName ;
	private String resType ;
	private Date userRegTime ;
	private String channelUserId ;
	private String channelUserName ;
	private String channelAccessToken ;
	private String channelRefreshToken ;
	private String loginOsType ;
	private int createdRoleNum ;
	private int loginServerId ;
	
}
