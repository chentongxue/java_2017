package sacred.alliance.magic.app.goods;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sacred.alliance.magic.app.attri.AttriBuffer;
import sacred.alliance.magic.app.attri.AttriItem;
import sacred.alliance.magic.app.goods.derive.EquipRecatingAttrWeightConfig;
import sacred.alliance.magic.app.goods.derive.RecatingBoundBean;
import sacred.alliance.magic.base.AttributeType;
import sacred.alliance.magic.base.BindingType;
import sacred.alliance.magic.base.GoodsType;
import sacred.alliance.magic.base.StorageType;
import sacred.alliance.magic.constant.Cat;
import sacred.alliance.magic.domain.GoodsBase;
import sacred.alliance.magic.domain.GoodsEquipment;
import sacred.alliance.magic.domain.RoleGoods;
import sacred.alliance.magic.util.DateUtil;

import com.game.draco.GameContext;
import com.game.draco.app.rune.domain.MosaicRune;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class RoleGoodsHelper {
	
	//private static final float _10000f= 10000f ;
	protected static final Logger logger = LoggerFactory.getLogger(RoleGoods.class);
	private static final int EQUMENT_MAX_HOLE = 7;

	/** 过期删除物品 */
	public static boolean isExpiredDel(RoleGoods roleGoods) {
		try {
			GoodsBase gb = GameContext.getGoodsApp().getGoodsBase(roleGoods.getGoodsId());
			if (null == gb) {
				return false;
			}
			return gb.getExpireType() == 1;
		} catch (Exception e) {
			logger.error("RoleGoodsHelper.isExpiredDel:", e);
		}
		return false;
	}

	/** 得到需要合成的时间 */
	public static int getComposeTime(RoleGoods roleGoods) {
		if (isActivate(roleGoods)) {
			return (int) DateUtil.dateDiffMinute(roleGoods.getExpiredTime(), new Date());
		}
		return roleGoods.getDeadline();
	}

	/** 删除物品并发送邮件 */
	public static boolean isDelSendMail(RoleGoods roleGoods) {
		return !isForever(roleGoods) && isExpiredDel(roleGoods) && isExpired(roleGoods);
	}

	/** 添加合成的时间 */
	public static void addComposeTime(RoleGoods roleGoods, int minute, boolean endGoodsIsActivate) {
		if (isExpired(roleGoods)) {
			roleGoods.setExpiredTime(DateUtil.add(new Date(), Calendar.MINUTE, minute));
			return;
		}
		if (isActivate(roleGoods)) {
			roleGoods.setExpiredTime(DateUtil.add(roleGoods.getExpiredTime(), Calendar.MINUTE, minute));
			return;
		}
		if (endGoodsIsActivate) {
			roleGoods.setExpiredTime(DateUtil.add(new Date(), Calendar.MINUTE, roleGoods.getDeadline() + minute));
			return;
		}
		roleGoods.setDeadline(roleGoods.getDeadline() + minute);
	}

	/** 过期 */
	public static boolean isExpired(RoleGoods roleGoods) {
		return !isForever(roleGoods) && isActivate(roleGoods) && (new Date().after(roleGoods.getExpiredTime()));
	}

	/** 永久物品 */
	public static boolean isForever(RoleGoods roleGoods) {
		GoodsBase gb = GameContext.getGoodsApp().getGoodsBase(roleGoods.getGoodsId());
		if (null == gb) {
			return false;
		}
		return gb.getDeadline() == 0;
	}

	/** 已经激活使用时间 */
	public static boolean isActivate(RoleGoods roleGoods) {
		return null != roleGoods.getExpiredTime();
	}

	/**
	 * 折分随机属性
	 */
	public static void splitAttrVar(RoleGoods roleGoods) {
		List<String[]> attrArray = Util.splitMultStr(roleGoods.getAttrVar(), Cat.comma, Cat.colon);
		if (null == attrArray) {
			return;
		}
		for (String[] array : attrArray) {
			roleGoods.getAttrVarList().add(new AttriItem(Byte.parseByte(array[0]), Util.toValue(array[1]), 0f));
		}
	}

	public static GoodsEquipment getGoodsEquipment(RoleGoods roleGoods) {
		GoodsBase gb = GameContext.getGoodsApp().getGoodsBase(roleGoods.getGoodsId());
		if (null == gb || !(gb instanceof GoodsEquipment)) {
			return null;
		}
		return (GoodsEquipment) gb;
	}


	/**
	 * 折分镶嵌
	 */
	public static void splitHoles(RoleGoods roleGoods) {
		GoodsEquipment ge = getGoodsEquipment(roleGoods);
		if (null == ge) {
			// 如果不是装备则不可镶嵌
			return;
		}
		if (Util.isEmpty(roleGoods.getMosaic())) {
			// 目前没有镶嵌,但以后可以镶嵌
			roleGoods.setMosaicRune(new MosaicRune[EQUMENT_MAX_HOLE]);
			return;
		}
		splitMosaic(roleGoods);
	}

	/**
	 * 拆分镶嵌
	 * @param roleGoods
	 * @param templateHoles
	 */
	private static void splitMosaic(RoleGoods roleGoods) {
		Map<Byte, MosaicRune> mosaicRuneMap = GameContext.getRuneApp().getMosaicRuneMap(roleGoods.getMosaic());
		roleGoods.setMosaicRune(new MosaicRune[EQUMENT_MAX_HOLE]);
		for (MosaicRune mosaicRune : mosaicRuneMap.values()) {
			if (null == mosaicRune) {
				continue;
			}
			roleGoods.getMosaicRune()[mosaicRune.getHole()] = mosaicRune;
		}
	}

	/** 得到随机属性列数之和 */
	public static int getSumAttrVarList(RoleGoods roleGoods) {
		int colSum = 0;
		ArrayList<AttriItem> attrVarList = roleGoods.getAttrVarList();
		if (Util.isEmpty(attrVarList)) {
			return colSum;
		}
		for (AttriItem item : attrVarList) {
			if (null == item) {
				continue;
			}
			colSum += item.getValue();
		}
		return colSum;
	}

	public static boolean hadMosaicRune(RoleGoods roleGoods) {
		MosaicRune[] mosaicRunes = roleGoods.getMosaicRune();
		for (MosaicRune hole : mosaicRunes) {
			if (null == hole || hole.getGoodsId() <= 0) {
				continue;
			}
			return true;
		}
		return false;
	}

	/**
	 * 统计镶嵌在装备身上等级超过level的个数
	 * @param level
	 * @return
	 */
	public static int countGemLevel(RoleGoods roleGoods, int level) {
		MosaicRune[] mosaicRunes = roleGoods.getMosaicRune();
		if (null == mosaicRunes || 0 == mosaicRunes.length) {
			return 0;
		}
		int total = 0;
		for (MosaicRune hole : mosaicRunes) {
			if (null == hole || hole.getGoodsId() <= 0) {
				continue;
			}
			GoodsBase gb = GameContext.getGoodsApp().getGoodsBase(hole.getGoodsId());
			if (null == gb) {
				continue;
			}
			if (gb.getLevel() < level) {
				continue;
			}
			total++;
		}
		return total;
	}
	

	/**
	 * 统计洗练属性品质>=quality的个数
	 * @param roleGoods
	 * @param quality
	 * @return
	 */
	public static int countRecastingAttribute(RoleGoods roleGoods, int quality) {
		int total = 0;
		for (AttriItem ai : roleGoods.getAttrVarList()) {
			if (null == ai) {
				continue;
			}
			byte attriType = ai.getAttriTypeValue();
			EquipRecatingAttrWeightConfig awc = GameContext.getGoodsApp().getEquipRecatingAttrWeightConfig(attriType, roleGoods.getQuality(), roleGoods.getStar());
			if (null == awc) {
				continue;
			}
			int value = (int) ai.getValue();
			RecatingBoundBean bean = awc.getRecatingBoundBean(value);
			if (null == bean) {
				continue;
			}
			if (bean.getQualityType() < quality) {
				continue;
			}
			total++;
		}
		return total;
	}

	/**
	 * 属性变化量合并
	 */
	public static void uniteAttrVar(RoleGoods roleGoods) {
		ArrayList<AttriItem> attrVarList = roleGoods.getAttrVarList();
		if (Util.isEmpty(attrVarList)) {
			roleGoods.setAttrVar("");
		}
		StringBuffer buffer = new StringBuffer();
		String tempCat = "";
		for (AttriItem item : attrVarList) {
			if (item == null) {
				continue;
			}
			if (item.getValue() != 0) {
				buffer.append(tempCat);
				buffer.append(item.getAttriTypeValue());
				buffer.append(Cat.colon);
				buffer.append((int) item.getValue());
				tempCat = Cat.comma;
				continue;
			}
		}
		roleGoods.setAttrVar(buffer.toString());
	}

	/**
	 * 镶嵌合并
	 */
	public static void uniteHoles(RoleGoods roleGoods) {
		GoodsEquipment ge = getGoodsEquipment(roleGoods);
		if (null == ge) {
			return;
		}
		uniteMosaic(roleGoods);
	}
	
	// 镶嵌合并
	private static void uniteMosaic(RoleGoods roleGoods) {
		MosaicRune[] mosaicRunes = roleGoods.getMosaicRune();
		Map<Byte, MosaicRune> mosaicRuneMap = Maps.newHashMap();
		for (MosaicRune mosaic : mosaicRunes) {
			if (null == mosaic) {
				continue;
			}
			mosaicRuneMap.put(mosaic.getHole(), mosaic);
		}
		if (Util.isEmpty(mosaicRuneMap)) {
			// 如果没有镶嵌符文
			roleGoods.setMosaic("");
			return;
		}
		roleGoods.setMosaic(GameContext.getRuneApp().getMosaicString(mosaicRuneMap));
	}

	/**
	 * 初始化装备基本属性、强化信息、镶嵌信息、
	 */
	public static void init(RoleGoods roleGoods) {
		if (roleGoods.getHadInit() == 1) {
			return;
		}
		// 解析装备属性及镶嵌属性
		splitAttrVar(roleGoods);
		splitHoles(roleGoods);
		roleGoods.setHadInit((byte) 1);
	}

	// 容错过期时间(单写一方法在此调用)
	public static void checkGoodsExpiredTime(RoleGoods roleGoods) {
		try {
			GoodsBase goodsBase = GameContext.getGoodsApp().getGoodsBase(roleGoods.getGoodsId());
			if (null == goodsBase) {
				return;
			}
			if (goodsBase.isForever()) {
				return;
			}

			int bagType = roleGoods.getStorageType();
			boolean on = bagType == StorageType.hero.getType();

			boolean mustActive = (goodsBase.getActivateType() == 1) || (goodsBase.getActivateType() == 0 && on);
			if (mustActive) {
				if (!Util.isEmpty(roleGoods.getExpiredTime())) {
					return;
				}
				Date endDate = DateUtil.add(new Date(), Calendar.MINUTE, goodsBase.getDeadline());
				roleGoods.setExpiredTime(endDate);
				return;
			}
			if (roleGoods.getDeadline() > 0) {
				return;
			}
			roleGoods.setDeadline(goodsBase.getDeadline());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void destructor(RoleGoods roleGoods) {
		uniteAttrVar(roleGoods);
		uniteHoles(roleGoods);
	}

	/**
	 * 获得道具全部属性，包含模板属性
	 * 
	 * @return
	 */
	public static AttriBuffer getAttriBuffer(RoleGoods roleGoods) {
		AttriBuffer buffer = AttriBuffer.createAttriBuffer();
		// 模板属性
		int goodsId = roleGoods.getGoodsId();
		GoodsBase goodsBase = GameContext.getGoodsApp().getGoodsBase(goodsId);
		if (goodsBase == null) {
			return buffer;
		}
		buffer.append(goodsBase.getAttriItemList());
		if(GoodsType.GoodsRune.getType() == goodsBase.getGoodsType()){
			buffer.append(buildRandomAttri(roleGoods));
			return buffer ;
		}
		if(! goodsBase.isEquipment()){
			//非装备
			return buffer;
		}
		buffer.append(GameContext.getEquipApp().getBaseAttriBuffer(goodsId, roleGoods.getQuality(),
				roleGoods.getStar()));
		//获得强化属性
		buffer.append(GameContext.getEquipApp().getStrengthenAttriBuffer(goodsId,
					roleGoods.getQuality(), roleGoods.getStar(), roleGoods.getStrengthenLevel()));
		buffer.append(buildRandomAttri(roleGoods));
		// 获得镶嵌宝石属性
		buffer.append(getMosaicArrti(roleGoods));
		return buffer;
	}

	public static int getEquipScore(RoleGoods roleGoods) {
		GoodsBase goodsBase = GameContext.getGoodsApp().getGoodsBase(roleGoods.getGoodsId());
		if (null == goodsBase || !goodsBase.isEquipment()) {
			return 0;
		}
		AttriBuffer buffer = getAttriBuffer(roleGoods);
		if (null == buffer || buffer.isEmpty()) {
			return 0;
		}
		return GameContext.getAttriApp().getAttriBattleScore(buffer);
	}

	public static int get(AttributeType attriType, AttriBuffer buffer) {
		AttriItem item = buffer.getAttriItem(attriType);
		if (null == item) {
			return 0;
		}
		return (int) item.getValue();
	}

	/**
	 * 随机属性转化：数据库内记录的是所属系列值，需转化为真实值
	 * @param randomList
	 * @return
	 */
	public static List<AttriItem> buildRandomAttri(RoleGoods roleGoods){
		return roleGoods.getAttrVarList();
	}

	/**
	 * 获得镶嵌宝石具体属性
	 * @return
	 */
	public static List<AttriItem> getMosaicArrti(RoleGoods roleGoods) {
		GoodsEquipment ge = getGoodsEquipment(roleGoods);
		if (null == ge) {
			// 如果不是装备则不可镶嵌
			return null;
		}
		return getGoodsMosaicArrti(roleGoods);
	}

	private static List<AttriItem> getGoodsMosaicArrti(RoleGoods roleGoods) {
		Collection<AttriItem> attriItems = GameContext.getRuneApp().getMosaicRuneAttriList(roleGoods.getMosaicRune());
		if (Util.isEmpty(attriItems)) {
			return null;
		}
		List<AttriItem> list = Lists.newArrayList();
		list.addAll(attriItems);
		return list;
	}

	/**
	 * !!!! 以前这个方法名叫 isBind(),不要更改回去,否则BeanUtils.copyProperties 此字段无法获得
	 * @return
	 */
	public static boolean hadBind(RoleGoods roleGoods) {
		return BindingType.already_binding.getType() == roleGoods.getBind();
	}

	/** 获得该物品模板信息 */
	public static GoodsBase getGoodsBase(RoleGoods roleGoods) {
		return GameContext.getGoodsApp().getGoodsBase(roleGoods.getGoodsId());
	}

	/** 获得物品类型 */
	public static GoodsType getGoodsType(RoleGoods roleGoods) {
		GoodsBase goodsBase = getGoodsBase(roleGoods);
		if (goodsBase == null) {
			return GoodsType.GoodsDefault;
		}
		return GoodsType.get(goodsBase.getGoodsType());
	}

	/**
	 * 下线是否消失
	 * @return
	 */
	public static boolean isOfflineDie(RoleGoods roleGoods) {
		GoodsBase goodsBase = GameContext.getGoodsApp().getGoodsBase(roleGoods.getGoodsId());
		if (goodsBase == null) {
			return false;
		}
		return goodsBase.hasOfflineDie();
	}

	public static int getEquipLocation(RoleGoods roleGoods) {
		int goodsId = roleGoods.getGoodsId();
		GoodsBase goodsBase = GameContext.getGoodsApp().getGoodsBase(goodsId);
		if (goodsBase instanceof GoodsEquipment) {
			GoodsEquipment equipment = (GoodsEquipment) goodsBase;
			return equipment.getEquipslotType();
		}
		return -1;
	}
	
}
