package ua.nc.service;

import org.apache.log4j.Logger;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Service;
import ua.nc.dao.MailDAO;
import ua.nc.dao.enums.DataBaseType;
import ua.nc.dao.exception.DAOException;
import ua.nc.dao.factory.DAOFactory;
import ua.nc.entity.Mail;
import ua.nc.entity.User;

import java.sql.Connection;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by Alexander Haliy on 23.04.2016.
 */
@Configuration
@EnableScheduling
@Service("mailService")
public class MailServiceImpl implements MailService {
    private static final Logger LOGGER = Logger.getLogger(MailServiceImpl.class);
    private static final int MILLIS_PER_HOUR = 1000 * 60 * 60;
    private static final int POOL_SIZE = 2;
    private static final int POOL_SIZE_SCHEDULER = 10;
    private static ThreadPoolTaskScheduler scheduler;
    private static ThreadPoolTaskScheduler schedulerMassDeliveryService;

    static {
        scheduler = new ThreadPoolTaskScheduler();
        schedulerMassDeliveryService = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(POOL_SIZE);
        schedulerMassDeliveryService.setPoolSize(POOL_SIZE_SCHEDULER);
        scheduler.initialize();
        schedulerMassDeliveryService.initialize();
    }

    private DAOFactory daoFactory = DAOFactory.getDAOFactory(DataBaseType.POSTGRESQL);

    public MailServiceImpl() {

    }

    /**
     * Create new Mail and store
     * it in db
     *
     * @param header
     * @param body
     */
    @Override
    public Mail createMail(String header, String body) {
        Connection connection = daoFactory.getConnection();
        MailDAO mailDAO = daoFactory.getMailDAO(connection);
        Mail mail = null;
        try {
            mail = new Mail();
            mail.setHeadTemplate(header);
            mail.setBodyTemplate(body);
            Mail newCreated = mailDAO.create(mail);
            LOGGER.debug(newCreated.getId());
        } catch (DAOException e) {
            LOGGER.warn("Mail not created ", e);
        } finally {
            daoFactory.putConnection(connection);
        }
        return mail;
    }

    /**
     * Send email to recipent
     *
     * @param address
     * @param mail
     */
    @Override
    public void sendMail(String address, Mail mail) {
        sendMail(address, mail.getHeadTemplate(), mail.getBodyTemplate());
    }

    /**
     * Send email  to recipient with concrete Mail entity
     * Async call function will return controll to the main flow
     * Sends email with delay 5 seconds, spam-filter will pass these
     *
     * @param address recipient address
     * @param header
     * @param body
     */


    public void sendMail(String address, String header, String body) {

        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        Properties properties = getMailProperties();
        mailSender.setProtocol("smtp");
        mailSender.setHost("smtp.gmail.com");
        mailSender.setPort(587);
        mailSender.setUsername("netcrackerua@gmail.com");
        mailSender.setPassword("netcrackerpwd");
        mailSender.setJavaMailProperties(properties);
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(address);
        message.setSubject(header);
        message.setText(body);
        AsynchronousSender(message, mailSender);
        //mailSender.send(message);
    }

