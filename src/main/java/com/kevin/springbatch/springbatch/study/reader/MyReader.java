package com.kevin.springbatch.springbatch.study.reader;

import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;

import java.util.Iterator;
import java.util.List;

public class MyReader implements ItemReader<String> {

    private final Iterator<String> iterator;

    public MyReader(List<String> data) {
        this.iterator = data.iterator();
    }

    @Override
    public String read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
        //一个一个数据读取
        if (iterator.hasNext()) {
            return iterator.next();
        } else {
            return null;
        }

    }
}
