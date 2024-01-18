package com.spring.javagreenS_Skg.pagination;

import lombok.Data;

@Data
public class PageVO {
	private int pag;			// 현재 페이지 번호
	private int pageSize;		// 한 페이지에 표시되는 항목의 수
	private int totRecCnt;		// 전체 레코드(데이터 항목)의 수
	private int totPage;		// 전체 페이지 수
	private int startIndexNo;	// 현재 페이지의 첫 번째 항목의 인덱스 , 0, 5, 10...
	private int curScrStartNo;	// 현재 페이지에서 표시되는 첫 번째 항목의 번호
	private int blockSize;		// 페이징 처리에서 한 번에 표시되는 페이지 번호의 블록 크기
	private int curBlock;		// 현재 블록의 번호
	private int lastBlock;		// 마지막 블록의 번호
	
	private String part;
}
