<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<jsp:include page="navbar.jsp" />
<html>
<head>
    <link href="${pageContext.request.contextPath}/css/userQuestions.css" rel="stylesheet" />
    <link href="${pageContext.request.contextPath}/css/global.css" rel="stylesheet" />
    <link href="https://fonts.googleapis.com/icon?family=Material+Icons" rel="stylesheet">
    <title><spring:message code="title.questions"/></title>
</head>
<body>
<div class="page">
    <jsp:include page="./components/backButton.jsp" />
    <h2><spring:message code="questions"/></h2>

    <c:choose>
        <c:when test="${not empty pendingQst}">
            <h4 class="notification-header"><i class="material-icons notification-icon">notifications_active</i><spring:message code="questions.new"/></h4>
            <c:forEach items="${pendingQst}" var="qst">
                <div class="question-box">
                    <c:url value="/servicio/${qst.key.serviceid}" var="serviceUrl"/>
                    <a class="none-decoration" href="${serviceUrl}">
                        <p class="qst-service">En <c:out value="${qst.value}"/></p>
                    </a>
                    <div class="qst-date-box">
                        <label class="qst"><c:out value="${qst.key.question}"/></label>
                        <label class="qst-date"><c:out value="${qst.key.date}"/></label>
                    </div>
                    <c:url value="/responder/${qst.key.id}/?pagina=1" var="askUrl"/>
                    <form:form action="${askUrl}" method="post" modelAttribute="responseForm">
                        <div class="flex">
                            <form:input path="response" type="text" class="input" placeholder=""/>
                            <input type="submit" value="<spring:message code="questions.send"/>" class="send-btn">
                        </div>
                        <form:errors path="response" element="p" cssClass="error"/>
                    </form:form>
                </div>
            </c:forEach>
        </c:when>
        <c:otherwise>
            <h4 class="notification-header"><i class="material-icons notification-icon">notifications_off</i><spring:message code="questions.none"/></h4>
            <jsp:include page="components/noResults.jsp"/>
        </c:otherwise>
    </c:choose>

    <c:url value="/negocios/consultas/?" var="questionsPath"/>
    <div class="padding-bottom">
        <c:if test="${pageCount>1}">
            <c:set var="page" value="${page}" scope="request" />
            <c:set var="pageCount" value="${pageCount}" scope="request" />
            <c:set var="path" value="${questionsPath}" scope="request" />
            <jsp:include page="components/pagination.jsp"/>
        </c:if>
    </div>
</div>

<script>
    window.onload = getPreviousPageInfo(document.referrer);
</script>
</body>
</html>