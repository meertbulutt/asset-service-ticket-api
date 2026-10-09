package com.example.assetserviceticket.config;

import com.example.assetserviceticket.entity.Asset;
import com.example.assetserviceticket.entity.Department;
import com.example.assetserviceticket.entity.ServiceTicket;
import com.example.assetserviceticket.entity.TicketComment;
import com.example.assetserviceticket.entity.User;
import com.example.assetserviceticket.enums.AssetStatus;
import com.example.assetserviceticket.enums.AssetType;
import com.example.assetserviceticket.enums.TicketPriority;
import com.example.assetserviceticket.enums.TicketStatus;
import com.example.assetserviceticket.enums.UserRole;
import com.example.assetserviceticket.repository.AssetRepository;
import com.example.assetserviceticket.repository.DepartmentRepository;
import com.example.assetserviceticket.repository.ServiceTicketRepository;
import com.example.assetserviceticket.repository.TicketCommentRepository;
import com.example.assetserviceticket.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Set;

/** Uygulama açılırken eksik demo kayıtlarını tamamlar; mevcut kayıtları çoğaltmaz. */
@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private static final String DEMO_PASSWORD = "Demo123!";
    private static final Set<String> DEMO_EMAILS = Set.of(
            "admin@example.com", "technician@example.com", "technician2@example.com",
            "employee@example.com", "employee2@example.com", "employee3@example.com"
    );

    private final DepartmentRepository departmentRepository;
    private final UserRepository userRepository;
    private final AssetRepository assetRepository;
    private final ServiceTicketRepository ticketRepository;
    private final TicketCommentRepository commentRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        Department it = getOrCreateDepartment("Bilgi Teknolojileri", "Bilgisayar, ağ ve yazılım hizmetlerinden sorumlu birim.");
        Department accounting = getOrCreateDepartment("Muhasebe", "Finans ve muhasebe işlemlerinden sorumlu birim.");
        Department production = getOrCreateDepartment("Üretim", "Üretim süreçlerini yürüten birim.");
        Department humanResources = getOrCreateDepartment("İnsan Kaynakları", "Çalışan ve insan kaynakları işlemlerinden sorumlu birim.");

        User admin = getOrCreateUser("Sistem Yöneticisi", "admin@example.com", UserRole.ADMIN, it);
        User technicianOne = getOrCreateUser("Ayşe Yılmaz", "technician@example.com", UserRole.TECHNICIAN, it);
        User technicianTwo = getOrCreateUser("Mehmet Demir", "technician2@example.com", UserRole.TECHNICIAN, it);
        User employeeOne = getOrCreateUser("Zeynep Kaya", "employee@example.com", UserRole.EMPLOYEE, accounting);
        User employeeTwo = getOrCreateUser("Can Arslan", "employee2@example.com", UserRole.EMPLOYEE, production);
        User employeeThree = getOrCreateUser("Elif Şahin", "employee3@example.com", UserRole.EMPLOYEE, humanResources);
        migrateLegacyPasswords();

        Asset laptop = getOrCreateAsset("AST-0001", "Dell Latitude Dizüstü Bilgisayar", AssetType.LAPTOP,
                AssetStatus.ACTIVE, "Dell", "Latitude 5440", it, employeeOne);
        Asset printer = getOrCreateAsset("AST-0002", "HP LaserJet Yazıcı", AssetType.PRINTER,
                AssetStatus.IN_SERVICE, "HP", "LaserJet Pro", accounting, null);
        Asset switchDevice = getOrCreateAsset("AST-0003", "Cisco Ağ Anahtarı", AssetType.NETWORK_DEVICE,
                AssetStatus.ACTIVE, "Cisco", "Catalyst 2960", production, null);
        Asset desktop = getOrCreateAsset("AST-0004", "Lenovo ThinkPad Dizüstü Bilgisayar", AssetType.LAPTOP,
                AssetStatus.ACTIVE, "Lenovo", "ThinkPad E14", it, employeeTwo);
        Asset canonPrinter = getOrCreateAsset("AST-0005", "Canon i-SENSYS Yazıcı", AssetType.PRINTER,
                AssetStatus.IN_SERVICE, "Canon", "i-SENSYS MF445dw", accounting, null);
        Asset server = getOrCreateAsset("AST-0006", "HPE ProLiant Sunucu", AssetType.SERVER,
                AssetStatus.ACTIVE, "HPE", "ProLiant DL360", it, null);
        Asset retiredLaptop = getOrCreateAsset("AST-0007", "HP ProBook Dizüstü Bilgisayar", AssetType.LAPTOP,
                AssetStatus.RETIRED, "HP", "ProBook 450", humanResources, employeeThree);
        Asset accessPoint = getOrCreateAsset("AST-0008", "Aruba Kablosuz Erişim Noktası", AssetType.NETWORK_DEVICE,
                AssetStatus.ACTIVE, "Aruba", "Instant On AP22", production, null);

        ServiceTicket firstTicket = getOrCreateTicket("TCK-000001", "Dizüstü bilgisayar aşırı ısınıyor",
                "Dizüstü bilgisayar yaklaşık bir saatlik kullanımdan sonra aşırı ısınıyor.",
                TicketPriority.HIGH, laptop, employeeOne, technicianOne, TicketStatus.ASSIGNED);
        getOrCreateTicket("TCK-000002", "Yazıcıda kağıt sıkışması",
                "Muhasebe yazıcısında sık sık kağıt sıkışması yaşanıyor.",
                TicketPriority.MEDIUM, printer, admin, null, TicketStatus.OPEN);
        getOrCreateTicket("TCK-000003", "Ağ anahtarında bağlantı sorunu",
                "Üretim bölümündeki ağ anahtarının bağlantı noktaları kararsız çalışıyor.",
                TicketPriority.CRITICAL, switchDevice, admin, technicianOne, TicketStatus.IN_PROGRESS);
        ServiceTicket fourthTicket = getOrCreateTicket("TCK-000004", "Yeni bilgisayarda VPN bağlantısı kurulamıyor",
                "Kullanıcı kurum ağına uzaktan bağlanırken VPN hatası alıyor.",
                TicketPriority.HIGH, desktop, employeeTwo, technicianTwo, TicketStatus.ASSIGNED);
        getOrCreateTicket("TCK-000005", "Yazıcı çıktısında soluk alanlar var",
                "Çıktıların bazı bölümleri soluk görünüyor; toner ve baskı kalitesi kontrol edilmeli.",
                TicketPriority.MEDIUM, canonPrinter, employeeOne, null, TicketStatus.OPEN);
        getOrCreateTicket("TCK-000006", "Dosya sunucusunda depolama alanı uyarısı",
                "Sunucuda boş disk alanı azaldı. Kullanılmayan dosyalar incelenmeli.",
                TicketPriority.CRITICAL, server, admin, technicianOne, TicketStatus.IN_PROGRESS);
        getOrCreateTicket("TCK-000007", "Kablosuz ağ kapsama alanı yetersiz",
                "Üretim alanının bir bölümünde kablosuz bağlantı sık sık kesiliyor.",
                TicketPriority.LOW, accessPoint, employeeTwo, technicianTwo, TicketStatus.RESOLVED);
        getOrCreateTicket("TCK-000008", "Kullanım dışı cihazın envanter kaydı",
                "Kullanım dışı dizüstü bilgisayarın envanter durumu doğrulandı.",
                TicketPriority.LOW, retiredLaptop, employeeThree, technicianOne, TicketStatus.CLOSED);

        addExampleComment(firstTicket, technicianOne,
                "Cihazın hava kanalları temizlendi. Sıcaklık değerleri izleniyor.");
        addExampleComment(fourthTicket, employeeTwo,
                "Sorun bu sabah başladı. Ev ağından bağlanırken de aynı hatayı alıyorum.");
    }

    private Department getOrCreateDepartment(String name, String description) {
        return departmentRepository.findByNameIgnoreCase(name).orElseGet(() -> {
            Department department = new Department();
            department.setName(name);
            department.setDescription(description);
            return departmentRepository.save(department);
        });
    }

    private User getOrCreateUser(String name, String email, UserRole role, Department department) {
        return userRepository.findByEmailIgnoreCase(email).orElseGet(() -> {
            User user = new User();
            user.setFullName(name);
            user.setEmail(email);
            user.setRole(role);
            user.setDepartment(department);
            user.setPasswordHash(passwordEncoder.encode(DEMO_PASSWORD));
            return userRepository.save(user);
        });
    }

    private void migrateLegacyPasswords() {
        var usersToUpdate = userRepository.findAll().stream()
                .filter(user -> user.getPasswordHash() == null
                        || (isDemoEmail(user.getEmail())
                        && !passwordEncoder.matches(DEMO_PASSWORD, user.getPasswordHash())))
                .toList();
        usersToUpdate.forEach(user -> user.setPasswordHash(passwordEncoder.encode(DEMO_PASSWORD)));
        userRepository.saveAll(usersToUpdate);

        var inactiveDemoUsers = userRepository.findAll().stream()
                .filter(user -> isDemoEmail(user.getEmail()) && !user.isActive())
                .toList();
        inactiveDemoUsers.forEach(user -> user.setActive(true));
        userRepository.saveAll(inactiveDemoUsers);
    }

    private boolean isDemoEmail(String email) {
        return DEMO_EMAILS.stream().anyMatch(demoEmail -> demoEmail.equalsIgnoreCase(email));
    }

    private Asset getOrCreateAsset(
            String tag, String name, AssetType type, AssetStatus status,
            String brand, String model, Department department, User assignedUser
    ) {
        return assetRepository.findByAssetTagIgnoreCase(tag).orElseGet(() -> {
            Asset asset = new Asset();
            asset.setAssetTag(tag);
            asset.setName(name);
            asset.setType(type);
            asset.setStatus(status);
            asset.setBrand(brand);
            asset.setModel(model);
            asset.setDepartment(department);
            asset.setAssignedUser(assignedUser);
            return assetRepository.save(asset);
        });
    }

    private ServiceTicket getOrCreateTicket(
            String number, String title, String description, TicketPriority priority,
            Asset asset, User creator, User technician, TicketStatus status
    ) {
        return ticketRepository.findByTicketNumber(number).orElseGet(() -> {
            ServiceTicket ticket = new ServiceTicket();
            ticket.setTicketNumber(number);
            ticket.setTitle(title);
            ticket.setDescription(description);
            ticket.setPriority(priority);
            ticket.setAsset(asset);
            ticket.setCreatedBy(creator);
            ticket.setAssignedTechnician(technician);
            ticket.setStatus(status);
            if (status == TicketStatus.CLOSED) ticket.setClosedAt(LocalDateTime.now());
            return ticketRepository.save(ticket);
        });
    }

    private void addExampleComment(ServiceTicket ticket, User author, String content) {
        if (ticket.getId() == null || commentRepository.existsByTicketId(ticket.getId())) return;
        TicketComment comment = new TicketComment();
        comment.setTicket(ticket);
        comment.setUser(author);
        comment.setContent(content);
        commentRepository.save(comment);
    }
}
