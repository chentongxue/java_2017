package sacred.alliance.magic.app.map.data;

public class TilesetLayer {
	private int width;
	private int height;
	private byte[][] mapIndex;
	private byte[][] reverseType;
	private int mapType; //0ㄩ濩倛ㄛ 1ㄩ撻倛
	private String mapGroundRes;
	public TilesetLayer() {
		
	}
	
	public TilesetLayer(int width, int height, byte[][] mapIndex, byte[][] reverseType, int mapType,String mapGroundRes) {
		this.width = width;
		this.height = height;
		this.mapIndex = mapIndex;
		this.reverseType = reverseType;
		this.mapType = mapType;
		this.mapGroundRes = mapGroundRes;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public byte[][] getMapIndex() {
		return mapIndex;
	}

	public void setMapIndex(byte[][] mapIndex) {
		this.mapIndex = mapIndex;
	}

	public byte[][] getReverseType() {
		return reverseType;
	}

	public void setReverseType(byte[][] reverseType) {
		this.reverseType = reverseType;
	}

	public int getMapType() {
		return mapType;
	}

	public void setMapType(int mapType) {
		this.mapType = mapType;
	}

	public String getMapGroundRes() {
		return mapGroundRes;
	}

	public void setMapGroundRes(String mapGroundRes) {
		this.mapGroundRes = mapGroundRes;
	}
}