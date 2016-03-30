package sacred.alliance.magic.app.goods;

import java.util.Comparator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.game.draco.GameContext;

import sacred.alliance.magic.domain.GoodsBase;
import sacred.alliance.magic.domain.RoleGoods;


/**
 * 物品排序类
 * @author Wang.K
 *
 */
public class GoodsComparator implements Comparator<RoleGoods>{
	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Override
	public int compare(RoleGoods rg1, RoleGoods rg2) {
		int result = 0;
		try {
			GoodsBase t1 = GameContext.getGoodsApp().getGoodsBase(rg1.getGoodsId());
			GoodsBase t2 = GameContext.getGoodsApp().getGoodsBase(rg2.getGoodsId());
			result = compareType(t1, t2);
			if (result == 0) {
				result = compareGoodsId(t1, t2);
				if (result == 0) {
					result = compareBindType(rg1, rg2);
				}
			}
			return result;
		} catch (Exception ex) {
			logger.error("sort storage error, " + ex);
			return result;
		}
	}
	
	
	
	/**
	 * 比较物品类型
	 * 
	 * @param t1
	 * @param t2
	 * @return
	 */
	private int compareType(GoodsBase t1, GoodsBase t2) {
		if (t1.getGoodsType() < t2.getGoodsType()) {
			return -1;
		} else if (t1.getGoodsType() > t2.getGoodsType()) {
			return 1;
		} else {
			return 0;
		}
	}
	
	/**
	 * 比较模板ID
	 */
	private int compareGoodsId(GoodsBase t1, GoodsBase t2) {
		if (t1.getId() < t2.getId()) {
			return -1;
		} else if (t1.getId() > t2.getId()) {
			return 1;
		} else {
			return 0;
		}
	}

	/**
	 * 比较绑定关系
	 */
	private int compareBindType(RoleGoods o1, RoleGoods o2) {
		if (o1.getBind() < o2.getBind()) {
			return -1;
		} else if (o1.getBind() > o2.getBind()) {
			return 1;
		} else {
			return 0;
		}
	}

}
