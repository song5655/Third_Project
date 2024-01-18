package com.spring.javagreenS_Skg;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.spring.javagreenS_Skg.pagination.PageProcess;
import com.spring.javagreenS_Skg.pagination.PageVO;
import com.spring.javagreenS_Skg.service.DbShopService;
import com.spring.javagreenS_Skg.service.MemberService;
import com.spring.javagreenS_Skg.vo.DbBaesongVO;
import com.spring.javagreenS_Skg.vo.DbCartListVO;
import com.spring.javagreenS_Skg.vo.DbOptionVO;
import com.spring.javagreenS_Skg.vo.DbOrderVO;
import com.spring.javagreenS_Skg.vo.DbProductQnAVO;
import com.spring.javagreenS_Skg.vo.DbProductReviewVO;
import com.spring.javagreenS_Skg.vo.DbProductVO;
import com.spring.javagreenS_Skg.vo.MemberVO;
import com.spring.javagreenS_Skg.vo.PayMentVO;

@Controller
@RequestMapping("/dbShop")
public class DbShopController {
	String msgFlag = "";

	@Autowired
	DbShopService dbShopService;

	@Autowired
	MemberService memberService;

	@Autowired
	PageProcess pageProcess;

	// 모든 분류목록 출력하기(처음화면의 '대/중/소' 분류 등록 및 조회 창 보여주기)
	@RequestMapping(value = "/dbCategory", method = RequestMethod.GET)
	public String dbCategoryGet(Model model) {
		List<DbProductVO> mainVos = dbShopService.getCategoryMain();
		List<DbProductVO> subVos = dbShopService.getCategorySub();

		model.addAttribute("mainVos", mainVos);
		model.addAttribute("subVos", subVos);

		return "admin/dbShop/dbCategory";
	}

	// 대분류 등록하기
	@ResponseBody
	@RequestMapping(value = "/categoryMainInput", method = RequestMethod.POST)
	public String categoryMainInputPost(DbProductVO vo) {
		// 기존에 같은이름의 대분류가 있는지를 체크한다.
		DbProductVO imsiVo = dbShopService.getCategoryMainOne(vo.getCategoryMainCode(), vo.getCategoryMainName());

		if (imsiVo != null)
			return "0";
		dbShopService.setCategoryMainInput(vo); // 대분류항목 저장
		return "1";
	}

	// 대분류 삭제하기
	@ResponseBody
	@RequestMapping(value = "/delCategoryMain", method = RequestMethod.POST)
	public String delCategoryMainPost(DbProductVO vo) {
		List<DbProductVO> vos = dbShopService.getCategorySubOne(vo);
		if (vos.size() != 0)
			return "0";
		dbShopService.delCategoryMain(vo.getCategoryMainCode());
		return "1";
	}

	// 소분류 등록하기
	@ResponseBody
	@RequestMapping(value = "/categorySubInput", method = RequestMethod.POST)
	public String categorySubInputPost(DbProductVO vo) {
		List<DbProductVO> vos = dbShopService.getCategorySubOne(vo);
		if (vos.size() != 0)
			return "0";
		dbShopService.setCategorySubInput(vo);
		return "1";
	}

	// 소분류 삭제하기
	@ResponseBody
	@RequestMapping(value = "/delCategorySub", method = RequestMethod.POST)
	public String delCategorySubPost(DbProductVO vo) {
		List<DbProductVO> vos = dbShopService.getDbProductOne(vo.getCategorySubCode());
		if (vos.size() != 0)
			return "0";
		dbShopService.delCategorySub(vo.getCategorySubCode());
		return "1";
	}

	// 상품 등록을 위한 목록창 보여주기
	@RequestMapping(value = "/dbProduct", method = RequestMethod.GET)
	public String dbProductGet(Model model) {
		List<DbProductVO> mainVos = dbShopService.getCategoryMain();
		model.addAttribute("mainVos", mainVos);
		return "admin/dbShop/dbProduct";
	}

	// 대분류 선택시 소분류명 가져오기
	@ResponseBody
	@RequestMapping(value = "/categorySubName", method = RequestMethod.POST)
	public List<DbProductVO> categorySubNamePost(String categoryMainCode) {
		return dbShopService.getCategorySubName(categoryMainCode);
	}

