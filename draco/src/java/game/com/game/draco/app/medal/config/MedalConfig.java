package com.game.draco.app.medal.config;

import java.util.ArrayList;
import java.util.List;

import com.game.draco.app.medal.MedalType;

import lombok.Data;
import sacred.alliance.magic.app.attri.AttriItem;
import sacred.alliance.magic.base.AttributeType;
import sacred.alliance.magic.base.QualityType;
import sacred.alliance.magic.util.Log4jManager;
import sacred.alliance.magic.util.Util;

public @Data class MedalConfig {

	private int type;
	private byte showLv;//显示等级
	private short level;
	private short slaveLevel ;
	private short num;
	private byte relyAttrType;
	private int relyAttrValue;
	private String name ;
	private short effectId ;
	private short iconId ;
	private byte attriType1;
	private int attriValue1;
	private byte attriType2;
	private int attriValue2;
	private byte attriType3;
	private int attriValue3;
	private byte attriType4;
	private int attriValue4;
	
	private int index;
	private MedalType medalType;
	private List<AttriItem> attriList = new ArrayList<AttriItem>();
	
	public void initMedalType(MedalType medalType){
		this.type = medalType.getType();
	}
	
	public void checkInit(String fileInfo){
		String info = fileInfo + "type=" + this.type + ",";
		this.medalType = MedalType.get(this.type);
		if(null == this.medalType){
			this.checkFail(info + "type is not exist.");
		}
		if(this.medalType.isAttribute()){
			info += "relyAttrType = " + this.relyAttrType + ", relyAttrValue=" + this.relyAttrValue + ".";
			AttributeType attrType = AttributeType.get(this.relyAttrType);
			if(null == attrType){
				this.checkFail(info + "relyAttrType is not exist.");
			}
			if(this.relyAttrValue <= 0){
				this.checkFail(info + "relyAttrValue is error.");
			}
		}else{
			info += "level=" + this.level + ", num=" + this.num + ".";
			if(this.level <= 0){
				this.checkFail(info + "level is error.");
			}
			if(MedalType.XiLian == this.medalType && null == QualityType.get(this.level)){
				this.checkFail(info + "level means QualityType, it's not exist.");
			}
			if(MedalType.XiangQian == this.medalType || MedalType.XiLian == this.medalType){
				if(this.num <= 0){
					this.checkFail(info + "num is error.");
				}
			}
		}
		if(Util.isEmpty(this.name)){
			this.checkFail(info + "name is not config.");
		}
		if(this.iconId <= 0){
			this.checkFail(info + "iconId is not config.");
		}
		//初始化属性列表
		this.addAttrToList(this.attriType1, this.attriValue1, info);
		this.addAttrToList(this.attriType2, this.attriValue2, info);
		this.addAttrToList(this.attriType3, this.attriValue3, info);
		this.addAttrToList(this.attriType4, this.attriValue4, info);
	}
	
	private void checkFail(String info){
		Log4jManager.CHECK.error(info);
		Log4jManager.checkFail();
	}
	
	private void addAttrToList(byte attriType, int attriValue, String info){
		if(attriType <= 0 || attriValue <= 0){
			return;
		}
		if(null == AttributeType.get(attriType)){
			this.checkFail(info + "attriType = " + attriType + ", it's not exist.");
		}
		this.attriList.add(new AttriItem(attriType, attriValue, 0f));
	}
	
}
