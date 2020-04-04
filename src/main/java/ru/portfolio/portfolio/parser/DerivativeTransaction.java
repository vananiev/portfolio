/*
 * Portfolio
 * Copyright (C) 2020  Vitalii Ananev <an-vitek@ya.ru>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package ru.portfolio.portfolio.parser;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import ru.portfolio.portfolio.pojo.CashFlowType;
import ru.portfolio.portfolio.pojo.Transaction;
import ru.portfolio.portfolio.pojo.TransactionCashFlow;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
@EqualsAndHashCode
public class DerivativeTransaction {
    public static final String QUOTE_CURRENCY = "PNT"; // point

    private long transactionId;
    private String portfolio;
    private String contract;
    private Instant timestamp;
    private int count;
    private BigDecimal value; // оценочная стоиомсть в валюце цены
    private BigDecimal commission;
    private String valueCurrency; // валюта платежа
    private String commissionCurrency; // валюта коммиссии

    public Transaction getTransaction() {
        return Transaction.builder()
                .id(transactionId)
                .portfolio(portfolio)
                .isin(contract)
                .timestamp(timestamp)
                .count(count)
                .build();
    }

    public List<TransactionCashFlow> getTransactionCashFlows() {
        List<TransactionCashFlow> list = new ArrayList<>(2);
        if (!value.equals(BigDecimal.ZERO)) {
            list.add(TransactionCashFlow.builder()
                    .transactionId(transactionId)
                    .eventType(valueCurrency.equals(QUOTE_CURRENCY) ?
                            CashFlowType.DERIVATIVE_QUOTE :
                            CashFlowType.DERIVATIVE_PRICE)
                    .value(value)
                    .currency(valueCurrency)
                    .build());
        }
        if (!commission.equals(BigDecimal.ZERO)) {
            list.add(TransactionCashFlow.builder()
                    .transactionId(transactionId)
                    .eventType(CashFlowType.COMMISSION)
                    .value(commission)
                    .currency(commissionCurrency)
                    .build());
        }
        return list;
    }
}