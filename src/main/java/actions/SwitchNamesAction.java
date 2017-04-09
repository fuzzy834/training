package actions;

import org.jahia.bin.Action;
import org.jahia.bin.ActionResult;
import org.jahia.services.content.*;
import org.jahia.services.render.RenderContext;
import org.jahia.services.render.Resource;
import org.jahia.services.render.URLResolver;
import org.slf4j.Logger;

import javax.jcr.RepositoryException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Created by usersmile on 07.04.17.
 */
public class SwitchNamesAction extends Action {

    private static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(SwitchNamesAction.class);

    private static final String FIRST_NAME_PROPERTY = "judgeFirstName";
    private static final String LAST_NAME_PROPERTY = "judgeLastName";

    private static final String CONTENT_FOLDER_TYPE = "jnt:contentFolder";

    @Override
    public ActionResult doExecute(final HttpServletRequest req,
                                  final RenderContext renderContext,
                                  final Resource resource,
                                  JCRSessionWrapper session,
                                  Map<String, List<String>> parameters,
                                  final URLResolver urlResolver) throws Exception {

        return (ActionResult) JCRTemplate.getInstance().doExecuteWithSystemSession(new JCRCallback<Object>() {
            @Override
            public Object doInJCR(JCRSessionWrapper session) throws RepositoryException {

                JCRNodeWrapper node = session.getNodeByUUID(resource.getNode().getIdentifier());

                if(node.isNodeType(CONTENT_FOLDER_TYPE)) {
                    JCRNodeIteratorWrapper nodes = node.getNodes();

                    for (JCRNodeWrapper n : nodes) {
                        switchActionSequence(n, FIRST_NAME_PROPERTY, LAST_NAME_PROPERTY);
                    }

                }else {
                    switchActionSequence(node, FIRST_NAME_PROPERTY, LAST_NAME_PROPERTY);
                }

                return new ActionResult(HttpServletResponse.SC_OK);
            }
        });
    }

    private void publishChangesToLiveWorkspace(JCRNodeWrapper node){
        try {
            JCRPublicationService.getInstance().publishByMainId(node.getIdentifier(),
                    "default",
                    "live",
                    null,
                    true,
                    Collections.singletonList(""));
        }catch (RepositoryException e){
            LOGGER.error(e.getMessage());
        }
    }

    private void switchNameProperties(JCRNodeWrapper node, String propertyName1, String propertyName2){
        try {
            String propertyValue1 = node.getPropertyAsString(propertyName1);
            String propertyValue2 = node.getPropertyAsString(propertyName2);

            node.setProperty(propertyName1, propertyValue2);
            node.setProperty(propertyName2, propertyValue1);
        }catch (RepositoryException e){
            LOGGER.error(e.getMessage());
        }
    }

    private void switchActionSequence (JCRNodeWrapper node, String propertyName1, String propertyName2){
        try {
            switchNameProperties(node, propertyName1, propertyName2);
            publishChangesToLiveWorkspace(node);
            node.getSession().save();
        }catch (RepositoryException e){
            LOGGER.error(e.getMessage());
        }
    }

}
