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
<c:set var="urlFilter" value="${pageContext.request.contextPath}/negocio/${businessId}/estadisticas?filtro="/>
<div class="page">
    <jsp:include page="./components/backButton.jsp" />
    <div class="header">
        <h2><spring:message code="statistics.title"/></h2>
        <div class="flex">
            <a href="${urlFilter}w">
                <button class="btn-basic rounded-btn" id="w"><spring:message code="statistics.week"/></button></a>
            <a href="${urlFilter}m">
                <button class="btn-basic rounded-btn" id="m"><spring:message code="statistics.month"/></button></a>
            <a href="${urlFilter}y">
                <button class="btn-basic rounded-btn" id="y"><spring:message code="statistics.year"/></button></a>
        </div>
    </div>
    <div class="boxes-container margin-auto">
        <div class="box service-box flex">
            <div class="service-box-text">
                <p class=""><spring:message code="statistics.total-requests"/></p>
                <h1 class="appointment-counter"><c:out value="${requestedAppointments}"/></h1>
            </div>
        </div>
        <div class="box service-box flex">
            <div class="service-box-text">
                <p class=""><spring:message code="statistics.finished-appointments"/></p>
                <h1 class="appointment-counter"><c:out value="${finishedAppointments}"/></h1>
            </div>
        </div>
    </div>
    <div class="header">
        <h3><spring:message code="statistics.appointments-per-service"/></h3>
    </div>
    <div class="boxes-container margin-auto">
        <c:forEach items="${serviceAppointmentCountList}" var="pair" varStatus="loop">
            <c:set var="service" value="${serviceMap[pair.key]}"/>
            <a class="none-decoration" href="${pageContext.request.contextPath}/servicio/${service.id}">
                <div class="box service-box flex">
                    <img class="service-box-img" src="${pageContext.request.contextPath}/images/${service.imageId}" alt=<spring:message code="business.service-image"/>>

                    <div class="service-box-text">
                        <h2 class="appointment-counter"><c:out value="${pair.value}"/></h2>
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
    document.getElementById('${filter}').classList.add('btn-selected')
    window.onload = getPreviousPageInfo(document.referrer);
</script>