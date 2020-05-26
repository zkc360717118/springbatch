package com.kevin.springbatch.springbatch.study;

import com.kevin.springbatch.springbatch.entity.User;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableBatchProcessing
public class RestartJobDemo {
    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Autowired
    @Qualifier("stringWriter")
    private ItemWriter<? super User> stringWriter;

    @Autowired
    @Qualifier("restartReader")
    private ItemReader<User> restartReader;

    @Bean
    public Job restartJobTest() {
        return jobBuilderFactory.get("restartJobTest9")
                .start(restartStep())
                .build();

    }

    @Bean
    public Step restartStep() {
        return stepBuilderFactory.get("restartStep")
                .<User, User> chunk(10)
                .reader(restartReader)
                .writer(stringWriter)
                .build();
    }


}
