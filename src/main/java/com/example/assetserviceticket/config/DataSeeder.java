package com.example.assetserviceticket.config;

import com.example.assetserviceticket.entity.Asset;
import com.example.assetserviceticket.entity.Department;
import com.example.assetserviceticket.entity.ServiceTicket;
import com.example.assetserviceticket.entity.User;
import com.example.assetserviceticket.enums.AssetStatus;
import com.example.assetserviceticket.enums.AssetType;
import com.example.assetserviceticket.enums.TicketPriority;
import com.example.assetserviceticket.enums.TicketStatus;
import com.example.assetserviceticket.enums.UserRole;
import com.example.assetserviceticket.repository.AssetRepository;
import com.example.assetserviceticket.repository.DepartmentRepository;
import com.example.assetserviceticket.repository.ServiceTicketRepository;
import com.example.assetserviceticket.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final DepartmentRepository departmentRepository;
    private final UserRepository userRepository;
    private final AssetRepository assetRepository;
    private final ServiceTicketRepository ticketRepository;

    @Override
    public void run(String... args) {
        if (departmentRepository.count() > 0 || userRepository.count() > 0 || assetRepository.count() > 0) {
            return;
        }

        Department it = createDepartment("IT", "Information technology department");
        Department accounting = createDepartment("Accounting", "Accounting department");
        Department production = createDepartment("Production", "Production department");

        User admin = createUser("Admin User", "admin@example.com", UserRole.ADMIN, it);
        User technician = createUser("Technician One", "technician@example.com", UserRole.TECHNICIAN, it);
        User employee = createUser("Employee One", "employee@example.com", UserRole.EMPLOYEE, accounting);

        Asset laptop = createAsset("AST-0001", "Dell Latitude Laptop", AssetType.LAPTOP, AssetStatus.ACTIVE,
                "Dell", "Latitude 5440", it, employee);
        Asset printer = createAsset("AST-0002", "HP LaserJet Printer", AssetType.PRINTER, AssetStatus.IN_SERVICE,
                "HP", "LaserJet Pro", accounting, null);
        Asset switchDevice = createAsset("AST-0003", "Cisco Network Switch", AssetType.NETWORK_DEVICE, AssetStatus.ACTIVE,
                "Cisco", "Catalyst 2960", production, null);

        createTicket("TCK-000001", "Laptop overheating", "Laptop becomes too hot after one hour of use.",
                TicketPriority.HIGH, laptop, employee, technician, TicketStatus.ASSIGNED);
        createTicket("TCK-000002", "Printer paper jam", "Accounting printer has repeated paper jam issues.",
                TicketPriority.MEDIUM, printer, admin, null, TicketStatus.OPEN);
        createTicket("TCK-000003", "Network switch connection issue", "Production network switch has unstable ports.",
                TicketPriority.CRITICAL, switchDevice, admin, technician, TicketStatus.IN_PROGRESS);
    }

    private Department createDepartment(String name, String description) {
        Department department = new Department();
        department.setName(name);
        department.setDescription(description);
        return departmentRepository.save(department);
    }

    private User createUser(String fullName, String email, UserRole role, Department department) {
        User user = new User();
        user.setFullName(fullName);
        user.setEmail(email);
        user.setRole(role);
        user.setDepartment(department);
        return userRepository.save(user);
    }

    private Asset createAsset(
            String assetTag,
            String name,
            AssetType type,
            AssetStatus status,
            String brand,
            String model,
            Department department,
            User assignedUser
    ) {
        Asset asset = new Asset();
        asset.setAssetTag(assetTag);
        asset.setName(name);
        asset.setType(type);
        asset.setStatus(status);
        asset.setBrand(brand);
        asset.setModel(model);
        asset.setDepartment(department);
        asset.setAssignedUser(assignedUser);
        return assetRepository.save(asset);
    }

    private void createTicket(
            String ticketNumber,
            String title,
            String description,
            TicketPriority priority,
            Asset asset,
            User createdBy,
            User assignedTechnician,
            TicketStatus status
    ) {
        ServiceTicket ticket = new ServiceTicket();
        ticket.setTicketNumber(ticketNumber);
        ticket.setTitle(title);
        ticket.setDescription(description);
        ticket.setPriority(priority);
        ticket.setAsset(asset);
        ticket.setCreatedBy(createdBy);
        ticket.setAssignedTechnician(assignedTechnician);
        ticket.setStatus(status);
        ticketRepository.save(ticket);
    }
}
