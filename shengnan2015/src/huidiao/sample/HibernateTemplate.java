package huidiao.sample;
/**
	可见摒弃了继承抽象类方式的回调方式更加简便灵活。
	不需要为了实现抽象方法而总是继承抽象类，而是只需要通过回调来增加一个方法即可，
	更加的直观简洁灵活。这算是回调的好处之一。 
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
                System.out.println("执行add操作...");    
            }    
         });    
     }     
  
     public void delete(){    
         execute(new CallBack(){    
            public void doCRUD(){    
                System.out.println("执行delete操作...");    
            }    
         });    
     }   
    
    public void getConnection(){    
        System.out.println("获得连接...");    
    }    
        
    public void releaseConnection(){    
        System.out.println("释放连接...");    
    }    
        
} 