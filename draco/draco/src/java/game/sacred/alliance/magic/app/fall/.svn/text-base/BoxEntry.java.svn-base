package sacred.alliance.magic.app.fall;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.game.draco.GameContext;
import com.game.draco.message.item.FallItem;
import com.game.draco.message.push.C0600_FallBoxNotifyMessage;
import com.game.draco.message.response.C0603_FallPickupRespMessage;

import sacred.alliance.magic.app.goods.GoodsOperateBean;
import sacred.alliance.magic.app.goods.behavior.result.AddGoodsBeanResult;
import sacred.alliance.magic.base.BindingType;
import sacred.alliance.magic.constant.RespTypeStatus;
import sacred.alliance.magic.constant.Status;
import sacred.alliance.magic.module.cache.Cache;
import sacred.alliance.magic.util.Converter;
import sacred.alliance.magic.vo.AbstractRole;
import sacred.alliance.magic.vo.MapInstance;
import sacred.alliance.magic.vo.RoleInstance;

public class BoxEntry implements Box{
	private static int BOX_EXIST_MIllIS_TIME = 5000;
	private MapInstance mapInstance ;
	//box里面不能存放RoleInstance,否则上线后未过期的box不能显示
	//role.getBehavior().sendMessage方法没有发送成功
	//因为下线后role的session已经断开，上线后又没法修改box中的role对象
	
	//private RoleInstance role ;
	private String roleId = "" ;
	private int x ;
	private int y ;
	private int outputType;
	private List<GoodsOperateBean> items = new ArrayList<GoodsOperateBean>();
	private String id ;
	
	/*public BoxEntry(RoleInstance role,
			Map<Integer,Integer> itemMap,String id,int x,int y){
		//this.role = role ;
		this.roleId = role.getRoleId();
		MapInstance mapInstance = role.getMapInstance();
		this.mapInstance = mapInstance ;
		for(Iterator<Map.Entry<Integer, Integer>> 
			it = itemMap.entrySet().iterator();it.hasNext();){
			Map.Entry<Integer, Integer> entry = it.next();
			this.put(entry.getKey(), entry.getValue());
		}
		this.x = x ;
		this.y = y ;
		this.id = id ;
	}*/
	public BoxEntry(RoleInstance role,
			List<GoodsOperateBean> itemList,String id,int x,int y,int outputType){
		this.roleId = role.getRoleId();
		MapInstance mapInstance = role.getMapInstance();
		this.mapInstance = mapInstance ;
		for(GoodsOperateBean agb : itemList){
			this.put(agb.getGoodsId(), agb.getGoodsNum(),agb.getBindType());
		}
		this.x = x ;
		this.y = y ;
		this.id = id ;
		this.outputType = outputType;
	}
	@Override
	public void destory() {
		if (null == mapInstance) {
			return;
		}
		mapInstance.clearBox(this);
	}

	@Override
	public boolean isNull() {
		return false;
	}

	@Override
	public List<GoodsOperateBean> list() {
		return items ;
	}

	@Override
	public void put(int goodsId, int num,BindingType bindType) {
		if(num <=0){
			return ;
		}
		for(Iterator<GoodsOperateBean> it = this.items.iterator();it.hasNext();){
			GoodsOperateBean item = it.next();
			if(goodsId == item.getGoodsId()){
				item.setGoodsNum(item.getGoodsNum()+num);
			}
		}
		GoodsOperateBean item = new GoodsOperateBean();
		item.setGoodsId(goodsId);
		item.setGoodsNum(num);
		item.setBindType(bindType);
		this.items.add(item);
	}

	@Override
	public void remove(int goodsId) {
		if(this.isEmpty()){
			return ;
		}
		for(Iterator<GoodsOperateBean> it = this.items.iterator();it.hasNext();){
			GoodsOperateBean item = it.next();
			if(goodsId == item.getGoodsId()){
				it.remove();
				return ;
			}
		}
	}

	@Override
	public boolean isEmpty() {
		return null == this.items || 0 == this.items.size();
	}

