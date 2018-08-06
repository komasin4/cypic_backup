package com.komasin4.cypic.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import util.StringUtil;
import com.komasin4.cypic.common.Common;

@Controller
public class AuthController extends Common {

	static final Logger logger = LoggerFactory.getLogger(AuthController.class);

	@Autowired
	private FolderController folderController;

	@PostMapping("/cyAuth")
	public String cyAuth( HttpServletRequest request, HttpServletResponse response
			,@RequestParam("email_rsa") String email_rsa
			,@RequestParam("passwd_rsa") String passwd_rsa
			)	{

		logger.debug("email_rsa:" + email_rsa);
		logger.debug("passwd_rsa:" + passwd_rsa);

		Map<String, String> data = new HashMap<>();
		data.put("email_rsa", email_rsa);
		data.put("passwd_rsa", passwd_rsa);
		data.put("email", "");
		data.put("passwd", "");

		Connection.Response conn_response = null;

		try	{

			//로그인
			conn_response = Jsoup.connect("https://cyxso.cyworld.com/LoginAuth.sk")
                    .userAgent(userAgent)
                    .timeout(3000)
                    .header("Origin", "http://cyxso.cyworld.com")
                    .header("Referer", "http://cyxso.cyworld.com/Login.sk")
                    .header("Accept", accept)
                    .header("Content-Type", content_type)
                    .header("Accept-Encoding", accept_encoding)
                    .header("Accept-Language", accept_language)
                    .data(data)
                    .method(Connection.Method.POST)
                    .execute();

			//세션에 쿠키 저장
			Map<String,String> loginCookie = conn_response.cookies();
			HttpSession session=request.getSession();
			session.setAttribute("cyCookie",loginCookie);

			//메인 페이지 불러오기 (tid 추출을 위함)
			Document cyMain = Jsoup.connect("http://www.cyworld.com")
					.userAgent(userAgent)
                    .header("Accept", accept)
                    .header("Content-Type", content_type)
                    .header("Accept-Encoding", accept_encoding)
                    .header("Accept-Language", accept_language)
	                .cookies(loginCookie) // 위에서 얻은 '로그인 된' 쿠키
					.get();

			//html에서 tid 추출
			String tid = null;
			for(Element el:cyMain.select("script[type]"))	{
				String unescapedHtml = el.data();
				int idx = unescapedHtml.indexOf("var tid=");
				if(idx > -1)	{
					String tid_src = unescapedHtml.substring(idx);
					tid = StringUtil.getVariable(tid_src, "tid");
					logger.debug("tid:" + tid);
				}
			}

			//세션에 tid 값 저장
			session.setAttribute("cyTid",tid);

			return "redirect:getfolder";

		} catch (Exception e)	{
			logger.error(e.getMessage());
		}

		return "0";
	}
}
