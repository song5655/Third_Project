package com.spring.javagreenS_Skg.vo;

import lombok.Data;

@Data
public class DbProductQnAVO {
	private int idx;			// 댓글/답글의 고유번호, AUTO_INCREMENT
	private int productIdx;		// dbproduct 테이블의 productIdx 값을 참조하는 외래 키
	private String mid;			// 댓글/답글 작성자의 mid
	private String wDate;		// 댓글/답글 작성 시각
	private String content;		// 댓글/답글 내용
	private int level;
	private int levelOrder;
	
	private int diffTime;		// 작성일과 현재 시각의 차이 저장
}

/*
level 
: 	댓글의 계층 구조, 부모-자식 관계를 표현, 

levelOrder
:	같은 계층의 댓글들 사이에서의 순서
	같은 부모를 가진 자식 댓글들 간의 순서
*/