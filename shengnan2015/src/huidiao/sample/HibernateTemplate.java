package huidiao.sample;
/**
	�ɼ������˼̳г����෽ʽ�Ļص���ʽ���Ӽ����
	����ҪΪ��ʵ�ֳ��󷽷������Ǽ̳г����࣬����ֻ��Ҫͨ���ص�������һ���������ɣ�
	���ӵ�ֱ�ۼ���������ǻص��ĺô�֮һ�� 
 */
public class HibernateTemplate {     
    public void execute(CallBack action){    
        getConnection();    
        action.doCRUD();    
        releaseConnection();    
    }    
     
    public void add(){    
         execute(new CallBack(){    
            public void doCRUD(){    
                System.out.println("ִ��add����...");    
            }    
         });    
     }     
  
     public void delete(){    
         execute(new CallBack(){    
            public void doCRUD(){    
                System.out.println("ִ��delete����...");    
            }    
         });    
     }   
    
    public void getConnection(){    
        System.out.println("�������...");    
    }    
        
    public void releaseConnection(){    
        System.out.println("�ͷ�����...");    
    }    
        
} 