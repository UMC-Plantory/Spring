package umc.plantory.domain.diary.service;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import umc.plantory.global.ai.AIClient;
import umc.plantory.global.ai.PromptFactory;
import umc.plantory.domain.diary.converter.DiaryConverter;
import umc.plantory.domain.diary.dto.DiaryRequestDTO;
import umc.plantory.domain.diary.dto.DiaryResponseDTO;
import umc.plantory.domain.diary.entity.Diary;
import umc.plantory.domain.diary.entity.DiaryImg;
import umc.plantory.domain.diary.repository.DiaryImgRepository;
import umc.plantory.domain.diary.repository.DiaryRepository;
import umc.plantory.domain.image.service.ImageUseCase;
import umc.plantory.domain.member.entity.Member;
import umc.plantory.domain.member.repository.MemberRepository;
import umc.plantory.domain.token.provider.JwtProvider;
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
import java.util.List;

/**
 * 일기 작성 관련 커맨드(등록/수정 등) 비즈니스 로직을 처리하는 서비스
 */

@Service
@RequiredArgsConstructor
public class DiaryCommandService implements DiaryCommandUseCase {
    private final DiaryRepository diaryRepository;
    private final DiaryImgRepository diaryImgRepository;
    private final MemberRepository memberRepository;
    private final WateringCanRepository wateringCanRepository;
    private final ImageUseCase imageUseCase;
    private final JwtProvider jwtProvider;
    private final AIClient aiClient;

    /**
     * 일기 등록
     *
     * @param request 일기 작성 요청 DTO
     * @return 저장된 일기에 대한 응답 DTO
     */
    @Override
    @Transactional
    public DiaryResponseDTO.DiaryInfoDTO saveDiary(String authorization, DiaryRequestDTO.DiaryUploadDTO request) {
        Member member = getLoginMember(authorization);

        // 일기 제목 생성
        String diaryTitle = generateDiaryTitle(request.getContent());

        // 일기 & 이미지 엔티티 생성 및 저장
        Diary diary = DiaryConverter.toDiary(request,member, diaryTitle);
        diaryRepository.save(diary);
        String imageUrl = handleDiaryImage(diary, request.getDiaryImgUrl(), false);

        // NORMAL 상태일 경우 누적 감정 기록 횟수 증가
        // 당일 작성한 일기일 경우 연속 기록 & 물뿌리개 +1
        if (diary.getStatus() == DiaryStatus.NORMAL) {
            member.increaseTotalRecordCnt();
            handleContinuousRecordCnt(diary, member);
            handleWateringCan(diary, member);

        // TEMP 상태일 경우 tempSavedAt 기록
        } else if (diary.getStatus() == DiaryStatus.TEMP) {
            diary.updateTempSavedAt(LocalDateTime.now());
        }

        return DiaryConverter.toDiaryInfoDTO(diary, imageUrl);
    }

    /**
     * 기존 일기를 수정
     *
     * @param diaryId 수정할 일기 ID
     * @param request 일기 수정 요청 DTO
     * @return 수정된 일기 정보를 담은 응답 DTO
     */
    @Override
    @Transactional
    public DiaryResponseDTO.DiaryInfoDTO updateDiary(String authorization, Long diaryId, DiaryRequestDTO.DiaryUpdateDTO request) {
        Member member = getLoginMember(authorization);

        Diary diary = getDiaryOrThrow(diaryId);

        // 일기 작성자 확인
        validateDiaryOwnership(diary, member);

        // 변경 전 상태
        DiaryStatus beforeStatus = diary.getStatus();

        // 일기 내용 업데이트
        Emotion emotion = request.getEmotion() != null ? Emotion.valueOf(request.getEmotion()) : diary.getEmotion();
        String content = request.getContent() != null ? request.getContent() : diary.getContent();
        LocalDateTime sleepStart = request.getSleepStartTime() != null ? request.getSleepStartTime() : diary.getSleepStartTime();
        LocalDateTime sleepEnd = request.getSleepEndTime() != null ? request.getSleepEndTime() : diary.getSleepEndTime();
        DiaryStatus status = request.getStatus() != null ? DiaryStatus.valueOf(request.getStatus()) : diary.getStatus();

        // NORMAL 저장일때 필수 필드 다 있는지 확인
        if (status == DiaryStatus.NORMAL &&
                (emotion == null || content == null || sleepStart == null || sleepEnd == null)) {
            throw new DiaryHandler(ErrorStatus.DIARY_MISSING_FIELDS);
        }

        // 일기 본문 변경 시, 제목 다시 생성
        String title = request.getContent() != null ? generateDiaryTitle(content) : diary.getTitle();

        // 일기, 이미지 업데이트 처리
        diary.update(emotion, title, content, sleepStart, sleepEnd, status);
        String diaryImgUrl = handleDiaryImage(diary, request.getDiaryImgUrl(), Boolean.TRUE.equals(request.getIsImgDeleted()));

        // TEMP 상태일 경우 tempSavedAt 기록
        if (status == DiaryStatus.TEMP) {
            diary.updateTempSavedAt(LocalDateTime.now());

        // TEMP → NORMAL 상태일 경우 누적 감정 기록 횟수 증가
        // 당일 작성한 일기일 경우 연속 기록 & 물뿌리개 +1
        } else if (beforeStatus == DiaryStatus.TEMP && status == DiaryStatus.NORMAL) {
            member.increaseTotalRecordCnt();
            handleContinuousRecordCnt(diary, member);
            handleWateringCan(diary, member);
        }

        return DiaryConverter.toDiaryInfoDTO(diary, diaryImgUrl);
    }

