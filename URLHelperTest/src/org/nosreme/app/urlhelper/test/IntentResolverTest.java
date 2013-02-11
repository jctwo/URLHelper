package org.nosreme.app.urlhelper.test;

import org.nosreme.app.urlhelper.IntentResolver;

import android.test.AndroidTestCase;

public class IntentResolverTest extends AndroidTestCase {

	public void testResolver() {
		IntentResolver resolver = new IntentResolver(getContext().getPackageManager(), "http://www.example.org/");
		
		assertNotNull(resolver);
		
		
	}
}
