package sacred.alliance.magic.app.recoup;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import lombok.Data;
import sacred.alliance.magic.app.goods.GoodsOperateBean;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.vo.RoleInstance;

public @Data class Recoup {
	private static final int ALL_CHANNEL = 0 ; //0表示不限 //TODO: 需要改成-1
	private static final String CAT1 = ";" ;
	private static final String CAT2 = ":" ;
	public static final String ID = "id" ;
	private int id ;
	private String senderName ;
	private String title ;
	private String context ;
	private int bindMoney ;
	private int gameMoney ;
	private String goodsInfo; // goodsId:num:bindType;goodsId:num:bindType
	private Date startTime ;
	private Date endTime ;
	private int channelId = ALL_CHANNEL ;//渠道ID，
	
	private List<GoodsOperateBean> goodsList = new ArrayList<GoodsOperateBean>();

	public void init(){
		if(Util.isEmpty(this.goodsInfo)){
			goodsList.clear();
			return ;
		}
		goodsList.clear();
		String[] strs = this.goodsInfo.split(CAT1);
		for(String str : strs){
			String[] ss = str.split(CAT2);
			GoodsOperateBean bean = new GoodsOperateBean(Integer.parseInt(ss[0]),
					Integer.parseInt(ss[1]),Integer.parseInt(ss[2]));
			goodsList.add(bean);
		}
	}
	
	public void appendGoodsInfo(){
		if(Util.isEmpty(this.goodsList)){
			this.goodsInfo = "" ;
			return ;
		}
		StringBuffer buffer = new StringBuffer("");
		String cat = "" ;
		for(GoodsOperateBean bean : goodsList){
			buffer.append(cat).append(bean.getGoodsId())
			.append(CAT2).append(bean.getGoodsNum())
			.append(CAT2).append(bean.getBindType().getType());
			cat = CAT1 ;
		}
		this.goodsInfo = buffer.toString();
	}
	
	public boolean inTime(){
		if(null == endTime || null == startTime){
			return false ;
		}
		long now = System.currentTimeMillis();
		return now >= startTime.getTime() && now<= endTime.getTime();
	}
	
	/**
	 * 是否符合补偿条件
	 * @param channelId
	 * @return
	 */
	public boolean canReceive(RoleInstance role){
		//1.渠道
		//2.在时间内
		//3.角色创建时间在赔偿开始时间前
		return (this.channelId == ALL_CHANNEL || this.channelId == role.getChannelId())
		    && this.inTime()
		    && (null != role.getCreateTime() && role.getCreateTime().getTime() < this.startTime.getTime());
	}
	
}
