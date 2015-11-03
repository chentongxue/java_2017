package a_effective.enum_;

import java.util.EnumSet;
import java.util.Set;

//138
/**
 * ��ö�ٴ���λ�������𣬸����̣��������Ҳ����ȫ
 * ��֮����ʽ��Ϊö������Ҫ���ڼ��ϣ�Set���У�����û��������λ������ʾ��
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
