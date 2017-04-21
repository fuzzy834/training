package rules;

import org.jahia.services.content.JCRNodeWrapper;
import org.jahia.services.content.rules.PublishedNodeFact;
import org.jahia.services.mail.MailService;
import javax.jcr.RepositoryException;
import javax.script.ScriptException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;


/**
 * Created by usersmile on 14.04.17.
 */
public class SendNotificationsToUser {

    private MailService mailService;
    private String updatedPath;

    private static final String EMAIL_PROPERTY = "email";

    public void onUpdate(PublishedNodeFact nodeFact) throws ScriptException, RepositoryException{
        JCRNodeWrapper node = nodeFact.getNode();
        String email = node.getPropertyAsString(EMAIL_PROPERTY);
        if(!node.isMarkedForDeletion()){
            Map<String, Object> bindings = new HashMap<>();
            bindings.put("user", node);
            mailService.sendMessageWithTemplate(updatedPath, bindings, email, mailService.defaultSender(), null, null, new Locale("en"), "Training");
        }
    }

    public void setMailService(MailService mailService) {
        this.mailService = mailService;
    }

    public void setUpdatedPath(String updatedPath) {
        this.updatedPath = updatedPath;
    }

}
