<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<jsp:include page="navbar.jsp" />
<html>
<head>
  <link href="${pageContext.request.contextPath}/css/profile.css" rel="stylesheet" />
  <link href="${pageContext.request.contextPath}/css/global.css" rel="stylesheet" />
  <link href="https://fonts.googleapis.com/icon?family=Material+Icons" rel="stylesheet">
  <title><spring:message code="title.business" arguments="${fn:escapeXml(business.businessName)}"/></title>
</head>
<body>

<c:url value="/borrar-negocio/${businessId}" var="deleteUrl"/>

<c:set var="title" scope="request"><spring:message code="popup.business.title"/></c:set>
<c:set var="message" scope="request"><spring:message code="popup.business.message"/></c:set>
<c:set var="action" scope="request"><spring:message code="popup.delete"/></c:set>
<c:set var="method" value="post" scope="request"/>
<c:set var="url" value="${deleteUrl}" scope="request"/>

<jsp:include page="components/popUp.jsp" />
<c:set var="statisticsTitle"><spring:message code="statistics.title"/></c:set>
<c:set var="urlCreateService" value="${pageContext.request.contextPath}/crear-servicio/${businessId}" />
<div class="page">
  <jsp:include page="./components/backButton.jsp" />
  <div class="header">
    <h2><c:out value="${business.businessName}"/></h2>
    <spring:message code="service.unrated" var="noRatings"/>
    <c:if test="${!isOwner}">
      <div class="align-right reviews-box">
        <c:if test="${avgRating > 0}">
          <a href="${pageContext.request.contextPath}/negocio/opiniones/${businessId}">
            <button class="reviews-btn"><spring:message code="review.see"/></button>
          </a>
        </c:if>
        <p class="business-rating">${avgRating > 0? avgRating:noRatings}<i class="material-icons business-star">star</i></p>
      </div>
    </c:if>
    <c:if test="${isOwner}">
      <div class="flex center-vertically">
        <button class="cancelBtn" id="deleteBtn" onclick="showPopUp()" >
          <i class="material-icons ">delete</i>
        </button>
        <a href="${pageContext.request.contextPath}/negocio/${businessId}/estadisticas">
          <button class="btn-basic rounded-btn"><i class="material-icons icon" title="${statisticsTitle}">equalizer</i></button></a>
        <a href="${urlCreateService}" class="none-decoration mr">
          <button class="btn center-vertically"><i class="material-icons ">add</i> <spring:message code="business.add-service"/></button>
        </a>
        <c:if test="${not empty serviceList}" >
          <a href="${pageContext.request.contextPath}/negocio/${businessId}/turnos?confirmados=false" class="none-decoration">
            <button class="btn center-vertically"><i class="material-icons ">calendar_today</i> <spring:message code="business.appointments"/></button>
          <c:if test="${countAppointments > 0}">
            <div class="pending">
              <c:out value="${countAppointments}"/>
            </div>
          </c:if>
          </a>
        </c:if>
      </div>
    </c:if>
  </div>

  <c:if test="${isOwner}">
    <div class="reviews-box">
      <p class="owner-business-rating">${avgRating > 0? avgRating:noRatings}<i class="material-icons owner-business-star">star</i></p>
      <c:if test="${avgRating > 0}">
      <a href="${pageContext.request.contextPath}/negocio/opiniones/${businessId}">
        <button class="reviews-btn"><spring:message code="review.see"/></button>
      </a>
      </c:if>
    </div>
  </c:if>

  <div class="business-info box">
    <div class="business-info-container">
      <div class="flex">
        <h3><spring:message code="business.contact"/></h3>
        <c:if test="${isOwner}">
          <div class="align-right">
            <button class="edit-btn" onclick="editBusiness()" id="toggle-btn"><spring:message code="business.edit"/></button>
          </div>
        </c:if>
      </div>
      <div id="business-info">
        <p class="business-info-text"><i class="material-icons business-icon">mail</i><c:out value="${business.email}"/></p>
        <p class="business-info-text"><i class="material-icons business-icon">call</i> <c:out value="${business.telephone}"/></p>
        <c:if test="${not empty business.location }"><p class="business-info-text"><i class="material-icons business-icon">location_on</i><c:out value="${business.location}"/></p></c:if>
      </div>
      <c:if test="${isOwner}">
        <c:url value="/${businessId}/editar-negocio" var="editBusiness"/>
        <form:form action="${editBusiness}" method="post" modelAttribute="BusinessForm">
          <div id="edit-business" class="transparent">
            <form:input path="businessName" type="text" value="${business.name}" class="transparent"/>
            <div>
              <div class="flex input-box">
                <i class="material-icons business-icon">mail</i>
                <form:input path="businessEmail" type="text" value="${business.email}" class="input business-input"/>
              </div>
              <div class="flex input-box">
                <i class="material-icons business-icon">call</i>
                <form:input path="businessTelephone" type="text" value="${business.telephone}" class="input business-input"/>
              </div>
              <div class="flex input-box">
                <i class="material-icons business-icon">location_on</i>
                <form:input path="businessLocation" type="text" value="${business.location}" class="input business-input"/>
              </div>
              <div class="align-center">
                <button type="submit" class="btn edit-business-btn"><spring:message code="business.save-changes"/></button>
              </div>
            </div>
          </div>
          <div class="errors">
            <p><form:errors path="businessEmail" cssClass="error"/></p>
            <p><form:errors path="businessTelephone" cssClass="error"/></p>
            <p><form:errors path="businessLocation" cssClass="error"/></p>
          </div>
        </form:form>
      </c:if>
    </div>
  </div>

  <h3><spring:message code="business.services"/></h3>
  <div class="boxes-container">
    <c:forEach items="${serviceList}" var="service" varStatus="loop">
      <a class="none-decoration" href="${pageContext.request.contextPath}/servicio/${service.id}">
        <div class="box preview-box">
          <img class="preview-box-img" src="${pageContext.request.contextPath}/images/${service.imageId}" alt=<spring:message code="business.service-image"/>>
          <p class="preview-box-text"><c:out value="${service.name}"/></p>
        </div>
      </a>
    </c:forEach>
    <c:if test="${ empty serviceList}">
      <c:set var="urlCallToAction" value="${urlCreateService}" scope="request" />
      <c:set var="message" scope="request"><spring:message code="business.not-found"/></c:set>
      <c:set var="textCallToAction" scope="request">+ <spring:message code="business.add-service"/></c:set>
      <jsp:include page="components/noResults.jsp"/>
    </c:if>
  </div>
</div>
</body>

<script>
  var cancelText = "<spring:message code='service.cancel'/>";
  var editText = "<spring:message code='business.edit'/>";

  window.onload = getPreviousPageInfo(document.referrer);

  function showPopUp() {
    document.getElementById("popup").style.display = "block";
  }

  function editBusiness() {
    var showInfo = document.getElementById('business-info');
    var showEdit = document.getElementById('edit-business');
    var toggleButton = document.getElementById('toggle-btn');

    if (showInfo.classList.contains('transparent')) {
      showInfo.classList.remove('transparent');
      showEdit.classList.add('transparent');
      toggleButton.textContent = editText;
    } else {
      showInfo.classList.add('transparent');
      showEdit.classList.remove('transparent');
      toggleButton.textContent = cancelText;
    }
  }
</script>
</html>
