package com.sampleproject.restservice.domain;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sampleproject.restservice.exception.DataParsingException;
import com.sampleproject.restservice.exception.JobExecutionException;
import com.sampleproject.restservice.model.Computable;
import com.sampleproject.restservice.strategy.DataFetchingStrategy;
import com.sampleproject.utility.Utility;

/**
 * This is class is responsible to fetch the content form the url location. It
 * would use the data fetching strategy to get the content. It follows composite
 * design pattern.
 * 
 * @author atulkotwale
 *
 */
public class SubTask implements Computable {

	final private Logger logger = LoggerFactory.getLogger(SubTask.class);

	private String url;

	private Long result;

	private ExecutorService service;

	private DataFetchingStrategy dataFetchStrategy;

	private JobStatus currentStatus;

	public JobStatus getCurrentStatus() {
		return currentStatus;
	}

	public SubTask(String url, DataFetchingStrategy fetchStrategy) {
		this.url = url;
		this.dataFetchStrategy = fetchStrategy;
		this.service = Executors.newFixedThreadPool(1);
	}

	public ExecutorService getService() {
		return service;
	}

	public void setService(ExecutorService service) {
		this.service = service;
	}

	public DataFetchingStrategy getDataFetchStrategy() {
		return dataFetchStrategy;
	}

	public void setDataFetchStrategy(DataFetchingStrategy dataFetchStrategy) {
		this.dataFetchStrategy = dataFetchStrategy;
	}

	public String getUrl() {
		return url;
	}

	public Long getResult() {
		return result;
	}

	/**
	 * This method would trigger the fetching process for url location. It would
	 * trigger the execution in separate thread.Thread would be terminated if run
	 * continuously for more then 25 seconds.
	 * 
	 */
	@Override
	public void compute() throws JobExecutionException {

		logger.debug("started execution of subtask for url- " + this.url);
		this.currentStatus = JobStatus.PENDING;

		Future<String> task = service.submit(() -> {
			String htmlContent = dataFetchStrategy.fetchData(url);
			return htmlContent;
		});

		service.shutdown();
		try {
			service.awaitTermination(25, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			logger.error("thread got intrerrupted." + e.getMessage());
		}

		if (task.isDone()) {
            logger.debug("Task is completed for url -" + url);
			String htmlContent;
			try {
				htmlContent = task.get();
				if (htmlContent != null && htmlContent.length() > 0) {
					this.result = Utility.getTagCount(htmlContent, "img");
					this.currentStatus = JobStatus.COMPLETE;
					logger.debug("job is complete. tag count is -" + result);
				} else {
					logger.debug("html content is null or blank for url-" +url);
					this.currentStatus = JobStatus.FAILED;
				}
			} catch (InterruptedException | ExecutionException | DataParsingException e) {

				logger.error("Error occured while executing subtask for url-" + url);
				this.currentStatus = JobStatus.FAILED;
				throw new JobExecutionException();
			}

		} else {
			logger.debug("task is not completed in given time for url -" + url );
			this.currentStatus = JobStatus.FAILED;
		}

	}

}
