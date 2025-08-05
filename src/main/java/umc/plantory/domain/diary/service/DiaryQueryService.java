package umc.plantory.domain.diary.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import umc.plantory.domain.diary.converter.DiaryConverter;
import umc.plantory.domain.diary.dto.DiaryRequestDTO;
import umc.plantory.domain.diary.dto.DiaryResponseDTO;
import umc.plantory.domain.diary.entity.Diary;
import umc.plantory.domain.diary.entity.DiaryImg;
import umc.plantory.domain.diary.repository.DiaryImgRepository;
import umc.plantory.domain.diary.repository.DiaryRepository;
import umc.plantory.domain.member.entity.Member;
import umc.plantory.domain.member.repository.MemberRepository;
import umc.plantory.domain.token.provider.JwtProvider;
import umc.plantory.global.apiPayload.code.status.ErrorStatus;
import umc.plantory.global.apiPayload.exception.handler.DiaryHandler;
import umc.plantory.global.apiPayload.exception.handler.MemberHandler;
import umc.plantory.global.enums.DiaryStatus;

import java.time.LocalDate;
import java.util.List;

/**
 * 일기 조회 비즈니스 로직을 처리하는 서비스
 */

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DiaryQueryService implements DiaryQueryUseCase {
    private final DiaryRepository diaryRepository;
    private final DiaryImgRepository diaryImgRepository;
    private final MemberRepository memberRepository;
    private final JwtProvider jwtProvider;

    /**
     * 특정 일기 ID에 대한 상세 정보를 조회
     *
     * @param authorization 요청 헤더의 JWT 토큰
     * @param diaryId 조회할 일기의 ID
     * @return DiaryInfoDTO 일기 상세 정보
     */
    @Override
    public DiaryResponseDTO.DiaryInfoDTO getDiaryInfo(String authorization, Long diaryId) {
        Member member = getLoginMember(authorization);
        Diary diary = getDiaryOrThrow(diaryId);

        validateDiaryOwnership(diary, member);

        String imageUrl = diaryImgRepository.findByDiary(diary)
                .map(DiaryImg::getDiaryImgUrl)
                .orElse(null);

        return DiaryConverter.toDiaryInfoDTO(diary, imageUrl);
    }

    /**
     * 특정 날짜에 작성된 NORMAL, SCRAP 상태의 일기 요약 정보를 조회
     *
     * @param authorization 요청 헤더의 JWT 토큰
     * @param date 조회할 날짜
     * @return DiarySimpleInfoDTO 일기 요약 정보
     */
    @Override
    public DiaryResponseDTO.DiarySimpleInfoDTO getDiarySimpleInfo(String authorization, LocalDate date) {
        Member member = getLoginMember(authorization);

        // 해당 날짜의 NORMAL, SCRAP 상태인 일기 조회
        Diary diary = diaryRepository.findByMemberIdAndDiaryDateAndStatusIn(member.getId(), date, List.of(DiaryStatus.NORMAL, DiaryStatus.SCRAP))
                .orElseThrow(() -> new DiaryHandler(ErrorStatus.DIARY_NOT_FOUND));

        return DiaryConverter.toDiarySimpleInfoDTO(diary);
    }

    /**
     * 특정 날짜에 TEMP 상태(임시 저장) 일기가 존재하는지 확인
     *
     * @param authorization 요청 헤더의 JWT 토큰
     * @param date 확인할 날짜
     * @return TempDiaryExistsDTO 존재 여부
     */
    @Override
    public DiaryResponseDTO.TempDiaryExistsDTO checkTempDiaryExistence(String authorization, LocalDate date) {
        Member member = getLoginMember(authorization);

        // 해당 날짜의 TEMP 상태인 일기 확인
        boolean exists = diaryRepository.existsByMemberIdAndDiaryDateAndStatus(member.getId(), date, DiaryStatus.TEMP);

        return DiaryConverter.toTempDiaryExistsDTO(exists);
    }

    /**
     * 감정, 날짜, 정렬, 커서 기반 조건을 포함하여 NORMAL/SCRAP 상태의 일기 목록을 조회
     *
     * @param authorization 요청 헤더의 JWT 토큰
     * @param request 일기 필터 조건 (정렬, 커서, 기간, 감정 등)
     * @return CursorPaginationDTO 일기 목록 + 페이징 정보 + 검색된 개수
     */
    @Override
    public DiaryResponseDTO.CursorPaginationDTO<DiaryResponseDTO.DiaryListInfoDTO> getDiaryList(String authorization, DiaryRequestDTO.DiaryFilterDTO request) {
        Long memberId = getLoginMember(authorization).getId();
        List<Diary> diaries = diaryRepository.findFilteredDiaries(memberId, request);

        // 페이징 처리
        boolean hasNext = hasNext(diaries, request.getSize());
        List<Diary> trimmed = trimToSize(diaries, request.getSize(), hasNext);
        LocalDate nextCursor = getNextCursor(trimmed, hasNext);

        List<DiaryResponseDTO.DiaryListInfoDTO> content = trimmed.stream()
                .map(DiaryConverter::toDiaryListInfoDTO)
                .toList();

        return DiaryConverter.toCursorPaginationDTO(content, hasNext, nextCursor);
    }

    /**
     * 검색 키워드가 포함된 NORMAL/SCRAP 상태의 일기 목록을 커서 기반으로 조회
     *
     * @param authorization 요청 헤더의 JWT 토큰
     * @param keyword 검색어
     * @param cursor 커서 기준 날짜 (LocalDate)
     * @param size 한 번에 가져올 일기 개수
     * @return CursorPaginationTotalDTO 일기 목록 + 페이징 정보 + 검색된 개수
     */
    @Override
    public DiaryResponseDTO.CursorPaginationTotalDTO<DiaryResponseDTO.DiaryListInfoDTO> searchDiaries(String authorization, String keyword, LocalDate cursor, int size) {
        Long memberId = getLoginMember(authorization).getId();
        List<Diary> diaries = diaryRepository.searchDiaries(memberId, keyword, cursor, size);

        // 페이징 처리
        boolean hasNext = hasNext(diaries, size);
        List<Diary> trimmed = trimToSize(diaries, size, hasNext);
        LocalDate nextCursor = getNextCursor(trimmed, hasNext);

        // 검색된 일기 총 개수
        long total = diaryRepository.countDiariesByKeyword(memberId, keyword);

        List<DiaryResponseDTO.DiaryListInfoDTO> content = trimmed.stream()
                .map(DiaryConverter::toDiaryListInfoDTO)
                .toList();

        return DiaryConverter.toCursorPaginationWithTotalDTO(content, hasNext, nextCursor, total);
    }

    /**
     * SCRAP 상태의 일기들을 정렬 기준 및 커서 기반으로 조회
     *
     * @param authorization 요청 헤더의 JWT 토큰
     * @param sort 정렬 기준 ("latest", "oldest")
     * @param cursor 커서 기준 날짜 (LocalDate)
     * @param size 한 번에 가져올 일기 개수
     * @return CursorPaginationDTO 스크랩 일기 목록 + 페이징 정보
     */
    @Override
    public DiaryResponseDTO.CursorPaginationDTO<DiaryResponseDTO.DiaryListInfoDTO> getScrapDiaryList(String authorization, String sort, LocalDate cursor, int size) {
        Long memberId = getLoginMember(authorization).getId();
        List<Diary> diaries = diaryRepository.findScrappedDiaries(memberId, sort, cursor, size);

        // 페이징 처리
        boolean hasNext = hasNext(diaries, size);
        List<Diary> trimmed = trimToSize(diaries, size, hasNext);
        LocalDate nextCursor = getNextCursor(trimmed, hasNext);

        List<DiaryResponseDTO.DiaryListInfoDTO> content = trimmed.stream()
                .map(DiaryConverter::toDiaryListInfoDTO)
                .toList();

        return DiaryConverter.toCursorPaginationDTO(content, hasNext, nextCursor);
    }

    /**
     * TEMP 상태의 일기들을 정렬 기준에 따라 조회
     *
     * @param authorization 요청 헤더의 JWT 토큰
     * @param sort 정렬 기준 ("latest", "oldest")
     * @return DiaryListDTO 임시 보관 일기 리스트
     */
    @Override
    public DiaryResponseDTO.DiaryListDTO getTempDiaryList(String authorization, String sort) {
        Member member = getLoginMember(authorization);
        return getDiaryListByStatus(member, DiaryStatus.TEMP, sort);
    }

    /**
     * DELETE 상태의 일기들을 정렬 기준에 따라 조회
     *
     * @param authorization 요청 헤더의 JWT 토큰
     * @param sort 정렬 기준 ("latest", "oldest")
     * @return DiaryListDTO 삭제한 일기 리스트
     */
    @Override
    public DiaryResponseDTO.DiaryListDTO getDeletedDiaryList(String authorization, String sort) {
        Member member = getLoginMember(authorization);
        return getDiaryListByStatus(member, DiaryStatus.DELETE, sort);
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

    // 주어진 일기의 작성자가 현재 사용자인지 확인
    private void validateDiaryOwnership(Diary diary, Member member) {
        if (!diary.getMember().getId().equals(member.getId())) {
            throw new DiaryHandler(ErrorStatus.DIARY_UNAUTHORIZED);
        }
    }

    // 다음 페이지 여부 판단 (size + 1개를 조회한 경우 true)
    private boolean hasNext(List<Diary> diaries, int size) {
        return diaries.size() > size;
    }

    // 다음 페이지가 존재하는 경우 size만큼 잘라낸 리스트 반환
    private List<Diary> trimToSize(List<Diary> diaries, int size, boolean hasNext) {
        return hasNext ? diaries.subList(0, size) : diaries;
    }

    // 다음 커서로 사용할 diaryDate 추출
    private LocalDate getNextCursor(List<Diary> diaries, boolean hasNext) {
        return hasNext ? diaries.get(diaries.size() - 1).getDiaryDate() : null;
    }

    // DiaryStatus 기반 일기 리스트 조회 (정렬만 적용, 페이징 없음)
    private DiaryResponseDTO.DiaryListDTO getDiaryListByStatus(Member member, DiaryStatus status, String sort) {
        Sort.Direction direction = "oldest".equals(sort) ? Sort.Direction.ASC : Sort.Direction.DESC;

        List<Diary> diaries = diaryRepository.findAllByMemberIdAndStatus(
                member.getId(),
                status,
                Sort.by(direction, "diaryDate")
        );

        List<DiaryResponseDTO.DiaryListSimpleInfoDTO> result = diaries.stream()
                .map(DiaryConverter::toDiayListSimpleInfoDTO)
                .toList();

        return DiaryConverter.toDiaryListDTO(result);
    }
}
