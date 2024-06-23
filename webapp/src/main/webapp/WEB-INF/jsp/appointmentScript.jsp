<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<script src="${pageContext.request.contextPath}/js/fetch.js"></script>
<script type="text/javascript">
    const businessUrl = '${pageContext.request.contextPath}/negocio'
    const deleteAppointmentUrl = '${pageContext.request.contextPath}/cancelar-turno/'

    var pageTotalResults = Number('${appointmentList.size()}')

    const totalResultsElement=document.getElementById('totalResults')

    var totalResults =  Number('${totalResults}')
    totalResultsElement.textContent = totalResults

    var moreResults =  Number('${moreResults}')
    const moreResultsElement=document.getElementById('moreResults')
    moreResultsElement.textContent = String(moreResults)

    const changeNext = Boolean ('${ !isUser and !confirmed }')

    const loader = document.getElementById('loader');

    function acceptAppointment(appointmentId,accepted, componentId){

        const data = new FormData();
        data.append('accepted', accepted);

        const url = businessUrl + '/solicitud-turno/' + appointmentId;
        if ( send(url,'POST', data) === 0) {
            document.getElementById(componentId).style.display = "none";
            changeTotalResults(accepted);
        }
    }

    function changeTotalResults(accepted) {
        totalResults-= 1
        pageTotalResults -=1
        if ( pageTotalResults===0 ) {
            loader.style.display = "flex";
            setTimeout(function() {
                loader.style.display = "none";
                window.location.reload()
            }, 2000);
        }
        totalResultsElement.textContent = String(totalResults)
        if ( changeNext===true && accepted) {
            moreResults += 1
            moreResultsElement.textContent = String(moreResults)
        }
    }

    function cancelAppointment(appointmentId,componentId) {

        const url = deleteAppointmentUrl + appointmentId;

        if ( send(url,'DELETE',{}) === 0 ) {
            document.getElementById(componentId).style.display = "none";
            changeTotalResults(false)
        }
    }

    function showAccordion( id, btn ) {
        btn.classList.toggle('active')
        document.getElementById(id).classList.toggle('active')
    }

    function showPopUpApp(componentId){
        document.getElementById(componentId).style.display = "block";
    }

    function closePopup(componentId) {
        document.getElementById(componentId).style.display = "none";
    }


</script>