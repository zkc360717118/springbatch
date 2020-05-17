package com.kevin.springbatch.springbatch.study.childJobs;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * 嵌套job
 */
@Configuration
@EnableBatchProcessing
public class ChildJob2 {
    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;


//    第二个job
    @Bean
    public Job jobJobTwo() {
        return jobBuilderFactory.get("job2")
                .start(job2step1())
                .build();
    }


    @Bean
    public Step job2step1() {
        return stepBuilderFactory.get("job2step1")
                .tasklet((contribution, chunkContext) -> {
                    System.out.println("job2step1");
                    return RepeatStatus.FINISHED;
                }).build();
    }

}
