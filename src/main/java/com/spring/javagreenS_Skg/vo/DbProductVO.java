package com.spring.javagreenS_Skg.vo;

import lombok.Data;

@Data
public class DbProductVO {
	private int idx;					// 상품고유번호
	private String productCode; 		// 상품고유코드(대분류코드+소분류코드+고유번호)
	private String productName;			// 상품명(상품코드-모델명)
	private String detail;				// 상품의 간단설명(초기화면 출력)
	private String mainPrice;			// 상품의 기본가격
	private String fName;				// 상품 기본사진(1장만 처리)-필수입력
	private String fSName;				// 서버에 저장될 상품의 파일 이름
	private String content;				// 상품의 상세설명(ckeditor를 이용한 이미지 처리)
	
	private String categoryMainCode;	// 대분류코드(A,B,C,...) => 영문 대문자 1자
	private String categoryMainName;	// 대분류명(텐트&타프/테이블/체어...)
	private String categorySubCode;		// 소분류코드(001,002,003,...) => 숫자 3자리를 문자형
	private String categorySubName;		// 소분류명(상품구분 - 텐트&타프:리빙쉘/돔텐트)
	
	private int reviewCount;
	private int qnaCount;
	
	private String searchString;
	
	private String mainName;
}
