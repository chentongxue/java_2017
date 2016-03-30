package sacred.alliance.magic.app.chat;

import com.game.draco.GameContext;

import sacred.alliance.magic.constant.TextId;

/** 聊天频道枚举 */
public enum ChannelType {
	
	Private((byte)1,TextId.CHANNEL_PRIVATE,"FFF04CFF"),//私聊频道
	Team((byte)2,TextId.CHANNEL_TEAM,"FF58edff"),//队伍频道
	Union((byte)3,TextId.CHANNEL_FACTION,"FF18ff00"),//门派频道
	World((byte)4,TextId.CHANNEL_WORLD,"FFFFB400"),//世界频道
	Speak((byte)5,TextId.CHANNEL_SPEAK,"FF7498ff"),//喊话频道
	Map((byte)6,TextId.CHANNEL_MAP,"FFF7AFAF"),//区域频道
	System((byte)7,TextId.CHANNEL_SYSTEM,"FFFF3c3c"),//系统频道
	Camp((byte)8,TextId.CHANNEL_CAMP,"FF0072ff"),//阵营频道
	Horn((byte)11,TextId.CHANNEL_HORN,"FFfdff50"),//大喇叭频道
	
	Publicize_System((byte)9,TextId.CHANNEL_PUBLICIZE_SYSTEM,"FFFF3c3c"),//走马灯广播-系统触发
	Publicize_Personal((byte)10,TextId.CHANNEL_PUBLICIZE_PERSONAL,"FFFF3c3c"),//走马灯广播-个人触发
	
	GmSys((byte)126,TextId.CHANNEL_GM_SYSTEM,"FFFF3c3c"),//未使用
	
	//Gm((byte)127,TextId.CHANNEL_GM,"FFFF3c3c"),//GM频道
	
	;
	
	private final byte type;
	private final String name;
	private final String color;
	
	ChannelType(byte type, String name, String color){
		this.type = type;
		this.name = name;
		this.color = color;
	}
	
	public byte getType(){
		return this.type ;
	}
	
	public String getName() {
		return GameContext.getI18n().getText(name);
	}

	public String getColor() {
		return color;
	}

	public static ChannelType getChannelType(byte type){
		for(ChannelType item : ChannelType.values()){
			if(item.getType() == type){
				return item;
			}
		}
		return null ;
	}
}
