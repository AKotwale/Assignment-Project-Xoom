package com.sampleproject.restservice.domain;

/**
 * Job status enum class.
 * @author atulkotwale
 *
 */
public enum JobStatus {
	NOT_STARTED("not_started"), STARTED("started"), PENDING("pending"), FAILED("failed"), COMPLETE("complete");

	String value;

	public String getValue() {
		return value;
	}

	JobStatus(String value) {
		this.value = value;
	}

}
