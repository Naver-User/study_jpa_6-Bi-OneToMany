package org.zerock.myapp.entity;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.zerock.myapp.listener.CommonEntityLifecyleListener;

import lombok.Data;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;


//@Log4j2
@Slf4j

@Data

@EntityListeners(CommonEntityLifecyleListener.class)

@Entity(name = "Team")
@Table(name = "team")
public class Team implements Serializable {	
	@Serial private static final long serialVersionUID = 1L;

	// 1. Set PK
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "team_id")	// Set mapping column name
	private Long id;			// PK
	
	
	// 2. Set Generals
	@Basic(optional = false)	// Not Null Constraint
	private String name;
	
	
	
	// =============================================
	// OneToMany (1:N), Bi-directional Association
	// =============================================
	
	// mappedBy 속성으로 연관관계의 주인을 설정하지 말라!!! (***)
	// 또한, 애시당초 설정할 수도 없다.
	// 이유는, 애시당초 OneToMany(1:N)관계에서는,
	// 자식 엔티티 타입에 FK속성 자체가 없기때문
//	@OneToMany								// 1
	@OneToMany(targetEntity = Member.class)	// 2, 첫번째 편법 (추천방식)
	
	// 3, 두번째 편법 (비추천방식) - 
	//    (*주의사항*) 아래의 @JoinColumn 어노테이션이 지정되면 안됩니다.!!
//	@OneToMany(targetEntity = Member.class, mappedBy = "team")	
	
	// 2가지 편법 중에, 권장하는 방법으로 아래와 같이 매핑합니다.
	@JoinColumn(name = "my_team", referencedColumnName = "team_id")
	
	@ToString.Exclude
	private List<Member> members = new ArrayList<>();
	
		
	// 일대다 양방향 관계에서는, 편법이긴 하지만, 위와같이
	// Children이 있다면, 특정 팀(Parent)에 소속된 멤버(Child)를
	// 올바르게 넣어줄 수 있는 Setter 메소드가 필요하게 됩니다.
	
	public void addMember(Member newMember) {
		log.trace("addMember({}) invoked.", newMember);
		
		// Append new member into the children (List<Member>)
		this.members.add(newMember);
		// Set initial value into the *read-only FK property in the Child entity.
		newMember.setTeam(this);
	} // addMember
	
	
	
} // end class

