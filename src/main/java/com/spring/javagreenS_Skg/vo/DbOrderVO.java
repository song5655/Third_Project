package com.spring.javagreenS_Skg.vo;

import lombok.Data;

@Data
public class DbOrderVO {
  private int idx;				// 고유번호
  private String orderIdx;		// 주문 고유번호
  private String mid;			// 주문한 회원의 아이디
  private int productIdx;		// 상품 고유 번호
  private String orderDate;		// 주문을 한 날짜
  private String productName;	// 상품명
  private int mainPrice;		// 상품 가격
  private String thumbImg;		// 서버에 저장된 상품의 메인 이미지
  private String optionName;	// 옵션명
  private String optionPrice;	// 옵션 가격
  private String optionNum;		// 옵션 수량
  private int totalPrice;		// 구매한 모든 항목의 가격(상품 + 옵션)
  
  private int cartIdx;  		// 장바구니 고유번호
  private int maxIdx;   		// 주문번호를 구하기위한 기존 최대 비밀번호필드
  private int baesong;  		// 배송비저장필드
  
  private String orderStatus;
  
  private int diffTime;
}
