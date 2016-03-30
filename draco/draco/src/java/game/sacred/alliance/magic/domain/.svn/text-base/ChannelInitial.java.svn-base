package sacred.alliance.magic.domain;

import java.util.ArrayList;
import java.util.List;

import sacred.alliance.magic.app.goods.Peshe;
import sacred.alliance.magic.constant.Cat;
import sacred.alliance.magic.util.Util;

public class ChannelInitial {
	
	private String channelId;
	private String item1;
	private String item2;
	private String item3;
	private String item4;
	private List<Peshe> items = new ArrayList<Peshe>();
	public String getChannelId() {
		return channelId;
	}
	public void setChannelId(String channelId) {
		this.channelId = channelId;
	}
	public String getItem1() {
		return item1;
	}
	public void setItem1(String item1) {
		this.item1 = item1;
	}
	public String getItem2() {
		return item2;
	}
	public void setItem2(String item2) {
		this.item2 = item2;
	}
	public String getItem3() {
		return item3;
	}
	public void setItem3(String item3) {
		this.item3 = item3;
	}
	public String getItem4() {
		return item4;
	}
	public void setItem4(String item4) {
		this.item4 = item4;
	}
	
	private void initItem(String str){
		if(!Util.isEmpty(str) && str.indexOf(Cat.colon) != -1){
			String[] item = item1.split(Cat.colon);
			Peshe peshe = new Peshe();
			peshe.setGoodsId(Integer.parseInt(item[0]));
			peshe.setNum(Integer.parseInt(item[1]));
			items.add(peshe);
		}
	}
	
	public void initItems(){
		this.initItem(item1);
		this.initItem(item2);
		this.initItem(item3);
		this.initItem(item4);
	}
	
	public List<Peshe> getItems() {
		return items;
	}
	public void setItems(List<Peshe> items) {
		this.items = items;
	}
}
