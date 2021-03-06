/*
 * InvestBook
 * Copyright (C) 2020  Vitalii Ananev <spacious-team@ya.ru>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package ru.investbook.converter;

import lombok.RequiredArgsConstructor;
import org.spacious_team.broker.pojo.Transaction;
import org.springframework.stereotype.Component;
import ru.investbook.entity.SecurityEntity;
import ru.investbook.entity.TransactionEntity;
import ru.investbook.entity.TransactionEntityPK;
import ru.investbook.repository.PortfolioRepository;
import ru.investbook.repository.SecurityRepository;

@Component
@RequiredArgsConstructor
public class TransactionConverter implements EntityConverter<TransactionEntity, Transaction> {
    private final PortfolioRepository portfolioRepository;
    private final SecurityRepository securityRepository;

    @Override
    public TransactionEntity toEntity(Transaction transaction) {
        portfolioRepository.findById(transaction.getPortfolio())
                .orElseThrow(() -> new IllegalArgumentException("В справочнике не найден брокерский счет: " + transaction.getPortfolio()));
        SecurityEntity securityEntity = securityRepository.findById(transaction.getSecurity())
                .orElseThrow(() -> new IllegalArgumentException("Ценная бумага с заданным ISIN не найдена: " + transaction.getSecurity()));

        TransactionEntityPK pk = new TransactionEntityPK();
        pk.setId(transaction.getId());
        pk.setPortfolio(transaction.getPortfolio());
        TransactionEntity entity = new TransactionEntity();
        entity.setPk(pk);
        entity.setSecurity(securityEntity);
        entity.setTimestamp(transaction.getTimestamp());
        entity.setCount(transaction.getCount());
        return entity;
    }

    @Override
    public Transaction fromEntity(TransactionEntity entity) {
        return Transaction.builder()
                .id(entity.getPk().getId())
                .portfolio(entity.getPk().getPortfolio())
                .security(entity.getSecurity().getId())
                .timestamp(entity.getTimestamp())
                .count(entity.getCount())
                .build();
    }
}
