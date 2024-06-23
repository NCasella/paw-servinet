<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<jsp:include page="navbar.jsp" />
<html>
<head>
    <link href="${pageContext.request.contextPath}/css/profile.css" rel="stylesheet" />
    <link href="${pageContext.request.contextPath}/css/business.css" rel="stylesheet" />
    <link href="${pageContext.request.contextPath}/css/global.css" rel="stylesheet" />
    <link href="https://fonts.googleapis.com/icon?family=Material+Icons" rel="stylesheet">
    <title><spring:message code="title.business-appointments"/></title>
</head>
<body>
<c:set var="isUser" value="false" scope="request" />
<c:set var="notConfirmed" value="${!confirmed}" />
<c:set var="requestForBusiness" value="${!confirmed}" scope="request" />
<div class="page">
    <jsp:include page="./components/backButton.jsp" />
    <div class="header">
        <h2><c:out value="${business.businessName}"/></h2>
        <div>
            <a href="${pageContext.request.contextPath}/negocio/${businessId}/turnos?confirmados=true">
                <button class="btn-basic btn-left ${confirmed? 'btn-selected':''}"><spring:message code="business.next"/>
                    (<span id="${confirmed? 'totalResults':'moreResults'}"></span>)</button></a>
            <a href="${pageContext.request.contextPath}/negocio/${businessId}/turnos?confirmados=false">
                <button class="btn-basic btn-right ${notConfirmed? 'btn-selected':''}"><spring:message code="business.requests"/>
                    (<span id="${notConfirmed? 'totalResults':'moreResults'}"></span>)</button></a>
        </div>
    </div>
    <div class="appointments-container">
        <c:forEach items="${appointmentList}" var="appointment" varStatus="loop">
            <c:set var="appointment" value="${appointment}" scope="request" />
            <c:set var="loop" value="${loop}" scope="request" />
            <c:set var="serviceName" value="${serviceMap[appointment.serviceid].name}" scope="request" />
            <c:set var="email" value="${userMap[appointment.userid].email}" scope="request" />
            <c:set var="name" value="${userMap[appointment.userid].fullName}" scope="request"/>
            <jsp:include page="components/appointmentContainer.jsp"/>
        </c:forEach>
        <c:choose>
            <c:when test="${ empty appointmentList}">
                <jsp:include page="components/noResults.jsp"/>
            </c:when>
            <c:otherwise>
                <div class="loader" id="loader"></div>
                <div id="pagination">
                    <c:if test="${pageCount > 1}">
                        <div class="pagination-box">
                            <c:set var="page" value="${page}" scope="request" />
                            <c:set var="pageCount" value="${pageCount}" scope="request" />
                            <c:set var="path" value="${pageContext.request.contextPath}/negocio/${businessId}/turnos?confirmados=${confirmed}&" scope="request" />
                            <jsp:include page="components/pagination.jsp" />
                        </div>
                    </c:if>
                </div>
            </c:otherwise>
        </c:choose>
    </div>

<script>
    window.onload = getPreviousPageInfo(document.referrer);
</script>

</div>

</body>
</html>

<jsp:include page="appointmentScript.jsp" />