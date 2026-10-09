package com.example.assetserviceticket.service.impl;

import com.example.assetserviceticket.dto.response.ReportSummaryResponse;
import com.example.assetserviceticket.enums.AssetStatus;
import com.example.assetserviceticket.enums.TicketPriority;
import com.example.assetserviceticket.enums.TicketStatus;
import com.example.assetserviceticket.enums.UserRole;
import com.example.assetserviceticket.repository.AssetRepository;
import com.example.assetserviceticket.repository.ServiceTicketRepository;
import com.example.assetserviceticket.repository.UserRepository;
import com.example.assetserviceticket.entity.Asset;
import com.example.assetserviceticket.entity.ServiceTicket;
import com.example.assetserviceticket.entity.User;
import com.example.assetserviceticket.exception.ResourceNotFoundException;
import com.example.assetserviceticket.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReportServiceImpl implements ReportService {

    private final AssetRepository assetRepository;
    private final ServiceTicketRepository ticketRepository;
    private final UserRepository userRepository;

    @Override
    public ReportSummaryResponse getSummary(String userEmail) {
        User currentUser = userRepository.findByEmailIgnoreCase(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Oturum kullanıcısı bulunamadı."));
        var assets = currentUser.getRole() == UserRole.EMPLOYEE
                ? assetRepository.findByAssignedUserId(currentUser.getId())
                : assetRepository.findAll();
        var tickets = switch (currentUser.getRole()) {
            case ADMIN -> ticketRepository.findAll();
            case TECHNICIAN -> ticketRepository.findByAssignedTechnicianIdOrderByCreatedAtDesc(currentUser.getId());
            case EMPLOYEE -> ticketRepository.findByCreatedByIdOrderByCreatedAtDesc(currentUser.getId());
        };
        return new ReportSummaryResponse(
                assets.size(),
                countAssetsByStatus(assets, AssetStatus.ACTIVE),
                countAssetsByStatus(assets, AssetStatus.IN_SERVICE),
                tickets.size(),
                countTicketsByStatus(tickets, TicketStatus.OPEN),
                countTicketsByStatus(tickets, TicketStatus.IN_PROGRESS),
                countTicketsByStatus(tickets, TicketStatus.CLOSED),
                tickets.stream().filter(ticket -> ticket.getPriority() == TicketPriority.CRITICAL).count()
        );
    }

    private long countAssetsByStatus(java.util.List<Asset> assets, AssetStatus status) {
        return assets.stream().filter(asset -> asset.getStatus() == status).count();
    }

    private long countTicketsByStatus(java.util.List<ServiceTicket> tickets, TicketStatus status) {
        return tickets.stream().filter(ticket -> ticket.getStatus() == status).count();
    }
}