    /**
     * Async mail sending
     *
     * @param message
     * @param mailSender
     */
    public void AsynchronousSender(final SimpleMailMessage message, final MailSender mailSender) {
        scheduler.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    mailSender.send(message);
                } catch (Exception e) {
                    LOGGER.warn("Failed to send", e);
                }
            }
        });
    }

    /**
     * Massive delivery service for async mailing
     * Everything you need is to put time
     *
     * @param dateDelivery specific date mail to be send
     * @param users        who will get invitation
     * @param mail         template
     */
    public void massDelivery(String dateDelivery, final List<User> users, final Mail mail) {
        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        // String date format 2012-07-06 13:05:45
        try {
            Date date = dateFormatter.parse(dateDelivery);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        schedulerMassDeliveryService.schedule(new Runnable() {
            @Override
            public void run() {
                try {

                    for (User i : users) {
                        //Sleep for one second,google may think you're spamming :(
                        Thread.sleep(1000);
                        sendMail(i.getEmail(), mail);
                    }

                } catch (Exception e) {
                    LOGGER.warn("Failed to send email", e);
                }
            }
        }, new Date(dateDelivery));
    }


    public void sendInterviewReminders(List<Date> interviewDates, int studentHours, int devHours, int hrHours,
                                       int baHours, Mail InterviewerMail, Mail IntervieweeMail) {
        int studentMillis = studentHours * MILLIS_PER_HOUR;
        int devMillis = devHours * MILLIS_PER_HOUR;
        int hrMillis = hrHours * MILLIS_PER_HOUR;
        int baMillis = baHours * MILLIS_PER_HOUR;

        for (Date interviewDate : interviewDates) {
            massDelivery(new Date(interviewDate.getTime() + studentMillis).toString(), new ArrayList<User>(), IntervieweeMail);
            massDelivery(new Date(interviewDate.getTime() + devMillis).toString(), new ArrayList<User>(), InterviewerMail);
            massDelivery(new Date(interviewDate.getTime() + hrMillis).toString(), new ArrayList<User>(), InterviewerMail);
            massDelivery(new Date(interviewDate.getTime() + baMillis).toString(), new ArrayList<User>(), InterviewerMail);
        }
    }


    @Override
    public Date planSchedule(int hoursPerDay, Mail interviewerMail, Map<String, String> interviewerParameters, Mail studentMail, Map<String, String> studentParameters) {
        return null;
    }

    @Override
    public List<Mail> getAllMails() {
        Connection connection = daoFactory.getConnection();
        MailDAO mailDAO = daoFactory.getMailDAO(connection);
        List<Mail> mails = new ArrayList<>();
        try {
            mails = mailDAO.getAll();
        } catch (DAOException e) {
            LOGGER.error("Can't retrieve all mail", e);
        } finally {
            daoFactory.putConnection(connection);
        }
        return mails;
    }

    @Override
    public void updateMail(Mail mail) {
        Connection connection = daoFactory.getConnection();
        MailDAO mailDAO = daoFactory.getMailDAO(connection);
        try {
            mailDAO.update(mail);
        } catch (DAOException e) {
            LOGGER.error("Can't update mail", e);
        } finally {
            daoFactory.putConnection(connection);
        }
    }

    @Override
    public void deleteMail(Mail mail) {
        Connection connection = daoFactory.getConnection();
        MailDAO mailDAO = daoFactory.getMailDAO(connection);
        try {
            mailDAO.delete(mail);
        } catch (DAOException e) {
            LOGGER.error("Can't delete mail", e);
        } finally {
            daoFactory.putConnection(connection);
        }
    }

    /**
     * Retrieve mail by id
     *
     * @param id
     */
    @Override
    public Mail getMail(Integer id) {
        Connection connection = daoFactory.getConnection();
        MailDAO mailDAO = daoFactory.getMailDAO(connection);
        Mail mail = null;
        try {
            mail = mailDAO.get(id);
        } catch (DAOException e) {
        } finally {
            daoFactory.putConnection(connection);
        }
        return mail;
    }

    /**
     * Get Mails by Header
     *
     * @param header
     * @return
     */
    public List<Mail> getByHeaderMailTemplate(String header) {
        List<Mail> mails = new ArrayList<>();
        Connection connection = daoFactory.getConnection();
        MailDAO mailDAO = daoFactory.getMailDAO(connection);
        try {
            mails = mailDAO.getByHeader(header);
        } catch (DAOException e) {
            LOGGER.error("Can't handle mail template", e);
        } finally {
            daoFactory.putConnection(connection);
        }
        return mails;
    }

    @Override
    public void massDelivery(String dateDelivery, List<User> users, Mail mail, Map<String, String> parameters) {

    }


    /**
     * Configuration for mail mail delivery service
     *
     * @return propeties
     */
    private Properties getMailProperties() {
        Properties mailProperties = new Properties();
        mailProperties.put("mail.smtps.auth", "true");
        mailProperties.put("mail.smtp.ssl.trust", "smtp.gmail.com");
        mailProperties.put("mail.smtp.starttls.enable", "true");
        mailProperties.put("mail.smtp.debug", "true");
        return mailProperties;
    }


}
