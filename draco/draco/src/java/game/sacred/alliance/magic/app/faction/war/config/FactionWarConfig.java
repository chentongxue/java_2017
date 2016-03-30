package sacred.alliance.magic.app.faction.war.config;

import java.util.Calendar;
import java.util.Date;

import lombok.Data;
import sacred.alliance.magic.app.map.data.NpcBorn;
import sacred.alliance.magic.constant.Cat;
import sacred.alliance.magic.util.DateUtil;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.vo.Point;

public @Data class FactionWarConfig {
	private String openTime;
	private String beginTime;
	private int battleTime;
	private int intervalTime;
	private int beforeEnterTime;
	private String mapId;
	private Point defendPoint;
	private int defendMapX;
	private int defendMapY;
	private Point attackPoint;
	private int attackMapX;
	private int attackMapY;
	private int gambleMoney;
	private int singleGambleMoney;
	private float gambleModulus;
	private String maxGambleRate;
	private String endTime;
	private String factionNpc1;
	private int npcX1;
	private int npcY1;
	
	private String factionNpc2;
	private int npcX2;
	private int npcY2;
	
	private NpcBorn factionSoulBorn1;
	private NpcBorn factionSoulBorn2;
	
	public void init(){
		defendPoint = new Point(mapId, defendMapX, defendMapY);
		attackPoint = new Point(mapId, attackMapX, attackMapY);
		
		factionSoulBorn1 = new NpcBorn();
		factionSoulBorn1.setBornnpcid(factionNpc1);
		factionSoulBorn1.setBornmapgxbegin(npcX1);
		factionSoulBorn1.setBornmapgxend(npcX1);
		factionSoulBorn1.setBornmapgybegin(npcY1);
		factionSoulBorn1.setBornmapgyend(npcY1);
		factionSoulBorn1.setBornnpccount(1);
		
		factionSoulBorn2 = new NpcBorn();
		factionSoulBorn2.setBornnpcid(factionNpc2);
		factionSoulBorn2.setBornmapgxbegin(npcX2);
		factionSoulBorn2.setBornmapgxend(npcX2);
		factionSoulBorn2.setBornmapgybegin(npcY2);
		factionSoulBorn2.setBornmapgyend(npcY2);
		factionSoulBorn2.setBornnpccount(1);
	}
	
	/**
	 * 是否可以创建分组 (开始创建时间到第一轮开始之前都可以创建分组)
	 * @return
	 */
	public boolean canCreate(){
		if(Util.isEmpty(openTime)){
			return false;
		}
		String times = openTime + Cat.strigula + beginTime;
		if(DateUtil.inOpenTime(new Date(),times)){
			return true;
		}
		return false;
	}
	
	public boolean warTime(){
		if(Util.isEmpty(openTime)){
			return false;
		}
		String times = openTime + Cat.strigula + endTime;
		if(DateUtil.inOpenTime(new Date(),times)){
			return true;
		}
		return false;
	}
	
	public long getCurRoundOpenTime(int curRound, int beginRound) {
		String[] array = beginTime.split(Cat.colon);
		int hour = Integer.valueOf(array[0]);
		int minutes = Integer.valueOf(array[1]);
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.HOUR_OF_DAY, hour);
		calendar.set(Calendar.MINUTE, minutes);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		Date curRoundOpenTime = DateUtil.addMinutes(calendar.getTime(), (curRound - beginRound) * intervalTime);
		return curRoundOpenTime.getTime();
	}
	
	public boolean canGamble(){
		String[] array = beginTime.split(Cat.colon);
		int hour = Integer.valueOf(array[0]);
		int minutes = Integer.valueOf(array[1]);
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.HOUR_OF_DAY, hour);
		calendar.set(Calendar.MINUTE, minutes);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		Date now = new Date();
		if(now.before(calendar.getTime())){
			return true;
		}
		return false;
	}
}
