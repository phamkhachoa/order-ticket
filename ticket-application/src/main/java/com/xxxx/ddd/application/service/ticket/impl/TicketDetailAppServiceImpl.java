package com.xxxx.ddd.application.service.ticket.impl;

import com.xxxx.ddd.application.mapper.TicketDetailMapper;
import com.xxxx.ddd.application.model.TicketDetailDTO;
import com.xxxx.ddd.application.model.cache.TicketDetailCache;
import com.xxxx.ddd.application.service.ticket.TicketDetailAppService;
import com.xxxx.ddd.application.service.ticket.cache.TicketDetailCacheService;
import com.xxxx.ddd.application.service.ticket.cache.TicketDetailCacheServiceRefactor;
import com.xxxx.ddd.domain.model.entity.TicketDetail;
import com.xxxx.ddd.domain.service.TicketDetailDomainService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@Slf4j
public class TicketDetailAppServiceImpl implements TicketDetailAppService {

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    // CALL Service Domain Module
    @Autowired
    private TicketDetailDomainService ticketDetailDomainService;

    // CALL CACHE
    @Autowired
    private TicketDetailCacheService ticketDetailCacheService;

    @Autowired
    private TicketDetailCacheServiceRefactor ticketDetailCacheServiceRefactor;

    private AtomicInteger counter = new AtomicInteger(0);

    // Lua script for atomic inventory check and decrement
    private static final String CHECK_AND_DECREASE_STOCK_LUA = """
        local stockKey = KEYS[1]
        local stock = redis.call('get', stockKey)
        if not stock or tonumber(stock) <= 0 then
            return 0
        end
        redis.call('decr', stockKey)
        return 1
        """;

    @Override
    public TicketDetailDTO getTicketDetailById(Long ticketId, Long version) {
        log.info("Implement Application : {}, {}: ", ticketId, version);
        TicketDetailCache ticketDetailCache = ticketDetailCacheServiceRefactor.getTicketDetail(ticketId, version);
        // mapper to DTO
        TicketDetailDTO ticketDetailDTO = TicketDetailMapper.mapperToTicketDetailDTO(ticketDetailCache.getTicketDetail());
        ticketDetailDTO.setVersion(ticketDetailCache.getVersion());
        initializeProduct(ticketId + "", ticketDetailDTO.getStockInitial(), System.currentTimeMillis(), 10000000);
        return ticketDetailDTO;
    }

    @Override
    public boolean orderTicketByUser(Long ticketId) {
        return ticketDetailCacheServiceRefactor.orderTicketByUser(ticketId);
    }

    @Override
    public boolean orderTicketByUserPro(Long ticketId, int userId) {
        return tryOrder(ticketId + "", userId + "");
    }

    // Initialize flash sale product inventory
    public void initializeProduct(String ticketId, long stockQuantity, long saleStartTime, long saleDuration) {
        String stockKey = "flash:stock:" + ticketId;
        String lockKey = "flash:lock:" + ticketId;

        // Set initial stock
        redisTemplate.opsForValue().set(stockKey, stockQuantity);

        // Set expiration for flash sale duration
        redisTemplate.expire(stockKey, saleDuration, TimeUnit.SECONDS);

        // Store sale start time
        redisTemplate.opsForValue().set("flash:start:" + ticketId, saleStartTime);
    }

    // Try to place an order
    public boolean tryOrder(String productId, String userId) {
        String stockKey = "flash:stock:" + productId;
        String orderKey = "flash:order:" + productId + ":" + userId;

        // Check if user has already purchased
//        Boolean hasOrdered = redisTemplate.hasKey(orderKey);
//        if (Boolean.TRUE.equals(hasOrdered)) {
//            throw new RuntimeException("User has already participated in this flash sale");
//        }

        // Execute Lua script for atomic inventory check and decrease
        DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();
        redisScript.setScriptText(CHECK_AND_DECREASE_STOCK_LUA);
        redisScript.setResultType(Long.class);

        Long result = redisTemplate.execute(redisScript, Arrays.asList(stockKey));

        if (result != null && result == 1) {
            // Record user's purchase
            redisTemplate.opsForValue().set(orderKey, true, 24, TimeUnit.HOURS);

            // Add to order queue for async processing
            log.info("Add order to queue {}", counter.incrementAndGet());
            return true;
        }

        log.info("Out of stock");


        return false;
    }

}
