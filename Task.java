import java.util.ArrayList;
import java.util.List;

class Task {
    int id, time, staff;
    int criticalTime;
    String name;
    int earliestStart, latestStart;
    List<Task> outEdges = new ArrayList<Task>();
    int cntPredecessors;

    public Task(int paramId) {
        id = paramId;
    }

    public void addEdge(Task task) {
        outEdges.add(task);
    }
}