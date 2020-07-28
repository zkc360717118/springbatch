package com.kevin.springbatch.springbatch.study.writer;

import com.kevin.springbatch.springbatch.entity.User;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import java.util.List;

@Component("myWriter")
public class MyWriter implements ItemWriter<String> {
    @Override
    public void write(List<? extends String> items) throws Exception {
        System.out.println(items.size());
        for (String user : items) {
            System.out.println(user);
        }
    }
}
