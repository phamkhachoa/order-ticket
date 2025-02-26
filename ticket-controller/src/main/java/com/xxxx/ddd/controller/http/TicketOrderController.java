package com.xxxx.ddd.controller.http;

import com.xxxx.ddd.application.model.TicketDetailDTO;
import com.xxxx.ddd.application.service.order.TicketOrderAppService;
import com.xxxx.ddd.application.service.ticket.TicketDetailAppService;
import com.xxxx.ddd.controller.model.enums.ResultUtil;
import com.xxxx.ddd.controller.model.vo.ResultMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Random;

@RestController
@RequestMapping("/ticket")
@Slf4j
public class TicketOrderController {

    // CALL Service Application
    @Autowired
    private TicketOrderAppService ticketOrderAppService;

    @GetMapping("/{ticketId}/{quantity}/order")
    public boolean orderTicketByLevel(@PathVariable("quantity") int quantity, @PathVariable("ticketId") Long ticketId) {
        return ticketOrderAppService.decreaseStockLevel1(ticketId, quantity);
    }

    @GetMapping("/{ticketId}/{quantity}/order-with-cache")
    public boolean orderTicketByLevel1WithCache(@PathVariable("quantity") int quantity, @PathVariable("ticketId") Long ticketId) {
        return ticketOrderAppService.decreaseStockLevel1WithCache(ticketId, quantity);
    }

    @GetMapping("/{ticketId}/{quantity}/cas")
    public boolean orderTicketByLevel3(@PathVariable("quantity") int quantity, @PathVariable("ticketId") Long ticketId) {
        return ticketOrderAppService.decreaseStockLevel3CAS(ticketId, quantity);
    }

    @GetMapping("/{ticketId}/{quantity}/cas-with-cache")
    public boolean orderTicketByLevel3WithCache(@PathVariable("quantity") int quantity, @PathVariable("ticketId") Long ticketId) {
        return ticketOrderAppService.decreaseStockLevel3CASWithCache(ticketId, quantity);
    }
}
