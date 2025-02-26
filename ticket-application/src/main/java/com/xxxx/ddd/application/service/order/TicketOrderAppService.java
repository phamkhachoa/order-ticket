package com.xxxx.ddd.application.service.order;

public interface TicketOrderAppService {
    boolean decreaseStockLevel1(Long ticketId, int quantity);

    boolean decreaseStockLevel1WithCache(Long ticketId, int quantity);

    boolean decreaseStockLevel2(Long ticketId, int quantity);

    boolean decreaseStockLevel3CAS(Long ticketId, int quantity);

    boolean decreaseStockLevel3CASWithCache(Long ticketId, int quantity);

    int getStockAvailable(Long ticketId);
}
