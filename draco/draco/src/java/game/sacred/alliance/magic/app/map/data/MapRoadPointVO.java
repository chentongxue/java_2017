package sacred.alliance.magic.app.map.data;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public class MapRoadPointVO {
	@XmlAttribute
	protected int id;
	@XmlAttribute
	protected int posX;
	/*@XmlElement(name = "res")
	DramaResVO res;*/
	@XmlAttribute
	protected int posY;
	@XmlElement(name = "connectpoint")
	protected List<Integer> connectList = new ArrayList<Integer>();
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getPosX() {
		return posX;
	}
	public void setPosX(int posX) {
		this.posX = posX;
	}
	public int getPosY() {
		return posY;
	}
	public void setPosY(int posY) {
		this.posY = posY;
	}
	public List<Integer> getConnectList() {
		return connectList;
	}
	public void setConnectList(List<Integer> connectList) {
		this.connectList = connectList;
	}
}
