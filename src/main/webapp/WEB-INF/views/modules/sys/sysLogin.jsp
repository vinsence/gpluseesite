<%@ page contentType="text/html;charset=UTF-8"%>
<%@ page
	import="org.apache.shiro.web.filter.authc.FormAuthenticationFilter"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html lang="en">
<head>
<title>${fns:getConfig('productName')}登录</title>
<meta name="decorator" content="default" />
<link rel="stylesheet" href="${ctxStatic}/common/typica-login.css">
<meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0">
<link rel="shortcut icon" href="favicon.ico">
<link href="${ctxStatic}/css/bootstrap.min.css?v=3.3.5" rel="stylesheet">
<link href="${ctxStatic}/css/font-awesome.min.css?v=4.4.0" rel="stylesheet">
<link href="${ctxStatic}/css/animate.min.css" rel="stylesheet">
<link href="${ctxStatic}/css/style.min.css?v=4.0.0" rel="stylesheet">
<script type="text/javascript">
		$(document).ready(function() {
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
<body class="gray-bg">
    <div class="middle-box text-center loginscreen  animated fadeInDown">
        <div>
		<%String error = (String) request.getAttribute(FormAuthenticationFilter.DEFAULT_ERROR_KEY_ATTRIBUTE_NAME);%>
		<div id="messageBox" class="alert alert-error <%=error==null?"hide":""%>"><button data-dismiss="alert" class="close">×</button>
			<label id="loginError" class="error"><%=error==null?"":"com.thinkgem.jeesite.modules.sys.security.CaptchaException".equals(error)?"用户或密码错误, 请重试.":"用户或密码错误, 请重试." %></label>
		</div>
            <form class="m-t"  action="${ctx}/login" method="post">
                <div class="form-group">
                    <input type="text" id="username" name="username" class="form-control" value="${username}" placeholder="登录名">
                </div>
                <div class="form-group">
                    <input type="password" id="password" name="password" class="form-control" placeholder="密码"/>
                </div>
                <button type="submit" class="btn btn-primary block full-width m-b">登 录</button>
                <p class="text-muted text-center"> <a href="login.html#"><small>忘记密码了?</small></a> | <a href="register.html">注册一个新账号</a>
                </p>

            </form>
        </div>
    </div>
    <script src="${ctxStatic}/js/jquery.min.js?v=2.1.4"></script>
    <script src="${ctxStatic}/js/bootstrap.min.js?v=3.3.5"></script>
    <script type="text/javascript" src="http://tajs.qq.com/stats?sId=9051096" charset="UTF-8"></script>
</body>

</html>