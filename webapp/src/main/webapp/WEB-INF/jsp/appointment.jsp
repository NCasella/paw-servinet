<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<jsp:include page="navbar.jsp" />
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/global.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/appointment.css">
    <link href="https://fonts.googleapis.com/icon?family=Material+Icons" rel="stylesheet">
    <title><spring:message code="title.appointment" arguments="${appointment.id}"/></title>
</head>
<body>
<div class="page">
    <div class="appointment-container">
        <div class="title">
            <h1 class="form-title"><spring:message code="appointment.detail" arguments="${appointment.id}"/></h1>
        </div>
        <c:if test="${appointment != null}">
            <div class="box">
                <div class="box-info">
                    <div class="align-center">
                        <c:choose>
                            <c:when test="${confirmed}">
                                <i class="material-icons confirmed-icon">check</i>
                                <h2><spring:message code="appointment.ready" arguments="${fn:escapeXml(user.name)}"/></h2>
                            </c:when>
                            <c:otherwise>
                                <i class="material-icons waiting-icon">schedule</i>
                                <h2><spring:message code="appointment.waiting-confirmation"/></h2>
                            </c:otherwise>
                        </c:choose>
                    </div>
                    <div class="align-center">
                        <c:choose>
                            <c:when test="${confirmed}">
                                <p><spring:message code="appointment.confirmed"/></p>
                            </c:when>
                            <c:otherwise>
                                <div class="state-text">
                                    <p><spring:message code="appointment.successfully-requested"/></p>
                                    <p><spring:message code="appointment.mail-send"/></p>
                                </div>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </div>
            </div>
        </c:if>

        <div class="box">
            <div class="box-info">
                <h3><spring:message code="service.info"/></h3>
                <p><span class="highlight-text"><spring:message code="service-name"/>:</span>
                    <a class="service-name none-decoration" href="${pageContext.request.contextPath}/servicio/${appointment.serviceid}">
                        <c:out value="${service.name}"/></a><p>
                <p><span class="highlight-text"><spring:message code="appointment.service.description"/> </span><c:out value="${service.description}"/><p>
                <p><span class="highlight-text"><spring:message code="appointment.service.price"/></span> <c:out value="${service.price}"/></p>

                <h3 class="appointment-info"><spring:message code="appointment.info"/></h3>
                <p><span class="highlight-text"><spring:message code="appointment.status"/> </span> <c:out value="${appointment.confirmed ? 'Confirmado':'Pendiente de confirmación'}"/></p>
                <p><span class="highlight-text"><spring:message code="appointment.solicitor-name"/> </span> <c:out value="${user.name}"/> <c:out value="${user.surname}"/></p>
                <p><span class="highlight-text"><spring:message code="appointment.date"/> </span><c:out value="${appointment.startDateWithTimeString}"/></p>
                <p><span class="highlight-text"><spring:message code="appointment.location"/> </span> <c:out value="${service.homeService? appointment.location : service.location}"/></p>
                <c:if test="${appointment.description != '' && appointment.description != null }">
                    <p><span class="highlight-text"><spring:message code="appointment.user-description"/> </span> <c:out value="${appointment.description}"/></p>
                </c:if>
            </div>
        </div>

        <c:url value="/cancelar-turno/${appointment.id}" var="cancelUrl" />
        <c:set var="title" scope="request"><spring:message code="popup.appointment.title"/></c:set>
        <c:set var="message" scope="request"><spring:message code="popup.appointment.message"/></c:set>
        <c:set var="action" scope="request"><spring:message code="popup.appointment.cancel"/></c:set>
        <c:set var="method" value="post" scope="request"/>
        <c:set var="url" value="${cancelUrl}" scope="request"/>


        <div class="align-center">
            <button onclick="showPopUp()" class="cancelBtn appointment-cancelBtn"><spring:message code="appointment.cancel"/></button>
            <jsp:include page="components/popUp.jsp" />
        </div>
    </div>
</div>
</body>

<script>
    function showPopUp() {
        document.getElementById("popup").style.display = "block";
    }
</script>

</html>
