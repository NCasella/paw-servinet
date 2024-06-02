<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<jsp:include page="navbar.jsp" />
<html>
<head>
    <link href="${pageContext.request.contextPath}/css/businessReviews.css" rel="stylesheet" />
    <link href="${pageContext.request.contextPath}/css/global.css" rel="stylesheet" />
    <link href="https://fonts.googleapis.com/icon?family=Material+Icons" rel="stylesheet">
    <link rel="stylesheet" href="https://fonts.googleapis.com/css2?family=Poppins:wght@300;400;500;600;700&display=swap">
    <title><spring:message code="title.businessReviews" arguments="${business.name}"/></title>
</head>
<body>
<div class="page">
    <h2><spring:message code="review.business" arguments="${business.name}"/></h2>

    <c:forEach items="${reviews}" var="review">
        <c:set value="${review.rating}" var="rate"/>
        <div class="review-box">
            <p class="comment"><spring:message code="review.user" arguments="${review.user.username}"/>
                <a href="${pageContext.request.contextPath}/servicio/${review.serviceid}" class="none-decoration">
                    <span class="review-service">${review.service.name}</span>
                </a>
            </p>
            <div class="flex">
                <div class="stars-container">
                    <c:forEach begin="1" end="${rate}" var="i">
                        <i class="material-icons yellow-star">star</i>
                    </c:forEach>
                    <c:forEach begin="${rate+1}" end="5" var="i">
                        <i class="material-icons gray-star">star</i>
                    </c:forEach>
                </div>
                <p class="date"><c:out value="${review.date}"/></p>
            </div>
            <p class="text"><c:out value="${review.comment}"/></p>
        </div>
    </c:forEach>
</div>
</body>
</html>


