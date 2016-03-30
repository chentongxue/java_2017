package baoutil;

import java.awt.Color;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import sacred.alliance.magic.core.Service;
import sacred.alliance.magic.vo.RoleInstance;

import lombok.Data;

import com.game.draco.app.vip.VipAppImpl;
import com.google.common.collect.Multimap;
public @Data class LogB{
	private static LogB LogB = null;
	public static LogBatJFrame jFrame = null;
	private  ColorRandom co = ColorRandom.GREEN;
	public  VipAppImpl VipAppImpl = null;
	public  AtomicInteger a = new AtomicInteger(0);
	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-mm-dd-hh-mm-ss-SSS");
	private  Lock lock = new ReentrantLock();
	private LogB(){
		jFrame = new LogBatJFrame("william");
	}
	public static synchronized LogB getInstance(){
		if(LogB==null){
			System.err.println("LogB==null so new one");
			LogB = new LogB();
		}
		return LogB;
	}
	public   <T> void i(T s){
		LogB.getInstance();
		co = co.NextColor();
		jFrame.setDocs("\n"+s, co.color, null, null, null);
	}
	/**
	 * 有时间  有行
	 * @param <T>
	 * @param s
	 * @date 2014-7-23 下午04:21:44
	 */
	public static <T> void ict(T s){
		LogB.getInstance().iCurrentAndCountT(s);
	}
	public static <T> void ic(T s){
		LogB.getInstance().iCurrentAndCount(s);
	}
	public static <T> void iTime(T s){
		if(s instanceof Date){
			LogB.getInstance().iCurrentAndCount(sdf.format(s));
		}else{
			LogB.getInstance().iCurrentAndCount(s);
		} 
	}
	public static <T> void icMap(Map m){
		  String s = m.toString();
		  s = s.replaceAll("\\(", "\\(\n");
		LogB.getInstance().iCurrentAndCount(s);
	}
	public static <T> void icMap(Multimap m){
		String s = m.toString();
		s = s.replaceAll("\\(", "\\(\n");
		LogB.getInstance().iCurrentAndCount(s);
	}
	public <T> void iCurrentAndCount(T s){
		lock.lock(); 
		co = co.NextColor();
		a.getAndIncrement(); 
		jFrame.setDocs("\n<"+a+">"+s, co.color, null, null, null);
		lock.unlock();
	}
	public <T> void iCurrentAndCountT(T s){
		lock.lock(); 
		co = co.NextColor();
		a.getAndIncrement(); 
		long time = System.currentTimeMillis();
		String dt = sdf.format(new Date(time));
		jFrame.setDocs("\n<"+a+">"+dt+"-=-"+s, co.color, null, null, null);
		lock.unlock();
	}
	public static <T> void iList(List list){
		if(list==null){
			LogB.getInstance().iCurrentAndCount("为空");
		}else if(list.size()==0){
			LogB.getInstance().iCurrentAndCount("为0");
		}else{
			String s = Arrays.toString(list.toArray());
			s = s.replaceAll("\\(", "\\(\n");
			LogB.getInstance().iCurrentAndCount(s);
		}
	}
	public   <T> void iList(Collection list){
		LogB.getInstance();
		String s = Arrays.toString(list.toArray());
		s = s.replaceAll("\\(", "\\(\n");
		co = co.NextColor();
		jFrame.setDocs("\n"+getTraceInfo()+s, co.color, null, null, null);
	}
	public   <T> void s(T s){
		LogB.getInstance();
		co = co.NextColor();
		jFrame.setDocs("\n"+getTraceInfo()+s, co.color, null, null, null);
	}
	public   <T> void y(T s){
		LogB.getInstance();
		jFrame.setDocs("\n"+s, Color.yellow, null, null, null);
	}
	public  void i(String str, Color col, Color b_col, Boolean bold,
			Integer fontSize){
		LogB.getInstance();
		jFrame.setDocs(str, col, b_col, bold, fontSize);
	}

	public  void i(String str, Color col) {
		LogB.getInstance();
		jFrame.setDocs(str, col, null, null, null);
	}
	public  String getTraceInfo(){  
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
	public  String addSpace(String s, int len){
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
	public static void setRole(RoleInstance role){
		LogB.getInstance().jFrame.setRole(role);
	}
	public static void removeRole(RoleInstance role){
		LogB.getInstance().jFrame.removeRole(role);
	}
	public static void main(String args[]){
		for(int i = 0;i < 100; i++){
			ic("i=1"+i);
		}
	}
}
