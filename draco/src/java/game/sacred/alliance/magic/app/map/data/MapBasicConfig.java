package sacred.alliance.magic.app.map.data;

import lombok.Data;

public @Data class MapBasicConfig {

	private String mapId ;
	private String mapName ;
	private short smallMapResId ;
	private int minTransLevel ;
	private int maxTransLevel ;
	private byte weather ;
	private short weatherTimes ;
	private byte showExit ; //是否显示出口
	private byte npcPK = 0 ;
	private int broadcastAllMax = 0;
	private byte roleCanPK = 0;
	/**
	 * 不同公会是否可PK
	 */
	private byte diffUnionCanPK = 0 ;
	/**
	 * 是否允许切换英雄
	 */
	private byte switchHero  ;
	/**
	 * 是否允许使用食物类型物品
	 */
	private byte useFood  ;
	/**
	 * 是否非当前英雄恢复hp
	 */
	private byte hpHealth ;
	/**
	 * 切换上阵的3英雄
	 */
	private byte canChange3Hero = 1 ;
	/**
	 * 是否可挂机,默认可挂机
	 */
	private byte canHook = 1 ;
	/**
	 * 是否可骑马
	 */
	private byte canOnHorse = 1 ;
	/**
	 * 是否可快捷语音
	 */
	private byte canQuickVoice ;
	/**
	 * 地图支持最大人数（-1：不受人数限制）
	 */
	private int maxRoleCount = -1;
	/**
	 * 要求进入角色等级
	 */
	private int roleLevelLimit ;
	/**
	 * 是否可原地复活
	 */
	private byte canSituReborn ;
	/**
	 * 是否可灵魂复活
	 */
	private byte canSoulReborn ;
	/**
	 * 是否显示任务
	 */
	private byte canShowQuest = 1;
	/**
	 * 是否显示队伍
	 */
	private byte canShowTeam = 0 ;
	/**
	 * 是否允许切磋PK
	 */
	private byte canLearnPk ;
	/**
	 * 默认的语音聊天频道
	 */
	private byte voiceChannelType ;
	
	/**
	 * 
	 * -1 代表没有
	 */
	private byte mapMusic = -1 ;

    private byte showWorldMap = 1 ;

    private byte showMenu = 1 ;

    private byte showFriendNpcHp ;
	
}
