<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<jsp:include page="navbar.jsp" />
<html>
<head>
    <title><spring:message code="verify.account"/></title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/global.css" />
    <link href="${pageContext.request.contextPath}/css/post.css" rel="stylesheet" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
</head>
<c:url value="/verificar-cuenta/${token}" var="postUrl"/>
<body>
<div class="postForm page">
    <form:form modelAttribute="ValidateUserForm" action="${postUrl}" method="post" class="form">
        <h2 class="form-title highlight-text"><spring:message code="verify.account"/></h2>
        <label>
            <p class="label"><spring:message code="verification-code"/></p>
            <spring:message code="input.verification-code" var="inputPassword"/>
            <form:errors path="verificationCode" cssClass="error" element="p"/>
            <form:input type="password" id="password" cssClass="input" path="verificationCode" placeholder="${inputPassword}" required="true"/>
        </label>
        <div class="align-center">
            <input type="submit" value="<spring:message code="verify.button"/>" class="btn submit-btn">
        </div>
    </form:form>
</div>
</body>

</html>
