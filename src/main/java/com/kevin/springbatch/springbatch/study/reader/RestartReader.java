package com.kevin.springbatch.springbatch.study.reader;

import com.kevin.springbatch.springbatch.entity.User;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.item.*;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindException;

import java.util.Iterator;
import java.util.List;

@Component("restartReader")
public class RestartReader implements ItemStreamReader<User> {
    private FlatFileItemReader<User> customerFlatFileItemReader = new FlatFileItemReader<>();
    private Long curLine = 0L; //当前行数
    private boolean restart = false;
    private ExecutionContext executionContext;

    private StepExecution stepExecution;

    public RestartReader() {
        customerFlatFileItemReader.setResource(new ClassPathResource("files/restart.txt"));
        //解析数据
        DelimitedLineTokenizer tokenizer = new DelimitedLineTokenizer();
        tokenizer.setNames(new String[]{"uid","username","password"});
        //转换成User
        DefaultLineMapper<User> mapper = new DefaultLineMapper<>();
        mapper.setLineTokenizer(tokenizer);
        mapper.setFieldSetMapper(fieldSet -> new User(fieldSet.readInt("uid"),
                fieldSet.readString("username"),
                fieldSet.readString("password")));

        mapper.afterPropertiesSet(); //没搞懂这个干啥的

        customerFlatFileItemReader.setLineMapper(mapper);
    }

    //chunk 里面每一条数据执行一次read()
    @Override
    public User read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
        User user;
        this.curLine++;

        if(restart){
            customerFlatFileItemReader.setLinesToSkip(this.curLine.intValue()-1);
            restart=false;
            System.out.println("start reading from line: "+ this.curLine);
        }

        customerFlatFileItemReader.open(this.executionContext);
        user = customerFlatFileItemReader.read();

        //制造异常，模拟生产上的异常
        if (user != null && user.getUsername().equals("错误的名字")) {
            throw new RuntimeException("something wrong 有错误");
        }
        return user;
    }

    //step执行之前运行的
    @Override
    public void open(ExecutionContext executionContext) throws ItemStreamException {
        this.executionContext = executionContext;
        if (executionContext.containsKey("curLine")) {
            this.curLine = executionContext.getLong("curLine");
            this.restart = true;
        } else {
            this.curLine = 0L;
            executionContext.put("curLine",this.curLine);
            System.out.println("start reading from line: "+ this.curLine+1);
        }
    }

    //每次执行完chunk指定条数的数据以后，执行下面的update
    @Override
    public void update(ExecutionContext executionContext) throws ItemStreamException {
        executionContext.put("curLine",this.curLine);  //每一个chunk执行完了以后，把最后一行行数，放入context当中
        System.out.println("currentLine:" + this.curLine);
    }

    //整个step执行完了 执行下面方法
    @Override
    public void close() throws ItemStreamException {

    }
}
