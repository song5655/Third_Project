package com.spring.javagreenS_Skg;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.spring.javagreenS_Skg.pagination.PageProcess;
import com.spring.javagreenS_Skg.pagination.PageVO;
import com.spring.javagreenS_Skg.service.AsService;
import com.spring.javagreenS_Skg.service.DbShopService;
import com.spring.javagreenS_Skg.service.MemberService;
import com.spring.javagreenS_Skg.vo.AsVO;
import com.spring.javagreenS_Skg.vo.DbProductQnAVO;
import com.spring.javagreenS_Skg.vo.DbProductReviewVO;
import com.spring.javagreenS_Skg.vo.MemberVO;

@Controller
@RequestMapping("/member")
public class MemberController {
	
	@Autowired
	MemberService memberService;
	
	@Autowired
	BCryptPasswordEncoder passwordEncoder;
	
	@Autowired
	JavaMailSender mailSender;
	
	@Autowired
	DbShopService dbShopService;
	
	@Autowired
	AsService asService;
	
	@Autowired
	PageProcess pageProcess;
	
	
	// 회원가입창으로 이동
	@RequestMapping(value="/memJoin", method=RequestMethod.GET)
	public String memJoinGet() {
		return "member/memJoin";
	}
	
	// 회원가입
	@RequestMapping(value="/memJoin", method= RequestMethod.POST)
	public String memJoinPost(MemberVO vo) {
		// ID 중복 체크
		if(memberService.getMemIdCheck(vo.getMid())!= null) {
			return "redirect:/msg/MemIdCheckNo";
		}
		// Spring Security의 PasswordEncoder를 사용하여 비밀번호를 암호화
		vo.setPwd(passwordEncoder.encode(vo.getPwd()));
		
		// memJoin.jsp의 JS 입력 값 유효성 검사, ID 중복 체크 완료 후 DB 접근
		int res = memberService.setMemInputOk(vo);	
		
		if(res==1) return "redirect:/msg/memInputOk";
		
		else return "redirect:/msg/memInputNo";
	}
	
	// 회원가입 ID 중복체크(Ajax 처리)
	@ResponseBody
	@RequestMapping(value = "/memIdCheck", method = RequestMethod.POST)
	public String memIdCheckPost(@RequestParam String mid) {
	    MemberVO vo = memberService.getMemIdCheck(mid);
	    return (vo != null) ? "1" : "0";
	}
	
	// 로그인창으로 이동
	@RequestMapping(value="/memLogin", method=RequestMethod.GET)
	public String memLoginGet(HttpServletRequest request) {
		// 로그인폼 호출 시 기존에 저장된 쿠키가 있다면 불러와서 mid에 담아 넘김
		Cookie[] cookies = request.getCookies();
		String mid = "";
		for(int i=0; i<cookies.length; i++) {
			if(cookies[i].getName().equals("cMid")) {
				mid = cookies[i].getValue();
				request.setAttribute("mid", mid);
				break;
			}
		}	
		return "member/memLogin";
	}

	// 로그인
	@RequestMapping(value = "/memLogin", method = RequestMethod.POST)
	public String memLoginPost(Model model,
	                           HttpServletRequest request,
	                           HttpServletResponse response,
	                           String mid,
	                           String pwd,
	                           @RequestParam(name = "idCheck", defaultValue = "", required = false) String idCheck,
	                           HttpSession session) {

	    MemberVO vo = memberService.getMemIdCheck(mid);

	    if (vo != null && passwordEncoder.matches(pwd, vo.getPwd()) && vo.getUserDel().equals("NO")) {
	        String strLevel = getStrLevel(vo.getLevel());

	        model.addAttribute("mid", mid);
	        session.setAttribute("sMid", mid);
	        session.setAttribute("sName", vo.getName());
	        session.setAttribute("sLevel", vo.getLevel());
	        session.setAttribute("sStrLevel", strLevel);

	        // 아이디 저장(쿠키 관리)
	        handleRememberMe(idCheck, mid, response, request);
	        // 마지막 접속일 최신화
	        memberService.setMemberVisitProcess(vo);
	        // 일자별 방문횟수 최신화
	        handleTodayVisitCount();
	        // 접속자의 장바구니 수량 반환
	        int count = dbShopService.getCartCount(mid);
	        session.setAttribute("sCount", count);
	        
	        return "redirect:/msg/memLoginOk";
	    } else {
	        return "redirect:/msg/memLoginNo";
	    }
	}

	private String getStrLevel(int level) {
	    switch (level) {
	        case 0:
	            return "관리자";
	        case 1:
	            return "일반회원";
	        case 2:
	            return "GOLD";
	        case 3:
	            return "VIP";
	        default:
	            return "";
	    }
	}

