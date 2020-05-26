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
package com.kevin.springbatch.springbatch.study.processor;

import java.util.List;


import com.kevin.springbatch.springbatch.dao.TransactionDao;
import com.kevin.springbatch.springbatch.entity.AccountSummary;
import com.kevin.springbatch.springbatch.entity.Transaction;
import org.springframework.batch.item.ItemProcessor;

/**
 * @author Michael Minella
 */
public class TransactionApplierProcessor implements
		ItemProcessor<AccountSummary, AccountSummary> {

	private TransactionDao transactionDao;

	public TransactionApplierProcessor(TransactionDao transactionDao) {
		this.transactionDao = transactionDao;
	}

	public AccountSummary process(AccountSummary summary) throws Exception {
		//先获取交易数据
		List<Transaction> transactions = transactionDao
				.getTransactionsByAccountNumber(summary.getAccountNumber());

		//更新账户表
		for (Transaction transaction : transactions) {
			summary.setCurrentBalance(summary.getCurrentBalance()
					+ transaction.getAmount());
		}
		return summary;
	}
}
