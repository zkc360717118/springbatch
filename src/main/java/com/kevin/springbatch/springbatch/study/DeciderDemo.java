package com.kevin.springbatch.springbatch.study;

import com.kevin.springbatch.springbatch.study.decider.MyDecider;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.job.flow.JobExecutionDecider;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * 决策器
 */
@Configuration
@EnableBatchProcessing
public class DeciderDemo {
    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job DeciderDemoJob() {
        return jobBuilderFactory.get("DeciderDemoJob")
                .start(decide_step1())
                .next(myDecider())
                .from(myDecider()).on("even").to(decide_step2())
                .from(myDecider()).on("odd").to(decide_step3())
                //*表示无论decide_step3()返回什么值，都再次返回上面next(myDecider()) 这一步； 这一步这么做就是为了，count++ 以后能再次回去执行一次even.。 如果不加整个流程只会执行decide_step1 和 decide_step3
                .from(decide_step3()).on("*").to(myDecider())
                .end()
                .build();
    }

    //创建决策器
    @Bean
    public JobExecutionDecider myDecider() {
        return new MyDecider();
    }

    @Bean
    public Step decide_step1() {
        return stepBuilderFactory.get("decide_step1")
                .tasklet((contribution, chunkContext) -> {
                    System.out.println("decide_step1");
                    return RepeatStatus.FINISHED;
                }).build();
    }

    @Bean
    public Step decide_step2() {
        return stepBuilderFactory.get("decide_step2")
                .tasklet((contribution, chunkContext) -> {
                    System.out.println("decide_step2");
                    return RepeatStatus.FINISHED;
                }).build();
    }

    @Bean
    public Step decide_step3() {
        return stepBuilderFactory.get("decide_step3")
                .tasklet((contribution, chunkContext) -> {
                    System.out.println("decide_step3");
                    return RepeatStatus.FINISHED;
                }).build();
    }

}
