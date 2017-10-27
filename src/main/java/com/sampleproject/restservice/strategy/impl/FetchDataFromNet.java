package com.sampleproject.restservice.strategy.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sampleproject.restservice.controller.JobController;
import com.sampleproject.restservice.exception.DataFetchingException;
import com.sampleproject.restservice.strategy.DataFetchingStrategy;

/**
 * This class is implementation of data fetching algorithm for
 * url location. It downloads the content from the internet.
 * 
 * @author atulkotwale
 *
 */
public class FetchDataFromNet implements DataFetchingStrategy {
	
	private final Logger logger = LoggerFactory.getLogger(FetchDataFromNet.class);
	
	/**
	 * This method fetch the data from Internet.
	 */
	@Override
	public String fetchData(final String inputUrl) throws DataFetchingException {

		URL url;
		InputStream is = null;
		BufferedReader br = null;
		String line = null;
		StringBuffer buf = new StringBuffer();
		logger.debug("fetching data from url." + inputUrl);
		try {
			url = new URL(inputUrl);
			is = url.openStream(); // throws an IOException
			br = new BufferedReader(new InputStreamReader(is));

			while ((line = br.readLine()) != null) {
				buf.append(line);
			}

		} catch (IOException mue) {
			logger.error("Error occured while fetching the data from network." + mue.getMessage());
		} finally {
			try {
				if (is != null)
					is.close();
			} catch (IOException ioe) {
				logger.error("Error occcured while closing the input stream."+ ioe.getMessage());
			}
		}

		return buf.toString();

	}

}
