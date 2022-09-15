package com.example.demo.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.entitties.Category;

@Repository
public interface CategoryRepository extends JpaRepository <Category, Long> {
    
}
