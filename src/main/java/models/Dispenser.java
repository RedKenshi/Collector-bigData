package models;

/**
 * Created by Maxime on 19/11/2015.
 */
import java.util.Queue;

public class Dispenser extends Thread{

    private Administrator theAdministrator;
    private Task nextTask;
    private Queue<Task> queueTask;
    private boolean collectorNeeded;
    private Integer taskCount;

    public Dispenser(Administrator anAdministrator){
        super("dispenserThread");
        theAdministrator = anAdministrator;
        queueTask = theAdministrator.getQueue();
        System.out.println("Dispenser created");
        taskCount = 1;
        this.start();
    }

    @Override
    public void run(){
        distribute();
    }

    public void distribute(){
        nextTask = null;
        while(true){//Evite de planter
            while(theAdministrator.getSwitchState()){
                if(!queueTask.isEmpty()){
                    //System.out.println("\nITERATING ...");
                    int before = queueTask.size();
                    nextTask = queueTask.poll();
                    System.out.println("\n--------------TASK-EXTRACTED-------------\n" +
                            "Taille de la file : " + before + "->" + queueTask.size() +
                            "\n-----------------------------------------");
                    //nextTask.preview();
                    //System.out.println("-----------------------------------------");
                    collectorNeeded = true;
                    if(nextTask != null){
                        Collector aFreeCollector = null;
                        while(collectorNeeded){
                            aFreeCollector = theAdministrator.getAFreeCollector(nextTask.getRegion(),nextTask.getPriority());
                            if(aFreeCollector != null){
                                collectorNeeded = false;
                            }
                        }
                        aFreeCollector.setTaskFree(false);
                        Thread taskExecution = new Thread(aFreeCollector);
                        nextTask.setName("task-N°" + taskCount);
                        aFreeCollector.affectTask(nextTask);
                        taskExecution.start();
                        taskCount++;
                        nextTask = null;
                    }
                }
            }
        }
    }
}
