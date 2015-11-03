import com.baidu.bjf.remoting.protobuf.utils.JDKCompilerHelper;
import com.baidu.bjf.remoting.protobuf.utils.compiler.JdkCompiler;


public class Test4 {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
			System.out.println(Math.floor(2.56));
			JdkCompiler COMPILER = new JdkCompiler(JDKCompilerHelper.class.getClassLoader());
			System.out.println(COMPILER);
	}

}
