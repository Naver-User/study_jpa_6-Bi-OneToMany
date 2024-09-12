package org.zerock.myapp.association;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.FlushModeType;
import javax.persistence.Persistence;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.Timeout;
import org.zerock.myapp.entity.Member;
import org.zerock.myapp.entity.Team;
import org.zerock.myapp.util.PersistenceUnits;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;


//@Log4j2
@Slf4j

@NoArgsConstructor

@TestInstance(Lifecycle.PER_CLASS)
@TestMethodOrder(OrderAnnotation.class)
public class O2MBiDireactionalMappingTests {
	private EntityManagerFactory emf;
	private EntityManager em;
	
	
	@BeforeAll
	void beforeAll() {	// 1회성 전처리
		log.trace("beforeAll() invoked.");
		
		// -- 1 ------------
//		this.emf = Persistence.createEntityManagerFactory(PersistenceUnits.H2);
//		this.emf = Persistence.createEntityManagerFactory(PersistenceUnits.ORACLE);
		this.emf = Persistence.createEntityManagerFactory(PersistenceUnits.MYSQL);
		
		Objects.requireNonNull(this.emf);

		// -- 2 ------------
		this.em = this.emf.createEntityManager();
		assertNotNull(this.em);
		
		this.em.setFlushMode(FlushModeType.COMMIT);
	} // beforeAll
	
	@AfterAll
	void afterAll() {	// 1회성 전처리
		log.trace("afterAll() invoked.");
		
		if(this.em != null) this.em.clear();
		
		try { this.em.close(); } catch(Exception _ignored) {}
		try { this.emf.close();} catch(Exception _ignored) {}
	} // afterAll
	
	
//	@Disabled
	@Order(1)
	@Test
//	@RepeatedTest(1)
	@DisplayName("1. prepareData")
	@Timeout(value=1L, unit = TimeUnit.MINUTES)
	void prepareData() {
		log.trace("prepareData() invoked.");
		
		try {
			this.em.getTransaction().begin();

			
			// -- 1 -------------
			
			// 3개의 팀 생성 및 저장
			IntStream.of(1, 2, 3).forEachOrdered(seq -> {
				Team transientTeam = new Team();
				transientTeam.setName("NAME-"+seq);
				
				this.em.persist(transientTeam);
			});	// .forEachOrdered

			
			// -- 2 -------------
			
			Team team1 = this.em.<Team>find(Team.class, 1L);	// MANAGED
			Team team2 = this.em.<Team>find(Team.class, 2L);	// MANAGED
			Team team3 = this.em.<Team>find(Team.class, 3L);	// MANAGED
			
			Objects.requireNonNull(team1);
			Objects.requireNonNull(team2);
			Objects.requireNonNull(team3);
			
			
			// -- 3 -------------
			
			// 총 20명의 팀원을 생성하되 아래와 같이 소속 팀을 결정합니다:
			// 	(1) >= 15, Set Team3
			// 	(2) >= 8,  Set Team2
			//	(3) >= 1,  Set Team1
			
			IntStream.rangeClosed(1, 20).forEachOrdered(seq -> {
				Member transientMember = new Member();
				transientMember.setName("NAME-"+seq);
				
				if(seq >= 15)		team3.addMember(transientMember);
				else if(seq >= 8)	team2.addMember(transientMember);
				else 				team1.addMember(transientMember);
				
				this.em.persist(transientMember);
			});	// .forEachOrdered
			
			this.em.getTransaction().commit();
		} catch(Exception e) {
			this.em.getTransaction().rollback();
			
			throw e;
		} // try-catch
	} // prepareData
	
	
//	@Disabled
	@Order(2)
	@Test
//	@RepeatedTest(1)
	@DisplayName("2. testOneToManyBiObjectGraphTraverseFromTeamToMembers")
	@Timeout(value=1L, unit = TimeUnit.MINUTES)
	void testOneToManyBiObjectGraphTraverseFromTeamToMembers() {
		log.trace("testOneToManyBiObjectGraphTraverseFromTeamToMembers() invoked.");
		
		// -- 1 --------------
		Team team1 = this.em.<Team>find(Team.class, 1L);
		Team team2 = this.em.<Team>find(Team.class, 2L);
		Team team3 = this.em.<Team>find(Team.class, 3L);
		
		Objects.requireNonNull(team1);
		Objects.requireNonNull(team2);
		Objects.requireNonNull(team3);

		// -- 2 --------------
		assert team1.getMembers() != null;
		team1.getMembers().forEach(m -> log.info(m.toString()));

		// -- 3 --------------
		assert team2.getMembers() != null;
		team2.getMembers().forEach(m -> log.info(m.toString()));

		// -- 4 --------------
		assert team3.getMembers() != null;
		team3.getMembers().forEach(m -> log.info(m.toString()));
	} // testOneToManyBiObjectGraphTraverseFromTeamToMembers
	
	
//	@Disabled
	@Order(3)
	@Test
//	@RepeatedTest(1)
	@DisplayName("4. testOneToManyBiObjectGraphTraverseFromMemberToTeam")
	@Timeout(value=1L, unit = TimeUnit.MINUTES)
	void testOneToManyBiObjectGraphTraverseFromMemberToTeam() {
		log.trace("testOneToManyBiObjectGraphTraverseFromMemberToTeam() invoked.");
		
		LongStream.rangeClosed(1L, 20L).forEachOrdered(seq -> {
			Member foundMember = this.em.<Member>find(Member.class, seq);
			
			Objects.requireNonNull(foundMember);
			log.info("\t+ foundMember: {}", foundMember);
		});
	} // testOneToManyBiObjectGraphTraverseFromMemberToTeam
	
	
	

} // end class
