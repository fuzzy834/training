package jobs;

import org.jahia.services.content.*;
import org.jahia.services.content.decorator.JCRUserNode;
import org.jahia.services.scheduler.BackgroundJob;
import org.jahia.services.usermanager.JahiaUserManagerService;
import org.quartz.JobExecutionContext;

import javax.jcr.RepositoryException;
import javax.jcr.query.Query;

/**
 * Created by usersmile on 13.04.17.
 */
public class RemoveDisabledUsers extends BackgroundJob{

    private static final String USER_REF_PROPERTY = "userRef";

    private JahiaUserManagerService userManagerService = JahiaUserManagerService.getInstance();


    @Override
    public void executeJahiaJob(JobExecutionContext jobExecutionContext) throws Exception {
        JCRTemplate.getInstance().doExecuteWithSystemSession(new JCRCallback<Boolean>() {
            @Override
            public Boolean doInJCR(final JCRSessionWrapper session) throws RepositoryException {
                JCRNodeIteratorWrapper nodeIterator = getDisabledJournalists(session);
                if (nodeIterator.hasNext()) {
                    for (JCRNodeWrapper journalist : nodeIterator) {
                        JCRUserNode user = getDisabledUser(journalist, journalist.getSession());
                        userManagerService.deleteUser(user.getPath(), journalist.getSession());
                        journalist.remove();
                        journalist.getSession().save();

                    }
                }
                return true;
            }
        });
    }

    private JCRNodeIteratorWrapper getDisabledJournalists(JCRSessionWrapper session) throws RepositoryException {
        QueryManagerWrapper queryManager = session.getWorkspace().getQueryManager();
        return queryManager
                .createQuery("SELECT * FROM [trnt:journalist] WHERE [enabled]=false", Query.JCR_JQOM)
                .execute()
                .getNodes();
    }

    private JCRUserNode getDisabledUser(JCRNodeWrapper node, JCRSessionWrapper session) throws RepositoryException {
        QueryManagerWrapper queryManager = session.getWorkspace().getQueryManager();
        JCRNodeIteratorWrapper nodeIterator = queryManager
                .createQuery(String.format("SELECT * FROM [jnt:user] WHERE [jcr:uuid]='%s'", node.getPropertyAsString(USER_REF_PROPERTY)), Query.JCR_JQOM)
                .execute()
                .getNodes();
        return (JCRUserNode) nodeIterator.next();
    }

}
