package com.kevin.springbatch.springbatch.study;

import com.kevin.springbatch.springbatch.entity.User;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.batch.item.xml.StaxEventItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.oxm.xstream.XStreamMarshaller;
import org.springframework.validation.BindException;

import java.util.HashMap;

@Configuration
@EnableBatchProcessing
public class ItemReadXmlDemo {
    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Autowired
    @Qualifier("stringWriter")
    private  ItemWriter<? super User> stringWriter;

    @Bean
    public Job jobReadXMLDemo() {
        return jobBuilderFactory.get("readXMLDemo1")
                .start(itemReadXmlstep1())
                .build();
    }

    @Bean
    public Step itemReadXmlstep1() {
        return stepBuilderFactory.get("readXmlDemo1step1")
                .<User,User>chunk(100)
                .reader(xmlReader())
                .writer(stringWriter)
                .build();
    }

    @Bean
    @StepScope  //限制范围
    public StaxEventItemReader<User> xmlReader() {
        StaxEventItemReader<User> reader = new StaxEventItemReader<>();
        reader.setResource(new ClassPathResource("files/kevin_user.xml"));

        //指定需要处理的根标签
        reader.setFragmentRootElementName("row");

        //把xml转换成对象
        XStreamMarshaller unMarsheller = new XStreamMarshaller();
        HashMap<String, Class> map = new HashMap<>();
        /**
         * <row>
         *   <uid>57465</uid>
         *   <username>Miss Alejandro Keebler</username>
         *   <password>6559079134</password>
         * </row>
         */
        map.put("row", User.class); //把row标签下面的东西，转成User类
        unMarsheller.setAliases(map);

        reader.setUnmarshaller(unMarsheller);

        return  reader;
    }
}
