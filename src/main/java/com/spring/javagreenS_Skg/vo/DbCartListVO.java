package com.spring.javagreenS_Skg.vo;

import lombok.Data;

@Data
public class DbCartListVO {
	private int idx;			// 장바구니 고유번호 
	private String cartDate;	// 장바구니에 상품을 담은 날짜
	private String mid;			// 장바구니에 해당하는 사용자 아이디
	private int productIdx;		// 장바구니에 넣은 상품의 고유번호
	private String productName;	// 장바구니에 넣은 상품의 이름
	private int mainPrice;		// 상품의 기본 가격
	private String thumbImg;	// 서버에 저장된 상품의 메인 이미지
	private String optionIdx;	// 옵션의 고유번호
	private String optionName;	// 옵션명
	private String optionPrice;	// 옵션 가격
	private String optionNum;	// 옵션 수량
	private int totalPrice;		// 구매한 모든 항목의 가격(상품 + 옵션)
}
