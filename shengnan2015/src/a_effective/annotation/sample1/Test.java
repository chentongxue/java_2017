package a_effective.annotation.sample1;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
//147
@Retention(RetentionPolicy.RUNTIME) //������ʱ����
@Target(ElementType.METHOD)			//ֻ�ڷ��������вźϷ�
public @interface Test {

}
