package com.spring.javagreenS_Skg.vo;

import lombok.Data;

@Data
public class DbProductReviewVO {
	private int idx;			// 후기 고유 번호
	private int productIdx;		// 후기를 작성한 상품의 고유 번호
	private String mid;			// 후기 작성자의 아이디
	private String wDate;		// 후기 작성일
	private String content;		// 후기 내용
	
	private String diffTime;	// 작성일과 현재 시각의 차이 저장
}

