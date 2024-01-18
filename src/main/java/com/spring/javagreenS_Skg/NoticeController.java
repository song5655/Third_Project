package com.spring.javagreenS_Skg;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.spring.javagreenS_Skg.pagination.PageProcess;
import com.spring.javagreenS_Skg.pagination.PageVO;
import com.spring.javagreenS_Skg.service.NoticeService;
import com.spring.javagreenS_Skg.vo.NoticeVO;

@Controller
@RequestMapping("/notice")
public class NoticeController {
	
	@Autowired
	NoticeService noticeService;
	
	@Autowired
	PageProcess pageProcess;
	
	// 공지사항으로 이동
	@RequestMapping(value="/noticeList", method=RequestMethod.GET)
	public String noticeListGet(
			@RequestParam(name="pag", defaultValue = "1", required = false) int pag,
			@RequestParam(name="pageSize", defaultValue = "10", required = false) int pageSize,
			Model model) {
		// 인자		: 1. pag, 2. pageSize, 3. 소속(section) 4. 분류(part) 5. 검색어(searchString)
		// section 	: 게시판(board), 회원(member), 방명록(guest)
		// part 	: 글제목(title), 글내용(content), 작성자(name)
		PageVO pageVO = pageProcess.totRecCnt(pag, pageSize, "notice", "", "");
		List<NoticeVO> vos = noticeService.getNoticeList(pageVO.getStartIndexNo(),pageSize);
		
		model.addAttribute("vos",vos);
		model.addAttribute("pageVO", pageVO);
		return"notice/noticeList";
	}
	
	// 공지사항 글쓰기 이동
	@RequestMapping(value="/noticeInput",method=RequestMethod.GET)
	public String noticeInputGet() {
		return "notice/noticeInput";
	}
	
	// 공지사항 글 등록
	@RequestMapping(value="/noticeInput",method=RequestMethod.POST)
	public String noticeInputPost(HttpSession session, NoticeVO vo) {
		String mid = (String) session.getAttribute("sMid");
		
		// content에 이미지가 있다면, 이미지를 /resources/data/ckeditor/notice 폴더에 저장
		noticeService.imgCheck(vo.getContent());
		
		// 이미지 복사작업이 끝나면 notice 폴더에 실제로 저장된 파일명을 db에 저장.
		// notice 폴더에 저장한 경로로 content의 경로를 수정한 뒤 DB에 저장
		vo.setContent(vo.getContent().replace("/data/ckeditor/","/data/ckeditor/notice/"));
		// insert into notice2 values(default, #{vo.name}, #{vo.title}, #{vo.content}, default, default, #{vo.mid}, #{vo.pin})
		noticeService.setNoticeInput(vo);
		return "redirect:/msg/noticeInputOk";
	}
	
	// 공지사항 글 클릭하여 상세보기
	@RequestMapping(value="/noticeContent", method=RequestMethod.GET)
	public String noticeContentGet(
		    @RequestParam int idx,
		    @RequestParam int pag,
		    @RequestParam int pageSize,
		    Model model,
		    HttpSession session
		) {
		//조회 수 증가(조회 수 중복방지처리)
		ArrayList<String> contentIdx = (ArrayList)session.getAttribute("sContentIdx");
		if(contentIdx == null) contentIdx = new ArrayList<String>();
		
		String imsiContentIdx = "notice"+idx;
		// 만약 contentIdx에 이 imsiContentIdx가 포함되어 있지 않다면, 이전에 조회한 적이 없는 글
		if(!contentIdx.contains(imsiContentIdx)) {
			noticeService.setReadNum(idx);
			contentIdx.add(imsiContentIdx);
		}
		// 사용자가 동일한 글을 여러 번 조회해도, 이미 조회한 글 번호를 세션에 저장하여 중복 조회를 방지
		session.setAttribute("sContentIdx", contentIdx);
		
		// 공지사항 글 가져오기.
		NoticeVO vo = noticeService.getNoticeContent(idx);
		
		// 이전글, 다음글 가져오기(idx와 title만을 가져오는 것)
		ArrayList<NoticeVO> pnVOS = noticeService.getPreNext(idx);
		// idx의 최소값을 가져온다.(noticeContent.jsp의 view 처리)
		int minIdx = noticeService.getMinIdx();
		
		model.addAttribute("vo", vo);
		model.addAttribute("pnVOS", pnVOS);
		model.addAttribute("minIdx", minIdx);
		model.addAttribute("pag", pag);
		model.addAttribute("pageSize", pageSize);
		return "notice/noticeContent";
	}
	
