package com.kevin.springbatch.springbatch.batch.batchprocessing;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;

public class UserItermProcesser implements ItemProcessor<User,User> {
    private static final Logger log = LoggerFactory.getLogger(UserItermProcesser.class);
    @Override
    public User process(User user) throws Exception {
        final String name = user.getUserName().toUpperCase();
        final String pwd = user.getPassWord().concat("pwd");
        final User newUser = new User(name, pwd);
        log.info("Converting (" + user + ") into (" + newUser + ")");
        return newUser;
    }
}
