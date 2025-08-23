package study.jpanplus1.interfaces;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import study.jpanplus1.entity.Member;
import study.jpanplus1.repository.MemberRepository;

import java.util.List;

@RestController
@RequestMapping("/all-members")
public class MemberController {

	private final MemberRepository memberRepository;

	public MemberController(MemberRepository memberRepository) {
		this.memberRepository = memberRepository;
	}

	// ====== findAll() ======

	// (1) findAll + LAZY/EAGER + 연관 접근 X
	@GetMapping("/findall/no-access")
	public List<String> findAllNoAccess() {
		System.out.println("=============== findAll (연관 접근 X) ===============");
		return memberRepository.findAll().stream()
			.map(Member::getName)
			.toList();
	}

	// (2) findAll + LAZY/EAGER + 연관 접근 O
	@GetMapping("/findall/with-access")
	public List<MemberResponse> findAllWithAccess() {
		System.out.println("=============== findAll (연관 접근 O) ===============");
		return memberRepository.findAll().stream()
			.map(MemberResponse::new)
			.toList();
	}


	// ====== JPQL (단순 SELECT m) ======

	// (3) JPQL + 연관 접근 X
	@GetMapping("/jpql/no-access")
	public List<String> jpqlNoAccess() {
		System.out.println("=============== JPQL (연관 접근 X) ===============");
		return memberRepository.findAllWithJpql().stream()
			.map(Member::getName)
			.toList();
	}

	// (4) JPQL + 연관 접근 O
	@GetMapping("/jpql/with-access")
	public List<MemberResponse> jpqlWithAccess() {
		System.out.println("=============== JPQL (연관 접근 O) ===============");
		return memberRepository.findAllWithJpql().stream()
			.map(MemberResponse::new)
			.toList();
	}


	// ====== JPQL + Fetch Join ======

	// (5) Fetch Join + 연관 접근 X
	@GetMapping("/fetch/no-access")
	public List<String> fetchNoAccess() {
		System.out.println("=============== Fetch Join (연관 접근 X) ===============");
		return memberRepository.findAllWithJpqlWithFetchJoin().stream()
			.map(Member::getName)
			.toList();
	}

	// (6) Fetch Join + 연관 접근 O
	@GetMapping("/fetch/with-access")
	public List<MemberResponse> fetchWithAccess() {
		System.out.println("=============== Fetch Join (연관 접근 O) ===============");
		return memberRepository.findAllWithJpqlWithFetchJoin().stream()
			.map(MemberResponse::new)
			.toList();
	}
}
