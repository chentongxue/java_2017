package sacred.alliance.magic.app.user;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.game.draco.GameContext;
import com.game.draco.app.mail.domain.Mail;
import com.game.draco.app.mail.type.MailSendRoleType;
import com.game.draco.message.response.C0002_ErrorRespMessage;

import sacred.alliance.magic.app.goods.RoleGoodsHelper;
import sacred.alliance.magic.app.goods.WarehousePack;
import sacred.alliance.magic.app.goods.behavior.result.GoodsResult;
import sacred.alliance.magic.base.OutputConsumeType;
import sacred.alliance.magic.base.StorageType;
import sacred.alliance.magic.component.id.IdFactory;
import sacred.alliance.magic.component.id.IdType;
import sacred.alliance.magic.domain.RoleGoods;
import sacred.alliance.magic.util.Log4jManager;
import sacred.alliance.magic.vo.RoleInstance;

/**
 * 仓库
 */
public class UserWarehouseAppImpl implements UserWarehouseApp{
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	/** 加载仓库物品 */
	@Override
	public void loadWarehouseGoods(RoleInstance role) {
		WarehousePack pack = role.getWarehousePack();
		if(pack == null){
			int warehoseCapacity = 0;
			try{
			}catch(Exception e){
				logger.error("UserWarehouseApp loadWarehouseGoods error",e);
			}
			role.setWarehoseCapacity(warehoseCapacity);
			pack = new WarehousePack(role, warehoseCapacity);
			role.setWarehousePack(pack);
		}
	}
	
	/** 仓库批量提取 */
	@Override
	public GoodsResult batchTake(RoleInstance role, int takeType){
		GoodsResult result = null;
		WarehousePack warehouse = role.getWarehousePack();
		if(UserWarehouseApp.BATCH_TAKE_ALL == takeType){
			result = warehouse.takeAll();
		}
		else if(UserWarehouseApp.BATCH_TAKE_EQU == takeType){
			result = warehouse.takeAllEqu();
		}
		else if(UserWarehouseApp.BATCH_TAKE_CON == takeType){
			result = warehouse.takeAllConsume();
		}
		if(result.isSuccess()){
			result.syncBackpack(role, OutputConsumeType.goods_warehouse_take);
		}
		return result;
	}
	
	/**
	 * 背包物品存入仓库
	 */
	@Override
	public GoodsResult put(RoleInstance role, RoleGoods roleGoods, OutputConsumeType ocType){
		WarehousePack pack = role.getWarehousePack();
		GoodsResult result = pack.put(roleGoods);
		if(result.isSuccess()){
			result.sync(role, StorageType.warehouse, ocType);
		}
		return result;
	}
	
	
	
	@Override
	public GoodsResult put(RoleInstance role, List<RoleGoods> list, OutputConsumeType ocType){
		WarehousePack pack = role.getWarehousePack();
		GoodsResult result = pack.put(list);
		if(result.isSuccess()){
			result.sync(role, StorageType.warehouse, ocType);
		}
		return result;
	}
	
	
	/**
	 * 仓库取回物品到背包
	 * 同步背包消息
	 */
	@Override
	public GoodsResult take(RoleInstance role, RoleGoods roleGoods, OutputConsumeType ocType){
		WarehousePack pack = role.getWarehousePack();
		GoodsResult result = pack.take(roleGoods);
		if(result.isSuccess()){
			result.syncBackpack(role, ocType);
		}
		return result;
	}
	
	
	@Override
	public GoodsResult take(RoleInstance role, List<RoleGoods> list, OutputConsumeType ocType){
		WarehousePack pack = role.getWarehousePack();
		GoodsResult result = pack.take(list);
		if(result.isSuccess()){
			result.syncBackpack(role, ocType);
		}
		return result;
	}
	
	
	/**
	 * 整理仓库
	 */
	@Override
	public void reorganization(RoleInstance role,OutputConsumeType ocType){
		if(role == null){
			return ;
		}
		GoodsResult result = role.getWarehousePack().reorganization();
		if(!result.isSuccess()){
			C0002_ErrorRespMessage resp = new C0002_ErrorRespMessage();
			resp.setInfo(result.getInfo());
			role.getBehavior().sendMessage(resp);
			return ;
		}
		result.sync(role, StorageType.warehouse, ocType);
	}
	
