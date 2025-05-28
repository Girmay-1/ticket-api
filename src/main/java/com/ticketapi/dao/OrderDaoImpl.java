package com.ticketapi.dao;

import com.ticketapi.model.Order;
import com.ticketapi.util.DatabaseQueries;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public class OrderDaoImpl implements OrderDao {

    private static final Logger logger = LoggerFactory.getLogger(OrderDaoImpl.class);
    private final JdbcTemplate jdbcTemplate;

    public OrderDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Order> orderRowMapper = (rs, rowNum) -> {
        Order order = new Order();
        order.setId(rs.getLong("id"));
        order.setUserId(rs.getLong("user_id"));
        order.setEventId(rs.getLong("event_id"));
        order.setTicketType(rs.getString("ticket_type"));
        order.setPrice(rs.getBigDecimal("price"));
        order.setQuantity(rs.getInt("quantity"));
        order.setStatus(rs.getString("status"));
        order.setPaymentIntentId(rs.getString("payment_intent_id"));
        
        Timestamp createdAt = rs.getTimestamp("created_at");
        Timestamp updatedAt = rs.getTimestamp("updated_at");
        
        if (createdAt != null) {
            order.setCreatedAt(createdAt.toLocalDateTime());
        }
        if (updatedAt != null) {
            order.setUpdatedAt(updatedAt.toLocalDateTime());
        }
        
        return order;
    };

    @Override
    public Order createOrder(Order order) {
        logger.info("Creating order for user: {}, event: {}", order.getUserId(), order.getEventId());
        
        KeyHolder keyHolder = new GeneratedKeyHolder();
        
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(
                DatabaseQueries.CREATE_ORDER.getQuery(),
                Statement.RETURN_GENERATED_KEYS
            );
            ps.setLong(1, order.getUserId());
            ps.setLong(2, order.getEventId());
            ps.setString(3, order.getTicketType());
            ps.setBigDecimal(4, order.getPrice());
            ps.setInt(5, order.getQuantity());
            ps.setString(6, order.getStatus());
            ps.setTimestamp(7, Timestamp.valueOf(order.getCreatedAt()));
            ps.setTimestamp(8, Timestamp.valueOf(order.getUpdatedAt()));
            return ps;
        }, keyHolder);

        // Extract the ID specifically from the generated keys
        Number key = null;
        if (keyHolder.getKeys() != null && !keyHolder.getKeys().isEmpty()) {
            Object idValue = keyHolder.getKeys().get("id");
            if (idValue == null) {
                // Try uppercase (some databases return uppercase column names)
                idValue = keyHolder.getKeys().get("ID");
            }
            if (idValue instanceof Number) {
                key = (Number) idValue;
            }
        }
        
        if (key != null) {
            order.setId(key.longValue());
            logger.info("Order created successfully with ID: {}", order.getId());
        } else {
            logger.error("Failed to create order - no ID key returned. Available keys: {}", 
                        keyHolder.getKeys());
            throw new RuntimeException("Failed to create order - could not retrieve generated ID");
        }
        
        return order;
    }

    @Override
    public Order getOrderById(Long id) {
        logger.debug("Getting order by ID: {}", id);
        
        try {
            return jdbcTemplate.queryForObject(
                DatabaseQueries.GET_ORDER_BY_ID.getQuery(),
                orderRowMapper,
                id
            );
        } catch (EmptyResultDataAccessException e) {
            logger.warn("Order not found with ID: {}", id);
            return null;
        }
    }

    @Override
    public List<Order> getOrdersByUserId(Long userId) {
        logger.debug("Getting orders for user ID: {}", userId);
        
        return jdbcTemplate.query(
            DatabaseQueries.GET_ORDERS_BY_USER_ID.getQuery(),
            orderRowMapper,
            userId
        );
    }

    @Override
    public void updateOrderWithPaymentIntent(Long orderId, String paymentIntentId) {
        logger.info("Updating order {} with payment intent ID: {}", orderId, paymentIntentId);
        
        int updated = jdbcTemplate.update(
            DatabaseQueries.UPDATE_ORDER_PAYMENT_INTENT.getQuery(),
            paymentIntentId,
            Timestamp.valueOf(LocalDateTime.now()),
            orderId
        );
        
        if (updated == 0) {
            logger.warn("No order found to update with ID: {}", orderId);
            throw new RuntimeException("Order not found: " + orderId);
        }
        
        logger.info("Order {} updated with payment intent successfully", orderId);
    }

    @Override
    public void updateOrderStatus(Long orderId, String status) {
        logger.info("Updating order {} status to: {}", orderId, status);
        
        int updated = jdbcTemplate.update(
            DatabaseQueries.UPDATE_ORDER_STATUS.getQuery(),
            status,
            Timestamp.valueOf(LocalDateTime.now()),
            orderId
        );
        
        if (updated == 0) {
            logger.warn("No order found to update with ID: {}", orderId);
            throw new RuntimeException("Order not found: " + orderId);
        }
        
        logger.info("Order {} status updated to {} successfully", orderId, status);
    }

    @Override
    public Order getOrderByPaymentIntent(String paymentIntentId) {
        logger.debug("Getting order by payment intent ID: {}", paymentIntentId);
        
        try {
            return jdbcTemplate.queryForObject(
                DatabaseQueries.GET_ORDER_BY_PAYMENT_INTENT.getQuery(),
                orderRowMapper,
                paymentIntentId
            );
        } catch (EmptyResultDataAccessException e) {
            logger.warn("Order not found with payment intent ID: {}", paymentIntentId);
            return null;
        }
    }

    @Override
    public void deleteOrder(Long id) {
        logger.info("Deleting order with ID: {}", id);
        
        int deleted = jdbcTemplate.update(
            DatabaseQueries.DELETE_ORDER.getQuery(),
            id
        );
        
        if (deleted == 0) {
            logger.warn("No order found to delete with ID: {}", id);
            throw new RuntimeException("Order not found: " + id);
        }
        
        logger.info("Order {} deleted successfully", id);
    }
}
