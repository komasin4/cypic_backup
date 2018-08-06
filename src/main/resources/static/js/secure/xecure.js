//document.domain = "cyworld.com";

var xGlobal = {
	CookieDomain		: "cyworld.com",	// 도메인
	CookieMain			: "LOGIN",			// 메인 쿠키
	CookieXecureLevel	: "xlevel",			// 보안 레벨 쿠키
	CookieIPLevel		: "iplevel",		// IP보안 레벨 쿠키
	CookieSaveId		: "saveid",			// 로그인 아이디 저장 여부 쿠키
	CookieLoginId		: "loginid",		// 로그인 아이디
	CookieSavePwd		: "savepwd",		// 로그인 아이디 저장 여부 쿠키
	CookieLoginRSAPwd	: "loginrsapwd",	// 로그인 비밀번호
	CookieALogin		: "alogin",			// 자동 로그인 여부
	CookieALoginTime	: "alogin_time",	// 자동 로그인 설정 시간
	ShowLoginPwd		: "☆☆☆☆☆☆",		// 비밀번호저장시표시되는비번
	XecureActivexURL	: "/common/secure/xecure_frame/xecure_activex_cyworld.html",	// CKKeyPro 동작관련 URL
	XecureBlankURL		: "/common/secure/xecure_frame/xecure_blank.html",			// CKKeyPro 동작관련 빈 페이지
	XecureInstallURL	: "/common/secure/install/TouchEnKey_Installer_skcoms.exe"			// CKKeyPro 설치파일
 }

var xRSA = {
	evalue : 'DD303A4EB455BA81F12DFA168FBB044C99B412CF8EA149709E81A3362B6F3136D577121276CA0CB60D49F958F3FDBA66B6D6CD3FBE0789A237A2DDB42499613D77F74FE8E1DE505B8F768DBD7881759F94EFB5090AC724805759A5516702D35CDAEC7708621A0D39488CACD872BB7AD26F6F5C76E0092FC5F3377A2D2404E48F',
	nvalue : '10001',

	encrypt : function(id_obj, pwd_obj, pwd_rsa_obj){

		try {
	    var rsa = new RSAKey();
	    rsa.setPublic(this.evalue,this.nvalue);
	    var fullData = xCommon.getFullToday()+'|^|'+id_obj.value+'|^|'+pwd_obj.value;
	    var res = rsa.encrypt(fullData);
	    if(res) {
	        pwd_rsa_obj.value = hex2b64(res);
	        return true;
	    	}
	   	else{return false};

		}catch (e) {
		    return false;
		}finally {
			pwd_obj.value = "";
		}
	},

	encryptE2E : function(id_obj, frm_nm, pwd_nm, pwd_rsa_obj){
		try {
		    var rsa = new RSAKey();
		    rsa.setPublic(this.evalue,this.nvalue);

			var CKKey_obj =  document.CKKeyPro;

	    	if (CKKey_obj == null || typeof (CKKey_obj) == "undefined" || CKKey_obj.object == null){
				return false;
			}else{
		        CKKey_obj.E2EInitEx("rsa" , "" , xRSA.evalue, xRSA.nvalue, id_obj.value );
				var res = CKKey_obj.GetEncData("",frm_nm,pwd_nm);
			    if(res) {
			        pwd_rsa_obj.value = hex2b64(res);
			        return true;
			    	}
			   	else{return false};
			}
		} catch (e) {
		    return false;
		} finally {
			document.getElementById(pwd_nm).value = "";
		}
	}
}

var xXecurePop = {
	PopWin : null,
	PopWinURL : "/common/secure/login_desc_pop.html",
	TabNo : 1 ,

 	openWin : function(tabno){
		if (parseInt(tabno) == 1){ // 설명 팝업 (키보드보안)
			this.TabNo = tabno;
			this.PopWin = window.open(this.PopWinURL, 'popXeWin', 'scrollbars=no, toolbar=no, location=no, status=no, menubar=no, resizable=no, , width=500, height=650');
			this.PopWin.focus();
		} else if (parseInt(tabno) == 2) {	// OTP 페이지로 이동
			window.open("https://cymember.cyworld.com/main/uotp/OtpMain.jsp");
		} else if (parseInt(tabno) == 3){ // 설명팝업 (IP보안)
			this.TabNo = tabno;
			this.PopWin = window.open(this.PopWinURL, 'popXeWin', 'scrollbars=no, toolbar=no, location=no, status=no, menubar=no, resizable=no, , width=500, height=650');
			this.PopWin.focus();
		}
	},

	resizePop : function(W,H){

		var browser = navigator.userAgent.toLowerCase();
		//alert(browser);
		var ie = (browser.indexOf("msie") != -1);
		var nt = (browser.indexOf("nt") != -1);
		var ie6 = (browser.indexOf("msie 6") != -1);
		var ie7 = (browser.indexOf("msie 7") != -1);
		var ie8 = (browser.indexOf("msie 8") != -1);
		var etc = (browser.indexOf("konqueror") != -1 || browser.indexOf("safari") != -1);
		var ff = (browser.indexOf("firefox") != -1);

		if (nt){
			if (ie7){
            	window.resizeTo(W, H);
			}else if (ie6){
				window.resizeTo(W, H - 19);
			}else if (ie8){
            	window.resizeTo(W, H + 2);
			}else if (ff){
            	window.resizeTo(W - 2, H + 6);
			}else{
            	window.resizeTo(W, H - 19);
			}
		}else{
            window.resizeTo(W, H + 0);
		}
	}
}

