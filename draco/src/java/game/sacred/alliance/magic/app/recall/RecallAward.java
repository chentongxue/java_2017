package sacred.alliance.magic.app.recall;

import java.util.Date;

import com.game.draco.GameContext;

import lombok.Data;
import sacred.alliance.magic.app.goods.GoodsOperateBean;
import sacred.alliance.magic.base.DateTimeBean;
import sacred.alliance.magic.constant.FormatConstant;
import sacred.alliance.magic.util.DateConverter;
import sacred.alliance.magic.util.Log4jManager;

public @Data class RecallAward{
	private int id; //奖励id
	private int minDay; //天数下限
	private int maxDay; //天数上限
	private int goodsId; //奖励物品id
	private short goodsNum; //奖励物品数量
	private byte bindType; //奖励物品绑定类型
	private String startDateAbs;//绝对开启日期
	private String endDateAbs;//绝对结束日期
	private int startDateRel;//相对开启日期
	private int endDateRel;//相对结束日期
	private String mailSender; //发件人
	private String mailTitle; //邮件标题
	private String mailDesc; //邮件内容
	
	private Date startDate;
	private Date endDate;
	private GoodsOperateBean awardGoods;
	
	public void init(String xlsName, String sheetName){
		if(goodsId <= 0 || null == GameContext.getGoodsApp().getGoodsBase(goodsId)){
			Log4jManager.checkFail();
			Log4jManager.CHECK.error("recall award," + "id=" + this.id
					+ ",goodId=" + goodsId + " is not exsit! in xlsName=" + xlsName
					+ ", sheetName=" + sheetName);
		}
		awardGoods = new GoodsOperateBean(goodsId, goodsNum, bindType);
		//验证时间是否正确
		DateTimeBean dateBean = DateConverter.getDateTimeBean(startDateRel, endDateRel, startDateAbs, endDateAbs, FormatConstant.DEFAULT_YMD);
		if(null == dateBean){
			this.dateErrorLog(xlsName, sheetName);
		}
		
		startDate = dateBean.getStartDate();
		endDate = dateBean.getEndDate();
		if(null == this.startDate || null == this.endDate){
			this.dateErrorLog(xlsName, sheetName);
		}
		
		if(this.startDate.after(this.endDate)){
			this.dateErrorLog(xlsName, sheetName);
		}
	}
	
	private void dateErrorLog(String xlsName, String sheetName){
		Log4jManager.checkFail();
		Log4jManager.CHECK.error("recall award," + " id=" + this.id
				+ ", startdate endDate config error!,in xlsName="
				+ xlsName + ", sheetName=" + sheetName);
	}

}
