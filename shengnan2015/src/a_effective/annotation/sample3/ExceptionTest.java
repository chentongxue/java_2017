package a_effective.annotation.sample3;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
//151  ������Ϊ����
@Retention(RetentionPolicy.RUNTIME) //������ʱ����
@Target(ElementType.METHOD)			//ֻ�ڷ��������вźϷ�
public @interface ExceptionTest {
	Class<? extends Exception>[] value();
}
