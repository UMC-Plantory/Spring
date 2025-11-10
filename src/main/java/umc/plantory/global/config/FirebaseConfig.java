package umc.plantory.global.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

import java.io.FileInputStream;
import java.io.IOException;

@Configuration
@Slf4j
public class FirebaseConfig {

    @PostConstruct
    public void init() throws IOException {
        // 실서버용
//        String fileResourceURL = "/home/ubuntu/plantory-config/fcm/plantory-firebase-adminsdk.json";
        // 개발용
        String fileResourceURL = "src/main/resources/security/server-security/fcm/plantory-firebase-adminsdk.json";
        try (FileInputStream serviceAccount = new FileInputStream(fileResourceURL)) {
            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();
            FirebaseApp.initializeApp(options);
            log.error("✅ Firebase initialized successfully.");
        } catch (Exception e) {
            log.error("❌ Failed to initialize Firebase: {}", e.getMessage());
            throw e;
        }
    }
}
