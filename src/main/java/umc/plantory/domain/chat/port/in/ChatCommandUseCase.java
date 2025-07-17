package umc.plantory.domain.chat.port.in;

public interface ChatCommandUseCase {
    String ask(String content, Long memberId);
}
