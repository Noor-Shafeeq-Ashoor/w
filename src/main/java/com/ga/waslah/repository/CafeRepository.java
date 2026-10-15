package com.ga.waslah.repository;

import com.ga.waslah.model.Cafe;
import com.ga.waslah.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CafeRepository extends JpaRepository<Cafe, Long> {
    List<Cafe> findByManager(User manager);
}