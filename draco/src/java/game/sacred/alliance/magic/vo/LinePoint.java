package sacred.alliance.magic.vo;

public class LinePoint extends Point{
	private int lineId ;
	public LinePoint() {
		 super();
	}

	public LinePoint(String mapid, int x, int y) {
	     super(mapid,x,y);
	}
	
	public LinePoint(String mapid, int x, int y,int lineId) {
	     super(mapid,x,y);
	     this.lineId = lineId ;
	}

	public int getLineId() {
		return lineId;
	}

	public void setLineId(int lineId) {
		this.lineId = lineId;
	}
	
}
