package umc.plantory.domain.diary.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
import umc.plantory.global.apiPayload.exception.handler.DiaryHandler;
import umc.plantory.global.apiPayload.exception.handler.MemberHandler;
import umc.plantory.global.enums.DiaryStatus;
import umc.plantory.global.enums.Emotion;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 일기 작성 관련 커맨드(등록/수정 등) 비즈니스 로직을 처리하는 서비스
 */

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DiaryCommandServiceImpl implements DiaryCommandService {
    private final DiaryRepository diaryRepository;
    private final DiaryImgRepository diaryImgRepository;
    private final MemberRepository memberRepository;
    private final WateringCanRepository wateringCanRepository;
    private final ImageService imageService;

    /**
     * 일기 등록
     *
     * @param request 일기 작성 요청 DTO
     * @return 저장된 일기에 대한 응답 DTO
     * @throws MemberHandler 회원이 존재하지 않는 경우
     */
    @Override
    @Transactional
    public DiaryResponseDTO.DiaryInfoDTO saveDiary(DiaryRequestDTO.DiaryUploadDTO request) {

        // 임시로 1번 멤버 불러오기
        Member member = memberRepository.findById(1L)
                .orElseThrow(() -> new MemberHandler(ErrorStatus.MEMBER_NOT_FOUND));

        // diary 엔티티 생성 및 저장
        Diary diary = DiaryConverter.toDiary(request, member);
        diaryRepository.save(diary);

        // 이미지 등록 처리
        String imageUrl = handleDiaryImage(diary, request.getDiaryImgUrl(), false);

        handleWateringCan(diary, member);

        return DiaryConverter.toDiaryInfoDTO(diary, imageUrl);
    }

    /**
     * 기존 일기를 수정
     *
     * @param diaryId 수정할 일기 ID
     * @param request 일기 수정 요청 DTO
     * @return 수정된 일기 정보를 담은 응답 DTO
     * @throws MemberHandler 회원이 존재하지 않는 경우
     * @throws DiaryHandler 일기가 존재하지 않거나 권한이 없는 경우
     */
    @Override
    @Transactional
    public DiaryResponseDTO.DiaryInfoDTO updateDiary(Long diaryId, DiaryRequestDTO.DiaryUpdateDTO request) {

        Member member = memberRepository.findById(1L)
                .orElseThrow(() -> new MemberHandler(ErrorStatus.MEMBER_NOT_FOUND));

        Diary diary = diaryRepository.findById(diaryId)
                .orElseThrow(() -> new DiaryHandler(ErrorStatus.DIARY_NOT_FOUND));

        // 해당 일기의 작성자가 맞는지 확인
        if (!diary.getMember().getId().equals(member.getId())) {
            throw new DiaryHandler(ErrorStatus.DIARY_UNAUTHORIZED);
        }

        // 이미지 업데이트 처리
        String diaryImgUrl = handleDiaryImage(diary, request.getDiaryImgUrl(), Boolean.TRUE.equals(request.getIsImgDeleted()));

        // 일기 내용 업데이트
        Emotion emotion = request.getEmotion() != null ? Emotion.valueOf(request.getEmotion()) : diary.getEmotion();
        String content = request.getContent() != null ? request.getContent() : diary.getContent();
        LocalDateTime sleepStart = request.getSleepStartTime() != null ? request.getSleepStartTime() : diary.getSleepStartTime();
        LocalDateTime sleepEnd = request.getSleepEndTime() != null ? request.getSleepEndTime() : diary.getSleepEndTime();
        DiaryStatus status = request.getStatus() != null ? DiaryStatus.valueOf(request.getStatus()) : diary.getStatus();

        // 임시 저장 → 정식 저장일때 필수 필드 다 있는지 확인
        if (status == DiaryStatus.NORMAL &&
                (emotion == null || content == null || content.isBlank() || sleepStart == null || sleepEnd == null)) {
            throw new DiaryHandler(ErrorStatus.DIARY_MISSING_FIELDS);
        }

        diary.update(emotion, content, sleepStart, sleepEnd, status);

        handleWateringCan(diary, member);

        return DiaryConverter.toDiaryInfoDTO(diary, diaryImgUrl);
    }

    // 이미지 처리
    private String handleDiaryImage(Diary diary, String newImageUrl, boolean isImgDeleted) {
        DiaryImg diaryImg = diaryImgRepository.findByDiary(diary).orElse(null);

        // 기존 이미지가 있는 경우
        if (diaryImg != null) {
            // 기존 이미지 삭제
            if (isImgDeleted) {
                imageService.deleteImage(diaryImg.getDiaryImgUrl());
                diaryImgRepository.delete(diaryImg);
                return null;
            }

            // 이미지 교체
            if (newImageUrl != null && !newImageUrl.equals(diaryImg.getDiaryImgUrl())) {
                imageService.validateImageExistence(newImageUrl);
                imageService.deleteImage(diaryImg.getDiaryImgUrl());
                diaryImg.updateUrl(newImageUrl);
                return newImageUrl;
            }

            return diaryImg.getDiaryImgUrl();
        }

        // 기존 이미지가 없는 경우
        // 이미지 새로 등록
        if (!isImgDeleted && newImageUrl != null) {
            imageService.validateImageExistence(newImageUrl);
            DiaryImg newDiaryImg = DiaryConverter.toDiaryImg(newImageUrl, diary);
            diaryImgRepository.save(newDiaryImg);
            return newImageUrl;
        }

        return null;
    }

    // 물뿌리개 지급
    private void handleWateringCan(Diary diary, Member member) {
        if (diary.getStatus() == DiaryStatus.NORMAL && diary.getDiaryDate().isEqual(LocalDate.now())) {
            member.increaseWateringCan();
            WateringCan wateringCan = WateringCanConverter.toWateringCan(diary);
            wateringCanRepository.save(wateringCan);
        }
    }
}