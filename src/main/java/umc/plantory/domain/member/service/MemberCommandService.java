package umc.plantory.domain.member.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import umc.plantory.domain.member.converter.MemberConverter;
import umc.plantory.domain.member.dto.MemberDataDTO;
import umc.plantory.domain.member.entity.Member;
import umc.plantory.domain.member.repository.MemberRepository;

@Service
@RequiredArgsConstructor
public class MemberCommandService implements MemberCommandUseCase {
    private final MemberRepository memberRepository;

    /**
     * id_token 에서 추출한 멤버 데이터를 통해
     * 기존 회원이라면 조회해서 가져오고
     * 첫 로그인이면 데이터 저장하는 메서드
     * @param kakaoMemberData : id_token 에서 추출한 멤버 데이터
     * @return : 추출한 멤버 데이터와 일치하는 멤버
     */
    @Override
    @Transactional
    public Member findOrCreateMember(MemberDataDTO.KakaoMemberData kakaoMemberData) {
        Member findOrNewMember = memberRepository.findByProviderId(kakaoMemberData.getSub())
                .orElseGet(() -> MemberConverter.toMember(kakaoMemberData));

        return memberRepository.save(findOrNewMember);
    }
}
