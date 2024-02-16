package com.tushar.microservices.order.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tushar.microservices.order.entity.Order;

@Repository
public interface OrderRepo extends JpaRepository<Order, Long> {


}
