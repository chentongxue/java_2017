package observer.testapi;

import java.util.Observable;
import java.util.Observer;

public class Watcher implements Observer {
	public Watcher(Watched w)
	{
		w.addObserver(this);
	}

	@Override
	public void update(Observable o, Object arg) {
		System.out.println("Data has been changed to: '" + ((Watched)o).retrieveData() + "'---args=="+arg);
	}
}