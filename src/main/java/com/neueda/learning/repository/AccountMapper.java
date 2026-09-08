package com.neueda.learning.repository;

import com.neueda.learning.entity.Account;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface AccountMapper {

    @Insert("""
        INSERT INTO accounts(name, balance)
        VALUES(#{name}, #{balance})
        """)
    @Options(
            useGeneratedKeys = true,
            keyProperty = "id"
    )
    int save(Account account);


    @Select("""
        SELECT id, name, balance
        FROM accounts
        """)
    List<Account> findAll();


    @Select("""
        SELECT id, name, balance
        FROM accounts
        WHERE id = #{id}
        """)
    Account findById(int id);


    @Update("""
        UPDATE accounts
        SET name = #{name},
            balance = #{balance}
        WHERE id = #{id}
        """)
    int update(int id,Account account);


    @Delete("""
        DELETE FROM accounts
        WHERE id = #{id}
        """)
    int deleteById(int id);
}