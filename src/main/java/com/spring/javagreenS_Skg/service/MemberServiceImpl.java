package com.spring.javagreenS_Skg.service;

import java.awt.image.BufferedImage;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.UUID;

import javax.imageio.ImageIO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageConfig;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.spring.javagreenS_Skg.dao.MemberDAO;
import com.spring.javagreenS_Skg.vo.MemberVO;

@Service
public class MemberServiceImpl implements MemberService{

	@Autowired
	MemberDAO memberDAO;

	// 아이디중복체크
	@Override
	public MemberVO getMemIdCheck(String mid) {
		return memberDAO.getMemIdCheck(mid);
	}
	
	// 회원가입
	@Override
	public int setMemInputOk(MemberVO vo) {
		return memberDAO.setMemInputOk(vo);
	}
	
	// 마지막 접속일
	@Override
	public void setMemberVisitProcess(MemberVO vo) {
		memberDAO.setMemberVisitProcess(vo);
	}
	
	@Override
	public void setMemPwdChange(String changePwd, String mid) {
		memberDAO.setMemPwdChange(changePwd, mid);
	}
	
	@Override
	public void setMemUpdateOk(MemberVO vo) {
		memberDAO.setMemUpdateOk(vo);
	}
	
	@Override
	public void setMemDeleteOk(String mid) {
		memberDAO.setMemDeleteOk(mid);
	}
	
	@Override
	public MemberVO getMemEmailFind(String name, String email) {
		return memberDAO.getMemEmailFind(name, email);
	}
	
	@Override
	public MemberVO getMemTelFind(String name, String tel) {
		return memberDAO.getMemTelFind(name, tel);
	}
	
	@Override
	public MemberVO getMemPwdFind(String mid, String name, String toMail) {
		return memberDAO.getMemPwdFind(mid, name, toMail);
	}
	
	@Override
	public void setPwdChange(String mid, String pwd) {
		memberDAO.setPwdChange(mid, pwd);
	}
	
	@Override
	public ArrayList<MemberVO> getAdminMemberLevelList(int startIndexNo, int pageSize, int level) {
		return memberDAO.getAdminMemberLevelList(startIndexNo, pageSize, level);
	}
	
	@Override
	public ArrayList<MemberVO> getAdminMemberMidList(int startIndexNo, int pageSize, String mid) {
		return memberDAO.getAdminMemberMidList(startIndexNo, pageSize, mid);
	}
	
	@Override
	public MemberVO getMemInfor(int idx) {
		return memberDAO.getMemInfor(idx);
	}
	
	@Override
	public void setAdminLevelUpdate(int idx, int level) {
		memberDAO.setAdminLevelUpdate(idx, level); 
	}
	
	@Override
	public MemberVO getMemberInfor(String mid) {
		return memberDAO.getMemberInfor(mid);
	}
	
	@Override
	public void setMemberReset(int idx) {
		memberDAO.setMemberReset(idx);
	}
	
	@Override
	public String getTodayVisitDate() {
		return memberDAO.getTodayVisitDate();
	}
	
	@Override
	public void setTodayVisitCountInsert() {
		memberDAO.setTodayVisitCountInsert();
	}
	
	@Override
	public void setTodayVisitCountUpdate(String strToday) {
		memberDAO.setTodayVisitCountUpdate(strToday);
	}
	
	@SuppressWarnings("unused")
	@Override
	public String setQrCode(String uploadPath, String moveUrl, int idx) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhhmm");
		UUID uid = UUID.randomUUID();
		String strUid = uid.toString().substring(0,13);
		String qrCode = strUid;
		
	  try {
	      File file = new File(uploadPath);		// qr코드 이미지를 저장할 디렉토리 지정
	      if(!file.exists()) {
	          file.mkdirs();
	      }
	      String codeurl = new String(moveUrl.getBytes("UTF-8"), "ISO-8859-1");	// qr코드 인식시 이동할 url 주소
	      //int qrcodeColor = 0xFF2e4e96;			// qr코드 바코드 생성값(전경색)
	      int qrcodeColor = 0xFF000000;			// qr코드 바코드 생성값(전경색) - 뒤의 6자리가 색상코드임
	      int backgroundColor = 0xFFFFFFFF;	// qr코드 배경색상값
	      
	      QRCodeWriter qrCodeWriter = new QRCodeWriter();
	      BitMatrix bitMatrix = qrCodeWriter.encode(codeurl, BarcodeFormat.QR_CODE,200, 200);
	      
	      MatrixToImageConfig matrixToImageConfig = new MatrixToImageConfig(qrcodeColor,backgroundColor);
	      BufferedImage bufferedImage = MatrixToImageWriter.toBufferedImage(bitMatrix,matrixToImageConfig);
	      
	      ImageIO.write(bufferedImage, "png", new File(uploadPath + qrCode + ".png"));		// ImageIO를 사용한 바코드 파일쓰기
	      
	      // qr코드 생성후 정보를 DB에 저장하기(신상내역으로 보낸것들만 저장하려함 - 나중에 본인이 생성된 qr코드 가져왔을때 DB에 있는 정보와 일치하는지 알아보기 위함)
	      memberDAO.setQrCode(qrCode+".png", idx);
	  } catch (Exception e) {
	      e.printStackTrace();
	  }
	  return qrCode;
	}
	
}
