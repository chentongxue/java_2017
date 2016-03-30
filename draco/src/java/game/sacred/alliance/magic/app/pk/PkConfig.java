package sacred.alliance.magic.app.pk;

import lombok.Data;

public @Data class PkConfig {
	private int openLevel;
	private short buffId;
	private short protectBuffId;
	private String killMsg;
	private int consumeGoodsId;
	private String mailTitle;
	private String mailContent;
	private int time;
	private String desc;
	private String npcId;
	private String dieInfo;//死亡提示信息
	private String battleMsg;
	private String massacreMsg;
}
