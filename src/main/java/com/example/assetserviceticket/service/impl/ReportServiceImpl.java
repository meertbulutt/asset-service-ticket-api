package com.example.assetserviceticket.service.impl;

import com.example.assetserviceticket.dto.response.ReportSummaryResponse;
import com.example.assetserviceticket.enums.AssetStatus;
import com.example.assetserviceticket.enums.TicketPriority;
import com.example.assetserviceticket.enums.TicketStatus;
import com.example.assetserviceticket.repository.AssetRepository;
import com.example.assetserviceticket.repository.ServiceTicketRepository;
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

    @Override
    public ReportSummaryResponse getSummary() {
        return new ReportSummaryResponse(
                assetRepository.count(),
                assetRepository.countByStatus(AssetStatus.ACTIVE),
                assetRepository.countByStatus(AssetStatus.IN_SERVICE),
                ticketRepository.count(),
                ticketRepository.countByStatus(TicketStatus.OPEN),
                ticketRepository.countByStatus(TicketStatus.IN_PROGRESS),
                ticketRepository.countByStatus(TicketStatus.CLOSED),
                ticketRepository.countByPriority(TicketPriority.CRITICAL)
        );
    }
}
