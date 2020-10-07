import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Stack;

class Main {

    public static void main(String[] args) throws IOException {

        // Read input
        String filename = args[0];
        Scanner in = new Scanner(new File(filename));

        int n = in.nextInt();
        Task[] tasks = new Task[n];

        for (int i = 0; i < n; i++) {
            tasks[i] = new Task(i + 1);
        }

        for (int i = 0; i < n; i++) {
            int id = in.nextInt();
            Task task = tasks[id - 1];
            task.name = in.next();
            task.time = in.nextInt();
            task.staff = in.nextInt();

            while (true) {
                int dep = in.nextInt();
                if (dep == 0) {
                    break;
                }
                tasks[dep - 1].addEdge(task);
                tasks[id - 1].cntPredecessors++;
            }
        }

        // Find criticalPath
        tasks = criticalPath(tasks);
        int maxTime = maxTime(tasks);

        // Print project
        printProject(tasks, maxTime);

        // Print task info
        for (Task task : tasks) {
            System.out.println("----Task " + task.id + "----");
            System.out.println(String.format("Name:%s \nTime: %s \nManpower: %s \nEarliest time: %s \nSlack: %s",
                    task.name, task.time, task.staff, task.earliestStart, (task.latestStart - task.earliestStart)));
            System.out.println("Dependendant task(s):");
            for (Task depTask : task.outEdges) {
                System.out.println(depTask.id);
            }
        }

    }

    private static Task[] criticalPath(Task[] taskInput) {
        // Topological sorting
        Task[] output = new Task[taskInput.length];
        Stack<Task> S = new Stack<Task>();
        for (Task task : taskInput) {
            if (task.cntPredecessors == 0) {
                task.criticalTime = task.time;
                task.earliestStart = 0;
                S.push(task);
            }
        }
        int i = 0;
        Task potentialCycleNode = null;
        while (!S.empty()) {
            Task currentTask = S.pop();
            output[i] = currentTask;
            i++;
            for (Task depTask : currentTask.outEdges) {
                depTask.cntPredecessors--;
                if (depTask.criticalTime - depTask.time < currentTask.criticalTime) {
                    depTask.criticalTime = currentTask.criticalTime + depTask.time;
                    depTask.earliestStart = currentTask.criticalTime;
                }

                if (depTask.cntPredecessors == 0) {
                    S.push(depTask);
                } else {
                    potentialCycleNode = depTask;
                }
            }
        }
        if (i < taskInput.length) {
            throw new RuntimeException("Cycle. Startnode is :" + potentialCycleNode);

        }
        return output;
    }

    private static int maxTime(Task[] tasks) {
        int max = -1;

        for (Task task : tasks) {
            if (max < task.criticalTime) {
                max = task.criticalTime;
            }
        }

        // Set latest based on longest path
        for (int i = tasks.length - 1; i >= 0; i--) {
            if (tasks[i].outEdges.isEmpty()) {
                tasks[i].latestStart = max - tasks[i].time;
            } else {
                tasks[i].latestStart = tasks[i].outEdges.get(0).latestStart - tasks[i].time;
                for (Task edgeTask : tasks[i].outEdges) {
                    if (edgeTask.latestStart - tasks[i].time < tasks[i].latestStart) {
                        tasks[i].latestStart = edgeTask.latestStart - tasks[i].time;
                    }
                }
            }
        }
        return max;
    }

    private static void printProject(Task[] tasks, int maxTime) {
        int manpower = 0;
        for (int time = 0; time <= maxTime; time++) {
            boolean print = false;
            ArrayList<Integer> starting = new ArrayList<Integer>();
            ArrayList<Integer> finishing = new ArrayList<Integer>();

            for (Task task : tasks) {
                if (task.earliestStart == time) {
                    starting.add(task.id);
                    manpower += task.staff;
                    print = true;
                }
                if ((task.earliestStart + task.time) == time) {
                    finishing.add(task.id);
                    manpower -= task.staff;
                    print = true;
                }
            }

            if (print) {
                System.out.println("\n\nTime: " + time);
                for (Integer integer : starting) {
                    System.out.println("Starting task: " + integer);
                }
                for (Integer integer : finishing) {
                    System.out.println("Finished task: " + integer);
                }
                System.out.println("Current manpower: " + manpower);
            }
        }
        System.out.println("-_-_-_-_-_-_-_-_-_-_-_-_-_-_-\n" + "Total and shortest time for the project completion is "
                + maxTime + "\n\n");

    }

}