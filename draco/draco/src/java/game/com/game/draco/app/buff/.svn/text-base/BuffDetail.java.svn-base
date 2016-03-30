package com.game.draco.app.buff;

import lombok.Data;

public @Data class BuffDetail {

	private short buffId ;
	private String name ;
	private int level ;
	private byte iconId ;
	private short effectId ;
	private byte timeType ;
	private byte categoryType ;
	private boolean transNoClean ;
	private int intervalTime ;
	private int persistTime ;
	private boolean dieLost ;
	private boolean offlineLost ;
	private boolean offlineTiming ;
	private int groupId ;
	private int hatredPercent ;
	private int hatredAdd ;
	private byte hurtType;
	private byte zoom; //外形缩放 10=原始大小
	private String discolor; //外形变色
	private String desc ;
	
	/**buff作用范围类型 0：团队BUFF（唯一）1：个人BUFF（共存 */
	private int beingType;
	/**替换类型 1 直接替换 2 延长时间 3 重置时间 */
	private int replaceType;
	/**不能替换时，提示语*/
	private String notReplaceDesc;
	
	public void init(){};
	public void check(){};
	
	public Buff newBuff(){
		return null ;
	}
}
