package sacred.alliance.magic.vo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.game.draco.GameContext;
import com.game.draco.message.item.ActiveDpsRankItem;
import com.game.draco.message.push.C2360_ActiveDpsRankNotifyMessage;
import com.game.draco.message.push.C2361_ActiveDpsSelfNotifyMessage;

import sacred.alliance.magic.util.Util;

public class MapDpsModel {
	
	protected final Logger logger = LoggerFactory.getLogger(this.getClass());
	private static final int DEFAULT_SHOW_SIZE = 10;//默认显示数量为10
	/** 显示排名的人数 */
	private int showSize = DEFAULT_SHOW_SIZE;
	/** KEY=角色ID,VALUE=输出值 */
	private Map<String,AtomicLong> dpsValueMap = new ConcurrentHashMap<String,AtomicLong>();
	/** 曾经进入过DPS模式地图的角色：KEY=角色ID,VALUE=角色名称 */
	private Map<String,String> roleNameMap = new ConcurrentHashMap<String, String>();
	
	public boolean isExistRoleName(String roleId){
		return roleNameMap.containsKey(roleId);
	}
	
	/**
	 * 累计输出值
	 * @param role
	 * @param value
	 */
	public void countDpsValue(AbstractRole role, int value){
		String roleId = role.getRoleId();
		if(!this.dpsValueMap.containsKey(roleId)){
			this.dpsValueMap.put(roleId, new AtomicLong());
		}
		AtomicLong totalHurt = this.dpsValueMap.get(roleId);
		totalHurt.addAndGet(value);
		this.roleNameMap.put(role.getRoleId(), role.getRoleName());
	}
	
	/**
	 * 广播伤害排名消息
	 * @param hurtList
	 */
	public void notifyRankMessage(MapInstance mapInstance){
		//输出血量排序
		List<Entry<String, AtomicLong>> hurtList = this.getDpsRankList();
		Collection<RoleInstance> roleList = mapInstance.getRoleList();
		if(null == hurtList || Util.isEmpty(roleList)){
			return;
		}
		int size = hurtList.size();
		List<ActiveDpsRankItem> dpsRankList = new ArrayList<ActiveDpsRankItem>();
		for(int i=0; i<size; i++){
			try {
				Entry<String, AtomicLong> entry = hurtList.get(i);
				if(null == entry){
					continue;
				}
				String roleId = entry.getKey();
				RoleInstance role = GameContext.getOnlineCenter().getRoleInstanceByRoleId(roleId);
				
				short index = (short) (i+1);
				int dpsValue = (int) entry.getValue().get();
				ActiveDpsRankItem item = new ActiveDpsRankItem();
				item.setIndex(index);
				item.setRoleName(this.roleNameMap.get(roleId));
				item.setCamp((byte) -1);
				item.setDpsValue(dpsValue);
				item.setRoleId(Integer.parseInt(roleId));
				if(null != role){
					item.setRoleName(role.getRoleName());
					item.setCamp(role.getCampId());
					//给每个人发自己的排名信息（在DPS地图的才发）
//					if(null != mapInstance.getRoleInstance(roleId)){
//						C2361_ActiveDpsSelfNotifyMessage selfMsg = new C2361_ActiveDpsSelfNotifyMessage();
//						selfMsg.setDpsRankItem(item);
//						role.getBehavior().sendMessage(selfMsg);
//					}
				}
				//构建显示排名的信息
				if(i<this.showSize){
					dpsRankList.add(item);
				}
			} catch (RuntimeException e) {
				this.logger.error(this.getClass().getName() + ".notifyRankMessage error: ", e);
			}
		}
		C2360_ActiveDpsRankNotifyMessage message = new C2360_ActiveDpsRankNotifyMessage();
		message.setDpsRankList(dpsRankList);
		//地图内广播
		mapInstance.broadcastMap(null, message);
	}
	
	/**
	 * 获取输出值排行
	 * @return
	 */
	public List<Entry<String, AtomicLong>> getDpsRankList(){
		return Util.getSortedMapEntryListByLongValue(this.dpsValueMap);
	}
	
	/**
	 * 清空输出值
	 */
	public void clearDpsValue(){
		this.dpsValueMap.clear();
		this.roleNameMap.clear();
	}
	
	/**
	 * 增加角色名称
	 * @param roleId
	 * @param roleName
	 */
	public void putRoleName(String roleId, String roleName){
		this.roleNameMap.put(roleId, roleName);
	}
	
	/**
	 * 获取角色名称
	 * @param roleId
	 * @return
	 */
	public String getRoleName(String roleId){
		return this.roleNameMap.get(roleId);
	}

	public int getShowSize() {
		return showSize;
	}

	public void setShowSize(int showSize) {
		this.showSize = showSize;
	}
	
}
