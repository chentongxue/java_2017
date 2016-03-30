package sacred.alliance.magic.app.goods.behavior.result;

import lombok.Data;
import sacred.alliance.magic.base.Result;

public @Data class UseResult extends Result{

	public UseResult(){
		
	}
	//是否需要二次确认
	private boolean mustConfirm = false;
	//需要二次确认时的附加信息
	private String confirmInfo = "" ;
	//二次确认命令字
	private short confirmCmdId = 0 ;
	
}
