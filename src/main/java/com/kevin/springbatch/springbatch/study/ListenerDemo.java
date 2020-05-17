package com.kevin.springbatch.springbatch.study;

import com.kevin.springbatch.springbatch.study.listener.MyChunkListener;
import com.kevin.springbatch.springbatch.study.listener.MyJobListener;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;


/**
 * 展示如果创建job和多个step
 */
@Configuration
@EnableBatchProcessing
public class ListenerDemo {
    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job listenerDemoJob() {
        return jobBuilderFactory.get("listenerDemoJob2")
                .start(listenstep1())
                .listener(new MyJobListener()) //添加监听
                .build();
    }

    @Bean
    public Step listenstep1() {
        return stepBuilderFactory.get("listenstep2")
                .<String, String >chunk(2) //读取2个处理一次 ，规定了输入和输出的类型
                .faultTolerant() // 容错
                .listener(new MyChunkListener())
                .reader(read())
                .writer(write())
                .build();
    }

    @Bean
    public ItemWriter<String> write() {
        return items -> {
            StringBuilder sb = new StringBuilder();
            for (String iterm : items) {
                sb.append(iterm);
            }
            System.out.println(sb.toString());
        };
    }

    @Bean
    public ItemReader<String> read() {
        return new ListItemReader<>(Arrays.asList("spring","java","go","python"));
    }


}
