package sacred.alliance.magic.domain;

import java.util.Date;

import lombok.Data;
import sacred.alliance.magic.util.Util;

public @Data class PublicNotice {
	
	public static String NOTICETYPE = "noticeType";
	
	private byte noticeType;//公告类型
	private String title;//标题
	private String content;//公告内容
	private Date updateTime;//公告修改时间
	private String color;
	
	public String getColorContent(){
		if(Util.isEmpty(this.color)){
			this.color = "FFFFFFFF";
		}
		return Util.getColorString(this.color, this.content);
	}
	
}
