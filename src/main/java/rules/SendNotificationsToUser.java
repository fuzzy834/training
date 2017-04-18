package rules;

import org.jahia.services.content.*;
import org.jahia.services.content.rules.AddedNodeFact;
import org.jahia.services.content.rules.PublishedNodeFact;
import org.jahia.services.mail.MailService;
import org.slf4j.Logger;

import javax.jcr.RepositoryException;
import javax.script.ScriptException;


/**
 * Created by usersmile on 14.04.17.
 */
public class SendNotificationsToUser {
    private MailService mailService;

    private static final String EMAIL_PROPERTY = "email";

    private static final String CREATED = "Your account has been created";
    private static final String DELETED = "Your account has been deleted";
    private static final String UPDATED = "Your account has been edited";

    private String createdPath;
    private String updatedPath;
    private String deletedPath;

    public void onCreate(AddedNodeFact nodeFact) throws ScriptException, RepositoryException{
        JCRNodeWrapper node = nodeFact.getNode();
        String email = node.getPropertyAsString(EMAIL_PROPERTY);
        sendEmail(email, CREATED, node);
    }

    public void onUpdate(PublishedNodeFact nodeFact) throws ScriptException, RepositoryException{
        JCRNodeWrapper node = nodeFact.getNode();
        String email = node.getPropertyAsString(EMAIL_PROPERTY);
        if(node.isMarkedForDeletion()){
            sendEmail(email, DELETED, node);
        }else {
            sendEmail(email, UPDATED, node);
        }
    }

    private void sendEmail(String email, String status, JCRNodeWrapper node) throws ScriptException, RepositoryException{

//        LOGGER.info(
//                email + "\r\n" +
//                status + "\r\n" +
//                mailService.defaultSender() + "\r\n" +
//                node.getSession().getLocale().toString() + "\r\n" +
//                Files.exists(Paths.get(status))
//        );

//        Map<String, Object> bindings = new HashMap<>();
//        bindings.put("user", node);
//        mailService.sendMessageWithTemplate(status, bindings, email, mailService.defaultSender(), "", "", node.getSession().getLocale(), "created");

        mailService.sendMessage(mailService.defaultSender(), email, "", "", status, "");
    }

    public void setMailService(MailService mailService) {
        this.mailService = mailService;
    }

    public void setCreatedPath(String createdPath) {
        this.createdPath = createdPath;
    }

    public void setUpdatedPath(String updatedPath) {
        this.updatedPath = updatedPath;
    }

    public void setDeletedPath(String deletedPath) {
        this.deletedPath = deletedPath;
    }
}
