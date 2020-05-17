package com.kevin.springbatch.springbatch.study;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;


/**
 * 并发执行
 */
@Configuration
@EnableBatchProcessing
public class SplitDemo {
    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;



    @Bean
    public Job splitJobWithFlow() {
        return jobBuilderFactory.get("splitJobWithFlow")
                .start(flowDemoFlow1())
                .split(new SimpleAsyncTaskExecutor()).add(flowDemoFlow2())
                .end()
                .build();
    }

    /**
     * 第一个flow
     * @return
     */
    @Bean
    public Flow flowDemoFlow1() {
        return new FlowBuilder<Flow>("flowDemoFlow")
                .start(split_flow_step1())
                .next(split_flow_step2())
                .build();
    }

    /**
     * 第二个flow
     * @return
     */
    @Bean
    public Flow flowDemoFlow2() {
        return new FlowBuilder<Flow>("flowDemoFlow")
                .start(split_steptest3())
                .build();
    }

    @Bean
    public Step split_flow_step1() {
        return stepBuilderFactory.get("split_flow_step1")
                .tasklet((contribution, chunkContext) -> {
                    System.out.println("split_flow step1");
                    return RepeatStatus.FINISHED;
                }).build();
    }

    @Bean
    public Step split_flow_step2() {
        return stepBuilderFactory.get("split_flow_step2")
                .tasklet((contribution, chunkContext) -> {
                    System.out.println("split_flow step2");
                    return RepeatStatus.FINISHED;
                }).build();
    }

    @Bean
    public Step split_steptest3() {
        return stepBuilderFactory.get("split_flow_step3")
                .tasklet((contribution, chunkContext) -> {
                    System.out.println("split_flow step3");
                    return RepeatStatus.FINISHED;
                }).build();
    }

}
