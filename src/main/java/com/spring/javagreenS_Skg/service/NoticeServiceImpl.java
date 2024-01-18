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

import com.spring.javagreenS_Skg.dao.NoticeDAO;
import com.spring.javagreenS_Skg.vo.NoticeVO;

@Service
public class NoticeServiceImpl implements NoticeService {

	@Autowired
	NoticeDAO noticeDAO;

	/*
	// 공지사항에 글을 올릴 때 그림파일도 함께 저장할 수 있도록 처리
	@Override
	public void imgCheck(String content) {
	    // content에 그림 파일(img src="/)이 없으면 처리 종료
	    if (!content.contains("src=\"/")) {
	        return;
	    }

	    HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
	    String uploadPath = request.getSession().getServletContext().getRealPath("/resources/data/ckeditor/");
	    // <p><img alt="" src="/javagreenS_Skg/data/ckeditor/notice/240117185749_스크린샷 2024-01-08 143137.png" style="height:36px; width:596px" /></p>
	    
	    int position = 35;
	    String imgSrc = "src=\"/";

	    // content에서 파일명부터 끝까지의 이름을 추출해서 nextImg에 저장
	    String nextImg = content.substring(content.indexOf(imgSrc) + position);
	    
	    System.out.println("nextImg : " + nextImg);
	    // 240117190817_스크린샷 2024-01-08 150419.png" style="height:75px; width:129px" /></p>
	    
	    boolean sw = true;
	    
    	while (sw) {
	        // 현재 이미지 파일의 경로를 추출
	        String imgFile = nextImg.substring(0, nextImg.indexOf("\""));
	        System.out.println("imgFile : " + imgFile);
	        // 240117190817_스크린샷 2024-01-08 150419.png
	        
	        String oriFilePath = uploadPath + imgFile;
	        String copyFilePath = uploadPath + "notice/" + imgFile;
	        
	        fileCopyCheck(oriFilePath, copyFilePath); // notice 폴더에 파일 복사

	        // nextImg = nextImg.substring(nextImg.indexOf(imgSrc) + position);
	        sw = false;
	    }
	}
	*/
	
	// 공지사항에 글을 올릴 때 그림파일도 함께 저장할 수 있도록 처리
	@Override
	public void imgCheck(String content) {
	    // content에 그림 파일(img src="/)이 없으면 처리 종료
	    if (!content.contains("src=\"/")) {
	        return;
	    }

	    HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
	    String uploadPath = request.getSession().getServletContext().getRealPath("/resources/data/ckeditor/");
	    
	    int position = 35;
	    String imgSrc = "src=\"/";
	    // content에서 파일명부터 끝까지의 이름을 추출해서 nextImg에 저장
	    String nextImg = content.substring(content.indexOf(imgSrc) + position);
	    
	    boolean sw = true;
	    
    	while (sw) {
	        // 현재 이미지 파일의 경로를 추출
	        String imgFile = nextImg.substring(0, nextImg.indexOf("\""));
	        
	        String oriFilePath = uploadPath + imgFile;
	        String copyFilePath = uploadPath + "notice/" + imgFile;
	        
	        fileCopyCheck(oriFilePath, copyFilePath); // notice 폴더에 파일 복사

	        sw = false;
	    }
	}
	
