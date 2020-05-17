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
public class ChildJob1 {
    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;


//    第一个job
    @Bean
    public Job jobJobOne() {
        return jobBuilderFactory.get("job1")
                .start(job1step1())
                .next(job1step2())
                .build();
    }

    @Bean
    public Step job1step1() {
        return stepBuilderFactory.get("job1step1")
                .tasklet((contribution, chunkContext) -> {
                    System.out.println("job1step1");
                    return RepeatStatus.FINISHED;
                }).build();
    }

    @Bean
    public Step job1step2() {
        return stepBuilderFactory.get("job1step2")
                .tasklet((contribution, chunkContext) -> {
                    System.out.println("job1step2");
                    return RepeatStatus.FINISHED;
                }).build();
    }


}
