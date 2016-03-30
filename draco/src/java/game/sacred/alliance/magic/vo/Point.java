//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.3 in JDK 1.6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2009.11.26 at 06:12:18 下午 CST 
//
package sacred.alliance.magic.vo;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import com.game.draco.GameContext;

import sacred.alliance.magic.base.MapLogicType;
import sacred.alliance.magic.constant.MapConstant;

/**
 * <p>Java class for Point complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Point">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;attribute name="mapid" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="x" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="y" type="{http://www.w3.org/2001/XMLSchema}int" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Point")
public class Point {

    @XmlAttribute
    protected String mapid;
    @XmlAttribute
    protected int x;
    @XmlAttribute
    protected int y;
    protected byte eventType ;

    public String getMapid() {
        return mapid;
    }

    public void setMapid(String mapid) {
        this.mapid = mapid;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public Point() {
    }

    public Point(String mapid, int x, int y,byte eventType) {
        this.mapid = mapid;
        this.x = x;
        this.y = y;
        this.eventType = eventType ;
    }
    
    public Point(String mapid, int x, int y) {
        this(mapid,x,y,(byte)0);
    }

    @Override
    public boolean equals(Object other) {
        if (null == other) {
            return false;
        }
        if (!(other instanceof Point)) {
            return false;
        }
        Point p = (Point) other;
        return this.mapid.equals(p.mapid) && this.x == p.x && this.y == p.y;
    }

    public boolean near(Point other) {
        if(null == other){
            return false ;
        }
        if(null == other.mapid || null == this.mapid){
            return false ;
        }
        if(!other.mapid.equals(this.mapid)){
            return false ;
        }
        return Math.abs(other.x - this.x) + Math.abs(other.y - this.y) < MapConstant.JUMP_POINT_EFFECT_RADIOS ;
    }
    
    public boolean inSameMap(Point other){
    	if(null == other){
            return false ;
        }
        if(null == other.mapid || null == this.mapid){
            return false ;
        }
    	return other.mapid.equals(this.mapid);
    }

    @Override
    public String toString() {
        return "mapId=" + this.mapid + " x=" + this.x + " y=" + this.y;
    }
    
    public static int getTwoPointDis(AbstractRole p1,AbstractRole p2){
    	if(null == p1 || null == p2){
    		return Integer.MAX_VALUE;
    	}
    	return math_DistPointPoint(p1.getMapX()-p2.getMapX(),p1.getMapY()-p2.getMapY());
    }
    
    public static int math_DistPointPoint(int dx, int dy) {
		if (dx < 0) {
			dx = -dx;
		}
		if (dy < 0) {
			dy = -dy;
		}

		int min, max;
		if (dx < dy) {
			min = dx;
			max = dy;
		} else {
			min = dy;
			max = dx;
		}
		return ((max << 8) - (max << 3) - (max << 1) + (min << 6) + (min << 5)
				+ (min << 2) + (min << 1)) >> 8;
	}
    
    /**
     * 是否是普通的地图
     * @return
     */
    public boolean isDefaultMap(){
    	sacred.alliance.magic.app.map.Map map = GameContext.getMapApp().getMap(this.mapid);
    	if(null == map){
    		return false;
    	}
    	MapLogicType mapLogicType = map.getMapConfig().getMapLogicType();
		if (null == mapLogicType 
				|| mapLogicType.isCopyType()
				//|| mapLogicType == MapLogicType.campWar
				|| mapLogicType == MapLogicType.activeMap
				|| mapLogicType == MapLogicType.angelChest
				|| mapLogicType == MapLogicType.unionTerritoryLogic
				|| mapLogicType == MapLogicType.arenaTop 
				|| mapLogicType == MapLogicType.siege
				|| mapLogicType == MapLogicType.roleBornGuide
				|| mapLogicType == MapLogicType.goblin) {
			return false;
		}
    	return true;
    }

	public byte getEventType() {
		return eventType;
	}

	public void setEventType(byte eventType) {
		this.eventType = eventType;
	}

	
   
}