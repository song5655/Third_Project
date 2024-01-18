package com.spring.javagreenS_Skg.vo;

import lombok.Data;

@Data
public class MemberVO {
	private int idx;			// 회원 고유번호(auto_increment)
	private String mid;			// 회원 아이디(중복 불허)
	private String pwd;			// 비밀번호(Spring Security)
	private String name;		// 회원 성명
	private String tel;			// 회원 전화번호
	private String email;		// 회원 이메일
	private String address;		// 회원 주소
	private String startDate;	// 최초 가입일
	private String lastDate;	// 마지막 접속일
	private String userDel;		// 회원 탈퇴 신청 여부('OK' | 'NO')
	private int level;			// 회원 등급
	private int point;			// 회원 포인트

	private String strLevel; 	// 회원등급을 문자로 저장하는 필드
	
	private int applyDiff; 		// 날짜 차이를 저장하는 필드
}