	// oriFilePath에 위치한 파일을 읽어와 copyFilePath로 지정한 위치에 복사
	private void fileCopyCheck(String oriFilePath, String copyFilePath) {
		File oriFile = new File(oriFilePath);
		File copyFile = new File(copyFilePath);
		
		try {
			// oriFile에서 데이터를 읽어오는 스트림(fis)을 생성
			FileInputStream fis = new FileInputStream(oriFile);
			// copyFile에 데이터를 쓰는 스트림(fos)을 생성
			FileOutputStream fos = new FileOutputStream(copyFile);
			
			byte[] buffer = new byte[2048];
			int count = 0;
			
			// read 메소드는 읽은 바이트 수를 반환(count 변수에 읽은 바이트 수가 저장)
			while((count = fis.read(buffer)) != -1) {
				// fos에 buffer 배열의 0번 째부터 count 크기만큼 write
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
	
	//공지사항 등록
	@Override
	public void setNoticeInput(NoticeVO vo) {
		noticeDAO.setNoticeInput(vo);
	}

	//공지사항 리스트
	@Override
	public List<NoticeVO> getNoticeList(int startIndexNo, int pageSize) {
		return noticeDAO.getNoticeList(startIndexNo, pageSize);
	}

	@Override
	public void setReadNum(int idx) {
		noticeDAO.setReadNum(idx);
		
	}

	@Override
	public NoticeVO getNoticeContent(int idx) {
		return noticeDAO.getNoticeContent(idx);
	}

	@Override
	public ArrayList<NoticeVO> getPreNext(int idx) {
		return noticeDAO.getPreNext(idx);
	}

	@Override
	public int getMinIdx() {
		return noticeDAO.getMinIdx();
	}

	// 공지사항의 수정하기를 누르면 수정하기 전의 notice 파일의 사진은 삭제
	@Override
	public void imgDelete(String content) {
		
		// 이미지 파일이 포함되어 있지 않은 경우에는 메소드를 종료
		if(content.indexOf("src=\"/") ==-1) return;
		
		HttpServletRequest request = ((ServletRequestAttributes)RequestContextHolder.currentRequestAttributes()).getRequest();
		String uploadPath = request.getSession().getServletContext().getRealPath("/resources/data/ckeditor/notice/");
		
		int position=42;
		String nextImg = content.substring(content.indexOf("src=\"/")+position);
		
		boolean sw = true;
		
		while(sw) {
			String imgFile = nextImg.substring(0,nextImg.indexOf("\""));
			String oriFilePath = uploadPath + imgFile;
			
			fileDelete(oriFilePath); // notice 폴더 존재하는 파일 삭제.
			
			if(nextImg.indexOf("src=\"/")==-1) {
				sw = false;
			}
			else {
				nextImg = nextImg.substring(nextImg.indexOf("src=\"/")+position);
			}
		}
	}
	
	// 원본 이미지 삭제처리(resources/data/ckeditor/notice폴더에서 삭제)
	// delete()는 파일이나 디렉토리를 삭제하는 Java의 File 클래스의 메서드
	private void fileDelete(String oriFilePath) {
		File delFile = new File(oriFilePath);
		if(delFile.exists()) delFile.delete();
	}

	//DB에서 게시글 삭제
	@Override
	public void setNoticeDelete(int idx) {
		noticeDAO.setNoticeDelete(idx); 
	}

	// 공지사항 수정(게시글의 내용(content)에서 이미지 파일의 포함 여부를 확인하여 게시글의 사진을 CKEditor에 복사)
	@Override
	public void imgCheckUpdate(String content) {

		// 이미지 파일이 포함되어 있지 않은 경우에는 메소드를 종료
		if(content.indexOf("src=\"/") == -1) return;
		
		HttpServletRequest request = ((ServletRequestAttributes)RequestContextHolder.currentRequestAttributes()).getRequest();
		String uploadPath = request.getSession().getServletContext().getRealPath("/resources/data/ckeditor/notice/");
		
		int position=42;
		String nextImg = content.substring(content.indexOf("src=\"/")+position);
		
		boolean sw= true;
		
		while(sw) {
			String imgFile = nextImg.substring(0,nextImg.indexOf("\""));
			String oriFilePath = uploadPath + imgFile;
			String copyFilePath = request.getRealPath("/resources/data/ckeditor/"+imgFile);
			
			// notice 폴더에 존재하는 파일을 ckeditor 폴더에 복사
			fileCopyCheck(oriFilePath, copyFilePath);
			
			if(nextImg.indexOf("src=\"/")==-1) {
				sw=false;
			}
			else {
				nextImg = nextImg.substring(nextImg.indexOf("src=\"/")+position);
			}
		}
	}
 
	@Override
	public void setNoticeUpdate(NoticeVO vo) {
		noticeDAO.setNoticeUpdate(vo);
	}

	@Override
	public List<NoticeVO> getNoticeSearch(int startIndexNo, int pageSize, String search, String searchString) {
		return noticeDAO.getNoticeSearch(startIndexNo, pageSize, search, searchString);
	}
}
