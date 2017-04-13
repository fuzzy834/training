package rules;

import org.jahia.services.content.*;
import org.jahia.services.content.decorator.JCRUserNode;
import org.jahia.services.content.rules.AddedNodeFact;
import org.jahia.services.content.rules.PublishedNodeFact;
import org.jahia.services.usermanager.JahiaUserManagerService;
import org.slf4j.Logger;

import javax.jcr.Property;
import javax.jcr.PropertyIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Value;
import javax.jcr.query.Query;
import java.util.*;

/**
 * Created by usersmile on 11.04.17.
 */
public class JournalistToUser {

    private static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(JournalistToUser.class);
    private JahiaUserManagerService userService = JahiaUserManagerService.getInstance();
    private JCRPublicationService publicationService = JCRPublicationService.getInstance();

    private static final String[] USER_JOURNALIST_PROPS = {"title", "academicTitle", "name",
            "surname", "address", "npa",
            "place", "phone", "cellphone",
            "email", "magazine", "languages",
            "accredetationType", "accreditatedFor"};
    private static final String NODENAME_PROPERTY = "j:nodename";
    private static final String PASSWORD_PROPERTY = "password";
    private static final String USER_REF_PROPERTY = "userRef";
    private static final String DELETION_DATE_PROPERTY = "j:deletionDate";
    private static final String DEFAULT_WORKSPACE = "default";
    private static final String LIVE_WORKSPACE = "live";


    public void createUser(AddedNodeFact nodeFact) {
        try {
            JCRNodeWrapper node = nodeFact.getNode();
            JCRSessionWrapper session = node.getSession();
            JCRUserNode user = createUserFromJournalist(node, session);
            node.setProperty(USER_REF_PROPERTY, user.getIdentifier());
            session.save();
            publishUserAndJournalist(user, node);
        } catch (RepositoryException e) {
            LOGGER.error(e.getMessage());
        }
    }

    public void updateAndPublish(PublishedNodeFact nodeFact) {
        try {
            JCRNodeWrapper node = nodeFact.getNode();
            JCRSessionWrapper session = node.getSession();
            JCRUserNode user = getUserById(node, session);
            if (null != node.getPropertyAsString(DELETION_DATE_PROPERTY)) {
                deleteUser(user, session);
            } else {
                updateUser(user, node);
            }
            session.save();
            publishUserAndJournalist(user, node);
        } catch (RepositoryException e) {
            LOGGER.error(e.getMessage());
        }
    }

    private void deleteUser(JCRUserNode user, JCRSessionWrapper session) {
        String userPath = user.getPath();
        userService.deleteUser(userPath, session);
    }

    private void updateUser(JCRUserNode user, JCRNodeWrapper node) throws RepositoryException {
        PropertyIterator propertyIterator = node.getProperties(USER_JOURNALIST_PROPS);
        while (propertyIterator.hasNext()) {
            Property property = propertyIterator.nextProperty();
            if(property.isMultiple()){
                user.setProperty(property.getName(), getStringFromArray(property.getValues()));
            }else user.setProperty(property.getName(), property.getValue().getString());
        }
    }

    private Properties getRequiredProperties(JCRNodeWrapper node) throws RepositoryException {
        Properties properties = new Properties();
        PropertyIterator propertyIterator = node.getProperties(USER_JOURNALIST_PROPS);
        while (propertyIterator.hasNext()) {
            Property property = propertyIterator.nextProperty();
            if (property.isMultiple()){
                properties.put(property.getName(), getStringFromArray(property.getValues()));
            }else properties.put(property.getName(), property.getValue().getString());
        }
        return properties;
    }

    private JCRUserNode createUserFromJournalist(JCRNodeWrapper node, JCRSessionWrapper session) throws RepositoryException {
        String username = node.getPropertyAsString(NODENAME_PROPERTY);
        String password = node.getPropertyAsString(PASSWORD_PROPERTY);
        Properties properties = getRequiredProperties(node);
        return userService.createUser(username, password, properties, session);
    }

    private String getStringFromArray(Value[] values) throws RepositoryException {
        StringBuilder concatValues = new StringBuilder();
        if (values.length >= 1) {
            for (Value value : values) {
                concatValues.append(value.getString()).append(", ");
            }
            String result = concatValues.toString();
            return result.substring(0, result.length() - 2);
        }
        return "";
    }

    private void publishUserAndJournalist(JCRUserNode user, JCRNodeWrapper node) throws RepositoryException {
        String[] uiids = {user.getIdentifier(), node.getIdentifier()};
        publicationService.publish(Arrays.asList(uiids),
                DEFAULT_WORKSPACE,
                LIVE_WORKSPACE,
                Collections.singletonList(""));
    }

    private JCRUserNode getUserById(JCRNodeWrapper node, JCRSessionWrapper session) throws RepositoryException {
        QueryManagerWrapper queryManager = session.getWorkspace().getQueryManager();
        JCRNodeIteratorWrapper nodeIterator = queryManager
                .createQuery("SELECT * FROM [jnt:user]", Query.JCR_JQOM)
                .execute()
                .getNodes();
        JCRNodeWrapper user = null;
        for (JCRNodeWrapper userNode : nodeIterator) {
            if (userNode.getIdentifier().equals(node.getPropertyAsString(USER_REF_PROPERTY))) {
                user = userNode;
            }
        }
        return (JCRUserNode) user;
    }
}
