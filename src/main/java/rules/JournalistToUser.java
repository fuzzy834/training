package rules;

import org.jahia.services.content.JCRNodeWrapper;
import org.jahia.services.content.JCRSessionWrapper;
import org.jahia.services.content.decorator.JCRUserNode;
import org.jahia.services.content.rules.AddedNodeFact;
import org.jahia.services.content.rules.PublishedNodeFact;
import org.jahia.services.usermanager.JahiaUserManagerService;
import org.slf4j.Logger;
import util.Util;

import javax.jcr.Property;
import javax.jcr.PropertyIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Value;
import java.util.*;

/**
 * Created by usersmile on 11.04.17.
 */
public class JournalistToUser {

    private static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(JournalistToUser.class);
    private JahiaUserManagerService userService = JahiaUserManagerService.getInstance();
    private Util util = Util.getInstance();

    private static final String[] USER_JOURNALIST_PROPS = {"title", "academicTitle", "name",
            "surname", "address", "npa",
            "place", "phone", "cellphone",
            "email", "magazine", "languages",
            "accredetationType", "accreditatedFor", "enabled"};
    private static final String NODENAME_PROPERTY = "j:nodename";
    private static final String PASSWORD_PROPERTY = "password";
    private static final String USER_REF_PROPERTY = "userRef";
    private static final String DELETION_DATE_PROPERTY = "j:deletionDate";
    private static final String UUID_PROPERTY = "jcr:uuid";
    private static final String USER_NODE_TYPE = "jnt:user";
    private static final String OWNER_ROLE = "owner";


    public void createUser(AddedNodeFact nodeFact) {
        try {
            JCRNodeWrapper node = nodeFact.getNode();
            JCRSessionWrapper session = node.getSession();
            JCRUserNode user = createUserFromJournalist(node, session);
            node.grantRoles("u:"+user.getName(), Collections.singleton(OWNER_ROLE));
            node.setProperty(USER_REF_PROPERTY, user.getIdentifier());
            session.save();
            util.publishNodes(user, node);
        } catch (RepositoryException e) {
            LOGGER.error(e.getMessage());
        }
    }

    public void updateAndPublish(PublishedNodeFact nodeFact) {
        try {
            JCRNodeWrapper node = nodeFact.getNode();
            JCRSessionWrapper session = node.getSession();
            JCRUserNode user = (JCRUserNode) util.findNode(session, USER_NODE_TYPE, UUID_PROPERTY, node.getPropertyAsString(USER_REF_PROPERTY)).next();
            if (null != node.getPropertyAsString(DELETION_DATE_PROPERTY)) {
                deleteUser(user, session);
            } else {
                updateUser(user, node);
            }
            session.save();
            util.publishNode(user);
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
        user.setPassword(node.getPropertyAsString(PASSWORD_PROPERTY));
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
}
