package sacred.alliance.magic.app.goods.suit;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sacred.alliance.magic.app.attri.AttriBuffer;
import sacred.alliance.magic.base.XlsSheetNameType;
import sacred.alliance.magic.domain.GoodsBase;
import sacred.alliance.magic.util.Log4jManager;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.util.XlsPojoUtil;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.google.common.collect.Maps;

public class SuitAppImpl implements SuitApp{
	private final  Logger logger = LoggerFactory.getLogger(this.getClass());
	private Map<Short,Suit> suitMap = Maps.newHashMap();
	
	
	@Override
	public Suit getSuit(short suitId) {
		return suitMap.get(suitId);
	}

	@Override
	public void setArgs(Object arg0) {
		
	}
	
	private void initSuitAttribute(){
		String fileName = "";
		String sheetName = "";
		String xlsPath = GameContext.getPathConfig().getXlsPath();
		try{
			fileName = XlsSheetNameType.suit_attributes.getXlsName();
			sheetName = XlsSheetNameType.suit_attributes.getSheetName();
			String sourceFile = xlsPath + fileName;
		    List<SuitAttribute> list = XlsPojoUtil.sheetToList(sourceFile, sheetName, SuitAttribute.class);
		    Collections.sort(list, new Comparator<SuitAttribute>(){
				@Override
				public int compare(SuitAttribute sa1, SuitAttribute sa2) {
					//升序
					if(sa1.getSuitId() > sa2.getSuitId()){
						return 1 ;
					}else if(sa1.getSuitId() < sa2.getSuitId()){
						return -1 ;
					}
					//升序
					if(sa1.getNum() > sa2.getNum()){
						return 1 ;
					}
					if(sa1.getNum() < sa2.getNum()){
						return -1 ;
					}
					return 0;
				}
		    });
		    String key = "" ;
			for(SuitAttribute item : list){
				String thisKey = item.getKey();
				if(key.equals(thisKey)){
					//同一套装下面配置了多个相关的数目
					Log4jManager.CHECK.error("load suit attribute config error,same num in one suit,suitId=" + item.getSuitId()  
							+ "not exits : sourceFile = " + fileName + " sheetName =" + sheetName);
					Log4jManager.checkFail();
					continue ;
				}
				key = thisKey ;
				item.init();
				Suit suit = this.getSuit(item.getSuitId());
				if(null == suit){
					//配置了不存在的套装ID
					Log4jManager.CHECK.error("load suit attribute config error,suitId=" + item.getSuitId()  
							+ "not exits : sourceFile = " + fileName + " sheetName =" + sheetName);
					Log4jManager.checkFail();
					continue ;
				}
				List<SuitAttribute> attributes = suit.getAttributes();
				if(null == attributes){
					attributes = new ArrayList<SuitAttribute>();
					suit.setAttributes(attributes);
				}
				attributes.add(item);
			}
		}catch(Exception e){
			Log4jManager.CHECK.error("load suit list error : sourceFile = " + fileName + " sheetName =" + sheetName, e);
			Log4jManager.checkFail();
		}
	}
	
	private void initSuit(){
		String fileName = "";
		String sheetName = "";
		String xlsPath = GameContext.getPathConfig().getXlsPath();
		try{
			fileName = XlsSheetNameType.suit_list.getXlsName();
			sheetName = XlsSheetNameType.suit_list.getSheetName();
			String sourceFile = xlsPath + fileName;
			this.suitMap = XlsPojoUtil.sheetToGenericMap(sourceFile, sheetName, Suit.class);
		}catch(Exception e){
			Log4jManager.CHECK.error("load suit list error : sourceFile = " + fileName + " sheetName =" + sheetName, e);
			Log4jManager.checkFail();
		}
	}

	@Override
	public void start() {
		//注意先后顺序
		this.initSuit();
		this.initSuitAttribute();
		this.build();
	}
	
	private void build(){
		try {
			for (Suit item : this.suitMap.values()) {
				item.init();
			}
		}catch(Exception ex){
			Log4jManager.CHECK.error("buid suit info error",ex);
			Log4jManager.checkFail();
		}
	}

	@Override
	public void stop() {
		
	}

	@Override
	public AttriBuffer getAttriBuffer(RoleInstance role) {
		try {
			Map<Short, Byte> suitNums = role.getEquipBackpack().getSuitInfo();
			if (Util.isEmpty(suitNums)) {
				return null;
			}
			AttriBuffer buffer = AttriBuffer.createAttriBuffer();
			for (Short suitId : suitNums.keySet()) {
				Suit suit = this.getSuit(suitId);
				if (null == suit) {
					continue;
				}
				byte num = suitNums.get(suitId);
				List<SuitAttribute> attributes = suit.getAttributes();
				for (SuitAttribute sa : attributes) {
					if (sa.getNum() > num) {
						break;
					}
					buffer.append(sa.getAttriItems());
				}
			}
			return buffer;
		} catch (Exception ex) {
			logger.error("", ex);
		}
		return null;
	}
	
	@Override
	public void suitChanged(RoleInstance role,GoodsBase onEquip,GoodsBase offEquip){
		try {
			short onSuitId = this.getSuitId(onEquip);
			short offSuitId = this.getSuitId(offEquip);
			if (onSuitId <= 0 && offSuitId <= 0) {
				// 不影响套装
				return;
			}
			if (onSuitId == offSuitId) {
				return;
			}
			AttriBuffer buffer = AttriBuffer.createAttriBuffer();
			Map<Short, Byte> currentSuitNums = role.getEquipBackpack()
					.getSuitInfo();
			if (onSuitId > 0) {
				byte currentNum = this.getSuitNum(currentSuitNums, onSuitId);
				this.appendSuitChange(buffer, onSuitId, currentNum - 1,
						currentNum);
			}
			if (offSuitId > 0) {
				byte currentNum = this.getSuitNum(currentSuitNums, offSuitId);
				this.appendSuitChange(buffer, offSuitId, currentNum + 1,
						currentNum);
			}
			GameContext.getUserAttributeApp().changeAttribute(role,
					buffer);
			role.getBehavior().notifyAttribute() ;
		}catch(Exception ex){
			logger.error("",ex);
		}
	}
	
	private byte getSuitNum(Map<Short,Byte> currentSuitNums,short suitId){
		Byte num = currentSuitNums.get(suitId);
		return (null==num)?0:num ;
	}
	
	private void appendSuitChange(AttriBuffer buffer,short suitId,int preNum,int currentNum){
		if(preNum == currentNum){
			return ;
		}
		Suit suit = this.getSuit(suitId);
		if(null == suit){
			return ;
		}
		List<SuitAttribute> attributes = suit.getAttributes();
		if(null == attributes){
			return ;
		}
		AttriBuffer changeBuffer = AttriBuffer.createAttriBuffer();
		boolean incr = currentNum > preNum ;
		int min = incr ? preNum : currentNum ;
		int max = incr ? currentNum : preNum ;
		for(SuitAttribute sa : attributes){
			if(sa.getNum() <= min){
				continue ;
			}
			if(sa.getNum() > max){
				break ;
			}
			changeBuffer.append(sa.getAttriItems());
		}
		if(!incr){
			changeBuffer.reverse();
		}
		buffer.append(changeBuffer);
	}
	
	private short getSuitId(GoodsBase equip){
		if(null == equip){
			return 0 ;
		}
		return equip.getSuitId();
	}

}