    /**
     * 일기를 스크랩 상태로 변경
     *
     * @param diaryId 일기 ID
     */
    @Override
    @Transactional
    public void scrapDiary(String authorization, Long diaryId) {
        Member member = getLoginMember(authorization);

        Diary diary = getDiaryOrThrow(diaryId);

        validateDiaryOwnership(diary, member);

        // NORMAL 상태인 일기만 스크랩 가능
        if (diary.getStatus() != DiaryStatus.NORMAL) {
            throw new DiaryHandler(ErrorStatus.DIARY_INVALID_STATUS);
        }

        diary.updateStatus(DiaryStatus.SCRAP);
    }

    /**
     * 일기 스크랩을 취소
     *
     * @param diaryId 일기 ID
     */
    @Override
    @Transactional
    public void cancelScrapDiary(String authorization, Long diaryId) {
        Member member = getLoginMember(authorization);

        Diary diary = getDiaryOrThrow(diaryId);

        validateDiaryOwnership(diary, member);

        // SCRAP 상태였던 일기만 취소 가능
        if (diary.getStatus() != DiaryStatus.SCRAP) {
            throw new DiaryHandler(ErrorStatus.DIARY_INVALID_STATUS);
        }

        diary.updateStatus(DiaryStatus.NORMAL);
    }

    /**
     * 여러 일기를 임시 보관 상태로 변경
     *
     * @param request 일기 ID 목록 DTO
     */
    @Override
    @Transactional
    public void tempSaveDiaries(String authorization, DiaryRequestDTO.DiaryIdsDTO request) {
        Member member = getLoginMember(authorization);

        List<Diary> diaries = getDiariesOrThrow(request.getDiaryIds());

        // 일기 작성자 확인 및 상태 TEMP로 변경
        for (Diary diary : diaries) {
            validateDiaryOwnership(diary, member);

            // NORMAL / SCRAP → TEMP 상태일 경우 누적 감정 기록 횟수 감소
            if (diary.getStatus() == DiaryStatus.NORMAL || diary.getStatus() == DiaryStatus.SCRAP) {
                member.decreaseTotalRecordCnt();
            }

            diary.updateStatus(DiaryStatus.TEMP);

            // tempSavedAt 기록
            diary.updateTempSavedAt(LocalDateTime.now());
        }
    }

    /**
     * 여러 일기를 삭제 상태로 변경
     *
     * @param request 일기 ID 목록 DTO
     */
    @Override
    @Transactional
    public void softDeleteDiaries(String authorization, DiaryRequestDTO.DiaryIdsDTO request) {
        Member member = getLoginMember(authorization);

        List<Diary> diaries = getDiariesOrThrow(request.getDiaryIds());

        // 일기 작성자 확인 및 상태 DELETE로 변경
        for (Diary diary : diaries) {
            validateDiaryOwnership(diary, member);

            // NORMAL / SCRAP → DELETE 상태일 경우 누적 감정 기록 횟수 감소
            if (diary.getStatus() == DiaryStatus.NORMAL || diary.getStatus() == DiaryStatus.SCRAP) {
                member.decreaseTotalRecordCnt();
            }

            diary.updateStatus(DiaryStatus.DELETE);

            // deletedAt 기록
            diary.updateDeletedAt(LocalDateTime.now());
        }
    }

    /**
     * 여러 일기를 영구 삭제
     *
     * @param request 일기 ID 목록 DTO
     */
    @Override
    @Transactional
    public void hardDeleteDiaries(String authorization, DiaryRequestDTO.DiaryIdsDTO request) {
        Member member = getLoginMember(authorization);

        List<Diary> diaries = getDiariesOrThrow(request.getDiaryIds());

        // 일기 작성자 확인 및 이미지, 물뿌리개 처리
        for (Diary diary : diaries) {
            validateDiaryOwnership(diary, member);

            // 이미지 삭제
            diaryImgRepository.findByDiary(diary).ifPresent(diaryImg -> {
                imageUseCase.deleteImage(diaryImg.getDiaryImgUrl());
                diaryImgRepository.delete(diaryImg);
            });

            // 물뿌리개에 연결된 diary null로 처리
            wateringCanRepository.findByDiary(diary)
                    .ifPresent(wateringCan -> wateringCan.updateDiary(null));

            diaryRepository.delete(diary);
        }
    }

