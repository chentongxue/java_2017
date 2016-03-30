package sacred.alliance.magic.app.ai;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sacred.alliance.magic.vo.AbstractRole;

public class StateMachine<T extends AbstractRole> {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final T owner;
    private State<T> current;
    private State<T> previous;
    private State<T> global;

    public StateMachine(T owner) {
        this.owner = owner;
    }

    public void handleMessage(Telegram telegram) {
        if (null == telegram) {
            return;
        }

        if (current != null) {
            current.onMessage(owner, telegram);
        }

        if (global != null) {
            global.onMessage(owner, telegram);
        }

    }

    public void update() {
        if (global != null) {
            global.execute(owner);
        }
        if (current != null) {
        	//System.out.println("roleid="+owner.getRoleId()+"  stateType"+current.getStateType().getName());
            current.execute(owner);
        }
    }

    public void switchState(State<T> state) {
    	//System.out.println("switchState: "+state.getStateType().getName());
        if (this.isInState(state)) {
            return;
        }
        previous = current;
        if (null != current) {
            current.exit(owner);
           /* if (logger.isDebugEnabled() && (owner instanceof NpcInstance)) {
                logger.debug("role: " + owner.getRoleId() + " exit " + current.getStateType().getName() + " " + ((NpcInstance)owner).getNpc().getNpcid());
            }*/
        }
        current = state;
        current.enter(owner);
       /* if (logger.isDebugEnabled()&& (owner instanceof NpcInstance)) {
            logger.debug("role: " + owner.getRoleId() + " enter " + current.getStateType().getName()+ " " + ((NpcInstance)owner).getNpc().getNpcid());
        }*/
    }

    public void revertToPreviousState() {
        switchState(previous);
    }

    public boolean isInState(StateType stateType) {
        if (null == current || null == stateType) {
            return false;
        }
        return current.getStateType().equals(stateType);
    }

    public boolean isInState(State<T> state) {
        if (null == current || null == state) {
            return false;
        }
        return current.getStateType().equals(state.getStateType());
    }

    public void setCurrentState(State<T> state) {
        this.current = state;
    }

    public void setPreviousState(State<T> state) {
        this.previous = state;
    }

    public void setGlobalState(State<T> state) {
        this.global = state;
    }

	public T getOwner() {
		return owner;
	}

	public State<T> getCurrent() {
		return current;
	}

	public State<T> getPrevious() {
		return previous;
	}

	public State<T> getGlobal() {
		return global;
	}
    
}
