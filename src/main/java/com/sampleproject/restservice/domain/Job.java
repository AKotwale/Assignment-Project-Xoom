package com.sampleproject.restservice.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sampleproject.restservice.exception.JobExecutionException;
import com.sampleproject.restservice.model.Computable;
import com.sampleproject.restservice.strategy.DataFetchingStrategy;
import com.sampleproject.restservice.strategy.impl.FetchDataFromNet;

/**
 * Job class.It contains sub task list for each url.
 * It follows composite design pattern. 
 * 
 * @author atulkotwale
 *
 */
public class Job implements Computable {
	
	final private List<Computable> jobList = new ArrayList<Computable>();
	final private DataFetchingStrategy fetchStrategy = new FetchDataFromNet();
	final private Logger logger = LoggerFactory.getLogger(Job.class);
	
	public List<Computable> getJobList() {
		return jobList;
	}


	public Job(List<String> urlList) {
		urlList.forEach(x -> {
			SubTask task = new SubTask(x, fetchStrategy);
			jobList.add(task);
		});
	}

	/**
	 * This method would invoke the sub task execution.
	 * This method uses completable future to decouple the 
	 * job processing from rest service end point.
	 * @throws - JobExecutionException - if any error occured 
	 * during execution.
	 */
	@Override
	public void compute() throws JobExecutionException {
		
		CompletableFuture.runAsync(() -> {
            
			jobList.forEach( task -> {
				try {
					task.compute();
				} catch (Exception e) {
					
					e.printStackTrace();
				}
			});
	  		
	    }).whenComplete((voidValue, error) -> {
			if(error != null) {
				logger.error("Error got while execution job." + error.getMessage());
			}
		});
	}

}
