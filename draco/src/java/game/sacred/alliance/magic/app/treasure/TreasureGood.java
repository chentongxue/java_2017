package sacred.alliance.magic.app.treasure;

import java.util.ArrayList;
import java.util.List;

import com.game.draco.GameContext;
import com.game.draco.app.chat.ChannelType;

import lombok.Data;
import sacred.alliance.magic.app.goods.GoodsOperateBean;
import sacred.alliance.magic.app.goods.Util;
import sacred.alliance.magic.base.BindingType;
import sacred.alliance.magic.util.KeySupport;
import sacred.alliance.magic.util.Log4jManager;
import sacred.alliance.magic.util.Wildcard;
import sacred.alliance.magic.vo.RoleInstance;

public @Data class TreasureGood implements KeySupport<Integer>{
	private int id;
	private int bindMoney;
	private int gameMoneyMin;
	private int gameMoneyMax;
	
	private int goods1;
	private short num1;
	private byte bindType1 = BindingType.template.getType();
	
	private int goods2;
	private short num2;
	private byte bindType2 = BindingType.template.getType();
	
	private int goods3;
	private short num3;
	private byte bindType3 = BindingType.template.getType();
	
	private int goods4;
	private short num4;
	private byte bindType4 = BindingType.template.getType();
	
	private String broadcastInfo;
	//物品列表
	private List<GoodsOperateBean> goodsList = new ArrayList<GoodsOperateBean>() ;
	@Override
	public Integer getKey() {
		return this.id;
	}
	
	private void init(int goods, int num, byte bindType,
			String wildcardGoodsName, String wildcardGoodsNum) {
		if (goods <= 0 || num <= 0) {
			return;
		}
		goodsList.add(new GoodsOperateBean(goods, num, bindType));
		if (Util.isEmpty(broadcastInfo)) {
			return;
		}
		broadcastInfo = broadcastInfo
				.replace(
						wildcardGoodsName,
						Wildcard.getChatGoodsName(goods,
								ChannelType.Publicize_Personal)).replace(
						wildcardGoodsNum, "" + num);
	}
	
	
	public void init(){
		this.init(goods1, num1, bindType1, Wildcard.TrGoods1_Name, Wildcard.TrGoods1_Num);
		this.init(goods2, num2, bindType2, Wildcard.TrGoods2_Name, Wildcard.TrGoods2_Num);
		this.init(goods3, num3, bindType3, Wildcard.TrGoods3_Name, Wildcard.TrGoods3_Num);
		this.init(goods4, num4, bindType4, Wildcard.TrGoods4_Name, Wildcard.TrGoods4_Num);
		if(bindMoney > 0 && !Util.isEmpty(broadcastInfo)){
			broadcastInfo = broadcastInfo.replace(Wildcard.BindMoney, "" + bindMoney);
		}
		
	}
	
	public String getBroadcastTips(RoleInstance role, int silverMoney, String treasureName){
		return broadcastInfo.replace(Wildcard.Treasure_Name, treasureName)
								        .replace(Wildcard.Role_Name, role.getRoleName()).replace(Wildcard.GameMoney, "" + silverMoney);
	}
	
	private boolean haveCheck = false ;
	public void check(){
		if(haveCheck){
			return ;
		}
		haveCheck = true ;
		//判断配置的npc是否存在
		//检测配置的物品是否存在
		for(GoodsOperateBean bean : this.getGoodsList()){
			if(null == GameContext.getGoodsApp().getGoodsBase(bean.getGoodsId())){
				Log4jManager.checkFail();
				Log4jManager.CHECK.error("treasure goodsId = "+ bean.getGoodsId()+ " no exsit, id=" + this.getId());
			}
		}
	}
}
