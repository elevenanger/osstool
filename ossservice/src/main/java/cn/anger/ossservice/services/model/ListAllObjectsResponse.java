package cn.anger.ossservice.services.model;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author : anger
 */
public class ListAllObjectsResponse extends CliResponse {
    private final List<ObjectSummary> objectSummaryList = new ArrayList<>();
    private int count;

    public List<ObjectSummary> getObjectSummaryList() {
        return objectSummaryList;
    }

    public void setObjectSummaryList(List<ObjectSummary> objectSummaries) {
        objectSummaryList.addAll(objectSummaries);
        setCount(objectSummaries.size());
    }

    public int getCount() {
        return count;
    }

    private void setCount(int count) {
        this.count = count;
    }

    @Override
    public String toString() {
        return "ListAllObjectsResponse : \n" +
                "objectSummaryList : \n" +
                objectSummaryList.stream()
                    .map(ObjectSummary::toString)
                    .collect(Collectors.joining("\n")) +
                "\ntotal count : " + count ;
    }
}
