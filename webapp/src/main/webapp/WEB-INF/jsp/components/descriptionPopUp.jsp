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

<div class="popup-contenedor" id="description-pop-up">
    <div class="popup">

        <h2><spring:message code="appointment.description"/></h2>
        <p class="description-span"> <c:out value="${appointmentDescription}"/> </p>

        <div class="btns-box">
            <button class="cancelLinedBtn" onclick="closePopup('description-pop-up')"><spring:message code="close"/></button>
        </div>
    </div>
</div>

</body>
</html>
