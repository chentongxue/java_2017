package sacred.alliance.magic.base;

public enum MapLineType {
	idle(1,0,35),//Á÷³©
	busy(2,35,85), //·±Ã¦
	full(3,85,Integer.MAX_VALUE); //±¬Âú
	
	private int type;
	private int minPro;
	private int maxPro;
	
	MapLineType(int type, int minPro, int maxPro){
		this.type = type;
		this.minPro = minPro;
		this.maxPro = maxPro;
	}

	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	
	public int getMinPro() {
		return minPro;
	}

	public void setMinPro(int minPro) {
		this.minPro = minPro;
	}

	public int getMaxPro() {
		return maxPro;
	}

	public void setMaxPro(int maxPro) {
		this.maxPro = maxPro;
	}

	public static int getMapLineStatus(int roleCount, int limitRoleCount){
		if(limitRoleCount <= 0 || roleCount <= 0){
			return idle.getType();
		}
		if((roleCount / limitRoleCount) > 0){
			return full.getType();
		}
		
		int pro = roleCount % limitRoleCount;
		for(MapLineType lineType : MapLineType.values()){
			if(lineType.getMinPro() < pro &&  pro < lineType.getMaxPro()){
				return lineType.getType();
			}
		}
		return full.getType();
	}
}
