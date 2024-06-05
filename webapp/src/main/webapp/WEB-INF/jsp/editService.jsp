<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<jsp:include page="navbar.jsp" />
<html>
<head>
    <title><spring:message code="title.edit-service" arguments="${service.name}"/></title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/global.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/post.css">
</head>
<body>
<c:url value="/editar-servicio/${service.id}" var="editUrl"/>
<div class="page">
    <jsp:include page="./components/backButton.jsp" />

    <div class="postForm">
        <form:form action="${editUrl}" modelAttribute="editServiceForm" method="post" class="form">
        <h2 class="form-title highlight-text"><spring:message code="service.edit-service" arguments="${fn:escapeXml(service.name)}"/></h2>
        <label>
            <p class="label"><spring:message code="service.description"/></p>
            <spring:message code="input.business" var="inputName"/>
            <form:input type="text" class="input" path="description" value="${service.description}" />
            <form:errors path="description" cssClass="error" element="p"/>
        </label>

        <label>
            <p class="label"><spring:message code="service.price"/></p>
            <label>
                <form:checkbox id="additionalCharges" path="additionalCharges" value="${service.additionalCharges}" />
                <form:errors path="additionalCharges" cssClass="error"/>
                <label for="additionalCharges"><spring:message code="service.additionalCharges"/></label>
            </label>

            <div class="service-div">
                <form:select path="pricingtype" class="input" id="priceType" value="${service.pricing}">
                    <c:forEach items="${pricingTypes}" var="item">
                        <form:option  value="${item}">${item.value}</form:option>
                    </c:forEach>
                </form:select>
                <form:input class="input" path="price" value="${service.price}" />
            </div>
            <form:errors path="price" cssClass="error" element="p"/>
        </label>

        <label>
        <p class="label"><spring:message code="service.duration"/></p>
        <spring:message code="input.service.duration" var="serviceDuration"/>
        <div class="btn-group" role="group" aria-label="Basic example">
            <c:forEach var="durType" items="${durationTypes}" varStatus="loop">
                <span class="r-pills">
                    <form:radiobutton hidden="hidden" path="minimalduration" value="${durType.value}"/>
                    <label for="minimalduration${loop.index+1}"><spring:message code="${durType.codeMsg}"/>
                    </label>
                </span>
            </c:forEach>
        </div>
            <form:errors path="minimalduration" cssClass="error"/>
        </label>

        <div class="align-center">
            <input type="submit" value="<spring:message code="service.save-changes"/>" class="btn submit-btn">
        </div>
        </form:form>
    </div>

    <script>
        window.onload = getPreviousPageInfo(document.referrer);
    </script>
</body>
</html>
