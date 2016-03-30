package sacred.alliance.magic.app.auction;

import java.util.List;

import lombok.Data;
import platform.message.item.MercuryRecordItem;

public @Data class SearchResult {
	private final static int SUCCESS = 1 ;
	private int status ;
	private String tips ;
	private int totalPage ;
	private int totalRecord ;
	private List<MercuryRecordItem> records ;
	
	public boolean isSuccess(){
		return SUCCESS == this.status;
	}
	
	public void flagSuccess(){
		this.status = SUCCESS ;
	}
}
