package sacred.alliance.magic.app.goods.suit;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;
import sacred.alliance.magic.app.attri.AttriItem;
import sacred.alliance.magic.constant.Cat;


public @Data class SuitAttribute {

	private short suitId ;
	private int num ;
	private byte attrId1 ;
	private int value1 ;
	private byte attrId2 ;
	private int value2 ;
	private byte attrId3 ;
	private int value3 ;
	private byte attrId4 ;
	private int value4 ;
	
	public String getKey(){
		return suitId + Cat.underline + num ;
	}
	
	//準饜离趼僇
	private List<AttriItem> attriItems = new ArrayList<AttriItem>();
	
	private void init(byte attrId,int value){
		if(attrId <=0 || value <=0){
			return ;
		}
		attriItems.add(new AttriItem(attrId,value,0f));
	}
	
	public void init(){
		this.init(attrId1, value1);
		this.init(attrId2, value2);
		this.init(attrId3, value3);
		this.init(attrId4, value4);
	}
}