	//게시글 삭제. 사진 먼저 삭제하고 DB 삭제
	@RequestMapping(value="/noticeDeleteOk", method=RequestMethod.GET)
	public String noticeDeleteOkGet(int idx, int pag, int pageSize, Model model) {
		// 게시글에 사진이 있다면 서버에 존재하는 사진을 먼저 삭제.
		NoticeVO vo = noticeService.getNoticeContent(idx);
		if(vo.getContent().indexOf("src=\"/") != -1) noticeService.imgDelete(vo.getContent());
		
		//DB에서 게시글 삭제
		noticeService.setNoticeDelete(idx);
		
		model.addAttribute("flag", "?pag=&"+pag+"pageSize="+pageSize);
		return "redirect:/msg/setNoticeDelete";
	}
	
	// 게시글 수정하기 폼 불러오기
	@RequestMapping(value="/noticeUpdate", method=RequestMethod.GET)
	public String noticeUpdateGet(int idx, int pag, int pageSize, Model model) {
		// select * from notice2 where idx = #{idx};
		NoticeVO vo = noticeService.getNoticeContent(idx);
		// 수정 폼으로 들어올 때 원본파일에 그림파일이 존재한다면, 현재폴더(notice)의 그림파일을 ckeditor폴더로 복사시켜둔다.
		// 기존에는 notice 폴더에 이미지 파일이 저장, 이를 ckeditor 폴더에도 복사함으로써 ckeditor에서도 이미지를 정상적으로 표시.
		// 데이터베이스(notice2의 content 컬럼)에 vo.getContent()에 그림 파일이 있으면, 파일 복사 메소드 실행
		if(vo.getContent().indexOf("src=\"/") != -1) noticeService.imgCheckUpdate(vo.getContent());
		
		model.addAttribute("vo",vo);
		model.addAttribute("pag",pag);
		model.addAttribute("pageSize",pageSize);
		
		return"notice/noticeUpdate";
	}
	
	// 기존 게시글에 있던 사진을 삭제하고, 새로 수정한 게시글의 이미지 경로를 상위 폴더로 수정
	// 수정한 게시글의 이미지를 하위 폴더에 넣어주고, 다시 경로를 하위 폴더로 수정
	// 왜 경로를 수정했다 말았다 하는지? => imgCheck 메서드를 사용하기 위해
	
	// 게시글 수정하기
	@RequestMapping(value="/noticeUpdate", method=RequestMethod.POST)
	public String noticeUpdatePost(NoticeVO vo, int pag, int pageSize, Model model) {
		// 원본(수정 전) 공지사항 정보를 가져온다.
		NoticeVO oriVo = noticeService.getNoticeContent(vo.getIdx());
		
		// 수정 전과 수정 후의 내용이 다른 경우에만 아래 작업을 수행
		if(!oriVo.getContent().equals(vo.getContent()))	{
			// 기존 공지사항의 내용에 이미지가 포함되어 있다면 공지사항(notice) 폴더의 기존 사진파일을 삭제
			if(oriVo.getContent().indexOf("src=\"/") != -1) noticeService.imgDelete(oriVo.getContent());

			// 수정하기를 할 때 수정한 이미지 파일이 ckeditor/notice 폴더에 있었다면, 이를 ckeditor 폴더로 경로 수정
			vo.setContent(vo.getContent().replace("/data/ckeditor/notice/", "/data/ckeditor/"));
			
			// 수정한 그림을 notice폴더에 넣어준다.(imgCheck 메소드는 ckeditor 폴더를 notice 폴더로 복사)
			noticeService.imgCheck(vo.getContent());
			
			// 다시 ckeditor에 있는 그림파일의 경로를 notice폴더로 변경시켜준다.
			vo.setContent(vo.getContent().replace("/data/ckeditor/", "/data/ckeditor/notice/"));
		}
		
		// 수정한 내용을 DB에 저장
		noticeService.setNoticeUpdate(vo);
		
		model.addAttribute("flag", "?pag="+pag+"&pageSize="+pageSize);
			
		return "redirect:/msg/noticeUpdateOk";
	}
	
	// 테스트화면 불러오기
	@RequestMapping(value="/ddd", method=RequestMethod.GET)
	public String ddd() {
		return "notice/ddd";
	}
	
	// 공지사항 검색
	@RequestMapping(value="/noticeSearch", method=RequestMethod.GET)
	public String noticeSearchGet(
			@RequestParam(name="pag", defaultValue="1", required = false) int pag,
			@RequestParam(name="pageSize", defaultValue="10", required = false) int pageSize,
			String search,
			String searchString,
			Model model) {
		PageVO pageVO = pageProcess.totRecCnt(pag, pageSize, "notice", search, searchString);
		List<NoticeVO> vos = noticeService.getNoticeSearch(pageVO.getStartIndexNo(), pageSize, search, searchString);
		
		String searchTitle;
		if(search.equals("title")) searchTitle="글제목";
		else if(search.equals("name")) searchTitle="작성자";
		else searchTitle="글내용";
		
		model.addAttribute("vos",vos);
		model.addAttribute("pageVO",pageVO);
		model.addAttribute("search",search);
		model.addAttribute("searchTitle",searchTitle);
		model.addAttribute("searchString",searchString);
		
		return "notice/noticeList";
	}
	
}
