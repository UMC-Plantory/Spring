package umc.plantory.domain.chat.service;

import umc.plantory.domain.chat.dto.ChatRequestDTO;
import umc.plantory.domain.chat.dto.ChatResponseDTO;

public interface ChatCommandUseCase {
    ChatResponseDTO ask(String authorization, ChatRequestDTO request);
}
