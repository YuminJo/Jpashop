package jpabook.jpashop.domain.service;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.domain.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberService {
    
    private final MemberRepository memberRepository;
    
    /**
     * Sign up
     */
    @Transactional
    public Long join(Member member) {
        validateDuplicateMember(member); // Check for duplicate members
        memberRepository.save(member);
        return member.getId();
    }

    private void validateDuplicateMember(Member member) {
        List<Member> findMembers = memberRepository.findByName(member.getName());
        if (!findMembers.isEmpty()) {
            throw new IllegalStateException("This member already exists.");
        }
    }
    
    /**
     * Find all members
     */
    public List<Member> findMembers() {
        return memberRepository.findAll();
    }
    
    /**
     * Find one member
     */
    public Member findOne(Long memberId) {
        return memberRepository.findOne(memberId);
    }

    @Transactional
    public void update(Long id, String name) {
        //Member를 반환하면 영속상태가 끊기기 때문에
        //쿼리랑 커맨드랑 분리
        //id 정도만 반환함.
        Member member = memberRepository.findOne(id);
        member.setName(name);
    }
}
