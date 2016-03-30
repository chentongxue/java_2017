package sacred.alliance.magic.base;

public enum Direction {

	UP((byte)-64),// 0
	DOWN((byte)64),
	LEFT((byte)-128),
	RIGHT((byte)0);
	
	byte type;
	
	Direction(byte type){
		this.type = type;
	}
	
	public byte getType() {
		return type;
	}
	
//	public static Direction getDir(byte dir){
//		
//		if(dir>=-32&&dir<32)return RIGHT;
//		else if(dir>=32&&dir<96)return DOWN;
//		else if(dir>=96&&dir<128)return LEFT;
//		else if(dir>=-128&&dir<=-96)return LEFT;
//		else if(dir>=-96&&dir<-32)return UP;
//		return DOWN;
//	}
	
	public static byte getDir(int x, int y, int nowX, int nowY){
		return _getDir(x,y,nowX,nowY).getType() ;
	}
	
    private static Direction _getDir(int x, int y, int nowX, int nowY) {
        if (x >= nowX) {
            // 觔
            if (Math.abs(y - nowY) <= Math.abs(x - nowX)) {
                return Direction.RIGHT;
            }
            if (y >= nowY) {
                // 芟
                return Direction.DOWN;
            } else {
                // 芟
                return Direction.UP;
            }
        } else {
            // 荔
            if (Math.abs(y - nowY) <= Math.abs(x - nowX)) {
                return Direction.LEFT;
            }
            if (y >= nowY) {
                // 吮
                return Direction.DOWN;
            } else {
                // 芟
                return Direction.UP;
            }
        }
    }
	
}
