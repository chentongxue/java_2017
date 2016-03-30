package sacred.alliance.magic.domain;
import java.util.ArrayList;
import java.util.Date;

import lombok.Data;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sacred.alliance.magic.app.attri.AttriItem;
import sacred.alliance.magic.app.goods.RoleGoodsHelper;
import sacred.alliance.magic.app.goods.Util;
import sacred.alliance.magic.base.SaveDbStateType;
import sacred.alliance.magic.constant.Cat;
import sacred.alliance.magic.util.DateUtil;
import sacred.alliance.magic.util.Log4jManager;

import com.alibaba.fastjson.annotation.JSONField;
import com.game.draco.GameContext;
import com.game.draco.app.rune.domain.MosaicRune;

/**
 * 角色物品
 * @author Administrator
 */
public @Data class RoleGoods implements Cloneable,java.io.Serializable{
	public final static String INSTANCEID = "id";
	public final static String ROLE_ID = "roleId" ;
	public final static String STORAGE_TYPE = "storageType" ;
	protected static final Logger logger = LoggerFactory.getLogger(RoleGoods.class);
	private String id = "";
	private String roleId = "";//角色id
	private byte storageType;//storageType：1:背包 2:仓库 3:装备上4.邮件
	private short gridPlace;//格子位置
	private int goodsId;//物品id
	private short strengthenLevel;//强化等级
	private byte quality ;
	private byte star ;
	private int currOverlapCount;//当前叠放数量
	private byte bind;//是否绑定 0:没有绑定 1:已经绑定
	//随机装备属性类型[类型1:col1,类型2：col2](只是用于随机装备)
	private String attrVar = "";
	//镶嵌[宝石ID1:绑定类型,宝石ID2:绑定类型]
	private String mosaic = "";
	//特殊公用参数，不同类型的物品代表意义不同
	//英雄装备存放的是英雄id
	private String otherParm = "";
	private Date expiredTime;//	date	有效期：
	private int deadline;//	int	有效期限（分钟）
	
	//洗练的属性，与attrVar字段相对应
	@JSONField(serialize=false)
	private ArrayList<AttriItem> attrVarList = new ArrayList<AttriItem>();
	/**
	 * 镶嵌的宝石列表
	 */
	@JSONField(serialize=false)
	private MosaicRune[] mosaicRune;// 镶嵌宝石列表
	@JSONField(serialize=false)
	private byte hadInit = 0; //初始化状态位，不对外开放
	//是否为预留物品，用于占物品栏格子使用
	@JSONField(serialize=false)
	private boolean spaceOccupying = false; 
	//入库状态
	@JSONField(serialize=false)
	private SaveDbStateType saveDbState = SaveDbStateType.Update;
	
	@JSONField(serialize=false)
	private boolean writeDb = true;//是否要有数据库操作
	
	public void offlineSaveDb(){
		if(this.isSpaceOccupying() || RoleGoodsHelper.isOfflineDie(this)){
			return ;
		}
		RoleGoodsHelper.destructor(this);
		if(this.saveDbState == SaveDbStateType.Insert){
			GameContext.getBaseDAO().insert(this);
			this.setSaveDbState(SaveDbStateType.Update);
			changeNotWrite();
			return;
		}
		if(this.writeDb){
			GameContext.getBaseDAO().update(this);
		}
	}
	
	private boolean equals(String src, String dest) {
		boolean srcNull = Util.isEmpty(dest);
		boolean destNull = Util.isEmpty(src);
		if(srcNull && destNull) {
			return true;
		}
		if(srcNull || destNull) {
			return false;
		}
		return src.equals(dest);
	}
	
	private boolean equals(Date src, Date dest) {
		boolean srcNull = Util.isEmpty(dest);
		boolean destNull = Util.isEmpty(src);
		if(srcNull && destNull) {
			return true;
		}
		if(srcNull || destNull) {
			return false;
		}
		return src.getTime() == dest.getTime();
	}
	
	private void changeWrite(){
		this.writeDb = true;
	}
	
	public void changeNotWrite(){
		this.writeDb = false;
	}

	public void setId(String id) {
		if(this.equals(this.id, id)) {
			return;
		}
		changeWrite();
		this.id = id;
	}

	public void setStorageType(byte storageType) {
		if(this.storageType == storageType) {
			return;
		}
		changeWrite();
		this.storageType = storageType;
	}

	public void setGoodsId(int goodsId) {
		if(this.goodsId == goodsId) {
			return;
		}
		changeWrite();
		this.goodsId = goodsId;
	}

	public void setStrengthenLevel(short level) {
		if(this.strengthenLevel == level) {
			return;
		}
		changeWrite();
		this.strengthenLevel = level;
	}

	public void setCurrOverlapCount(int currOverlapCount) {
		if(this.currOverlapCount == currOverlapCount) {
			return;
		}
		changeWrite();
		this.currOverlapCount = currOverlapCount;
	}

	public void setBind(byte bind) {
		if(this.bind == bind) {
			return;
		}
		changeWrite();
		this.bind = bind;
	}
	
	public void setQuality(byte quality) {
		if(this.quality == quality) {
			return;
		}
		changeWrite();
		this.quality = quality;
	}
	
	public void setStar(byte star) {
		if(this.star == star) {
			return;
		}
		changeWrite();
		this.star = star;
	}

	public void setAttrVar(String attrVar) {
		if(this.equals(this.attrVar, attrVar)) {
			return;
		}
		changeWrite();
		this.attrVar = attrVar;
	}

	public void setMosaic(String mosaic) {
		if(this.equals(this.mosaic, mosaic)) {
			return;
		}
		changeWrite();
		this.mosaic = mosaic;
	}

	public void setOtherParm(String otherParm) {
		if(this.equals(this.otherParm, otherParm)) {
			return;
		}
		changeWrite();
		this.otherParm = otherParm;
	}

	public void setExpiredTime(Date expiredTime) {
		if(this.equals(this.expiredTime, expiredTime)) {
			return;
		}
		changeWrite();
		this.expiredTime = expiredTime;
	}

	public void setDeadline(int deadline) {
		if(this.deadline == deadline) {
			return;
		}
		changeWrite();
		this.deadline = deadline;
	}

	public void setRoleId(String roleId) {
		if(this.equals(this.roleId, roleId)) {
			return;
		}
		changeWrite();
		this.roleId = roleId;
	}
	
	@Override
	public RoleGoods clone() {
		try {
			RoleGoods newGoods = (RoleGoods) super.clone();
			if(this.mosaicRune != null){
				newGoods.mosaicRune = (MosaicRune[])this.mosaicRune.clone();
			}
			newGoods.attrVarList = (ArrayList<AttriItem>)attrVarList.clone();
			return newGoods;
		} catch (CloneNotSupportedException ex) {
			ex.printStackTrace();
		}
		return null;
	}
	
	public void offlineLog(){
		try{
			StringBuffer sb = new StringBuffer();
			sb.append(id);
			sb.append(Cat.pound);
			sb.append(roleId);
			sb.append(Cat.pound);
			sb.append(storageType);
			sb.append(Cat.pound);
			sb.append(goodsId);
			sb.append(Cat.pound);
			sb.append(mosaic);
			sb.append(Cat.pound);
			sb.append(currOverlapCount);
			sb.append(Cat.pound);
			sb.append(bind);
			sb.append(Cat.pound);
			sb.append(attrVar);
			sb.append(Cat.pound);
			sb.append(this.strengthenLevel);
			sb.append(Cat.pound);
			sb.append(this.quality);
			sb.append(Cat.pound);
			sb.append(this.star);
			sb.append(Cat.pound);
			sb.append(otherParm);
			sb.append(Cat.pound);
			sb.append(deadline);
			sb.append(Cat.pound);
			sb.append(DateUtil.getTimeByDate(expiredTime));
			sb.append(Cat.pound);
			Log4jManager.OFFLINE_GOODS_DB_LOG.info(sb.toString());
		}catch(Exception e){
			logger.error("logoutLog:",e);
		}
	}
}
