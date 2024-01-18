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
  <title>dbShopList.jsp</title>
	<jsp:include page="/WEB-INF/views/include/bs4.jsp"/>
	<meta name="viewport" content="width=device-width, initial-scale=1">
	<script>
	  'use strict';
	  function productDelCheck(idx) {
		  let ans = confirm("현재 상품을 삭제하시겠습니까?");
		  if(!ans) return false;
		  
		  $.ajax({
			  type : "post",
			  url  : "${ctp}/dbShop/dbShopDeleteOk",
			  data : {idx : idx},
			  success:function() {
				  alert("삭제되었습니다.");
				  location.reload();
			  },
			  error : function() {
				  alert("전송실패!");
			  }
		  });
	  }
	</script>
</head>
<body>
<jsp:include page="/WEB-INF/views/include/header1.jsp"/>
<jsp:include page="/WEB-INF/views/include/nav.jsp"/>
<div class="container" style="width:60%">
<div class="w3-main">
<br>
  <span>[<a href="${ctp}/dbShop/dbShopList">전체보기</a>]</span> /
  <c:forEach var="subTitle" items="${subTitleVos}" varStatus="st">
  	<span>[<a href="${ctp}/dbShop/dbShopList?part=${subTitle.categorySubCode}">${subTitle.categorySubName}</a>]</span>
	  <c:if test="${!st.last}"> / </c:if>
  </c:forEach>
  <hr/>
  <div class="row">
    <div class="col">
	    <h4><b>상품 리스트</b></h4>
    </div>
    <div class="col text-right">
		  <button type="button" class="btn btn-primary" onclick="location.href='${ctp}/dbShop/dbProduct';">상품등록하러가기</button>
    </div>
  </div>
  <hr/>
  <c:set var="cnt" value="0"/>
  <div class="row mt-4">
    <c:forEach var="vo" items="${productVos}">
      <div class="col-md-3">
        <div style="text-align:center">
          <a href="${ctp}/admin/dbShopContent?idx=${vo.idx}&pag=${pageVO.pag}&pageSize=${pageVO.pageSize}">
            <img src="${ctp}/dbShop/product/${vo.FSName}" width="250px" height="250px"/>
            <div>
              <font size="2">${vo.productName}</font>
              <a href="javascript:productDelCheck(${vo.idx})" class="badge badge-danger">삭제</a>
            </div>
            <div><font size="2" color="orange"><fmt:formatNumber value="${vo.mainPrice}" pattern="#,###"/>원</font></div>
            <div><font size="2">${vo.detail}</font></div>
          </a>
        </div>
      </div>
      <c:set var="cnt" value="${cnt+1}"/>
      <c:if test="${cnt%4 == 0}">
      	</div>
        <div class="row mt-5">
      </c:if>
    </c:forEach>
    <div class="container">
      <c:if test="${fn:length(productVos) == 0}"><h3>제품 준비 중입니다.</h3></c:if>
    </div>
  </div>
  <hr/>
</div>
  <!-- 블록 페이징 처리 시작 -->
<div class="text-center">
	<ul class="pagination justify-content-center" >
	  <c:if test="${pageVO.pag > 1}"> 
	  	<li class="page-item"><a href="dbShopList?part=${categorySubCode}&pag=1&pageSize=${pageVO.pageSize}" class="page-link text-secondary">◁◁</a></li>
	  </c:if>
	  <c:if test="${pageVO.curBlock > 0}">
	  	<li class="page-item"><a href="dbShopList?part=${categorySubCode}&pag=${(pageVO.curBlock-1)*pageVO.blockSize + 1}&pageSize=${pageVO.pageSize}" class="page-link text-secondary">◀</a></li>
	  </c:if>
	  <c:forEach var="i" begin="${(pageVO.curBlock*pageVO.blockSize)+1}" end="${(pageVO.curBlock*pageVO.blockSize)+pageVO.blockSize}">
	    <c:if test="${i <= pageVO.totPage && i == pageVO.pag}">
	      <li class="page-item active"><a href="dbShopList?part=${categorySubCode}&pag=${i}&pageSize=${pageVO.pageSize}" class="page-link bg-secondary border-secondary">${i}</a></li>
	    </c:if>
	    <c:if test="${i <= pageVO.totPage && i != pageVO.pag}">
	      <li class="page-item"><a href="dbShopList?part=${categorySubCode}&pag=${i}&pageSize=${pageVO.pageSize}" class="page-link text-secondary">${i}</a></li>
	    </c:if>
	  </c:forEach>
	  <c:if test="${pageVO.curBlock < pageVO.lastBlock}">
	    <li class="page-item"><a href="dbShopList?part=${categorySubCode}&pag=${(pageVO.curBlock+1)*pageVO.blockSize + 1}&pageSize=${pageVO.pageSize}" class="page-link text-secondary">▶</a></li>
	  </c:if>
	  <c:if test="${pageVO.pag != pageVO.totPage}">
	  	<li class="page-item"><a href="dbShopList?part=${categorySubCode}&pag=${pageVO.totPage}&pageSize=${pageVO.pageSize}" class="page-link text-secondary">▷▷</a></li>
	  </c:if>
	</ul>
</div>
<!-- 블록 페이징 처리 끝 -->	
</div>
<p><br/></p>
<jsp:include page="/WEB-INF/views/include/footer.jsp"/>
</body>
</html>