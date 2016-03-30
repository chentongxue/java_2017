package sacred.alliance.magic.app.ai;

public interface State<T> {
	public StateType getStateType();
	public void enter(T entity);
	public void execute(T entity);
	public void exit(T entity);
	public void onMessage(T entity, Telegram telegram);
}
