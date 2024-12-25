<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<html>
<head>
    <link rel="shortcut icon" type="image/x-icon" href="<c:url value='/resources/logo.png'/>">
    <link href="${pageContext.request.contextPath}/css/navbar.css" rel="stylesheet" />
    <link href="${pageContext.request.contextPath}/css/global.css" rel="stylesheet" />
    <link rel="stylesheet" href="https://fonts.googleapis.com/css2?family=Poppins:wght@300;400;500;600;700&display=swap">
</head>
<body>
    <div class="nav-bar page">
        <a href="${pageContext.request.contextPath}/">
            <div class="logo-img-container">
                <img class="img" src="${pageContext.request.contextPath}/resources/servinet.png" alt="<spring:message code="servinet.logo"/>">
            </div>
        </a>
        <div class="nav-items">
            <a href="${pageContext.request.contextPath}/servicios" class="none-decoration">
                <p class="nav-item"><spring:message code="navbar.all-services"/></p>
            </a>

            <div class="dropdown">
            <c:choose>
                <c:when test="${isLoggedIn}">
                    <p class="nav-item"><spring:message code="navbar.account"/></p>
                    <div class="dropdown-content">
                        <a href="${pageContext.request.contextPath}/perfil"><spring:message code="navbar.profile"/></a>
                        <a href="${pageContext.request.contextPath}/turnos?confirmados=true"><spring:message code="navbar.appointments"/></a>
                        <c:choose>
                            <c:when test="${isProvider}">
                                <a href="${pageContext.request.contextPath}/negocios"><spring:message code="navbar.businesses"/></a>
                            </c:when>
                            <c:otherwise>
                                <a href="${pageContext.request.contextPath}/registrar-negocio"><spring:message code="navbar.create-businesses"/></a>
                            </c:otherwise>
                        </c:choose>
                        <a href="${pageContext.request.contextPath}/logout"><spring:message code="profile.logout"/></a>
                    </div>
                </c:when>
                <c:otherwise>
                    <p class="nav-item">
                        <a href="${pageContext.request.contextPath}/login">
                            <button class="navbar-btn"><spring:message code="login"/></button>
                        </a>
                    </p>
                </c:otherwise>
            </c:choose>
            </div>
        </div>
    </div>
</body>
</html>


