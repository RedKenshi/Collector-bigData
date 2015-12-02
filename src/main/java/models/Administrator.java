package models;

/**
 * Created by Maxime on 19/11/2015.
 */

import controller.Receiver;
import controller.Sender;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;


/*An administrator object handle a set of keys, it is communicating
with externals applications by the way of an amqp server (in/out). He is also
communicating with a set of collectors by the way of a waiting queue (dispenser).*/
public class Administrator {

    private static Administrator instance;

    private ArrayList<String> regions;
    private ArrayList<Collector> collectors;
    private ArrayList<Key> APIkeys;
    private Integer nbKeys;
    private Sender sender;
    private Receiver receiver;
    private Queue<Task> taskQueue;
    private Dispenser theDispenser;
    private boolean switchOnOff;

    private Administrator(){
        nbKeys = 0;
        APIkeys = new ArrayList<Key>();
        collectors = new ArrayList<Collector>();
        regions = new ArrayList<String>(Arrays.asList("br","eune","euw","kr","lan","las","na","oce","tr","ru"));
        sender = new Sender(this);
        receiver = new Receiver(this);
        taskQueue = new LinkedList<Task>();
        switchOnOff = true;
        theDispenser = new Dispenser(this);
        sender.testSend();
    }

    // return the only one instance of the class
    public static Administrator get(){
        if(instance == null){
            instance = new Administrator();
        }
        return instance;
    }

    //first method to get called when a message come from amqp server
    //USING A SWITCH
    //identifiate nature of the message :
    // - a request that will use a collector and an API call
    // - a modification on the application's configuration
    public void taskReceived(String aReceivedStream) {
        JSONObject jsonReceivedStream = new JSONObject(aReceivedStream);
        JSONObject jsonRequest = jsonReceivedStream.getJSONObject("request");
        String taskType = jsonRequest.getString("type");
        String taskTarget = jsonRequest.getString("target");

        switch(taskType){
            case "request" :
                handleRequest(jsonRequest, taskTarget);
                break;
            case "config" :
                handleConfig(jsonRequest, taskTarget);
                break;
        }
    }

    //method called to handle request that will use a collector and an API call
    public void handleRequest(JSONObject jsonRequest, String taskTarget) throws JSONException{
        //System.out.println("REQUETE SUR DES SUMMONERS :");
        String taskIdentifier = jsonRequest.getString("identifier");
        String taskRegion = jsonRequest.getString("region");
        String taskPriority = jsonRequest.getString("priority");
        Task aTask = new Task(taskTarget,taskIdentifier,taskRegion,taskPriority);
        JSONArray jsonDataArray =  jsonRequest.getJSONArray("data");
        for(int i=0;i<jsonDataArray.length();i++) {
            JSONObject jsonData = jsonDataArray.getJSONObject(i);
            String aData = jsonData.getString("ident");
            aTask.addData(aData);
        }
        queueTask(aTask);
    }

    //method called to handle modification on the application's configuration
    //like keys, collector's affectation
    //USING A SWITCH
    public void handleConfig(JSONObject jsonRequest, String taskTarget) throws JSONException{
        switch(taskTarget){
            case "keys":
                System.out.println("CLÉES RECUES !");
                JSONArray jsonKeyArray = jsonRequest.getJSONArray("keys");
                generateKeys(jsonKeyArray);
                //showKeys();
                break;
            case "collectors":
                System.out.println("GENERATION DES COLLECTEURS !");
                generateCollectors();
                //showCollectors();
                break;
        }
    }

    //getter of the application on/off switch
    public boolean getSwitchState(){
        return switchOnOff;
    }

    //setter of the application on/off switch
    public void setSwitchState(boolean aState){
        switchOnOff = aState;
    }

    //getter of the task waiting queue
    public Queue<Task> getQueue(){
        return taskQueue;
    }

    //method used to add a task to the waiting queue
    public void queueTask(Task aTask){
        //System.out.println("Mise en attente d'une tache");
        taskQueue.add(aTask);
    }

    //generate keys from a valid JSONArray object
    //each object key must have following attribute :
    //"value" the value of the key
    //"rateLimit" the delay in ms between to apicall for the key
    public void generateKeys(JSONArray jsonKeyArray) throws JSONException{
        for(int i=0;i<jsonKeyArray.length();i++) {
            JSONObject aJsonKey = jsonKeyArray.getJSONObject(i);
            Key aKey = new Key(aJsonKey.getString("value"),aJsonKey.getInt("rateLimit"),aJsonKey.getString("purpose"));
            APIkeys.add(aKey);
            nbKeys++;
        }
        System.out.println(APIkeys.size() + " clées générées");
    }

    //generate the collector set from keys and regions
    //must be called AFTER the keys generation
    public void generateCollectors(){
        for (int i = 0; i < nbKeys;i++){
            String aName = "collector"+i;
            for(String aRegion : regions){
                Collector aCollector = new Collector(this,i,APIkeys.get(i),aRegion,aName);
                collectors.add(aCollector);
            }
        }
        System.out.println(collectors.size() + " collecteurs générés");
    }

    //display keys
    public void showKeys(){
        System.out.println("--------------------\n--------KEYS-----(" + APIkeys.size() + ")\n--------------------");
        for (int i = 0; i < APIkeys.size();i++){
            APIkeys.get(i).showDetails();
        }
    }

    //display collectors
    public void showCollectors(){
        System.out.println("--------------------\n--------COLL-----(" + collectors.size() + ")\n--------------------");
        for (int i = 0; i < collectors.size();i++){
            collectors.get(i).showDetails();
        }
    }

    //return a taskFree collector in the set matching the given region
    public Collector getAFreeCollector(String aRegion, String aPurpose){
        Collector aFreeCollector;
        aFreeCollector = null;
        for (Collector aCollector : collectors) {
            if(aCollector.matchTaskPurpose(aPurpose)){
                if(aCollector.matchTaskRegion(aRegion)){
                    if(aCollector.isTaskFree()){
                        aFreeCollector = aCollector;
                        break;
                    }
                }
            }
        }
        return aFreeCollector;
    }

    public Dispenser getDispenser(){
        return theDispenser;
    }
}