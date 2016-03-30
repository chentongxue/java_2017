package com.game.draco.app.quest;

public class QuestAcceptParam {

	private String shareRoleId ;
	private boolean shareCancel = false ;
	private int tiggerGoodsId ;
	private Model acceptModel ;
	
	/**
	 * 接任务模式
	 *
	 */
	public enum Model{
		Normal((byte)0),
		Goods((byte)1),
		Share((byte)2),
		;
		
		private byte type ;
		private Model(byte type){
			this.type = type ;
		}
		
		public byte getType(){
			return type ;
		}
	}

	public String getShareRoleId() {
		return shareRoleId;
	}

	public void setShareRoleId(String shareRoleId) {
		this.shareRoleId = shareRoleId;
	}

	public boolean isShareCancel() {
		return shareCancel;
	}

	public void setShareCancel(boolean shareCancel) {
		this.shareCancel = shareCancel;
	}

	public int getTiggerGoodsId() {
		return tiggerGoodsId;
	}

	public void setTiggerGoodsId(int tiggerGoodsId) {
		this.tiggerGoodsId = tiggerGoodsId;
	}

	public Model getAcceptModel() {
		return acceptModel;
	}

	public void setAcceptModel(Model acceptModel) {
		this.acceptModel = acceptModel;
	}

}