	private void handleRememberMe(String idCheck,
			                      String mid, 
			                      HttpServletResponse response, 
			                      HttpServletRequest request ) {
	    if (idCheck.equals("on")) {
	        Cookie cookie = new Cookie("cMid", mid);
	        cookie.setMaxAge(60 * 60 * 24 * 7);   // 쿠키 만료시간 7일(단위:초)
	        response.addCookie(cookie);
	    } else {
	        Cookie[] cookies = request.getCookies();
	        for (int i = 0; i < cookies.length; i++) {
	            if (cookies[i].getName().equals("cMid")) {
	                cookies[i].setMaxAge(0); 	  // 기존에 저장된 쿠키 삭제
	                response.addCookie(cookies[i]);
	                break;
	            }
	        }
	    }
	}

    // 일자별 방문횟수 최신화
	private void handleTodayVisitCount() {
	    String visitDate = memberService.getTodayVisitDate();
	    Date today = new Date();
	    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	    String strToday = sdf.format(today);

	    if (!strToday.equals(visitDate)) {
	        memberService.setTodayVisitCountInsert();
	    } else {
	        memberService.setTodayVisitCountUpdate(strToday);
	    }
	}

	// 로그아웃
	@RequestMapping(value="/memLogout", method=RequestMethod.GET)
	public String memLogoutGet(HttpSession session, Model model) {
		String mid = (String)session.getAttribute("sMid");
		session.invalidate();
		
		model.addAttribute("mid",mid);
		return "redirect:/msg/memLogout";
	}
	
	// 비밀번호 변경창으로 이동
	@RequestMapping(value="/memPwdChange", method=RequestMethod.GET)
	public String memPwdChangeGet(HttpSession session, Model model) {
		String mid = (String)session.getAttribute("sMid");
		
		model.addAttribute("mid",mid);
		return "member/memPwdChange";
	}
	
	// 비밀번호 변경
	@RequestMapping(value="/memPwdChange", method=RequestMethod.POST)
	public String memPwdChangePost(String pwd, String newPwd, HttpSession session, Model model) {
		String mid = (String)session.getAttribute("sMid");
		MemberVO vo = memberService.getMemIdCheck(mid);
		
		if(vo != null && passwordEncoder.matches(pwd, vo.getPwd())) {

			// 기존 비밀번호가 일치하면 새로운 비밀번호 암호화
			String changePwd = passwordEncoder.encode(newPwd);
			memberService.setMemPwdChange(changePwd, mid);	
			
			return "redirect:/msg/memPwdChangeOK";
		} 
		else {
			return "redirect:/msg/memPwdChangeNO";
		}
	}
	
	// 회원정보수정 전 비밀번호 확인
	@RequestMapping(value="/memPwdCheck", method=RequestMethod.GET)
	public String memPwdCheckGet() {
		return "member/memPwdCheck";
	}
	
	// 회원정보수정 전 비밀번호 확인
	@RequestMapping(value="/memPwdCheck", method=RequestMethod.POST)
	public String memPwdCheckPost(String pwd, HttpSession session, Model model) {
	    String mid = (String) session.getAttribute("sMid");
	    MemberVO vo = memberService.getMemIdCheck(mid);

	    if (isValidPassword(vo, pwd)) {
	        session.setAttribute("sPwd", pwd);
	        model.addAttribute("vo", vo);
	        return "member/memUpdate";
	    } else {
	        return "redirect:/msg/memPwdCheckNo";
	    }
	}

	private boolean isValidPassword(MemberVO vo, String inputPwd) {
	    return vo != null && passwordEncoder.matches(inputPwd, vo.getPwd());
	}

	// 회원정보수정
	@RequestMapping(value="/memUpdateOk", method=RequestMethod.POST)
	public String memUpdatePost(MemberVO vo) {
		memberService.setMemUpdateOk(vo);
		return "redirect:/msg/memUpdateOk";
	}
	
	// 회원탈퇴
	@RequestMapping(value="/memDeleteOk", method=RequestMethod.GET)
	public String memDeleteOkGet(HttpSession session, Model model) {
		String mid = (String) session.getAttribute("sMid");
		
		memberService.setMemDeleteOk(mid);
		
		session.invalidate();
		model.addAttribute("mid",mid);
		
		return"redirect:/msg/memDeleteOk";
	}
	
	// 아이디찾기 폼
	@RequestMapping(value="/memIdFind",method=RequestMethod.GET)
	public String memIdFindGet() {
		return "member/memIdFind";
	}
	
	// 아이디찾기(email)
	@RequestMapping(value="/memIdEmailFindOk",method=RequestMethod.POST)
	public String memIdEmailFindPost(String name, String email, Model model) {
		MemberVO vo = memberService.getMemEmailFind(name,email);

		if(vo != null) {
			model.addAttribute("vo",vo);
			return "member/memIdFindOk";
		}
		else {
			return "redirect:/msg/memIdFindNo";
		}
	}
	
	// 아이디찾기(tel)
	@RequestMapping(value="/memIdTelFindOk",method=RequestMethod.POST)
	public String memIdTelFindPost(String name, String tel, Model model) {
		System.out.println("name : "+ name);
		System.out.println("tel : "+ tel);
		MemberVO vo = memberService.getMemTelFind(name,tel);
		
		if(vo != null) {
			model.addAttribute("vo",vo);
			return "member/memIdFindOk";
		}
		else {
			return "redirect:/msg/memIdFindNo";
		}
	}
	
