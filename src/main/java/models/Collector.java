package models;

/**
 * Created by Maxime on 19/11/2015.
 */
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class Collector implements Runnable{

    private Administrator theAdministrator;
    private Key APIKey;
    private String region, name;
    private boolean taskFree, requestReady;
    private Integer id, rateLimit;
    private Task currentTask;
    private Integer delay;

    public Collector(Administrator anAdministrator, Integer anId, Key anAPIKey, String aRegion, String aName){
        name = aName;
        theAdministrator = anAdministrator;
        id = anId;
        delay = anAPIKey.getRateLimit();
        APIKey = anAPIKey;
        region = aRegion;
        taskFree = true;
        requestReady = true;
        currentTask = null;
    }

    public void showDetails(){
        System.out.println("Collector n°" + id + ", name : " + this.getName());
        System.out.println("Key : " + APIKey.getValue() + ", waiting for a task : " + taskFree + ", ready to request : " + requestReady + ", region : " + region);
    }

    public void affectTask(Task aTask){
        currentTask = aTask;
        currentTask.setLastReqTime(System.nanoTime());
    }

    @Override
    public void run(){
        //System.out.println("Task \"" + currentTask.getName() + "\" have been affected to " + this.getName() + ", dispenser : " + theAdministrator.getDispenser().getState()+ "");
        setTaskFree(false);
        currentTask.setStartTime();
        while(currentTask.isDone() == false){
            if(isRequestReady()){
                doARequest();
                currentTask.incReqDone();
                if(currentTask.getNbReqDone() == currentTask.getNbData()){
                    currentTask.setDone(true);
                }
            }
        }
        currentTask.setEndTime();
        currentTask.summary(this);
        setTaskFree(true);
    }

    public void doARequest(){
        setRequestReady(false);
        ScheduledThreadPoolExecutor exec = new ScheduledThreadPoolExecutor(1);
        exec.schedule(new Runnable() {
            public void run() {
                setRequestReady(true);
            }
        }, delay, TimeUnit.MILLISECONDS);
        currentTask.printTask(this);//--- REPLACE by API call HERE !!
        currentTask.setLastReqTime(System.nanoTime());
    }

    public boolean isTaskFree(){
        return taskFree;
    }

    public boolean matchTaskRegion(String aRegion){
        boolean match = false;
        if(region.equals(aRegion)){
            match = true;
        }
        return match;
    }

    public boolean isRequestReady(){
        return requestReady;
    }

    public void setTaskFree(boolean aState){
        taskFree = aState;
    }

    public void setRequestReady(boolean aState){
        requestReady = aState;
    }

    public String getRegion(){
        return region;
    }

    public String getName(){
        return name;
    }
}