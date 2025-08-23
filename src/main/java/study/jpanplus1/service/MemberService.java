package study.jpanplus1.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import study.jpanplus1.entity.Member;

import study.jpanplus1.repository.MemberRepository;

@Service
public class MemberService {

	private final MemberRepository memberRepository;

	public MemberService(MemberRepository memberRepository) {
		this.memberRepository = memberRepository;
	}

	@Transactional
	public Long join(Member member) {
		memberRepository.save(member);
		return member.getId();
	}
}
