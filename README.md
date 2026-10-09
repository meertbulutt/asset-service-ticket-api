# VarlıkMasası — Demirbaş ve Teknik Destek Yönetimi

VarlıkMasası, kurum içindeki bilgisayar, yazıcı ve ağ cihazlarını takip etmek; bu cihazlarla ilgili teknik destek taleplerini açmak, teknisyenlere atamak ve durumlarını izlemek için hazırlanmış bir staj bitirme projesidir.

Proje iki kullanım yüzü sunar:

- **Kullanıcı paneli:** Demirbaş, destek talebi, kullanıcı ve departman kayıtlarını Türkçe bir web ekranında görüntüler ve temel işlemleri yapar.
- **Swagger:** REST API uç noktalarını, istek/yanıt biçimlerini ve şemaları teknik olarak gösterir; istekleri elle denemeye yarar.

Panel ve Swagger aynı Spring Boot uygulamasına, aynı API'ye ve aynı PostgreSQL veritabanına bağlanır.

## Özellikler

- Demirbaşları listeleme, ad/etiket arama, tür ve duruma göre filtreleme, yeni demirbaş ekleme
- Yönetici ekranından demirbaşları kullanıcılara zimmetleme
- Destek taleplerini listeleme, filtreleme ve yeni talep açma
- Aktif teknisyen atama, talep durumunu güncelleme ve yorumları görüntüleme/ekleme
- Kullanıcı ve departman dizinlerini görüntüleme; kullanıcı ekleme ve hesabı devre dışı bırakma
- E-posta/parola ile oturum açma, oturumu kapatma ve role göre API işlemlerini sınırlandırma
- Toplam demirbaş, aktif demirbaş, açık talep ve işlemdeki talep özetleri
- Türkçe Swagger açıklamaları, doğrulama mesajları ve API hata yanıtları
- Uygulama ilk açılırken eksik demo kayıtlarını tamamlama; mevcut kayıtları çoğaltmama

## Teknolojiler

- Java 21
- Spring Boot 3.3.7, Spring Web ve Spring Data JPA
- PostgreSQL
- Bean Validation
- Spring Security, BCrypt parola özeti ve oturum tabanlı kimlik doğrulama
- Springdoc OpenAPI / Swagger UI
- HTML, CSS ve JavaScript (ek ön yüz çerçevesi veya Node.js gerektirmez)
- Maven

## Proje mimarisi

Bir istek web panelinden veya Swagger'dan API'ye gelir. `controller` isteği karşılar, `service` iş kurallarını uygular, `repository` veritabanı işlemini yürütür. Sonuç JSON olarak istemciye döner. Panel bu JSON yanıtlarını ekranda tablo, kart ve formlara dönüştürür.

| Klasör | Sorumluluk |
| --- | --- |
| `controller` | HTTP uç noktalarını tanımlar. |
| `service` / `service/impl` | İş kurallarını ve uygulama akışını yönetir. |
| `repository` | Spring Data JPA ile PostgreSQL'e erişir. |
| `entity` | Veritabanı tablolarını ve ilişkileri tanımlar. |
| `dto/request` | API isteklerini ve giriş doğrulamalarını tanımlar. |
| `dto/response` | API yanıtlarının veri biçimini tanımlar. |
| `exception` | Hataları ortak Türkçe yanıt biçimine dönüştürür. |
| `config` | Swagger ayarları ve örnek veri başlangıcını içerir. |
| `src/main/resources/static` | Spring Boot'un sunduğu kullanıcı panelini içerir. |

## Kurulum ve çalıştırma

### Gereksinimler

Java 21, Maven ve PostgreSQL 16 (veya uyumlu güncel bir sürüm) kurulu olmalıdır. PostgreSQL sunucusu `localhost:5432` adresinde dinlemelidir.

### Veritabanı

Uygulamanın varsayılan ayarları `src/main/resources/application.properties` içindedir:

```properties
spring.datasource.url=${DB_URL:jdbc:postgresql://localhost:5432/asset_ticket_db}
spring.datasource.username=${DB_USERNAME:postgres}
spring.datasource.password=${DB_PASSWORD}
```

PostgreSQL'de `asset_ticket_db` adında bir veritabanı oluşturun. Veritabanı parolasını proje dosyasına yazmayın; VS Code terminalinde uygulamayı başlatmadan önce `DB_PASSWORD` ortam değişkenine yerel parolanızı verin. Örneğin, parolanız `postgres` ise:

