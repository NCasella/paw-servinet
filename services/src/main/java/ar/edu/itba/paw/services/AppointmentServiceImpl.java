package ar.edu.itba.paw.services;

import ar.edu.itba.paw.model.*;
import ar.edu.itba.paw.model.exceptions.AppointmentAlreadyConfirmed;
import ar.edu.itba.paw.model.exceptions.AppointmentNonExistentException;
import ar.edu.itba.paw.model.exceptions.ServiceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import javax.mail.MessagingException;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@org.springframework.stereotype.Service("appointmentServiceImpl")
public class AppointmentServiceImpl implements AppointmentService{

    private final AppointmentDao appointmentDao;
    private final EmailService emailService;
    private final ServiceDao serviceDao;
    private final BusinessDao businessDao;
    private final UserService userService;

    private final Logger LOGGER = LoggerFactory.getLogger(AppointmentServiceImpl.class);
    @Autowired
    public AppointmentServiceImpl(final AppointmentDao appointmentDao, final EmailService emailService,
                                    final BusinessDao businessDao, ServiceDao serviceDao, final UserService userService) {
        this.appointmentDao = appointmentDao;
        this.emailService = emailService;
        this.businessDao = businessDao;
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
    public List<Appointment> getAllUpcomingServicesAppointments(Collection<Long> serviceIds, boolean confirmed) {
        return appointmentDao.getAllUpcomingServicesAppointments(serviceIds,confirmed);
    }
    @Transactional(readOnly = true)
    @Override
    public List<AppointmentInfo> getAllUpcomingUserAppointments(long userid, boolean confirmed) {
        return appointmentDao.getAllUpcomingUserAppointments(userid, confirmed);
    }
    @Transactional
    @Override
    public Appointment create(long serviceid, String name, String surname, String email, String location, String telephone, String date) {
        Service service = serviceDao.findById(serviceid).orElseThrow(ServiceNotFoundException::new);
        User newuser = userService.findByEmail(email).orElse(null);
        LocalDateTime startDate = LocalDateTime.parse(date);
        Appointment appointment = appointmentDao.create(service.getId(), newuser.getUserId(), startDate, startDate.plusMinutes(service.getDuration()), location);
        Business business = businessDao.findById(service.getBusinessid()).get();
            //* si ya tiene el Service => ya lo paso x param
        emailService.requestAppointment(appointment, service, business, newuser);
        LOGGER.info("Appointment request email sent successfully.");
        return appointment;
    }
    @Transactional
    @Override
    public long confirmAppointment(long appointmentid) {
        Optional<Appointment> optionalAppointment = findById(appointmentid);
        if (!optionalAppointment.isPresent())
            throw new AppointmentNonExistentException();
        Appointment appointment = optionalAppointment.get();
        final Service service = serviceDao.findById(appointment.getServiceid()).get();
        final User client = userService.findById( appointment.getUserid()).get();

        if (appointment.getConfirmed())
            throw new AppointmentAlreadyConfirmed();

        Business business = businessDao.findById( service.getBusinessid()).get();
        appointmentDao.confirmAppointment(appointment.getId());
        emailService.confirmedAppointment(appointment, service, business, client);
        LOGGER.info("Appointment confirmation email sent successfully.");
        return service.getId();
    }
    @Transactional
    @Override
    public long denyAppointment(long appointmentid) {
        Optional<Appointment> optionalAppointment = findById(appointmentid);
        if (!optionalAppointment.isPresent())
            throw new AppointmentNonExistentException();
        Appointment appointment = optionalAppointment.get();
        final Service service = serviceDao.findById(appointment.getServiceid()).get();
        final User client = userService.findById( appointment.getUserid()).get();

        if (appointment.getConfirmed())
            throw new AppointmentAlreadyConfirmed();

        Business business = businessDao.findById( service.getBusinessid()).get();
        appointmentDao.cancelAppointment(appointment.getId());
        emailService.deniedAppointment(appointment, service, business, client,false);
        LOGGER.info("Denied appointment email sent successfully.");

        return service.getId();
    }
    @Transactional
    @Override
    public long cancelAppointment(long appointmentid) {
        Optional<Appointment> optionalAppointment = findById(appointmentid);
        if (!optionalAppointment.isPresent())
            throw new AppointmentNonExistentException();
        Appointment appointment = optionalAppointment.get();
        final Service service = serviceDao.findById(appointment.getServiceid()).get();
        final User client = userService.findById( appointment.getUserid()).get();

        Business business = businessDao.findById( service.getBusinessid()).get();
        appointmentDao.cancelAppointment(appointment.getId());
        emailService.cancelledAppointment(appointment, service,business, client,false);
        LOGGER.info("Cancel Appointment email sent successfully.");

        return service.getId();
    }
}
