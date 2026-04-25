package com.farmers.APIUtil;

import java.io.File;
import java.io.FileInputStream;
import java.security.KeyStore;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

import org.apache.logging.log4j.core.Logger;

public class GetSSLSocketFactory {
	
	private static GetSSLSocketFactory getsslsocket = null;
	
	private GetSSLSocketFactory () {
	}
	
	public static GetSSLSocketFactory getInstance() {
		if(getsslsocket == null) {
			getsslsocket = new GetSSLSocketFactory();
		}
		return getsslsocket;
	} 
	
	public SSLSocketFactory getsslsocket(String callid, Logger logger) {
		SSLSocketFactory sslSocketFactory = null;
		try {
			TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
			File file = new File(Constants.SSLSOCKETJKSPATH);
			SSLContext context = SSLContext.getInstance("TLSv1.2");
			logger.info(callid+ " : SSLContext created");
			KeyStore keyStore = KeyStore.getInstance("JKS");
			keyStore.load(new FileInputStream(file), "changeit".toCharArray());
			KeyStore ks = KeyStore.getInstance("jks");
			FileInputStream fis = new FileInputStream(file);
			ks.load(fis, "changeit".toCharArray());
			KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
			tmf.init(ks);
			kmf.init(ks, "changeit".toCharArray());
			context.init(null, tmf.getTrustManagers(), null);
			sslSocketFactory = context.getSocketFactory();
		}
		catch (Exception e) {
            // Handle all exceptions
            logger.error(callid+" : Exception :"+e);
            stacktrace.printStackTrace(logger, e);
            e.printStackTrace();
        }
		return sslSocketFactory;
	}


}
