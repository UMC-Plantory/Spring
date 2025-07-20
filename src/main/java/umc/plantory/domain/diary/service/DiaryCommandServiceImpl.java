package umc.plantory.domain.diary.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import umc.plantory.domain.diary.converter.DiaryConverter;
import umc.plantory.domain.diary.dto.request.DiaryRequestDTO;
import umc.plantory.domain.diary.dto.response.DiaryResponseDTO;
import umc.plantory.domain.diary.entity.Diary;
import umc.plantory.domain.diary.entity.DiaryImg;
import umc.plantory.domain.diary.repository.DiaryImgRepository;
import umc.plantory.domain.diary.repository.DiaryRepository;
import umc.plantory.domain.image.service.ImageService;
import umc.plantory.domain.member.entity.Member;
import umc.plantory.domain.member.repository.MemberRepository;
import umc.plantory.domain.wateringCan.converter.WateringCanConverter;
import umc.plantory.domain.wateringCan.entity.WateringCan;
import umc.plantory.domain.wateringCan.repository.WateringCanRepository;
import umc.plantory.global.apiPayload.code.status.ErrorStatus;
import umc.plantory.global.apiPayload.exception.handler.MemberHandler;
import umc.plantory.global.enums.DiaryStatus;

import java.time.LocalDate;

/**
 * 일기 작성 관련 커맨드(등록/수정 등) 비즈니스 로직을 처리하는 서비스
 */

@Service
@RequiredArgsConstructor
public class DiaryCommandServiceImpl implements DiaryCommandService {
    private final DiaryRepository diaryRepository;
    private final DiaryImgRepository diaryImgRepository;
    private final MemberRepository memberRepository;
    private final WateringCanRepository wateringCanRepository;
    private final ImageService imageService;

    /**
     * 일기 저장
     * <p>
     * 1. 회원 조회 (임시: ID 1 고정)
     * 2. 일기 엔티티 생성 및 저장
     * 3. 이미지가 존재할 경우, S3 존재 여부 확인 및 DiaryImg 저장
     * 4. 정식 저장 + 오늘 날짜이면 물뿌리개 지급
     * </p>
     *
     * @param request 일기 작성 요청 DTO
     * @return 저장된 일기에 대한 응답 DTO
     * @throws MemberHandler 회원이 존재하지 않는 경우
     */
    @Override
    @Transactional
    public DiaryResponseDTO.DiaryInfoDTO saveDiary(DiaryRequestDTO request) {

        // 임시로 1번 멤버 불러오기
        Member member = memberRepository.findById(1L)
                .orElseThrow(() -> new MemberHandler(ErrorStatus.MEMBER_NOT_FOUND));

        // diary 엔티티 생성 및 저장
        Diary diary = DiaryConverter.toDiary(request, member);
        diaryRepository.save(diary);

        // 이미지 URL이 있는 경우 → S3에 해당 이미지가 등록되었는지 확인 + DiaryImg 저장
        String imageUrl = request.getDiaryImgUrl();
        if (imageUrl != null && !imageUrl.isBlank()) {
            imageService.validateImageExistence(imageUrl);
            DiaryImg diaryImg = DiaryConverter.toDiaryImg(imageUrl, diary);
            diaryImgRepository.save(diaryImg);
        }

        // 정식 저장이고 오늘 작성된 일기인 경우
        if (diary.getStatus() == DiaryStatus.NORMAL && diary.getDiaryDate().isEqual(LocalDate.now())) {
            // 유저가 가진 물뿌리개 +1
            member.increaseWateringCan();

            // wateringCan 엔티티 생성 및 저장
            WateringCan wateringCan = WateringCanConverter.toWateringCan(diary);
            wateringCanRepository.save(wateringCan);
        }

        return DiaryConverter.toDiaryInfoDTO(diary, imageUrl);
    }
}
