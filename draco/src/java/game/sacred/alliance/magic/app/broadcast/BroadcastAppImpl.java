package sacred.alliance.magic.app.broadcast;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.game.draco.GameContext;
import com.game.draco.app.chat.ChannelType;
import com.game.draco.app.chat.ChatSysName;
import com.game.draco.app.npc.domain.NpcTemplate;

import sacred.alliance.magic.app.broadcast.config.BroadcastGoods;
import sacred.alliance.magic.base.BroadcastType;
import sacred.alliance.magic.base.XlsSheetNameType;
import sacred.alliance.magic.util.Log4jManager;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.util.Wildcard;
import sacred.alliance.magic.util.XlsPojoUtil;


public class BroadcastAppImpl implements BroadcastApp{

	private final Logger logger = LoggerFactory.getLogger(this.getClass()); 
	private Map<String, BroadcastGoods> lootGoodsIdMap = new HashMap<String, BroadcastGoods>();
	private Map<String, BroadcastGoods> boxGoodsIdMap = new HashMap<String, BroadcastGoods>();
	@Override
	public void start() {
		initConfig();
	}
	
	private void initConfig(){
		//加载物品掉落喊话
		String fileName = XlsSheetNameType.broadcast_goods.getXlsName();
		String sheetName = XlsSheetNameType.broadcast_goods.getSheetName();
		initBroadcastGoods(fileName, sheetName, lootGoodsIdMap);
		
		//加载宝箱打开获得物品喊话
		fileName = XlsSheetNameType.broadcast_box.getXlsName();
		sheetName = XlsSheetNameType.broadcast_box.getSheetName();
		initBroadcastGoods(fileName, sheetName, boxGoodsIdMap);
	}
	
	/**
	 * 加载根据物品掉落的物品Id广播
	 */
	private void initBroadcastGoods(String fileName, String sheetName, Map<String, BroadcastGoods> map){
		try{
			String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
			List<BroadcastGoods> list = XlsPojoUtil.sheetToList(sourceFile, sheetName, BroadcastGoods.class);
			if(Util.isEmpty(list)) {
				return;
			}
			for(BroadcastGoods bg : list) {
				if(null == bg) {
					continue;
				}
				int goodsId = bg.getGoodsId();
				String targetId = bg.getTargetId();
				String key = getKey(goodsId, targetId);
				map.put(key, bg);
			}
		}catch(Exception ex){
			Log4jManager.checkFail();
			Log4jManager.CHECK.error("loadExel error: sourceFile = " + fileName + "sheetName = " + sheetName, ex);
		}
	}
	
	private String getKey(int goodsId, String targetId){
		return goodsId + targetId;
	}
	
	@Override
	public void broadCast(String roleName, int goodsId, String targetId, BroadcastType type) {
		try{
			switch(type) {
			case loot:
				broadcastLoot(roleName, goodsId, targetId);
				return;
			case box:
				broadcastBox(roleName, goodsId, targetId);
				return;
			}
		}catch(Exception e){
			logger.error("BroadcastApp.broadCast error:",e);
		}
	}
	
	private void broadcastLoot(String roleName, int goodsId, String targetId){
		String key = getKey(goodsId, targetId);
		BroadcastGoods bg = this.lootGoodsIdMap.get(key);
		if(null == bg) {
			return;
		}
		NpcTemplate npc = GameContext.getNpcApp().getNpcTemplate(targetId);
		if(null == npc) {
			return;
		}
		String goodsContent = Wildcard.getChatGoodsContent(goodsId, ChannelType.Publicize_Personal);
		String message = MessageFormat.format(bg.getContent(), roleName, npc.getNpcname(), goodsContent);							
		broadcast(message);
	}
	
	private void broadcastBox(String roleName, int goodsId, String targetId){
		String key = getKey(goodsId, targetId);
		BroadcastGoods bg = this.boxGoodsIdMap.get(key);
		if(null == bg) {
			return;
		}
		String boxContent = Wildcard.getChatGoodsContent(Integer.parseInt(targetId), ChannelType.Publicize_Personal);
		String goodsContent = Wildcard.getChatGoodsContent(goodsId, ChannelType.Publicize_Personal);
		String message = MessageFormat.format(bg.getContent(), roleName, boxContent, goodsContent);							
		broadcast(message);
	}
	
	private void broadcast(String message){
		GameContext.getChatApp().sendSysMessage(ChatSysName.System, ChannelType.Publicize_System, message, null, null);
	}

	@Override
	public void stop() {
		
	}
	
	@Override
	public void setArgs(Object arg0) {
		
	}
}
