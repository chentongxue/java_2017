package com.game.draco.app.chat;

import java.io.Serializable;
import java.util.List;

import com.game.draco.app.AppSupport;
import com.game.draco.app.chat.config.ChatLimitConfig;
import com.game.draco.app.npc.domain.NpcInstance;
import com.game.draco.message.item.ChatContextItem;

import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.Service;
import sacred.alliance.magic.vo.RoleEntity;
import sacred.alliance.magic.vo.RoleInstance;

public interface ChatApp extends Service, AppSupport {
	
	/**
	 * 发布聊天消息
	 * @param role
	 * @param channelType
	 * @param message
	 * @param contextList
	 * @param targetRole
	 * @return
	 */
	public Result sendMessage(RoleEntity speaker, ChannelType channelType,
			String message, List<ChatContextItem> contextList, RoleInstance targetRole);
	
	/**
	 * 发送系统消息
	 * @param channelType
	 * @param message
	 * @param contextList
	 * @param target
	 * @return
	 */
	public Result sendSysMessage(ChatSysName chatSysName, ChannelType channelType, String message, 
			List<ChatContextItem> contextList, Serializable target);
	
	/**
	 * 
	 * @param tranType
	 * @param srcRoleId
	 * @param srcRoleName
	 * @param targetRoleId
	 * @param targetRoleName
	 * @param itemId
	 * @param itemName
	 * @param itemCount
	 */
	/*public void notifyTransactionToGm(TransactionType tranType,String srcRoleId,String srcRoleName,String targetRoleId,
			String targetRoleName,String itemId,String itemName,int itemCount);*/
	
	/**
	 * NPC喊话
	 * @param npcInstance
	 * @param channelType
	 * @param message
	 * @param contextList
	 * @param target
	 * @return
	 */
	public Result sendSysMessage(NpcInstance npcInstance, ChannelType channelType, String message, 
			List<ChatContextItem> contextList, Serializable target);
	
	/**
	 * 获取聊天限制条件
	 * @param channelType
	 * @return
	 */
	public ChatLimitConfig getChatLimitConfig(ChannelType channelType);
	
	/**
	 * 发送语音消息
	 * @param speaker
	 * @param channelType
	 * @param data
	 * @param targetRole
	 * @return
	 */
	public Result sendVoiceMessage(RoleEntity speaker, ChannelType channelType, byte[] data, RoleInstance targetRole);
	
	/**
	 * 系统发送语音聊天消息
	 * @param chatSysName
	 * @param channelType
	 * @param data
	 * @param target
	 * @return
	 */
	public Result sendSysVoiceMessage(ChatSysName chatSysName, ChannelType channelType, byte[] data, Serializable target);
	
	/**
	 * 判断玩家能否在指定频道说话
	 * 部分玩家可能被禁言
	 * @param speaker
	 * @param channelType
	 * @return
	 */
	public Result canSpeakByForbid(RoleEntity speaker, ChannelType channelType);
	
	/**
	 * 获取聊天频道连接处理信息
	 * @param param
	 * @return
	 */
	public Message getChatShowMessage(String param);
	
}
