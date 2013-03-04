package org.nosreme.app.urlhelper.test;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.net.URLStreamHandlerFactory;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FakeHttpOpener implements URLStreamHandlerFactory {
	
	public class FakeHttpConnection extends HttpURLConnection {
		public FakeHttpOpener mOwner;
		
		protected FakeHttpConnection(URL url, FakeHttpOpener owner) {
			super(url);
			mOwner = owner;
		}

		@Override
		public void connect() throws IOException {
		}

		@Override
		public void disconnect() {
		}

		@Override
		public boolean usingProxy() {
			return false;
		}
		
		@Override
		public int getResponseCode() {
		    return 301;
		}
		
		@Override
		public Map<String, List<String>> getHeaderFields() {
			
			Map<String, List<String>> result = new HashMap<String, List<String>>();
			List<String> l = new ArrayList<String>();
			l.add(mOwner.getRedirectUrl());
			
			result.put("location",  l);
			return result;
		}
		
	}
	
	public class FakeURLStreamHandler extends URLStreamHandler {
		
		private FakeHttpConnection conn = null;
		FakeHttpOpener mOwner;
		
		public FakeURLStreamHandler(FakeHttpOpener owner)
		{
			mOwner = owner;
		}
		
		@Override
		protected URLConnection openConnection(URL u) throws IOException {
			if (conn == null)
			{
				conn = new FakeHttpConnection(u, mOwner);
			}
			return conn;
		}
		
	}

	private String redirectUrl;
	public FakeHttpOpener(String url) {
		redirectUrl = url;
	}
	public String getRedirectUrl()
	{
		return redirectUrl;
	}
	public URLStreamHandler createURLStreamHandler(String protocol) {
		return new FakeURLStreamHandler(this);
	}

}
