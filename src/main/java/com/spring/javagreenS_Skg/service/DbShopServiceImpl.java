package com.spring.javagreenS_Skg.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import com.spring.javagreenS_Skg.dao.DbShopDAO;
import com.spring.javagreenS_Skg.vo.DbBaesongVO;
import com.spring.javagreenS_Skg.vo.DbCartListVO;
import com.spring.javagreenS_Skg.vo.DbOptionVO;
import com.spring.javagreenS_Skg.vo.DbOrderVO;
import com.spring.javagreenS_Skg.vo.DbProductQnAVO;
import com.spring.javagreenS_Skg.vo.DbProductReviewVO;
import com.spring.javagreenS_Skg.vo.DbProductVO;
import com.spring.javagreenS_Skg.vo.NoticeVO;
import com.spring.javagreenS_Skg.vo.PayMentVO;

@Service
public class DbShopServiceImpl implements DbShopService {

	@Autowired
	DbShopDAO dbShopDAO;

	@Override
	public DbProductVO getCategoryMainOne(String categoryMainCode, String categoryMainName) {
		return dbShopDAO.getCategoryMainOne(categoryMainCode, categoryMainName);
	}

	@Override
	public void setCategoryMainInput(DbProductVO vo) {
		dbShopDAO.setCategoryMainInput(vo);
	}

	@Override
	public List<DbProductVO> getCategoryMain() {
		return dbShopDAO.getCategoryMain();
	}

	@Override
	public List<DbProductVO> getCategorySub() {
		return dbShopDAO.getCategorySub();
	}

	@Override
	public List<DbProductVO> getCategorySubOne(DbProductVO vo) {
		return dbShopDAO.getCategorySubOne(vo);
	}

	@Override
	public void delCategoryMain(String categoryMainCode) {
		dbShopDAO.delCategoryMain(categoryMainCode);
	}

	@Override
	public void setCategorySubInput(DbProductVO vo) {
		dbShopDAO.setCategorySubInput(vo);
	}

	@Override
	public List<DbProductVO> getDbProductOne(String categorySubCode) {
		return dbShopDAO.getDbProductOne(categorySubCode);
	}

	@Override
	public void delCategorySub(String categorySubCode) {
		dbShopDAO.delCategorySub(categorySubCode);
	}

	@Override
	public List<DbProductVO> getCategorySubName(String categoryMainCode) {
		return dbShopDAO.getCategorySubName(categoryMainCode);
	}

	
	@Override
	public List<DbProductVO> getSubTitle() {
		return dbShopDAO.getSubTitle();
	}

	@Override
	public List<DbProductVO> getDbShopList(String categorySubCode,int startIndexNo, int pageSize) {
		return dbShopDAO.getDbShopList(categorySubCode,startIndexNo,pageSize);
	}

	@Override
	public DbProductVO getDbShopProduct(int idx) {
		return dbShopDAO.getDbShopProduct(idx);
	}

	
	@Override 
	public List<DbOptionVO> getDbShopOption(int productIdx) { 
		return dbShopDAO.getDbShopOption(productIdx); 
	}
	 

