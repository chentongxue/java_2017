package stream;
/**
 *管道流实现发牌程序 
 */
import java.io.*;
public class SendCard 
{
	PipedInputStream[] in;                  //管道输入流
	PipedOutputStream[] out;                //管道输出流
	int[][] data;                           //保存牌
	
	public SendCard(int max, int n) throws IOException
	{
		in = new PipedInputStream[n];       //管道输入流对象数组
		out = new PipedOutputStream[n];     //管道输出流对象数组
		for(int i = 0; i < n; i ++)
		{
			in[i] = new PipedInputStream(); //管道输入流对象
			out[i] = new PipedOutputStream(in[i]);
		}
		Sender s = new Sender(out, max);    //创建发牌线程
		s.setPriority(10);                  //设置最高优先级
		s.start();
		data = new int[n][max/n];           //保存牌n个人，每人 max/n 张牌
		for(int i = 0; i < n; i++)          //创建并启动n个接收线程
			new Receiver(in[i], data[i]).start();//第i个人的牌保存在data[i]数组中	
	}
	public void finalize() throws IOException
	{
		for(int i = 0; i < in.length; i++)
		{
			in[i].close();
			out[i].close();
		}
	}
	public void print()
	{
		for(int i = 0; i < data.length; i++)
		{
			System.out.print("Receiver");
			for(int j = 0; j < data[i].length; j++)
				System.out.print(data[i][j]+" ");
			System.out.println();
		}
	}
	public static void main(String[] args) throws IOException 
	{
		SendCard sc = new SendCard(52, 4);
		try
		{
			Thread.sleep(32);                    //让出CPU让发送线程和接收线程先执行
		}
		catch(InterruptedException e){}
		sc.print();
		
	}
}
