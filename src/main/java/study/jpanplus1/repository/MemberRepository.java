package study.jpanplus1.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import study.jpanplus1.entity.Member;

public interface MemberRepository extends JpaRepository<Member, Long> {

	@Query("SELECT m FROM Member m")
	List<Member> findAllWithJpql();

	@Query("SELECT m FROM Member m JOIN FETCH m.team JOIN FETCH m.major")
	List<Member> findAllWithJpqlWithFetchJoin();
}
