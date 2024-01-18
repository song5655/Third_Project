package com.spring.javagreenS_Skg;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class HomeController {
	
	@RequestMapping(value = {"/","/h","/main"}, method = RequestMethod.GET)
	public String home(Locale locale, Model model) {
		return "main/main";
	}
	
	// CKEditor에서 글을 올릴 때 이미지와 함께 올리면 이곳에서 서버 파일시스템에 그림파일을 저장할 수 있도록 처리.
	// CKEditor에서 업로드된 이미지를 서버에 저장하고, 해당 이미지에 대한 URL을 클라이언트에게 제공
	@ResponseBody
	@RequestMapping("/imageUpload")
	public void imageUploadGet(HttpServletRequest request, HttpServletResponse response,
			MultipartFile upload) throws Exception{
		// 'upload'라는 이름의 MultipartFile 파라미터를 받는다. 이는 클라이언트가 업로드한 파일.

		response.setCharacterEncoding("utf-8");
		response.setContentType("text/html; charset=utf-8");
		
		// 만약 업로드된 파일이 "example.jpg"라면, originalFilename은 "example.jpg"
		String originalFilename= upload.getOriginalFilename();
		
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyMMddHHmmss");
		originalFilename = sdf.format(date) +"_"+originalFilename;
		
		byte[] bytes = upload.getBytes();
		
		// 업로드된 파일을 저장할 경로를 설정
		// 현재 세션의 서블릿 컨텍스트 경로를 가져온 후, 그 아래에 /resources/data/ckeditor/를 붙여 실제 이미지 파일이 저장된 디렉토리의 경로를 구성
		String uploadPath = request.getSession().getServletContext().getRealPath("/resources/data/ckeditor/");
		// 서버 파일 시스템에 업로드된 파일을 저장
		OutputStream outStr = new FileOutputStream(new File(uploadPath+originalFilename));
		outStr.write(bytes);
		
		// 서버 파일시스템에 저장된 파일을 화면에 보여주기위한 작업
		PrintWriter out = response.getWriter();
		String fileUrl = request.getContextPath()+"/data/ckeditor/"+originalFilename;
		// 클라이언트에게 JSON 형식의 응답을 전송합니다. 이 응답은 업로드된 파일의 정보 및 URL을 포함
		/* {"atom:"12.jpg","변수":1,~~~}*/
		out.println("{\"originalFilename\":\""+originalFilename+"\",\"uploaded\":1,\"url\":\""+fileUrl+"\"}");
		
		out.flush();
		outStr.close();
	}
}