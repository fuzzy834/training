package util;

import org.jahia.services.content.JCRNodeIteratorWrapper;
import org.jahia.services.content.JCRNodeWrapper;
import org.jahia.services.content.JCRPublicationService;
import org.jahia.services.content.JCRSessionWrapper;

import javax.jcr.RepositoryException;
import javax.jcr.query.Query;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by usersmile on 18.04.17.
 */
public class Util {

    private static JCRPublicationService publicationService = JCRPublicationService.getInstance();
    private static final String DEFAULT_WORKSPACE = "default";
    private static final String LIVE_WORKSPACE = "live";

    private Util() {
    }

    private static Util instance;

    public static Util getInstance() {
        if (instance == null){
            instance = new Util();
        }
        return instance;
    }

    public void publishNode(JCRNodeWrapper node) throws RepositoryException{
        publicationService.publish(Collections.singletonList(node.getIdentifier()),
                DEFAULT_WORKSPACE,
                LIVE_WORKSPACE,
                Collections.singletonList(""));
    }

    public void publishNodes(JCRNodeWrapper...nodes) throws RepositoryException{
        List<String> uuids = new ArrayList<>();
        for (JCRNodeWrapper node : nodes) {
            publishNode(node);
        }
    }

    public JCRNodeIteratorWrapper findNode(JCRSessionWrapper session,
                                                  String nodeType,
                                                  String selector,
                                                  String identifier) throws RepositoryException{
        return session
                .getWorkspace()
                .getQueryManager()
                .createQuery(
                        String.format("SELECT * FROM [%s] WHERE [%s]='%s'", nodeType, selector, identifier),
                        Query.JCR_JQOM)
                .execute()
                .getNodes();
    }

    public JCRNodeIteratorWrapper findNode(JCRSessionWrapper session,
                                           String nodeType,
                                           String selector,
                                           boolean identifier) throws RepositoryException{
        return session
                .getWorkspace()
                .getQueryManager()
                .createQuery(
                        String.format("SELECT * FROM [%s] WHERE [%s]=%s", nodeType, selector, identifier),
                        Query.JCR_JQOM)
                .execute()
                .getNodes();
    }
}
