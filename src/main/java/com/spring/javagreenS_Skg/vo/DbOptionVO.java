package com.spring.javagreenS_Skg.vo;

import lombok.Data;

@Data
public class DbOptionVO {
	private int idx;			// 옵션 고유 번호
	private int productIdx;		// 상품 고유 번호(상품의 idx 참조하는 외래 키)
	private String optionName;	// 옵션 이름 
	private int optionPrice;	// 옵션 가격
}
