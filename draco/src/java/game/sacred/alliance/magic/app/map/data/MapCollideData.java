package sacred.alliance.magic.app.map.data;

public class MapCollideData {
	private int collideWidth;
	private int collideHeight;
	byte[][] collideData;

	public byte[][] getCollideData() {
		return collideData;
	}

	public void setCollideData(byte[][] collideData) {
		this.collideData = collideData;
	}
	
	public MapCollideData(byte[][] collideData,int collideWidth,int collideHeight){
		this.collideData = collideData;
		this.collideWidth = collideWidth;
		this.collideHeight = collideHeight;
	}

	public int getCollideWidth() {
		return collideWidth;
	}

	public void setCollideWidth(int collideWidth) {
		this.collideWidth = collideWidth;
	}

	public int getCollideHeight() {
		return collideHeight;
	}

	public void setCollideHeight(int collideHeight) {
		this.collideHeight = collideHeight;
	}

	
}
