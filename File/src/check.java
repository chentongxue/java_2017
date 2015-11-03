//����9.10��  ʹ�ùܵ���ʵ�ַ��Ƴ���

import java.io.*;

public class check
{
    public check(int n) throws IOException             //nָ������������
    {
        PipedInputStream[] in = new PipedInputStream[n];     //�ܵ���������������
        PipedOutputStream[] out = new PipedOutputStream[n];  //�ܵ��������������

        for(int i=0;i<in.length;i++)
        {
            in[i] = new PipedInputStream();                //����һ���ܵ�����������
            out[i] = new PipedOutputStream(in[i]);         //����һ���ܵ���������󲢽�������
        }
        
        new Sender(out, 12).start();                       //����һ�������߳�
        for(int i=0;i<in.length;i++)                       //������������߳�
            new Receiver(in[i]).start();
    }
    
    public check() throws IOException
    {
        this(4);                                           //Ĭ����4��������
    }
    
    public static void main (String args[]) throws IOException
    {
        new check();                                    //4��������
    }
}

class Sender extends Thread                                //�����߳�
{
    private PipedOutputStream[] out;
    private int max;
    
    public Sender(PipedOutputStream[] out,int max)
    {
        this.out= out;
        this.max= max;                                     //�������
    }
    
    public Sender(PipedOutputStream[] out)
    {
        this(out,52);
    }

    public void run()                                      //�߳���
    {
        System.out.print("Sender:  ");
        int k=1;
        try 
        {
            while (k<=this.max)
            {
                for(int i=0; k<=this.max && i<out.length; i++)
                {
                    this.out[i].write(k);
                    System.out.print(k+"  ");
                    k++;
                }
            }
            
            for(int i=0;i<out.length;i++)
                this.out[i].close();                       //�رչܵ������
            System.out.println();
        }
        catch(IOException e)
        {
            System.out.println(e);
        }
    }
}

class Receiver extends Thread                              //�����߳�
{
    private PipedInputStream in;
    
    public Receiver(PipedInputStream in)
    {
        this.in = in;
    }
    
    public void run()
    {
        System.out.print("Receiver: "+this.getName()+"  ");
        try
        {
            int i=-1;
            do                                            //������δ����ʱ
            {
                i = this.in.read();
                if (i!=-1)
                    System.out.print(i+"  ");
            }while (i!=-1);
            
            System.out.println();
            this.in.close();                              //�رչܵ�������
        }
        catch(IOException e)
        {
            System.out.println(e);
        }
    }
}


/*
�������н�����£�
Sender:  1  2  3  4  5  6  7  8  9  10  11  12  
Receiver: Thread-1  1  5  9  
Receiver: Thread-2  2  6  10  
Receiver: Thread-3  3  7  11  
Receiver: Thread-4  4  8  12  


        new SendCard(3);              //3��������

Sender:  1  2  3  4  5  6  7  8  9  10  11  12  
Receiver: Thread-1  1  4  7  10  
Receiver: Thread-2  2  5  8  11  
Receiver: Thread-3  3  6  9  12  

*/