	// 관리자 상품등록, 상품상세설명(ckeditor)에 이미지를 올리면 "data/dbShop/" 에 저장
	@ResponseBody
	@RequestMapping("/imageUpload")
	public void imageUploadGet(HttpServletRequest request, HttpServletResponse response,
			@RequestParam MultipartFile upload) throws Exception {
		// 'upload'라는 이름의 MultipartFile 파라미터를 받는다. 이는 클라이언트가 업로드한 파일.
		response.setCharacterEncoding("utf-8");
		response.setContentType("text/html; charset=utf-8");

		// 만약 업로드된 파일이 "example.jpg"라면, originalFilename은 "example.jpg"
		String originalFilename = upload.getOriginalFilename();

		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyMMddHHmmss");
		originalFilename = sdf.format(date) + "_" + originalFilename;

		byte[] bytes = upload.getBytes();

		// ckeditor에서 올린 파일을 서버 파일시스템에 저장시켜준다.
		String uploadPath = request.getSession().getServletContext().getRealPath("/resources/data/dbShop/");
		OutputStream outStr = new FileOutputStream(new File(uploadPath + originalFilename));
		outStr.write(bytes); // 서버에 업로드시킨 그림파일이 저장된다.

		// 서버 파일시스템에 저장된 파일을 화면(textarea)에 출력하기
		PrintWriter out = response.getWriter();
		String fileUrl = request.getContextPath() + "/data/dbShop/" + originalFilename;
		out.println("{\"originalFilename\":\"" + originalFilename + "\",\"uploaded\":1,\"url\":\"" + fileUrl
				+ "\"}"); /* "atom":"12.jpg","uploaded":1,"": */

		out.flush();
		outStr.close();
	}

	// 상품 등록 시키기
	@RequestMapping(value = "/dbProduct", method = RequestMethod.POST)
	public String dbProductPost(MultipartFile file, DbProductVO vo) {
		// 이미지파일 업로드시에는 ckeditor폴더에서 product폴더로 복사작업처리
		int res = dbShopService.imgCheckProductInput(file, vo);
		System.out.println("res : " + res);
		msgFlag = "dbProductInputOk";
		return "redirect:/msg/" + msgFlag;
	}

	// 등록된 상품 보여주기(관리자화면에서 보여주기)
	@RequestMapping(value = "/dbShopList", method = RequestMethod.GET)
	public String dbShopListGet(@RequestParam(name = "pag", defaultValue = "1", required = false) int pag,
			@RequestParam(name = "pageSize", defaultValue = "12", required = false) int pageSize,
			@RequestParam(name = "part", defaultValue = "전체", required = false) String categorySubCode, Model model) {
		PageVO pageVO = null; 
		List<DbProductVO> productVos = null;
		List<DbProductVO> subTitleVos = dbShopService.getSubTitle();
		if(categorySubCode.equals("전체")) {
			pageVO = pageProcess.totRecCnt(pag, pageSize, "dbShop", "", "");
			productVos = dbShopService.getDbShopList(categorySubCode, pageVO.getStartIndexNo(), pageSize);
		}
		else {
			pageVO = pageProcess.totRecCnt(pag, pageSize, "dbShop","categorySubCode", categorySubCode);
			productVos = dbShopService.getDbShopList(categorySubCode, pageVO.getStartIndexNo(), pageSize);
		}
		
		model.addAttribute("subTitleVos", subTitleVos);
		model.addAttribute("part", categorySubCode);
		model.addAttribute("productVos", productVos);
		model.addAttribute("pageVO", pageVO);
		model.addAttribute("categorySubCode", categorySubCode);
		
		return "admin/dbShop/dbShopList";
	}

	// 옵션 등록창 보기
	@RequestMapping(value = "/dbOption", method = RequestMethod.GET)
	public String dbOptionGet(Model model) {
		String[] productNames = dbShopService.getProductName();
		model.addAttribute("productNames", productNames);

		return "admin/dbShop/dbOption";
	}

	// 옵션등록시 상품을 선택하면 상품의 상세설명 가져와서 뿌리기
	@ResponseBody
	@RequestMapping(value = "/getProductInfor", method = RequestMethod.POST)
	public List<DbProductVO> getProductInforPost(String productName) {
		return dbShopService.getProductInfor(productName);
	}

	// 옵션등록시 선택한 상품에 대한 옵션들을 보여주기위한 처리
	@ResponseBody
	@RequestMapping(value = "/getOptionList", method = RequestMethod.POST)
	public List<DbOptionVO> getOptionListPost(int productIdx) {
		return dbShopService.getOptionList(productIdx);
	}

	// 옵션 기록사항 등록하기
	@RequestMapping(value = "/dbOption", method = RequestMethod.POST)
	public String dbOptionPost(DbOptionVO vo, String[] optionName, int[] optionPrice) {
		for (int i = 0; i < optionName.length; i++) {
			// 같은 제품에 같은 옵션이 등록되었으면 skip시킨다.
			int optionCnt = dbShopService.getOptionSame(vo.getProductIdx(), optionName[i]);
			if (optionCnt != 0)
				continue;

			vo.setProductIdx(vo.getProductIdx());
			vo.setOptionName(optionName[i]);

			vo.setOptionPrice(optionPrice[i]);
			dbShopService.setDbOptionInput(vo);
		}
		msgFlag = "dbOptionInputOk";
		return "redirect:/msg/" + msgFlag;
	}

