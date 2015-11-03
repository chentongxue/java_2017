package stream;

import java.io.IOException;
import java.io.PipedInputStream;

class Receiver extends Thread                       //内部类 接收线程
{
	private PipedInputStream in;
	int data[];
	
	public Receiver(PipedInputStream in, int data[])
	{
		this.in = in;
		this.data = data;
	}
	public void run()
	{
		try
		{
			int i = 0, value = this.in.read();
			while(value != -1)
			{
				data[i++] = value;
				value = this.in.read();
			}
			this.in.close();
		}
		catch(IOException e)
		{
			System.out.println("119"+e);
		}
	}
}