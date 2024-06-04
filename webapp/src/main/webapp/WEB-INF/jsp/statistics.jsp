<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<jsp:include page="navbar.jsp" />
<html>
<head>
    <link href="${pageContext.request.contextPath}/css/global.css" rel="stylesheet" />
    <link href="${pageContext.request.contextPath}/css/profile.css" rel="stylesheet" />
    <link href="${pageContext.request.contextPath}/css/statistics.css" rel="stylesheet" />
    <title><spring:message code="title.statistics"/></title>
</head>
<body>
<c:set var="urlFilter" value="${pageContext.request.contextPath}/statistics?filtro="/>
<div class="page">
    <div class="header">
        <h2><spring:message code="statistics.title"/></h2>
        <div class="flex">
            <a href="${urlFilter}semanal">
                <button class="btn-basic rounded-btn btn-selected" id="w"><spring:message code="statistics.week"/></button></a>
            <a href="${urlFilter}semanal">
                <button class="btn-basic rounded-btn" id="m"><spring:message code="statistics.month"/></button></a>
            <a href="${urlFilter}semanal">
                <button class="btn-basic rounded-btn" id="y"><spring:message code="statistics.year"/></button></a>
        </div>
    </div>
    <div class="boxes-container ">
        <div class="box service-box flex">
            <div class="service-box-text">
                <p class=""><spring:message code="statistics.total-requests"/></p>
                <h1 class="appointment-counter"><c:out value="${appointmentCountMap.size}"/>20</h1>
            </div>
        </div>
        <div class="box service-box flex">
            <div class="service-box-text">
                <p class=""><spring:message code="statistics.finished-appointments"/></p>
                <h1 class="appointment-counter"><c:out value="${appointmentCountMap.size}"/>20</h1>
            </div>
        </div>
    </div>
    <div class="header">
        <h3><spring:message code="statistics.appointments-per-service"/></h3>
    </div>
    <div class="boxes-container ">
        <c:forEach items="${serviceList}" var="service" varStatus="loop">
            <a class="none-decoration" href="${pageContext.request.contextPath}/servicio/${service.id}">
                <div class="box service-box flex">
                    <img class="service-box-img" src="${pageContext.request.contextPath}/images/${service.imageId}" alt=<spring:message code="business.service-image"/>>

                    <div class="service-box-text">
                        <h2 class="appointment-counter"><c:out value="${appointmentCountMap[service.id]}"/>20</h2>
                        <p class="service-box-name"><c:out value="${service.name}"/></p>
                    </div>
                </div>
            </a>
        </c:forEach>
    </div>
</div>
</body>
</html>
<script>
    //id add class to btn w m y
</script>