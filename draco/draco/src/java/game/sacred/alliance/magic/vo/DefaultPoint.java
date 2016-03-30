package sacred.alliance.magic.vo;

public class DefaultPoint {
//	private Direction dir;
	
	private int x;
	
	private int y;

	public DefaultPoint(int nx,int ny){
//		this.dir = ndir;
		this.x = nx;
		this.y = ny;
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

//	public Direction getDir() {
//		return dir;
//	}

//	public void setDir(Direction dir) {
//		this.dir = dir;
//	}
	
}
