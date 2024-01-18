package com.spring.javagreenS_Skg.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.spring.javagreenS_Skg.dao.AsDAO;
import com.spring.javagreenS_Skg.vo.AsReplyVO;
import com.spring.javagreenS_Skg.vo.AsVO;
import com.spring.javagreenS_Skg.vo.NoticeVO;

@Service
public class AsServiceImpl implements AsService{
	
	@Autowired
	AsDAO asDAO;

	//as 등록
	@Override
	public void setAsInputOk(AsVO vo) {
		asDAO.setAsInputOk(vo);
	}

	//as 리스트
	@Override
	public List<AsVO> getAsList(int startIndexNo, int pageSize) {
		return asDAO.getAsList(startIndexNo, pageSize);
	}

	@Override
	public AsVO getAsContent(int idx) {
		return asDAO.getAsContent(idx);
	}

	@Override
	public void imgDelete(String content) {
		// 		0		 1         2         3         4         5         6
		//      012345678901234567890123456789012345678901234567890123456789012345678901234567890
		// <img src="/javagreenS_Skg/data/ckeditor/as/220622152324_5.gif" style="height:30px; width:30px" />
		
		if(content.indexOf("src=\"/") ==-1) return;
		
		HttpServletRequest request = ((ServletRequestAttributes)RequestContextHolder.currentRequestAttributes()).getRequest();
		String uploadPath = request.getSession().getServletContext().getRealPath("/resources/data/ckeditor/as/");
		
		int position=38;
		String nextImg = content.substring(content.indexOf("src=\"/")+position);
		System.out.println("nextImg : " + nextImg);
		boolean sw = true;
		
		while(sw) {
			String imgFile = nextImg.substring(0,nextImg.indexOf("\""));
			System.out.println("imgFile : " + imgFile);
			String oriFilePath = uploadPath + imgFile;
			System.out.println("oriFilePath : " + oriFilePath);
			
			fileDelete(oriFilePath); // as 폴더 존재하는 파일 삭제.
			
			if(nextImg.indexOf("src=\"/")==-1) {
				sw = false;
			}
			else {
				nextImg = nextImg.substring(nextImg.indexOf("src=\"/")+position);
			}
		}
	}
		
	// 원본 이미지를 삭제처리(resources/data/ckeditor/as폴더에서 삭제)
	private void fileDelete(String oriFilePath) {
		File delFile = new File(oriFilePath);
		if(delFile.exists()) delFile.delete();
	}

	// 게시판의 글을 올릴 때 그림파일도 함께 저장할 경우 처리하는 메소드
	@Override
	public void imgCheck(String content) {
		//                1         2         3         4         5         6
		//      012345678901234567890123456789012345678901234567890123456789012345678901234567890
		// <img src="/javagreenS_Skg/data/ckeditor/220622152324_5.gif" style="height:30px; width:30px" />
		
		// 이 작업은 content안에 그림파일(img src="/)가 있을 때에만 수행.
		if(content.indexOf("src=\"/") ==-1) return;
		
		HttpServletRequest request = ((ServletRequestAttributes)RequestContextHolder.currentRequestAttributes()).getRequest();
		String uploadPath = request.getSession().getServletContext().getRealPath("/resources/data/ckeditor/");
		
		int position=35;
		String nextImg = content.substring(content.indexOf("src=\"/")+position);
		boolean sw = true;
		
		while(sw) {
			String imgFile = nextImg.substring(0,nextImg.indexOf("\""));
			
			String oriFilePath = uploadPath + imgFile;
			String copyFilePath = uploadPath +"as/"+ imgFile;
			
			fileCopyCheck(oriFilePath,copyFilePath);  //as 폴더에 파일을 복사처리.
			
			if(nextImg.indexOf("src=\"/")==-1) {
				sw = false;
			}
			else {
				nextImg = nextImg.substring(nextImg.indexOf("src=\"/")+position);
			}
		}
	}
	
