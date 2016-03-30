package sacred.alliance.magic.app.goods.behavior.result;

import java.util.ArrayList;
import java.util.List;

import sacred.alliance.magic.app.goods.GoodsOperateBean;

public class AddGoodsBeanResult extends GoodsResult{
	
	private List<GoodsOperateBean> putFailureList;
	private List<GoodsOperateBean> putSuccessList;
	
	public AddGoodsBeanResult(){
		putFailureList = new ArrayList<GoodsOperateBean>();
		putSuccessList = new ArrayList<GoodsOperateBean>();
	}

	public List<GoodsOperateBean> getPutFailureList() {
		return putFailureList;
	}

	public void setPutFailureList(List<GoodsOperateBean> putFailureList) {
		this.putFailureList = putFailureList;
	}
	
	public List<GoodsOperateBean> getPutSuccessList() {
		return putSuccessList;
	}

	public void setPutSuccessList(List<GoodsOperateBean> putSuccessList) {
		this.putSuccessList = putSuccessList;
	}

	public AddGoodsBeanResult success(){
		this.result = AddGoodsBeanResult.SUCCESS;
		return this;
	}
	
	public AddGoodsBeanResult setInfo(String info){
		this.info = info;
		return this;
	}
	
}