var xXecureLayer = {
	LayerClose : function() {
	    document.getElementById('layerXKDisabled').style.display = "none";
	    //document.getElementById('domain').style.visibility = "visible";
	}
}

var xLogin = {
	cSaveId			: "off",
	cLoginId		: "",
	cSavePwd		: "off",
	cALogin			: "off",
	cLoginRSAPwd	: "",

	getCookie : function ()	{

		if ( (! xCommon.getSubCookie(xGlobal.CookieMain, xGlobal.CookieSaveId)) ||  xCommon.getSubCookie(xGlobal.CookieMain, xGlobal.CookieSaveId) == this.cSaveId ){
			xCommon.setSubCookie(xGlobal.CookieMain, xGlobal.CookieSaveId, this.cSaveId);
			this.cSaveId = "off";
			this.cLoginId = "";
			this.cSavePwd = "off";
			this.cALogin = "off";
			this.cLoginRSAPwd = "";
		}else{
			this.cSaveId = xCommon.getSubCookie(xGlobal.CookieMain, xGlobal.CookieSaveId);
			this.cLoginId = xCommon.getSubCookie(xGlobal.CookieMain, xGlobal.CookieLoginId);
			this.cSavePwd = xCommon.getSubCookie(xGlobal.CookieMain, xGlobal.CookieSavePwd);
			this.cALogin = xCommon.getSubCookie(xGlobal.CookieMain, xGlobal.CookieALogin);
			this.cLoginRSAPwd = xCommon.getSubCookie(xGlobal.CookieMain, xGlobal.CookieLoginRSAPwd);
			if (this.cLoginRSAPwd)
				this.cLoginRSAPwd = this.cLoginRSAPwd.replace(/@@/g,"=");	// RSA 암호화 시 @@ 버그로 = 변환
		}
	},

	setCookie : function () { // (saveid_obj, id_obj){
		var saveid_obj		= arguments[0];
		var id_obj			= arguments[1];
		var savepasswd_obj	= (arguments[2] != null ? arguments[2] : "");
		var RSApasswd_obj	= (arguments[3] != null ? arguments[3] : "");

		if((saveid_obj.type == "checkbox" && saveid_obj.checked == true)
				|| (saveid_obj.type == "hidden" && saveid_obj.value == "on")){
			this.cSaveId = "on";
			saveid_obj.value = this.cSaveId;
			this.cLoginId  = id_obj.value;
		}else{
			this.cSaveId = "off";
			saveid_obj.value = this.cSaveId;
			this.cLoginId = "";
		}
		xCommon.setSubCookie(xGlobal.CookieMain, xGlobal.CookieSaveId, this.cSaveId);
		xCommon.setSubCookie(xGlobal.CookieMain, xGlobal.CookieLoginId, this.cLoginId);

		// 자동로그인 활성화시만  비밀번호 저장
		if(savepasswd_obj != null && savepasswd_obj.disabled == false) {
			if(savepasswd_obj.checked == true){
				this.cSavePwd = "on";
				this.cLoginRSAPwd  = RSApasswd_obj.value;
				this.cLoginRSAPwd = this.cLoginRSAPwd.replace(/=/g,"@@");	// RSA 암호화 시 = 버그로 @@ 변환
			}else{
				this.cSavePwd = "off";
				this.cLoginRSAPwd = "";
			}
			xCommon.setSubCookie(xGlobal.CookieMain, xGlobal.CookieSavePwd, this.cSavePwd);
			xCommon.setSubCookie(xGlobal.CookieMain, xGlobal.CookieLoginRSAPwd, this.cLoginRSAPwd);
		}
	},

	setLoginForm : function () { // (saveid_obj, id_obj) {
		var saveid_obj		= arguments[0];
		var id_obj			= arguments[1];
		var savepasswd_obj	= (arguments[2] != null ? arguments[2] : "");
		var passwd_obj		= (arguments[3] != null ? arguments[3] : "");
		var RSApasswd_obj	= (arguments[4] != null ? arguments[4] : "");

		this.getCookie();

		if(this.cSaveId == "on"){
			saveid_obj.value = "on";
			saveid_obj.checked = true;
			id_obj.focus();
			id_obj.value = this.cLoginId;
		}else{
			saveid_obj.value = "off";
			saveid_obj.checked = false;
			id_obj.value = "";
		}

		if(this.cALogin == "on"){
			savepasswd_obj.checked = true;
			passwd_obj.value = xGlobal.ShowLoginPwd;
			RSApasswd_obj.value = this.cLoginRSAPwd;
		}else{
			if (savepasswd_obj != "") {
				savepasswd_obj.checked = false;
				passwd_obj.value = "";
				RSApasswd_obj.value = "";
			}
		}

		// 2010.03.03 ydahn IP보안 추가
		ipSecure.setText(ipSecure.getCookie());
	},

	chkCookie : function() {
		var savepasswd_obj	= (arguments[0] != null ? arguments[0] : "");
		var passwd_obj		= (arguments[1] != null ? arguments[1] : "");

		if (savepasswd_obj.checked && passwd_obj.value == xGlobal.ShowLoginPwd && this.cLoginRSAPwd != ""){
			return false;
		}else{
			return true;
		}
	},

	chgCheckbox : function (img_obj, saveid_obj){
			if (saveid_obj.value == "on") {
				saveid_obj.value = "off";
				img_obj.className = "";
			} else {
				saveid_obj.value = "on";
				img_obj.className = "check";
			}
	}
}

