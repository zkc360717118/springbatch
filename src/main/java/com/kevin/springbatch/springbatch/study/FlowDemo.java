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


/**
 * 展示如果创建job和多个step
 */
@Configuration
@EnableBatchProcessing
public class FlowDemo {
    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;


    @Bean
    public Job jobWithFlow() {
        return jobBuilderFactory.get("jobWithFlow2")
                .start(flowDemoFlow())
                .next(steptest3())
                .end()
                .build();
    }

    /**
     * 创建flow(其实就是把多个step放到flow包在一起)
     * @return
     */
    @Bean
    public Flow flowDemoFlow() {
        return new FlowBuilder<Flow>("flowDemoFlow")
                .start(flow_step1())
                .next(flow_step2())
                .build();
    }

    @Bean
    public Step flow_step1() {
        return stepBuilderFactory.get("flow_step1")
                .tasklet((contribution, chunkContext) -> {
                    System.out.println("flow step1");
                    return RepeatStatus.FINISHED;
                }).build();
    }

    @Bean
    public Step flow_step2() {
        return stepBuilderFactory.get("flow_step2")
                .tasklet((contribution, chunkContext) -> {
                    System.out.println("flow step2");
                    return RepeatStatus.FINISHED;
                }).build();
    }

    @Bean
    public Step steptest3() {
        return stepBuilderFactory.get("flow_step3")
                .tasklet((contribution, chunkContext) -> {
                    System.out.println("flow step3");
                    return RepeatStatus.FINISHED;
                }).build();
    }

}
