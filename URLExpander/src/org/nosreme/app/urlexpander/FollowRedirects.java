package org.nosreme.app.urlexpander;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;

public class FollowRedirects {
	/* Follow one HTTP redirect, by issuing a HEAD request and checking
	 * for a 3XX return code with Location: header.
	 * Return null if there's no redirection. */
	public static String followRedirect(String urlString)
	{
    	try {
            URL url;
    		url = new URL(urlString);
    		HttpURLConnection conn = (HttpURLConnection)url.openConnection();
    		conn.setRequestMethod("HEAD");
    		conn.setInstanceFollowRedirects(false);
    		// See http://code.google.com/p/android/issues/detail?id=16227                         
    		// and http://code.google.com/p/android/issues/detail?id=24672
    		// for why the Accept-Encoding is required to disable gzip
    		conn.setRequestProperty("Accept-Encoding", "identity");
    		int resp = conn.getResponseCode();
    		if (resp == 301 || resp == 302)
    		{
    			Map<String, List<String>> headers = conn.getHeaderFields();
    			List<String> hlist = headers.get("location");
    			if (hlist.size() != 1)
    			{
    				/* There's either more than one Location header or
    				 * none - this is a failure.
    				 */
    				return null;
    			}
    			else
    			{
    				/* There is exactly one location header, so return
    				 * its contents as a link. 
    				 */
    				return hlist.get(0);
    			}
    		}
    		else
    		{
    			return null;
    		}	
    	}
    	catch (Exception e)
    	{
    		return null;
    	}
		
	}
	
	/* Follow up to maxIterations redirections and return the final
	 * non-redirected URL.
	 * If the original URL doesn't redirect, then returns null.
	 */
	public static String expandUrl(String urlString, int maxIterations)
	{
        String result = null;
        int iterations = maxIterations;
        do {
        	String u = followRedirect(urlString);
        	if (u != null)
        	{
        		result = u;
        	}
        } while (--iterations > 0);
        
        return result;
	}


}