// Common
var xCommon = {

	getCookie : function (name)	{
		var nameOfCookie  = name + "=";

		var x = 0;
		while ( x <= document.cookie.length )
		{
			var y = ( x + nameOfCookie.length );
			if ( document.cookie.substring( x, y ) == nameOfCookie )
			{
				if ((endOfCookie = document.cookie.indexOf( ";", y )) == -1 ) endOfCookie = document.cookie.length;
				return unescape (document.cookie.substring( y, endOfCookie));
			}
			x = document.cookie.indexOf( " ", x ) + 1;
			if (x == 0) break;
		}
		return;
	},

	getSubCookie : function (name, name2) {
		var CookieValue = this.getCookie(name);

		if (CookieValue == '' || CookieValue == null) { return false; }

		var x = 0;
		var y = 0;
		while ( x <= CookieValue.length && x != -1)
		{
			if (x != 0) { x = CookieValue.indexOf('&', x) + 1; }
			y = CookieValue.indexOf('=', y + 1);

			if (CookieValue.substring(x, y) == name2) {
				z = CookieValue.indexOf('&', x) + 0;
				if (z <= 0) { z = CookieValue.length; }
				return (CookieValue.substring(y + 1, z));
				break;
			}
			else {
				x = y;
			}
		}
	},

	setCookie : function (name, value, isEscape) {

		isEscape = (isEscape == undefined) ? true : false;

		if (isEscape) {
			value = escape(value);
		}

		var todayDate = new Date();
		todayDate.setDate( todayDate.getDate() + 9999 );
		document.cookie = name + "=" + value + "; domain="+ xGlobal.CookieDomain+"; path=/;expires=" + todayDate.toGMTString() + ";";
	},

	setSubCookie : function (name, name2, value) {
		var _value = this.getCookie(name);

		var _subvalue = this.getSubCookie(name, name2);

		if (_value == undefined || _value == "") {
			_value = name2 + "=" + value;
		}
		else if (_subvalue == undefined) {
			_value += "&" + name2 + "=" + value;
		}
		else {
			var CookieDic = _value.split("&");
			for (var i = 0; i < CookieDic.length; i++) {
				var CookiePair = CookieDic[i].split("=");
				if (CookiePair[0] == name2) {
					CookiePair[1] = escape(value);
				}
				CookieDic[i] = CookiePair.join("=");
			}

			_value = CookieDic.join("&");
		}

		this.setCookie(name, _value, false);
	},

	checkBrowser : function() {
		if (navigator.userAgent.toUpperCase().indexOf("MSIE") != -1) return "IE";
		else if (navigator.userAgent.toUpperCase().indexOf("MOZILLA") != -1) return "FF";
		else return null;
	},

	trim : function(str) {
		if (!str) return '';
		return str.replace(/^\s*|\s*$/g, '');
	},

	getFullToday : function()
	{
	    var today = new Date();
	    var buf = "";

	    buf += today.getYear() + "y";
	    buf += (today.getMonth() + 1) + "m";
	    buf += today.getDate() + "d ";
	    buf += today.getHours() + "h";
	    buf += today.getMinutes() + "m";
	    buf += today.getSeconds() + "s";

		return buf;
	}
}