```powershell
$env:DB_PASSWORD = "postgres"
```

Bu değişken yalnızca açık olan terminal için geçerlidir; yeni terminal açtığınızda tekrar ayarlayın. Gerçek parolaları GitHub'a göndermeyin.

PostgreSQL'i Windows hizmeti olarak kurduysanız hizmetin çalıştığını doğrulayın. Elle oluşturulmuş yerel bir küme kullanıyorsanız PostgreSQL sunucusunu başlatın:

```powershell
& "$env:ProgramFiles\PostgreSQL\16\bin\pg_ctl.exe" -D "$env:USERPROFILE\postgres-data" -l "$env:USERPROFILE\postgres-data\postgres.log" start
```

Veritabanı henüz oluşturulmadıysa bir kez şu komutu çalıştırın:

```powershell
& "$env:ProgramFiles\PostgreSQL\16\bin\createdb.exe" -h localhost -p 5432 -U postgres asset_ticket_db
```

### Uygulamayı başlatma

VS Code'da `pom.xml` dosyasının bulunduğu proje klasörünü açın. **Terminal > Yeni Terminal** menüsünden terminal açıp şunu çalıştırın:

```powershell
mvn spring-boot:run
```

İlk açılışta Maven bağımlılıkları indirir. Konsolda `Started AssetServiceTicketApiApplication` satırını gördüğünüzde uygulama hazırdır. Terminali açık tutun; durdurmak için `Ctrl+C` kullanın.

## Ekranlar

- **Kullanıcı paneli:** <http://localhost:8080/>
- **Giriş ekranı:** <http://localhost:8080/login.html>
- **Swagger API dokümantasyonu:** <http://localhost:8080/swagger-ui/index.html>
- **OpenAPI JSON:** <http://localhost:8080/v3/api-docs>

Panelde önce **Genel Bakış** özetini, ardından rolünüze açık olan ekranları inceleyebilirsiniz. Yönetici demirbaşları kullanıcıya zimmetleyebilir ve talepleri teknisyene atayabilir. Çalışan yalnızca kendisine zimmetli cihazlardan talep açar; talep sahibi oturumdan otomatik belirlenir. Talep tablosundaki yorumlar da oturum açan kullanıcının hesabına yazılır. Kullanıcı ve departman yönetim ekranları yalnızca yöneticiye açıktır.

Swagger'da `GET /api/assets` listesi demirbaş ID numaralarını gösterir. `POST /api/tickets` gövdesinde yalnızca talep bilgileri ve `assetId` gönderilir; talep sahibi oturum açmış hesaptan otomatik alınır. Yorumlarda da kullanıcı kimliği seçilmez, yorum giriş yapan hesaba bağlanır. Endpoint açıklamaları işlemin amacını, **Schemas** ise istek ve yanıt verilerinin alanlarını anlatır.

## Giriş ve roller

Uygulama paneline ve `/api/**` uç noktalarına erişmek için oturum açılması gerekir. Tarayıcı oturumu güvenli çerezle taşır; Swagger arayüzü dokümantasyon için açık kalır, ancak **Try it out** ile API çağrısı yapmak için önce aynı tarayıcıda giriş yapılmalıdır. Yeni kullanıcı oluştururken parola belirlenir ve veritabanına BCrypt özeti yazılır. Özet panel sayıları da kullanıcının rolüne ve erişebildiği kayıtlara göre hesaplanır.

Yerel demo verileriyle farklı rolleri denemek için bu e-posta hesaplarından biriyle giriş yap. Örnek hesapların geçici parolası `Demo123!` olarak ayarlanır:

| Rol | E-posta |
| --- | --- |
| Yönetici | `admin@example.com` |
| Teknisyen | `technician@example.com` veya `technician2@example.com` |
| Çalışan | `employee@example.com`, `employee2@example.com` veya `employee3@example.com` |

Roller ve kayıt erişimleri şu şekilde sınırlandırılır:

- **Yönetici:** Tüm demirbaş ve destek taleplerini görür; kullanıcı/departman/demirbaş yönetir ve talepleri teknisyenlere atar.
- **Teknisyen:** Kurum envanterini görüntüler; yalnızca kendisine atanmış destek taleplerini görür, durumlarını günceller ve yorum yazar.
- **Çalışan:** Yalnızca kendisine zimmetli demirbaşları ve kendi açtığı talepleri görür; yalnızca kendi talebine yorum ekler.

