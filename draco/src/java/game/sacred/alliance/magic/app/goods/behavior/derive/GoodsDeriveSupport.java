package sacred.alliance.magic.app.goods.behavior.derive;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sacred.alliance.magic.app.goods.GoodsOperateBean;
import sacred.alliance.magic.base.AttributeType;
import sacred.alliance.magic.base.OperatorType;
import sacred.alliance.magic.base.OutputConsumeType;
import sacred.alliance.magic.base.StorageType;
import sacred.alliance.magic.domain.GoodsBase;
import sacred.alliance.magic.domain.GoodsEquipment;
import sacred.alliance.magic.domain.GoodsRune;
import sacred.alliance.magic.domain.RoleGoods;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;

public abstract class GoodsDeriveSupport {
	
	public final static Logger logger = LoggerFactory.getLogger(GoodsDeriveSupport.class);
	
	public static RoleGoods getRoleGoods(RoleInstance role, byte bagType, 
			String goodsInstanceId,int targetId) {
		StorageType storageType = StorageType.get(bagType);
		RoleGoods roleGoods = GameContext.getUserGoodsApp().getRoleGoods(role, storageType, 
				goodsInstanceId,targetId);
		return roleGoods;
	}
	
	/**
	 * 衍生操作耗费的金钱
	 * @param role
	 * @param equRG
	 * @param type
	 */
	public static void payMoney(RoleInstance role, int gameMoney, OutputConsumeType ocType) {
		if(gameMoney <=0){
			return ;
		}
		GameContext.getUserAttributeApp().changeRoleMoney(role,
				AttributeType.gameMoney, OperatorType.Decrease, gameMoney,ocType);
		role.getBehavior().notifyAttribute();
	}
	
	public static boolean addGoodsMap(Map<Integer,Integer> map ,int goodsId,int num){
		if(goodsId > 0 && num >0){
			if(map.containsKey(goodsId)){
				map.put(goodsId, map.get(goodsId) + num);
				return true;
			}
			map.put(goodsId, num);
			return true ;
		}
		return false ;
	}
	
	public static boolean addGoodsList(List<GoodsOperateBean> list ,int goodsId,int num){
		if(goodsId > 0 && num >0){
			GoodsOperateBean gob = new GoodsOperateBean();
			gob.setGoodsId(goodsId);
			gob.setGoodsNum(num);
			list.add(gob);
			return true ;
		}
		return false ;
	}
	public static int getGoodsNum(RoleInstance role,int goodsId){
		if(0 >= goodsId){
			return 0 ;
		}
		return role.getRoleBackpack().countByGoodsId(goodsId);
	}
	
	public static GoodsRune getGemTemplate(int id){
		if(id <=0){
			return null ;
		}
		GoodsBase gb = null ;
		try{
			gb = GameContext.getGoodsApp().getGoodsBase(id);
		}catch(Exception ex){
			gb = null ;
		}
		if(null == gb || !(gb instanceof GoodsRune)){
			return null ;
		}
		return (GoodsRune)gb ;
	}
	
	public static void notifyGoodsInfo(RoleInstance role ,RoleGoods rg, byte bagType){
		if(null == role || null == rg){
			return ;
		}
		GameContext.getUserGoodsApp().syncSomeGoodsGridMessage(role, rg);
	}
	
	public static void initBaseAttrNotifyGoodsInfo(RoleInstance role ,RoleGoods rg,byte bagType){
		if(null == role || null == rg){
			return ;
		}
		notifyGoodsInfo(role,rg,bagType);
	}
	
	public static GoodsEquipment getGoodsEquipment(int id) {
		//获得装备模板属性
		return GameContext.getGoodsApp().getGoodsTemplate(GoodsEquipment.class, id);
	}
	
	public static int getGoodsNum(List<RoleGoods> goodsList){
		if(Util.isEmpty(goodsList)){
			return 0 ;
		}
		int num = 0;
		for(RoleGoods rg : goodsList){
			if(null == rg){
				continue ;
			}
			num += rg.getCurrOverlapCount();
		}
		return num;
	}
	
}
