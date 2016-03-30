package sacred.alliance.magic.app.broadcast.config;

import lombok.Data;

public @Data class BroadcastGoods {
	private int goodsId;
	private String targetId;
	private String content;
}
