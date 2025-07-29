package umc.plantory.domain.chat.service;

public interface ChatCommandUseCase {
    String ask(String content, Long memberId);
}
