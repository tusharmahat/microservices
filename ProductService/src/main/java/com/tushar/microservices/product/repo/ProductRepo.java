package com.tushar.microservices.product.repo;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.tushar.microservices.product.entity.Product;

@Repository
public interface ProductRepo extends MongoRepository<Product, String> {

}