	// 옵션 삭제
	@ResponseBody
	@RequestMapping(value = "/optionDelete", method = RequestMethod.POST)
	public String optionDeletePost(int idx) {
		dbShopService.setOptionDelete(idx);
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
		
		return "dbShop/dbProductContent";
	}

	// 상품 삭제(사진 먼저 삭제하고 DB 삭제)
	@SuppressWarnings("deprecation")
	@ResponseBody
	@RequestMapping(value = "/dbShopDeleteOk", method = RequestMethod.POST)
	public String dbShopDeleteOkPost(HttpServletRequest request, int idx) {
		
		// 게시글에 사진이 있다면 서버에 존재하는 사진을 먼저 삭제.
		DbProductVO vo = dbShopService.getDbShopContent(idx);
		if (vo.getContent().indexOf("src=\"/") != -1) dbShopService.imgDelete(vo.getContent());
    
		// 썸네일 파일(fSName) 삭제처리하기
		String uploadPath = request.getRealPath("/resources/data/dbShop/product/");
		String realPathFile = uploadPath + vo.getFSName();
		new File(realPathFile).delete();
		

		// DB에서 게시글 삭제
		dbShopService.setDbShopDelete(idx);

		return "";
	}

	// 게시글 수정하기 폼 불러오기
	@RequestMapping(value = "/dbProductUpdate", method = RequestMethod.GET)
	public String dbProductUpdateGet(int idx, int pag, int pageSize, Model model) {
		// 수정창으로 들어올때 원본파일에 그림파일이 존재한다면, 현재폴더(product)의 그림파일을 dbShop폴더로 복사시켜둔다.
		DbProductVO vo = dbShopService.getDbShopContent(idx);
		if (vo.getContent().indexOf("src=\"/") != -1)
			dbShopService.imgCheckUpdate(vo.getContent());

		model.addAttribute("vo", vo);
		model.addAttribute("pag", pag);
		model.addAttribute("pageSize", pageSize);

		return "admin/dbShop/dbProductUpdate";
	}

	// nav.jsp의 category(대분류/소분류)로 상품의 목록보기
	@RequestMapping(value = "/dbProductList", method = RequestMethod.GET)
	public String dbProductListGet(
			@RequestParam(name = "categoryMainCode", defaultValue = "", required = false) String categoryMainCode,
			@RequestParam(name = "categorySubCode", defaultValue = "", required = false) String categorySubCode,
			@RequestParam(name = "pag", defaultValue = "1", required = false) int pag,
			@RequestParam(name = "pageSize", defaultValue = "16", required = false) int pageSize, 
			Model model) {
		
		  PageVO pageVO = null; 
		  List<DbProductVO> productVos = null;
		  
		  // categoryMainCode 검색
		  if(!categoryMainCode.equals("")) {
			// select count(*) from dbProduct2 where categoryMainCode = #{searchString}; + 페이징 처리에 필요한 변수
		  	pageVO = pageProcess.totRecCnt(pag, pageSize, "dbShop", "categoryMainCode", categoryMainCode);
		  	// categoryMain2 테이블에서 categoryMainCode에 해당하는 categoryMainName을 가져와 mainName으로 별칭을 붙여서 가져온다.
		  	// categoryMainCode에 해당하는 상품을 dbProduct2 테이블에서 가져와서 페이징 처리된 목록으로 반환한다.
		  	productVos = dbShopService.getDbShopMainCodeList(categoryMainCode, pageVO.getStartIndexNo(), pageSize);
		  }
		  // categorySubCode 검색
		  else {
			  	// SELECT count(*) FROM dbProduct2 where categorySubCode = #{categorySubCode}; + 페이징 처리에 필요한 변수
				pageVO = pageProcess.totRecCnt(pag, pageSize, "dbShop", "categorySubCode", categorySubCode);
				// categorySub2 테이블에서 categorySubCode에 해당하는 categorySubName을 가져와 mainName으로 별칭을 붙여서 가져온다.
				// categorySubCode가 '전체'가 아닌 경우에만 실행되며, 해당 조건에 맞는 결과를 dbProduct2 테이블에서 가져와서 페이징 처리된 목록으로 반환한다.
				productVos = dbShopService.getDbShopList(categorySubCode, pageVO.getStartIndexNo(), pageSize);
		  }
		  model.addAttribute("categorySubCode", categorySubCode);
		  model.addAttribute("categoryMainCode", categoryMainCode);
		  model.addAttribute("pageVO", pageVO);
		  model.addAttribute("productVos", productVos);
		return "dbShop/dbProductList";
	}

