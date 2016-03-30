package sacred.alliance.magic.app.goods.suit;

import java.util.ArrayList;
import java.util.List;

import com.game.draco.GameContext;
import com.game.draco.message.item.AttriTypeStrValueItem;
import com.game.draco.message.item.EquipSuitAttriItem;
import com.game.draco.message.response.C0516_EquipSuitInfoRespMessage;

import lombok.Data;
import sacred.alliance.magic.app.attri.AttriItem;
import sacred.alliance.magic.base.AttributeType;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.domain.GoodsBase;
import sacred.alliance.magic.domain.GoodsEquipment;
import sacred.alliance.magic.util.KeySupport;
import sacred.alliance.magic.util.Log4jManager;

public @Data class Suit implements KeySupport<Short>{

	private short suitId ;
	private String suitName ;
	private int equipId1 ;
	private int equipId2 ;
	private int equipId3 ;
	private int equipId4 ;
	private int equipId5 ;
	private int equipId6 ;
	private int equipId7 ;
	
	private List<Byte> conditionTypeList = new ArrayList<Byte>();
	private C0516_EquipSuitInfoRespMessage suitInfoMessage ;
	private List<SuitAttribute> attributes = new ArrayList<SuitAttribute>();
	
	public Message getDetailMessage(){
		return suitInfoMessage ;
	}
	
	public Short getKey(){
		return this.suitId ;
	}
	
	
	private void initConditionType(int equipId){
		if(equipId <=0){
			return ;
		}
		GoodsBase gb = GameContext.getGoodsApp().getGoodsBase(equipId);
		if(null == gb){
			Log4jManager.CHECK.error("suit config error,suitId=" + suitId + " equipId=" + equipId + " not exist");
			Log4jManager.checkFail();
			return ;
		}
		if(!(gb instanceof GoodsEquipment)){
			Log4jManager.CHECK.error("suit config error,suitId=" + suitId + " equipId=" + equipId + " not equipment");
			Log4jManager.checkFail();
			return ;
		}
		GoodsEquipment ge = (GoodsEquipment)gb ;
		byte conditionType = (byte)ge.getEquipslotType() ;
		if(conditionTypeList.contains(conditionType)){
			Log4jManager.CHECK.error("suit config error,suitId=" + suitId 
					+ " config error,too many conditionType=" + conditionType + " in this suit");
			Log4jManager.checkFail();
			return ;
		}
		conditionTypeList.add(conditionType);
		short geSuitId = ge.getSuitId() ;
		if(geSuitId > 0 && geSuitId != this.suitId){
			Log4jManager.CHECK.error("suit config error,the goods in too many suit. goodsId=" + geSuitId);
			Log4jManager.checkFail();
		}
		ge.setSuitId(this.suitId);
	}
	
	public void init(){
		this.initConditionType(equipId1);
		this.initConditionType(equipId2);
		this.initConditionType(equipId3);
		this.initConditionType(equipId4);
		this.initConditionType(equipId5);
		this.initConditionType(equipId6);
		this.initConditionType(equipId7);
		
		byte[] cts = new byte[conditionTypeList.size()];
		for(int i=0;i<conditionTypeList.size();i++){
			cts[i] = conditionTypeList.get(i);
		}
		
		C0516_EquipSuitInfoRespMessage respMsg = new C0516_EquipSuitInfoRespMessage();
		respMsg.setStatus((byte)1);
		respMsg.setSuitId(this.suitId);
		respMsg.setSuitName(this.suitName);
		respMsg.setPlaces(cts);
		if(null == this.attributes){
			this.suitInfoMessage = respMsg ;
			return ;
		}
		//初始化套装属性
		List<EquipSuitAttriItem> suitInfos = new ArrayList<EquipSuitAttriItem>();
		for(SuitAttribute item : this.attributes){
			EquipSuitAttriItem suitItem = new EquipSuitAttriItem();
			suitItem.setTotal((byte)item.getNum());
			List<AttriTypeStrValueItem> attirs = new ArrayList<AttriTypeStrValueItem>();
			suitItem.setAttirs(attirs);
			for(AttriItem attri : item.getAttriItems()){
				AttriTypeStrValueItem a = new AttriTypeStrValueItem();
				a.setType(attri.getAttriTypeValue());
				a.setValue(AttributeType.formatValue(attri.getAttriTypeValue(), attri.getValue()));
				attirs.add(a);
			}
			suitInfos.add(suitItem);
		}
		respMsg.setInfos(suitInfos);
		this.suitInfoMessage = respMsg ;
	}
}
