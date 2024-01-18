package com.spring.javagreenS_Skg;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.spring.javagreenS_Skg.pagination.PageProcess;
import com.spring.javagreenS_Skg.pagination.PageVO;
import com.spring.javagreenS_Skg.service.AdminService;
import com.spring.javagreenS_Skg.service.DbShopService;
import com.spring.javagreenS_Skg.service.MemberService;
import com.spring.javagreenS_Skg.vo.ChartVO;
import com.spring.javagreenS_Skg.vo.DbOptionVO;
import com.spring.javagreenS_Skg.vo.DbProductQnAVO;
import com.spring.javagreenS_Skg.vo.DbProductVO;
import com.spring.javagreenS_Skg.vo.MemberVO;

@Controller
@RequestMapping("/admin")
public class AdminController {
	String msgFlag="";
	
	@Autowired
	AdminService adminService;
	
	@Autowired
	PageProcess pageProcess;
	
	@Autowired
	MemberService memberService;
	
	@Autowired
	DbShopService dbShopService;
	
	// 회원관리 폼 이동
	@RequestMapping(value="/adMemberList", method = RequestMethod.GET)
	public String adMemberListGet(
			@RequestParam(name="pag", defaultValue="1", required=false) int pag,
			@RequestParam(name="pageSize", defaultValue="20", required=false) int pageSize,
			@RequestParam(name="level", defaultValue="99", required=false) int level,
			@RequestParam(name="mid", defaultValue="", required=false) String mid,
			Model model) {
		
	  PageVO pageVo = null;
	  if(mid.equals("")) {
		// 전체 조회
	  	pageVo = pageProcess.totRecCnt(pag, pageSize, "adminMemberList", "", level+"");
	  }
	  else {
		// 개별 조회(mid 조회)
	  	pageVo = pageProcess.totRecCnt(pag, pageSize, "adminMemberList", mid, "");
	  }
	  
	  ArrayList<MemberVO> vos = new ArrayList<MemberVO>();
	  if(mid.equals("")) {
		// level로 검색처리(defaultValue="99")
	  	vos = memberService.getAdminMemberLevelList(pageVo.getStartIndexNo(), pageSize, level);
	  }
	  else {
		// 개별 id로 검색처리
	  	vos = memberService.getAdminMemberMidList(pageVo.getStartIndexNo(), pageSize, mid);
	  }
		model.addAttribute("vos", vos);
		model.addAttribute("level", level);
		model.addAttribute("mid", mid);
		model.addAttribute("pageVo", pageVo);
		model.addAttribute("totRecCnt", pageVo.getTotRecCnt());
		
		return "admin/member/adMemberList";
	}
	
	// adMemberList에서 개별자료
	@RequestMapping(value="/adMemberInfor",method=RequestMethod.GET)
	public String adMemberInforGet(int idx, Model model) {
		MemberVO vo = memberService.getMemInfor(idx);
		model.addAttribute("vo", vo);
		return "admin/member/adMemberInfor";
	}
	
	// 등업
	@ResponseBody
	@RequestMapping(value="/adMemberLevel", method = RequestMethod.POST)
	public String adMemberLevelPost(int idx, int level) {
		memberService.setAdminLevelUpdate(idx, level);
		return "";
	}
	
	// 문의글관리 폼 이동
	@RequestMapping(value="/adMemQnA", method=RequestMethod.GET)
	public String adMemQnAGet(Model model,
		@RequestParam(name = "pag", defaultValue = "1", required = false) int pag,
		@RequestParam(name = "pageSize", defaultValue = "5", required = false) int pageSize) { 

		// 문의
		PageVO pageVO = pageProcess.totRecCnt(pag, pageSize, "adMemQna", "", "");
		ArrayList<DbProductQnAVO> vos = dbShopService.getAdQnABoardList(pageVO.getStartIndexNo(),pageSize);
		
		model.addAttribute("pageVO",pageVO);
		model.addAttribute("vos",vos);
		return "admin/member/adMemQnA";
	}
	
	// 쇼핑몰분석
	@RequestMapping(value="/adChart", method = RequestMethod.GET)
	public String adChartGet() {
		return "admin/adChart";
	}
	
	// 구글차트만들기2 - 자료 입력하여 차크 만들기
	@RequestMapping(value="/googleChart", method=RequestMethod.GET)
	public String googleChartGet(Model model,
			@RequestParam(name="part", defaultValue="bar", required=false) String part) {
		model.addAttribute("part", part);
		return "admin/chart/chart";
	}
	
	// 구글차트 방문자수
	@RequestMapping(value="/googleChart", method=RequestMethod.POST) 
	public String googleChartPost(Model model, ChartVO vo) { 
		model.addAttribute("vo",vo); 
		return "admin/chart/chart";
	}
	 
	// 최근 방문자 수 차트로 표시하기
	@RequestMapping(value="/googleChartRecently", method=RequestMethod.GET)
	public String googleChartRecentlyGet(Model model,
	       @RequestParam(name="part", defaultValue="lineChartVisitCount", required=false) String part) {
	   
	   List<ChartVO> vos = null;
	   
	   if(part.equals("lineChartVisitCount")) {
	       // 7일 동안의 방문일자와 방문자 수 반환
	       vos = adminService.getRecentlyVisitCount();
	       
	       // 차트의 날짜 표시(String)
	       String[] visitDates = new String[7];
	       // 차트의 날짜(int, x축)
	       int[] visitDays = new int[7];
	       // 차트의 방문자 수(int, y축)
	       int[] visitCounts = new int[7];
	       
	       for(int i=0; i<7; i++) {
	           visitDates[i] = vos.get(i).getVisitDate();
	           visitDays[i] = Integer.parseInt(vos.get(i).getVisitDate().toString().substring(8));
	           visitCounts[i] = vos.get(i).getVisitCount();
	       }
	       
	       model.addAttribute("title", "최근 7일간 방문횟수");
	       model.addAttribute("subTitle", "최근 7일동안 방문한 해당일자 방문자 총수를 표시합니다.");
	       model.addAttribute("visitCount", "방문횟수");
	       model.addAttribute("legend", "일일 방문자 수");
	       model.addAttribute("part", part);
	       model.addAttribute("visitDates", visitDates);
	       model.addAttribute("visitDays", visitDays);
	       model.addAttribute("visitCounts", visitCounts);
	   }
	   
	   return "admin/chart/chart";
	}

	// 회원삭제
	@ResponseBody
	@RequestMapping(value="/adMemberReset", method=RequestMethod.POST)
	public String adMemberResetPost(int idx) {
		memberService.setMemberReset(idx);
		return "";
	}
	
	// 진열된 상품 클릭 시 상품내역 상세보기
	@RequestMapping(value = "/dbShopContent", method = RequestMethod.GET)
	public String dbShopContentGet(
			@RequestParam(value = "idx", required = true) int idx,
	        @RequestParam(value = "pag", required = false, defaultValue = "1") int pag,
	        @RequestParam(value = "pageSize", required = false, defaultValue = "16") int pageSize,
			Model model) {
		DbProductVO productVo = dbShopService.getDbShopProduct(idx);
		// 상품 상세 정보 불러오기
		List<DbOptionVO> optionVos = dbShopService.getDbShopOption(idx); // 옵션 정보 모두 가져오기
		model.addAttribute("productVo", productVo);
		model.addAttribute("optionVos", optionVos);
		model.addAttribute("pag", pag);
		model.addAttribute("pageSize", pageSize);
		
		return "admin/dbShop/dbShopContent";
	}
}