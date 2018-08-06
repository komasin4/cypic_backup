package com.komasin4.cypic.controller;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.komasin4.cypic.model.Folder;
import com.komasin4.cypic.model.Post;

import util.StringUtil;

@Controller
public class test {

	static final Logger logger = LoggerFactory.getLogger(test.class);

	@GetMapping("test")
	public String test1 ()	{
		return "test";
	}


	@PostMapping("test2")
	public String test2	(
			@RequestParam("email_rsa") String email_rsa
			,@RequestParam("passwd_rsa") String passwd_rsa

			){

		logger.debug("email_rsa:" + email_rsa);
		logger.debug("passwd_rsa:" + passwd_rsa);

		/*


			<form id="login" name="login" id="id_save" target="_self" method="post" onsubmit="return checkInput();">
				<fieldset>
				<input type="text" name="email" value="" id="uid"/>
				<input type="password" name="passwd" value="" id="upw"/>
				<input type="image" src="/common/img/web/btn_login_s.gif" alt="로그인" class="btn_login" />
				<input type="checkbox" name="savecid" value="" id="id_save" />
		        <input type="hidden" name="loginstr" value="" />
		        <input type="hidden" name="redirection" value="http://www.cyworld.com/cymain"/>
	        	<input type="hidden" id="pop" name="pop" value="" />
		        <input type="hidden" id="passwd_rsa" name="passwd_rsa" value=""/>
		        <input type="hidden" id="email_rsa" name="email_rsa" value=""/>
				<input type="hidden" id="iplevel" name="iplevel" value="2" />
				<input type="hidden" id="mode" name="mode" value="" />
				<input type="hidden" id="cpurl" name="cpurl" value="" />
				</fieldset>
			</form>


		 */

		// Window, Chrome의 User Agent.
		String userAgent = "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/57.0.2987.133 Safari/537.36";
//
//		String email = "komasin4@cyworld.com";
//		String password = "skagml@0401";
//
//		// 전송할 폼 데이터
		Map<String, String> data = new HashMap<>();
		data.put("email_rsa", email_rsa);
		data.put("passwd_rsa", passwd_rsa);
		data.put("email", "");
		data.put("passwd", "");
//
//		data.put("rememberLoginId", "1");
//		data.put("redirectUrl", "http://tistory.com/");
//		data.put("ofp", ofp); // 로그인 페이지에서 얻은 토큰들
//		data.put("nfp", nfp);

		// 로그인(POST)

		Connection.Response response = null;

		try	{

			//Connection.Response response = Jsoup.connect("https://cyxso.cyworld.com/LoginAuth")
			response = Jsoup.connect("https://cyxso.cyworld.com/LoginAuth.sk")
		                                    .userAgent(userAgent)
		                                    .timeout(3000)
		                                    .header("Origin", "http://cyxso.cyworld.com")
		                                    .header("Referer", "http://cyxso.cyworld.com/Login.sk")
		                                    .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8")
		                                    .header("Content-Type", "application/x-www-form-urlencoded")
		                                    .header("Accept-Encoding", "gzip, deflate, br")
		                                    .header("Accept-Language", "ko-KR,ko;q=0.8,en-US;q=0.6,en;q=0.4")
		                                    .data(data)
		                                    .method(Connection.Method.POST)
		                                    .execute();

		Map<String,String> loginCookie = response.cookies();

		Document cyMain = Jsoup.connect("http://www.cyworld.com")
				.userAgent(userAgent)
//                .header("Referer", "http://www.tistory.com/")
                .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8")
                .header("Content-Type", "application/x-www-form-urlencoded")
                .header("Accept-Encoding", "gzip, deflate, sdch")
                .header("Accept-Language", "ko-KR,ko;q=0.8,en-US;q=0.6,en;q=0.4")
                .cookies(loginCookie) // 위에서 얻은 '로그인 된' 쿠키
				.get();

		String tid = null;

//		for(Element el:cyMain.getElementsByTag("script"))	{
		for(Element el:cyMain.select("script[type]"))	{
			String unescapedHtml = el.data();
			int idx = unescapedHtml.indexOf("var tid=");
			if(idx > -1)	{
				String tid_src = unescapedHtml.substring(idx);
				tid = StringUtil.getVariable(tid_src, "tid");
				logger.debug("tid:" + tid);
			}
		}

		List<Folder> folderList = new ArrayList<Folder>();
		List<Post> postList = new ArrayList<Post>();

		if(tid != null)	{

			Document cyHome = Jsoup.connect("http://cy.cyworld.com/home/" + tid + "/menu/?type=folder")
					.userAgent(userAgent)
	                .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8")
	                .header("Content-Type", "application/x-www-form-urlencoded")
	                .header("Accept-Encoding", "gzip, deflate, sdch")
	                .header("Accept-Language", "ko-KR,ko;q=0.8,en-US;q=0.6,en;q=0.4")
	                .cookies(loginCookie) // 위에서 얻은 '로그인 된' 쿠키
					.get();

			for(Element el:cyHome.select("label"))	{
				Folder folder = new Folder(el.getElementsByTag("input").attr("value"), el.getElementsByTag("em").text(), el.getElementsByTag("input").attr("name"));
				logger.debug(el.getElementsByTag("em").text() + ":" + el.getElementsByTag("input").attr("value"));
				folderList.add(folder);
			}

			//logger.debug(cyHome.html());

			for(Folder folder:folderList)	{
				Document posts = Jsoup.connect("http://cy.cyworld.com/home/" + tid + "/postlist?folderid=" + folder.getId())
						.userAgent(userAgent)
		                .header("Referer", "http://cy.cyworld.com/home/" + tid)
		                .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8")
		                .header("Content-Type", "application/x-www-form-urlencoded")
		                .header("Accept-Encoding", "gzip, deflate, sdch")
		                .header("Accept-Language", "ko-KR,ko;q=0.8,en-US;q=0.6,en;q=0.4")
		                .cookies(loginCookie) // 위에서 얻은 '로그인 된' 쿠키
						.get();

				for(Element el:posts.select("article"))	{
					Post post = new Post();
					post.setId(el.attr("id").substring(0, el.attr("id").indexOf("_")));
					post.setTitle(el.getElementsByTag("h3").text());

					String style_thumbString = el.getElementsByTag("figure").attr("style");

					post.setThumb(getUrlFromStyle(style_thumbString));

					List<String> imgs = new ArrayList<String>();
					imgs.add(getOrgUrl(post.getThumb()));

					post.setImgs(imgs);

					logger.debug(post.getTitle() + ":" + post.getId() + ":" + post.getThumb() + ":" + (post.getImgs() != null && post.getImgs().size() > 0 ?post.getImgs().get(0):""));
					postList.add(post);

					//break;
				}

				//break;
			}


			List<String> imgList = new ArrayList<String>();

			for(Post post:postList)	{

				Document postDoc = Jsoup.connect("http://cy.cyworld.com/home/" + tid + "/post/" + post.getId())
						.userAgent(userAgent)
		                .header("Referer", "http://cy.cyworld.com/home/" + tid)
		                .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8")
		                .header("Content-Type", "application/x-www-form-urlencoded")
		                .header("Accept-Encoding", "gzip, deflate, sdch")
		                .header("Accept-Language", "ko-KR,ko;q=0.8,en-US;q=0.6,en;q=0.4")
		                .cookies(loginCookie) // 위에서 얻은 '로그인 된' 쿠키
						.get();

				Element postEl = postDoc.getElementById("postData");

				Elements imgEls = postEl.getElementsByTag("img");

				for(Element el:imgEls)	{
					String srctext = el.attr("srctext");
					if(srctext != null)	{
						srctext = URLDecoder.decode(srctext,  "UTF-8");

						if(srctext.indexOf("/file_down.asp") > -1)	{
							srctext.replaceFirst("/file_down.asp",  "/vm_file_down.asp");
						}

						imgList.add(srctext);
						logger.debug("img src:" + srctext);
					}

				}

			}

		}

//		Document cyMain = Jsoup.connect(("https://www.cyworld.com").us
//			                .userAgent(userAgent)
//			                .header("Referer", "http://www.tistory.com/")
//			                .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8")
//			                .header("Content-Type", "application/x-www-form-urlencoded")
//			                .header("Accept-Encoding", "gzip, deflate, sdch")
//			                .header("Accept-Language", "ko-KR,ko;q=0.8,en-US;q=0.6,en;q=0.4")
//			                .cookies(loginCookie) // 위에서 얻은 '로그인 된' 쿠키
//			                .get();




		} catch (Exception e)	{
			e.printStackTrace();
		}
		// 로그인 성공 후 얻은 쿠키.
		// 쿠키 중 TSESSION 이라는 값을 확인할 수 있다.
//		Map<String, String> loginCookie = response.cookies();

		return "test.html";
	}

	public String getUrlFromStyle(String src)	{
		String var = ":url(";
		String value = null;

		try	{

			int idx = src.indexOf(var);

			if(idx > -1)	{
				String s1 = src.substring(idx);
				String s2 = s1.substring(s1.indexOf("'") + 1);
				value = s2.substring(0, s2.indexOf("'"));
			}
		} catch (Exception e)	{
			e.printStackTrace();
		}

		return value;
	}

	public String getOrgUrl(String src)	{
		String var = "&url=";
		String value = null;

		try	{

			int idx = src.indexOf(var);

			if(idx > -1)	{
				String s1 = src.substring(idx);
				String s2 = s1.substring(s1.indexOf("=") + 1);
				//value = s2.substring(0, s2.indexOf("'"));
				value = URLDecoder.decode(s2, "UTF-8");

				if(value.indexOf("/file_down.asp") > -1)	{
					value.replaceFirst("/file_down.asp",  "/vm_file_down.asp");
				}

			}
		} catch (Exception e)	{
			e.printStackTrace();
		}

		return value;
	}
}
