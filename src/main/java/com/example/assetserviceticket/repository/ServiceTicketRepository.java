package com.example.assetserviceticket.repository;

import com.example.assetserviceticket.entity.ServiceTicket;
import com.example.assetserviceticket.enums.TicketPriority;
import com.example.assetserviceticket.enums.TicketStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ServiceTicketRepository extends JpaRepository<ServiceTicket, Long> {
    Optional<ServiceTicket> findTopByOrderByIdDesc();

    Optional<ServiceTicket> findByTicketNumber(String ticketNumber);

    List<ServiceTicket> findByCreatedByIdOrderByCreatedAtDesc(Long userId);

    List<ServiceTicket> findByAssignedTechnicianIdOrderByCreatedAtDesc(Long userId);

    long countByStatus(TicketStatus status);

    long countByPriority(TicketPriority priority);

    boolean existsByAssetId(Long assetId);
}
