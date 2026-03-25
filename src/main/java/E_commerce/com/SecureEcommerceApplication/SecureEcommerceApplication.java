package E_commerce.com.SecureEcommerceApplication;
import com.resend.*;

import com.resend.core.exception.ResendException;
import com.resend.services.emails.model.CreateEmailOptions;
import com.resend.services.emails.model.CreateEmailResponse;
import io.github.cdimascio.dotenv.Dotenv;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@OpenAPIDefinition(
		info = @Info(
				title = "Secure E-commerce API",
				version = "1.0",
				description = "REST API documentation for the Secure E-commerce Application",
				contact = @Contact(
						name = "Omar Abouhashim"
				)
		)
)
@SecurityScheme(
		name = "bearerAuth",
		description = "JWT auth description",
		scheme = "bearer",
		type = SecuritySchemeType.HTTP,
		bearerFormat = "JWT",
		in = SecuritySchemeIn.HEADER
)
@EnableScheduling // 👈 required for @Scheduled
public class SecureEcommerceApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(SecureEcommerceApplication.class, args);
		// تحميل كل متغيرات .env قبل أي bean
		Dotenv dotenv = Dotenv.configure().filename(".env").load();
		dotenv.entries().forEach(entry -> System.setProperty(entry.getKey(), entry.getValue()));

		SpringApplication.run(SecureEcommerceApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
	}
	}