// Xecure
var xXecure = {
	preXlevel  : 2,

	getCookie : function ()	{
			var cXecureLevel = 2;

			if ( (! xCommon.getSubCookie(xGlobal.CookieMain, xGlobal.CookieXecureLevel)) ||  xCommon.getSubCookie(xGlobal.CookieMain, xGlobal.CookieXecureLevel) == cXecureLevel ){
				xCommon.setSubCookie(xGlobal.CookieMain, xGlobal.CookieXecureLevel, cXecureLevel);
			}else{
				cXecureLevel = xCommon.getSubCookie(xGlobal.CookieMain, xGlobal.CookieXecureLevel);

				if (! (cXecureLevel == 1 || cXecureLevel == 2 || cXecureLevel == 3)){
					cXecureLevel = 2;
					xCommon.setSubCookie(xGlobal.CookieMain, xGlobal.CookieXecureLevel, cXecureLevel);
				}
			};
			return cXecureLevel ;
	},

	setCookie : function (value)	{
		xCommon.setSubCookie(xGlobal.CookieMain, xGlobal.CookieXecureLevel, value);
	},

	checkBrowser : function ()	{
		if (xCommon.checkBrowser() == "IE")
			return true;
		else
			return false;
	},

	isCKKeyPro : function () {

		if (document.CKKeyPro == null || typeof (document.CKKeyPro) == "undefined" || document.CKKeyPro.object == null)
			return false;
		else
			return true;
	},

	installActiveX : function () {
		var ckKey = null;
		var ret = false;

		try {
			ckKey = new ActiveXObject("CKSKComm.CKCommInst");
		}
		catch (e) { }

		return (ckKey != null);
	},

	excCKKeyPro : function (preXlevel) {
		document.getElementById("XecureFrame").src=xGlobal.XecureActivexURL;
		this.preXlevel = preXlevel;
	},

	clearCKKeyPro : function(){
		if (document.getElementById("XecureFrame").src != xGlobal.XecureBlankURL)
			document.getElementById("XecureFrame").src = xGlobal.XecureBlankURL;

		if (parent.document.CKKeyPro == null || typeof (parent.document.CKKeyPro) == "undefined" || parent.document.CKKeyPro.object == null){
			return;
		}else{
			parent.document.getElementById("XecureDiv").innerHTML = "";

		try { parent.document.CKKeyPro.Clear("login", "XecureDiv", 0); } catch (e) { }

		}
	},

	getCKKeyProInstall : function () {
		location.href = xGlobal.XecureInstallURL;
	},

	getCKKeyProInstallPop : function () {
		window.open(xGlobal.XecureInstallURL, 'popInstall', 'scrollbars=no, toolbar=no, location=no, status=no, menubar=no, resizable=no, , width=0, height=0');
	},

	showCKKeyProPopup : function () {
		var x, y;
		x = screen.width - 2;
		y = screen.height - 45; // 43 is popup image height
		try {
			document.CKKeyPro.ShowPopup(100, 8000, 100, 5, x, y);
		} catch (err) { }
	},

	hideCKKeyProPopup : function () {
		try {
				if(document.CKKeyPro.HidePopup)
					document.CKKeyPro.HidePopup();
			} catch (err) { }
	},

	clearPASSWD : function() {
		parent.document.login.passwd.value = "";
	}

}

//CKKeyPro OnKeyUp Bug Fix
function XecureCK_UIEevents(frm,ele,event,keycode) {
	var obj;
	var eventObj;

	try{
		obj=document.forms[frm].elements[ele];
		if( document.createEventObject )
		{
			eventObj = document.createEventObject();
			eventObj.keyCode=keycode;
			if(obj)
			{
				obj.fireEvent(event,eventObj);
			}
		}
	}
	catch(e) {}
}

