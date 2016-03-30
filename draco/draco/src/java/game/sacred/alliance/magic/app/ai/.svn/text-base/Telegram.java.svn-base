package sacred.alliance.magic.app.ai;

import sacred.alliance.magic.vo.AbstractRole;

/**
 *
 * 100316 改为非泛型
 */
public class Telegram {

    private final AbstractRole sender; //其实发送者也是NpcInstance,为了以后扩展定义为AbstractRole
    private final AbstractRole receiver;
    private final MessageType type;
    private double dispatchTime;
    private Object extraInfo;

    public double getDispatchTime() {
        return dispatchTime;
    }

    public void setDispatchTime(double dispatchTime) {
        this.dispatchTime = dispatchTime;
    }

    public AbstractRole getSender() {
        return sender;
    }

    public AbstractRole getReceiver() {
        return receiver;
    }

    public MessageType getType() {
        return type;
    }

    public Telegram(AbstractRole sender, AbstractRole receiver, MessageType type) {
        this(sender, receiver, type, 0, null);
    }

    public Telegram(AbstractRole sender, AbstractRole receiver, MessageType type, double dispatchTime) {
        this(sender, receiver, type, dispatchTime, null);
    }

    public Telegram(AbstractRole sender, AbstractRole receiver, MessageType type, double dispatchTime, Object extraInfo) {
        this.sender = sender;
        this.receiver = receiver;
        this.type = type;
        this.dispatchTime = dispatchTime;
        this.extraInfo = extraInfo;
    }

    public Object getExtraInfo() {
        return extraInfo;
    }

    public void setExtraInfo(Object extraInfo) {
        this.extraInfo = extraInfo;
    }
}
