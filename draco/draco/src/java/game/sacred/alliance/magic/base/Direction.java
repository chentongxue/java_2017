package sacred.alliance.magic.base;

public enum Direction {

	UP(-64),// 0
	DOWN(64),
	LEFT(-128),
	RIGHT(0);
	
	int type;
	
	Direction(int type){
		this.type = type;
	}
	
	public int getType() {
		return type;
	}
	
	public static Direction getDir(byte dir){
		
		if(dir>=-32&&dir<32)return RIGHT;
		else if(dir>=32&&dir<96)return DOWN;
		else if(dir>=96&&dir<128)return LEFT;
		else if(dir>=-128&&dir<=-96)return LEFT;
		else if(dir>=-96&&dir<-32)return UP;
		return DOWN;
	}
	
	
    public static Direction getDir(int x, int y, int nowX, int nowY) {
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