    // 로그인한 사용자 반환
    private Member getLoginMember(String authorization) {
        String token = jwtProvider.resolveToken(authorization);
        if (token == null) {
            throw new MemberHandler(ErrorStatus._UNAUTHORIZED);
        }

        jwtProvider.validateToken(token);
        Long memberId = jwtProvider.getMemberId(token);

        return memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberHandler(ErrorStatus.MEMBER_NOT_FOUND));
    }

    // 일기 ID로 일기 단건 조회
    private Diary getDiaryOrThrow(Long diaryId) {
        return diaryRepository.findById(diaryId)
                .orElseThrow(() -> new DiaryHandler(ErrorStatus.DIARY_NOT_FOUND));
    }

    // 일기 ID로 일기 여러건 조회
    private List<Diary> getDiariesOrThrow(List<Long> diaryIds) {
        List<Diary> diaries = diaryRepository.findAllById(diaryIds);
        if (diaries.size() != diaryIds.size()) {
            throw new DiaryHandler(ErrorStatus.DIARY_NOT_FOUND);
        }
        return diaries;
    }

    // 주어진 일기의 작성자가 현재 사용자인지 확인
    private void validateDiaryOwnership(Diary diary, Member member) {
        if (!diary.getMember().getId().equals(member.getId())) {
            throw new DiaryHandler(ErrorStatus.DIARY_UNAUTHORIZED);
        }
    }

    // 이미지 처리
    private String handleDiaryImage(Diary diary, String newImageUrl, boolean isImgDeleted) {
        DiaryImg diaryImg = diaryImgRepository.findByDiary(diary).orElse(null);

        // 기존 이미지가 있는 경우
        if (diaryImg != null) {
            // 기존 이미지 삭제
            if (isImgDeleted && newImageUrl == null) {
                imageUseCase.deleteImage(diaryImg.getDiaryImgUrl());
                diaryImgRepository.delete(diaryImg);
                return null;
            }

            // 이미지 교체
            if (newImageUrl != null && !newImageUrl.equals(diaryImg.getDiaryImgUrl())) {
                imageUseCase.validateImageExistence(newImageUrl);
                imageUseCase.deleteImage(diaryImg.getDiaryImgUrl());
                diaryImg.updateImgUrl(newImageUrl);
                return newImageUrl;
            }

            return diaryImg.getDiaryImgUrl();
        }

        // 기존 이미지가 없는 경우
        // 이미지 새로 등록
        if (!isImgDeleted && newImageUrl != null) {
            imageUseCase.validateImageExistence(newImageUrl);
            DiaryImg newDiaryImg = DiaryConverter.toDiaryImg(newImageUrl, diary);
            diaryImgRepository.save(newDiaryImg);
            return newImageUrl;
        }

        return null;
    }

    // 연속 기록 처리
    private void handleContinuousRecordCnt(Diary diary, Member member) {
        // 당일 작성한 일기고, 오늘 연속 기록이 늘어난 적 없을 때
        if (diary.getDiaryDate().isEqual(LocalDate.now()) &&
                (member.getLastDiaryDate() == null || !(member.getLastDiaryDate().isEqual(LocalDate.now())))) {

            // 연속 기록 +1
            member.increaseContinuousRecordCnt();

            // 마지막으로 작성한 일기 날짜 업데이트
            member.updateLastDiaryDate(diary.getDiaryDate());
        }
    }

    // 물뿌리개 지급
    private void handleWateringCan(Diary diary, Member member) {
        // 당일 작성한 일기고, 오늘 물뿌리개를 지급 받은 적 없을 때
        if (diary.getDiaryDate().isEqual(LocalDate.now()) &&
                !wateringCanRepository.existsByDiaryDateAndMember(diary.getDiaryDate(), member)) {

            // 사용자가 가진 물뿌리개 개수 +1
            member.increaseWateringCan();

            // wateringCan 엔티티 생성 및 저장
            WateringCan wateringCan = WateringCanConverter.toWateringCan(diary, member);
            wateringCanRepository.save(wateringCan);
        }
    }

    // 프롬프트 생성 및 AI 호출 후, 제목 응답 받기
    private String generateDiaryTitle(String content) {
        Prompt prompt = PromptFactory.buildDiaryTitlePrompt(content);
        return aiClient.getResponse(prompt);
    }
}