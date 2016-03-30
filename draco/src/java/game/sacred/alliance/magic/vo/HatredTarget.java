package sacred.alliance.magic.vo;

import java.util.List;

import sacred.alliance.magic.base.RoleType;
import sacred.alliance.magic.constant.AIMoveConstant;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.util.SortedValueMap;
import sacred.alliance.magic.util.SortedValueNumMap;

import com.game.draco.GameContext;
import com.game.draco.app.npc.domain.NpcInstance;
import com.google.common.collect.Lists;

public class HatredTarget {

	private SortedValueNumMap hatredMap = new SortedValueNumMap();
	
	AbstractRole role;
	
	public HatredTarget(AbstractRole role){
		this.role = role;
	}
	
	public SortedValueNumMap getHatredMap() {
		return hatredMap;
	}
	
	public List<String> getHatredList(){
		List<String> list = Lists.newArrayList();
		for (Object haredRoleId : getHatredMap().keySet()) {
			String roleId = haredRoleId.toString();
			list.add(roleId);
		}
		return list;
	}
	
	public void broadcast(Message message) {
		if(null == message){
			return ;
		}
		try{
			MapInstance mapInstance = role.getMapInstance() ;
			if(null == mapInstance){
				return ;
			}
			for (Object haredRoleId : getHatredMap().keySet()) {
				String roleId = haredRoleId.toString();
				RoleInstance role = mapInstance.getRoleInstance(roleId);
				if(null == role){
					continue ;
				}
				GameContext.getMessageCenter().send("",role.getUserId(), message);
			}
		}catch(Exception ex){
			//ex.printStackTrace();
		}
		
	}
	
	public void copyHatredMap(HatredTarget dest){
		for(Object haredRoleId : getHatredMap().keySet()){
    		String strHR = haredRoleId.toString();
    		dest.getHatredMap().put(strHR, getHatredMap().get(strHR));
    	}
	}
	
    /**判断是否在仇恨列表中*/
    public boolean inHatredMap(String targetId){
    	SortedValueMap hm = getHatredMap();
    	if(null == hm){
    		return false ;
    	}
    	return hm.containsKey(targetId);
    }
    //仇恨列表是否为空
    public boolean isEmptyHatredMap(){
    	SortedValueMap hm = getHatredMap();
    	if(null == hm){
    		return true ;
    	}
    	return hm.isEmpty();
    }
    
    //删除某仇恨用户
    public void removeHateTarget(String targetId){
    	removeHateTarget(targetId, true);
    }
    
    private void removeHateTarget(String targetId, boolean needRemoveFromTarget){
    	getHatredMap().remove(targetId);
    	if(role.getRoleType() == RoleType.NPC){
    		NpcInstance npc = (NpcInstance)role;
    		if(npc.getOwnerInstance() != null && npc.getOwnerInstance().getRoleId().equals(targetId)) {
    			Object o = getHatredMap().getFirstKey();
    			if(o != null) {
    				npc.setOwnerInstance(role.getMapInstance().getAbstractRole(o.toString()));
    			}
    		}
    	}
    	
    	if(needRemoveFromTarget){
    		AbstractRole target = role.getMapInstance().getAbstractRole(targetId);
    		if(target==null){
    			return;
    		}
    		target.getHatredTarget().removeHateTarget(role.getRoleId(), false);
    	}
    }
    
    public void clearHatredMap(MapInstance mapInstance){
    	clearHatredMap(mapInstance,true);
    }
    
    private void clearHatredMap(MapInstance mapInstance,boolean needClearFromTarget){
    	try {
			SortedValueMap hm = getHatredMap();
			if (null == hm) {
				hm = new SortedValueNumMap();
				return;
			}
			if (hm.size() <= 0) {
				return;
			}
			if (role.getRoleType() == RoleType.NPC) {
				((NpcInstance) role).setOwnerInstance(null);
			}
			if (!needClearFromTarget || null == mapInstance) {
				hm.clear();
				return;
			}
			for (Object key : hm.keySet()) {
				String hateTarget = (String) key;
				if (hateTarget == null || hateTarget.length() <= 0) {
					continue;
				}
				AbstractRole target = mapInstance.getAbstractRole(hateTarget);
				if (target == null) {
					continue;
				}
				target.getHatredTarget().removeHateTarget(role.getRoleId(),
						false);
			}
			hm.clear();
		}catch(Exception ex){
    		
    	}
    }
    
    //清空全部仇恨
    public void clearHatredMap(){
    	clearHatredMap(true);
    }
    
    public void clearHatredMap(boolean needClearFromTarget){
    	this.clearHatredMap(role.getMapInstance(), needClearFromTarget);
    }
    

    //获得第一仇恨目标
    public String getFirstHateTarget(){
    	if(isEmptyHatredMap()){
    		return null ;
    	}
    	return getHatredMap().getFirstKey().toString();
    }
    //增加仇恨目标
    public void addHatred(AbstractRole targetRole,int hateValue){
    	if(null == targetRole 
    			|| targetRole.isDeath()
    			|| targetRole.getRoleId().equals(role.getRoleId())){
    		return ;
    	}
    	addHatred(targetRole, hateValue, true);
    }	
    private void addHatred(AbstractRole targetRole,int hateValue, boolean needAddFromTarget){
    	if(needAddFromTarget){
    		targetRole.getHatredTarget().addHatred(role, AIMoveConstant.MOVE_IN_LINE_SIGHT_HATRED, false);
    	}
    	if (role.getRoleType() == RoleType.NPC) {
			NpcInstance npc = (NpcInstance) role;
			if (null == npc.getOwnerInstance()) {
				npc.setOwnerInstance(targetRole);
				npc.setAttackedTime(System.currentTimeMillis());
			}
		}
    	String targetRoleId = targetRole.getRoleId();
    	Object value = getHatredMap().get(targetRoleId);
    	if(null == value){
    		getHatredMap().put(targetRoleId,hateValue);
    	}else{
    		//注意为了通用,这里用了Double,用Int会有隐患问题
        	getHatredMap().put(targetRoleId,hateValue + Double.parseDouble(value.toString()));
    	}
    }
	
}
