package com.prod.backend.repo;

import com.prod.backend.entity.TicketEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TicketRepository extends JpaRepository<TicketEntity, Integer> {

    List<TicketEntity> findByUser_Id(long userId);
}
