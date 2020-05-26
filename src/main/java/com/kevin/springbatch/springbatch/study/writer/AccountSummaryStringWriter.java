package com.kevin.springbatch.springbatch.study.writer;

import com.kevin.springbatch.springbatch.entity.AccountSummary;
import com.kevin.springbatch.springbatch.entity.User;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import java.util.List;

@Component("tesAccountSummarytWriter")
public class AccountSummaryStringWriter implements ItemWriter<AccountSummary> {
    @Override
    public void write(List<? extends AccountSummary> items) throws Exception {
        for (AccountSummary user : items) {
            System.out.println(user);
        }
    }
}
