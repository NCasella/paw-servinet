<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<jsp:include page="navbar.jsp" />
<html>
<head>
    <link href="${pageContext.request.contextPath}/css/reviews.css" rel="stylesheet" />
    <link href="${pageContext.request.contextPath}/css/global.css" rel="stylesheet" />
    <link href="https://fonts.googleapis.com/icon?family=Material+Icons" rel="stylesheet">
    <link rel="stylesheet" href="https://fonts.googleapis.com/css2?family=Poppins:wght@300;400;500;600;700&display=swap">
    <title><spring:message code="title.businessReviews" arguments="${business.name}"/></title>
</head>
<body>
<div class="page reviews-page">
    <h2><spring:message code="review.business" arguments="${business.name}"/></h2>

    <c:if test="${!empty reviews}">
        <c:set var="rating" value="${avgRating}" scope="request"/>
        <c:set var="allRatingCount" value="${ratingsCount}" scope="request"/>
        <c:set var="ratingCountList" value="${ratingCountList}" scope="request"/>
        <jsp:include page="./components/reviews.jsp" />
    </c:if>

    <h3 class="recent-reviews-title"><spring:message code="reviews.recent"/></h3>

    <c:forEach items="${reviews}" var="review">
        <c:set value="${review.rating}" var="rate"/>
        <div class="review-box">
            <div class="review-content">
            <p class="user-comment"><spring:message code="review.user" arguments="${review.user.username}"/>
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
        </div>
    </c:forEach>

    <c:if test="${ empty reviews}">
        <c:url var="urlGoBack" value="/negocio/${business.businessid}"/>
        <c:set var="urlCallToAction" value="${urlGoBack}" scope="request" />
        <c:set var="message" scope="request"><spring:message code="reviews.not-found" arguments="${business.name}"/></c:set>
        <c:set var="textCallToAction" scope="request"><spring:message code="business.go"/></c:set>
        <jsp:include page="components/noResults.jsp"/>
    </c:if>
</div>
</body>
</html>


