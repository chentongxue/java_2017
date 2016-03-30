package baoutilcopy;

import java.awt.Color;

public class LogB {
	private static LogB LogB = null;
	private static LogBatJFrame jFrame = null;
	private static ColorRandom co = ColorRandom.GREEN;
	private static int flag = 0;
	private LogB(){
		jFrame = new LogBatJFrame("william");
	}
	public static LogB getInstance(){
		if(LogB==null){
			LogB = new LogB();
		}
		return LogB;
	}
	public  static <T> void i(T s){
//		LogB.getInstance();
//		co = co.NextColor();
//		jFrame.setDocs("\n"+getTraceInfo()+s, co.color, null, null, null);
	}
	public  static <T> void s(T s){
		LogB.getInstance();
		co = co.NextColor();
		jFrame.setDocs("\n"+getTraceInfo()+s, co.color, null, null, null);
	}
	public static void i(String str, Color col, Color b_col, Boolean bold,
			Integer fontSize){
		LogB.getInstance();
		jFrame.setDocs(str, col, b_col, bold, fontSize);
	}

	public static void i(String str, Color col) {
		LogB.getInstance();
		jFrame.setDocs(str, col, null, null, null);
	}
	public static String getTraceInfo(){  
        StringBuffer sb = new StringBuffer();   
          
        StackTraceElement[] stacks = new Throwable().getStackTrace();  
        String className = stacks[1].getClassName();
        String methodName = stacks[1].getMethodName();
        String lineNum = stacks[1].getLineNumber()+"";
        
        className = addSpace(className,4);
        methodName = addSpace(methodName,4);
        lineNum = addSpace(lineNum,4);
        
//        int n = 10;
//        while(n-->=0)
        sb.append("~").append(className).append(">").append(methodName).append(">").append(lineNum);  
          
        return sb.toString();  
    }  
	public static String addSpace(String s, int len){
		StringBuffer sb = new StringBuffer();
		sb.append(s);
		for (int i = 0; i < len-s.length(); i++) {
			sb.append(" ");
		}
		return new String(sb);
	}
	private enum ColorRandom{
		RED(Color.RED){
			public ColorRandom NextColor(){
				return GREEN;
			}
		},
		GREEN(Color.GREEN){
			public ColorRandom NextColor(){
				return WHITE;
			}
		},
		WHITE(Color.WHITE){
			public ColorRandom NextColor(){
				return RED;
			}
		};
		public abstract ColorRandom NextColor();
		private Color color;
		private ColorRandom(Color color){
			this.color = color;
			};
		
	}
	
}
