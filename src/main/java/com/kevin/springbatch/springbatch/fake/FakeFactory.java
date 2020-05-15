package com.kevin.springbatch.springbatch.fake;

import com.github.javafaker.Faker;
import com.kevin.springbatch.springbatch.jpawithmysql.entity.User;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FakeFactory {
    /**
     * faker 指定汉语，默认英语
     */
    private static Faker faker = new Faker(Locale.ENGLISH); // 或者china 换成汉语


    public static List<User> fakeUser(final int num) {
        return Stream.generate(() -> new User(faker.name().fullName(), String.valueOf(faker.number().randomNumber(10,true))))
                .limit(num)
                .collect(Collectors.toList());
    }
}
