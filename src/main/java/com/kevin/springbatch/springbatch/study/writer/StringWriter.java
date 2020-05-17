package com.kevin.springbatch.springbatch.study.writer;

import com.kevin.springbatch.springbatch.entity.User;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import java.util.List;

@Component("stringWriter")
public class StringWriter implements ItemWriter<User> {
    @Override
    public void write(List<? extends User> items) throws Exception {
        for (User user : items) {
            System.out.println(user);
        }
    }
}
