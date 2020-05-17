package com.kevin.springbatch.springbatch.study;

import com.kevin.springbatch.springbatch.study.childJobs.ChildJob1;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.JobStepBuilder;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;


/**
 * 父job由子job构成
 */
@Configuration
@EnableBatchProcessing
public class ParentJob {
    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Autowired
    private ChildJob1 childJob1;

    @Autowired
    private Job jobJobOne;

    @Autowired
    private Job jobJobTwo;

    @Autowired
    private JobLauncher launcher;

    //启动父job
    @Bean
    public Job fatcherJob(JobRepository repository, PlatformTransactionManager platformTransactionManager) {
        return jobBuilderFactory.get("ParentJobName") //父job名字
                .start(childJob1(repository,platformTransactionManager))
                .next(childJob2(repository,platformTransactionManager))
                .build();
    }

    //    注意，这个返回的Job类型的Step,因为第一个感觉应该是返回Job才对，所以这里解释一下
    private Step childJob1(JobRepository repository, PlatformTransactionManager platformTransactionManager) {
        return new JobStepBuilder(new StepBuilder("childJob1"))
                .job(jobJobOne)
                .launcher(launcher)
                .repository(repository)
                .transactionManager(platformTransactionManager)
                .build();
    }

    private Step childJob2(JobRepository repository, PlatformTransactionManager platformTransactionManager) {
        return new JobStepBuilder(new StepBuilder("childJob2"))
                .job(jobJobTwo)
                .launcher(launcher)
                .repository(repository)
                .transactionManager(platformTransactionManager)
                .build();
    }


}
