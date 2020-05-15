package com.kevin.springbatch.springbatch.jpawithmysql.controller;

import com.kevin.springbatch.springbatch.fake.FakeFactory;
import com.kevin.springbatch.springbatch.jpawithmysql.dao.UserRepository;
import com.kevin.springbatch.springbatch.jpawithmysql.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequestMapping("user")
public class UserController {
    @Autowired
    private UserRepository baseRepository;

    @GetMapping("/add")
    @ResponseBody
    public User addUser(@RequestParam String name, @RequestParam String password) {
        User n = new User();
        n.setUsername(name);
        n.setPassword(password);

        baseRepository.save(n);
        return n;
    }

    @GetMapping("/all")
    @ResponseBody
    public Iterable<User> getall() {
        return baseRepository.findAll();
    }

    @GetMapping("/fakeData")
    @ResponseBody
    public void fakeData(Integer number){
        if (number <= 0) return;
        long start = System.currentTimeMillis();
        List<User> users = FakeFactory.fakeUser(number);
        baseRepository.saveAll(users); // 不用批量插入10000条用时 224595

        long end = System.currentTimeMillis();
        long l = end - start;
        System.out.println("共用时间:"+l);

    }
}
