package com.example.assetserviceticket.repository;

import com.example.assetserviceticket.entity.ServiceTicket;
import com.example.assetserviceticket.enums.TicketPriority;
import com.example.assetserviceticket.enums.TicketStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ServiceTicketRepository extends JpaRepository<ServiceTicket, Long> {
    Optional<ServiceTicket> findTopByOrderByIdDesc();

    long countByStatus(TicketStatus status);

    long countByPriority(TicketPriority priority);

    boolean existsByAssetId(Long assetId);
}
