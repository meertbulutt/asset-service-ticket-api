package com.example.assetserviceticket.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** Swagger arayüzünde gösterilecek API başlığını ve tanıtım metnini ayarlar. */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI assetServiceTicketOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Demirbaş ve Teknik Destek Talep API'si")
                        .description("Kurum demirbaşlarını ve teknik destek taleplerini yönetmek için REST API. "
                                + "API uç noktalarına erişmeden önce /login.html adresinden oturum açın; Swagger arayüzü dokümantasyon için açıktır. "
                                + "POST /api/tickets için GET /api/assets listesinden demirbaş ID'sini bulun; talep sahibi oturumdan otomatik alınır. "
                                + "Kullanıcı yönetimi yalnızca yönetici rolüne açıktır. "
                                + "Rol, durum, tür ve öncelik seçenekleri büyük harfli sabit değerlerle gönderilir.")
                        .version("v1"));
    }
}
