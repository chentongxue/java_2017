package stream;

import java.io.IOException;
import java.io.PipedOutputStream;

class Sender extends Thread               //内部类 发送线程
{
	private PipedOutputStream[] out;
	private int max;
	public Sender(PipedOutputStream[] out, int max)
	{
		this.out = out;
		this.max = max;
	}
	public Sender(PipedOutputStream[] out)
	{
		this(out, 52);
	}
	public void run()
	{
		System.out.print("Sender :");
		int k = 1;
		try
		{
			while(k <= this.max)
			{
				for(int i = 0; k <= this.max && i<out.length; i++)
				{
					this.out[i].write(k);
					System.out.print(k+" ");
					k++;
				}
			}
		    for(int i = 0; i < out.length; i++)
		    	this.out[i].close();              //关闭管道输出流
		    System.out.println();
		}
		catch(IOException e)
		{
			System.out.println("91"+e);
		}
	}
}