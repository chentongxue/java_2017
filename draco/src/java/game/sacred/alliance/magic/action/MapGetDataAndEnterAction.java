package sacred.alliance.magic.action;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.game.draco.app.union.battle.UnionBattleApp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sacred.alliance.magic.app.map.Map;
import sacred.alliance.magic.app.map.MapDataInfo;
import sacred.alliance.magic.app.map.data.MapConfig;
import sacred.alliance.magic.app.map.point.JumpMapPoint;
import sacred.alliance.magic.constant.RespTypeStatus;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.util.Converter;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.vo.MapInstance;
import sacred.alliance.magic.vo.RebornPointDetail;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.buff.stat.BuffStat;
import com.game.draco.app.npc.domain.NpcInstance;
import com.game.draco.message.item.AbstractBodyItem;
import com.game.draco.message.item.MapBaffleItem;
import com.game.draco.message.item.MapBuffItem;
import com.game.draco.message.item.MapJumpPointItem;
import com.game.draco.message.item.RoleBodyItem;
import com.game.draco.message.request.C0240_MapGetDataAndEnterReqMessage;
import com.game.draco.message.response.C0240_MapGetDataAndEnterRespMessage;

public class MapGetDataAndEnterAction extends BaseAction<C0240_MapGetDataAndEnterReqMessage> {
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Override
	public Message execute(ActionContext context, C0240_MapGetDataAndEnterReqMessage reqMsg) {
		RoleInstance roleIn = this.getCurrentRole(context);
		if(null == roleIn){
			return null;
		}
		
		C0240_MapGetDataAndEnterRespMessage resp = new C0240_MapGetDataAndEnterRespMessage();
		resp.setType(RespTypeStatus.FAILURE);
		String backMapId = null;
		MapInstance mapIn = null;
		String toMapId = null ;
		try{
			toMapId = roleIn.getMapId();
			// 通知有角色进入地图---->通知其他玩家本人进入
			roleIn.getBehavior().enterMap();
			mapIn = roleIn.getMapInstance();
			//已经设置为相关 Reloginjumpmapid
			backMapId = roleIn.getMapId();
			if(null == mapIn){
				//进入失败
				resp.setBackMapId(backMapId);
				return resp;
			}
			resp.setCompress((byte)1);
			MapConfig mapConfig = mapIn.getMap().getMapConfig();
			//backMapId = mapConfig.getReloginjumpmapid();
			// 进入地图获得其它玩家的信息
			List<AbstractBodyItem> bodyItemes = new ArrayList<AbstractBodyItem>();
			List<MapBuffItem> buffItems = new ArrayList<MapBuffItem>();
			Collection<RoleInstance> roleList = mapIn.getRoleList();
			
			for (RoleInstance current : roleList) {
				try {
					// 不包括自己
					if (current.getRoleId().equals(roleIn.getRoleId())) {
						continue;
					}
					if (!GameContext.getOnlineCenter().isOnlineByUserId(current.getUserId())) {
						continue;
					}
					//死亡玩家不再列出(玩家死亡时并没有从地图移除)
					if(!mapIn.isNormalLive(current)){
						continue ;
					}
					RoleBodyItem bodyItem = Converter.getRoleBodyItem(current,
							roleIn);
					bodyItemes.add(bodyItem);
				} catch (Exception ex) {
					logger.error("", ex);
				}
			}
			//进入地图获得NPC的信息
			Collection<NpcInstance> npcList = mapIn.getNpcList();
			for (NpcInstance ni : npcList) {
				try{
					bodyItemes.add(Converter.getNpcBodyItem(ni,roleIn));
				}catch(Exception ex){
					logger.error("",ex);
				}
			}
			
			//物理层
			Collection<NpcInstance> baffleList = mapIn.getBaffleList();
			for (NpcInstance ni : baffleList) {
				MapBaffleItem baffleItem = new MapBaffleItem();
				baffleItem.setRoleId(ni.getIntRoleId());
				baffleItem.setMapx((short)ni.getMapX());
				baffleItem.setMapy((short)ni.getMapY());
				baffleItem.setResId(ni.getResid());
				bodyItemes.add(baffleItem);
			}
			Collection<BuffStat> buffList = mapIn.getBuffList();
			for (BuffStat buf : buffList) {
				try{
					MapBuffItem item = Converter.getMapBuffItem(buf) ;
					if(null != item){
						buffItems.add(item);
					}
				}catch(Exception ex){
					logger.error("",ex);
				}
			}
			resp.setBuffItemes(buffItems);
			resp.setBodyItemes(bodyItemes);
			resp.setMapDisplayName(mapConfig.getMapdisplayname());
            //其他模块可能会修改此名字
            UnionBattleApp app = GameContext.getUnionBattleApp() ;
            if(null != app){
                String newMapName = app.getCapitalName();
                if(!Util.isEmpty(newMapName)){
                    resp.setMapDisplayName(newMapName);
                }
            }

			resp.setLineId((byte)roleIn.getLineId());
			if (0 == roleIn.getMapX() && 0 == roleIn.getMapY()) {
				resp.setMapX((short)mapConfig.getMaporiginx());
				resp.setMapY((short)mapConfig.getMaporiginy());
			} else {
				resp.setMapX((short)roleIn.getMapX());
				resp.setMapY((short)roleIn.getMapY());
			}
			//地图逻辑类型
			resp.setMapType(mapConfig.getLogictype());
			//刷出的跳转点列表
			List<MapJumpPointItem> jumpPointItems = new ArrayList<MapJumpPointItem>();
			for(JumpMapPoint jumpPoint : mapIn.getRefreshJumpPointList()){
				if(null == jumpPoint){
					continue;
				}
				MapJumpPointItem item = new MapJumpPointItem();
				item.setX((short) jumpPoint.getX());
				item.setY((short) jumpPoint.getY());
				item.setMapName(GameContext.getMapApp().getMap(jumpPoint.getTomapid()).getMapConfig().getMapdisplayname());
				jumpPointItems.add(item);
			}
			resp.setJumpPointItems(jumpPointItems);
			
			//对于的地图数据是否存在
			String mapInMapId = mapIn.getMap().getMapId(); 
			MapDataInfo dataInfo = GameContext.getMapApp().getMapData(mapInMapId); 
			if(null == dataInfo){
				this.logError(mapInMapId, "");
				return resp;
			}
			String clientMd5 = reqMsg.getMd5Str() ;
			if(Util.isEmpty(clientMd5)){
				clientMd5 = "" ;
			}
			byte[] mapData = null ;
			//地图数据不一致时更新数据
			if(!clientMd5.equals(dataInfo.getMd5())){
				mapData = dataInfo.getData() ;
			}
			//进入成功将当前地图id返回给客户端,客户端对本地资源比较
			//如果本地资源和服务器返回不一致,客户端会再请求地图资源
			resp.setInfo(mapInMapId);
			resp.setType((byte) RespTypeStatus.SUCCESS);
			resp.setMapData(mapData);
			int seq = roleIn.getMapChangeSeq().getAndIncrement();
			resp.setSeq(seq);
			//地图相关属性
			resp.setMapProperty(mapConfig.getMapProperty());
			//默认语音聊天频道
			resp.setVoiceChannelType(mapConfig.getVoiceChannelType());
			return resp;
			
		}catch(Exception e){
			logger.error("", e);
			if(null != mapIn && null != roleIn){
				//失败情况需要将用户从地图删除
				try {
					roleIn.getBehavior().exitMap();
				} catch (Exception e1) {
					logger.error("",e1);
				}
			}
			try {
				if (null == backMapId && null != roleIn) {
					Map toMap = GameContext.getMapApp().getMap(
							toMapId);
					// 获得死亡复活点
					RebornPointDetail detail = GameContext
							.getRoleRebornApp().getRebornPointDetail(
									toMap.getMapId(),
									roleIn);
					if (toMapId.equals(detail.getRebornMapId())) {
						// 此情况下如果设置backMapId地图有错误会产生死循环
						// 不设置backMapId,直接让客户端异常不能进入
						backMapId = null;
					} else {
						roleIn.setMapId(detail.getRebornMapId());
						backMapId = detail.getRebornMapId();
					}
				}
			}catch(Exception ex){
				logger.error("",ex);
			}
			resp.setBackMapId(backMapId);
			return resp;
		}finally {
			//跳地图标识
			roleIn.getJumpMap().compareAndSet(true, false);
		}
	}
	
	private void logError(String mapId,String versionId){
   	 logger.warn("MapGetDataAndEnterRespMessage was null,pls check the map data config,mapId=" 
   			 + mapId + " versionId = " + versionId + " mapId = " + mapId);
   }

}
