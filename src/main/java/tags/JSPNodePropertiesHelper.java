package tags;

import org.jahia.api.Constants;
import org.jahia.services.content.JCRNodeWrapper;
import org.jahia.services.content.JCRSessionFactory;
import org.jahia.services.content.JCRSessionWrapper;
import org.jahia.services.usermanager.JahiaUser;
import org.jahia.taglibs.jcr.AbstractJCRTag;
import org.slf4j.Logger;
import util.Util;

import javax.jcr.RepositoryException;
import javax.servlet.jsp.JspException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by usersmile on 21.04.17.
 */
public class JSPNodePropertiesHelper extends AbstractJCRTag{

    private static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(JSPNodePropertiesHelper.class);

    private static final String[] LANGUAGES = {"French", "German", "Italian"};

    private String nodeId;
    private String propName;
    private boolean multiValued;
    private String propValue;

    public static String getJahiaUserProperty(JahiaUser user, String propertyName){
        return user.getProperty(propertyName);
    }

    public void setJournalistProperty(){
        try{
            JCRSessionWrapper session = JCRSessionFactory.getInstance().getCurrentSystemSession(Constants.EDIT_WORKSPACE, null, null);
            JCRNodeWrapper node = (JCRNodeWrapper) Util.getInstance().findNode(session, "trnt:journalist", "jcr:uuid", nodeId).next();

            if(!node.hasProperty(propName)){
                LOGGER.warn("Following node " + node.getName() + "doesn't contain property " + propName);
                return;
            }
            if(!multiValued) {
                node.setProperty(propName, propValue);
            }
            else {
                if (propValue.contains(",")){
                    String[] values = propValue.split(",");
                    for (int i = 0; i < values.length; i++){
                        values[i] = values[i].trim();
                    }
                    node.setProperty(propName, propName.equals("languages")?getLanguageValues(values):values);
                } else {
                    node.setProperty(propName, new String[]{propValue});
                }
            }
            session.save();
            Util.getInstance().publishNode(node);
        }catch (RepositoryException e){
            LOGGER.error(e.getMessage());
        }
    }

    private String[] getLanguageValues(String[] values){
        List<String> valsList = Arrays.asList(values);
        valsList.retainAll(Arrays.asList(LANGUAGES));
        String[] result = new String[valsList.size()];
        for (int i = 0; i < result.length; i++){
            result[i] = valsList.get(i);
        }
        return result;
    }

    @Override
    public int doStartTag() throws JspException {
        setJournalistProperty();
        return EVAL_BODY_INCLUDE;
    }

    public String getNodeId() {
        return nodeId;
    }

    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }

    public String getPropName() {
        return propName;
    }

    public void setPropName(String propName) {
        this.propName = propName;
    }

    public boolean isMultiValued() {
        return multiValued;
    }

    public void setMultiValued(boolean multiValued) {
        this.multiValued = multiValued;
    }

    public String getPropValue() {
        return propValue;
    }

    public void setPropValue(String propValue) {
        this.propValue = propValue;
    }
}
