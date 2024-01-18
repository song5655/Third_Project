package com.spring.javagreenS_Skg.vo;

import lombok.Data;

@Data
public class NoticeVO {
	private int idx;			// 글 고유번호
	private String name;		// 작성자
	private String title;		// 글 제목
	private String content;		// 글 내용
	private String wDate;		// 작성일
	private int readNum;		// 조회 수
	private String mid;			// 회원 아이디
	private int pin;			// 게시물 상단고정('1' | '0')
	
	private int diffTime;		// 작성일과 현재 시각의 차이 저장
}
