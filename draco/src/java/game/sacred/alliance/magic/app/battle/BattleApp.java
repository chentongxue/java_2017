package sacred.alliance.magic.app.battle;

import com.game.draco.app.AppSupport;

import sacred.alliance.magic.base.AttrFontColorType;
import sacred.alliance.magic.base.AttrFontSizeType;
import sacred.alliance.magic.core.exception.ServiceException;
import sacred.alliance.magic.vo.AbstractRole;
import sacred.alliance.magic.vo.AttrFontInfo;

public interface BattleApp extends AppSupport{

	void killedRole(AbstractRole attack, AbstractRole victim) throws ServiceException;
	
	public void attack(AbstractRole attack, AbstractRole victim, int hp,int mp,int hated);
	/**
	 * 创建战斗飘字
	 * @param sizeType
	 * @param colorType
	 * @param value
	 * @param ownerId
	 * @param attackerId
	 * @return
	 */
	public AttrFontInfo creatAttrFontInfo(AttrFontSizeType sizeType,
			AttrFontColorType colorType, int value, int ownerId, int attackerId);
}
