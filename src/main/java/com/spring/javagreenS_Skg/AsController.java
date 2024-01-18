package com.spring.javagreenS_Skg;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.spring.javagreenS_Skg.common.ARIAUtil;
import com.spring.javagreenS_Skg.pagination.PageProcess;
import com.spring.javagreenS_Skg.pagination.PageVO;
import com.spring.javagreenS_Skg.service.AsService;
import com.spring.javagreenS_Skg.service.MemberService;
import com.spring.javagreenS_Skg.vo.AsReplyVO;
import com.spring.javagreenS_Skg.vo.AsVO;
import com.spring.javagreenS_Skg.vo.MemberVO;
import com.spring.javagreenS_Skg.vo.NoticeVO;

@Controller
@RequestMapping("/as")
public class AsController {

	@Autowired
	AsService asService;
	
	@Autowired
	PageProcess pageProcess;
	
	@Autowired
	MemberService memberService;
	
	// as 리스트로 이동.
	@RequestMapping(value="/asList", method=RequestMethod.GET)
	public String asListGet(
			@RequestParam(name="pag", defaultValue = "1", required = false) int pag,
			@RequestParam(name="pageSize", defaultValue = "15", required = false) int pageSize,
			Model model) {
		
		// as2 테이블의 전체 레코드 수 및 페이징 처리에 필요한 값을 가져온다.
		PageVO pageVO = pageProcess.totRecCnt(pag, pageSize, "as", "", "");
		// diffTime를 계산하여 as2 테이블에서 idx 컬럼을 기준으로 정렬하여 데이터를 제한하여 가져온다.
		List<AsVO> vos = asService.getAsList(pageVO.getStartIndexNo(),pageSize);
		
		model.addAttribute("vos",vos);
		model.addAttribute("pageVO",pageVO);
		return"as/asList";
	}
	
	// as 글쓰기로 이동.
	@RequestMapping(value="/asInput", method=RequestMethod.GET)
	public String asInputGet(Model model, HttpSession session) {
		String mid = (String) session.getAttribute("sMid");
		MemberVO vo = memberService.getMemberInfor(mid);
		model.addAttribute("vo",vo);
		return"as/asInput";
	}
	
