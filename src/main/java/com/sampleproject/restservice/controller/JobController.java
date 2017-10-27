package com.sampleproject.restservice.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sampleproject.restservice.domain.Job;
import com.sampleproject.restservice.domain.JobStatus;
import com.sampleproject.restservice.domain.SubTask;
import com.sampleproject.restservice.exception.JobExecutionException;
import com.sampleproject.restservice.model.JobResponse;
import com.sampleproject.restservice.model.UrlList;

/**
 * This is rest controller class. This class
 * would maintain job directory. All the jobs would be 
 * stored in that directory. 
 * @author atulkotwale
 *
 */


@RestController
public class JobController {


	private final Map<Long, Job> jobDirectory = new ConcurrentHashMap<Long, Job>();
	private final Logger logger = LoggerFactory.getLogger(JobController.class); 
	
	
	
    /**
     * This method will create new job object. The job object would contain
     * sub task object for each url. The sub task would be trigger on the execution of 
     * the main job. It will save the job in the job directory for future reference.
     * @param list - list of the url(s).
     * @return jobId - Id of the job.
     */
	@RequestMapping(value = "/submitJob", method = RequestMethod.POST, consumes = "application/json")
	public ResponseEntity<String> submitJob(@RequestBody UrlList list) {
		
		
		
		if(list ==null || list.getUrlList() == null || list.getUrlList().isEmpty()) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("");
		}
		
		
		logger.debug("Started submitJob method.");
		if(logger.isDebugEnabled()) {
		logger.debug("following urls got as input -");
		list.getUrlList().stream().forEach(x -> {
		   logger.debug(x);	
		});
		}

		Job newJob = new Job(list.getUrlList());
		Long jobId = System.currentTimeMillis();
		jobDirectory.put(jobId, newJob);
		try {
			newJob.compute();
		} catch (JobExecutionException e) {
			logger.error("Error occured while submitting the job." + e.getMessage());
		}

		logger.debug("Job saved. jobId is-" + jobId.toString());
		return ResponseEntity.ok(jobId.toString());
	}
    
	
	/**
	 * This method would fetch the job from the job directory. It 
	 * would populate the response list for each sub task and send 
	 * it to client.
	 * @param jobId - Id of the job needs to fetch.
	 * @return - List of the response of the sub task.
	 */
	@RequestMapping(value="/getJobStatus")
	public ResponseEntity<List<JobResponse>> getJobStatus(@RequestParam String jobId) {

		
		if(jobId == null || jobId.isEmpty()) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
		}
		
		final Long jobNumber = Long.valueOf(jobId);
		logger.debug("Job id recieved from client - " + jobNumber);
		
		final List<JobResponse> responseList = new ArrayList<JobResponse>();
		
		if (jobDirectory.containsKey(jobNumber)) {
			logger.debug("found the job in the job directory. fetching the responses.");
			Job newJob = jobDirectory.get(jobNumber);
			newJob.getJobList().stream().forEach(job -> {
				JobResponse jobResponse = new JobResponse();
				jobResponse.setUrl(((SubTask) job).getUrl());
				if (((SubTask) job).getCurrentStatus().equals(JobStatus.COMPLETE)) {
					jobResponse.setResult(((SubTask) job).getResult().toString());
				} else {
					jobResponse.setResult(((SubTask) job).getCurrentStatus().getValue());
				}
				responseList.add(jobResponse);
		    });
			
			logger.debug("Finished fetching responses.");
			if(logger.isDebugEnabled()) {
				responseList.stream().forEach( x -> {
					logger.debug("url -" + x.getUrl() + " result- " + x.getResult());
				});
			}
		  	
		  return ResponseEntity.status(HttpStatus.OK).body(responseList);

		} else {
          logger.debug("Could not find jobid " + jobId + " in job directory.");
          return  ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
		}

		
	}

}
