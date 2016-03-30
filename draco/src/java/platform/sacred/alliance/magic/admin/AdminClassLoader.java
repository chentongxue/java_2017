package sacred.alliance.magic.admin;

import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLStreamHandlerFactory;

public class AdminClassLoader extends URLClassLoader {

	public AdminClassLoader(URL[] urls, ClassLoader parent) {
		super(urls, parent);
	}

	public AdminClassLoader(URL[] urls) {
		super(urls);
	}

	public AdminClassLoader(URL[] urls, ClassLoader parent, URLStreamHandlerFactory factory) {
		super(urls, parent, factory);

	}

	/**
	 * Append a URL to this loader's search path.
	 * @param url The URL to append.
	 */
	public void addURL(URL url) {
		super.addURL(url);
	}
	
}
