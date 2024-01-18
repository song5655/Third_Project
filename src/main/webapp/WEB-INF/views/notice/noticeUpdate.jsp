<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<c:set var="ctp" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>noticeUpdate.jsp</title>
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <jsp:include page="/WEB-INF/views/include/bs4.jsp"/>
    <script src="${ctp}/ckeditor/ckeditor.js"></script>
    <script>
        function fCheck() {
            var title = myForm.title.value;
            var content = myForm.content.value;

            if(title.trim() == "") {
                alert("글제목을 입력하세요");
                myForm.title.focus();
            }
            else {
                myForm.submit();
            }
        }
    </script>
    <style>
        .button {
            border: none;
            color: white;
            text-align: center;
            text-decoration: none;
            display: inline-block;
            font-size: 14px;
            margin: 4px 0px;
            cursor: pointer;
            height:40px;
            width:120px;
        }

        .button1 {border-radius: 2px; background-color:#4a5164;}
        .button2 {border-radius: 2px; background-color:#3cc2b1;}
        .button3 {border-radius: 2px; background-color:#0a253e;}
    </style>
</head>
<body>  
<jsp:include page="/WEB-INF/views/include/header1.jsp"/>
<jsp:include page="/WEB-INF/views/include/nav.jsp"/>
<hr/>
<div class="container" style="width:70%">
    <div class="w3-main p-5">
        <b><h5>공지사항</h5></b>
        <form name="myForm" method="post">
            <table class="table">
                <tr>
                    <th>작성자</th>
                    <td>${vo.name}</td>
                </tr>
                <tr>
                    <th>제목</th>
                    <td><input type="text" name="title" value="${vo.title}" class="form-control" autofocus required /></td>
                </tr>
                <tr>
                    <th>내용</th>
                    <td><textarea rows="6" name="content" id="CKEDITOR" class="form-control" required>${vo.content}</textarea></td>
                    <!-- HomeController에 /imageUpload -->
                    <script>
                        CKEDITOR.replace("content",{
                            height:500,
                            filebrowserUploadUrl : "${ctp}/imageUpload",
                            uploadUrl : "${ctp}/imageUpload"
                        });
                    </script>
                </tr>
                <tr>
                    <th>상단고정</th>
                    <td>
                        <div class="form-check-inline">
                            <label class="form-check-label">
                                <input type="radio" class="form-check-input" name="pin" value="1" <c:if test="${vo.pin=='1'}">checked</c:if>/><font size="2px">YES</font>
                            </label>
                        </div>
                        <div class="form-check-inline">
                            <label class="form-check-label">
                                <input type="radio" class="form-check-input" name="pin" value="0" <c:if test="${vo.pin=='0'}">checked</c:if>/><font size="2px">NO</font>
                            </label>
                        </div>
                    </td>
                </tr>
                <tr>
                    <td colspan="2" class="text-center">
                        <input type="button" value="수정하기" onclick="fCheck()" class="button button1"/> &nbsp;
                        <input type="reset" value="다시입력" class="button button2"/> &nbsp;
                        <input type="button" value="돌아가기" onclick="location.href='${ctp}/notice/noticeList';" class="button button3"/>
                    </td>
                </tr>
            </table>
        </form>
    </div>
</div>
<br/>
<jsp:include page="/WEB-INF/views/include/footer.jsp"/>
</body>
</html>
