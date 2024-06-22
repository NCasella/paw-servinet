<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<jsp:include page="navbar.jsp" />

<html>
<head>
    <link href="${pageContext.request.contextPath}/css/services.css" rel="stylesheet" />
    <link href="${pageContext.request.contextPath}/css/global.css" rel="stylesheet" />
    <link href="https://fonts.googleapis.com/icon?family=Material+Icons" rel="stylesheet">
    <title><spring:message code="title.services"/></title>
</head>
<body>
<c:set var="numParameters" value="0"/>
<c:url var="Path" value="/servicios/">
    <c:if test="${not empty param.categoria}"><c:param name="categoria" value="${param.categoria}" /> <c:set var="numParameters" value="${numParameters+1}"/></c:if>
    <c:if test="${not empty param.calificacion}"><c:param name="calificacion" value="${param.calificacion}" /><c:set var="numParameters" value="${numParameters+1}"/></c:if>
    <c:if test="${not empty param.query}"><c:param name="query" value="${param.query}" /><c:set var="numParameters" value="${numParameters+1}"/></c:if>
    <c:if test="${not empty paramValues.ubicacion}"><c:forEach var="ubicaciones" items="${paramValues.ubicacion}"><c:param name="ubicacion" value="${ubicaciones}"/> <c:set var="numParameters" value="${numParameters+1}"/></c:forEach></c:if>
</c:url>

<c:choose>
    <c:when test="${empty param or (numParameters == 0 and not empty param.pagina)}">
        <c:set var="filtersPath" value="${Path}?"/>
    </c:when>
    <c:otherwise>
        <c:set var="filtersPath" value="${Path}&"/>
    </c:otherwise>
</c:choose>


