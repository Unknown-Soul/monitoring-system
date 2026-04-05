package com.springbatch.batchsample.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.job.JobExecution;
import org.springframework.batch.core.listener.JobExecutionListener;
import org.springframework.stereotype.Component;

/*
 * ============================================================
 * JOB LISTENER: JobCompletionNotificationImpl
 * ============================================================
 * - Executes callback methods before/after a Job execution.
 * - Implements JobExecutionListener interface.
 * 
 * Interview Q: What listeners are available in Spring Batch?
 * - JobExecutionListener: beforeJob(), afterJob()
 * - StepExecutionListener: beforeStep(), afterStep()
 * - ChunkListener: beforeChunk(), afterChunk(), onError()
 * - ItemReadListener: afterRead(), beforeRead(), onReadError()
 * - ItemProcessListener: afterProcess(), beforeProcess(), onProcessError()
 * - ItemWriteListener: afterWrite(), beforeWrite(), onWriteError()
 */
@Component
public class JobCompletionNotificationImpl implements JobExecutionListener {

    private Logger logger = LoggerFactory.getLogger(JobCompletionNotificationImpl.class);

    // Called BEFORE the Job starts
    @Override
    public void beforeJob(JobExecution jobExecution) {
        logger.info(">>> JOB STARTED <<<");
    }

    // Called AFTER the Job completes
    @Override
    public void afterJob(JobExecution jobExecution) {
        if (jobExecution.getStatus() == BatchStatus.COMPLETED) {
            logger.info(">>> JOB COMPLETED SUCCESSFULLY <<<");
        } else {
            logger.error(">>> JOB FAILED WITH STATUS: {} <<<", jobExecution.getStatus());
        }
    }
}

/*
 * Interview Q: What is BatchStatus?
 * 
 * BatchStatus enum values:
 * - COMPLETED: Job finished successfully
 * - FAILED: Job finished with error
 * - STARTED: Job is running
 * - STARTING: Job about to start
 * - STOPPING: Job is stopping
 * - STOPPED: Job was stopped manually
 * - ABANDONED: Job was abandoned
 * 
 * Interview Q: How to access job execution details?
 * 
 * JobExecution provides:
 * - getJobId(): Unique job ID
 * - getJobParameters(): Parameters passed to job
 * - getStepExecutions(): List of all step executions
 * - getStatus(): Current status of the job
 * - getStartTime(), getEndTime(), getDuration()
 */