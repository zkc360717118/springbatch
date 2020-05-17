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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.util.HashMap;

@Configuration
@EnableBatchProcessing
public class ItemReadMysqlDemo {
    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    //数据源
    @Autowired
    private DataSource dataSource;

    @Autowired
    @Qualifier("stringWriter")
    private  ItemWriter<? super User> stringWriter;

    @Bean
    public Job ItemReadMysqlMethod() {
        return jobBuilderFactory.get("ItemReadMysqlMethod1")
                .start(itemReadMysqlstep1())
                .build();

    }

    @Bean
    public Step itemReadMysqlstep1() {

        return stepBuilderFactory.get("itemReadMysqlstep1")
                .<User,User>chunk(2)
                .reader(mysqlReader())
                .writer(stringWriter)
                .build();
    }

    @Bean
    @StepScope  //限制范围
    public JdbcPagingItemReader<User> mysqlReader() {
        JdbcPagingItemReader<User> reader = new JdbcPagingItemReader<>();
        reader.setDataSource(dataSource);
        reader.setFetchSize(10);
        //把堆区的记录转换成User
        reader.setRowMapper((resultSet, i) -> {
            User user = new User();
            user.setUid(resultSet.getInt(1));
            user.setUsername(resultSet.getString(2));
            user.setPassword(resultSet.getString(3));
            return user;
        });

        //指定sql语句
        MySqlPagingQueryProvider provider = new MySqlPagingQueryProvider();
        //指定查询的字段
        provider.setSelectClause("uid,username,password");
        //指定表名字
        provider.setFromClause("from user");
        //指定哪个字段进行排序
        HashMap<String, Order> sort = new HashMap<>(1);
        sort.put("uid", Order.ASCENDING);
        provider.setSortKeys(sort);
        //把provider给reader
        reader.setQueryProvider(provider);
        return  reader;
    }
}
