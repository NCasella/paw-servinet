<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<html>
<head>
  <link href="${pageContext.request.contextPath}/css/global.css" rel="stylesheet" />
  <link href="${pageContext.request.contextPath}/css/pagination.css" rel="stylesheet" />
  <link href="https://fonts.googleapis.com/icon?family=Material+Icons" rel="stylesheet">
</head>
<body>

<!--------------------------------- PAGINATION ------------------------------------->


  <div class="page-manage-box">
    <c:choose>
      <c:when test="${page != 0}">
        <a class="page-text" href="${path}pagina=0"><spring:message code="pagination.first"/></a>
        <a class="page-text" href="${path}pagina=${page-1}"><spring:message code="pagination.previous"/></a>
      </c:when>
      <c:otherwise>
        <label class="none-page-text"><spring:message code="pagination.none-first"/></label>
        <label class="none-page-text"><spring:message code="pagination.none-previous"/></label>
      </c:otherwise>
    </c:choose>


    <div class="page-nums">
      <c:choose>
        <c:when test="${pageCount < 6}">
          <c:forEach var="i" begin="0" end="${pageCount-1}">
            <a class="none-decoration" href="${path}pagina=${i}">
              <c:choose>
                <c:when test="${i == page}">
                  <label class="page-num selected-page">${i}</label>
                </c:when>
                <c:otherwise>
                  <label class="page-num none-selected-page">${i}</label>
                </c:otherwise>
              </c:choose>
            </a>
          </c:forEach>
        </c:when>
        <c:otherwise>
          <c:choose>
            <c:when test="${page < 3}">
              <c:forEach var="i" begin="0" end="3">
                <a class="none-decoration" href="${path}pagina=${i}">
                  <c:choose>
                    <c:when test="${i == page}">
                      <label class="page-num selected-page">${i}</label>
                    </c:when>
                    <c:otherwise>
                      <label class="page-num none-selected-page">${i}</label>
                    </c:otherwise>
                  </c:choose>
                </a>
              </c:forEach>
              <label class="none-selected-page">...</label>
              <a class="none-decoration" href="${path}pagina=${pageCount-1}"><label class="page-num none-selected-page"> <c:out value="${pageCount-1}"/> </label></a>
            </c:when>
            <c:when test="${page > pageCount-1-3}">
              <a class="none-decoration" href="${path}pagina=0"><label class="page-num none-selected-page"> 0 </label></a>
              <label class="none-selected-page">...</label>
              <c:forEach var="i" begin="${pageCount-1-3}" end="${pageCount-1}">
                <a class="none-decoration" href="${path}pagina=${i}">
                  <c:choose>
                    <c:when test="${i == page}">
                      <label class="page-num selected-page">${i}</label>
                    </c:when>
                    <c:otherwise>
                      <label class="page-num none-selected-page">${i}</label>
                    </c:otherwise>
                  </c:choose>
                </a>
              </c:forEach>
            </c:when>
            <c:otherwise>
              <a class="none-decoration" href="${path}pagina=0"><label class="page-num none-selected-page"> 0 </label></a>
              <label class="page-num none-selected-page"> ... </label>
              <a class="none-decoration" href="${path}pagina=${page-1}"><label class="page-num none-selected-page"> <c:out value="${page-1}"/> </label></a>
              <a class="none-decoration" href="${path}pagina=${page}"><label class="page-num selected-page"> <c:out value="${page}"/> </label></a>
              <a class="none-decoration" href="${path}pagina=${page+1}"><label class="page-num none-selected-page"> <c:out value="${page+1}"/> </label></a>
              <label class="page-num none-selected-page"> ... </label>
              <a class="none-decoration" href="${path}pagina=${pageCount-1}"><label class="page-num none-selected-page"> <c:out value="${pageCount-1}"/> </label></a>
            </c:otherwise>
          </c:choose>
        </c:otherwise>
      </c:choose>
    </div>


    <c:choose>
      <c:when test="${page < pageCount-1}">
        <a class="page-text" href="${path}pagina=${page+1}"><spring:message code="pagination.next"/></a>
      </c:when>
      <c:otherwise>
        <label class="none-page-text"><spring:message code="pagination.none-next"/></label>
      </c:otherwise>
    </c:choose>

    <c:choose>
      <c:when test="${page != pageCount-1}">
        <a class="page-text" href="${path}pagina=${pageCount-1}"><spring:message code="pagination.last"/></a>
      </c:when>
      <c:otherwise>
        <label class="none-page-text"><spring:message code="pagination.none-last"/></label>
      </c:otherwise>
    </c:choose>
  </div>


</body>
</html>
