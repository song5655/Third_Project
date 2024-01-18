package com.spring.javagreenS_Skg.vo;

import lombok.Data;

@Data
public class AsVO {
	private int idx; 				// A/S 게시글의 idx
	private String name; 			// 작성자 이름
	private String pwd; 			// 4자리 비밀번호(ARIA)
	private String tel;				// 작성자 전화번호
	private String email;			// 작성자 이메일
	private String productName;		// 상품명(작성자가 작성)
	private String place;			// 구입처(작성자가 작성)
	private String purchaseDate;	// 구매일(작성자가 작성)
	private String wDate;			// A/S 게시글 작성일
	private String title;			// 게시글 제목
	private String content;			// 게시글 내용
	private String open;			// 공개여부('NO' | 'OK')
	private String sw;				// 답변완료('NO' | 'OK')
	private String mid;				// 작성자 mid
	
	private int diffTime;			// 작성일과 현재 시각의 차이 저장 
	private int replyCount;			// 댓글의 개수를 저장
}
