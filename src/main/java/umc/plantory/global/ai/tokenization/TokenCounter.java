package umc.plantory.global.ai.tokenization;

import com.knuddels.jtokkit.Encodings;
import com.knuddels.jtokkit.api.Encoding;
import com.knuddels.jtokkit.api.EncodingRegistry;
import com.knuddels.jtokkit.api.EncodingType;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TokenCounter {
    private final Encoding encoding;

    public TokenCounter() {
        EncodingRegistry registry = Encodings.newDefaultEncodingRegistry();
        this.encoding = registry.getEncoding(EncodingType.CL100K_BASE);
    }

    // 메시지 리스트로부터 전체 토큰 수 계산
    public int calculateTotalTokens(List<String> messages) {
        int totalTokens = 0;
        for (String message : messages) {
            totalTokens += encoding.countTokens(message);
        }
        return totalTokens;
    }
}