//2010.03.03 ydahn IP보안 추가
var ipSecure = {

	getCookie : function ()	{
			var cXecureLevel = 2;

			if ( (! xCommon.getSubCookie(xGlobal.CookieMain, xGlobal.CookieIPLevel)) ||  xCommon.getSubCookie(xGlobal.CookieMain, xGlobal.CookieIPLevel) == cXecureLevel ){
				xCommon.setSubCookie(xGlobal.CookieMain, xGlobal.CookieIPLevel, cXecureLevel);
			}else{
				cXecureLevel = xCommon.getSubCookie(xGlobal.CookieMain, xGlobal.CookieIPLevel);

				if (! (cXecureLevel == 1 || cXecureLevel == 2 || cXecureLevel == 3)){
					cXecureLevel = 2;
					xCommon.setSubCookie(xGlobal.CookieMain, xGlobal.CookieIPLevel, cXecureLevel);
				}
			};
			return cXecureLevel ;
	},

	setCookie : function (value)	{
		xCommon.setSubCookie(xGlobal.CookieMain, xGlobal.CookieIPLevel, value);
	},

	setText : function (ipLevel) {
		var objIPLevelText = document.getElementById("ipLevelText");
		if (objIPLevelText != null){
			if(ipLevel=='3'){
				objIPLevelText.className="three";
				objIPLevelText.innerHTML = "ON";
			}else if(ipLevel=='2'){
				objIPLevelText.className="two";
				objIPLevelText.innerHTML = "ON";
			}else{
				objIPLevelText.className="one";
				objIPLevelText.innerHTML = "OFF";
			}
		}

		// 네이트 메인 싸이월드 탭용 싸이월드 IPLevel 전달 파라미터 셋팅
		var objipLevel = document.getElementById("iplevel");
		if (objipLevel != null)
			objipLevel.value = ipLevel;
	}
}


//////팝업 리사이징 관련 스크립트
window.cyui={};
cyui.browser={};
cyui.browser.userAgent=navigator.userAgent.toLowerCase();
cyui.browser.isOpera=(cyui.browser.userAgent.indexOf('opera')!=-1);
cyui.browser.isSafari=(cyui.browser.userAgent.indexOf('safari')!=-1);
cyui.browser.isIE=(cyui.browser.userAgent.indexOf('msie')!=-1&&!cyui.browser.isOpera);
cyui.browser.isFF=(cyui.browser.userAgent.indexOf('firefox')!=-1);

cyui.event={};
cyui.event.hnd=[];
cyui.event.remove=function(hnd){
if (window.removeEventListener) hnd[0].removeEventListener(hnd[1], hnd[2], false);
else if (window.detachEvent) hnd[0].detachEvent('on'+hnd[1], hnd[2]);
};
cyui.event.add=function(obj, evt, fnc){
if (window.addEventListener) obj.addEventListener(evt, fnc, false);
else if (window.attachEvent) obj.attachEvent( 'on'+evt, fnc );
return [obj, evt, fnc];
};

cyui.browser.getClientRect=function(){
return self.innerHeight?{width:self.innerWidth,height:self.innerHeight}:((document.documentElement && document.documentElement.clientHeight)?{width:document.documentElement.clientWidth,height:document.documentElement.clientHeight}:{width:document.body.clientWidth,height:document.body.clientHeight});
};
cyui.browser.getVersion=function(){
var ver=navigator.appVersion;
ver=cyui.browser.isIE?(ver.split(';')[1].split(' '))[2]:ver.split(' ')[0];
return parseFloat(ver);
};
cyui.browser.getWindowPosition = function() {
y =  window.screenY || window.screenTop,
x = window.screenX || window.screenLeft
return { x:x, y:y};
};

cyui.setWindowSize=function(iWidth, iHeight){
	var clientRect=cyui.browser.getClientRect();
	var dist={width:iWidth-clientRect.width, height:iHeight-clientRect.height};
	if(dist.width||dist.height){
		document.body.style.width=iWidth+'px';
		document.body.style.height=iHeight+'px';
		if(window.frameElement&&window.frameElement.tagName=='IFRAME'){
			var iframe=window.frameElement;
			iframe.style.width=iWidth+'px';
			iframe.style.height=iHeight+'px';
		}else{
			window.resizeBy(dist.width, dist.height);
			if((cyui.browser.isIE&&cyui.browser.getVersion()>7)||cyui.browser.isFF)
			{
				clientRect=cyui.browser.getClientRect();
				dist={width:iWidth-clientRect.width, height:iHeight-clientRect.height};
				if(dist.width||dist.height) window.resizeBy( dist.width, dist.height );
			}
		}
	}
};

cyui.resize=function(iWidth, iHeight, bIsNow){
if(bIsNow) cyui.setWindowSize(iWidth, iHeight);
else cyui.event.add(window, 'load', function(){cyui.setWindowSize(iWidth, iHeight);});
};
//////팝업 리사이징 관련 스크립트 끝 ///////