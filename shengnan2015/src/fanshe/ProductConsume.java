package fanshe;
public class ProductConsume
{
	public static void main(String[] args)
	{
		Q q = new Q();
		new Thread(new Producer(q)).start();
		new Thread(new Consumer(q)).start();
	}
}
class Producer implements Runnable
{
	Q q;
	public Producer(Q q)
	{
		this.q = q;
	}
	public void run()
	{
		int i = 0;
		while(true)
		{
			/*synchronized (q)
			{
				if(q.bFull)
					try {q.wait();} catch (InterruptedException e1) {}
				if(i == 0)
				{
					q.name = "张三";
					try {Thread.sleep(10);} catch (InterruptedException e) {}
					q.sex = "male";
				}
				else {
					q.name = "李四";
					q.sex = "female";
				}
				i = (i+1)%2;
				q.bFull = true;
				q.notify();//唤醒，通知wait();
			}//synchronized(q);
			*/
			if(i==0)
				q.put("hellokity", "female");
			else {
				q.put("blackcaptain", "male");
			}
			i = (i+1)%2;
		}
	}
}
class Consumer implements Runnable
{
	Q q;
	public Consumer(Q q)
	{
		this.q = q;
	}
	public void run()
	{
		while(true)
		{
			/*synchronized (q)
			{
				if(!q.bFull)
					try {q.wait();} catch (InterruptedException e) {}
				System.out.print("A"+q.name);
				System.out.println("B"+q.sex);
				q.bFull = false;
				q.notify();//通知生产者线程数据已取走
			}*/
			q.get();
		}
	}
}
class Q
{
	private boolean bFull = false;//缓冲区标记
	private String name = "unKown";
	private String sex = "unKown";
	public synchronized void put(String name,String sex)
	{
		if(bFull)//满的话，要等
			try {wait();} catch (InterruptedException e1) {}
		this.name = name;
		try {Thread.sleep(10);} catch (InterruptedException e) {}
		this.sex = sex;
		bFull = true;
		notify();
	}
	public synchronized void get()
	{
		if(!bFull)//满的话，要等
			try {wait();} catch (InterruptedException e1) {}
		System.out.print(" "+name);
		System.out.println(" "+sex);
		bFull = false;
		notify();
	}
}