	@Override
	public int imgCheckProductInput(MultipartFile file, DbProductVO vo) {
		// 먼저 기본(메인)그림파일은 'dbShop/product'폴더에 업로드 시켜준다.
		try {
			String originalFilename = file.getOriginalFilename();
			if(originalFilename != null && originalFilename != "") {
				// 상품 메인사진을 업로드처리하기위해 중복파일명처리와 업로드처리
				Date date = new Date();
				SimpleDateFormat sdf = new SimpleDateFormat("yyMMddHHmmss");
				String saveFileName = sdf.format(date) + "_" + originalFilename;
				writeFile(file, saveFileName);	// 메일 이미지를 서버에 업로드 시켜주는 메소드 호출
				vo.setFName(originalFilename);	// 업로드시 파일명을 fName에 저장
				vo.setFSName(saveFileName);		// 서버에 저장된 파일명을 vo에 set시켜준다.
			}
			else {
				return 0;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		//             0         1         2         3         4         5
		//             012345678901234567890123456789012345678901234567890
		// <img alt="" src="/javagreenS_Skg/data/dbShop/211229124318_4.jpg"
		// <img alt="" src="/javagreenS_Skg/data/dbShop/product/211229124318_4.jpg"
		
		// ckeditor을 이용해서 담은 상품의 상세설명내역에 그림이 포함되어 있으면 그림을 dbShop/product폴더로 복사작업처리 시켜준다.
		String content = vo.getContent();
		if(content.indexOf("src=\"/") == -1) return 0;		// content박스의 내용중 그림이 없으면 돌아간다.
		
		HttpServletRequest request = ((ServletRequestAttributes)RequestContextHolder.currentRequestAttributes()).getRequest();
		String uploadPath = request.getRealPath("/resources/data/dbShop/");
		
		int position = 33;
		String nextImg = content.substring(content.indexOf("src=\"/") + position);
		boolean sw = true;
		
		while(sw) {
			String imgFile = nextImg.substring(0,nextImg.indexOf("\""));
			String copyFilePath = "";
			String oriFilePath = uploadPath + imgFile;	// 원본 그림이 들어있는 '경로명+파일명'
			
			copyFilePath = uploadPath + "product/" + imgFile;	// 복사가 될 '경로명+파일명'
			
			fileCopyCheck(oriFilePath, copyFilePath);	// 원본그림이 복사될 위치로 복사작업처리하는 메소드
			
			if(nextImg.indexOf("src=\"/") == -1) sw = false;
			else nextImg = nextImg.substring(nextImg.indexOf("src=\"/") + position);
		}
		// 이미지 복사작업이 종료되면 실제로 저장된 'dbShop/product'폴더명을 vo에 set시켜줘야 한다.
		vo.setContent(vo.getContent().replace("/data/dbShop/", "/data/dbShop/product/"));
		// 파일 복사작업이 모두 끝나면 vo에 담긴내용을 상품의 내역을 DB에 저장한다.
		// 먼저 productCode를 만들어주기 위해 지금까지 작업된 dbProduct테이블의 idx필드중 최대값을 읽어온다. 없으면 0으로 처리한다.
		int maxIdx = 0;
		DbProductVO maxVo = dbShopDAO.getProductMaxIdx();
		if(maxVo != null) {
			maxIdx = maxVo.getIdx() + 1;
			vo.setIdx(maxIdx);
		}
		vo.setProductCode(vo.getCategoryMainCode()+vo.getCategorySubCode()+maxIdx);
		return dbShopDAO.setDbProductInput(vo);
	}

	// 실제 파일(dbShop폴더)을 'dbShop/product'폴더로 복사처리하는곳
	private void fileCopyCheck(String oriFilePath, String copyFilePath) {
		File oriFile = new File(oriFilePath);
		File copyFile = new File(copyFilePath);
		
		try {
			FileInputStream  fis = new FileInputStream(oriFile);
			FileOutputStream fos = new FileOutputStream(copyFile);
			
			byte[] buffer = new byte[2048];
			int count = 0;
			while((count = fis.read(buffer)) != -1) {
				fos.write(buffer, 0, count);
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

	//메인 상품 이미지 서버에 저장하기
	private void writeFile(MultipartFile fName, String saveFileName) throws IOException{
		byte[] data = fName.getBytes();
		
		HttpServletRequest request = ((ServletRequestAttributes)RequestContextHolder.currentRequestAttributes()).getRequest();
		String uploadPath = request.getSession().getServletContext().getRealPath("/resources/data/dbShop/product/");
		
		FileOutputStream fos = new FileOutputStream(uploadPath + saveFileName);
		fos.write(data);
		fos.close();
		
	}

	@Override
	public String[] getProductName() {
		return dbShopDAO.getProductName();
	}

	@Override
	public List<DbProductVO> getProductInfor(String productName) {
		return dbShopDAO.getProductInfor(productName);
	}

	@Override
	public List<DbOptionVO> getOptionList(int productIdx) {
		return dbShopDAO.getOptionList(productIdx);
	}

	@Override
	public int getOptionSame(int productIdx, String optionName) {
		return dbShopDAO.getOptionSame(productIdx, optionName);
	}

	@Override
	public void setDbOptionInput(DbOptionVO vo) {
		dbShopDAO.setDbOptionInput(vo);
	}

	@Override
	public void setOptionDelete(int idx) {
		dbShopDAO.setOptionDelete(idx);
	}

	@Override
	public DbProductVO getDbShopContent(int idx) {
		return dbShopDAO.getDbShopContent(idx);
	}

	// Product의 ckeditor에서 올린 이미지파일 삭제
	@Override
	public void imgDelete(String content) {
		// 		                   1         2         3         4         5         6
		//             012345678901234567890123456789012345678901234567890123456789012345678901234567890
		// <img alt="" src="/javagreenS_Skg/data/dbShop/product/211229124318_4.jpg"
		
		if(content.indexOf("src=\"/") ==-1) return;
		
		HttpServletRequest request = ((ServletRequestAttributes)RequestContextHolder.currentRequestAttributes()).getRequest();
		String uploadPath = request.getSession().getServletContext().getRealPath("/resources/data/dbShop/product/");
		
		int position=41;
		String nextImg = content.substring(content.indexOf("src=\"/")+position);
		boolean sw = true;
		
		while(sw) {
			String imgFile = nextImg.substring(0,nextImg.indexOf("\""));
			String oriFilePath = uploadPath + imgFile;
			
			fileDelete(oriFilePath);  //product 폴더 존재하는 파일 삭제.(ckeditor에서 올린 그림파일만 삭제처리함)
			
			if(nextImg.indexOf("src=\"/")==-1) {
				sw = false;
			}
			else {
				nextImg = nextImg.substring(nextImg.indexOf("src=\"/")+position);
			}
		}
		
	}
	
	// 원본 이미지 삭제처리(resources/data/dbShop/product폴더에서 삭제)
	private void fileDelete(String oriFilePath) {
		File delFile = new File(oriFilePath);
		if(delFile.exists()) delFile.delete();
	}

	// db에서 게시글 삭제
	@Override
	public void setDbShopDelete(int idx) {
		dbShopDAO.setDbShopDelete(idx);
	}

	// 수정
	@Override
	public void imgCheckUpdate(String content) {
		// 						 0         1         2         3         4         5
		//             012345678901234567890123456789012345678901234567890
		// <img alt="" src="/javagreenS_Skg/data/dbShop/211229124318_4.jpg"
		// <img alt="" src="/javagreenS_Skg/data/dbShop/product/211229124318_4.jpg"
		
		if(content.indexOf("src=\"/") == -1) return;
		
		HttpServletRequest request = ((ServletRequestAttributes)RequestContextHolder.currentRequestAttributes()).getRequest();
		String uploadPath = request.getSession().getServletContext().getRealPath("/resources/data/dbShop/product/");
		
		int position=41;
		String nextImg = content.substring(content.indexOf("src=\"/")+position);
		boolean sw= true;
		
		while(sw) {
			String imgFile = nextImg.substring(0,nextImg.indexOf("\""));
			String oriFilePath = uploadPath + imgFile;
			String coptFilePath = request.getRealPath("/resources/data/dbShop/"+imgFile);
			
			fileCopyCheck(oriFilePath, coptFilePath); // product 폴더에 존재하는 파일을 dbShop/에 복사
			
			if(nextImg.indexOf("src=\"/")==-1) {
				sw=false;
			}
			else {
				nextImg = nextImg.substring(nextImg.indexOf("src=\"/")+position);
			}
		}
	}

	@Override
	public ArrayList<DbProductReviewVO> getProductReview(int idx) {
		return dbShopDAO.getProductReview(idx);
	}

	@Override
	public DbCartListVO getDbCartListProductOptionSearch(String productName, String optionName, String mid) {
		return dbShopDAO.getDbCartListProductOptionSearch(productName, optionName, mid);
	}

	@Override
	public void dbShopCartUpdate(DbCartListVO vo) {
		dbShopDAO.dbShopCartUpdate(vo);
	}

	@Override
	public void dbShopCartInput(DbCartListVO vo) {
		dbShopDAO.dbShopCartInput(vo);
	}

	@Override
	public List<DbCartListVO> getDbCartList(String mid) {
		return dbShopDAO.getDbCartList(mid);
	}

	@Override
	public DbOrderVO getOrderMaxIdx() {
		return dbShopDAO.getOrderMaxIdx();
	}

	@Override
	public DbCartListVO getCartIdx(int idx) {
		return dbShopDAO.getCartIdx(idx);
	}

	@Override
	public void dbCartDelete(int idx) {
		dbShopDAO.dbCartDelete(idx);
	}

	@Override
	public List<DbBaesongVO> getOrderBaesong(String orderIdx) {
		return dbShopDAO.getOrderBaesong(orderIdx);
	}

	@Override
	public List<DbOrderVO> getMyOrderList(int startIndexNo, int pageSize, String mid) {
		return dbShopDAO.getMyOrderList(startIndexNo, pageSize, mid);
	}

	@Override
	public List<DbBaesongVO> getOrderCondition(String mid, int conditionDate, int startIndexNo, int pageSize) {
		return dbShopDAO.getOrderCondition(mid, conditionDate, startIndexNo, pageSize);
	}

	@Override
	public List<DbBaesongVO> getMyOrderStatus(int startIndexNo, int pageSize, String mid, String startJumun, String endJumun, String conditionOrderStatus) {
		return dbShopDAO.getMyOrderStatus(startIndexNo, pageSize, mid, startJumun, endJumun,	conditionOrderStatus);
	}

	@Override
	public List<DbBaesongVO> getAdminOrderStatus(int startIndexNo, int pageSize, String startJumun, String endJumun, String orderStatus) {
		return dbShopDAO.getAdminOrderStatus(startIndexNo, pageSize, startJumun, endJumun, orderStatus);
	}

	@Override
	public List<DbProductVO> getProductSearch(int startIndexNo, int pageSize, String searchString) {
		return dbShopDAO.getProductSearch(startIndexNo, pageSize, searchString);
	}

	@Override
	public List<DbBaesongVO> getOrderStatus(String mid, String orderStatus, int startIndexNo, int pageSize) {
		return dbShopDAO.getOrderStatus(mid, orderStatus, startIndexNo, pageSize);
	}

	@Override
	public List<DbProductVO> getDbShopMainCodeList(String categoryMainCode, int startIndexNo, int pageSize) {
		return dbShopDAO.getDbShopMainCodeList(categoryMainCode, startIndexNo, pageSize);
	}

	@Override
	public void setOrderStatusUpdate(String orderIdx, String orderStatus) {
		dbShopDAO.setOrderStatusUpdate(orderIdx, orderStatus);
	}

	@Override
	public ArrayList<DbProductQnAVO> getProductQnA(int idx) {
		return dbShopDAO.getProductQnA(idx);
	}

	@Override
	public void setQnAInput(DbProductQnAVO dbProductQnAVO) {
		dbShopDAO.setQnAInput(dbProductQnAVO);
	}

	@Override
	public void setdbShopQnADelete(int idx) {
		dbShopDAO.setdbShopQnADelete(idx);
	}

	@Override
	public String maxLevelOrder(int productIdx) {
		return dbShopDAO.maxLevelOrder(productIdx);
	}

	@Override
	public int getCartCount(String mid) {
		return dbShopDAO.getCartCount(mid);
	}

	@Override
	public ArrayList<DbProductReviewVO> getReviewBoardList(String mid, int startIndexNo, int pageSize) {
		return dbShopDAO. getReviewBoardList(mid, startIndexNo, pageSize);
	}

	@Override
	public ArrayList<DbProductQnAVO> getQnABoardList(String mid, int startIndexNo, int pageSize) {
		return dbShopDAO.getQnABoardList(mid, startIndexNo, pageSize);
	}

	@Override
	public ArrayList<DbProductQnAVO> getAdQnABoardList(int startIndexNo, int pageSize) {
		return dbShopDAO.getAdQnABoardList(startIndexNo, pageSize);
	}

	@Override
	public void setReviewInput(DbProductReviewVO vo) {
		dbShopDAO.setReviewInput(vo);
	}

	@Override
	public void setdbShopReviewDelete(int idx) {
		dbShopDAO.setdbShopReviewDelete(idx);
	}

	@Override
	public String getQrCode(int idx) {
		return dbShopDAO.getQrCode(idx);
	}
	
	// 댓글, 답글 트랜잭션 처리
	@Override
	@Transactional
	public void qnaInput2Transaction(DbProductQnAVO dbProductQnAVO) {
	    try {
			// 같은 상품의 같은 level의 댓글로 범위 제한
			// 현재 level의 댓글의 levelOrder의 값보다 큰 값 중에 최솟값(바로 다음으로 큰 값)을 가져오기
	        int maxLevelOrder = dbShopDAO.getMaxLevelOrder(
	                dbProductQnAVO.getProductIdx(), 
	                dbProductQnAVO.getLevel(), 
	                dbProductQnAVO.getLevelOrder()
	                );
	        // 가져온 값을 자식 댓글의 levelOrder로 사용
	        dbProductQnAVO.setLevelOrder(maxLevelOrder);
	        // 자식의 level은 부모 level보다 +1 시켜준다.
	        dbProductQnAVO.setLevel(dbProductQnAVO.getLevel() + 1);
	        // 부모댓글의 levelOrder값보다 크거나 같은 모든 댓글의 levelOrder값을 +1.(update)
	        dbShopDAO.levelOrderPlusUpdate(dbProductQnAVO);
	        // 수정된 dbProductQnAVO를 저장(insert)
	        dbShopDAO.setQnAInput2(dbProductQnAVO);
	    } catch (Exception e) {
	    	// 예외가 발생하면 롤백
	        throw new RuntimeException("Transaction failed: " + e.getMessage(), e);
	    }
	}

	/*
	@Override
	public int getMaxLevelOrder(int productIdx, int level ,int levelOrder) {
		return dbShopDAO.getMaxLevelOrder(productIdx, level ,levelOrder);
	}
	
	@Override
	public void levelOrderPlusUpdate(DbProductQnAVO dbProductQnAVO) {
		dbShopDAO.levelOrderPlusUpdate(dbProductQnAVO);
	}

	@Override
	public void setQnAInput2(DbProductQnAVO dbProductQnAVO) {
		dbShopDAO.setQnAInput2(dbProductQnAVO);
	}
	*/

	// 결제 트랜잭션 처리
    @Override
    @Transactional
    public void processPaymentAndOrder(List<DbOrderVO> orderVos, PayMentVO receivePayMentVo, DbBaesongVO baesongVo) {
        try {
        	
            for (DbOrderVO vo : orderVos) {
            	// dborder2 테이블에 orderIdx 저장(같은 주문은 같은 orderIdx 값)
                vo.setIdx(Integer.parseInt(vo.getOrderIdx().substring(8)));
                // 주문 내역을 주문테이블(dbOrder)에 저장
                dbShopDAO.setDbOrder(vo);
                // 주문 내역을 장바구니(dbCartList)에서 삭제
                dbShopDAO.dbCartDeleteAll(vo.getCartIdx());
            }

            // 주문 테이블의 idx 값을 배송 테이블의 oIdx 값에 저장
            baesongVo.setOIdx(orderVos.get(0).getIdx());
            // 주문 테이블의 orderIdx 값을 배송 테이블의 orderIdx 값에 저장
            baesongVo.setOrderIdx(orderVos.get(0).getOrderIdx());

            // 배송 내용을 배송 테이블(dbBaesong)에 저장
            dbShopDAO.setDbBaesong(baesongVo);

            // 회원 테이블에 포인트 적립하기(1%)
            dbShopDAO.setMemberPointPlus((int) (baesongVo.getOrderTotalPrice() * 0.01), orderVos.get(0).getMid());
        } catch (Exception e) {
            // 예외가 발생하면 롤백
            throw new RuntimeException("Transaction failed: " + e.getMessage(), e);
        }
    }

    /*
	@Override
	public void setDbOrder(DbOrderVO vo) {
		dbShopDAO.setDbOrder(vo);
	}

	@Override
	public void dbCartDeleteAll(int cartIdx) {
		dbShopDAO.dbCartDeleteAll(cartIdx);
	}

	@Override
	public void setDbBaesong(DbBaesongVO baesongVo) {
		System.out.println("baesongVo : " + baesongVo);
		dbShopDAO.setDbBaesong(baesongVo);
	}

	@Override
	public void setMemberPointPlus(int point, String mid) {
		dbShopDAO.setMemberPointPlus(point, mid);
	}
	*/
	
}
