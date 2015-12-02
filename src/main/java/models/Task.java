package models;

/**
 * Created by Maxime on 19/11/2015.
 */
import java.util.ArrayList;

public class Task {

    private Integer nbReqDone;
    private String target, identifier, region, name;
    private ArrayList<String> elements;
    private boolean TaskDone;
    private long startTime, lastReqTime, endTime;

    public Task(String aTarget, String anIdentifier, String aRegion){
        target = aTarget;
        identifier = anIdentifier;
        region = aRegion;
        elements = new ArrayList<String>();
        TaskDone = false;
        nbReqDone = 0;
    }

    public void summary(Collector aCollector){
        long execTime = (endTime - startTime);
        int average = (int) (execTime / nbReqDone) / 1000000;
        execTime = execTime/1000000;
        String outputSummary = "\n-------------END-OF-TASK-------------\n" + "End of task : " + name + " by " + aCollector.getName() + "\n" + nbReqDone + " request done, in " + execTime + " ms, average : " + average + "ms/req" + "\n" + "------------------------------------\n";
        System.out.println(outputSummary);
    }

    public void setEndTime(){
        endTime = System.nanoTime();
    }

    public void setStartTime(){
        startTime = System.nanoTime();
    }

    public void setLastReqTime(long aTime){
        lastReqTime = aTime;
    }

    public long getLastReqTime(){
        return lastReqTime;
    }

    public int getNbReqDone(){
        return nbReqDone;
    }

    public void incReqDone(){
        nbReqDone++;
    }

    public boolean isDone(){
        return TaskDone;
    }

    public void setDone(boolean statut){
        TaskDone = statut;
    }

    public int getNbData(){
        return elements.size();
    }

    public void setName(String aName){
        name = aName;
    }

    public String getName(){
        return name;
    }

    public String getTarget(){
        return target;
    }

    public String getRegion(){
        return region;
    }

    public String getData(Integer index){
        return elements.get(index);
    }

    public void addData(String aData){
        elements.add(aData);
    }

    public void printTask(Collector unColl){
        //long timeFromStart = (System.nanoTime() - startTime)/1000000000;
        //long timeElapsed = (System.nanoTime() - lastReqTime)/1000000;
        //System.out.println("[" + name + ":" + nbReqDone + "] Req : " + target + " by " + identifier + ". Id : " + elements.get(nbReqDone) + " dans : " + unColl.getName() + " time : " + timeFromStart + "s (+" + timeElapsed + "ms)");
    }

    public void preview(){
        System.out.println("PREVIEW : " + name + " req " + target + " by " + identifier + " in region \"" + region + "\". elements : " + elements.size());
    }
}
