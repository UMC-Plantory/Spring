package umc.plantory.domain.chat.service;

import umc.plantory.domain.chat.dto.ChatRequestDTO;

public interface ChatCommandUseCase {
    String ask(String authorization, ChatRequestDTO request);
}
