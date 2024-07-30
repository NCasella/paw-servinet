<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<jsp:include page="navbar.jsp" />
<html>
    <head>
        <meta name="viewport" content="width=device-width; initial-scale=1.0">
        <link href="${pageContext.request.contextPath}/css/global.css" rel="stylesheet" />
        <link href="${pageContext.request.contextPath}/css/post.css" rel="stylesheet" />
        <title><spring:message code="login"/></title>
    </head>
    <body>
        <c:url value="/login" var="postPath"/>
        <div class="postForm page">
        <form:form action="${postPath}" method="post" cssClass="form">
        <h2 class="form-title highlight-text"><spring:message code="login"/></h2>
            <div>
                <label>
                    <p class="label">
                    <spring:message code="email"/>
                    </p>
                    <input name="email" type="email" class="input" placeholder="<spring:message code="input.email"/>" required />
                </label>
                <form:errors path="email" element="p" cssClass="error"/>
            </div>
            <div>
                <label>
                    <p class="label">
                    <spring:message code="password"/>
                    </p>
                    <input name="password" type="password" class="input" placeholder="<spring:message code="input.password"/>" required />
                </label>
                <form:errors path="password" element="p" cssClass="error"/>
            </div>
            <c:if test="${param.error!=null}">
                <p class="error"><spring:message code="error.badcredentials"/></p>
            </c:if>
            <div>
                <label class="password-options">
                    <div>
                        <input type="checkbox" name="remember-me"/>
                        <spring:message code="login.rememberme"/>
                    </div>
                    <a href="${pageContext.request.contextPath}/olvide-mi-clave"><spring:message code="login.forgotpassword"/></a>
                </label>
            </div>
            <div class="register-options">
                <p><spring:message code="login.register-1"/> <a href="${pageContext.request.contextPath}/registrarse"><spring:message code="login.register-2"/></a>
                </p>
            </div>
            <div class="align-center">
                <input type="submit" value="<spring:message code="login.submit"/>" class="btn submit-btn">
            </div>
        </form:form>
        </div>
    </body>
</html>