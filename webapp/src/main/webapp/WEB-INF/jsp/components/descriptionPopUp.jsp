<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<html>
<head>
    <link href="${pageContext.request.contextPath}/css/popUp.css" rel="stylesheet" />
    <link href="${pageContext.request.contextPath}/css/global.css" rel="stylesheet" />
    <link href="${pageContext.request.contextPath}/css/business.css" rel="stylesheet" />
    <title></title>
</head>
<body>

<div class="popup-contenedor" id="${id2}">
    <div class="popup">

        <h2><spring:message code="appointments.description"/></h2>
        <p class="description-pop-up"> <c:out value="${ appointmentDescription}"/> </p>

        <div class="btns-box">
            <button class="cancelLinedBtn" onclick="closePopup('${id2}')"><spring:message code="close"/></button>
        </div>
    </div>
</div>

</body>
</html>
