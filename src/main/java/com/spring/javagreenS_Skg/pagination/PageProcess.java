package com.spring.javagreenS_Skg.pagination;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mysql.cj.conf.url.XDevApiDnsSrvConnectionUrl;
import com.spring.javagreenS_Skg.dao.AsDAO;
import com.spring.javagreenS_Skg.dao.DbShopDAO;
import com.spring.javagreenS_Skg.dao.MemberDAO;
import com.spring.javagreenS_Skg.dao.NoticeDAO;
import com.spring.javagreenS_Skg.vo.DbProductVO;

@Service
public class PageProcess {
// 페이지처리 하고싶은 것 다 autowired
	
	@Autowired
	NoticeDAO noticeDAO;
	
	@Autowired
	AsDAO asDAO;
	
	@Autowired 
	DbShopDAO dbShopDAO;
	
	@Autowired
	MemberDAO memberDAO;
	// 인자:1.page번호, 2.page크기, 3.소속(예:게시판(board), 회원(member), 방명록(guest)) 4.분류(part) 5.검색어(searchString)
	// pag : 현재 페이지 번호, pageSize : 한 페이지에 표시되는 항목의 수
	public PageVO totRecCnt(int pag, int pageSize, String section, String part, String searchString) {
		PageVO pageVO = new PageVO();
		
		int totRecCnt = 0;
		int blockSize = 10;
		
		// section에 따른 레코드 갯수를 구해오기
		
		// 공지사항(notice)
		if(section.equals("notice")) {
			if(searchString.equals("")) {
				// select count(*) from notice2;
				totRecCnt = noticeDAO.totRecCnt();
			}
			else {
				String search = part;
				// select count(*) from notice2 where ${search} like concat('%', #{searchString}, '%');
				totRecCnt = noticeDAO.totSearchRecCnt(search, searchString);
			}
		}	
		else if (section.equals("as")){
			if(searchString.equals("")) {
				totRecCnt = asDAO.totRecCnt();
			}
			else {
				String search = part;
				totRecCnt = asDAO.totSearchRecCnt(search,searchString);
			}
		}
		else if (section.equals("dbShop")){
			if(searchString.equals("")) {
				totRecCnt = dbShopDAO.totRecCnt(searchString); 
			}
			else if(!part.equals("categorySubCode")) {
				totRecCnt = dbShopDAO.totSearchRecCnt(part,searchString); 
			}
			else {
				totRecCnt = dbShopDAO.totSubCodeRecCnt(searchString);
			}
		}
		else if(section.equals("dbMyOrder")) {
			String mid = searchString;
			totRecCnt = dbShopDAO.totRecOrderCnt(mid);
		}
		else if(section.equals("myOrderStatus")) {
			// searchString = startJumun + "@" + endJumun + "@" + conditionOrderStatus;
			String[] searchStringArr = searchString.split("@");
			// part 에는 mid 가 들어가 있음 (level 이 '0'일 경우, 즉 '관리자'일 경우, mid 는 '전체'로 설정된다.)
			// searchStringArr[0] = startJumun, searchStringArr[1] = endJumun, searchStringArr[2] = conditionOrderStatus
			totRecCnt = dbShopDAO.totRecCntMyOrderStatus(part,searchStringArr[0],searchStringArr[1],searchStringArr[2]);
		}
		else if(section.equals("dbShopMyOrderStatus")) {
			totRecCnt = dbShopDAO.totRecCntStatus(part,searchString);
		}
		else if(section.equals("dbShopMyOrderCondition")) {
			//System.out.println("part : " + part + " , serchString: " + searchString);
			totRecCnt = dbShopDAO.totRecCntCondition(part, Integer.parseInt(searchString));
		}
		else if(section.equals("adminDbOrderProcess")) {
			// strOrderStatus = startJumun + "@" + endJumun + "@" + orderStatus
			String[] searchStringArr = searchString.split("@");
			totRecCnt = dbShopDAO.totRecCntAdminStatus(searchStringArr[0],searchStringArr[1],searchStringArr[2]);
		}
		else if(section.equals("adminMemberList")) {
			if(part.equals("")) {
				totRecCnt = memberDAO.totRecCntAdminMemberList(Integer.parseInt(searchString));
			}
			else {
				totRecCnt = memberDAO.totRecCntAdminMemberMidList(part);
			}
		}
		else if(section.equals("memAS")) {
			totRecCnt = asDAO.totMemASRecCnt(searchString);
		}
		else if(section.equals("memReview")) {
			totRecCnt = dbShopDAO.totMemReviewRecCnt(searchString);
		}
		else if(section.equals("memQna")) {
			totRecCnt = dbShopDAO.totMemQnARecCnt(searchString);
		}
		else if(section.equals("adMemQna")) {
			totRecCnt = dbShopDAO.totAdMemQnARecCnt();
		}
		
		int totPage = (totRecCnt%pageSize)==0 ? totRecCnt/pageSize : (totRecCnt/pageSize)+1;
		int startIndexNo = (pag - 1) * pageSize;
		int curScrStartNo = totRecCnt - startIndexNo;
		int curBlock = (pag - 1) / blockSize;
		int lastBlock = (totPage % blockSize)==0 ? (totPage / blockSize) - 1 : (totPage / blockSize);
		
		pageVO.setPag(pag);
		pageVO.setPageSize(pageSize);
		pageVO.setTotRecCnt(totRecCnt);
		pageVO.setTotPage(totPage);
		pageVO.setStartIndexNo(startIndexNo);
		pageVO.setCurScrStartNo(curScrStartNo);
		pageVO.setBlockSize(blockSize);
		pageVO.setCurBlock(curBlock);
		pageVO.setLastBlock(lastBlock);
		
		return pageVO;
	}
}