	// 실제 서버(ckeditor)에 저장되어 있는 파일을 as폴더로 복사처리. 
	private void fileCopyCheck(String oriFilePath, String copyFilePath) {
		File oriFile = new File(oriFilePath);
		File copyFile = new File(copyFilePath);
		
		try {
			FileInputStream fis = new FileInputStream(oriFile);
			FileOutputStream fos = new FileOutputStream(copyFile);
			
			byte[] buffer = new byte[2048];
			int count = 0;
			while((count = fis.read(buffer)) != -1) {
				fos.write(buffer,0,count);
			}
			fos.flush();
			fos.close();
			fis.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void setAsUpdate(AsVO vo) {
		asDAO.setAsUpdate(vo);
	}

	// A/s 문의내용 수정하기(그림파일이 있는 경우, CkEDITOR에 그림파일을 보여주기 위한 작업)
	@Override
	public void imgCheckUpdate(String content) {
		//                1         2         3         4         5         6
		//      012345678901234567890123456789012345678901234567890123456789012345678901234567890
		// <img src="/javagreenS_Skg/data/ckeditor/220622152324_5.gif" style="height:30px; width:30px" />
		// <img src="/javagreenS_Skg/data/ckeditor/as/220622152324_5.gif" style="height:30px; width:30px" />
	
		if(content.indexOf("src=\"/") ==-1) return;
		
		HttpServletRequest request = ((ServletRequestAttributes)RequestContextHolder.currentRequestAttributes()).getRequest();
		String uploadPath = request.getSession().getServletContext().getRealPath("/resources/data/ckeditor/");
		
		int position=35;
		String nextImg = content.substring(content.indexOf("src=\"/")+position);
		System.out.println(nextImg); // as/240103152644_220715130638_m1.jpeg" style="height:230px; width:290px" /></p>
		boolean sw = true;
		
		while(sw) {
			String imgFile = nextImg.substring(0,nextImg.indexOf("\""));
			System.out.println(imgFile); // as/240103152644_220715130638_m1.jpeg
			String oriFilePath = uploadPath + imgFile;
			
			// "as/" 부분을 제거하여 임시로 처리
			oriFilePath = oriFilePath.replace("as/", "");
			
			System.out.println(oriFilePath);
			// D:\JavaProject\springframework\works\.metadata\.plugins\org.eclipse.wst.server.core\tmp0\wtpwebapps\javagreenS_Skg\resources\data\ckeditor\as/240103152644_220715130638_m1.jpeg
			String copyFilePath = request.getRealPath("/resources/data/ckeditor/" + imgFile);
			System.out.println(copyFilePath);
			// D:\JavaProject\springframework\works\.metadata\.plugins\org.eclipse.wst.server.core\tmp0\wtpwebapps\javagreenS_Skg\resources\data\ckeditor\as\240103152644_220715130638_m1.jpeg
		
			fileCopyCheck(oriFilePath,copyFilePath); //as 폴더 존재하는 파일을 ckeditor에 복사.
			
			if(nextImg.indexOf("src=\"/")==-1) {
				sw = false;
			}
			else {
				nextImg = nextImg.substring(nextImg.indexOf("src=\"/")+position);
			}
		}
	}

	// DB에서 게시글 삭제
	@Override
	public void setAsDelete(int idx) {
		asDAO.setAsDelete(idx);
		
	}

	// 댓글입력
	@Override
	public String maxLevelOrder(int asIdx) {
		return asDAO.maxLevelOrder(asIdx);
	}

	@Override
	public void setAsReplyInput(AsReplyVO replyVo) {
		asDAO.setAsReplyInput(replyVo);
		
	}

	@Override
	public ArrayList<AsReplyVO> getAsReply(int idx) {
		return asDAO.getAsReply(idx);
	}

	@Override
	public void setAsReplyDelete(int idx) {
		asDAO.setAsReplyDelete(idx);
	}

	@Override
	public void setAsReplyUpdate(AsReplyVO replyVo) {
		asDAO.setAsReplyUpdate(replyVo);
		
	}

	@Override
	public void levelOrderPlusUpdate(AsReplyVO replyVo) {
		asDAO.levelOrderPlusUpdate(replyVo);
	}

	@Override
	public void setAsReplyInput2(AsReplyVO replyVo) {
		asDAO.setAsReplyInput2(replyVo);
	}

	@Override
	public void swCheck(int idx) {
		asDAO.swCheck(idx);
	}

	@Override
	public ArrayList<AsVO> getAsBoardList(String mid, int startIndexNo, int pageSize) {
		return asDAO.getAsBoardList(mid,startIndexNo,pageSize);
	}

	@Override
	public void setAsDeleteReply(int idx) {
		asDAO.setAsDeleteReply(idx);
	}

	@Override
	public List<AsVO> getAsSearch(int startIndexNo, int pageSize, String search, String searchString) {
		return asDAO.getAsSearch(startIndexNo, pageSize, search, searchString);
	}

}