	// 진열된 상품 클릭 시 상품내역 상세보기
	@RequestMapping(value = "/dbProductContent", method = RequestMethod.GET)
	public String dbProductContentGet(int idx, Model model) {
		// 선택된 제품의 모든 정보와 해당 제품에 대한 후기 개수(reviewCount)와 문의 개수(qnaCount) 반환
		DbProductVO productVo = dbShopService.getDbShopProduct(idx);
		// 선택된 제품의 옵션 정보 모두 가져오기(옵션은 여러 개 가능, List에 넣는다.)
		List<DbOptionVO> optionVos = dbShopService.getDbShopOption(idx);
		model.addAttribute("productVo", productVo);
		model.addAttribute("optionVos", optionVos);

		// 선택된 제품의 후기 가져오기
		ArrayList<DbProductReviewVO> reviewVos = dbShopService.getProductReview(idx);
		model.addAttribute("reviewVos", reviewVos);
		
		// 선택된 제품의 문의 가져오기(order by levelOrder asc;)
		ArrayList<DbProductQnAVO> qnaVos = dbShopService.getProductQnA(idx);
		model.addAttribute("qnaVos",qnaVos);
		
		return "dbShop/dbProductContent";
	}

	// 제품 상세보기 화면에서 구매하기, 장바구니담기를 선택하면 /dbProductContent로 이동한다.
	@RequestMapping(value = "/dbProductContent", method = RequestMethod.POST)
	public String dbProductContentPost(
	    DbCartListVO vo,
	    HttpSession session,
	    String flag,
	    Model model,
	    @RequestParam(name = "categoryMainCode", defaultValue = "", required = false) String categoryMainCode,
	    @RequestParam(name = "categorySubCode", defaultValue = "", required = false) String categorySubCode,
	    @RequestParam(name = "pag", defaultValue = "1", required = false) int pag,
	    @RequestParam(name = "pageSize", defaultValue = "16", required = false) int pageSize
	) {
		
	    String mid = (String) session.getAttribute("sMid");
	    // select * from dbCartList2 where productName=#{productName} and optionName=#{optionName} and mid = #{mid};
	    DbCartListVO resVo = dbShopService.getDbCartListProductOptionSearch(vo.getProductName(), vo.getOptionName(), mid);
	    
	    // 장바구니에 존재하는 제품을 구매하기/장바구니담기를 한다면 Update
	    if (resVo != null) {
	        // vo.getOptionNum은 옵션의 수량 리스트(옵션, 수량 각각 여러 개일 수 있다. ex) 블랙 3개, 골드 4개)
	        // vo.getOptionNum은 옵션의 수량만 가지고 있다( ex) 3, 4)
	        // 따라서 옵션의 수량이 여러 개면 ','로 분리되어 들어있기에 ','로 분리시킨다.
	        String[] voOptionNums = vo.getOptionNum().split(",");
	        // 기존 DB에 저장되어 있던 장바구니
	        // 같은 제품명, 옵션명, mid 이름을 통해 가져온 옵션의 수량 리스트
	        String[] resOptionNums = resVo.getOptionNum().split(",");
	        
	        // 기존 옵션 수량(DB)과 현재 옵션 수량(vo)을 합치기 위한 배열
	        int[] nums = new int[99];
	        
	        // 더해진 수량을 다시 문자열로 표현하기 위해 생성
	        String strNums = "";
	        
	        for (int i = 0; i < voOptionNums.length; i++) {
	            nums[i] += (Integer.parseInt(voOptionNums[i]) + Integer.parseInt(resOptionNums[i]));
	            strNums += nums[i];
	            if (i < nums.length - 1)
	                strNums += ",";
	        }
	        vo.setOptionNum(strNums);
	        dbShopService.dbShopCartUpdate(vo);
	    // 장바구니에 존재하지 않는 제품을 구매하기/장바구니담기를 한다면 Insert
	    } else {
	        dbShopService.dbShopCartInput(vo);
	    }
	    
	    // 장바구니에 담긴 상품의 개수를 session에 누적시켜준다.
	    int count = (int) session.getAttribute("sCount") + 1;
	    session.setAttribute("sCount", count);
	    
	    // flag의 값이 "order"가 아닐 경우 dbProductList.jsp로 이동할 때 위치 및 페이징 처리를 위해 추가
	    model.addAttribute("flag", "?categorySubCode="+categorySubCode+"&categoryMainCode="+categoryMainCode+"&pag="+pag+"&pageSize="+pageSize);
	    
	    // dbProductContent.jsp에서 넘긴 flag의 값이 "order"(구매하기)라면 장바구니 화면("dbShop/dbCartList"+flag)으로 이동
	    if (flag.equals("order")) {
	        return "redirect:/msg/cartOrderOk";
	    // dbProductContent.jsp에서 넘긴 flag의 값이 "order"(구매하기)가 아니라면 제품 리스트 화면("dbShop/dbProductList"+flag)으로 이동
	    } else {
	        return "redirect:/msg/cartInputOk";
	    }
	}

	// 장바구니에 담겨있는 모든 품목들 보여주기(장바구니는 DB에 들어있는 자료를 바로 불러와서 처리하면된다.)
	@RequestMapping(value = "/dbCartList", method = RequestMethod.GET)
	public String dbCartListGet(HttpSession session, DbCartListVO vo, Model model) {
		String mid = (String) session.getAttribute("sMid");
		// select * from dbCartList2 where mid = #{mid} order by idx;
		List<DbCartListVO> vos = dbShopService.getDbCartList(mid);

		model.addAttribute("cartListVos", vos);
		return "dbShop/dbCartList";
	}

