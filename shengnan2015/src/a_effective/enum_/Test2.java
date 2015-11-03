package a_effective.enum_;

import java.util.EnumSet;
import java.util.Set;

//138
/**
 * 用枚举代替位域后的嗲吗，更剪短，更清楚，也更安全
 * 总之，正式因为枚举类型要用在集合（Set）中，所以没有理由用位域来表示它
 */
//EnumSet - a modern replacement for bit field
public class Test2 {
	public enum Style {	BOLD, ITALIC, UNDERLINE, STRIKETHROUTH }
	
	// Any Set could be passed in, but EnumSet is clearly best
	public void applyStyles(Set<Style> styles){
	
	}
	public static void main(String args[]){
		Test2 t = new Test2();
		t.applyStyles(EnumSet.of(Style.BOLD, Style.ITALIC));
	}
}
