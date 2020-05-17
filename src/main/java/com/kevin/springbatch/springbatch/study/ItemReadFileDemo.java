package com.kevin.springbatch.springbatch.study;

import com.kevin.springbatch.springbatch.entity.User;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.Order;
import org.springframework.batch.item.database.support.MySqlPagingQueryProvider;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.validation.BindException;

import javax.sql.DataSource;
import java.util.HashMap;

@Configuration
@EnableBatchProcessing
public class ItemReadFileDemo {
    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Autowired
    @Qualifier("stringWriter")
    private  ItemWriter<? super User> stringWriter;

    @Bean
    public Job jobReadFileDemo() {
        return jobBuilderFactory.get("readFileDemo1")
                .start(itemReadFilestep1())
                .build();
    }

    @Bean
    public Step itemReadFilestep1() {
        return stepBuilderFactory.get("readFileDemo1step1")
                .<User,User>chunk(100)
                .reader(fileReader())
                .writer(stringWriter)
                .build();
    }

    @Bean
    @StepScope  //限制范围
    public FlatFileItemReader<User> fileReader () {
        FlatFileItemReader<User> reader = new FlatFileItemReader<>();
        reader.setResource(new ClassPathResource("files/kevin_user.csv"));
        reader.setLinesToSkip(1); //跳过第一行，比如第一行是表头
        //解析数据
        DelimitedLineTokenizer tokenizer = new DelimitedLineTokenizer();
        tokenizer.setNames(new String[]{"uid","username","password"});
        //转换成User
        DefaultLineMapper<User> mapper = new DefaultLineMapper<>();
        mapper.setLineTokenizer(tokenizer);
        mapper.setFieldSetMapper(new FieldSetMapper<User>() {
            @Override
            public User mapFieldSet(FieldSet fieldSet) throws BindException {
                return new User(fieldSet.readInt("uid"),
                        fieldSet.readString("username"),
                        fieldSet.readString("password"));
            }
        });

        mapper.afterPropertiesSet(); //没搞懂这个干啥的

        reader.setLineMapper(mapper);
        return  reader;
    }
}
