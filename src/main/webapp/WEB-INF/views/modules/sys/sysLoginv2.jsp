<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="org.apache.shiro.web.filter.authc.FormAuthenticationFilter"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html lang="en">
<head>
	<title>${fns:getConfig('productName')} 登录</title>
	<meta name="decorator" content="default"/>
    <link rel="stylesheet" href="${ctxStatic}/common/typica-login.css">
	<style type="text/css">
		.control-group{border-bottom:0px;}
	</style>
	    <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0">
	<link href="${ctxStatic}/css/bootstrap.min.css" rel="stylesheet">
    <link href="${ctxStatic}/css/font-awesome.min.css?v=4.4.0" rel="stylesheet">
    <link href="${ctxStatic}/css/animate.min.css" rel="stylesheet">
    <link href="${ctxStatic}/css/style.min.css" rel="stylesheet">
    <link href="${ctxStatic}/css/login.min.css" rel="stylesheet">
    <script src="${ctxStatic}/common/backstretch.min.js"></script> 
	<script type="text/javascript">
		$(document).ready(function() {
			$.backstretch([
 		      "${ctxStatic}/images/bg1.jpg", 
 		      "${ctxStatic}/images/bg2.jpg",
 		      "${ctxStatic}/images/bg3.jpg"
 		  	], {duration: 10000, fade: 2000});
			
			$("#loginForm").validate({
				rules: {
					validateCode: {remote: "${pageContext.request.contextPath}/servlet/validateCodeServlet"}
				},
				messages: {
					username: {required: "请填写用户名."},password: {required: "请填写密码."},
					validateCode: {remote: "验证码不正确.", required: "请填写验证码."}
				},
				errorLabelContainer: "#messageBox",
				errorPlacement: function(error, element) {
					error.appendTo($("#loginError").parent());
				} 
			});
		});
		// 如果在框架中，则跳转刷新上级页面
		if(self.frameElement && self.frameElement.tagName=="IFRAME"){
			parent.location.reload();
		}
	</script>
</head>
<body class="signin"> 
       <div class="signinpanel">
        <div class="row">
            <div class="col-sm-7">
                <div class="signin-info">
                    <div class="m-b"></div>
                    
                </div>
            </div>
            <div class="col-sm-5">
                <form method="post" action="index.html">
                    <p class="m-t-md">欢迎登陆</p>
                    <input type="text" class="form-control uname" placeholder="用户名" />
                    <input type="password" class="form-control pword m-b" placeholder="密码" />
                    <a href="">忘记密码了?</a>
                    <button class="btn btn-success btn-block">登录</button>
                </form>
            </div>
        </div>
        <div class="signup-footer">
            <div class="pull-left">
                &copy; 2016 All Rights Reserved.
            </div>
        </div>
    </div>
  </body>
</html>