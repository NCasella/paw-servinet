<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<jsp:include page="navbar.jsp" />
<html>
<head>
    <link href="${pageContext.request.contextPath}/css/profile.css" rel="stylesheet" />
    <link href="${pageContext.request.contextPath}/css/global.css" rel="stylesheet" />
    <link href="https://fonts.googleapis.com/icon?family=Material+Icons" rel="stylesheet">
    <title><spring:message code="title.my-businesses"/></title>
</head>
<body>
<c:set var="urlCreateBusiness" value="${pageContext.request.contextPath}/registrar-negocio" />
<div class="page">
    <div class="header">
        <h2><spring:message code="businesses.my-businesses"/></h2>
        <div class="flex center-vertically">
            <c:if test="${not empty businessList}" >
                <a href="${urlCreateBusiness}" class="none-decoration mr">
                    <button class="btn center-vertically"><i class="material-icons">storefront</i> <spring:message code="businesses.add-business"/></button>
                </a>
                <a href="${pageContext.request.contextPath}/negocios/turnos/?confirmados=true" class="none-decoration mr">
                    <button class="btn center-vertically"><i class="material-icons">calendar_today</i> <spring:message code="businesses.appointments"/></button>
                </a>
                <a href="${pageContext.request.contextPath}/negocios/consultas" class="none-decoration">
                    <button class="btn center-vertically"><i class="material-icons">notifications_active</i> <spring:message code="businesses.questions"/></button>
                </a>
            </c:if>
        </div>
    </div>

    <div class="boxes-container">
        <c:forEach items="${businessList}" var="business" varStatus="loop">
            <div class="business-container">
                <a class="none-decoration" href="${pageContext.request.contextPath}/negocio/${business.businessid}">
                    <div class="business-box box">
                        <!--img class="preview-box-img" src="${pageContext.request.contextPath}/images/{business.imageId}" alt="Imagen del servicio"-->
                        <p class="preview-box-text"><c:out value="${business.businessName}"/></p>
                    </div>
                </a>
                <a class="none-decoration" href="${pageContext.request.contextPath}/crear-servicio/${business.businessid}">
                    <div class="new-service">
                        <i class="material-icons">add</i>
                        <spring:message code="business.add-service"/>
                    </div>
                </a>
            </div>
        </c:forEach>
    </div>

    <c:if test="${ empty businessList}">
        <c:set var="urlCallToAction" value="${urlCreateBusiness}" scope="request" />
        <c:set var="message" scope="request"><spring:message code="businesses.no-results"/></c:set>
        <c:set var="textCallToAction" scope="request"><spring:message code="business.create"/></c:set>
        <jsp:include page="components/noResults.jsp"/>
    </c:if>
</div>
</body>
</html>


