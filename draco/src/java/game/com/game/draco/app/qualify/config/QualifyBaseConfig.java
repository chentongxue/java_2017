package com.game.draco.app.qualify.config;

import java.util.Date;
import java.util.List;

import lombok.Data;
import sacred.alliance.magic.constant.Cat;
import sacred.alliance.magic.util.DateUtil;
import sacred.alliance.magic.util.Log4jManager;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.util.Wildcard;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.app.chat.ChannelType;
import com.game.draco.base.CampType;
import com.google.common.collect.Lists;

public @Data class QualifyBaseConfig {
	
	private String shopId;
	private byte challengeTimes;// 每天可挑战次数
	private int cooldownTime;// 挑战冷却时间单位秒
	private String firstGiveGiftTime;// 第一次发奖时间
	private String scendGiveGiftTime;// 第二次发奖时间
	private String mapId;
	private int mapX;
	private int mapY;
	private int targetMapX;
	private int targetMapY;
	private String rankDesc;// 玩法说明
	private String broadcastInfo;// 世界广播
	
	// 不是从配置文件中获取
	private byte firstGiftHour;
	private byte firstGiftMinutes;
	private byte scendGiftHour;
	private byte scendGiftMinutes;
	
	public void init(String fileInfo) {
		if (this.challengeTimes <= 0) {
			this.checkFail(fileInfo + "challengeTimes is config error!");
		}
		if (this.cooldownTime <= 0) {
			this.checkFail(fileInfo + "cooldownTime is config error!");
		}
		this.initGiftTime();
	}
	
	// 初始化发奖时间
	private void initGiftTime() {
		String[] infos = Util.splitStr(firstGiveGiftTime, Cat.colon);
		if (infos.length >= 2) {
			this.firstGiftHour = Byte.parseByte(infos[0]);
			this.firstGiftMinutes = Byte.parseByte(infos[1]);
			infos = Util.splitStr(scendGiveGiftTime, Cat.colon);
			this.scendGiftHour = Byte.parseByte(infos[0]);
			this.scendGiftMinutes = Byte.parseByte(infos[1]);
		}
	}
	
	public String getNextGiveGiftTime() {
		int nowHour = DateUtil.getHour(new Date());
		StringBuffer buffer = new StringBuffer();
		if (nowHour < this.firstGiftHour || nowHour > this.scendGiftHour) {
			buffer.append(this.firstGiftHour);
			if (this.firstGiftMinutes > 0) {
				buffer.append(Cat.colon).append(this.firstGiftMinutes);
			}
			return buffer.toString();
		}
		buffer.append(this.scendGiftHour);
		if (this.scendGiftMinutes > 0) {
			buffer.append(Cat.colon).append(this.scendGiftMinutes);
		}
		return buffer.toString();
	}
	
	private void checkFail(String info) {
		Log4jManager.CHECK.error(info);
		Log4jManager.checkFail();
	}
	
	/**
	 * 获得配置中的发放奖励时间，Spring框架格式
	 * @return
	 */
	public List<String> getCronExpressionList(){
		if (Util.isEmpty(this.firstGiveGiftTime) || Util.isEmpty(this.scendGiveGiftTime)) {
			return null;
		}
		String weekExpress = "* * ?";// 每天都执行
		List<String> list = Lists.newArrayList();
		// 第一次发奖时间
		StringBuffer cronExpress = new StringBuffer();
		cronExpress.append("0").append(Cat.blank).append(this.firstGiftMinutes).append(Cat.blank).append(this.firstGiftHour).append(Cat.blank).append(weekExpress);
		list.add(cronExpress.toString());
		// 第二次发奖时间
		cronExpress = new StringBuffer();
		cronExpress.append("0").append(Cat.blank).append(this.scendGiftMinutes).append(Cat.blank).append(this.scendGiftHour).append(Cat.blank).append(weekExpress);
		list.add(cronExpress.toString());
		return list;
	}
	
	public String getBroadcastTips(RoleInstance role, int targRank) {
		// 如果该等级没有强化广播
		if (Util.isEmpty(this.broadcastInfo)) {
			return "";
		}
		// 根据玩家阵营赋予玩家名称不同颜色
		String roleName = Util.getColorRoleName(role, ChannelType.Publicize_Personal);
		// 名次信息
		String rankInfo = String.valueOf(targRank) + Util.getColor(ChannelType.Publicize_Personal.getColor());
		return this.broadcastInfo.replace(Wildcard.Role_Name, roleName).replace(Wildcard.Number, rankInfo);
	}
	
}
