package com.game.draco.app.compass.config;

import com.game.draco.GameContext;
import com.game.draco.app.chat.ChannelType;
import lombok.Data;
import sacred.alliance.magic.base.DateTimeBean;
import sacred.alliance.magic.util.*;

import java.util.Date;

public @Data class TaobaoExtra {
	private static final String DateFormat = "yyyy-MM-dd";
	private short extraId ;
	private String startDateAbs;//绝对开启日期
	private String endDateAbs;//绝对结束日期
	private int startDateRel;//相对开启日期
	private int endDateRel;//相对结束日期
	private int hitRate ;
	private int goodsId ;
	private int minGoodsNum ;
	private int maxGoodsNum ;
	private byte bindType ;
	private String broadcastInfo ;
	
	//开启时间（启动时构建）
	private Date startDate;
	private Date endDate;
	
	public void init() {
		String info = "load taobao extra error: extraid =" + this.extraId + ".";
		if (null == GameContext.getGoodsApp().getGoodsBase(this.goodsId)) {
				this.checkFail(info + "goodsId=" + this.goodsId
						+ ",this goods is not exist!");
		}
		// 转换时间格式
		DateTimeBean bean = DateConverter
				.getDateTimeBean(this.startDateRel, this.endDateRel,
						this.startDateAbs, this.endDateAbs, DateFormat);
		if (null == bean) {
			this.checkFail(info
					+ "The startDateRel/endDateRel/startDateAbs/endDateAbs is error.");
		}
		this.startDate = bean.getStartDate();
		this.endDate = bean.getEndDate();
		if (null == this.startDate || null == this.endDate) {
			this.checkFail(info
					+ "Please config the start time or the end time.");
		}
		// 给时间字符串赋值
		this.startDateAbs = DateUtil.date2Str(this.startDate, DateFormat);
		this.endDateAbs = DateUtil.date2Str(this.endDate, DateFormat);
	}
	
	private void checkFail(String info){
		Log4jManager.CHECK.error(info);
		Log4jManager.checkFail();
	}
	
	public boolean isTimeOpen(){
		long now = System.currentTimeMillis();
		return now >= this.startDate.getTime() && now < this.endDate.getTime() ;
	}
	
	public CompassRoleAward getAward(Compass compass,byte place) {
		//判断是否在活动期间
		if(!this.isTimeOpen()){
			return null ;
		}
		if(!RandomUtil.on(hitRate)){
			return null ;
		}
		int num = RandomUtil.randomInt(minGoodsNum, maxGoodsNum) ;
		if(num <=0){
			return null ;
		}
		// 抽中的奖励
		CompassRoleAward roleAward = new CompassRoleAward();
		roleAward.setId(compass.getTaobaoId());
		roleAward.setPlace(place);
		roleAward.setGoodsId(this.goodsId);
		roleAward.setGoodsNum(num);
		roleAward.setBindType(this.bindType);
		roleAward.setBroadcastInfo(this.buildBroadcastInfo(broadcastInfo, compass));
		return roleAward;
	}
	
	private String buildBroadcastInfo(String broadcastInfo, Compass compass){
		if(Util.isEmpty(broadcastInfo)){
			return "";
		}
		return broadcastInfo.replace(Wildcard.Compass_Name, compass.getName()).replace(Wildcard.GoodsName,
				Wildcard.getChatGoodsContent(this.goodsId, ChannelType.Publicize_Personal));
	}
}
