package sacred.alliance.magic.domain;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;
import sacred.alliance.magic.app.attri.AttriItem;
import sacred.alliance.magic.constant.RespTypeStatus;
import sacred.alliance.magic.util.Log4jManager;
import sacred.alliance.magic.util.Util;

import com.game.draco.message.item.GoodsBaseHeroAidItem;
import com.game.draco.message.item.GoodsBaseItem;

public @Data class GoodsHeroAid extends GoodsBase{
	
	private int swallowExp ;
	private String attriIds	;
	private String attriValues ;
	private String attriPercents ;

	private List<AttriItem> attriItemList ;
	
	@Override
	public List<AttriItem> getAttriItemList() {
		if(Util.isEmpty(attriItemList)){
			return null ;
		}
		List<AttriItem> list = new ArrayList<AttriItem>();
		for(AttriItem ai : attriItemList){
			list.add(ai.clone());
		}
		return list;
	}
	
	@Override
	public GoodsBaseItem getGoodsBaseInfo(RoleGoods roleGoods) {
		GoodsBaseHeroAidItem item = new GoodsBaseHeroAidItem();
		this.setGoodsBaseItem(roleGoods, item);
		item.setSwallowExp(swallowExp);
		item.setSecondType(secondType);
		return item;
	}
	
	
	
	@Override
	public void init(Object initData) {
		if(Util.isEmpty(attriIds)){
			return ;
		}
		//印记不能同时配置添加属性点与属性百分比
		if(!Util.isEmpty(attriValues) && !Util.isEmpty(attriPercents)){
			Log4jManager.CHECK.error("can not config the attriValues and attriPercents at same time,goodsId=" + this.id);
			Log4jManager.checkFail();
			return ;
		}
		String[] ids = Util.splitString(attriIds);
		String[] values = Util.splitString(attriValues);
		String[] percents = Util.splitString(attriPercents);
		int idsSize = this.size(ids);
		int valueSize = this.size(values);
		int pSize = this.size(percents);
		boolean isValue = true ;
		String[] arr = null ;
		if(idsSize == valueSize){
			arr = values ;
		}else if(idsSize == pSize){
			isValue = false ;
			arr = percents ;
		}
		if(null == arr){
			Log4jManager.CHECK.error("config error the attriValues size or the attriPercents size not eq the id size,goodsId=" + this.id);
			Log4jManager.checkFail();
			return ;
		}
		List<AttriItem> arriItemList = new ArrayList<AttriItem>();
		for(int i=0;i<idsSize;i++){
			float f = Float.parseFloat(arr[i]) ;
			if(!isValue){
				f = f/RespTypeStatus.FULL_RATE ;
			}
			AttriItem item = new AttriItem(Byte.parseByte(ids[i]),f,!isValue);
			arriItemList.add(item);
		}
		this.attriItemList = arriItemList ;
	}
	
	private int size(String[] arr){
		return (null == arr)?0:arr.length ;
	}
}
