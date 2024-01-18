package com.spring.javagreenS_Skg.vo;

import lombok.Data;

@Data
public class AsReplyVO {
	private int idx; 			// 댓글의 idx
	private int asIdx; 			// 댓글이 있는 A/S 게시글의 idx(as 테이블 idx을 참조하는 외래 키)
	private String name;		// 작성자 이름
	private String wDate;		// 작성 시간
	private String content;		// 작성 내용
	private int level;
	private int levelOrder;
	
}

/*
	level 
	: 	댓글의 계층 구조, 부모-자식 관계를 표현, 
		부모 댓글의 경우 level이 0이고, 그 자식 댓글은 1, 2, 3, ... 과 같이 증가

	levelOrder
	:	같은 계층의 댓글들 사이에서의 순서
		같은 부모를 가진 자식 댓글들 간의 순서
*/