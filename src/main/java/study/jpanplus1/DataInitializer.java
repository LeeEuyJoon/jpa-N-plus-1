package study.jpanplus1;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import study.jpanplus1.entity.Major;
import study.jpanplus1.entity.Member;
import study.jpanplus1.entity.Team;
import study.jpanplus1.repository.MajorRepository;
import study.jpanplus1.repository.MemberRepository;
import study.jpanplus1.repository.TeamRepository;

@Component
public class DataInitializer implements CommandLineRunner {

	private final MemberRepository memberRepository;
	private final TeamRepository teamRepository;
	private final MajorRepository majorRepository;
	private final int MEMBER_NUMBER = 3;

	public DataInitializer(MemberRepository memberRepository,
		TeamRepository teamRepository,
		MajorRepository majorRepository) {
		this.memberRepository = memberRepository;
		this.teamRepository = teamRepository;
		this.majorRepository = majorRepository;
	}

	@Override
	public void run(String... args) throws Exception {
		for (int i = 1; i <= MEMBER_NUMBER; i++) {
			// 팀 생성
			Team team = new Team();
			team.setName("Team " + i);
			teamRepository.save(team);

			// 전공 생성
			Major major = new Major();
			major.setName("Major " + i);
			majorRepository.save(major);

			// 멤버 생성
			Member member = new Member();
			member.setName("member" + i);
			member.setTeam(team);
			member.setMajor(major);

			memberRepository.save(member);
		}

		System.out.println(">>> 초기 데이터 저장 완료 (멤버/팀/전공 각각 3개씩)");
	}
}