	@Override
	public int onLogin(RoleInstance role, Object context) {
		// TODO Auto-generated method stub
		return 0;
	}
	
	/** 下线逻辑 */
	@Override
	public int onLogout(RoleInstance role, Object context) {
		try {
			if(null == role.getWarehousePack()) {
				return 1;
			}
			role.getWarehousePack().offline();
		} catch (Exception ex) {
			//offline内部已经调用
			//userGoodsApplication.offlineLog(roleInstance);
			Log4jManager.OFFLINE_ERROR_LOG.error("offlineWarehouseGoods error,roleId="
					+ role.getRoleId() + ",userId=" + role.getUserId(), ex);
			return 0;
		}
		
		return 1;
	}
	
	@Override
	public int onCleanup(String roleId, Object context) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void clearWarehouseAndMail(String roleId){
		try{
			RoleInstance role = GameContext.getOnlineCenter().getRoleInstanceByRoleId(roleId);
			if(role != null){
				WarehousePack pack = role.getWarehousePack();
				if(null == pack) {
					loadWarehouseGoods(role);
				}
				role.getWarehousePack().clearWarehouseAndMail();
				//!!!!!!!一定不能把仓库容器设置成null，否则离开公会再加入，会导致打开仓库时在数据库中查询，复制物品
				//!!!!!!!在一个登陆周期内，一旦容器已经有了，就一定不能销毁
				//role.setWarehousePack(null);
			}else{
				clearWarehouseOffline(roleId);
			}
		}catch(Exception e){
			logger.error("UserWarehouseApp clearWarehouseAndMail error",e);
		}
	}
	
	private void clearWarehouseOffline(String roleId){
		try{
			List<RoleGoods> list = GameContext.getBaseDAO().selectList(RoleGoods.class, 
					"roleId", roleId,"storageType", StorageType.warehouse.getType());
			
			for(RoleGoods roleGoods : list){
				RoleGoodsHelper.init(roleGoods);
				GameContext.getBaseDAO().delete(RoleGoods.class, RoleGoods.INSTANCEID, roleGoods.getId());
			}
			sendGoodsByMail(roleId, list);
		}catch(Exception e){
			logger.error("UserWarehouseApp clearWarehouseOffline error",e);
		}
	}
	
	@Override
	public void sendGoodsByMail(String roleId,List<RoleGoods> allGoods){
		String title = "仓库物品";
		String context = "仓库物品";
		OutputConsumeType ocType = OutputConsumeType.goods_warehouse_clear;
		try {
			Mail mail = new Mail(IdFactory.getInstance().nextId(IdType.MAIL));
			int affixNum = 0;
			for(RoleGoods roleGoods : allGoods) {
				if(null == roleGoods){
					continue;
				}
				if(affixNum >= Mail.MaxAccessoryNum){
					GameContext.getMailApp().sendMail(mail);//发送邮件
					mail = new Mail(IdFactory.getInstance().nextId(IdType.MAIL));//创建新邮件
					affixNum = 0;//重置附件物品数量
				}
				affixNum ++;//累计附件物品
				mail.setSendRole(MailSendRoleType.System.getName());
				mail.setTitle(title);
				mail.setContent(context);
				mail.setRoleId(roleId);
				mail.setSendSource(ocType.getType());
				mail.addRoleGoods(roleGoods);
			}
			if(affixNum > 0){//发送附件不满的邮件
				GameContext.getMailApp().sendMail(mail);
			}
		}catch(Exception e){
			logger.error("sendGoodsByMail error",e);
		}
	}
	
}
