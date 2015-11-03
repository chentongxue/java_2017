package stream;
/**
 *�ܵ���ʵ�ַ��Ƴ��� 
 */
import java.io.*;
public class SendCard 
{
	PipedInputStream[] in;                  //�ܵ�������
	PipedOutputStream[] out;                //�ܵ������
	int[][] data;                           //������
	
	public SendCard(int max, int n) throws IOException
	{
		in = new PipedInputStream[n];       //�ܵ���������������
		out = new PipedOutputStream[n];     //�ܵ��������������
		for(int i = 0; i < n; i ++)
		{
			in[i] = new PipedInputStream(); //�ܵ�����������
			out[i] = new PipedOutputStream(in[i]);
		}
		Sender s = new Sender(out, max);    //���������߳�
		s.setPriority(10);                  //����������ȼ�
		s.start();
		data = new int[n][max/n];           //������n���ˣ�ÿ�� max/n ����
		for(int i = 0; i < n; i++)          //����������n�������߳�
			new Receiver(in[i], data[i]).start();//��i���˵��Ʊ�����data[i]������	
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
			Thread.sleep(32);                    //�ó�CPU�÷����̺߳ͽ����߳���ִ��
		}
		catch(InterruptedException e){}
		sc.print();
		
	}
}
