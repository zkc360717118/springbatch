package com.kevin.springbatch.springbatch.study;

import com.kevin.springbatch.springbatch.study.writer.MyWriter;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;


/**
 * 展示如果创建job和多个step
 */
@Configuration
@EnableBatchProcessing
public class ItemWriterDemo {
    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Autowired
    @Qualifier("myWriter")
    private ItemWriter<String> myWriter;

    @Bean
    public Job ItemWriterDemoJob() {
        return jobBuilderFactory.get("ItemWriterDemoJob1")
                .start(ItemWriterDemoJobStep())
                .build();

    }

    @Bean
    public Step ItemWriterDemoJobStep() {
        return stepBuilderFactory.get("ItemWriterDemoJobStep")
                .<String,String>chunk(5)
                .reader(myRead())
                .writer(myWriter)
                .build();
    }

    @Bean
    public  ItemReader<String> myRead() {
        ArrayList<String> items = new ArrayList<>();

        for (int i = 0; i < 50; i++) {
            items.add("java" + i);

        }
        return new ListItemReader<String>(items);
    }


}
