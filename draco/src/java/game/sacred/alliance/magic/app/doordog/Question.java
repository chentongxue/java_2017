package sacred.alliance.magic.app.doordog;

import java.util.List;

import sacred.alliance.magic.vo.RoleInstance;

import lombok.Data;

public @Data class Question {
	public final static byte TYPE_TEXT = 0;
	public final static byte TYPE_IMAGE = 1;
	public final static byte SUBTYPE_IMAGE = 0;
	public final static byte SUBTYPE_IMAGE_QUESTION = 1;
	public final static byte SUBTYPE_INTELLIGENCE = 2;
	
	public final static byte DOORDOG_STEP_ONE = 0;
	public final static byte DOORDOG_STEP_TWO = 1;
	public final static byte DOORDOG_STEP_THREE = 2;

	private byte type; //验证类型，0：文本，1：图片，2：声音
	private byte subType; //图片小类型：0：图片验证码,1：图片问题验证码,2:处理客服答题
	private List<String> desc; //问题描述
	private List<byte[]> contentList; //问题二进制数组
	private String answer; //答案
	private String format; //当验证类型是图片或者声音时，指定图片声音的格式
	private byte step; 
	
	public String toString(){
		return this.contentList + " " + this.answer ;
	}
	
	public QuestionDetail getRealQuestion(RoleInstance role){
		QuestionDetail detail = new QuestionDetail();
		if(subType != SUBTYPE_INTELLIGENCE){
			detail.setDesc(desc.get(0));
			detail.setQuestion( contentList.get(0));
			return detail;
		}
		
		if(this.step == DOORDOG_STEP_ONE){
			detail.setDesc(desc.get(0));
			detail.setQuestion( contentList.get(0));
			this.step = DOORDOG_STEP_TWO;
			return detail;
		}
		else if(this.step == DOORDOG_STEP_TWO){
			this.step = DOORDOG_STEP_THREE;
		}
		detail.setDesc(desc.get(1));
		detail.setQuestion( contentList.get(1));
		return detail;
		
	}
}
