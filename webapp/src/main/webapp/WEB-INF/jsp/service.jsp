<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<jsp:include page="navbar.jsp" />
<html>
<head>
    <link href="${pageContext.request.contextPath}/css/service.css" rel="stylesheet" />
    <link href="${pageContext.request.contextPath}/css/reviews.css" rel="stylesheet" />
    <link href="${pageContext.request.contextPath}/css/global.css" rel="stylesheet" />
    <link href="https://fonts.googleapis.com/icon?family=Material+Icons" rel="stylesheet">
    <title><spring:message code="title.service" arguments="${service.name}"/> </title>
</head>
<c:url value="/contratar-servicio/${serviceId}" var="contratarUrl"/>
<body>

    <c:url value="/borrar-servicio/${serviceId}" var="deleteUrl"/>

    <c:set var="title" scope="request"><spring:message code="popup.service.title"/></c:set>
    <c:set var="message" scope="request"><spring:message code="popup.service.message"/></c:set>
    <c:set var="action" scope="request"><spring:message code="popup.delete"/></c:set>
    <c:set var="method" value="post" scope="request"/>
    <c:set var="url" value="${deleteUrl}" scope="request"/>
    <jsp:include page="components/popUp.jsp" />

    <div class="page">

        <c:if test="${option!=null}">
            <a href="${pageContext.request.contextPath}/servicio/${serviceId}" class="none-decoration">
                <label class="go-back">
                    <i class="material-icons icon">arrow_back</i>
                    <spring:message code="service.back" arguments="${service.name}"/>
                </label>
            </a>
        </c:if>

        <c:if test="${option==null}">
        <div class="header">
            <h2><c:out value="${service.name}"/></h2>
            <c:if test="${isOwner}">
                <div>
                    <a href="${pageContext.request.contextPath}/editar-servicio/${service.id}/" class="none-decoration">
                        <button class="editBtn"><spring:message code="service.edit"/></button>
                    </a>
                    <button class="cancelBtn" onclick="showPopUp()"><spring:message code="service.delete"/></button>
                </div>
            </c:if>
        </div>
        <div class="info-container">
            <div class="img-container">
                <img class="service-img img" src="${pageContext.request.contextPath}/images/${service.imageId}" alt="<spring:message code="service.image"/>">
            </div>
            <div class="info-box">
                <div class="info-text">
                    <h3 class="no-padding-or-margin"><spring:message code="service.details"/></h3>
                    <div class="flex">
                        <p class="category-text"><spring:message code="${service.category.codeMsg}"/></p>
                        <spring:message code="service.unrated" var="unrated"/>
                        <div class="align-right service-rate">
                            <p>${avgRating > 0? avgRating:unrated}</p>
                            <i class="material-icons yellow-star service-rate-star">star</i>
                        </div>
                    </div>
                    <p class="text-with-icon no-top-margin"> <i class="material-icons icon">location_on</i>
                        <c:forEach var="neighbour" items="${service.neighbourhoodAvailable}">
                            <c:out value="${neighbour}"/>
                        </c:forEach>
                    </p>
                    <p class="text-with-icon"> <i class="material-icons icon">house</i>
                        <c:choose>
                            <c:when test="${service.homeService}">
                                <spring:message code="service.at-home"/>
                            </c:when>
                            <c:otherwise>
                                <c:if test="${not empty service.location}">
                                    <c:out value="${service.location}"/>
                                </c:if>
                            </c:otherwise>
                        </c:choose>
                    </p>
                    <p class="text-with-icon"> <i class="material-icons icon">schedule</i> <spring:message code="service.time" arguments="${service.duration}"/> </p>
                    <p class="text-with-icon"><i class="material-icons icon">attach_money</i>
                        <c:choose>
                            <c:when test="${service.pricing.value == TBDPricing}">
                                <label class="TBD-comment"><spring:message code="${service.pricing.codeMsg}"/></label>
                            </c:when>
                            <c:otherwise>
                                <c:out value="${service.price}"/>
                                <label class="comment"><spring:message code="${service.pricing.codeMsg}"/></label>
                            </c:otherwise>
                        </c:choose>
                    </p>
                    <c:if test="${service.additionalCharges}">
                        <p class="text-with-icon warning-text"><i class="material-icons icon">warning</i><spring:message code="service.additional-costs"/></p>
                    </c:if>

                    <c:url value="/negocio/${service.businessid}" var="businessUrl"/>
                    <a class="none-decoration" href="${businessUrl}">
                        <p class="view-business"><i class="material-icons icon">storefront</i><c:out value="${business.name}"/></p>
                    </a>

                    <c:if test="${!isOwner}">
                        <div class="btn-container no-top-margin">
                            <a href="${contratarUrl}" class="none-decoration">
                                <button class="btn btn-text">
                                    <spring:message code="service.new-appointment"/>
                                </button>
                            </a>
                        </div>
                    </c:if>
                </div>
            </div>
        </div>

        <div class="description-box">
            <div class="text-description">
                <p><c:out value="${service.description}"/></p>
            </div>
        </div>
        </c:if>

        <!--form action="${deleteUrl}" method="post">
            <input type="submit" value="Borrar servicio" class="deleteBtn"/>
        </form-->

        <c:if test="${option==null}">
            <div class="align-center">
                <div class="switch-btn">
                    <button class="btn-basic btn-left btn-selected" id="toggleQuestionsButton" onclick="toggleQuestions()"><spring:message code="service.questions"/></button>
                    <button class="btn-basic btn-right" id="toggleReviewsButton" onclick="toggleReviews()"><spring:message code="service.reviews"/></button>
                </div>
            </div>
        </c:if>

        <div class="rq-container">

            <c:if test="${option!='rw'}">
            <div class="rq-info" id="questions">
                <h3><spring:message code="service.q&r"/></h3>
                <c:url value="/preguntar/${serviceId}" var="askUrl"/>
                <form:form action="${askUrl}" method="post" modelAttribute="questionForm">
                    <div class="flex">
                        <spring:message code="input.service.question" var="questionPlaceholder"/>
                        <form:textarea path="question" class="input" placeholder="${questionPlaceholder}"/>
                        <input type="submit" value="<spring:message code="service.send"/>" class="send-btn">
                    </div>
                    <form:errors path="question" element="p" cssClass="error"/>
                </form:form>

                <c:choose>
                    <c:when test="${empty questions}">
                        <c:choose>
                            <c:when test="${isOwner}">
                                <p class="comment"><spring:message code="service.no-questions"/></p>
                            </c:when>
                            <c:otherwise>
                                <p class="comment"><spring:message code="service.first-to-ask"/></p>
                            </c:otherwise>
                        </c:choose>
                    </c:when>
                    <c:otherwise>
                        <h4><spring:message code="service.last-questions"/></h4>
                        <c:forEach items="${questions}" var="qst">
                            <div class="question-box">
                                <div class="qst-text">
                                    <label class="text"><c:out value="${qst.question}"/></label>
                                    <label class="date"><c:out value="${qst.date}"/></label>
                                </div>
                                <c:if test="${qst.response != null}">
                                    <div class="response-container">
                                        <i class="material-icons">subdirectory_arrow_right</i>
                                        <p class="text"><c:out value="${qst.response}"/></p>
                                    </div>
                                </c:if>
                            </div>
                        </c:forEach>
                    </c:otherwise>
                </c:choose>

                <div class="align-center">
                    <c:choose>
                        <c:when test="${option==null}">
                            <c:if test="${questionsCount > questionPage*10}">
                            <a class="none-decoration" href="${pageContext.request.contextPath}/servicio/${serviceId}/?opcion=qst">
                                <p class="page-text"><spring:message code="home.show-more"/></p>
                            </a>
                            </c:if>
                        </c:when>
                        <c:otherwise>
                            <c:if test="${questionPage > 1 || questionsCount > questionPage*10}">
                            <c:choose>
                                <c:when test="${questionPage > 1}">
                                    <a class="none-decoration" href="${pageContext.request.contextPath}/servicio/${serviceId}/?opcion=qst&qstPag=${questionPage-1}">
                                        <p class="page-text"><spring:message code="pagination.previous"/></p>
                                    </a>
                                </c:when>
                                <c:otherwise>
                                    <p class="none-page-text"><spring:message code="pagination.none-previous"/></p>
                                </c:otherwise>
                            </c:choose>
                            <c:choose>
                                <c:when test="${questionsCount > questionPage*10}">
                                    <a class="none-decoration" href="${pageContext.request.contextPath}/servicio/${serviceId}/?opcion=qst&qstPag=${questionPage+1}">
                                        <p class="page-text"><spring:message code="pagination.next"/></p>
                                    </a>
                                </c:when>
                                <c:otherwise>
                                    <p class="none-page-text"><spring:message code="pagination.none-next"/></p>
                                </c:otherwise>
                            </c:choose>
                            </c:if>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>
            </c:if>

            <c:if test="${option!='qst'}">
                <c:choose>
                    <c:when test="${option==null}"><div id="reviews" class="rq-info transparent"></c:when>
                    <c:otherwise><div id="reviews" class="rq-info"></c:otherwise>
                </c:choose>

                <h3><spring:message code="service.reviews"/></h3>

                <c:if test="${!empty reviews}">
                    <c:set var="rating" value="${service.ratingAvg}" scope="request"/>
                    <c:set var="allRatingCount" value="${service.ratingsCount}" scope="request"/>
                    <c:set var="ratingCountList" value="${ratingCountList}" scope="request"/>
                    <jsp:include page="./components/reviews.jsp" />
                </c:if>

                <c:if test="${!isOwner}">
                <c:choose>
                    <c:when test="${hasAlreadyRated != null}">
                        <c:url value="/editar-opinion/${serviceId}/${hasAlreadyRated.id}" var="rateUrl"/>
                        <h4><spring:message code="service.yourreview"/></h4>
                        <div class="user-opinion-box">

                            <div id="user-review">
                                <div class="flex">
                                    <div class="stars-container">
                                        <c:forEach begin="1" end="${hasAlreadyRated.rating}" var="i">
                                            <i class="material-icons yellow-star">star</i>
                                        </c:forEach>
                                        <c:forEach begin="${hasAlreadyRated.rating+1}" end="5" var="i">
                                            <i class="material-icons gray-star">star</i>
                                        </c:forEach>
                                    </div>
                                    <p class="date"><c:out value="${hasAlreadyRated.date}"/></p>
                                    <div class="align-right">
                                        <button class="edit-btn" onclick="toggleUserReview()"><spring:message code="service.edit-review"/></button>
                                    </div>
                                </div>
                                <p class="text"><c:out value="${hasAlreadyRated.comment}"/></p>
                            </div>
                            <form:form action="${rateUrl}" method="post" modelAttribute="editReviewForm">
                                <div id="edit-review" class="transparent">
                                    <div class="flex edit-header">
                                        <c:forEach begin="1" end="5" var="i">
                                            <i class="material-icons star" onclick="selectRate(${i})">star</i>
                                        </c:forEach>
                                        <div class="align-right">
                                            <button class="edit-btn" onclick="toggleUserReview()"><spring:message code="service.cancel"/></button>
                                        </div>
                                    </div>
                                    <form:input path="editedRating" type="hidden" id="rating" value="${hasAlreadyRated.comment}"/>
                                    <div class="flex">
                                        <form:textarea path="editedComment" class="input edit-input" placeholder="" value="${hasAlreadyRated.comment}"/>
                                        <input type="submit" value="<spring:message code="service.edit-review"/>" class="send-btn">
                                    </div>
                                </div>
                                <form:errors path="editedRating" element="p" cssClass="error"/>
                                <form:errors path="editedComment" element="p" cssClass="error"/>
                            </form:form>
                        </div>

                    </c:when>
                    <c:otherwise>
                        <h4><spring:message code="service.add-review"/></h4>
                        <c:url value="/opinar/${serviceId}" var="rateUrl"/>
                        <form:form action="${rateUrl}" method="post" modelAttribute="reviewForm">
                            <c:forEach begin="1" end="5" var="i">
                                <i class="material-icons star" onclick="selectRate(${i})">star</i>
                            </c:forEach>
                            <form:input path="rating" type="hidden" id="rating" value="0"/>
                            <div class="flex">
                                <spring:message code="service.write-review" var="writeReviewPlaceholder"/>
                                <form:textarea path="comment" class="input" placeholder="${writeReviewPlaceholder}"/>
                                <input type="submit" value="<spring:message code="service.send"/>" class="send-btn">
                            </div>
                            <form:errors path="rating" element="p" cssClass="error"/>
                            <form:errors path="comment" element="p" cssClass="error"/>
                        </form:form>
                    </c:otherwise>
                </c:choose>
                </c:if>

                <c:choose>
                    <c:when test="${empty reviews}">
                        <c:choose>
                            <c:when test="${isOwner}">
                                <p class="comment"><spring:message code="service.no-rates"/></p>
                            </c:when>
                            <c:otherwise>
                                <p class="comment"><spring:message code="service.first-to-rate"/></p>
                            </c:otherwise>
                        </c:choose>
                    </c:when>
                    <c:otherwise>
                        <h4><spring:message code="service.last-reviews"/></h4>
                        <c:forEach items="${reviews}" var="review">
                            <c:if test="${review.id != hasAlreadyRated.id}">
                                <c:set value="${review.rating}" var="rate"/>
                                <div class="question-box">
                                    <div class="flex">
                                        <label class="user">@${review.user.username}</label>
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
                            </c:if>
                        </c:forEach>
                    </c:otherwise>
                </c:choose>

                <div class="align-center">
                <c:choose>
                    <c:when test="${option==null}">
                        <c:if test="${reviewsCount > reviewPage*10}">
                        <a class="none-decoration" href="${pageContext.request.contextPath}/servicio/${serviceId}/?opcion=rw">
                            <p class="page-text"><spring:message code="home.show-more"/></p>
                        </a>
                        </c:if>
                    </c:when>
                    <c:otherwise>
                        <c:if test="${reviewPage > 1 || reviewsCount > reviewPage*10}">
                        <c:choose>
                            <c:when test="${reviewPage > 1}">
                                <a class="none-decoration" href="${pageContext.request.contextPath}/servicio/${serviceId}/?opcion=rw&rwPag=${reviewPage-1}">
                                    <p class="page-text"><spring:message code="pagination.previous"/></p>
                                </a>
                            </c:when>
                            <c:otherwise>
                                    <p class="none-page-text"><spring:message code="pagination.none-previous"/></p>
                            </c:otherwise>
                        </c:choose>
                        <c:choose>
                            <c:when test="${reviewsCount > reviewPage*10}">
                                <a class="none-decoration" href="${pageContext.request.contextPath}/servicio/${serviceId}/?opcion=rw&rwPag=${reviewPage+1}">
                                    <p class="page-text"><spring:message code="pagination.next"/></p>
                                </a>
                            </c:when>
                            <c:otherwise>
                                <p class="none-page-text"><spring:message code="pagination.none-next"/></p>
                            </c:otherwise>
                        </c:choose>
                        </c:if>
                    </c:otherwise>
                </c:choose>
                </div>
            </div>
            </c:if>
        </div>

    </div>

    <script>
        function toggleQuestions() {
            var showQuestions = document.getElementById('questions');
            var showReviews = document.getElementById('reviews');
            var toggleQuestionsButton = document.getElementById('toggleQuestionsButton');
            var toggleReviewsButton = document.getElementById('toggleReviewsButton');

            if (showQuestions.classList.contains('transparent')) {
                showQuestions.classList.remove('transparent');
                showReviews.classList.add('transparent');
                toggleQuestionsButton.classList.add('btn-selected');
                toggleReviewsButton.classList.remove('btn-selected');
            }
        }

        function toggleReviews() {
            var showQuestions = document.getElementById('questions');
            var showReviews = document.getElementById('reviews');
            var toggleQuestionsButton = document.getElementById('toggleQuestionsButton');
            var toggleReviewsButton = document.getElementById('toggleReviewsButton');

            if (showReviews.classList.contains('transparent')) {
                showReviews.classList.remove('transparent');
                showQuestions.classList.add('transparent');
                toggleReviewsButton.classList.add('btn-selected');
                toggleQuestionsButton.classList.remove('btn-selected');
            }
        }

        window.onload = function() {
            var hasAlreadyRated = ${hasAlreadyRated != null};
            if(hasAlreadyRated) {
                selectRate(${hasAlreadyRated.rating})
            }
        };

        function selectRate(rating) {
            document.getElementById("rating").value = rating;
            var star = document.getElementsByClassName("star");
            for (var i = 0; i < star.length; i++) {
                if (i < rating) {
                    star[i].style.color = "#ffd528";
                } else {
                    star[i].style.color = "#b9b9b9";
                }
            }
        }

        function toggleUserReview() {
            var showUserReview = document.getElementById('user-review');
            var showEditReview = document.getElementById('edit-review');

            if (showUserReview.classList.contains('transparent')) {
                showUserReview.classList.remove('transparent');
                showEditReview.classList.add('transparent');
            } else {
                showUserReview.classList.add('transparent');
                showEditReview.classList.remove('transparent');
            }
        }

        function showPopUp() {
            document.getElementById("popup").style.display = "block";
        }
    </script>
</body>
</html>