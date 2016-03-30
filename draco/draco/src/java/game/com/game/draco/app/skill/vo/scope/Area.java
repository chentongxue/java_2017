package com.game.draco.app.skill.vo.scope;

import sacred.alliance.magic.base.Direction;
import sacred.alliance.magic.vo.Point;

/**
 * 作用区域
 * @author tiefengKuang 
 * @date 2009-12-10 
 * @version 0.0.0.1
 * @changeLog
 *  1) create: 2009-12-10
 */
public interface Area {

	/**
	 * 坐标(x,y)是否在此区域内
	 * @param x
	 * @param y
	 * @return
	 */
	public boolean inArea(int x,int y, Direction dir);
	
	public String getKey();

    public Point getCenterPoint() ;

    public Direction getDir();
}
