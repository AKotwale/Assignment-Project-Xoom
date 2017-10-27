package com.sampleproject.restservice.strategy;

import com.sampleproject.restservice.exception.DataFetchingException;

/**
 * Base class for fetching strategy. 
 * @author atulkotwale
 *
 */
public interface DataFetchingStrategy {
     
	public String fetchData(String url) throws DataFetchingException;
}
