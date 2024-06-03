<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<html>
<head>
    <link href="${pageContext.request.contextPath}/css/reviews.css" rel="stylesheet" />
    <link href="${pageContext.request.contextPath}/css/global.css" rel="stylesheet" />
    <link href="https://fonts.googleapis.com/icon?family=Material+Icons" rel="stylesheet">
    <link rel="stylesheet" href="https://fonts.googleapis.com/css2?family=Poppins:wght@300;400;500;600;700&display=swap">
    <title></title>
</head>
<body>
    <div class="flex">
        <div class="rating-counts">
            <c:forEach items="${ratingCountList}" var="count">
                <div class="progress-box">
                    <label class="progress-rate">${count.key}</label>
                    <progress id="file" max="100" value="${100*count.value/allRatingCount}"></progress>
                </div>
            </c:forEach>
        </div>
        <div class="ratingAvg-info">
            <h1 class="ratingAvg">${rating}</h1>
            <c:forEach begin="1" end="${rating}" var="i">
                <i class="material-icons yellow-star">star</i>
            </c:forEach>
            <c:forEach begin="${rating+1}" end="5" var="i">
                <i class="material-icons gray-star">star</i>
            </c:forEach>
            <p class="ratingAvg"><spring:message code="reviews.opinions-amount" arguments="${allRatingCount}"/></p>
        </div>
    </div>
</body>

