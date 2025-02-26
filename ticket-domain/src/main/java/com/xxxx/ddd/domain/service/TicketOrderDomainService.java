package com.xxxx.ddd.domain.service;

import com.xxxx.ddd.domain.model.entity.TicketDetail;

public interface TicketOrderDomainService {
    boolean decreaseStockLevel1(Long ticketId, int quantity);
    boolean decreaseStockLevel2(Long ticketId, int quantity);

    boolean decreaseStockLevel3CAS(Long ticketId, int oldStockAvailable, int quantity);

    int getStockAvailable(Long ticketId);
}
