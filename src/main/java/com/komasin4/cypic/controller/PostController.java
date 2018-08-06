package com.komasin4.cypic.controller;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.komasin4.cypic.common.Common;
import com.komasin4.cypic.model.Folder;
import com.komasin4.cypic.model.Post;

@Controller
public class PostController extends Common {

	static final Logger logger = LoggerFactory.getLogger(PostController.class);

	@RequestMapping(value="getpost", method = {RequestMethod.GET, RequestMethod.POST}, produces=MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody List<Post> getFolder(HttpServletRequest request, HttpServletResponse response
			,@RequestParam("folder_id") String folder_id
			)
	{

		logger.debug("getpost...");

		logger.debug("folder_id:" + folder_id);

		List<Post> postList = new ArrayList<Post>();

		try {

			HttpSession session=request.getSession();
			String tid = (String) session.getAttribute("cyTid");
			Map<String,String> loginCookie = (Map<String,String>) session.getAttribute("cyCookie");

			Document posts = Jsoup.connect("http://cy.cyworld.com/home/" + tid + "/postlist?folderid=" + folder_id)
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
				
				Long createAt = Long.valueOf(el.attr("id").substring(el.attr("id").indexOf("_") + 1));
				Date createDate = new Date(createAt);
				SimpleDateFormat formatYYYYMM = new SimpleDateFormat("yyyyMM");
				String yyyymm = formatYYYYMM.format(createDate);
				
				post.setCreateAt(createAt);
				post.setYyyymm(yyyymm);
				

				String style_thumbString = el.getElementsByTag("figure").attr("style");
				post.setThumb(getUrlFromStyle(style_thumbString));

				//List<String> imgs = new ArrayList<String>();
				//imgs.add(getOrgUrl(post.getThumb()));
				//post.setImgs(imgs);

				logger.debug(post.getTitle() + ":" + post.getId() + post.getThumb());
				//postList.add(post);
				//break;
				post.setImgs(getImageFromPost(tid, loginCookie, post));
				postList.add(post);
			}
			
			Post lastPost_before = postList.get(postList.size() - 1);
			Post lastPost = null;

			for(;;)	{
				
				if(lastPost == null)
					lastPost = lastPost_before;
				else if(lastPost_before.getId().equals(lastPost.getId()))
					break;
				
				List<Post> morePostList = getMorePostList(tid, loginCookie, folder_id, lastPost.getId(), lastPost.getCreateAt(), lastPost.getYyyymm());
				if(morePostList == null || morePostList.size() < 1)
					break;
				else
					postList.addAll(morePostList);
				
				lastPost = postList.get(postList.size() - 1);
			}
			

		} catch (Exception e)	{
			logger.error(e.getMessage());
		}

		return postList;
	}
	
	
	@RequestMapping(value="getpostmore", method = {RequestMethod.GET, RequestMethod.POST}, produces=MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody List<Post> getFolder(HttpServletRequest request, HttpServletResponse response
			,@RequestParam("folder_id") String folder_id
			,@RequestParam("lastid") String lastid
			,@RequestParam("lastdate") Long lastdate
			,@RequestParam("lastyymm") String lastyymm
			)
	{

		logger.debug("getpost...");

		logger.debug("folder_id:" + folder_id);
		
		HttpSession session=request.getSession();
		String tid = (String) session.getAttribute("cyTid");
		Map<String,String> loginCookie = (Map<String,String>) session.getAttribute("cyCookie");

		return  getMorePostList(tid, loginCookie, folder_id, lastid, lastdate, lastyymm);
		
	}
	
	
	public List<Post> getMorePostList(String tid, Map<String,String> loginCookie, String folder_id, String lastid, Long lastdate, String lastyymm)	{
		
		List<Post> postList = new ArrayList<Post>();
		
		try {

			Document posts = Jsoup.connect("http://cy.cyworld.com/home/" + tid + "/postmore?folderid=" + folder_id + "&lastid=" + lastid + "&lastdate=" + lastdate + "&lastyymm=" + lastyymm + "&startdate=&enddate=&tagname=&listsize=24")
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
				
				Long createAt = Long.valueOf(el.attr("id").substring(el.attr("id").indexOf("_") + 1));
				Date createDate = new Date(createAt);
				SimpleDateFormat formatYYYYMM = new SimpleDateFormat("yyyyMM");
				String yyyymm = formatYYYYMM.format(createDate);
				
				post.setCreateAt(createAt);
				post.setYyyymm(yyyymm);
				

				String style_thumbString = el.getElementsByTag("figure").attr("style");
				post.setThumb(getUrlFromStyle(style_thumbString));

				logger.debug(post.getTitle() + ":" + post.getId() + post.getThumb());
				
				post.setImgs(getImageFromPost(tid, loginCookie, post));
				postList.add(post);
			}
			

		} catch (Exception e)	{
			logger.error(e.getMessage());
		}

		
		return postList;
	}
	
	public List<String> getImageFromPost(String tid, Map<String,String> loginCookie, Post post)	{
		ArrayList<String> imgList = new ArrayList<String>();
		
		try {

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
						srctext = srctext.replace("/file_down.asp",  "/vm_file_down.asp");
					}

					imgList.add(srctext);
					logger.debug("img src:" + srctext);
				}

			}

		} catch (Exception e)	{
			logger.error(e.getMessage());
		}
		
		
		return imgList;
	}
	
    private static void downloadUsingNIO(String urlStr, String file) throws IOException {
	    	if(urlStr != null && !urlStr.isEmpty())	{
	    	
	        URL url = new URL(urlStr);
	        ReadableByteChannel rbc = Channels.newChannel(url.openStream());
	        FileOutputStream fos = new FileOutputStream(file);
	        fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
	        fos.close();
	        rbc.close();
	    }
    }
	
	@PostMapping(value="getimage")
	public void getImage(HttpServletRequest request, HttpServletResponse response
			//,@RequestBody List<String> imgList
			,@RequestBody Post post
			)
	{
		
		int idx = 0;
		
		for(String img:post.getImgs())	{
			logger.debug("***" + img);
			
			try {
				downloadUsingNIO(img, "/test/" + post.getTitle() + "_" + getFileName(img) );
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
	}	
	
	private String getFileName(String url)	{
		
		String fileName = UUID.randomUUID().toString() + ".jpg";
		//String ext = "";
		
//		if(url.lastIndexOf('.') > -1)
//			ext = url.substring(url.lastIndexOf('.'));
		
		try {
		
			if(url.indexOf("vm_file_down.asp?redirect=") > -1)	{
				String path = url.substring(url.indexOf("vm_file_down.asp?redirect=") + 26);
				if(path.lastIndexOf('/') > -1)
					fileName = path.substring(path.lastIndexOf('/') + 1);
				else if(path.lastIndexOf("%2F") > -1)
					fileName = path.substring(path.lastIndexOf("%2F") + 4);
			} else if(url.indexOf("c2down.cyworld.co.kr") > -1)	{
				fileName = url.substring(url.indexOf("&name=") + 6);
			}	
			fileName = URLDecoder.decode(fileName, "UTF-8");
		} catch (Exception e)	{
			logger.error(e.getMessage());
		}
		
		return fileName;
		
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
}
