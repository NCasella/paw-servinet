package ar.edu.itba.paw.services;

import ar.edu.itba.paw.model.*;
import ar.edu.itba.paw.model.exceptions.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;


import java.time.LocalDateTime;
import java.util.*;

@org.springframework.stereotype.Service("appointmentServiceImpl")
public class AppointmentServiceImpl implements AppointmentService{

    private final AppointmentDao appointmentDao;
    private final EmailService emailService;
    private final ServiceDao serviceDao;
    private final UserService userService;

    private final static int PAGESIZE = 10;

    private final Logger LOGGER = LoggerFactory.getLogger(AppointmentServiceImpl.class);
    @Autowired
    public AppointmentServiceImpl(final AppointmentDao appointmentDao, final EmailService emailService, ServiceDao serviceDao, final UserService userService) {
        this.appointmentDao = appointmentDao;
        this.emailService = emailService;
        this.serviceDao = serviceDao;
        this.userService = userService;
    }

    @Override
    public Optional<Appointment> findById(long id) {
        return appointmentDao.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Appointment> getAllUpcomingServiceAppointments(long serviceid) {
        return appointmentDao.getAllUpcomingServiceAppointments(serviceid);
    }
    @Transactional(readOnly = true)
    @Override
    public List<Appointment> getAllUpcomingServicesAppointments(Collection<Long> serviceIds, boolean confirmed, int page) {
        return appointmentDao.getAllUpcomingServicesAppointments(serviceIds,confirmed, page, PAGESIZE);
    }

    @Transactional(readOnly = true)
    @Override
    public long getServicesAppointmentCount(Collection<Long> serviceIds, boolean confirmed) {
        return appointmentDao.getServicesAppointmentCount(serviceIds,confirmed);
    }

    @Transactional(readOnly = true)
    @Override
    public List<Pair<Long,Long>> getServicesFinishedAppointmentCount(Collection<Long> serviceIds, DateIntervalFilter filter) {
        LocalDateTime currentDateTime = LocalDateTime.now();
        return appointmentDao.getServicesFinishedAppointmentCount(serviceIds, filter.getEndDate(currentDateTime),currentDateTime);
    }

    @Transactional(readOnly = true)
    @Override
    public Long getServicesRequestedAppointmentCount(Collection<Long> serviceIds, DateIntervalFilter filter) {
        LocalDateTime currentDateTime = LocalDateTime.now();
        return appointmentDao.getServicesRequestedAppointmentCount(serviceIds, filter.getEndDate(currentDateTime));
    }

    @Transactional(readOnly = true)
    @Override
    public List<Appointment> getAllUpcomingUserAppointments(long userid, boolean confirmed, int page) {
        return appointmentDao.getAllUpcomingUserAppointments(userid, confirmed, page, PAGESIZE);
    }


    @Transactional(readOnly = true)
    @Override
    public long getUserAppointmentCount(long userid, boolean confirmed) {
        return appointmentDao.getUserAppointmentCount(userid,confirmed);
    }

    @Transactional(readOnly = true)
    @Override
    public List<Appointment> getPreviousUserAppointments(long userid, int page) {
        return appointmentDao.getPreviousUserAppointments(userid,page, PAGESIZE);
    }

    @Transactional(readOnly = true)
    @Override
    public long getPreviousUserAppointmentCount(long userid) {
        return appointmentDao.getPreviousUserAppointmentCount(userid);
    }

    @Transactional(readOnly = true)
    @Override
    public long getPageCount(long count){
        return count / PAGESIZE + (( count % PAGESIZE !=0 )? 1:0 );
    }

    @Transactional
    @Override
    public Appointment create(long serviceid, String name, String surname, String email, String location, String telephone, String date, String description) {
        Service service = serviceDao.findById(serviceid).orElseThrow(ServiceNotFoundException::new);
        User newuser = userService.findByEmail(email).orElseThrow(UserNotFoundException::new);
        LocalDateTime startDate = LocalDateTime.parse(date);
        Appointment appointment = appointmentDao.create(service, newuser, startDate, startDate.plusMinutes(service.getDuration()), location, description);
        Business business = service.getBusiness();

        emailService.requestAppointment(appointment, service, business, newuser, business.getOwnedBy().getLocale());
        LOGGER.info("Appointment request email sent successfully.");
        return appointment;
    }

    @Transactional
    @Override
    public void changePendingAppointmentStatus(long appointmentid, boolean confirmed) {
        Appointment appointment = findById(appointmentid).orElseThrow(AppointmentNonExistentException::new);
        final Service service = appointment.getServiceAppointed();
        final User client = appointment.getAppointedBy();

        if (appointment.getConfirmed())
            throw new AppointmentAlreadyConfirmed();

        Business business = service.getBusiness();
        if ( confirmed ) {
            appointmentDao.confirmAppointment(appointment.getId());
            emailService.confirmedAppointment(appointment, service, business, client, business.getOwnedBy().getLocale());
            LOGGER.info("Appointment confirmation email sent successfully.");
        } else {
            appointmentDao.cancelAppointment(appointment.getId());
            emailService.deniedAppointment(appointment, service, business, client,false,business.getOwnedBy().getLocale());
            LOGGER.info("Denied appointment email sent successfully.");
        }
    }

    @Transactional
    @Override
    public long confirmAppointment(long appointmentid) {

        Appointment appointment = findById(appointmentid).orElseThrow(AppointmentNonExistentException::new);
        final Service service = appointment.getServiceAppointed();
        final User client = appointment.getAppointedBy();

        if (appointment.getConfirmed())
            throw new AppointmentAlreadyConfirmed();

        Business business = service.getBusiness();
        appointmentDao.confirmAppointment(appointment.getId());
        emailService.confirmedAppointment(appointment, service, business, client, business.getOwnedBy().getLocale() );
        LOGGER.info("Appointment confirmation email sent successfully.");
        return service.getId();
    }
    @Transactional
    @Override
    public long denyAppointment(long appointmentid) {
        Appointment appointment = findById(appointmentid).orElseThrow(AppointmentNonExistentException::new);
        final Service service = appointment.getServiceAppointed();
        final User client = appointment.getAppointedBy();

        if (appointment.getConfirmed())
            throw new AppointmentAlreadyConfirmed();

        Business business = service.getBusiness();
        appointmentDao.cancelAppointment(appointment.getId());
        emailService.deniedAppointment(appointment, service, business, client,false,business.getOwnedBy().getLocale());
        LOGGER.info("Denied appointment email sent successfully.");

        return service.getId();
    }
    @Transactional
    @Override
    public long cancelAppointment(long appointmentid) {
        Appointment appointment = findById(appointmentid).orElseThrow(AppointmentNonExistentException::new);
        final Service service = appointment.getServiceAppointed();
        final User client = appointment.getAppointedBy();
        LOGGER.info("Found user");
        Business business = service.getBusiness();
        appointmentDao.cancelAppointment(appointment.getId());
        LOGGER.info("About to send email");
        emailService.cancelledAppointment(appointment, service,business, client,false, business.getOwnedBy().getLocale());
        LOGGER.info("Cancel Appointment email sent successfully.");

        return service.getId();
    }

    @Transactional
    @Override
    public Appointment create(long serviceid, long userid, String location, LocalDateTime startDate, String description) {
        Service service = serviceDao.findById(serviceid).orElseThrow(ServiceNotFoundException::new);
        User newuser = userService.findById(userid).orElseThrow(UserNotFoundException::new);

        Appointment appointment = appointmentDao.create(service, newuser, startDate, startDate.plusMinutes(service.getDuration()), location, description);
        Business business = service.getBusiness();

        emailService.requestAppointment(appointment, service, business, newuser, business.getOwnedBy().getLocale());
        LOGGER.info("Appointment request email sent successfully.");
        return appointment;
    }

    @Transactional
    @Override
    public PagedList<Appointment> getUserAppointments(long userId, AppointmentStatus status, int page) {
        List<Appointment> list;
        if (status==AppointmentStatus.FINISHED) {
            list = getPreviousUserAppointments(userId, page);
            return PagedList.of(list, (int) getPreviousUserAppointmentCount(userId) );
        }
        list = getAllUpcomingUserAppointments(userId,status==AppointmentStatus.CONFIRMED,page);
        return PagedList.of(list, (int) getUserAppointmentCount(userId,status==AppointmentStatus.CONFIRMED) );
    }

    @Transactional
    @Override
    public PagedList<Appointment> getBusinessAppointments(long businessId, AppointmentStatus status, int page) {
        List<BasicService> services = serviceDao.getAllBusinessBasicServices(businessId);
        Map<Long, BasicService> serviceMap = new HashMap<>();
        services.forEach(service -> serviceMap.put(service.getId(), service));
        Set<Long> serviceIds =  serviceMap.keySet();

        List<Appointment> l = getAllUpcomingServicesAppointments(serviceIds, status==AppointmentStatus.CONFIRMED, page);
        final int totalResults = (int) getServicesAppointmentCount(serviceIds,status==AppointmentStatus.CONFIRMED);
        return PagedList.of(l,totalResults);
    }
}
