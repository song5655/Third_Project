package com.spring.javagreenS_Skg.vo;

import lombok.Data;

@Data
public class DbBaesongVO {
	// dbbaesong2 테이블의 컴럼
	private int idx;				// 배송 고유 번호
	private int oIdx;				// 주문 고유 번호(주문 테이블의 idx 참조하는 외래 키)
	private String orderIdx;		// 주문 고유번호(생성한 주문 고유번호)
	private int orderTotalPrice;	// 주문한 모든 상품의 총 가격
	private String mid;				// 회원 아이디
	private String name;			// 배송지 받는사람 이름
	private String address;			// 배송지 (우편번호)주소
	private String tel;				// 받는사람 전화번호
	private String message;			// 배송 요청사항
	private String payment;			// 결제 도구
	private String payMethod;		// 결제 도구에 따른 방법(카드번호)
	private String orderStatus;		// 주문 상태(결제완료->배송중->배송완료->구매완료)
	
	// dborder2 테이블의 컬럼
	private int productIdx;			// 상품 고유 번호
	private String orderDate;		// 주문을 한 날짜
	private String productName;		// 상품명
	private int mainPrice;			// 상품 가격
	private String thumbImg;		// 서버에 저장된 상품의 메인 이미지
	private String optionName;		// 옵션명
	private String optionPrice;		// 옵션 가격
	private String optionNum;		// 옵션 수량
	private int totalPrice;			// 구매한 모든 항목의 가격(상품 + 옵션)
}
