package sacred.alliance.magic.app.invite;

import lombok.Data;
import sacred.alliance.magic.constant.Cat;
import sacred.alliance.magic.util.KeySupport;

public @Data class InviteDownLoad implements KeySupport<String>{

	private int channelId ;
	private int osType	;
	private String url ;

	@Override
	public String getKey() {
		return channelId + Cat.underline + this.osType ;
	}

}
