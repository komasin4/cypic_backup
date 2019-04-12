package com.komasin4.cypic.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.komasin4.cypic.common.Common;
import com.komasin4.cypic.model.Folder;

@Controller

public class FolderController extends Common {

	static final Logger logger = LoggerFactory.getLogger(FolderController.class);

	@RequestMapping(value="getfolder", method = {RequestMethod.GET, RequestMethod.POST})
	public String getFolder(HttpServletRequest request, HttpServletResponse response
			)
	{

		logger.debug("getfolder");

		HttpSession session=request.getSession();
		String tid = (String) session.getAttribute("cyTid");
		Map<String,String> loginCookie = (Map<String,String>) session.getAttribute("cyCookie");
		List<Folder> folderList = new ArrayList<Folder>();
		String folderListString = "";

		try	{
			if(tid != null)	{

				Document cyHome = Jsoup.connect("http://cy.cyworld.com/home/" + tid + "/menu/?type=folder")
						.userAgent(userAgent)
		                .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8")
		                .header("Content-Type", "application/x-www-form-urlencoded")
		                .header("Accept-Encoding", "gzip, deflate, sdch")
		                .header("Accept-Language", "ko-KR,ko;q=0.8,en-US;q=0.6,en;q=0.4")
		                .cookies(loginCookie) // 위에서 얻은 '로그인 된' 쿠키
		                .ignoreHttpErrors(true).validateTLSCertificates(false).followRedirects(true)
						.get();

				for(Element el:cyHome.select("label"))	{
				//for(Element el:cyHome.select("label[class=menuD03]"))	{
					Folder folder = new Folder(el.getElementsByTag("input").attr("value"), el.getElementsByTag("em").text(), el.getElementsByTag("input").attr("name"));
					logger.debug(el.getElementsByTag("em").text() + ":" + el.getElementsByTag("input").attr("value"));
					folderList.add(folder);
					if(folderListString.equals(""))
						folderListString = el.getElementsByTag("input").attr("value");
					else
						folderListString += "," + el.getElementsByTag("input").attr("value");
				}


			}
		} catch (Exception e)	{
			logger.error(e.getMessage());
		}

		session.setAttribute("folderlist", folderList);
		session.setAttribute("folderListString", folderListString);

		return "folderinfo";

	}
}


