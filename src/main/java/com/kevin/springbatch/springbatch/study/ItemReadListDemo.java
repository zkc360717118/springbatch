package com.kevin.springbatch.springbatch.study;

import com.kevin.springbatch.springbatch.study.reader.MyReader;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableBatchProcessing
public class ItemReadListDemo {
    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job ItemReadMethod() {
        return jobBuilderFactory.get("itemReadDemo1")
                .start(itemReadstep1())
                .build();

    }

    @Bean
    public Step itemReadstep1() {
        return stepBuilderFactory.get("itemReadstep1")
                .<String,String>chunk(2)
                .reader(iteamReaderDemoReader())
                .writer(list->{
                    StringBuilder sb = new StringBuilder();
                    for (String s : list) {
                        sb.append(s);
                    }
                    System.out.println("结果："+sb.toString());  // 前面chunk规定了一次读取2个，所以总共有4条数据，调用wirter2次。每次一个list有2个元素。
                })
                .build();
    }

    @Bean
    public MyReader iteamReaderDemoReader() {
        List<String> data = Arrays.asList("cat", "dog", "duck", "pig");
        return new MyReader(data);
    }
}
