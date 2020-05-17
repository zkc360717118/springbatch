package com.kevin.springbatch.springbatch.study;

import org.springframework.batch.core.*;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;


/**
 * 展示如果创建job和多个step
 */
@Configuration
@EnableBatchProcessing
public class ParametersDemo implements StepExecutionListener {
    private Map<String,JobParameter> parameters;

    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job paramDemo() {
        return jobBuilderFactory.get("parametersDemo4")
                .start(parameterstep1())
                .build();


    }


    /**
     *     job 执行的是step,job使用的数据肯定是step中使用，所以只需要给step传递数据，如何做?
     */
    @Bean
    public Step parameterstep1() {
        return stepBuilderFactory.get("parameterstep1")
                .listener(this) //因为这里已经实现了监听，所以用this
                .tasklet((contribution, chunkContext) -> {
                    //输出外面传递进来的参数
                    System.out.println("info 信息："+parameters.get("info"));
                    return RepeatStatus.FINISHED;
                }).build();
    }


    @Override
    public void beforeStep(StepExecution stepExecution) {
//        job执行之前，就把参数先传递给类变量
        parameters = stepExecution.getJobParameters().getParameters();
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        return null;
    }
}
