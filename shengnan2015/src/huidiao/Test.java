package huidiao;

public class Test {

	 /**  
     * һ�����������Եķ�����������һ���ȽϺ�ʱ��ѭ��  
     */   
    public   static   void  testMethod(){  
        for ( int  i= 0 ; i< 1000000000 ; i++){  
              
        }  
    }  
    /**  
     * һ���򵥵Ĳ��Է���ִ��ʱ��ķ���  
     */   
    public   void  testTime(){  
        long  begin = System.currentTimeMillis(); //������ʼʱ��   
        testMethod(); //���Է���   
        long  end = System.currentTimeMillis(); //���Խ���ʱ��   
        System.out.println("[use time]:"  + (end - begin)); //��ӡʹ��ʱ��   
    }  
      
    public   static   void  main(String[] args) {  
        Test test=new  Test();  
        test.testTime();  
    }  
}  
