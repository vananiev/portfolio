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

package ru.investbook.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import ru.investbook.entity.SecurityDescriptionEntity;



@Transactional(readOnly = true)
public interface SecurityDescriptionRepository extends JpaRepository<SecurityDescriptionEntity, String> {

    @Transactional
    default void createOrUpdateSector(String securityId, String sector) {
        findById(securityId)
                .ifPresentOrElse(
                        $ -> updateSector(securityId, sector),
                        () -> {
                            var entity = new SecurityDescriptionEntity();
                            entity.setSecurity(securityId);
                            entity.setSector(sector);
                            save(entity);
                        });
    }

    @Transactional
    @Modifying
    @Query("UPDATE SecurityDescriptionEntity SET sector = :sector WHERE security = :securityId")
    int updateSector(String securityId, String sector);
}
