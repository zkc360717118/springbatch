package com.kevin.springbatch.springbatch.study;

import com.kevin.springbatch.springbatch.dao.TransactionDao;
import com.kevin.springbatch.springbatch.dao.TransactionDaoSupport;
import com.kevin.springbatch.springbatch.entity.AccountSummary;
import com.kevin.springbatch.springbatch.entity.Transaction;
import com.kevin.springbatch.springbatch.entity.User;
import com.kevin.springbatch.springbatch.study.processor.TransactionApplierProcessor;
import com.kevin.springbatch.springbatch.study.reader.TransactionReader;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.batch.item.file.mapping.PassThroughFieldSetMapper;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import javax.sql.DataSource;

/**
 * job 的停止有三种
 * 1.自然停止，也就是成功了，ExitStatus.COMPLETED
 * 2，编码停止，
 *  2.1 ExitStatus.STOPPED
 *  2.2 stepExecution.setTerminateOnly()
 * 3. 其他运行中的错误停止
 *
 * 该类会演示2.3 中的三种停止，和处理方式
 */
@EnableBatchProcessing
@Configuration
public class StopJobCasesDemo {
    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Autowired
    @Qualifier("tesAccountSummarytWriter")
    private ItemWriter<? super AccountSummary> testWriter;

    @Bean
    @StepScope
    public TransactionReader transactionReader() {
        return new TransactionReader(fileItemReader(null));
    }

    @Bean
    @StepScope
    public FlatFileItemReader<FieldSet> fileItemReader(
            @Value("#{jobParameters['transactionFile']}") Resource inputFile) {
        return new FlatFileItemReaderBuilder<FieldSet>()
                .name("fileItemReader")
                .resource(inputFile)
                .lineTokenizer(new DelimitedLineTokenizer())
                .fieldSetMapper(new PassThroughFieldSetMapper())
                .build();
    }

    @Bean
    public JdbcBatchItemWriter<Transaction> transactionWriter(DataSource dataSource) {
        return new JdbcBatchItemWriterBuilder<Transaction>()
                .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
                .sql("INSERT INTO spring_batch.transaction " +
                        "(account_summary_id, timestamp, amount) " +
                        "VALUES ((SELECT ID FROM spring_batch.ACCOUNT_SUMMARY " +
                        "	WHERE ACCOUNT_NUMBER = :accountNumber), " +
                        ":timestamp, :amount)")
                .dataSource(dataSource).build();
    }

    @Bean
    public Step importTransactionFileStep() {
        System.out.println("把交易记录从文件中，放到交易表transaction中");
        return this.stepBuilderFactory.get("importTransactionFileStep").<Transaction, Transaction>chunk(10)
                .reader(transactionReader()) //从文件中读取数据
                .writer(transactionWriter(null)) //把数据放入交易表
                .allowStartIfComplete(true)
                .listener(transactionReader())
                .build();
    }

    @Bean
    @StepScope
    public JdbcCursorItemReader<AccountSummary> accountSummaryReader(DataSource dataSource) {
        return new JdbcCursorItemReaderBuilder<AccountSummary>()
                .name("accountSummaryReader")
                .dataSource(dataSource)
                .sql("SELECT ACCOUNT_NUMBER, CURRENT_BALANCE " +
                        "FROM spring_batch.ACCOUNT_SUMMARY A " +
                        "WHERE A.ID IN (" +
                        "	SELECT DISTINCT T.account_summary_id " +
                        "	FROM spring_batch.transaction T) " +
                        "ORDER BY A.ACCOUNT_NUMBER")
                .rowMapper((resultSet, rowNumber) -> {
                    AccountSummary summary = new AccountSummary();

                    summary.setAccountNumber(resultSet.getString("account_number"));
                    summary.setCurrentBalance(resultSet.getDouble("current_balance"));

                    return summary;
                }).build();
    }

    @Bean
    public TransactionDao transactionDao(DataSource dataSource) {
        return new TransactionDaoSupport(dataSource);
    }
    @Bean
    public TransactionApplierProcessor transactionApplierProcessor() {
        return new TransactionApplierProcessor(transactionDao(null));
    }

    @Bean
    public JdbcBatchItemWriter<AccountSummary> accountSummaryWriter(DataSource dataSource) {
        return new JdbcBatchItemWriterBuilder<AccountSummary>().dataSource(dataSource)
                .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
                .sql("UPDATE spring_batch.ACCOUNT_SUMMARY " +
                        "SET CURRENT_BALANCE = :currentBalance " +
                        "WHERE ACCOUNT_NUMBER = :accountNumber")
                .build();
    }

    @Bean
    public Step applyTransactionsStep() {
        return this.stepBuilderFactory.get("applyTransactionsStep")
                .<AccountSummary, AccountSummary>chunk(100)
                .reader(accountSummaryReader(null))
                .processor(transactionApplierProcessor())
                .writer(accountSummaryWriter(null))
                .build();
    }

    @Bean
    @StepScope //step scoped, so we get a new instance for each step
    public FlatFileItemWriter<AccountSummary> accountSummaryFileWriter(
            @Value("#{jobParameters['summaryFile']}") Resource summaryFile) {

        DelimitedLineAggregator<AccountSummary> lineAggregator = new DelimitedLineAggregator<>();
        BeanWrapperFieldExtractor<AccountSummary> fieldExtractor = new BeanWrapperFieldExtractor<>();
        fieldExtractor.setNames(new String[]{"accountNumber", "currentBalance"});
        fieldExtractor.afterPropertiesSet();
        lineAggregator.setFieldExtractor(fieldExtractor);

        return new FlatFileItemWriterBuilder<AccountSummary>()
                .name("accountSummaryFileWriter")
                .resource(summaryFile)
                .lineAggregator(lineAggregator)
                .build();
    }

    @Bean
    public Step generateAccountSummaryStep() {
        return this.stepBuilderFactory.get("generateAccountSummaryStep")
                .<AccountSummary, AccountSummary>chunk(100)
                .reader(accountSummaryReader(null))
                .writer(accountSummaryFileWriter(null))
//                .writer(testWriter)
                .build();
    }

    @Bean
    public Job transactionJob() {
//        return this.jobBuilderFactory.get("transactionJob22")
//                .start(importTransactionFileStep())
//                .on("STOPPED").stopAndRestart(importTransactionFileStep()) //如果上一步stopped的状态，重启以后接着跑
//                .from(importTransactionFileStep()).on("*").to(applyTransactionsStep())// 跑第二个step
//                .from(applyTransactionsStep()).next(generateAccountSummaryStep())// 跑第三step
//                .end()
//                .build();

//        因为使用了importTransactionFileStep()中reader里面的stepExecution来手动处理错误，使得我们job的组装变得更加简单了
        return this.jobBuilderFactory.get("transactionJob31")
//				.preventRestart()
                .start(importTransactionFileStep())
                .next(applyTransactionsStep())
                .next(generateAccountSummaryStep())
                .build();


    }
}
