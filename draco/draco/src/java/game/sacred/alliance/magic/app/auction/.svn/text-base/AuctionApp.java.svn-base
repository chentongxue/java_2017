package sacred.alliance.magic.app.auction;

import java.util.List;

import com.game.draco.message.item.AuctionMyShelfItem;
import com.game.draco.message.item.AuctionSearchItem;
import com.game.draco.message.request.C0858_AuctionMenuReqMessage;
import com.game.draco.message.request.C0850_AuctionSearchReqMessage;

import platform.message.item.MercuryRecordItem;
import platform.message.response.C5401_MercurySearchRespMessage;
import sacred.alliance.magic.base.Money;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.core.Service;
import sacred.alliance.magic.domain.GoodsBase;
import sacred.alliance.magic.domain.RoleGoods;
import sacred.alliance.magic.vo.RoleInstance;

public interface AuctionApp extends Service {
	
	public static final short AuctionMenuCmdId = new C0858_AuctionMenuReqMessage().getCommandId();
	
	public List<AuctionMenu> getAuctionMenuList(); 
	
	public SearchResult getRoleAuctionList(RoleInstance role);
	
	public SearchResult search(RoleInstance role,C0850_AuctionSearchReqMessage searchReq);
	
	public AuctionSearchItem convertSearchItem(MercuryRecordItem record) ;
	
	public AuctionMyShelfItem convertMyShelfItem(MercuryRecordItem record);
	
	/**物品上架*/
	public Result putGoods(RoleInstance role,String goodsId,Money money,int timeHours) ;
	
	/**钱币上架*/
	public Result putMoney(RoleInstance role,Money money,Money price,int timeHours);
	
	/**退市*/
	public Result delisting(RoleInstance role,String id) ;
	
	public Result buy(RoleInstance role,String id);
	
	public MercuryRecordItem getRecord(String id);
	
	public RoleGoods toRoleGoods(MercuryRecordItem item);
	
	public void expiredRecords();
	
	//public MercuryRecordItem toMercuryRecordItem(RoleGoods roleGoods);
	
	public FeeInfoConfig getFeeInfoConfig() ;
	
	public byte getBindType(GoodsBase goodsBase) ;
	
	/**
	 * 查询角色的拍卖行物品信息
	 * @param roleId
	 * @return
	 * @throws Exception
	 */
	public C5401_MercurySearchRespMessage queryRoleAuctionList(String roleId) throws Exception;
	
	/**
	 * 封停的角色拍卖的物品下架放入角色邮箱中
	 * @param roleId
	 */
	public void frozenRoleDownShelf(String roleId);
	
	public void notifyHaveExpiredGoods(RoleInstance role);
	
}