	@Override
	public void pickup(AbstractRole picker, int goodsId) {
		
		synchronized (this) {
			AddGoodsBeanResult result = GameContext.getFallApp().pickupAction(id, this.getOwner(), goodsId,
					this.listFallItem(),this.outputType);
			List<GoodsOperateBean> successList = result.getPutSuccessList();
			boolean havePick = false;
			for(Iterator<GoodsOperateBean> it = this.items.iterator();it.hasNext();){
				GoodsOperateBean item = it.next();
				if(this.isSuccess(successList, item)){
					havePick = true;
					it.remove();
				}
			}
			C0603_FallPickupRespMessage respMsg = new C0603_FallPickupRespMessage();
			respMsg.setInstanceId(this.id);
			respMsg.setItemId(goodsId);
			if(0 == this.items.size()){
				mapInstance.clearBox(this);
				respMsg.setStatus(RespTypeStatus.SUCCESS);
				picker.getBehavior().sendMessage(respMsg);
				return ;
			}
			//如果是单个的提取成功
			if(havePick && goodsId > 0){
				respMsg.setStatus(RespTypeStatus.SUCCESS);
			}else{
				//拾取失败的物品返回
				respMsg.setStatus(RespTypeStatus.FAILURE);
				respMsg.setInfo(Status.GOODS_BACKPACK_FULL.getTips());
			}
			picker.getBehavior().sendMessage(respMsg);
		}
	}
	/**已经成功拾取*/
	private boolean isSuccess(List<GoodsOperateBean> successList,GoodsOperateBean item){
		for(GoodsOperateBean agb : successList){
			if((agb.getGoodsId() == item.getGoodsId()) /*&& (agb.getBindType() == item.getBindType())*/){
				return true;
			}
		}
		return false;
	}
	
	/*@Override
	public void pickup(AbstractRole picker, int goodsId) {
		synchronized (this) {
			Map<Integer,Integer> successMap = GameContext.getFallApp().pickupAction(id, this.getOwner(), 
					goodsId,this.listFallItem());
			if(null != successMap && successMap.size()>0){
				for(Iterator<Item> it = this.items.iterator();it.hasNext();){
					Item item = it.next();
					if(successMap.containsKey(item.getGoodsId())){
						it.remove();
					}
				}
			}
			if(0 == this.items.size()){
				mapInstance.clearBox(this);
			}
		}
	}*/

	@Override
	public void notifyOwner() {
		//通知用户box的存在
		Cache<String, BoxEntry> boxes = GameContext.getMapApp().getBoxesCache();
		int delayTime = (int)boxes.getRemain(id);
		if(BOX_EXIST_MIllIS_TIME >= delayTime){
			return;
		}
		C0600_FallBoxNotifyMessage notify = new C0600_FallBoxNotifyMessage();
		notify.setInstanceId(id);
		notify.setX((short) x);
		notify.setY((short) y);
		notify.setRemainTime(delayTime-BOX_EXIST_MIllIS_TIME);
		
		this.getOwner().getBehavior().sendMessage(notify);
	}

	@Override
	public boolean cache() {
		mapInstance.putBox(this.getOwner(), this.id, this);
		return true;
	}

	
	@Override
	public String getBoxId() {
		return this.id;
	}

	@Override
	public List<FallItem> listFallItem() {
		List<FallItem> fallItemList = new ArrayList<FallItem>();
		if(this.isEmpty()){
			return fallItemList ;
		}
		for(GoodsOperateBean item : this.items){
			fallItemList.add(Converter.getFallItem(GoodsOperateBean.createAddGoodsBean(item.getGoodsId(), item.getGoodsNum(), item.getBindType().getType())));
		}
		return fallItemList;
	}

	@Override
	public boolean isOwner(RoleInstance role) {
		if(null == role || null == this.roleId){
			return false ;
		}
		return role.getRoleId().equals(this.roleId);
	}

	@Override
	public RoleInstance getOwner() {
		return GameContext.getOnlineCenter().getRoleInstanceByRoleId(this.roleId);
	}

	

	
}
