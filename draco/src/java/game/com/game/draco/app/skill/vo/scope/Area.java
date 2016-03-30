package com.game.draco.app.skill.vo.scope;

import sacred.alliance.magic.vo.AbstractRole;
import sacred.alliance.magic.vo.Point;

/**
 * 作用区域
 * @author tiefengKuang 
 * @date 2009-12-10 
 * @version 0.0.0.1
 * @changeLog
 *  1) create: 2009-12-10
 */
public abstract class Area {

	/**
	 * 坐标(x,y)是否在此区域内
	 * @param x
	 * @param y
	 * @return
	 */
	public abstract boolean inArea(AbstractRole attacker, int x,int y,byte dir);
	
	protected abstract String getKey();

	protected abstract Point getCenterPoint() ;
    
	protected short getDir(byte dir){
    	return (short)(((dir&0xff)*360)>>8);
    }

}
