show tables;

/* 대분류(categoryMain2) */
create table categoryMain2 (
  categoryMainCode  char(1)  not null,			/* 대분류코드(A,B,C,... => 영문 대문자 1자 */
  categoryMainName  varchar(20) not null,		/* 대분류명(텐트&타프/ 테이블/ 체어) */
  primary key(categoryMainCode),
  unique key(categoryMainName)
);

/* 소분류(categorySub2) */
create table categorySub2 (
  categoryMainCode  char(1) not null,		/* 대분류코드를 외래키로 지정 */
  categorySubCode	char(3) not null,		/* 소분류코드(001,002,003,... => 숫자 3자리를 문자형으로) */
  categorySubName	varchar(20) not null,	/* 소분류명(상품구분 - 텐트&타프:리빙쉘/돔텐트, 타프/타프웰, 팝업/그늘막) */
  primary key(categorySubCode),
  foreign key(categoryMainCode)   references categoryMain2(categoryMainCode)
);

/* 상품 테이블(dbProduct2) */
create table dbProduct2 (
  idx  				int not null,			/* 상품 고유번호 */
  categoryMainCode  char(1) not null,		/* 대분류코드 외래키지정 */
  categorySubCode   char(3) not null,		/* 소분류코드 외래키지정 */
  productCode 		varchar(20)  not null,	/* 상품고유코드(대분류코드+소분류코드+고유번호) */
  productName		varchar(50)  not null,	/* 상품명(상품코드-모델명) - 세분류 */
  detail			varchar(100) not null,	/* 상품의 간단설명(초기화면 출력) */
  mainPrice			int not null,			/* 상품의 기본가격 */
  fName				varchar(100) not null,	/* 상품 기본사진(1장만 처리)-필수입력 */
  fSName			varchar(100) not null,	/* 서버에 저장될 상품의 고유파일이름 */
  content			text not null,			/* 상품의 상세설명 - ckeditor를 이용한 이미지 처리 */
  primary key(idx, productCode),
  foreign key(categoryMainCode) references categorySub2(categoryMainCode),
  foreign key(categorySubCode)  references categorySub2(categorySubCode)
);

/*리뷰테이블(productReview2)*/
create table productReview2(
	idx 		int not null auto_increment, 	/*글의 고유번호*/
	productIdx 	int not null,           		/*원본글(상품)의 고유번호(외래키로 지정)*/
	mid 		varchar(20) not null,   		/*댓글 올린이의 아이디*/
	wDate 	 	datetime default now(),   		/*후기 올린 날짜*/
	content  	text not null,           		/*후기 내용*/
	primary key(idx),                			/*주키(기본키)는 idx*/
	foreign key(productIdx) references dbProduct2(idx)
);

/*문의테이블(productQnA2)*/
create table productQnA2(
	idx 		 	int not null auto_increment, 	/*글의 고유번호*/
	productIdx 		int not null,           		/*원본글(상품)의 고유번호(외래키로 지정)*/
	mid 			varchar(20) not null,   		/*댓글 올린이의 아이디*/
	wDate 	 		datetime default now(),    		/*문의글 올린 날짜*/
	content  		text not null,           		/*문의글 내용 내용*/
	primary key(idx),
	foreign key(productIdx) references dbProduct2(idx)
);

ALTER TABLE productQnA2 ADD level int NOT NULL DEFAULT '0';
ALTER TABLE productQnA2 ADD levelOrder int NOT NULL DEFAULT '0';

/* 상품 옵션(dbOption2) */
create table dbOption2 (
  idx 			int not null auto_increment,	/* 옵션 고유번호 */
  productIdx  	int not null,					/* dbProduct2테이블의 고유번호 */
  optionName  	varchar(50) not null,			/* 옵션 이름 */
  optionPrice 	int not null default 0,			/* 옵션 가격 */
  primary key(idx),
  foreign key(productIdx) references dbProduct2(idx)
);

