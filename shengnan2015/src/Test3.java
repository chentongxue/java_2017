import com.baidu.bjf.remoting.protobuf.utils.JDKCompilerHelper;
import com.baidu.bjf.remoting.protobuf.utils.compiler.JdkCompiler;


public class Test3 {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
			System.out.println(Math.floor(2.56));
			int a = 8;
			int b= 1000;
			double c = (double)a/b;
			System.out.println(""+c);
			
			int bb = 9;
			int dd = 10;
			double aa =  (double)bb/dd;
			int ccc = 100;
			int ddd = (int) (ccc * aa);
			System.out.println(""+ddd);
			
			System.out.println(""+Math.ceil(2.01));
			
	}

}
