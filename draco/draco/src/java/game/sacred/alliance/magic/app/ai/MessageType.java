package sacred.alliance.magic.app.ai;

public enum MessageType {
    MOVE(1),//行走
    SHOUT(2), //呼叫
    ATTACK(3),//攻击
    INVIEW(4),//进入视野
    JUSTDIE(5),//死亡瞬间
//    SEEK_RESCUE(6),//求补血
    ;
    
    private final int type ;
    MessageType(int type){
    	this.type = type ;
    }
    
    public int getType(){
    	return this.type ;
    }
}