	// 장바구니에서 선택한 제품들을 주문하기
	@RequestMapping(value = "/dbCartList", method = RequestMethod.POST)
	public String dbCartListPost(HttpServletRequest request, HttpSession session, Model model) {
	    // 세션에서 사용자 아이디 가져오기
	    String mid = session.getAttribute("sMid").toString();
	    // 배송비 정보 가져오기
	    int baesong = Integer.parseInt(request.getParameter("baesong"));
	    // 주문 고유번호 생성
	    String orderIdx = createOrderIdx();
	    // 장바구니에서 체크한 제품의 idx 값들 가져오기
	    String[] idxChecked = request.getParameterValues("idxChecked");

	    // 주문 정보를 저장할 리스트
	    List<DbOrderVO> orderVos = new ArrayList<>();

	    for (String strIdx : idxChecked) {
	        // 장바구니에서 idx에 해당하는 정보 가져오기
	        DbCartListVO cartVo = dbShopService.getCartIdx(Integer.parseInt(strIdx));
	        
	        // 주문 정보 생성
	        DbOrderVO orderVo = createOrderVo(cartVo, orderIdx, mid, baesong);
	        
	        orderVos.add(orderVo);
	    }

	    // 세션에 주문 정보 저장
	    session.setAttribute("sOrderVos", orderVos);

	    // 사용자 정보 가져오기
	    MemberVO memberVo = memberService.getMemIdCheck(mid);
	    model.addAttribute("memberVo", memberVo);

	    return "dbShop/dbOrder";
	}

	private String createOrderIdx() {
	    // 주문 고유번호 생성
	    DbOrderVO maxIdx = dbShopService.getOrderMaxIdx();
	    int idx = (maxIdx != null) ? maxIdx.getMaxIdx() + 1 : 1;
	    
	    Date today = new Date();
	    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
	    
	    // 주문 고유번호 = "(오늘 날짜 "yyyyMMdd" 형식) + (기존 DB의 idx 최대값 +1)"
	    return sdf.format(today) + idx;
	}

	private DbOrderVO createOrderVo(
	        DbCartListVO cartVo, 
	        String orderIdx, 
	        String mid, 
	        int baesong
	) {
	    DbOrderVO orderVo = new DbOrderVO();

	    // 장바구니 정보를 주문 정보로 복사
	    orderVo.setProductIdx(cartVo.getProductIdx());
	    orderVo.setProductName(cartVo.getProductName());
	    orderVo.setMainPrice(cartVo.getMainPrice());
	    orderVo.setThumbImg(cartVo.getThumbImg());
	    orderVo.setOptionName(cartVo.getOptionName());
	    orderVo.setOptionPrice(cartVo.getOptionPrice());
	    orderVo.setOptionNum(cartVo.getOptionNum());
	    orderVo.setTotalPrice(cartVo.getTotalPrice());
	    orderVo.setCartIdx(cartVo.getIdx());
	    
	    // 주문에 필요한 추가 정보 설정
	    orderVo.setBaesong(baesong);
	    orderVo.setOrderIdx(orderIdx);
	    orderVo.setMid(mid);

	    return orderVo;
	}
	
	@ResponseBody
	@RequestMapping(value = "/dbCartDelete", method = RequestMethod.POST)
	public String dbCartDeleteGet(int idx, HttpSession session) {
	    dbShopService.dbCartDelete(idx);
	    
	    // 세션에서 sCount 변수 가져오기
	    Integer sCount = (Integer) session.getAttribute("sCount");
	    
	    // sCount가 null이 아니면 1 감소시키기
	    if (sCount != null && sCount > 0) {
	        session.setAttribute("sCount", sCount - 1);
	    }
	    return "";
	}

	// 결제시스템(결제창 호출하기) - API이용
	@RequestMapping(value = "/payment", method = RequestMethod.POST)
	public String paymentPost(DbOrderVO orderVo, PayMentVO payMentVo, DbBaesongVO baesongVo, HttpSession session, Model model) {
		model.addAttribute("payMentVo", payMentVo);
		
		session.setAttribute("sPayMentVo", payMentVo);
		
		session.setAttribute("sBaesongVo", baesongVo);

		return "dbShop/paymentOk";
	}
	