/* ================ 상품 주문 시작 시에 사용하는 테이블 ==================== */

/* 장바구니 테이블 */
create table dbCartList2 (
  idx   		int not null auto_increment,	/* 장바구니 고유번호 */
  cartDate 		datetime default now(),			/* 장바구니에 상품을 담은 날짜 */
  mid   		varchar(20) not null,			/* 장바구니를 사용한 사용자의 아이디 - 로그인한 회원 아이디 */
  productIdx  	int not null,					/* 장바구니에 구입한 상품의 고유번호 */
  productName 	varchar(50) not null,			/* 장바구니에 담은 구입한 상품명 */
  mainPrice   	int not null,					/* 메인상품의 기본 가격 */
  thumbImg		varchar(100) not null,			/* 서버에 저장된 상품의 메인 이미지 */
  optionIdx	  	varchar(50)	 not null,			/* 옵션의 고유번호리스트(여러개가 될수 있기에 문자열 배열로 처리한다.) */
  optionName  	varchar(100) not null,			/* 옵션명 리스트(배열처리) */
  optionPrice 	varchar(100) not null,			/* 옵션가격 리스트(배열처리) */
  optionNum		varchar(50)  not null,			/* 옵션수량 리스트(배열처리) */
  totalPrice  	int not null,					/* 구매한 모든 항목(상품과 옵션포함)에 따른 총 가격 */
  primary key(idx, mid),
  foreign key(productIdx) references dbProduct2(idx) on update cascade on delete restrict
);

/* 주문 테이블 -  */
create table dbOrder2 (
  idx         int not null auto_increment, 	/* 고유번호 */
  orderIdx    varchar(15) not null,   		/* 주문 고유번호(새롭게 만들어 주어야 한다.) */
  mid         varchar(20) not null,   		/* 주문자 ID */
  productIdx  int not null,           		/* 상품 고유번호 */
  orderDate   datetime default now(), 		/* 실제 주문을 한 날짜 */
  productName varchar(50) not null,   		/* 상품명 */
  mainPrice   int not null,				    /* 메인 상품 가격 */
  thumbImg    varchar(100) not null,   		/* 썸네일(서버에 저장된 메인상품 이미지) */
  optionName  varchar(100) not null,  		/* 옵션명    리스트 -배열로 넘어온다- */
  optionPrice varchar(100) not null,  		/* 옵션가격  리스트 -배열로 넘어온다- */
  optionNum   varchar(50)  not null,  		/* 옵션수량  리스트 -배열로 넘어온다- */
  totalPrice  int not null,					/* 구매한 상품 항목(상품과 옵션포함)에 따른 총 가격 */
  primary key(idx, orderIdx),
  foreign key(productIdx) references dbProduct2(idx)  on update cascade on delete cascade
);

/* 배송테이블 */
create table dbBaesong2 (
  idx     			int not null auto_increment,
  oIdx    			int not null,								/* 주문테이블의 고유번호를 외래키로 지정함 */
  orderIdx    		varchar(15) not null,   					/* 주문 고유번호 */
  orderTotalPrice 	int not null,   							/* 주문한 모든 상품의 총 가격 */
  mid         		varchar(20) not null,   					/* 회원 아이디 */
  name				varchar(20) not null,   					/* 배송지 받는사람 이름 */
  address     		varchar(100) not null,  					/* 배송지 (우편번호)주소 */
  tel				varchar(15),								/* 받는사람 전화번호 */
  message     		varchar(100),								/* 배송시 요청사항 */
  payment			varchar(10) not null,						/* 결제도구 */
  payMethod   		varchar(50) not null,  						/* 결제도구에 따른 방법(카드번호) */
  orderStatus 		varchar(10) not null default '결제완료', 		/* 주문순서(결제완료->배송중->배송완료->구매완료) */
  primary key(idx),
  foreign key(oIdx) references dbOrder2(idx) on update cascade on delete cascade
);