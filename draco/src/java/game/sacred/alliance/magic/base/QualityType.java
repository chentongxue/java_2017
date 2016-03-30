package sacred.alliance.magic.base;

public enum QualityType {
	
	gray(0,"FF4c4c4c", "灰色"),//灰
	white(1,"FFffffff", "白色"), // 白
	green(2,"FF00ff24", "绿色"), // 绿
	blue(3,"FF00aeff", "蓝色"), //蓝
	purple(4,"FFcc2fff", "紫色"), //紫
	orange(5,"FFffee2f", "橙色"),//橙
	red(6,"FFff1b1b", "红色"),//红
	golden(7,"FFffe3a7", "金色"),//土豪金
	
	;
	private final int type;
	private final String color;
	private final String name;

	QualityType(int type, String color, String name) {
		this.type = type;
		this.color = color;
		this.name = name;
	}

	public int getType() {
		return type;
	}
	
	public String getColor() {
		return color;
	}
	
	public String getName() {
		return name;
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