	// 결제 - API
	@SuppressWarnings("unchecked")
	@RequestMapping(value="/paymentResult", method=RequestMethod.GET)
	public String paymentResultGet(HttpSession session, PayMentVO receivePayMentVo, Model model) {
		// orderVos에는 장바구니에서 사용자가 체크한 제품들이 저장
        List<DbOrderVO> orderVos = (List<DbOrderVO>) session.getAttribute("sOrderVos");
        PayMentVO payMentVo = (PayMentVO) session.getAttribute("sPayMentVo");
        DbBaesongVO baesongVo = (DbBaesongVO) session.getAttribute("sBaesongVo");

        // API 사용 시 결제 금액을 10원으로 고정하여 처리, 이 값을 다시 계산한 값으로 교체
        payMentVo.setAmount(baesongVo.getOrderTotalPrice());
        // 아임포트에서 반환해 준 값을 payMentVo에 저장
        payMentVo.setImp_uid(receivePayMentVo.getImp_uid());
        payMentVo.setMerchant_uid(receivePayMentVo.getMerchant_uid());
        payMentVo.setPaid_amount(receivePayMentVo.getPaid_amount());
        payMentVo.setApply_num(receivePayMentVo.getApply_num());

        // 서비스를 통해 비즈니스 로직 및 트랜잭션 처리
        dbShopService.processPaymentAndOrder(orderVos, receivePayMentVo, baesongVo);

        // 장바구니 session 처리
        String mid = (String) session.getAttribute("sMid");
        session.setAttribute("sCount", dbShopService.getCartCount(mid));

        // 주문 정보들을 다시 session에 저장
        session.setAttribute("sPayMentVo", payMentVo);

        return "redirect:/msg/paymentResultOk";
    }
	
	@RequestMapping(value = "/paymentResultOk",method = RequestMethod.GET)
	public String paymentResultOkGet() {
		return "dbShop/paymentResult";
	}
	
	// 배송지 정보 보여주기
	@RequestMapping(value = "/dbOrderBaesong", method = RequestMethod.GET)
	public String dbOrderBaesongGet(String orderIdx, Model model) {
		// 같은 주문번호가 2개 이상 있을수 있기에 List객체로 받아온다.
		List<DbBaesongVO> vos = dbShopService.getOrderBaesong(orderIdx);
		// 같은 배송지면 0번째것 하나만 vo에 담아서 넘겨주면 된다.
		model.addAttribute("vo", vos.get(0));

		return "dbShop/dbOrderBaesong";
	}

	// 주문조회 폼
	@RequestMapping(value = "/dbMyOrder", method = RequestMethod.GET)
	public String dbMyOrderGet(HttpServletRequest request, HttpSession session, Model model,
			@RequestParam(name = "pag", defaultValue = "1", required = false) int pag,
			@RequestParam(name = "pageSize", defaultValue = "5", required = false) int pageSize) { 
		String mid = (String) session.getAttribute("sMid");

		// 페이징 처리(블록페이지) 변수 지정 시작
		PageVO pageVo = pageProcess.totRecCnt(pag, pageSize, "dbMyOrder", "", mid);
		// 오늘 구매한 내역을 초기화면에 보여준다.(사용자의 주문 정보와 관련된 배송 정보를 함께 가져오는 것, 배송 테이블(dbBaesong2)과 주문 테이블(dbOrder2)을 조인)
		List<DbOrderVO> vos = dbShopService.getMyOrderList(pageVo.getStartIndexNo(), pageSize, mid);

		model.addAttribute("vos", vos);
		model.addAttribute("pageVo", pageVo);

		return "dbShop/dbMyOrder";
	}

	// 주문 상태별 조회(전체/결제완료/배송중/배송완료/구매완료/반품처리) 조회하기
	@RequestMapping(value="/orderStatus", method=RequestMethod.GET)
	public String orderStatusGet(HttpSession session,
		     @RequestParam(name="pag", defaultValue="1", required=false) int pag,
			 @RequestParam(name="pageSize", defaultValue="5", required=false) int pageSize,
			 @RequestParam(name="orderStatus", defaultValue="전체", required=false) String orderStatus,
			 Model model) {
		String mid = (String) session.getAttribute("sMid");

   		PageVO pageVo = pageProcess.totRecCnt(pag, pageSize, "dbShopMyOrderStatus", mid, orderStatus);
   
   		List<DbBaesongVO> vos = dbShopService.getOrderStatus(mid, orderStatus, pageVo.getStartIndexNo(), pageSize);
   
   		model.addAttribute("orderStatus", orderStatus);
   		model.addAttribute("vos", vos);
   		model.addAttribute("pageVo", pageVo);

   		return "dbShop/dbMyOrder";
   }
	 
