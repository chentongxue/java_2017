package com.game.draco.app.skill.vo.scope;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import sacred.alliance.magic.util.RandomUtil;
import sacred.alliance.magic.vo.AbstractRole;

import com.game.draco.app.skill.vo.scope.target.EffectTargetLogic;

/***
 * 目标域
 * @author tiefengKuang 
 * @date 2009-12-10 
 * @version 0.0.0.1
 * @changeLog
 *  1) create: 2009-12-10
 */
public class TargetScope {
	public final static int SELECT_ALL = 0 ;
	public final static int SELECT_RANDOM_ONE = 1 ; //随机一个

	
	public TargetScope(EffectTarget target){
		this(null,target,SELECT_ALL);
	}
	
	public TargetScope(Area area,EffectTarget target){
		this(area,target,SELECT_ALL);
	}
	
	public TargetScope(EffectTarget target,int selectType){
		this(null,target,selectType);
	}
	
	public TargetScope(Area area,EffectTarget target,int selectType){
		this.area = area ;
		this.target = target ;
		this.selectType = selectType ;
	}
	
	public TargetScope(Area area,EffectTarget target,int selectType, byte targetNum){
		this(area, target, selectType);
		this.targetNum = targetNum;
	}
	
	public TargetScope(Area area,EffectTarget target,int selectType, byte targetNum,boolean pentacombo ){
		this(area, target, selectType);
		this.targetNum = targetNum;
		this.pentacombo = pentacombo;
	}
	
	/**如果area==null,则表示非区域目标*/
	private Area area ;
	private EffectTarget target ;
	private int selectType ;
	private byte targetNum = 0; //攻击目标上线
	private boolean pentacombo = false;
	
	public Area getArea() {
		return area;
	}
	public void setArea(Area area) {
		this.area = area;
	}
	public EffectTarget getTarget() {
		return target;
	}
	public void setTarget(EffectTarget target) {
		this.target = target;
	}
	
	
	public int getSelectType() {
		return selectType;
	}
	public void setSelectType(int selectType) {
		this.selectType = selectType;
	}
	
	public Collection<AbstractRole> getTargetRole(AbstractRole attacker){
		EffectTargetLogic logic = EffectTarget.getEffectTargetLogic(target);
		if(null == logic) {
			return null;
		}
		List<AbstractRole> list =  logic.getTargetRole(attacker, area, targetNum);
		if(SELECT_ALL == this.selectType){
			if(!pentacombo){
				return list;
			}
			if(list.size() < targetNum){
				List<AbstractRole> newList = new ArrayList<AbstractRole>();
				newList.addAll(list);
				for(int i=0;i<targetNum;i++){
					if(list.size() >= targetNum){
						break;
					}
					if(newList.size() > i){
						list.add(newList.get(i));
					}else{
						list.add(newList.get(0));
					}
				}
			}
			return list ;
		}
		if(SELECT_RANDOM_ONE == this.selectType){
			//从列表中随机取一个，如果列表为空则去目标
			int size = list.size();
			if(0 == size){
				AbstractRole targetRole = attacker.getTarget();
				if(null != targetRole){
					list.add(targetRole);
					return list ;
				}
			}
			Collection<AbstractRole> returnList = new ArrayList<AbstractRole>();
			int random = RandomUtil.absRandomInt(size);
			AbstractRole target = list.get(random);
			attacker.setTarget(target);
			returnList.add(target);
			list.clear();
			list = null ;
			return returnList ;
		}
		return list ;
	}
}
