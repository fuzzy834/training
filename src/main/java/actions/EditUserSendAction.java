package actions;

import org.jahia.bin.Action;
import org.jahia.bin.ActionResult;
import org.jahia.services.content.*;
import org.jahia.services.render.RenderContext;
import org.jahia.services.render.Resource;
import org.jahia.services.render.URLResolver;
import org.slf4j.Logger;
import util.Util;

import javax.jcr.RepositoryException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Created by usersmile on 17.04.17.
 */
public class EditUserSendAction extends Action {

    private Util util = Util.getInstance();

    private static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(EditUserSendAction.class);
    private static final String JOURNALIST_NODE_TYPE = "trnt:journalist";
    private static final String NODENAME_PROPERTY = "j:nodename";

    @Override
    public ActionResult doExecute(final HttpServletRequest req,
                                  final RenderContext renderContext,
                                  final Resource resource,
                                  JCRSessionWrapper session,
                                  final Map<String, List<String>> parameters,
                                  final URLResolver urlResolver) throws Exception {
        return (ActionResult) JCRTemplate.getInstance().doExecuteWithSystemSession(new JCRCallback<Object>() {
            @Override
            public Object doInJCR(JCRSessionWrapper session) throws RepositoryException {
                JCRNodeWrapper user = resource.getNode();
                JCRNodeWrapper journalist = (JCRNodeWrapper) util.findNode(session, JOURNALIST_NODE_TYPE, NODENAME_PROPERTY, user.getPropertyAsString(NODENAME_PROPERTY)).next();
                for (Map.Entry<String, List<String>> pair : parameters.entrySet()){
                    if (journalist.hasProperty(pair.getKey())){
                        journalist.setProperty(pair.getKey(), pair.getValue().get(0));
                    }
                }
                journalist.getSession().save();
                JCRPublicationService.getInstance().publishByMainId(journalist.getIdentifier(), "default", "live", null, true, Collections.singletonList(""));

                return new ActionResult(HttpServletResponse.SC_OK);
            }
        });
    }
}