	// 주문 조건 조회하기(날짜별(오늘/일주일/1개월/3개월/전체)
	@RequestMapping(value = "/orderCondition", method = RequestMethod.GET)
	public String orderConditionGet(HttpSession session, int conditionDate, Model model,
			@RequestParam(name = "pag", defaultValue = "1", required = false) int pag,
			@RequestParam(name = "pageSize", defaultValue = "5", required = false) int pageSize) {
		String mid = (String) session.getAttribute("sMid");
		String strConditionDate = conditionDate + "";
		PageVO pageVo = pageProcess.totRecCnt(pag, pageSize, "dbShopMyOrderCondition", mid, strConditionDate);

		// 특정 사용자(mid 값과 일치)의 주문 정보와 해당 배송 정보를 최근 주문일을 기준으로 내림차순으로 정렬하여 가져오는 것
		List<DbOrderVO> vos = dbShopService.getMyOrderList(pageVo.getStartIndexNo(), pageSize, mid);
		model.addAttribute("vos", vos);
		model.addAttribute("pageVo", pageVo);
		model.addAttribute("conditionDate", conditionDate);

		Calendar startDateJumun = Calendar.getInstance();
		Calendar endDateJumun = Calendar.getInstance();
		startDateJumun.setTime(new Date()); // 오늘날짜로 셋팅
		endDateJumun.setTime(new Date()); // 오늘날짜로 셋팅
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String startJumun = "";
		String endJumun = "";
		
		switch (conditionDate) {
			case 1:
				startJumun = sdf.format(startDateJumun.getTime());
				endJumun = sdf.format(endDateJumun.getTime());
				break;
			case 7:
				startDateJumun.add(Calendar.DATE, -7);
				break;
			case 30:
				startDateJumun.add(Calendar.MONTH, -1);
				break;
			case 90:
				startDateJumun.add(Calendar.MONTH, -3);
				break;
			case 99999:
				startDateJumun.set(2022, 00, 01);
				break;
			default:
				startJumun = null;
				endJumun = null;
		}
		if (conditionDate != 1 && endJumun != null) {
			startJumun = sdf.format(startDateJumun.getTime());
			endJumun = sdf.format(endDateJumun.getTime());
		}

		model.addAttribute("startJumun", startJumun);
		model.addAttribute("endJumun", endJumun);

		return "dbShop/dbMyOrder";
	}

	// 날짜별(startJumun, endJumun), 상태별(conditionOrderStatus) 주문내역 확인
	@RequestMapping(value = "/myOrderStatus", method = RequestMethod.GET)
	public String myOrderStatusGet(HttpServletRequest request, HttpSession session, String startJumun, String endJumun,
			@RequestParam(name = "pag", defaultValue = "1", required = false) int pag,
			@RequestParam(name = "pageSize", defaultValue = "10", required = false) int pageSize,
			@RequestParam(name = "conditionOrderStatus", defaultValue = "전체", required = false) String conditionOrderStatus,
			Model model) {
		String mid = (String) session.getAttribute("sMid");
		int level = (int) session.getAttribute("sLevel");

		if (level == 0) mid = "전체";
		String searchString = startJumun + "@" + endJumun + "@" + conditionOrderStatus;
		PageVO pageVo = pageProcess.totRecCnt(pag, pageSize, "myOrderStatus", mid, searchString);
		
		// startIndexNo(현재 페이지의 첫 번째 항목의 인덱스)
		// 주문 정보를 검색, 주문 상태(conditionOrderStatus), 주문 일자 범위(startJumun부터 endJumun까지), 및 회원 ID(mid)를 기준으로 검색 결과를 반환하고 페이징 처리
		List<DbBaesongVO> vos = dbShopService.getMyOrderStatus(pageVo.getStartIndexNo(), pageSize, mid, startJumun, endJumun, conditionOrderStatus);
		model.addAttribute("vos", vos);
		model.addAttribute("startJumun", startJumun);
		model.addAttribute("endJumun", endJumun);
		model.addAttribute("conditionOrderStatus", conditionOrderStatus);
		model.addAttribute("pageVo", pageVo);

		return "dbShop/dbMyOrder";
	}

	// 관리자에서 주문 확인하기
	@RequestMapping(value = "/adminOrderStatus")
	public String dbOrderProcessGet(Model model,
			@RequestParam(name = "startJumun", defaultValue = "", required = false) String startJumun,
			@RequestParam(name = "endJumun", defaultValue = "", required = false) String endJumun,
			@RequestParam(name = "orderStatus", defaultValue = "전체", required = false) String orderStatus,
			@RequestParam(name = "pag", defaultValue = "1", required = false) int pag,
			@RequestParam(name = "pageSize", defaultValue = "10", required = false) int pageSize) {

		List<DbBaesongVO> vos = null;
		PageVO pageVo = null;
		String strNow = "";
		if (startJumun.equals("")) {
			Date now = new Date();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			strNow = sdf.format(now);

			startJumun = strNow;
			endJumun = strNow;
		}

		String strOrderStatus = startJumun + "@" + endJumun + "@" + orderStatus;
		pageVo = pageProcess.totRecCnt(pag, pageSize, "adminDbOrderProcess", "", strOrderStatus);

		vos = dbShopService.getAdminOrderStatus(pageVo.getStartIndexNo(), pageSize, startJumun, endJumun, orderStatus);

		model.addAttribute("startJumun", startJumun);
		model.addAttribute("endJumun", endJumun);
		model.addAttribute("orderStatus", orderStatus);
		model.addAttribute("vos", vos);
		model.addAttribute("pageVo", pageVo);

		return "admin/dbShop/dbOrderProcess";
	}

