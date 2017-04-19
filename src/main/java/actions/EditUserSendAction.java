package actions;

import org.jahia.api.Constants;
import org.jahia.bin.Action;
import org.jahia.bin.ActionResult;
import org.jahia.services.content.*;
import org.jahia.services.render.RenderContext;
import org.jahia.services.render.Resource;
import org.jahia.services.render.URLResolver;
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

    private static final String JOURNALIST_NODE_TYPE = "trnt:journalist";
    private static final String NODENAME_PROPERTY = "j:nodename";
    private static final String LAGUAGES = "german, french, italian";
    private static final String LANGUAGE_PROPERTY = "languages";

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

                if(validate(parameters, journalist)) {
                    //set password
                    if (!parameters.get("newPassword").get(0).isEmpty()
                            && !parameters.get("confirmNewPassword").get(0).isEmpty()){
                            journalist.setProperty("password", parameters.get("newPassword").get(0));
                    }
                    //set other props
                    StringBuilder languages = new StringBuilder();
                    for (Map.Entry<String, List<String>> pair : parameters.entrySet()) {
                        //acquiring new language set
                        if (LAGUAGES.contains(pair.getKey())) {
                            languages.append(pair.getValue().get(0));
                            languages.append(" ");
                        }
                        //setting other available props
                        if (journalist.hasProperty(pair.getKey())) {
                            journalist.setProperty(pair.getKey(), pair.getValue().get(0));
                        }
                    }
                    //setting languages
                    journalist.setProperty(LANGUAGE_PROPERTY, languages.toString().trim().split(" "));

                    //saving and publishing changes
                    journalist.getSession().save();
                    JCRPublicationService.getInstance().publishByMainId(journalist.getIdentifier(), Constants.EDIT_WORKSPACE, Constants.LIVE_WORKSPACE, null, true, Collections.singletonList(""));
                }

                return new ActionResult(HttpServletResponse.SC_OK);
            }
        });
    }

    private boolean validate(Map<String, List<String>> parameters, JCRNodeWrapper node){
        boolean zip = parameters.get("npa").get(0).matches("^[0-9]{5}$");
        boolean email = parameters.get("email").get(0)
                .matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}$");
        boolean password = parameters.get("newPassword").get(0)
                .equals(parameters.get("confirmNewPassword").get(0))
                && !parameters.get("newPassword").get(0)
                .equals(node.getPropertyAsString("password"));

        return email && zip && password;
    }
}
