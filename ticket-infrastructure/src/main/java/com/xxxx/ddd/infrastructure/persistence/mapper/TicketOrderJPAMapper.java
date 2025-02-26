package com.xxxx.ddd.infrastructure.persistence.mapper;

import com.xxxx.ddd.domain.model.entity.TicketDetail;
import org.hibernate.annotations.Parameter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface TicketOrderJPAMapper extends JpaRepository<TicketDetail, Long> {

    @Query("select t.stockAvailable from TicketDetail t where t.id = :ticketId")
    int getStockAvailable(@Param("ticketId") Long ticketId);

    @Modifying
    @Transactional
    @Query("update TicketDetail t set t.updatedAt = CURRENT_TIMESTAMP, " +
            "t.stockAvailable = t.stockAvailable - :quantity " +
            "where t.id = :ticketId and t.stockAvailable >= :quantity ")
    int decreaseStockLevel1(@Param("ticketId")Long ticketId, @Param("quantity") int quantity);


    @Modifying
    @Transactional
    @Query("update TicketDetail t set t.updatedAt = CURRENT_TIMESTAMP, " +
            "t.stockAvailable = t.stockAvailable - :quantity " +
            "where t.id = :ticketId")
    int decreaseStockLevel0(@Param("ticketId")Long ticketId,@Param("quantity") int quantity);


    @Modifying
    @Transactional
    @Query("update TicketDetail t set t.updatedAt = CURRENT_TIMESTAMP, " +
            "t.stockAvailable = :oldStockAvailable - :quantity " +
            "where t.id = :ticketId and t.stockAvailable = :oldStockAvailable ")
    int decreaseStockLevel3CAS(@Param("ticketId")Long ticketId,@Param("oldStockAvailable") int oldStockAvailable,@Param("quantity") int quantity);
}
