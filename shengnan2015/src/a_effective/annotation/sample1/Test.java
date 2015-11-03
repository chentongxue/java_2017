package a_effective.annotation.sample1;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
//147
@Retention(RetentionPolicy.RUNTIME) //在运行时保留
@Target(ElementType.METHOD)			//只在方法声明中才合法
public @interface Test {

}
