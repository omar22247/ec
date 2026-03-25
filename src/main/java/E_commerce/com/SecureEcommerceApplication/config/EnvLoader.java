//package E_commerce.com.SecureEcommerceApplication.config;
//
//
//import io.github.cdimascio.dotenv.Dotenv;
//import jakarta.annotation.PostConstruct;
//
//import org.springframework.stereotype.Component;
//
//@Component
//public class EnvLoader {
//
//    @PostConstruct
//    public void loadEnv() {
//        Dotenv dotenv = Dotenv.configure()
//                .filename(".env") // اسم الملف
//                .load();
//
//        dotenv.entries().forEach(entry -> {
//            // يضيف كل متغير كـ System Property
//            System.setProperty(entry.getKey(), entry.getValue());
//        });
//    }
//}