Her API liste ve detay isteği de aynı kuralları uygular; ekranda gizlemek tek başına güvenlik sınırı olarak kullanılmaz. Talep ve yorumun sahibi oturum açmış hesaptan sunucuda belirlenir. Bu parola yalnızca yerel eğitim demosu içindir; gerçek kullanımda değiştirilmeli ve parola yenileme/güvenli hesap yönetimi eklenmelidir.

Yeni bir kişinin giriş yapabilmesi için yönetici **Kullanıcılar > Kullanıcı ekle** bölümünden e-posta, rol, departman ve ilk parolayı belirleyerek hesap açar. Giriş bilgileri o kişiye güvenli biçimde iletilir; herkese açık kendi kendine kayıt kapalıdır. Oturum açan kişi destek talebi açtığında veya yorum yaptığında kayıt otomatik olarak kendi hesabına yazılır.

## Demo verileri

Veritabanı boşken uygulama dört departman, altı kullanıcı, sekiz demirbaş, sekiz destek talebi ve örnek yorumlar oluşturur. Roller, durumlar ve öncelikler veritabanı/API sözleşmesinde sabit kodlardır; panel bunları Türkçe etiketlerle gösterir. Örneğin API'de `IN_PROGRESS`, panelde “İşlemde” olarak görünür.

`DataSeeder` kayıtları benzersiz e-posta, etiket ve talep numarasıyla arar; yalnızca eksik olanları ekler. Böylece uygulama her yeniden başladığında aynı demo kayıtlarını çoğaltmaz. Yerel demo hesaplarının `Demo123!` parolasını ve etkin durumunu da eşitler; bu yalnızca sabit örnek hesaplardaki demo parolasıdır. JPA şema güncellemesi için `ddl-auto=update` kullanır; bu ayar yerel demo içindir.

## API uç noktaları

| İşlev | HTTP | Adres |
| --- | --- | --- |
| Departman oluştur/listele | `POST` / `GET` | `/api/departments` |
| Departman detayı/güncelle/sil | `GET` / `PUT` / `DELETE` | `/api/departments/{id}` |
| Kullanıcı oluştur/listele | `POST` / `GET` | `/api/users` |
| Kullanıcı detayı/güncelle/devre dışı bırak | `GET` / `PUT` / `PATCH` | `/api/users/{id}` |
| Demirbaş oluştur/listele/filtrele | `POST` / `GET` | `/api/assets` |
| Demirbaş detayı/güncelle/sil | `GET` / `PUT` / `DELETE` | `/api/assets/{id}` |
| Etiketle demirbaş bul | `GET` | `/api/assets/by-tag/{assetTag}` |
| Talep oluştur/listele | `POST` / `GET` | `/api/tickets` |
| Talep detayı/güncelle/sil | `GET` / `PUT` / `DELETE` | `/api/tickets/{id}` |
| Talebi ata/durumunu değiştir | `PATCH` | `/api/tickets/{id}/assign`, `/api/tickets/{id}/status` |
| Talep yorumlarını gör/ekle | `GET` / `POST` | `/api/tickets/{ticketId}/comments` |
| Özet raporu getir | `GET` | `/api/reports/summary` |

## İş kuralları ve kapsam

- Demirbaş etiketleri, departman adları ve kullanıcı e-posta adresleri benzersizdir.
- Yeni talepler `OPEN` durumunda ve `TCK-000001` biçiminde bir numarayla oluşturulur.
- Talep yalnızca aktif `TECHNICIAN` rolündeki kullanıcıya atanabilir.
- Talep `CLOSED` olunca kapanış zamanı kaydedilir; kapatılmış veya iptal edilmiş talepler güncellenemez.
- İlişkili kayıtları veya yorumları bulunan bazı kaynaklar silinemez.

Bu sürüm, eğitim amaçlı yerel kullanıma yönelik bir demo uygulamasıdır. Oturum açma ve temel rol kontrolleri uygulanmıştır; ancak parola sıfırlama, çok faktörlü doğrulama, hesap kilitleme, kapsamlı denetim kaydı, otomatik test paketi ve dosya eki yönetimi bu kapsamda yoktur. README'deki demo parolalarını değiştirin; uygulamayı ek güvenlik çalışması yapmadan internete açık üretim ortamına dağıtmayın.
