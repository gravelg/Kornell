package kornell.core.lom;

import java.util.ArrayList;
import java.util.List;

public class ContentsOps {
    //TODO: bind directly to Contents.collectActoms
    public static List<Actom> collectActoms(Contents contents) {
        List<Actom> actoms = new ArrayList<Actom>();
        for (Content content:contents.getChildren()) {
            collectContent(content, actoms);
        }
        return actoms;
    }


    private static void collectTopic(Topic topic, List<Actom> actoms) {
        for (Content content : topic.getChildren()) {
            collectContent(content,actoms);
        }
    }

    private static void collectContent(Content content, List<Actom> actoms) {
        ContentFormat format = content.getFormat();
        //TODO: Replace if with switch
        if(ContentFormat.Topic.equals(format)){
            collectTopic(content.getTopic(),actoms);
        }else if(ContentFormat.ExternalPage.equals(format)){
            collectPage(content.getExternalPage(),actoms);
        }

    }


    private static void collectPage(ExternalPage externalPage,
            List<Actom> actoms) {
        actoms.add(externalPage);
    }


}
