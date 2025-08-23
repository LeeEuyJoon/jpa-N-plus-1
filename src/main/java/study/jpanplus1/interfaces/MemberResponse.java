package study.jpanplus1.interfaces;

import study.jpanplus1.entity.Member;

public class MemberResponse {
	private Long id;
	private String name;
	private String teamName;
	private String majorName;

	public MemberResponse(Member member) {
		this.id = member.getId();
		this.name = member.getName();
		this.teamName = member.getTeam().getName();
		this.majorName = member.getMajor().getName();
	}

	public Long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getTeamName() {
		return teamName;
	}

	public String getMajorName() {
		return majorName;
	}
}