	// header1.jsp 검색
	@RequestMapping(value="/productSearch",method=RequestMethod.GET) 
	public String productSearch(
			@RequestParam(name="pag", defaultValue="1", required = false) int pag,
			@RequestParam(name="pageSize", defaultValue="16", required = false) int pageSize,
			String searchString, Model model) {
		// select count(*) from dbProduct2 where detail like concat('%',#{searchString},'%');
		// 위의 SQL 쿼리로 totRecCnt 값 및 페이징 처리에 필요한 값을 가지고 pageVO에 값 저장
		PageVO pageVO = pageProcess.totRecCnt(pag, pageSize, "dbShop", "",searchString);
		// select * from dbProduct2 where detail like concat('%',#{searchString},'%') order by idx desc limit #{startIndexNo},#{pageSize};
		// dbProduct2 테이블의 detail 컬럼을 searchString 으로 검색한 뒤 페이지별로 값을 가져와서 vos에 저장
		List<DbProductVO> vos = dbShopService.getProductSearch(pageVO.getStartIndexNo(), pageSize,searchString);
		
		model.addAttribute("pageVO",pageVO);
		model.addAttribute("vos", vos);
		model.addAttribute("searchString",searchString);
		
		return"dbShop/productSearch"; 
	}
	
	// 관리자가 주문상태를 변경처리하는것
	@ResponseBody
	@RequestMapping(value="/goodsStatus", method=RequestMethod.POST)
	public String goodsStatusGet(String orderIdx, String orderStatus) {
		dbShopService.setOrderStatusUpdate(orderIdx, orderStatus);
		return "";
	}  
	
	// 고객문의 댓글 등록
	@ResponseBody
	@RequestMapping(value="/dbShopQnAInput", method=RequestMethod.POST)
	public String dbShopQnAPost(DbProductQnAVO dbProductQnAVO) {
		int levelOrder = 0;
		
		// productqna2 테이블에서 특정 "getProductIdx"에 해당하는 레코드 중 "levelOrder" 컬럼의 최대값을 검색
		// 특정 QnA글 (getProductIdx)에 속한 댓글들 중에서 가장 높은 순서(levelOrder)를 가져온다.
		// 같은 계층의 댓글들 사이의 순서 중 가장 큰 수를 가져온다.
		String strLevelOrder = dbShopService.maxLevelOrder(dbProductQnAVO.getProductIdx());
		
		// 최대값이 있다면 해당 값에 1을 더하여 새로운 댓글의 levelOrder를 결정
		if(strLevelOrder != null) levelOrder = Integer.parseInt(strLevelOrder)+1;
		
		// levelOrder 값을 구해서 dbProductQnAVO에 설정 
		dbProductQnAVO.setLevelOrder(levelOrder);
		
		// 댓글 정보를 데이터베이스에 등록
		dbShopService.setQnAInput(dbProductQnAVO);
		return "";
	}
	
	// 고객문의 댓글 삭제
	@ResponseBody
	@RequestMapping(value="/dbShopQnADelete", method=RequestMethod.POST)
	public String dbShopQnADeletePost(int idx) {
		dbShopService.setdbShopQnADelete(idx);
		return "";
	}
	
	// 고객문의 댓글의 답글 저장
    @ResponseBody
    @RequestMapping(value = "/qnaInput2", method = RequestMethod.POST)
    public String qnaInput2Post(DbProductQnAVO dbProductQnAVO) {
        // 트랜잭션 메서드 호출
        dbShopService.qnaInput2Transaction(dbProductQnAVO);
        return "";
    }

	// 후기작성으로 이동
	@RequestMapping(value="/dbReview", method=RequestMethod.GET)
	public String dbReviewGet(DbProductVO vo, int idx, Model model,
			@RequestParam(name="pag", defaultValue="1", required = false) int pag,
			@RequestParam(name="pageSize", defaultValue="10", required = false) int pageSize) {

		vo = dbShopService.getDbShopContent(idx);
		
		model.addAttribute("vo",vo);
		model.addAttribute("pag",pag);
		model.addAttribute("pageSize",pageSize);
		return "dbShop/dbReview";
	}
	
	// 후기등록
	@ResponseBody
	@RequestMapping(value="/dbShopReviewInput",method=RequestMethod.POST)
	public String dbShopReviewInputPost(DbProductReviewVO vo) {
		dbShopService.setReviewInput(vo);
		return "";
	}
	
	// 후기삭제
	@ResponseBody
	@RequestMapping(value="/dbShopReviewDelete", method=RequestMethod.POST)
	public String dbShopReviewDeletePost(int idx) {
		dbShopService.setdbShopReviewDelete(idx);
		return "";
	}
}
