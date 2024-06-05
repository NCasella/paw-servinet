<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<html>
<head>
    <link href="${pageContext.request.contextPath}/css/global.css" rel="stylesheet" />
    <link href="https://fonts.googleapis.com/icon?family=Material+Icons" rel="stylesheet">
    <link rel="stylesheet" href="https://fonts.googleapis.com/css2?family=Poppins:wght@300;400;500;600;700&display=swap">
    <title></title>
</head>
<body>

<a href="javascript:history.back()" class="none-decoration back-container">
    <i class="material-icons back-arrow">arrow_back</i>
    <label class="back-text" id="previous-page-text"><spring:message code="back.goBack"/></label>
</a>

<script>
    var serviceText = "<spring:message code='back.service'/>";
    var servicesText = "<spring:message code='back.services'/>";
    var businessText = "<spring:message code='back.business'/>";
    var businessesText = "<spring:message code='back.businesses'/>";
    var profileText = "<spring:message code='back.profile'/>";
    var appointmentsText = "<spring:message code='back.appointments'/>";

    function getPreviousPageInfo(referrer) {
        if(referrer.includes("servicios")) {
            document.getElementById('previous-page-text').textContent = servicesText;
        } else if(referrer.includes("servicio")) {
            document.getElementById('previous-page-text').textContent = serviceText;
        } else if (referrer.includes("negocios")) {
            document.getElementById('previous-page-text').textContent = businessesText;
        } else if(referrer.includes("negocio")) {
            document.getElementById('previous-page-text').textContent = businessText;
        } else if(referrer.includes("perfil")) {
            document.getElementById('previous-page-text').textContent = profileText;
        } else if(referrer.includes("turnos")) {
            document.getElementById('previous-page-text').textContent = appointmentsText;
        }
    }
</script>
</body>
</html>