	// as 글 등록(이미지가 있을 시 이미지 처리)
	@RequestMapping(value="/asInput", method=RequestMethod.POST)
	public String asInputPost(AsVO vo) {
		try {
			vo.setPwd(ARIAUtil.ariaEncrypt(vo.getPwd())); //암호화
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		// content에 이미지가 있다면, service에서 이미지만 /resources/data/ckeditor/as 폴더에 저장
		asService.imgCheck(vo.getContent());
		
		// 이미지 복사작업이 끝나면 as 폴더에 실제로 저장된 파일명을 db에 저장.
		vo.setContent(vo.getContent().replace("/data/ckeditor/","/data/ckeditor/as/"));
		asService.setAsInputOk(vo);
		
		return"redirect:/msg/asInputOk"; 
	}
	
	// as 글 자세히보기-비밀번호 입력창(비밀글)
	@RequestMapping(value="/asPwdCheck", method=RequestMethod.GET)
	public String asPwdCheckGet(int idx, int pag, int pageSize, Model model) {
		model.addAttribute("idx",idx);
		model.addAttribute("pag",pag);
		model.addAttribute("pageSize",pageSize);
		return"as/asPwdCheck";
	}
	
	// as 글 자세히보기(비밀번호 체크 후 content(비밀글))
	@RequestMapping(value = "/asPwdCheck", method = RequestMethod.POST)
	public String asPwdCheckPost(int idx, int pag, int pageSize, String name, String pwd, Model model) {
	    String decPwd = "";

	    // A/S 글의 idx 값으로 idx에 해당하는 게시글의 정보를 as2 테이블에서 가져오고, 해당 게시글의 댓글의 수를 asReply2 테이블에서 replyCount 라는 이름으로 가져온다.
	    AsVO vo = asService.getAsContent(idx);

	    if (vo != null && vo.getName().equals(name)) {
	        try {
	            decPwd = ARIAUtil.ariaDecrypt(vo.getPwd());
	        } catch (InvalidKeyException e) {
	            e.printStackTrace();
	        } catch (UnsupportedEncodingException e) {
	            e.printStackTrace();
	        }
	        if (pwd.equals(decPwd)) {
	            model.addAttribute("vo", vo);
	            model.addAttribute("pag", pag);
	            model.addAttribute("pageSize", pageSize);

	    		// 댓글 가져오기(replyVos), idx 에 대한 asReply2 테이블의 레코드를 가져오고, levelOrder로 정렬
	            ArrayList<AsReplyVO> replyVos = asService.getAsReply(idx);
	            model.addAttribute("replyVos", replyVos);

	            return "as/asContent";
	        }
	    }

	    return "redirect:/msg/asPwdCheckNo";
	}
	
	// as 글 상세보기(공개글)
	@RequestMapping(value="/asContent", method=RequestMethod.GET)
	public String asContentGet(int idx, int pag, int pageSize, Model model) {
		// A/S 글의 idx 값으로 idx에 해당하는 게시글의 정보를 as2 테이블에서 가져오고, 해당 게시글의 댓글의 수를 asReply2 테이블에서 replyCount 라는 이름으로 가져온다.
		AsVO vo = asService.getAsContent(idx);
		
		model.addAttribute("vo",vo);
		model.addAttribute("pag",pag);
		model.addAttribute("pageSize",pageSize);
	  
		// 댓글 가져오기(replyVos), idx 에 대한 asReply2 테이블의 레코드를 가져오고, levelOrder로 정렬
		ArrayList<AsReplyVO> replyVos = asService.getAsReply(idx);
		model.addAttribute("replyVos", replyVos);
			
		return"as/asContent";
	}
	
	// 게시글 수정 폼 화면 불러오기
	@RequestMapping(value="/asUpdate",method = RequestMethod.GET)
	public String asUpdateGet(int idx, int pag, int pageSize, Model model) {
		// A/S 글의 idx 값으로 idx에 해당하는 게시글의 정보를 as2 테이블에서 가져오고, 해당 게시글의 댓글의 수를 asReply2 테이블에서 replyCount 라는 이름으로 가져온다.
		AsVO vo = asService.getAsContent(idx);
		// 수정 폼으로 들어올 때 원본파일에 그림파일이 존재한다면, 현재폴더(as)의 그림파일을 ckeditor폴더로 복사시켜둔다.
		// 기존에는 as 폴더에 이미지 파일이 저장, 이를 ckeditor 폴더에도 복사함으로써 ckeditor에서도 이미지를 정상적으로 표시.
		// 데이터베이스(as2의 content 컬럼)에 vo.getContent()에 그림 파일이 있으면, 파일 복사 메소드 실행
		if(vo.getContent().indexOf("src=\"/") != -1) asService.imgCheckUpdate(vo.getContent());
		  
		model.addAttribute("vo",vo); 
		model.addAttribute("pag",pag);
		model.addAttribute("pageSize",pageSize);
			 
		return "as/asUpdate";
	}
	
  // as글 수정
  @RequestMapping(value="/asUpdate",method = RequestMethod.POST) 
  public String asUpdatePost(AsVO vo, int pag, int pageSize, Model model) {
	  AsVO oriVo = asService.getAsContent(vo.getIdx()); 
	  
	  // content안에서 내용의 수정이 없을시는 아래작업을 처리할 필요가 없다.
	  if(!oriVo.getContent().equals(vo.getContent())) { 
	  	// 수정버튼을 클릭하고 post 호출시에는기존의 as폴더의 사진파일들을 모두 삭제처리한다. 
	  	if(oriVo.getContent().indexOf("src=\"/") != -1) asService.imgDelete(oriVo.getContent());
	  
		  // 파일복사전에 원본파일의 위치가 'ckeditor/as'폴더였던것을 'ckeditor'폴더로 변경시켜두어야 한다.
		  vo.setContent(vo.getContent().replace("/data/ckeditor/as/", "/data/ckeditor/"));
		  
		  // 앞의 준비작업이 완료되면, 수정된 그림(복사된그림)을 다시 as폴더에 복사처리한다.(/data/ckeditor/ ->/data/ckeditor/as/) 
		  // 이 작업은 처음 게시글을 올릴때의 파일복사 작업과 동일한 작업이다.
		  asService.imgCheck(vo.getContent());
	  
		  // 다시 ckeditor에 있는 그림파일의 경로를 ckeditor/as폴더로 변경시켜준다.
		  vo.setContent(vo.getContent().replace("/data/ckeditor/","/data/ckeditor/as/")); 
	  }
	  
	  // 잘 정비된 vo를 DB에 저장시켜준다. 
	  asService.setAsUpdate(vo);
	  
	  model.addAttribute("flag", "?pag="+pag+"&pageSize="+pageSize);
	  
	  return "redirect:/msg/asUpdateOk";
  }
  
  	// A/S 글 삭제. 사진 먼저 삭제하고 DB 삭제
	@RequestMapping(value="/asDeleteOk", method=RequestMethod.GET)
	public String asDeleteOkGet(int idx, int pag, int pageSize, Model model) {
		// A/S 글의 idx 값으로 idx에 해당하는 게시글의 정보를 as2 테이블에서 가져오고, 해당 게시글의 댓글의 수를 asReply2 테이블에서 replyCount 라는 이름으로 가져온다.
		AsVO vo = asService.getAsContent(idx);
		// A/S 글에 사진이 있다면 서버에 존재하는 사진을 먼저 삭제.
		if(vo.getContent().indexOf("src=\"/") != -1) asService.imgDelete(vo.getContent());
		
		// 댓글이 있다면 댓글 삭제, delete from asReply2 where asIdx = #{idx};
		asService.setAsDeleteReply(idx);
		
		// DB에서 게시글 삭제, delete from as2 where idx = #{idx};
		asService.setAsDelete(idx);
		
		model.addAttribute("flag", "?pag=&"+pag+"pageSize="+pageSize);
		return "redirect:/msg/setAsDelete";
	}
	
	// 댓글등록
	@ResponseBody
	@RequestMapping(value="/asReplyInput", method=RequestMethod.POST)
	public String asReplyInputPost(AsReplyVO replyVo) {
		int levelOrder = 0;

		// asReply2 테이블에서 특정 "asIdx"에 해당하는 레코드 중 "levelOrder" 컬럼의 최대값을 검색
		// 특정 A/S 글 (asIdx)에 속한 댓글들 중에서 가장 높은 순서(levelOrder)를 가져온다.
		String strLevelOrder = asService.maxLevelOrder(replyVo.getAsIdx());
	  
		// 최대값이 있다면 해당 값에 1을 더하여 새로운 댓글의 levelOrder를 결정
		if(strLevelOrder != null) levelOrder = Integer.parseInt(strLevelOrder)+1;
		
		// 댓글 정보를 데이터베이스에 등록
		replyVo.setLevelOrder(levelOrder);
		
		// insert into asReply2 value(default, #{replyVo.asIdx},#{replyVo.name},default,#{replyVo.content},#{replyVo.level},#{replyVo.levelOrder});
		asService.setAsReplyInput(replyVo);
		
		return "1";
	}
	
	// 댓글삭제
	@ResponseBody
	@RequestMapping(value="/asReplyDelete",method=RequestMethod.POST)
	public String asReplyDeletePost(int idx) {
		// delete from asReply2 where idx = #{idx};
		asService.setAsReplyDelete(idx);
		return"";
	}

	// 댓글수정
	@ResponseBody
	@RequestMapping(value="/asReplyUpdate",method=RequestMethod.POST)
	public String asReplyUpdatePost(AsReplyVO replyVo) {
		asService.setAsReplyUpdate(replyVo);
		return "";
	}
	
	// 대댓글
	@ResponseBody
	@RequestMapping(value="/asReplyInput2", method=RequestMethod.POST)
	public String asReplyInput2Post(AsReplyVO replyVo) {
		asService.levelOrderPlusUpdate(replyVo);     //부모댓글의 levelOrder값보다 큰 모든 댓글의 levelOrder값을 +1. (update)
		replyVo.setLevel(replyVo.getLevel()+1); // 자신의 level은 부모 level보다 +1 시켜준다.
		replyVo.setLevelOrder(replyVo.getLevelOrder()+1); // 자신의 levelOrder은 부모 levelOrder보다 +1 시켜준다.
		
		asService.setAsReplyInput2(replyVo);
		return"";
	}
	
	// 관리자 확인
	@ResponseBody
	@RequestMapping(value="/swCheck", method=RequestMethod.POST)
	public String swCheckPost(int idx) {
		asService.swCheck(idx);
		return"";
	}
	
	// A/S 검색
	@RequestMapping(value="/asSearch", method=RequestMethod.GET)
	public String asSearchGet(
			@RequestParam(name="pag", defaultValue="1", required = false) int pag,
			@RequestParam(name="pageSize", defaultValue="10", required = false) int pageSize,
			String search,
			String searchString,
			Model model) {
		PageVO pageVO = pageProcess.totRecCnt(pag, pageSize, "as", search, searchString);
		List<AsVO> vos = asService.getAsSearch(pageVO.getStartIndexNo(), pageSize, search, searchString);
		
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