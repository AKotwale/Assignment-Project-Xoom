/*
 * Copyright 2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.sampleproject.restservice.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class JobControllerTests {

	@Autowired
	private MockMvc mockMvc;

	@Test
	public void submitJobShouldReturnJobIdMessage() throws Exception {

		this.mockMvc.perform(post("/submitJob").contentType(MediaType.APPLICATION_JSON).content(
				"{\"urlList\" : [\"https://stackoverflow.com\", \"https://www.google.com\", \"https://www.timesofindia.com\",\"https://www.facebook.com\",\"https://www.thehindu.com\",\"https://www.dawn.com\",\"https://unsplash.com/\"]}\n"))
				.andDo(print()).andExpect(status().is2xxSuccessful());

	}

	@Test
	public void submitJobShouldReturnBadRequestStatusOnEmptyUrlList() throws Exception {

		this.mockMvc.perform(post("/submitJob").contentType(MediaType.APPLICATION_JSON).content("{\"urlList\" : []}\n"))
				.andDo(print()).andExpect(status().isBadRequest());

	}

	@Test
	public void getJobStatusShouldReturnBadRequestStatusOnBlankJobId() throws Exception {

		this.mockMvc.perform(get("/getJobStatus").param("jobId", "")).andDo(print()).andExpect(status().isBadRequest());

	}

	@Test
	public void getJobStatusShouldReturnBadRequestStatusOnNullJobId() throws Exception {

		this.mockMvc.perform(get("/getJobStatus")).andDo(print()).andExpect(status().isBadRequest());

	}

}
