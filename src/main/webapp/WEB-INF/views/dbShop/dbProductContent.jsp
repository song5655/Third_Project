<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<% pageContext.setAttribute("newLine", "\n"); %>
<c:set var="ctp" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html>
<head>
	<meta charset="UTF-8">
	<title>dbProductContent.jsp(상품정보 상세보기)</title>
	<jsp:include page="/WEB-INF/views/include/bs4.jsp"/>
	<script>
	  'use strict';
	
	  let idxArray = new Array(); /* 배열의 개수 지정없이 동적배열로 잡았다. */
	
	  // selectOption form에서 옵션을 선택하는 드롭다운  메뉴가 변경될 때 작동하는 JS
	  $(function () {
	    $("#selectOption").change(function () {
	      // <option value="0:기본품목_${productVo.mainPrice}">기본품목</option>
	      // <option value="${vo.idx}:${vo.optionName}_${vo.optionPrice}">${vo.optionName}</option>
	      let selectOption = $(this).val();
	      // 현재 옵션의 고유번호(기본품목은 0으로 처리) 추출
	      let idx = selectOption.substring(0, selectOption.indexOf(":"));
	      // 옵션명 추출
	      let optionName = selectOption.substring(selectOption.indexOf(":") + 1, selectOption.indexOf("_"));
	      // 옵션가격 추출
	      let optionPrice = selectOption.substring(selectOption.indexOf("_") + 1);
	      // 옵션가격(numberWithCommas => 천 단위마다 콤마를 붙여주는 JS)
	      let commaPrice = numberWithCommas(optionPrice);
	
	      // div 의 id 가 "layer' + idx + '" 인 div의 length 가 0인지 확인(선택한 옵션이 이미 추가되었는지 확인, 0이면 추가가 되지 않은 옵션)
	      if ($("#layer" + idx).length == 0 && selectOption != "") {
	    	// 옵션의 고유번호에 따른 해당위치의 배열에 고유번호(idx)를 저장. 즉, idx가 5번이면 idxArray[5]번방에 5를 기억시키고 있다.
	        idxArray[idx] = idx; 
	
	        /*
          		옵션을 추가할때마다 품목(옵션)명(optionName)과 수량(numBox), 옵션가격(commaPrice)이 보이는 div가 하나씩 생긴다.
          		여러 옵션이 추가될 때마다 각 옵션에 대한 HTML 문자열이 str 변수에 누적 (+=) 
	        	=> 최종적으로 $("#product1").append(str)를 통해 해당 부분(product1)에 동적으로 생성된 HTML을 추가
	        	
	        */
	        
	        let str = '';
	        str += '<div class="layer row" id="layer' + idx + '" style="border-top:1px solid black; border-bottom:1px solid black"><div class="col">' + optionName + '</div>';
	        str += '<input type="number" class="text-center numBox" id="numBox' + idx + '" name="optionNum" onchange="numChange(' + idx + ')" value="1" min="1"/> &nbsp;';
	        str += '<input type="text" id="imsiPrice' + idx + '" class="price" value="' + commaPrice + '" readonly />';
	        /* 변동되는 가격을 재 계산하기위해 price+idx 아이디를 사용하고 있다. numChange에서 값을 변경하면서 optionPrice의 값도 옵션가격 * 수량 값으로 변한다.*/
	        str += '<input type="hidden" id="price' + idx + '" value="' + optionPrice + '"/> &nbsp;';
	        str += '<input type="button" class="btn btn-danger btn-sm" onclick="remove(' + idx + ')" value="삭제"/>';
	        /* 현재 상태에서의 변경된 상품(옵션)의 가격이다. */
	        str += '<input type="hidden" name="statePrice" id="statePrice' + idx + '" value="' + optionPrice + '"/>';
	        str += '<input type="hidden" name="optionIdx" value="' + idx + '"/>';
	        str += '<input type="hidden" name="optionName" value="' + optionName + '"/>';
	        str += '<input type="hidden" name="optionPrice" value="' + optionPrice + '"/>';
	        str += '</div>';
	        str += '<br/>';
	        $("#product1").append(str);
	        onTotal();
	      } else {
	        alert("이미 선택한 옵션입니다.");
	      }
	    });
	  });
	
	  // 상품의 총 금액 (재)계산하기
	  function onTotal() {
	    let total = 0;
	    for (let i = 0; i < idxArray.length; i++) {
	      if ($("#layer" + idxArray[i]).length != 0) {
	        total += parseInt(document.getElementById("price" + idxArray[i]).value);
	        /* 재 계산된 총 금액을 myForm폼의 totalPriceResult에 담아서 값을 넘겨주려고 한다. */
	        document.getElementById("totalPriceResult").value = total;
	      }
	    }
	    /* 출력 목적으로 만들어 놓은 totalPrice의 text박스에 콤마를 표시하여 보여주고 있다.(코드 411번 라인)*/
	    document.getElementById("totalPrice").value = numberWithCommas(total);
	  }
	
	  // 옵션 수량 변경 시 처리
	  function numChange(idx) {
		// ("statePrice" + idx).value => optionPrice, document.getElementById("numBox" + idx).value => 수량 (옵션가격 * 수량)
	    let price = document.getElementById("statePrice" + idx).value * document.getElementById("numBox" + idx).value;
	 	// ("imsiPrice" + idx) => commaPrice 에 계산한 price에 천 단위 콤마 처리(보여주는 용도, 수량의 증감 시 가격의 변화)
	    document.getElementById("imsiPrice" + idx).value = numberWithCommas(price);
	 	// "price" + idx => optionPrice는 개별품목(옵션 등)의 상품가격으로 let price로 수정한 price에 따라 변동처리된 상품가격을 hidden으로 담아서 넘기려한다.
	    document.getElementById("price" + idx).value = price;
	 	// 수량이 변동될때마다 다시 총 상품금액을 계산시켜주고 있다.
	    onTotal();
	  }
	  
	  // 등록(추가)시킨 옵션 상품 삭제하기
	  function remove(idx) {
	    $("div").remove("#layer" + idx);
	    onTotal();
	  }
	  
	  // 천 단위마다 콤마를 표시해 주는 함수
	  function numberWithCommas(x) {
	    return x.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ",");
	  }
	  
	  // 주문하기
	  function order() {
		// "totalPrice"라는 id를 가진 HTML 요소의 값을 가져온다.
	    let totalPrice = document.getElementById("totalPrice").value;
	    if (totalPrice == "" || totalPrice == 0) {
	      alert("옵션을 선택해주세요");
	      return false;
	    } else if ('${sMid}' == "") {
	      alert("로그인 후 이용 가능합니다.");
	      location.href = "${ctp}/member/memLogin";
	    } else {
	      document.getElementById("flag").value = "order";
	      document.myForm.submit();
	    }
	  }
	
	  // 장바구니 호출 시 수행함수
	  function cart() {
	    if (document.getElementById("totalPrice").value == 0) {
	      alert("옵션을 선택해주세요");
	      return false;
	    } else if ('${sMid}' == "") {
	      alert("로그인 후 이용 가능합니다.");
	      location.href = "${ctp}/member/memLogin";
	    } else {
	      document.myForm.submit();
	    }
	  }
		
	  // 후기 삭제
	  function reviewDelete(idx) {
	    let ans = confirm("상품후기를 삭제하시겠습니까?");
	    if (!ans) return false;
	
	    $.ajax({
	      type: "post",
	      url: "${ctp}/dbShop/dbShopReviewDelete",
	      data: {
	        idx: idx
	      },
	      success: function (data) {
	        alert("삭제되었습니다.");
	        location.reload();
	      },
	      error: function () {
	        alert("전송오류");
	      },
	    });
	  }
	
	  // 고객문의 댓글 등록
	  function qnaCheck() {
	    let qnaContent = $("#qnaContent").val();
	    if (qnaContent.trim() == "") {
	      alert("문의글을 입력하세요.");
	      $("#qnaContent").focus();
	      return false;
	    }
	    let query = {
	      productIdx: ${productVo.idx},
	      mid: '${sMid}',
	      content: qnaContent,
	    };
	    $.ajax({
	      type: "post",
	      url: "${ctp}/dbShop/dbShopQnAInput",
	      data: query,
	      success: function (data) {
	        alert("문의글이 등록되었습니다.");
	        location.reload();
	      },
	      error: function () {
	        alert("전송오류");
	      },
	    });
	  }
	
	  // 고객문의 댓글 삭제
	  function QnADelete(idx) {
	    let ans = confirm("문의글을 삭제하시겠습니까?");
	    if (!ans) return false;
	
	    $.ajax({
	      type: "post",
	      url: "${ctp}/dbShop/dbShopQnADelete",
	      data: {
	        idx: idx
	      },
	      success: function (data) {
	        alert("삭제되었습니다.");
	        location.reload();
	      },
	      error: function () {
	        alert("전송오류");
	      },
	    });
	  }
	
	  // 고객문의 댓글의 답글 등록
	  function insertQnA(idx, level, levelOrder, mid) {
	    let insertQnA = '';
	    insertQnA += '<div class="container" style="width:920px;">';
	    insertQnA += '<div class="form-group">';
	    insertQnA += '<textarea rows="3" class="form-control p-0" name="qnaContent" id="qnaContent' + idx + '">';
	    insertQnA += '</textarea>';
	    insertQnA += '<div class="p-0 text-right mt-3">';
	    insertQnA += '<input type="button" value="답글달기" onclick="qnaCheck2(' + idx + ',' + level + ',' + levelOrder + ')"/>';
	    insertQnA += '</div>';
	    insertQnA += '</div>';
	    insertQnA += '<input type="hidden" name="mid" value="${sMid}"/>';
	    insertQnA += '</div>';
	
	    $("#QnABoxOpenBtn" + idx).hide();
	    $("#QnABoxCloseBtn" + idx).show();
	    $("#QnABox" + idx).slideDown(500);
	    $("#QnABox" + idx).html(insertQnA);
	  }
	
	  // 고객문의 댓글의 답글 입력 폼 닫기 처리
	  function closeQnA(idx) {
	    $("#QnABoxOpenBtn" + idx).show();
	    $("#QnABoxCloseBtn" + idx).hide();
	    $("#QnABox" + idx).slideUp(500);
	  }
	
	  // 고객문의 댓글의 답글 저장
	  function qnaCheck2(idx, level, levelOrder) {
	    let productIdx = "${productVo.idx}";
	    let mid = "${sMid}";
	    let content = "qnaContent" + idx;
	    let contentVal = $("#" + content).val();
	
	    if (contentVal == "") {
	      alert("글 내용을 입력하지 않았습니다.");
	      $("#" + qnaContent).focus;
	      return false;
	    }
	
	    let query = {
	      productIdx: productIdx,
	      mid: mid,
	      content: contentVal,
	      level: level,
	      levelOrder: levelOrder
	    };
	    $.ajax({
	      type: "post",
	      url: "${ctp}/dbShop/qnaInput2",
	      data: query,
	      success: function () {
	        location.reload();
	      },
	      error: function () {
	        alert("전송오류");
	      },
	    });
	  }
	</script>

	<style>
	  .layer  {
	    border: 0px;
	    width: 100%;
	    padding: 10px;
	    margin-left: 1px;
	    /* background-color: #eee; */
	  }
	  .numBox { width: 40px }
	  .price  {
	    width: 160px;
	    /* background-color: #eee; */
	    text-align: right;
	    font-size: 1em;
	    border: 1px;
	    /* outline: none; */
	  }
	  .totalPrice {
	    text-align: right;
	    margin-right: 10px;
	    color: #f63;
	    font-size: 1.5em;
	    font-weight: bold;
	    border: 0px;
	    outline: none;
	  }
	  .button {
	    border: none;
	    color: white;
	    text-align: center;
	    text-decoration: none;
	    display: inline-block;
	    font-size: 20px;
	    padding: 0px;
	    margin: 0px;
	    cursor: pointer;
	    height: 50px;
	  }
	  .button1 { background-color: #000000; width: 464px; }
	  .button2 { background-color: #ffffff; color: #222222; border: 1px solid #d7d5d5; width: 232px; }
	  /*tab*/
	  * { box-sizing: border-box }
	  /* Set height of body and the document to 100% */
	  body, html {
	    height: 100%;
	    margin: 0;
	    font-family: Arial;
	  }
	  /* Style tab links */
	  .tablink {
	    background-color: #ffffff;
	    /* color: white; */
	    float: left;
	    border: none;
	    outline: none;
	    cursor: pointer;
	    padding: 14px 16px;
	    font-size: 14px;
	    width: 25%;
	    font-weight: bold;
	  }
	  .tablink:hover { background-color: #e5e5e5; }
	  /* Style the tab content (and add height:100% for full page content) */
	  .tabcontent {
	    /* color: white; */
	    display: none;
	    padding: 100px 20px;
	    height: 100%;
	  }
	  .btn-group button {
	    background-color: #ffffff;
	    border: 1px solid #e5e5e5; 	/* Green border */
	    padding: 10px 24px; 		/* Some padding */
	    cursor: pointer; 			/* Pointer/hand icon */
	    float: left; 				/* Float the buttons side by side */
	  }
	  /* Clear floats (clearfix hack) */
	  .btn-group:after {
	    content: "";
	    clear: both;
	    display: table;
	  }
	  .btn-group button:not(:last-child) { border-right: none; /* Prevent double borders */}
	</style>

</head>
<body>
<jsp:include page="/WEB-INF/views/include/header1.jsp"/>
<jsp:include page="/WEB-INF/views/include/nav.jsp"/>
<div class="container" style="width:60%">
<br/><br/>
<div class="row">
  <!-- 상품메인 이미지 -->
  <div class="col-sm-6">
    <img src="${ctp}/dbShop/product/${productVo.FSName}" style="width:500px; height:500px;"/><br/>
    <%-- <div style="width:100%; text-align:right;" class="pr-5"><img src="${ctp}/qrCode/${qrCode}" width="100px"/></div> --%>
  </div>
  <div class="col-sm-6" style="padding-left:40px">
    <!-- 상품 기본정보 -->
    <div style="width:98%" >
      <hr style="height:1px;border:none;color:#333;background-color:#333; padding:0px; margin:0px"/>
      <div class="text-left" style="font-size:1.3em; font-weight: 600; padding-top:20px">${productVo.detail}</div>
      <hr/>
      <div>
        <div class="row">
          <div class="col-sm-3 text-left" style="font-size:1.2em; font-weight: bold;">판매가</div>
          <div class="col-sm-9 text-left" style="font-size:1.2em; font-weight: bold;">
            <font color="orange"><fmt:formatNumber value="${productVo.mainPrice}"/>원</font>
          </div>
        </div>
        <div class="text-left" style="font-size:1.2em; font-weight: bold;">${productVo.productName}</div>
      </div>
    </div>
    <!-- 상품주문을 위한 옵션정보 출력 -->
    <hr/>
    <div class="form-group">
      <form name="optionForm">  <!-- 옵션의 정보를 보여주기위한 form -->
        <select size="1" class="form-control" id="selectOption">
          <option value="" disabled selected>상품옵션선택</option>
          <option value="0:기본품목_${productVo.mainPrice}">기본품목</option>
          <c:forEach var="vo" items="${optionVos}">
            <option value="${vo.idx}:${vo.optionName}_${vo.optionPrice}">${vo.optionName}</option>
          </c:forEach>
        </select>
        <br/>
        <div class="text-left" style="font-size:0.8em;">(최소주문수량 1개 이상)</div>
      </form>
    </div>
    <br/>
    <div>
	  <!-- 실제 상품의 정보를 넘겨주기 위한 form -->
      <form name="myForm" method="post">
        <input type="hidden" name="mid" value="${sMid}"/>
        <input type="hidden" name="productIdx" value="${productVo.idx}"/>
        <input type="hidden" name="productName" value="${productVo.productName}"/>
        <input type="hidden" name="mainPrice" value="${productVo.mainPrice}"/>
        <input type="hidden" name="thumbImg" value="${productVo.FSName}"/>
        <input type="hidden" name="totalPrice" id="totalPriceResult"/>	<!-- 계산된 총 금액(totalPrice)을 넘겨주기 위해 사용 -->
        <input type="hidden" name="flag" id="flag"/>					<!-- 장바구니담기를 하지 않고 구매 시 flag='order'을 넘기는 변수 -->
        <!-- 위쪽에서 메인상품의 기본정보, product1에서는 선택한 옵션의 정보가 보이도록 설정. -->
        <!-- form이 제출되면 "product1" div 내부에 포함된 값들도 함께 HTTP 요청에 포함되어 전송 -->
        <div id="product1"></div>
      </form>
    </div>
    <!-- 상품의 총가격(옵션포함가격) 출력처리 -->
    <div class="row">
      <hr/>
      <div class="col-sm-3 text-left" style="font-size:1em;font-weight: bold">총상품금액</div>
      <div class="col-sm-9 text-right" style="font-weight: bold">
        <!-- 아래의 id, class totalPrice는 출력용으로 보여주기 위해서만 사용한 것으로 값의 전송과는 관계가 없다. -->
        <input type="text" class="totalPrice" id="totalPrice" value="<fmt:formatNumber value='0'/>" readonly />
      </div>
    </div>
    <br/>
    <!-- 구매하기/장바구니담기/계속쇼핑하기 처리 -->
    <hr/>
    <div class="text-center">
      <button class="button button1" onclick="order()">구매하기</button>
      <div class="btn-group">
        <button class="button button2" onclick="cart()">장바구니담기</button>
        <button class="button button2" onclick="location.href='${ctp}/dbShop/dbProductList?categorySubCode=${productVo.categorySubCode}';">계속쇼핑하기</button>
      </div>
    </div>
  </div>
</div>
<br/><br/>

  <!-- 상품 상세설명 보여주기 -->
  <hr/>
	<button class="tablink" onclick="openPage('content', this)"  id="defaultOpen">상품상세정보</button>
	<button class="tablink" onclick="openPage('review', this)">상품후기</button>
	<button class="tablink" onclick="openPage('QnA', this)">고객문의</button>
	<button class="tablink" onclick="openPage('info', this)">상품구매안내</button>
	
	<!-- 상품상세정보 -->
	<div id="content" class="tabcontent text-center"><br/>
  		${productVo.content}
  	</div>
	<div id="review" class="tabcontent">
	    <!-- 상품후기 -->
		<div class="container p-0 m-0">
			<div class="text-left"><h5><b>후기 <font color="fc8404">${productVo.reviewCount}</font>개</b></h5></div>
				<div class="text-center" style="width:980px;  border:1px solid #d7d5d5; background-color:#f8f8f8;margin-top:10px">
				<div style="margin:10px;">
				<c:if test="${productVo.reviewCount==0}"> 
					<div class="text-left" style="margin:10px"><font size="2px"><b>작성된 후기가 없습니다.</b></font></div>
				</c:if>
				<div id="reply">
					<table class="table table-hover text-center">
						<c:forEach var="reviewVo" items="${reviewVos}">
						<tr style="border-style:hidden">
							<th style="width:100px" class="text-center" >
								${reviewVo.mid}&nbsp;&nbsp;&nbsp; | 
							</th>
							<th style="width:150px">${reviewVo.WDate}</th>
							<th>
								<c:if test="${reviewVo.mid==sMid}"> 
									<input type="button" value="삭제" onclick="reviewDelete(${reviewVo.idx})"class="btnn btn1"/>
								</c:if>
							</th>
						</tr>
						<tr style="border-style:hidden">
							<td colspan="5" class="text-left" style="padding-left:20px;">
								${fn:replace(reviewVo.content,newLine,"<br/>")}
							</td>
						</tr>	
						</c:forEach>
					</table>
				</div>
			</div>
		</div>
		</div>
	</div>
	<!-- 고객문의 -->
	<div id="QnA" class="tabcontent">
	  <div class="container p-0 m-0">
			<div class="text-left"><h5><b>문의</b></h5></div>
				<div class="text-center" style="width:980px;  border:1px solid #d7d5d5; background-color:#f8f8f8;margin-top:10px">
				<div style="margin:10px;">
				<c:if test="${productVo.qnaCount==0}"> 
					<div class="text-left" style="margin:10px"><font size="2px"><b>게시물이 없습니다.</b></font></div>
				</c:if>
				<div id="QnA">
					<table class="table table-hover text-center">
						<c:forEach var="qnaVo" items="${qnaVos}">
						<tr style="border-style:hidden">
							<th style="width:100px" class="text-center" >
								<c:if test="${qnaVo.level <= 0}"> <!-- 부모댓글의 경우 들여쓰지 않음 -->
									${qnaVo.mid}&nbsp;&nbsp;&nbsp; | 
								</c:if>
								<c:if test="${qnaVo.level > 0}"> <!-- 부모댓글이 아닐 경우 들여씀 -->
								<c:forEach var="i" begin="1" end="${qnaVo.level}">└</c:forEach>
									${qnaVo.mid}			
								</c:if>
							</th>
							<th style="width:150px">${qnaVo.WDate}</th>
							<th>
								<c:if test="${sLevel==0 or sMid==qnaVo.mid}">
									<input type="button" value="삭제" onclick="QnADelete(${qnaVo.idx})"class="btnn btn1"/>
								</c:if>
							</th>
						</tr>
						<tr style="border-style:hidden">
							<td colspan="5" class="text-left" style="padding-left:20px;">
							<c:if test="${qnaVo.level <= 0}"> <!-- 부모댓글의 경우 들여쓰지 않음 -->
								${fn:replace(qnaVo.content,newLine,"<br/>")}&nbsp;&nbsp;&nbsp;  
							</c:if>
							<c:if test="${qnaVo.level > 0}"> <!-- 부모댓글이 아닐 경우 들여씀 -->
							<c:forEach var="i" begin="1" end="${qnaVo.level}"> &nbsp;&nbsp;</c:forEach>
								${fn:replace(qnaVo.content,newLine,"<br/>")}					
							</c:if>
							</td>
						</tr>	
						<tr>
							<td class="text-right">
								<c:if test="${sLevel==0}">
									<input type="button" value="답글" onclick="insertQnA('${qnaVo.idx}','${qnaVo.level}','${qnaVo.levelOrder}','${qnaVo.mid}')" id="QnABoxOpenBtn${qnaVo.idx}" class="btn btn-secondary btn-sm"/>
									<input type="button" value="닫기" onclick="closeQnA('${qnaVo.idx}')" id="QnABoxCloseBtn${qnaVo.idx}" class="btn btn-info btn-sm" style="display:none;"/>
								</c:if>
								<hr/>
							</td>
						</tr>
						<tr>
							<td colspan="5" class="m-0 p-0" style="border-top:none;"><div id="QnABox${qnaVo.idx}"></div></td>
						</tr>
						</c:forEach>
					</table>
				</div>
			</div>
		</div>
			<!-- 고객문의 댓글 -->
			<c:if test="${sMid!=null}">
				<form name="qnaForm" method="post">
					<div class="text-center" style="margin:20px; width:950px; height:200px;border:1px solid #d7d5d5;" >
						<div class="text-center m-3">
						<div class="text-left">작성자 : ${sName}</div>
						<div style="margin:15px"><textarea rows="4" name="qnaContent" id="qnaContent" class="form-control"></textarea></div>
						<div class="text-right" style="margin:2px">
							<input type="button" value="등록" onclick="qnaCheck()" class="btn btn-default btn-sm"/></div>
						</div>
					</div>
				</form>	
			</c:if>	
		</div>
	</div>
	<!-- 상품구매안내 -->
	<div id="info" class="tabcontent text-center">
		<div class="row" style="border:1px solid #ccc;">
			<div class="col-sm-6" style="padding:30px; border:1px solid #ccc;">
				<div class="text-left" style="font-size:1.3em; font-weight:600;">PAYMENT INFO</div>
				<div class="text-left" style="font-size:1em;">상품결제정보</div>
				<hr/>
				<div class="text-left" style="font-size:0.9em; padding-top:20px; color:#8e908e">
					고액결제의 경우 안전을 위해 카드사에서 확인전화를 드릴 수도 있습니다. 확인과정에서 도난 카드의 사용이나 타인 명의의 주문등 정상적인 주문이 아니라고 판단될 경우 임의로 주문을 보류 또는 취소할 수 있습니다. 
					<br/>
					<br/> 
					<br/> 
					무통장 입금은 상품 구매 대금은 PC뱅킹, 인터넷뱅킹, 텔레뱅킹 혹은 가까운 은행에서 직접 입금하시면 됩니다.<br/><br/> 
					주문시 입력한 입금자명과 실제입금자의 성명이 반드시 일치하여야 하며, 7일 이내로 입금을 하셔야 하며 입금되지 않은 주문은 자동취소 됩니다.
				</div>
			</div>
			<div class="col-sm-6" style="padding:30px; border:1px solid #ccc;">
				<div class="text-left" style="font-size:1.3em; font-weight:600;">DELIVERY INFO</div>
				<div class="text-left" style="font-size:1em;">배송정보</div>
				<hr/>
				<div class="text-left" style="font-size:0.9em; padding-top:20px; color:#8e908e">
					배송 방법 : 택배<br/>
					배송 비용 : 무료<br/>
					배송 기간 : 2일 ~ 2일<br/>
					배송 안내 : - 산간벽지나 도서지방은 별도의 추가금액을 지불하셔야 하는 경우가 있습니다.<br/>
					고객님께서 주문하신 상품은 입금 확인후 배송해 드립니다. 다만, 상품종류에 따라서 상품의 배송이 다소 지연될 수 있습니다.
				</div>
			</div>
		  <div class="col-sm-12" style="padding:30px; border:1px solid #ccc;">
		  	<div class="text-left" style="font-size:1.3em; font-weight:600;">EXCHANGE INFO</div>
				<div class="text-left" style="font-size:1em;">교환 및 반품정보</div>
				<hr/>
				<div class="text-left" style="font-size:0.9em; padding-top:20px; color:#8e908e">
					<b>교환 및 반품 주소</b><br/>
					- [10045] 경기도 김포시 양촌읍 양촌역길 76 1층 물류센터<br/>
					<br/>
					<b>교환 및 반품이 가능한 경우</b><br/>
					- 상품을 공급 받으신 날로부터 7일이내 단, 가전제품의 경우 포장을 개봉하였거나 포장이 훼손되어 상품가치가 상실된 경우에는 교환/반품이 불가능합니다.<br/>
					- 공급받으신 상품 및 용역의 내용이 표시.광고 내용과 다르거나 다르게 이행된 경우에는 공급받은 날로부터 3월이내, 그사실을 알게 된 날로부터 30일이내<br/>
					<br/>
					<b>교환 및 반품이 불가능한 경우</b><br/>
					- 고객님의 책임 있는 사유로 상품등이 멸실 또는 훼손된 경우. 단, 상품의 내용을 확인하기 위하여 포장 등을 훼손한 경우는 제외<br/>
					- 포장을 개봉하였거나 포장이 훼손되어 상품가치가 상실된 경우<br/>
					  &nbsp;&nbsp;(예 : 가전제품, 식품, 음반 등, 단 액정화면이 부착된 노트북, LCD모니터, 디지털 카메라 등의 불량화소에	따른 반품/교환은 제조사 기준에 따릅니다.)<br/>
					- 고객님의 사용 또는 일부 소비에 의하여 상품의 가치가 현저히 감소한 경우 단, 화장품등의 경우 시용제품을 제공한 경우에 한 합니다.<br/>
					- 시간의 경과에 의하여 재판매가 곤란할 정도로 상품등의 가치가 현저히 감소한 경우<br/>
					- 복제가 가능한 상품등의 포장을 훼손한 경우<br/>
					  &nbsp;&nbsp;(자세한 내용은 고객만족센터 1:1 E-MAIL상담을 이용해 주시기 바랍니다.)<br/>
					<br/>
					※ 고객님의 마음이 바뀌어 교환, 반품을 하실 경우 상품반송 비용은 고객님께서 부담하셔야 합니다<br/>
					  &nbsp;&nbsp;(색상 교환, 사이즈 교환 등 포함)<br/>
					<b>단순 변심 교환 및 반품시 최소 3,000원 ~ 최대 12,000원(편도) 의 택배비용이 발생됩니다.(무료배송일 경우 왕복배송비 부과)</b>
				</div>
		  	
		  </div>	
	   </div>
	  </div>
	</div>
	
	<script>
	  // pageName은 보여줄 탭의 id, elmnt는 클릭한 탭의 요소(element), color는 클릭한 탭의 배경색
	  function openPage(pageName, elmnt, color) {
	    var i, tabcontent, tablinks;
	
	    // 모든 탭 컨텐츠를 숨김
	    tabcontent = document.getElementsByClassName("tabcontent");
	    for (i = 0; i < tabcontent.length; i++) {
	      tabcontent[i].style.display = "none";
	    }
	
	    // 모든 탭 링크의 배경색을 초기화
	    tablinks = document.getElementsByClassName("tablink");
	    for (i = 0; i < tablinks.length; i++) {
	      tablinks[i].style.backgroundColor = "";
	    }
	
	    // 선택된 탭의 컨텐츠를 보여주고 배경색을 설정
	    document.getElementById(pageName).style.display = "block";
	    elmnt.style.backgroundColor = color;
	  }
	
	  // 페이지 로드 시 defaultOpen 탭을 클릭(상품상세정보의 id가 defaultOpen으로 설정)
	  document.getElementById("defaultOpen").click();
	</script>

  
</div>
<jsp:include page="/WEB-INF/views/include/footer.jsp"/>
</body>
</html>