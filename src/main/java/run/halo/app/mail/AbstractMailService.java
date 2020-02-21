package run.halo.app.mail;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.lang.NonNull;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.util.Assert;
import run.halo.app.exception.EmailException;
import run.halo.app.model.properties.EmailProperties;
import run.halo.app.service.OptionService;

import javax.mail.internet.InternetAddress;
import java.io.UnsupportedEncodingException;

/**
 * Abstract mail service.
 *
 * @author johnniang
 */
@Slf4j
public abstract class AbstractMailService implements MailService {

    private JavaMailSender cachedMailSender;

    private MailProperties cachedMailProperties;

    private String cachedFromName;

    private OptionService optionService;

    protected AbstractMailService(OptionService optionService) {
        this.optionService = optionService;
    }

    /**
     * Send mail template.
     *
     * @param callback mime message callback.
     */
    protected void sendMailTemplate(Callback callback) {
        if (callback == null) {
            log.info("Callback is null, skip to send email");
            return;
        }

        // check if mail is enable
        Boolean emailEnabled = optionService.getByPropertyOrDefault(EmailProperties.ENABLED, Boolean.class);

        if (!emailEnabled) {
            // If disabled
            log.info("Email has been disabled by yourself, you can re-enable it through email settings on admin page.");
            return;
        }

        // get mail sender
        JavaMailSender mailSender = getMailSender();
        printMailConfig();

        // create mime message helper
        MimeMessageHelper messageHelper = new MimeMessageHelper(mailSender.createMimeMessage());

        // handle message set separately
        try {
            messageHelper.setFrom(getFromAddress(mailSender));
            callback.handle(messageHelper);
        } catch (Exception e) {
            throw new EmailException("邮件发送失败，请检查 SMTP 服务配置是否正确", e);
        }

        mailSender.send(messageHelper.getMimeMessage());
    }

    /**
     * Get java mail sender.
     *
     * @return java mail sender
     */
    @NonNull
    private synchronized JavaMailSender getMailSender() {
        if (this.cachedMailSender == null) {
            // create mail sender factory
            MailSenderFactory mailSenderFactory = new MailSenderFactory();
            // get mail sender
            this.cachedMailSender = mailSenderFactory.getMailSender(getMailProperties());
        }

        return this.cachedMailSender;
    }

    /**
     * Get from-address.
     *
     * @param javaMailSender java mail sender.
     * @return from-name internet address
     * @throws UnsupportedEncodingException throws when you give a wrong character encoding
     */
    private synchronized InternetAddress getFromAddress(@NonNull JavaMailSender javaMailSender) throws UnsupportedEncodingException {
        Assert.notNull(javaMailSender, "Java mail sender must not be null");

        if (StringUtils.isBlank(this.cachedFromName)) {
            // set personal name
            this.cachedFromName = optionService.getByPropertyOfNonNull(EmailProperties.FROM_NAME).toString();
        }

        if (javaMailSender instanceof JavaMailSenderImpl) {
            // get user name(email)
            JavaMailSenderImpl mailSender = (JavaMailSenderImpl) javaMailSender;
            String username = mailSender.getUsername();

            // build internet address
            return new InternetAddress(username, this.cachedFromName, mailSender.getDefaultEncoding());
        }

        throw new UnsupportedOperationException("Unsupported java mail sender: " + javaMailSender.getClass().getName());
    }

    /**
     * Get mail properties.
     *
     * @return mail properties
     */
    @NonNull
    private synchronized MailProperties getMailProperties() {
        if (cachedMailProperties == null) {
            // create mail properties
            MailProperties mailProperties = new MailProperties(log.isDebugEnabled());

            // set properties
            mailProperties.setHost(optionService.getByPropertyOrDefault(EmailProperties.HOST, String.class));
            mailProperties.setPort(optionService.getByPropertyOrDefault(EmailProperties.SSL_PORT, Integer.class));
            mailProperties.setUsername(optionService.getByPropertyOrDefault(EmailProperties.USERNAME, String.class));
            mailProperties.setPassword(optionService.getByPropertyOrDefault(EmailProperties.PASSWORD, String.class));
            mailProperties.setProtocol(optionService.getByPropertyOrDefault(EmailProperties.PROTOCOL, String.class));
            this.cachedMailProperties = mailProperties;
        }

        return this.cachedMailProperties;
    }

    /**
     * Print mail configuration.
     */
    private void printMailConfig() {
        if (!log.isDebugEnabled()) {
            return;
        }

        // get mail properties
        MailProperties mailProperties = getMailProperties();
        log.debug(mailProperties.toString());
    }

    /**
     * Clear cached instance.
     */
    protected void clearCache() {
        this.cachedMailSender = null;
        this.cachedFromName = null;
        this.cachedMailProperties = null;
        log.debug("Cleared all mail caches");
    }

    /**
     * Message callback.
     */
    protected interface Callback {
        /**
         * Handle message set.
         *
         * @param messageHelper mime message helper
         * @throws Exception if something goes wrong
         */
        void handle(@NonNull MimeMessageHelper messageHelper) throws Exception;
    }
}
