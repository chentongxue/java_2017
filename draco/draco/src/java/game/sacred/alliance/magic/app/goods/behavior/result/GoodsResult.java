package sacred.alliance.magic.app.goods.behavior.result;

import java.util.ArrayList;
import java.util.List;

import com.game.draco.GameContext;
import com.game.draco.message.response.C0002_ErrorRespMessage;

import sacred.alliance.magic.app.goods.Util;
import sacred.alliance.magic.app.log.vo.StatRoleGoodsRecord;
import sacred.alliance.magic.base.OutputConsumeType;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.base.StorageType;
import sacred.alliance.magic.domain.RoleGoods;
import sacred.alliance.magic.vo.RoleInstance;

public class GoodsResult extends Result{
	public static final int ALL_GOODS = 0;
	//添加删除物品同一事务时，用于区分删除的物品
	public static final int ADD_DEL_GOODS_FOR_DEL = 1; 
	
	/** 需要更新格子状态的物品<格子类型,物品> */
	private List<RoleGoods> updateGrids = new ArrayList<RoleGoods>();
	
	/** 新添加的物品同步<格子类型,物品> */
	private List<RoleGoods> newGrids = new ArrayList<RoleGoods>();
	/** 物品日志 */
	private List<StatRoleGoodsRecord> goodsRecords = new ArrayList<StatRoleGoodsRecord>();
	/** 同一事务中存在添加和删除，并且操作日志类型不同时，才会收集赋值 */
	private List<StatRoleGoodsRecord> delGoodsRecords = new ArrayList<StatRoleGoodsRecord>();
	
	/**
	 * =======================================================================
	 * 同步背包消息
	 * 1.发送格子更新协议
	 * 2.发送同步部分的背包协议
	 * =======================================================================
	 */
	public void syncBackpack(RoleInstance role, OutputConsumeType ocType1, OutputConsumeType ocType2){
		this.syncBackpack(role, ocType1);
		if(!Util.isEmpty(delGoodsRecords)){
			GameContext.getStatLogApp().roleGoodsLog(role, ocType2, "", delGoodsRecords);
		}
	}
	
	public void sync(RoleInstance role, StorageType storageType, OutputConsumeType ocType){
		if(role == null){
			return ;
		}
		if(!this.isSuccess()){
			role.getBehavior().sendMessage(new C0002_ErrorRespMessage((short)-2,this.info));
			return ;
		}
		if(!Util.isEmpty(updateGrids)){
			GameContext.getUserGoodsApp().updateGoodsGridMessage(role, updateGrids, storageType);
		}
		if(!Util.isEmpty(newGrids)){
			GameContext.getUserGoodsApp().syncSomeGoodsGridMessage(role, newGrids, storageType.getType());
		}
		if(!Util.isEmpty(goodsRecords)){
			GameContext.getStatLogApp().roleGoodsLog(role, ocType, "", goodsRecords);
		}
	}
	
	public void syncBackpack(RoleInstance role, OutputConsumeType ocType){
		this.sync(role, StorageType.bag, ocType);
	}
	
	
	public void addRoleGoodsRecord(RoleGoods roleGoods, int overlapChangeCount){
		RoleInstance role = GameContext.getOnlineCenter().getRoleInstanceByRoleId(roleGoods.getRoleId());
		if(null == role){
			return ;
		}
		StatRoleGoodsRecord rgr = GameContext.getStatLogApp().createRoleGoodsRecord(role, roleGoods, overlapChangeCount, "", null);
		if(null == rgr){
			return ;
		}
		goodsRecords.add(rgr);
	}
	
	public void addDelGoodsRecords(RoleGoods roleGoods, int overlapChangeCount){
		RoleInstance role = GameContext.getOnlineCenter().getRoleInstanceByRoleId(roleGoods.getRoleId());
		if(null == role){
			return ;
		}
		StatRoleGoodsRecord rgr = GameContext.getStatLogApp().createRoleGoodsRecord(role, roleGoods, overlapChangeCount, "", null);
		if(null == rgr){
			return ;
		}
		delGoodsRecords.add(rgr);
	} 
	
	
	
	
	public void addNeedUpdateGrid(RoleGoods roleGoods){
		if(roleGoods == null){
			return ;
		}
		updateGrids.add(roleGoods);
	}
	
	public void addNewGrid(RoleGoods roleGoods){
		if(roleGoods == null){
			return ;
		}
		newGrids.add(roleGoods);
	}
	
	
	
	/** 属性收集 */
	public void collect(GoodsResult result, int operatorType){
		this.newGrids.addAll(result.getNewGrids());
		this.updateGrids.addAll(result.updateGrids);
		if(operatorType == GoodsResult.ADD_DEL_GOODS_FOR_DEL){
			this.delGoodsRecords.addAll(result.getGoodsRecords());
			return ;
		}
		this.goodsRecords.addAll(result.getGoodsRecords());
	}
	
	
	public List<StatRoleGoodsRecord> getGoodsRecords() {
		return goodsRecords;
	}
	
	
	public GoodsResult setResult(byte ret) {
		this.result = ret;
		return this;
	}
	public GoodsResult setInfo(String info) {
		this.info = info;
		return this;
	}
	public List<RoleGoods> getUpdateGrids() {
		return updateGrids;
	}
	public List<RoleGoods> getNewGrids() {
		return newGrids;
	}
	
	public GoodsResult success(){
		this.result = Result.SUCCESS;
		return this;
	}
	
	public GoodsResult failure(){
		this.result = Result.FAIL;
		return this;
	}
}
