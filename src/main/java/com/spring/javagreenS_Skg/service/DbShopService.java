package com.spring.javagreenS_Skg.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.spring.javagreenS_Skg.vo.DbBaesongVO;
import com.spring.javagreenS_Skg.vo.DbCartListVO;
import com.spring.javagreenS_Skg.vo.DbOptionVO;
import com.spring.javagreenS_Skg.vo.DbOrderVO;
import com.spring.javagreenS_Skg.vo.DbProductQnAVO;
import com.spring.javagreenS_Skg.vo.DbProductReviewVO;
import com.spring.javagreenS_Skg.vo.DbProductVO;
import com.spring.javagreenS_Skg.vo.PayMentVO;

public interface DbShopService {

	public DbProductVO getCategoryMainOne(String categoryMainCode, String categoryMainName);

	public void setCategoryMainInput(DbProductVO vo);

	public List<DbProductVO> getCategoryMain();

	public List<DbProductVO> getCategorySub();

	public List<DbProductVO> getCategorySubOne(DbProductVO vo);

	public void delCategoryMain(String categoryMainCode);

	public void setCategorySubInput(DbProductVO vo);

	public List<DbProductVO> getDbProductOne(String categorySubCode);

	public void delCategorySub(String categorySubCode);

	public List<DbProductVO> getCategorySubName(String categoryMainCode);

	public List<DbProductVO> getSubTitle();

	public List<DbProductVO> getDbShopList(String categorySubCode, int startIndexNo, int pageSize);

	public DbProductVO getDbShopProduct(int idx);

	public int imgCheckProductInput(MultipartFile file, DbProductVO vo);

	public String[] getProductName();

	public List<DbProductVO> getProductInfor(String productName);

	public List<DbOptionVO> getOptionList(int productIdx);

	public int getOptionSame(int productIdx, String optionName);

	public void setDbOptionInput(DbOptionVO vo);

	public void setOptionDelete(int idx);

	public List<DbOptionVO> getDbShopOption(int productIdx);

	public DbProductVO getDbShopContent(int idx);

	public void imgDelete(String content);

	public void setDbShopDelete(int idx);

	public void imgCheckUpdate(String content);

	public ArrayList<DbProductReviewVO> getProductReview(int idx);

	public DbCartListVO getDbCartListProductOptionSearch(String productName, String optionName, String mid);

	public void dbShopCartUpdate(DbCartListVO vo);

	public void dbShopCartInput(DbCartListVO vo);

	public List<DbCartListVO> getDbCartList(String mid);

	public DbOrderVO getOrderMaxIdx();

	public DbCartListVO getCartIdx(int idx);

	public void dbCartDelete(int idx);

	public List<DbBaesongVO> getOrderBaesong(String orderIdx);

	public List<DbOrderVO> getMyOrderList(int startIndexNo, int pageSize, String mid);

	public List<DbBaesongVO> getOrderCondition(String mid, int conditionDate, int startIndexNo, int pageSize);

	public List<DbBaesongVO> getMyOrderStatus(int startIndexNo, int pageSize, String mid, String startJumun, String endJumun, String conditionOrderStatus);

	public List<DbBaesongVO> getAdminOrderStatus(int startIndexNo, int pageSize, String startJumun, String endJumun, String orderStatus);

	public List<DbProductVO> getProductSearch(int startIndexNo, int pageSize, String searchString);

	public List<DbBaesongVO> getOrderStatus(String mid, String orderStatus, int startIndexNo, int pageSize);

	public List<DbProductVO> getDbShopMainCodeList(String categoryMainCode, int startIndexNo, int pageSize);

	public void setOrderStatusUpdate(String orderIdx, String orderStatus);

	public ArrayList<DbProductQnAVO> getProductQnA(int idx);

	public void setQnAInput(DbProductQnAVO dbProductQnAVO);

	public void setdbShopQnADelete(int idx);

	public String maxLevelOrder(int productIdx);



	public int getCartCount(String mid);

	public ArrayList<DbProductReviewVO> getReviewBoardList(String mid, int startIndexNo, int pageSize);

	public ArrayList<DbProductQnAVO> getQnABoardList(String mid, int startIndexNo, int pageSize);

	public ArrayList<DbProductQnAVO> getAdQnABoardList(int startIndexNo, int pageSize);

	public void setReviewInput(DbProductReviewVO vo);

	public void setdbShopReviewDelete(int idx);

	public String getQrCode(int idx);


	// 댓글, 답글 트랜잭션 처리
	
    void qnaInput2Transaction(DbProductQnAVO dbProductQnAVO);
    
	/*
	public int getMaxLevelOrder(int productIdx, int level, int levelOrder);
	
	public void levelOrderPlusUpdate(DbProductQnAVO dbProductQnAVO);
	
	public void setQnAInput2(DbProductQnAVO dbProductQnAVO);
	*/
	
	// 결제 트랜잭션 처리

	public void processPaymentAndOrder(List<DbOrderVO> orderVos, PayMentVO receivePayMentVo, DbBaesongVO baesongVo);

	/*
	public void setDbOrder(DbOrderVO vo);
	
	public void dbCartDeleteAll(int cartIdx);
	
	public void setDbBaesong(DbBaesongVO baesongVo);
	
	public void setMemberPointPlus(int point, String mid);
	 */

}
