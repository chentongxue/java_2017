package sacred.alliance.magic.base;

public enum QualityType {
	
	gray(0,"FF4c4c4c"),//灰
	white(1,"FFffffff"), // 白
	green(2,"FF00ff24"), // 绿
	blue(3,"FF00aeff"), //蓝
	purple(4,"FFcc2fff"), //紫
	orange(5,"FFffee2f"),//橙
	red(6,"FFff1b1b"),//红
	golden(7,"FFffe3a7"),//土豪金
	
	;
	private final int type;
	private final String color;

	QualityType(int type, String color) {
		this.type = type;
		this.color = color;
	}

	public int getType() {
		return type;
	}
	
	public String getColor() {
		return color;
	}

	public static QualityType get(int type) {
		for(QualityType qt : QualityType.values()){
			if(qt.getType() == type){
				return qt;
			}
		}
		return null;
	}
	
}
