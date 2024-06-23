<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<jsp:include page="navbar.jsp" />
<html>
<head>
    <link href="${pageContext.request.contextPath}/css/home.css" rel="stylesheet" />
    <link href="${pageContext.request.contextPath}/css/global.css" rel="stylesheet" />
    <link href="${pageContext.request.contextPath}/css/services.css" rel="stylesheet" />
    <link href="https://fonts.googleapis.com/icon?family=Material+Icons" rel="stylesheet">
    <title><spring:message code="title.servinet"/></title>
</head>
<body>
<div class="page">
    <div class="header">
        <h2 class="slogan"><spring:message code="servinet.slogan"/></h2>
        <div class="align-center">
            <form class="form-box" action="${pageContext.request.contextPath}/servicios" method="GET">
                <div class="search-container">
                    <input type="text" class="search-box" placeholder="<spring:message code="services.search-placeholder"/>" name="query" value="<c:out value="${param.query}"/>"/>
                    <button type="submit" class="search-button"><i class="material-icons">search</i></button>
                </div>
            </form>
        </div>
    </div>
    <h3><spring:message code="home.search-by-category"/></h3>
    <div class="boxes-container" id="someCategories">
        <c:forEach items="${categories}" var="category" varStatus="loop">
            <c:if test="${loop.index < 5}">
                <a class="none-decoration" href="${pageContext.request.contextPath}/servicios/?categoria=${category.value}">
                    <div class="box">
                        <h4 class="box-text"><spring:message code="${category.codeMsg}"/></h4>
                        <i class="material-icons icon">${category.icon}</i>
                    </div>
                </a>
            </c:if>
        </c:forEach>
    </div>

    <div class="boxes-container boxes-container-wrap hide" id="allCateogries">
        <c:forEach items="${categories}" var="category" varStatus="loop">
                <a class="none-decoration" href="${pageContext.request.contextPath}/servicios/?categoria=${category.value}">
                    <div class="box">
                        <h4 class="box-text"><spring:message code="${category.codeMsg}"/></h4>
                        <i class="material-icons icon">${category.icon}</i>
                    </div>
                </a>
        </c:forEach>
    </div>

    <div class="show-btn-container">
        <button class="show-btn" id="toggleCategoryButton" onclick="toggleCategory()"><spring:message code="home.show-more"/></button>
    </div>

    <h3 class="container-text"><spring:message code="home.search-by-closeness"/></h3>
    <div class="boxes-container" id="someLocations">
        <c:forEach items="${neighbourhoods}" var="neighbourhood" varStatus="loop">
            <c:if test="${loop.index < 5}">
                <a class="none-decoration" href="${pageContext.request.contextPath}/servicios/?ubicacion=${neighbourhood.value}">
                    <div class="box location-box">
                        <h4 class="box-text"><c:out value="${neighbourhood.value}"/></h4>
                    </div>
                </a>
            </c:if>
        </c:forEach>
    </div>

    <div class="boxes-container boxes-container-wrap hide" id="allLocations">
        <c:forEach items="${neighbourhoods}" var="neighbourhood" varStatus="loop">
                <a class="none-decoration" href="${pageContext.request.contextPath}/servicios/?ubicacion=${neighbourhood.value}">
                    <div class="box location-box">
                        <h4 class="box-text"><c:out value="${neighbourhood.value}"/></h4>
                    </div>
                </a>
        </c:forEach>
    </div>

    <div class="show-btn-container">
        <button class="show-btn" id="toggleLocationButton" onclick="toggleLocation()"><spring:message code="home.show-more"/></button>
    </div>

    <div class="recommended-services-containers">
        <h3 class="container-text"><spring:message code="home.recommended-services"/></h3>
        <c:forEach items="${recommendedServices}" var="item">
            <div class="service-box">
                <a class="service-text" href="${pageContext.request.contextPath}/servicio/${item.id}">
                    <div class="service-data-container">
                        <div class="service-img-container">
                            <img class="img service-img" src="${pageContext.request.contextPath}/images/${item.imageId}" alt="<spring:message code="service.image"/>">
                        </div>
                        <div class="service-info">
                            <div class="service-header">
                                <c:set var="categoryCodeMsg" value="${item.category.codeMsg}"/>
                                <p class="item comment"><spring:message code="${categoryCodeMsg}"/></p>
                                <p class="align-right">$
                                    <c:choose>
                                    <c:when test="${item.pricing.value == TBDPricing}">
                                <p class="TBD-comment"><spring:message code="pricing.tbd"/></p>
                                </c:when>
                                <c:otherwise>
                                    <c:out value="${item.price}"/>
                                </c:otherwise>
                                </c:choose>
                                </p>
                            </div>
                            <div class="service-header">
                                <h3 class="service-title"> <c:out value="${item.name}"/></h3>
                                <c:set value="${item.ratingAvg}" var="ratingAvg"/>
                                <spring:message code="service.unrated" var="noRatings"/>
                                <p class="align-right">${ratingAvg > 0? ratingAvg:noRatings}<i class="material-icons yellow-star">star</i></p>
                            </div>
                            <p class="item"> <i class="material-icons">location_on</i>
                                <c:if test="${not empty item.location}">
                                    <c:out value="${item.location}"/>,
                                </c:if>
                                <c:forEach items="${item.neighbourhoodAvailable}" var="neighbour">
                                    <c:out value=" ${neighbour}"/>
                                </c:forEach>
                            </p>
                        </div>
                    </div>
                </a>
            </div>
        </c:forEach>
    </div>

    <div class="btn-container">
        <button class="btn">
            <a href="${pageContext.request.contextPath}/servicios" class="none-decoration btn-text"><spring:message code="home.view-all-services"/></a>
        </button>
    </div>
</div>


<script>
    var showMoreText = "<spring:message code='home.show-more'/>";
    var showLessText = "<spring:message code='home.show-less'/>";

    function toggleCategory() {
        var someCategories = document.getElementById('someCategories');
        var allCateogries = document.getElementById('allCateogries');
        var toggleButton = document.getElementById('toggleCategoryButton');

        if (someCategories.classList.contains('hide')) {
            someCategories.classList.remove('hide');
            allCateogries.classList.add('hide');
            toggleButton.textContent = showMoreText;
        } else {
            someCategories.classList.add('hide');
            allCateogries.classList.remove('hide');
            toggleButton.textContent = showLessText;

        }
    }

    function toggleLocation() {
        var someLocations = document.getElementById('someLocations');
        var allLocations = document.getElementById('allLocations');
        var toggleButton = document.getElementById('toggleLocationButton');

        if (someLocations.classList.contains('hide')) {
            someLocations.classList.remove('hide');
            allLocations.classList.add('hide');
            toggleButton.textContent = showMoreText;
        } else {
            someLocations.classList.add('hide');
            allLocations.classList.remove('hide');
            toggleButton.textContent = showLessText;

        }
    }

</script>
</body>
</html>


