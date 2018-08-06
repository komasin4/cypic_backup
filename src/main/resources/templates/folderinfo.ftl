<html>


<head>
<title>폴더정보</title>
<script src="http://code.jquery.com/jquery.min.js"></script>
<script type="text/javascript">

	var folderlist = "${folderListString}";

	function getPost()	{
		alert('getpost');

		var folderListArray = folderlist.split(",");

		$.each(folderListArray, function(index, value) {
  			//console.log(index + ':' + value);
  			//$('#tempdiv').text(index + ':' + value);
  			//$('#tempdiv').append('<br/>' + index + ':' + value);

  			var param = "folder_id=" + value;

			$.ajax({
				   url: "/getpost",
				   async: false,
				   data: param,
				   success: function(args) {
				      //console.log(index + ":" + value);
				      console.log(args);
				      $('#tempdiv').append('<br/>' + index + ":" + value + ":" + args);
				   },
				   error: function(error) {
				      console.log(error);
				      console.log("error:" + index + ":" + value);
				   }
				});
		});
/*
		$.ajax({
			   url: "/getpost",
			   async: false,
			   dataType: 'jsonp',
			   success: function(json) {
			      ...
			   },
			   error: function(error) {
			      console.log(error);
			   }
			});
*/
	}
	
	function getPostOneFolder()	{
	
		var folder_id = $('#folder_id').val();
	
		//alert("getPostOneFolder:" + folder_id);
		
	  	var param = "folder_id=" + folder_id;
	  	var imgList = new Array();
	  	

		$.ajax({
			   url: "/getpost",
			   async: false,
			   data: param,
			   datatype: "json",
			   success: function(data) {
			      console.log(data);
		          for (i = 0; i < data.length; i++) {
		          	$('#tempdiv').append("<br>" + data[i].id + ":" + data[i].title + ":" + data[i].createAt + ":" + data[i].yyyymm + ":" + data[i].imgs);

					getImages(data[i]);

					/*
		          	for(j = 0; j < data[i].imgs.length ; j++)	{
			          	$('#tempdiv').append("<br>&nbsp&nbsp" + data[i].imgs[j]);
			          	imgList.push(data[i].imgs[j]);
			        }
					*/
    			  }

			   },
			   error: function(error) {
			      console.log(error);
			      console.log("error:" + index + ":" + value);
			   }
		});
		
		/*
		for(z = 0 ; imgList.length ; z++)	{
			download(imgList[z], z + ".jepg");
		}
		*/
		

		
	}
	
	function getImages(post)	{
	
			$.ajax({
			   type : "POST",
			   url: "/getimage",
			   async: false,
			   data: JSON.stringify(post),
			   datatype: "json",
			   contentType : "application/json; charset=UTF-8",
			   success: function(data) {
			      console.log("succ");
			   },
			   error: function(error) {
			      console.log(error);
			   }
		});
	
	}
	
	
	function download(dataurl, filename) {
  		var a = document.createElement("a");
  		a.href = dataurl;
  		a.setAttribute("download", filename);
  		var b = document.createEvent("MouseEvents");
  		b.initEvent("click", false, true);
  		a.dispatchEvent(b);
  		return false;
	}

</script>
</head>

<!--
<body onload="javascript:getPost()">
-->
<body>
<#include "userinfo.ftl">

<form name="getPost" action="">
	<input name="folder_id" id="folder_id" type="text"/>
	<input type="button" value="getPost" onclick="javascript:getPostOneFolder();"/>
</form>

<div id="tempdiv" style="OVERFLOW-Y:auto; width:100%; height:150px;">list...</div>

<br/>

<!--

<ul class="tree">
<#list folderlist as folder>

	<#assign before_depth = 0>

	<#if folder.depth == "depth1">

	    <input type="checkbox" checked="checked" id="c1" />
	    <label class="tree_label" for="c1">${folder.depth},${folder.name}</label>
	    
	    
	<#elseif folder.depth == "depth2">
		<ul>
    	<li>
	    <input type="checkbox" checked="checked" id="c2" />
	    <label for="c2" class="tree_label">${folder.depth},${folder.name}</label>
	<#elseif folder.depth == "depth3">
		<ul>
    	<li><span class="tree_label">${folder.depth},${folder.name}</span></li>
    <#else>
   	</#if>

	<#assign before_depth = folder.depth>
	

</#list>
<ul>

--!>

<#list folderlist as folder>
	<div id="${folder.id}">
    	<p>${folder.depth},${folder.name},${folder.id}
    	<div id="${folder.id}_posts"/>
	</div>
</#list>

</body>

</html>