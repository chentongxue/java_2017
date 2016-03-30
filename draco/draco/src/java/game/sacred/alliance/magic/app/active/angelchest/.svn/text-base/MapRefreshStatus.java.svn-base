package sacred.alliance.magic.app.active.angelchest;

import java.text.MessageFormat;
import java.util.Date;

import com.game.draco.GameContext;

import lombok.Data;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.util.DateUtil;

public @Data class MapRefreshStatus {

	private String mapId ;
	private int lineId ;
	private Date startTime ;
	private int refreshIndex ;
	private String nextOpenTimeStr = "" ; 
	//当前周期是否已结束
	private boolean curLoopOver = false ;
	
	/**
	 * 是否同一个周期
	 * @return
	 */
	public boolean inSameLoop() {
		if (null == startTime) {
			return false;
		}
		return GameContext.getAngelChestApp()
		.getStartTime(new Date()).getTime() == startTime.getTime() ;
	}
	
	
	public void initState(){
		Date now = new Date();
		this.startTime = GameContext.getAngelChestApp().getStartTime(now);
		this.refreshIndex = 0 ;
		this.curLoopOver = false ;
		Date nextOpenTime = GameContext.getAngelChestApp().getNextTime(now);
		this.nextOpenTimeStr = MessageFormat.format(GameContext.getI18n().getText(TextId.ANGELCHEST_NEXT_OPEN_TIME),
				DateUtil.date2FormatDate(nextOpenTime, GameContext.getI18n().getText(TextId.ANGELCHEST_NEXT_OPEN_TIME_FORMAT)));
	}
}
