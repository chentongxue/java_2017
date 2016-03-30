package sacred.alliance.magic.app.chest;

import java.util.List;

import com.game.draco.app.mail.type.MailSendRoleType;
import com.game.draco.message.item.AngelChestInfoItem;

import sacred.alliance.magic.base.OutputConsumeType;
import sacred.alliance.magic.vo.RoleInstance;

public interface ChestApp {

	/**
	 * 根据宝箱类型ID获得宝箱信息对象
	 * @param chestType
	 * @return
	 */
	ChestTypeInfo getChestTypeInfo(int chestType) ;
	/**
	 * 根据地图ID获得当前地图刷怪信息列表
	 * @param mapId
	 * @return
	 */
    List<ChestRefreshInfo> getMapChestRefreshList(String mapId) ;
    /**
     * 根据地图ID获得当前地图会出现的宝箱信息对象列表
     * @param mapId
     * @return
     */
    List<ChestTypeInfo> getMapChestTypeInfo(String mapId);
    
    public boolean canOpen(RoleInstance role, long refreshTime, long readyTime, AngelChestInfoItem chest);
    
    public boolean openChest(RoleInstance role, int chestType,
			OutputConsumeType consumeType,OutputConsumeType mailConsumeType,
			String mailContext,MailSendRoleType mailSendRoleType);
    
    ChestRefreshRange getChestRefreshRange(String rangeId);
}
