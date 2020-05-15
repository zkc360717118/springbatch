package com.kevin.springbatch.springbatch.config;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableBatchProcessing
public class JobConfiguration {
    //注入创建对象的 对象
    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    //注入创建step对象的 对象
    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    //开始创建任务
    @Bean
    public Job helloWorld() {
        return jobBuilderFactory.get("helloWorld")
                .start(step1())
                .build();
    }

    @Bean
    public Step step1() {
        return stepBuilderFactory.get("step1")
                .tasklet((contribution, chunkContext) -> {
                    System.out.println("hello world");
                    return RepeatStatus.FINISHED ;
                }).build();
    }

}
