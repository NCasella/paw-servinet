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
    <jsp:include page="./components/backButton.jsp" />
    <h2><spring:message code="review.business" arguments="${business.name}"/></h2>

    <c:if test="${!empty reviews}">
        <c:set var="rating" value="${business.businessRatingAvg}" scope="request"/>
        <c:set var="allRatingCount" value="${business.businessRatingCount}" scope="request"/>
        <c:set var="ratingCountList" value="${ratingCountList}" scope="request"/>
        <jsp:include page="./components/reviews.jsp" />
    </c:if>

    <div class="align-right">
        <h3 class="recent-reviews-title">
            <c:choose>
                <c:when test="${currentFilter == null}">
                    <spring:message code="reviews.recent"/>
                </c:when>
                <c:otherwise>
                    <spring:message code="${currentFilter.codeMsg}" var="currentFilterMsg"/>
                    <spring:message code="reviews.title" arguments="${currentFilterMsg}"/>
                </c:otherwise>
            </c:choose>
        </h3>

        <label class="align-right review-filter-container">
            <select class="reviews-filter" onChange="window.location.href=this.value">
                <option class="disabled-option" value="" disabled selected><spring:message code="reviews.order-by"/></option>
                <c:forEach items="${availableFilters}" var="filter">
                    <c:url value="/negocio/opiniones/${business.businessid}/" var="filterChange">
                        <c:param name="rwFilter" value="${filter.filter}"/>
                    </c:url>
                    <option class="review-option" value="${filterChange}"><spring:message code="${filter.codeMsg}"/></option>
                </c:forEach>
            </select>
        </label>
    </div>

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

    <c:url value="/negocio/opiniones/${business.businessid}/" var="filtersPath">
        <c:if test="${currentFilter != null}"> <c:param name="rwFilter" value="${currentFilter.filter}"/> </c:if>
    </c:url>

    <c:choose>
        <c:when test="${currentFilter == null}">
            <c:set value="${filtersPath}?" var="paginationFiltersPath"/>
        </c:when>
        <c:otherwise>
            <c:set value="${filtersPath}&" var="paginationFiltersPath"/>
        </c:otherwise>
    </c:choose>

    <div class="align-center">
            <c:choose>
                <c:when test="${page > 1}">
                    <a class="none-decoration" href="${paginationFiltersPath}pagina=${page-1}">
                        <p class="page-text"><spring:message code="pagination.previous"/></p>
                    </a>
                </c:when>
                <c:otherwise>
                    <p class="none-page-text"><spring:message code="pagination.none-previous"/></p>
                </c:otherwise>
            </c:choose>
            <c:choose>
                <c:when test="${pageCount > page}">
                    <a class="none-decoration" href="${paginationFiltersPath}pagina=${page+1}">
                        <p class="page-text"><spring:message code="pagination.next"/></p>
                    </a>
                </c:when>
                <c:otherwise>
                    <p class="none-page-text"><spring:message code="pagination.none-next"/></p>
                </c:otherwise>
            </c:choose>
    </div>

    <c:if test="${ empty reviews}">
        <c:url var="urlGoBack" value="/negocio/${business.businessid}"/>
        <c:set var="urlCallToAction" value="${urlGoBack}" scope="request" />
        <c:set var="message" scope="request"><spring:message code="reviews.not-found" arguments="${business.name}"/></c:set>
        <c:set var="textCallToAction" scope="request"><spring:message code="business.go"/></c:set>
        <jsp:include page="components/noResults.jsp"/>
    </c:if>

    <script>
        window.onload = getPreviousPageInfo(document.referrer);
    </script>
</div>
</body>
</html>


