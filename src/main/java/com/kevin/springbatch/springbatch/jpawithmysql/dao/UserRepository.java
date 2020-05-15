package com.kevin.springbatch.springbatch.jpawithmysql.dao;


import com.kevin.springbatch.springbatch.jpawithmysql.entity.User;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.data.repository.CrudRepository;

// 继承CrudRepository接口，<实体类, 主键类型>
// JPA根据实体类的类名去对应表名（可以使用@Entity的name属性？@Table进行修改）
public interface UserRepository extends CrudRepository<User,Integer> {
}