	// 비밀번호찾기 폼
	@RequestMapping(value="/memPwdFind",method=RequestMethod.GET)
	public String memPwdFindGet() {
		return "member/memPwdFind";
	}
	
	// 비밀번호찾기 임시비밀번호 발급(email전송)
	@RequestMapping(value="/memPwdSearchOk", method=RequestMethod.GET)
	public String memPwdSearchOkGet(String mid, String name, String toMail) {
	    MemberVO vo = memberService.getMemPwdFind(mid, name, toMail);
	    
	    if (vo != null) {
	    	// UUID를 활용한 8자리 임시비밀번호 생성
	        UUID uid = UUID.randomUUID();
	        String pwd = uid.toString().substring(0, 8);
	        String content = pwd;
	        
	        // 생성한 임시비밀번호를 사용자의 이메일로 발송
	        String res = mailSend(toMail, content);

	        if (res.equals("1")) {
	            // 이메일 전송이 성공한 경우에만 DB 값을 최신화
	            memberService.setPwdChange(mid, passwordEncoder.encode(pwd));
	            return "redirect:/msg/memIdPwdSearchOk";
	        } else {
	            return "redirect:/msg/memIdPwdSearchNo";
	        }
	    } else {
	        return "redirect:/msg/memIdPwdSearchNo";
	    }
	}

	// 생성한 임시비밀번호를 사용자의 이메일로 발송
	public String mailSend(String toMail, String content) {
		try {
			String title="임시비밀번호가 발급되었습니다.";
			// 메세지를 변환시켜서 보관함(messageHelper)에 저장하여 준비.
			MimeMessage message = mailSender.createMimeMessage();
			MimeMessageHelper messageHelper = new MimeMessageHelper(message, true, "UTF-8");
			messageHelper.setTo(toMail);
			messageHelper.setSubject(title);
			messageHelper.setText("<hr/>신규 비밀번호는 : <font color='red'><b>" + content + "</b></font><hr>", true);
			//메일 전송하기
			mailSender.send(message);
		} catch (MessagingException e) {
			e.printStackTrace();
		}
		return "1";
	}
	
	// 마이페이지
	@RequestMapping(value="/myPage", method=RequestMethod.GET)
	public String myPageGet() {
		
		return "member/myPage";
	}

	// 게시물관리
	@RequestMapping(value="/memBoard", method=RequestMethod.GET)
	public String memBoardGet(HttpSession session, Model model,
			@RequestParam(name = "pag", defaultValue = "1", required = false) int pag,
			@RequestParam(name = "pageSize", defaultValue = "5", required = false) int pageSize) { 
		
		String mid = (String) session.getAttribute("sMid");
		// as
		// select count(*) from as2 where mid=#{searchString}; as2 테이블에서 mid에 해당하는 레코드 개수 + 페이징 처리에 필요한 값 반환
		PageVO pageVO = pageProcess.totRecCnt(pag, pageSize, "memAS", "", mid);
		// select * from as2 where mid = #{mid} order by idx desc limit #{startIndexNo},#{pageSize};
		ArrayList<AsVO> AsVos = asService.getAsBoardList(mid,pageVO.getStartIndexNo(),pageSize);
		
		model.addAttribute("pageVO",pageVO);
		model.addAttribute("AsVos",AsVos);

		// 후기
		// select count(*) from productReview2 where mid=#{searchString}; productReview2 테이블에서 mid에 해당하는 레코드 개수 + 페이징 처리에 필요한 값 반환
		PageVO pageRVO = pageProcess.totRecCnt(pag, pageSize, "memReview", "", mid);
		// select *,cast(TIMESTAMPDIFF(MINUTE,wDate,NOW())/60 as signed integer) AS diffTime from productReview2 where mid = #{mid} order by idx desc limit #{startIndexNo},#{pageSize};
		ArrayList<DbProductReviewVO> ReviewVos = dbShopService.getReviewBoardList(mid,pageVO.getStartIndexNo(),pageSize);
		
		model.addAttribute("pageRVO",pageRVO);
		model.addAttribute("ReviewVos",ReviewVos);
		
		//문의
		// select count(*) from productQnA2 where mid=#{searchString}; productQnA2 테이블에서 mid에 해당하는 레코드 개수 + 페이징 처리에 필요한 값 반환
		PageVO pageQVO = pageProcess.totRecCnt(pag, pageSize, "memQna", "", mid);
		// select *,cast(TIMESTAMPDIFF(MINUTE,wDate,NOW())/60 as signed integer) AS diffTime from productQnA2 where mid = #{mid} order by idx desc limit #{startIndexNo},#{pageSize};
		ArrayList<DbProductQnAVO> QnAVos = dbShopService.getQnABoardList(mid,pageVO.getStartIndexNo(),pageSize);
		
		model.addAttribute("pageQVO",pageQVO);
		model.addAttribute("QnAVos",QnAVos);
		return "member/memBoard";
	}
}
