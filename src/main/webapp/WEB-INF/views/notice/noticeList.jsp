<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<c:set var="ctp" value="${pageContext.request.contextPath}"/>

<!DOCTYPE html>
<html>
<head>
    <title>noticeList.jsp</title>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@4.6.1/dist/css/bootstrap.min.css">
    <jsp:include page="/WEB-INF/views/include/bs4.jsp"/>
    <script>
        'use strict';
        // 검색기처리
        function searchCheck() {
            let searchString=$("#searchString").val();

            if(searchString.trim()==""){
                alert("검색어를 입력하세요!");
                searchForm.searchString.focus();
            }
            else{ 
                searchForm.submit();
            }
        }

        function searchChange() {
            document.getElementById("searchString").focus();        
        }
    </script>
    <style>
        #title{
            background-color:#fafafa;
            font-size:12px;
            color: #555555;
        }
        .text{
            font-size:12px;
            text-align: center;
            color: #555555;
        }
    </style>
</head>
<body>
    <jsp:include page="/WEB-INF/views/include/header1.jsp"/>
    <jsp:include page="/WEB-INF/views/include/nav.jsp"/>
    <div class="container" style="width:60%">
        <div class="w3-main p-5">
            <h4><b>공지사항</b></h4>
            <c:if test="${sLevel == 0}">
                <div class="text-right p-0">
                    <a href="noticeInput" class="btn btn-default btn-sm">글쓰기</a>
                </div>
            </c:if>
            <br/>
            <c:if test="${searchString != null}">
                <div class="text-left" style="font-size:1em; color:#848684">"<font style="color:blue">${searchString}</font>" 검색 결과입니다</div><br/>
            </c:if>
            <table class="table table-hover">
                <tr id="title">
                    <th class="text-center" style="width:50px">번호</th>
                    <th class="text-center" style="width:300px">제목</th>
                    <th class="text-center" style="width:100px">작성자</th>
                    <th class="text-center" style="width:100px">작성일</th>
                    <th class="text-center" style="width:70px">조회수</th>
                </tr>
                <c:set var="curScrStartNo" value="${pageVO.curScrStartNo}"></c:set>
                <c:forEach var="vo" items="${vos}">
                    <tr class="text">
                        <c:if test="${vo.pin == 1}">
                        	<!-- 공지사항 여부가 1(즉, true)인 경우, 공지로 표시 -->
                            <td class="text"><b>공지</b></td>
                            <td class="text-left">
                            	<!-- 글 제목 클릭 시 href 로 해당 글 이동 가능 -->
                                <a href="noticeContent?idx=${vo.idx}&pag=${pageVO.pag}&pageSize=${pageVO.pageSize}"><font color="#555555"><b>${vo.title}</b></font></a>
                                <!-- 작성된 지 24시간 이내인 경우에 빨간색 "N"을 표시 -->
                                <c:if test="${vo.diffTime <=24}"><font color="red">N</font></c:if>
                            </td>
                        </c:if>
                        <c:if test="${vo.pin != 1}">
                            <td>${curScrStartNo}</td>
                            <td class="text-left">
                                <a href="noticeContent?idx=${vo.idx}&pag=${pageVO.pag}&pageSize=${pageVO.pageSize}"><font color="#555555">${vo.title}</font></a>
                                <c:if test="${vo.diffTime <=24}"><font color="red">N</font></c:if>
                            </td>
                        </c:if>
                        <td>${vo.name}</td>
                        <td>
                        	<!-- 작성된 지 24시간 이내라면 시간을, 그 이후라면 날짜를 표시 -->
                            <c:if test="${vo.diffTime <=24}">${fn:substring(vo.WDate,11,19)}</c:if>
                            <c:if test="${vo.diffTime >24}">${fn:substring(vo.WDate,0,10)}</c:if>
                        </td>
                        <!-- 조회 수 -->
                        <td>${vo.readNum}</td>
                    </tr>
                    <!--  curScrStartNo의 값을 1만큼 감소, 감소시킨 값을 curScrStartNo에 할당, 다음 공지사항의 번호를 설정하는 용도 -->
                    <c:set var="curScrStartNo" value="${curScrStartNo-1}"/>
                </c:forEach>
                <tr><td colspan="5" class="padding"></td></tr>
            </table>
        </div>
        <!-- 블록 페이징 처리 시작 -->
        <div class="text-center">
            <ul class="pagination justify-content-center" >
                <c:if test="${pageVO.pag > 1}"> 
                    <li class="page-item"><a href="noticeList?pag=1&pageSize=${pageVO.pageSize}" class="page-link text-secondary">◁◁</a></li>
                </c:if>
                <c:if test="${pageVO.curBlock > 0}">
                    <li class="page-item"><a href="noticeList?pag=${(pageVO.curBlock-1)*pageVO.blockSize + 1}&pageSize=${pageVO.pageSize}" class="page-link text-secondary">◀</a></li>
                </c:if>
                <c:forEach var="i" begin="${(pageVO.curBlock*pageVO.blockSize)+1}" end="${(pageVO.curBlock*pageVO.blockSize)+pageVO.blockSize}">
                    <c:if test="${i <= pageVO.totPage && i == pageVO.pag}">
                        <li class="page-item active"><a href="noticeList?pag=${i}&pageSize=${pageVO.pageSize}" class="page-link bg-secondary border-secondary">${i}</a></li>
                    </c:if>
                    <c:if test="${i <= pageVO.totPage && i != pageVO.pag}">
                        <li class="page-item"><a href='noticeList?pag=${i}&pageSize=${pageVO.pageSize}' class="page-link text-secondary">${i}</a></li>
                    </c:if>
                </c:forEach>
                <c:if test="${pageVO.curBlock < pageVO.lastBlock}">
                    <li class="page-item"><a href="noticeList?pag=${(pageVO.curBlock+1)*pageVO.blockSize + 1}&pageSize=${pageVO.pageSize}" class="page-link text-secondary">▶</a></li>
                </c:if>
                <c:if test="${pageVO.pag != pageVO.totPage}">
                    <li class="page-item"><a href="noticeList?pag=${pageVO.totPage}&pageSize=${pageVO.pageSize}" class="page-link text-secondary">▷▷</a></li>
                </c:if>
            </ul>
        </div>
        <!-- 블록 페이징 처리 끝 -->    
        <!-- 검색기 처리 시작 -->
        <br/>
        <div class="text-center">
            <form name="searchForm" method="get" action="noticeSearch">
                <b>검색 : </b>
                <select name="search" onchange="searchChange()">
                    <option value="title">글제목</option>
                    <option value="content">글내용</option>
                    <option value="name">작성자</option>
                </select>
                <input type="text" name="searchString" id="searchString"/>
                <input type="button" value="검색" onclick="searchCheck()"/>
                <input type="hidden" name="pag" value="${pageVO.pag}"/>
                <input type="hidden" name="pageSize" value="${pageVO.pageSize}"/>
            </form>
        </div>
        <!-- 검색기 처리 끝 -->    
        <br/>
        <br/>
        <br/>
    </div>
    <jsp:include page="/WEB-INF/views/include/footer.jsp"/>
</body>
</html>
