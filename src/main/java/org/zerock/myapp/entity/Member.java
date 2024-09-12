package org.zerock.myapp.entity;

import java.io.Serial;
import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.zerock.myapp.listener.CommonEntityLifecyleListener;

import lombok.Data;


@Data

@EntityListeners(CommonEntityLifecyleListener.class)

@Entity(name = "Member")
@Table(name = "member")
public class Member implements Serializable {
	@Serial private static final long serialVersionUID = 1L;

	// 1. Set PK
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "member_id")
	private Long id;
	
	
	// 2. Set Generals.
	@Basic(optional = false, fetch = FetchType.EAGER)	// Not Null Constraint, Eager Loading
	private String name;
	
	
	
	// =============================================
	// OneToMany (1:N), Bi-directional Association
	// =============================================
	
//	@ManyToOne								// 1
//	@ManyToOne(targetEntity = Team.class)	// 2
	
	// 3
	@ManyToOne(
		optional = true,			// This means `Null Relationship`.
		targetEntity = Team.class,	// Set Parent (1)'s entity type.
		fetch = FetchType.EAGER,
		cascade = CascadeType.ALL
	)
	
	@JoinColumn(
		name = "my_team", 
		referencedColumnName = "team_id" ,
		
		// 이 FK 속성의 값은 외부에서 수정할 수 없는, 
		// 읽기전용속성으로 만들어 놓습니다.
		// 이 설정은 어디까지나, 일대다(1:N) 양방향 연관관계를 위한 편법 설정입니다.
		insertable = false, 
		updatable = false)
	
	private Team team;		// FK, Set *Read-only property.
		   
	
	
} // end class


