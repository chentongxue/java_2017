package cn.bao.wifi.domain;
/**
 * 位置信息
 * @author bao
 * {"BuildingAlias":"Malloo-WANDA01",
 * "BuildingId":"Malloo-WANDA01",
 * "Floor":5,
 * "FloorAlias":"Malloo-WANDA01#2",
 * "x":12,
 * "y":45}
 */
public class LocationResult {
	private String BuildingAlias;
	private String BuildingId;
	private int Floor;
	private String FloorAlias;
	private boolean info;
	private String message;
	private double x;
	private double y;
	public String getBuildingAlias() {
		return BuildingAlias;
	}
	public void setBuildingAlias(String buildingAlias) {
		BuildingAlias = buildingAlias;
	}
	public String getBuildingId() {
		return BuildingId;
	}
	public void setBuildingId(String buildingId) {
		BuildingId = buildingId;
	}
	public int getFloor() {
		return Floor;
	}
	public void setFloor(int floor) {
		Floor = floor;
	}
	public String getFloorAlias() {
		return FloorAlias;
	}
	public void setFloorAlias(String floorAlias) {
		FloorAlias = floorAlias;
	}
	public double getX() {
		return x;
	}
	public void setX(double x) {
		this.x = x;
	}
	public double getY() {
		return y;
	}
	public void setY(double y) {
		this.y = y;
	}
	public boolean getInfo() {
		return info;
	}
	public void setInfo(boolean info) {
		this.info = info;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	@Override
	public String toString() {
		return "LocationResult [BuildingAlias=" + BuildingAlias
				+ ", BuildingId=" + BuildingId + ", Floor=" + Floor
				+ ", FloorAlias=" + FloorAlias + ", info=" + info
				+ ", message=" + message + ", x=" + x + ", y=" + y + "]";
	}
	public String markString() {
		return "\nLocationResult ["+"BuildingId=" + BuildingId + ", Floor=" + Floor
				+  ", info=" + info
				+ ", message=" + message + ", x=" + x + ", y=" + y + "]";
	}
}
