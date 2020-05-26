/*
 * Copyright 2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.kevin.springbatch.springbatch.dao;

import java.util.List;

import javax.sql.DataSource;


import com.kevin.springbatch.springbatch.entity.Transaction;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * @author Michael Minella
 */
public class TransactionDaoSupport extends JdbcTemplate implements TransactionDao {

	public TransactionDaoSupport(DataSource dataSource) {
		super(dataSource);
	}

	@SuppressWarnings("unchecked")
	public List<Transaction> getTransactionsByAccountNumber(String accountNumber) {
		//把每条交易记录对应的账户id,时间戳，账户金额，通过账户表查出来
		return query(
				"select t.id, t.timestamp, t.amount " +
						"from spring_batch.transaction t inner join spring_batch.account_summary a on " +
						"a.id = t.account_summary_id " +
						"where a.account_number = ?",
				new Object[] { accountNumber },
				(rs, rowNum) -> {
					Transaction trans = new Transaction();
					trans.setAmount(rs.getDouble("amount"));
					trans.setTimestamp(rs.getDate("timestamp"));
					return trans;
				}
		);
	}
}