<div class="page">
    <div class="filters-info">
        <div class="flex">
            <form class="form-box" action="${pageContext.request.contextPath}/servicios" method="GET">
                <div class="search-container">
                    <input type="text" class="search-box" placeholder="<spring:message code="services.search-placeholder"/>" name="query" value="<c:out value="${param.query}"/>"/>
                    <button type="submit" class="search-button"><i class="material-icons">search</i></button>
                </div>
            </form>
        </div>


        <c:if test="${not empty paramValues.ubicacion or not empty paramValues.calificacion or not empty paramValues.categoria}">
            <h4><spring:message code="services.selected-filters"/></h4>
            <div class="filters-selected flex">
                <c:forEach items="${paramValues.ubicacion}" var="location">
                    <button class="filter-container">
                        <c:out value="${location}"/>
                        <c:url value="/servicios" var="locationRemove">
                            <c:if test="${not empty param.categoria}"><c:param name="categoria" value="${param.categoria}" /></c:if>
                            <c:if test="${not empty param.query}"><c:param name="query" value="${param.query}" /></c:if>
                            <c:forEach var="calificaciones" items="${paramValues.calificacion}"><c:param name="calificacion" value="${calificaciones}"/></c:forEach>
                            <c:forEach items="${paramValues.ubicacion}" var="paramUb">
                                <c:if test="${paramUb != location}">
                                    <c:param name="ubicacion" value="${paramUb}"/>
                                </c:if>
                            </c:forEach>
                        </c:url>
                        <a href="${locationRemove}"><i class="material-icons close-filter-icon">close</i></a>
                    </button>
                </c:forEach>
                <c:if test="${not empty param.calificacion}">
                    <button class="filter-container">
                        <c:forEach var="rating" items="${ratings}">
                            <c:if test="${rating.name == param.calificacion}">
                                <spring:message code="${rating.codeMsg}"/>
                            </c:if>
                        </c:forEach>
                        <c:url value="/servicios" var="rateRemove">
                            <c:if test="${not empty param.categoria}"><c:param name="categoria" value="${param.categoria}" /></c:if>
                            <c:if test="${not empty param.query}"><c:param name="query" value="${param.query}" /></c:if>
                            <c:forEach var="ubicaciones" items="${paramValues.ubicacion}"><c:param name="ubicacion" value="${ubicaciones}"/></c:forEach>
                        </c:url>
                        <a href="${rateRemove}"><i class="material-icons close-filter-icon">close</i></a>
                    </button>
                </c:if>
                <c:if test="${not empty param.categoria}">
                    <button class="filter-container">
                        <spring:message code="${category.codeMsg}"/>
                        <c:url value="/servicios" var="categoryRemove">
                            <c:if test="${not empty param.query}"><c:param name="query" value="${param.query}" /></c:if>
                            <c:forEach var="calificaciones" items="${paramValues.calificacion}"><c:param name="calificacion" value="${calificaciones}"/></c:forEach>
                            <c:forEach var="ubicaciones" items="${paramValues.ubicacion}"><c:param name="ubicacion" value="${ubicaciones}"/></c:forEach>
                        </c:url>
                        <a href="${categoryRemove}"><i class="material-icons close-filter-icon">close</i></a>
                    </button>
                </c:if>
            </div>
            <c:url value="/servicios" var="filtersRemove">
                <c:if test="${not empty param.categoria}"><c:param name="categoria" value="${param.categoria}" /></c:if>
                <c:if test="${not empty param.query}"><c:param name="query" value="${param.query}" /></c:if>
            </c:url>
            <a class="none-decoration" href="${filtersRemove}">
                <p class="remove-filters"><i class="material-icons">filter_alt_off</i><spring:message code="services.remove-all-filters"/></p>
            </a>
        </c:if>
        <p class="comment"> <spring:message code="services.search-results" arguments="${resultsAmount}"/></p>
    </div>

    <div class="info">
        <div class="content">
            <c:choose>
                <c:when test="${isServicesEmpty}">
                    <c:set var="urlCallToAction" value="${pageContext.request.contextPath}/" scope="request" />
                    <c:set var="textCallToAction" scope="request"><spring:message code="invalid.back-to-home"/></c:set>
                    <c:set var="message" scope="request"><spring:message code="services.no-search-results"/></c:set>
                    <jsp:include page="components/noResults.jsp"/>
                </c:when>
                <c:otherwise>
                    <div class="services-container">
                        <c:forEach items="${services}" var="item">
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
                </c:otherwise>
            </c:choose>
        </div>

        <div class="filters-column">
        <div class="filters-box">
            <h3><spring:message code="services.filter-rate"/></h3>
            <c:forEach items="${ratings}" var="rate">
                <c:url value="/servicios" var="ratingChange">
                    <c:if test="${not empty param.categoria}"><c:param name="categoria" value="${param.categoria}" /></c:if>
                    <c:if test="${not empty param.query}"><c:param name="query" value="${param.query}" /></c:if>
                    <c:forEach var="ubicaciones" items="${paramValues.ubicacion}"><c:param name="ubicacion" value="${ubicaciones}"/></c:forEach>
                    <c:param name="calificacion" value="${rate.name}"/>
                </c:url>
                <a class="none-decoration filter-text" href="${ratingChange}">
                    <spring:message code="${rate.codeMsg}"/>:
                    <c:forEach begin="1" end="${rate.minValue}">
                        <i class="material-icons stars">star</i>
                    </c:forEach>
                    <c:if test="${rate.minValue != 5}">
                        <spring:message code="rating.ormore"/>
                    </c:if>
                </a>
            </c:forEach>
            <h3><spring:message code="services.filter-category"/></h3>
            <c:forEach items="${categories}" var="categ">
                <c:url value="/servicios" var="categoryChange">
                    <c:if test="${not empty param.calificacion}"><c:param name="calificacion" value="${param.calificacion}"/></c:if>
                    <c:if test="${not empty param.query}"><c:param name="query" value="${param.query}" /></c:if>
                    <c:forEach var="ubicaciones" items="${paramValues.ubicacion}"><c:param name="ubicacion" value="${ubicaciones}"/></c:forEach>
                    <c:param name="categoria" value="${categ.value}"/>
                </c:url>
                <a class="none-decoration filter-text category-text" href="${categoryChange}">
                    <i class="material-icons category-icon">${categ.icon}</i>
                    <spring:message code="${categ.codeMsg}"/>
                </a>
            </c:forEach>
            <h3><spring:message code="services.filter-location"/></h3>
            <c:forEach items="${availableNb}" var="neighbourhood">
                <c:url value="/servicios" var="locationChange">
                    <c:if test="${not empty param.categoria}"><c:param name="categoria" value="${param.categoria}" /></c:if>
                    <c:if test="${not empty param.query}"><c:param name="query" value="${param.query}" /></c:if>
                    <c:if test="${not empty param.calificacion}"><c:param name="calificacion" value="${param.calificacion}"/></c:if>
                    <c:forEach var="ubicaciones" items="${paramValues.ubicacion}"><c:if test="${ubicaciones != neighbourhood}"><c:param name="ubicacion" value="${ubicaciones}"/></c:if> </c:forEach>
                    <c:param name="ubicacion" value="${neighbourhood}"/>
                </c:url>
                <a class="none-decoration filter-text" href="${locationChange}"><c:out value="${neighbourhood}"/></a>
            </c:forEach>
        </div>
        </div>

    </div>


    <!--------------------------------- PAGINATION ------------------------------------->

    <c:if test="${!isServicesEmpty}">
        <c:set var="page" value="${page}" scope="request" />
        <c:set var="pageCount" value="${pageCount}" scope="request" />
        <c:set var="path" value="${filtersPath}" scope="request" />
        <jsp:include page="components/pagination.jsp"/>
    </c:if>

</div>

</body>
</html>
