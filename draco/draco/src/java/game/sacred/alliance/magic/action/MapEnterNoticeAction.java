package sacred.alliance.magic.action;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.game.draco.GameContext;
import com.game.draco.app.buff.stat.MapBuffStat;
import com.game.draco.app.npc.domain.NpcInstance;
import com.game.draco.message.item.AbstractBodyItem;
import com.game.draco.message.item.MapBaffleItem;
import com.game.draco.message.item.MapBuffItem;
import com.game.draco.message.item.MapJumpPointItem;
import com.game.draco.message.item.RoleBodyItem;
import com.game.draco.message.request.C0203_MapEnterNoticeReqMessage;
import com.game.draco.message.response.C0203_MapEnterNoticeRespMessage;

import sacred.alliance.magic.app.map.Map;
import sacred.alliance.magic.app.map.data.MapConfig;
import sacred.alliance.magic.app.map.point.JumpMapPoint;
import sacred.alliance.magic.base.MapLogicType;
import sacred.alliance.magic.constant.RespTypeStatus;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.util.Converter;
import sacred.alliance.magic.vo.MapInstance;
import sacred.alliance.magic.vo.RebornPointDetail;
import sacred.alliance.magic.vo.RoleInstance;

public class MapEnterNoticeAction extends BaseAction<C0203_MapEnterNoticeReqMessage> {
	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Override
	public Message execute(ActionContext context, C0203_MapEnterNoticeReqMessage reqMsg) {
		RoleInstance roleIn = this.getCurrentRole(context);
		C0203_MapEnterNoticeRespMessage resp = new C0203_MapEnterNoticeRespMessage();
		resp.setType(RespTypeStatus.FAILURE);
		String backMapId = null ;
		MapInstance mapIn = null ;
		String toMapId = null ;
		try {
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
			Collection<MapBuffStat> buffList = mapIn.getBuffList();
			for (MapBuffStat buf : buffList) {
				try{
				buffItems.add(Converter.getMapBuffItem(buf));
				}catch(Exception ex){
					logger.error("",ex);
				}
			}
			resp.setBuffItemes(buffItems);
			resp.setBodyItemes(bodyItemes);
			resp.setMapDisplayName(mapConfig.getMapdisplayname());
			resp.setLineId((byte)roleIn.getLineId());
			//关卡ID
			resp.setGateId(mapConfig.getMaplevelname());
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
			//进入成功将当前地图id返回给客户端,客户端对本地资源比较
			//如果本地资源和服务器返回不一致,客户端会再请求地图资源
			resp.setInfo(mapIn.getMap().getMapId());
			resp.setType((byte) RespTypeStatus.SUCCESS);
			return resp;
		} catch (Exception e) {
			logger.error("", e);
			if(null != mapIn && null != roleIn){
				//失败情况需要将用户从地图删除
				try {
					roleIn.getBehavior().exitMap();
				} catch (Exception e1) {
					//e1.printStackTrace();
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
		} finally {
			//跳地图标识
			roleIn.getJumpMap().compareAndSet(true, false);
		}
	}

}
