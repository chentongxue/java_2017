package sacred.alliance.magic.app.benefit;

import java.util.ArrayList;
import java.util.List;

import com.game.draco.GameContext;

import lombok.Data;
import sacred.alliance.magic.app.goods.GoodsOperateBean;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.domain.GoodsBase;
import sacred.alliance.magic.vo.RoleInstance;

public @Data class LoginCount {
	
	private int index;//连续登录天数
	//private byte imageId;//图片ID
	//private String name;//奖励名称
	private int minLevel;//等级下限
	private int maxLevel;//等级上限
	/*private int bindMoney;//绑金
	private int silverMoney;//银币*/
	private int goodsId1;//奖励1ID
	private int goodsNum1;//奖励1数量	
	private int bind1;//奖励1绑定类型
	private int goodsId2;
	private int goodsNum2;
	private int bind2;
	private int goodsId3;
	private int goodsNum3;
	private int bind3;
	//奖励物品列表
	private List<GoodsOperateBean> goodsList = new ArrayList<GoodsOperateBean>();
	
	public Result checkAndInit(){
		Result result = new Result();
		String info = "index=" + this.index + ",minLevel=" + this.maxLevel + ",maxLevel=" + this.maxLevel + ".";
		this.addMustGoods(this.goodsId1, this.goodsNum1, this.bind1);
		this.addMustGoods(this.goodsId2, this.goodsNum2, this.bind2);
		this.addMustGoods(this.goodsId3, this.goodsNum3, this.bind3);
		//验证物品是否存在
		for(GoodsOperateBean bean : this.goodsList){
			if(null == bean){
				continue;
			}
			int goodsId = bean.getGoodsId();
			GoodsBase goodsBase = GameContext.getGoodsApp().getGoodsBase(goodsId);
			if(null == goodsBase){
				return result.setInfo(info + "goodsId=" + goodsId + ",this goods is not exist!");
			}
		}
		return result.success();
	}
	
	/** 添加奖励物品 */
	private void addMustGoods(int goodsId, int goodsNum, int bind){
		if(goodsId <= 0 || goodsNum <= 0){
			return;
		}
		this.goodsList.add(new GoodsOperateBean(goodsId, goodsNum, bind));
	}
	
	/**
	 * 是否满足等级需求
	 * @param role
	 * @return
	 */
	public boolean isSuitLevel(RoleInstance role){
		if(null == role){
			return false;
		}
		int roleLevel = role.getLevel();
		return roleLevel >= this.minLevel && roleLevel <= this.maxLevel;
	}
	
}
