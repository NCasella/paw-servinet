<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<jsp:include page="navbar.jsp" />
<html>
<head>
    <link href="${pageContext.request.contextPath}/css/profile.css" rel="stylesheet" />
    <link href="${pageContext.request.contextPath}/css/global.css" rel="stylesheet" />
    <title><spring:message code="title.my-appointments"/></title>
</head>
<body>
<c:set var="isUser" value="true" scope="request" />
<div class="page">
    <div class="header">
        <h2><spring:message code="appointments.my-appointments"/></h2>
        <div class="switch-btn">
            <a href="${pageContext.request.contextPath}/turnos/?confirmados=true">
                <button class="btn-basic btn-left ${confirmed? 'btn-selected':''}" ><spring:message code="appointments.next"/></button></a>
            <a href="${pageContext.request.contextPath}/turnos/?confirmados=false">
                <button class="btn-basic btn-right ${!confirmed? 'btn-selected':''}" ><spring:message code="appointments.requested"/></button></a>
        </div>
    </div>
    <div class="appointments-container">
        <c:forEach items="${appointmentList}" var="appointment" varStatus="loop">
            <c:set var="appointment" value="${appointment}" scope="request" />
            <c:set var="loop" value="${loop}" scope="request" />
            <c:set var="serviceName" value="${serviceContactInfoMap[appointment.serviceid].serviceName}" scope="request" />
            <c:set var="email" value="${serviceContactInfoMap[appointment.serviceid].businessEmail}" scope="request" />
            <jsp:include page="components/appointmentContainer.jsp"/>
        </c:forEach>
        <c:if test="${ empty appointmentList}">
            <c:set var="urlCallToAction" value="${pageContext.request.contextPath}/" scope="request" />
            <c:set var="textCallToAction" scope="request"><spring:message code="services.look-for-services"/></c:set>
            <jsp:include page="components/noResults.jsp"/>
        </c:if>
    </div>
</div>
</body>
</html>

<jsp:include page="appointmentScript.jsp" />