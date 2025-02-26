package com.xxxx.ddd.domain.respository;

import com.xxxx.ddd.domain.model.entity.TicketDetail;

import java.util.Optional;

public interface TicketOrderRepository {

    boolean decreaseStockLevel1(Long ticketId, int quantity);
    boolean decreaseStockLevel2(Long ticketId, int quantity);

    boolean decreaseStockLevel3CAS(Long ticketId, int oldStockAvailable, int quantity);

    int getStockAvailable(Long ticketId);
}
