package edu.ben.rate_review.email;

import com.sendgrid.*;
import com.sendgrid.Email;

import java.util.HashMap;

/**
 * SendGrid
 */
public class SendGridManager {
    public static final String FROM_EMAIL = "ratereviewtest@gmail.com";
    private static SendGridManager manager;
    private SendGrid mailer = null;

    public SendGridManager() {
        final String API_KEY = System.getenv("SENDGRID_API_KEY");
        if (API_KEY == null) {
            throw new NullPointerException("Missing SendGrid API Key");
        }
        this.mailer = new SendGrid(API_KEY);
    }

    public static SendGridManager getInstance() {
        if (manager == null) {
            manager = new SendGridManager();
        }
        return manager;
    }

    public SendGrid getMailer() {
        return this.mailer;
    }

    public void send(HashMap<String, String> params) {
        final Email from = new Email(FROM_EMAIL, "Rate and Review");
        String subject;
        Email to;
        Content content;

        // Check for a valid subject
        if (!params.containsKey("subject") && params.get("subject") == null) {
            throw new NullPointerException("Missing message subject");
        }
        subject = params.get("subject");

        // Check for a valid message
        if (!params.containsKey("to") || params.get("to") == null) {
            throw new NullPointerException("Missing to address");
        }
        to = new Email(params.get("to"));

        if (params.containsKey("name") && params.get("name") != null) {
            to.setName(params.get("name"));
        }

        // Check for a valid message
        if (!params.containsKey("message") || params.get("message") == null) {
            throw new NullPointerException("Missing message content");
        }
        content = new Content("text/html", params.get("message"));


        // Create the mail message
        Mail mail = new Mail(from, subject, to, content);
        Request request;
        Response response;
        System.out.println("Sending email - to:" + to.getEmail() + ", subject: " + subject + ", message:" + content.getValue());
        try {
            request = new Request();
            request.method = Method.POST;
            request.endpoint = "mail/send";
            request.body = mail.build();
            response = this.mailer.api(request);
            System.out.println(response.statusCode);
            System.out.println(response.body);
            System.out.println(response.headers);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
