<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<html>
<head>
    <link href="${pageContext.request.contextPath}/css/profile.css" rel="stylesheet" />
    <link href="${pageContext.request.contextPath}/css/business.css" rel="stylesheet" />
    <link href="${pageContext.request.contextPath}/css/global.css" rel="stylesheet" />
    <link href="https://fonts.googleapis.com/icon?family=Material+Icons" rel="stylesheet">
</head>
<body>
<c:set var="popUpId" value="-${loop.count}"/>
<c:set var="descId" value="d${loop.count}"/>
<c:set var="description" value="${appointment.description}"/>
<c:set var="renewTitle"><spring:message code="appointments.renew.title"/></c:set>
    <div class="appointment-container" id="${loop.count}">
        <div class="box appointment-box">
            <span class="appointment-field day"><c:out value="${history? appointment.startDateWithYearString : appointment.startDateString}"/></span>
            <span class="appointment-field time"><i class="material-icons icon">schedule</i> <c:out value="${appointment.startDateTimeString}"/>
                    <c:if test="${appointment.duration}">
                        - <c:out value="${appointment.endDateTimeString}"/>
                    </c:if>
                    </span>
            <a class="appointment-field service-name-not-bold" href="${pageContext.request.contextPath}/servicio/${appointment.serviceid}">
                <c:out value="${serviceName}"/>
            </a>
            <span class="appointment-field id"> #<c:out value="${appointment.id}"/></span>

            <div class="decision-container appointment-field">
                <c:choose>
                    <c:when test="${history}">
                        <a class="none-decoration" href="${pageContext.request.contextPath}/contratar-servicio/${appointment.serviceid}">
                            <button class="decision-btn accept-btn" ><i class="material-icons  icon accept-icon" title="${renewTitle}">event_repeat</i></button>
                        </a>
                    </c:when>
                    <c:when test="${confirmed || isUser}">
                        <button onclick="showPopUpApp(${popUpId})" class="decision-btn accept-btn" ><i class="material-icons  icon cancel-icon">delete</i></button>
                    </c:when>
                    <c:otherwise>
                        <button onclick="acceptAppointment(${appointment.id},true,${loop.count})" class="decision-btn accept-btn" ><i class="material-icons  icon accept-icon">check</i></button>
                        <button onclick="showPopUpApp(${popUpId})" class="decision-btn decision-btn"><i class="material-icons  deny-icon">add</i></button>
                    </c:otherwise>
                </c:choose>
            </div>
            <button class="appointment-field accordion-btn" onclick="showAccordion(${loop.count},this)">
                <i class="material-icons icon">arrow_drop_down</i>
            </button>
        </div>

        <c:set var="appointmentId" value="${appointment.id}" scope="request"/>
        <c:set var="id" value="${popUpId}" scope="request"/>
        <jsp:include page="appointmentPopUp.jsp" />
        <c:set var="appointmentDescription" value="${description}" scope="request"/>
        <c:set var="id2" value="${descId}" scope="request"/>
        <jsp:include page="descriptionPopUp.jsp" />

        <div class="accordion-container">
            <c:if test="${confirmed}">
                <div class="appointment-field contact-container">
                    <c:if test="${not isUser}">
                        <span class="accordion-field"><i class="material-icons icon">account_circle</i>
                            <c:out value="${name}"/></span>
                    </c:if>
                    <span class=" accordion-field"><i class="material-icons icon">mail</i> <c:out value="${email}"/></span>
                </div>
            </c:if>
            <div class="appointment-field contact-container description-field">
                <span class="appointment-field accordion-field"><i class="material-icons icon">house</i>
                    <c:choose>
                        <c:when test="${appointment.homeService}" >
                            <c:out value="${appointment.location}"/></c:when>
                        <c:otherwise> <spring:message code="service.at-professional-house"/> </c:otherwise>
                    </c:choose>
                </span>
                <c:if test="${!isUser}">
                    <span class="accordion-field description-span ${confirmed? 'smaller-box':''} ">
                        <c:choose>
                            <c:when test="${appointment.description != null && appointment.description != ''}">
                                <c:out value="${appointment.description}" />
                            </c:when>
                            <c:otherwise>
                               <spring:message code="appointment.no-description"/>
                            </c:otherwise>
                        </c:choose>
                    </span>
                </c:if>
            </div>
            <c:choose>
                <c:when test="${isUser}">
                    <a href="${pageContext.request.contextPath}/turno/${appointment.serviceid}/${appointment.id}" class="none-decoration info-access">
                        <button class="info-access center-vertically info-btn"><i class="material-icons icon info-icon ">info</i> <spring:message code="appointment.info"/> </button>
                    </a>
                </c:when>
                <c:when test="${not empty appointmentDescription}">
                    <a onclick="showPopUpApp('d${loop.count}')" class=" center-vertically info-btn info-access"><i class="material-icons icon info-icon ">info</i> <spring:message code="read-more"/> </a>
                </c:when>
            </c:choose>

        </div>
    </div>
</body>


