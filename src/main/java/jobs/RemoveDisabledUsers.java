package jobs;

import org.jahia.services.content.*;
import org.jahia.services.content.decorator.JCRUserNode;
import org.jahia.services.scheduler.BackgroundJob;
import org.jahia.services.usermanager.JahiaUserManagerService;
import org.quartz.JobExecutionContext;
import util.Util;

import javax.jcr.RepositoryException;

/**
 * Created by usersmile on 13.04.17.
 */
public class RemoveDisabledUsers extends BackgroundJob{

    private static final String USER_REF_PROPERTY = "userRef";
    private static final String UUID_PROPERTY = "jcr:uuid";
    private static final String ENABLED_PROPERTY = "enabled";

    private static final String USER_NODE_TYPE = "jnt:user";
    private static final String JOURNALIST_NODE_TYPE = "trnt:journalist";


    private JahiaUserManagerService userManagerService = JahiaUserManagerService.getInstance();
    private Util util = Util.getInstance();

    @Override
    public void executeJahiaJob(JobExecutionContext jobExecutionContext) throws Exception {
        JCRTemplate.getInstance().doExecuteWithSystemSession(new JCRCallback<Boolean>() {
            @Override
            public Boolean doInJCR(final JCRSessionWrapper session) throws RepositoryException {
                JCRNodeIteratorWrapper nodeIterator = util.findNode(session, JOURNALIST_NODE_TYPE, ENABLED_PROPERTY, false);
                if (nodeIterator.hasNext()) {
                    for (JCRNodeWrapper journalist : nodeIterator) {
                        JCRUserNode user = (JCRUserNode) util.findNode(session, USER_NODE_TYPE, UUID_PROPERTY, journalist.getPropertyAsString(USER_REF_PROPERTY)).next();
                        userManagerService.deleteUser(user.getPath(), session);
                        journalist.remove();
                        removeJournalistFromLiveWorkspace(journalist);
                        session.save();
                    }
                }
                return true;
            }
        });
    }

    private void removeJournalistFromLiveWorkspace(JCRNodeWrapper node) throws RepositoryException {
        JCRSessionWrapper session = JCRSessionFactory.getInstance().getCurrentSystemSession("live", null, null);
        JCRNodeIteratorWrapper nodeIterator = util.findNode(session, JOURNALIST_NODE_TYPE, UUID_PROPERTY, node.getIdentifier());
        if (nodeIterator.hasNext()) {
            JCRNodeWrapper journalist = (JCRNodeWrapper) nodeIterator.next();
            journalist.remove();
            session.save();
        }
    }
}
