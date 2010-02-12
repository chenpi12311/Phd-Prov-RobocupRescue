package org.openprovenance.model;
import java.io.File;
import java.io.StringWriter;
import java.util.Collection;
import java.util.Date;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import javax.xml.bind.JAXBException;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
// added by Iman
import java.util.ArrayList;


/**
* Unit test for simple Robocup OPM Graph
* There will be methods that will add to the sets of artifacts, processes, 
* 		agents, and objects (represent edges).
* Each of the methos will represent an action that can be made by 
* 		the Ambulance agent
* These methods will return two objects: the initial state (artifact) that was 
* 		the cause of the action happening and the action (process) that happened
* The main Graph maker will call each of the methods and form parts of the 
* 		graph then link them together in one big graph
*/
public class RobocupTestOneAmbAgent
    extends TestCase
{
 
    static public OPMFactory oFactory=new OPMFactory();
    
    //added by Iman so that sets of Artifacts, Processes, Agents, and 
    //	Objects (edges) are formed
    private Collection<Artifact> artifacts = new ArrayList<Artifact>();
    private Collection<Process> processes = new ArrayList<Process>();
    private Collection<Agent> agents = new ArrayList<Agent>();
    private Collection<Object> objects = new ArrayList<Object>();
    // added by Iman to append to the IDs of the artifacts and processes of the 
    // actions that can be repeated
    private int artfsCount = 0;
    private int procsCount = 0;
    
    /**
	* Create the test case
	*
	* @param testName name of the test case
	*/
    public RobocupTest( String testName )
    {
        super( testName );
    }
 
    static OPMGraph graph1;
  
    public void testRobocup() throws JAXBException, InterruptedException 
    {    	
        OPMGraph graph=makeRobocupGraph(oFactory);
 
        OPMSerialiser serial=OPMSerialiser.getThreadOPMSerialiser();
        serial.serialiseOPMGraph(new File("target/robocup.xml"),graph,true);
 
        graph1=graph;
        System.out.println("Robocup Test asserting True");
        assertTrue( true );
    }
 
 
    public OPMGraph makeRobocupGraph(OPMFactory oFactory) throws InterruptedException
    {
        Collection<Account> black=Collections.singleton(oFactory.newAccount("black"));
        Agent ag1 = oFactory.newAgent("ag1",black,"Agent 1");
        agents.add(ag1);
        
        //get the nodes and edges resulting from unloading the civilian
        Artifact ambulanceUnloadArt  = makeRobocupAmbulanceUnload(oFactory, black, ag1);
        
        //get the nodes and edges resulting from moving the civilian
        Object[] ambulanceMoveWithCivilian = makeRobocupAmbulanceMoveWithCivilian(oFactory, black, ag1);
        Artifact ambulanceMoveWithCivilianArt = (Artifact) ambulanceMoveWithCivilian[0];
        Process  ambulanceMoveWithCivilianProc = (Process) ambulanceMoveWithCivilian[1];
        // Now, link the artifact that influenced the action of one step to the process (action) of the previous state that resulted in it  
        WasGeneratedBy wgUnloadArt=oFactory.newWasGeneratedBy(ambulanceUnloadArt,oFactory.newRole("result of action"),ambulanceMoveWithCivilianProc,black);
        objects.add(wgUnloadArt);
        
        //get the nodes and edges resulting from loading the civilian
        Object[] ambulanceLoadCivilian = makeRobocupAmbulanceLoadCivilian(oFactory, black, ag1);
        Artifact ambulanceLoadCivilianArt = (Artifact)ambulanceLoadCivilian[0];
        Process ambulanceLoadCivilianProc = (Process)ambulanceLoadCivilian[1];
        // Now, link the artifact that influenced the action of one step to the process (action) of the previous state that resulted in it
        WasGeneratedBy wgMoveCivArt=oFactory.newWasGeneratedBy(ambulanceMoveWithCivilianArt,oFactory.newRole("result of action"),ambulanceLoadCivilianProc,black);
        objects.add(wgMoveCivArt);
        
        //get the nodes and edges resulting from unburrying the civilian
        Object[] ambulanceUnburyCivilian = makeRobocupAmbulanceUnburyCivilian(oFactory, black, ag1);
        Artifact ambulanceUnburyCivilianArt = (Artifact)ambulanceUnburyCivilian[0];
        Process ambulanceUnburyCivilianProc = (Process)ambulanceUnburyCivilian[1];
        //Now, link the artifact that influenced the action of one step to the process (action) of the previous state that resulted in it
        WasGeneratedBy wgLoadArt=oFactory.newWasGeneratedBy(ambulanceLoadCivilianArt,oFactory.newRole("result of action"),ambulanceUnburyCivilianProc,black);
        objects.add(wgLoadArt);
        
        //get the nodes and edges resulting from unburrying the civilian - 2nd iteration
        Object[] ambulanceUnburyCivilian2 = makeRobocupAmbulanceUnburyCivilian(oFactory, black, ag1);
        Artifact ambulanceUnburyCivilianArt2 = (Artifact)ambulanceUnburyCivilian2[0];
        Process ambulanceUnburyCivilianProc2 = (Process)ambulanceUnburyCivilian2[1];
        //Now, link the artifact that influenced the action of one step to the process (action) of the previous state that resulted in it
        WasGeneratedBy wgLoadArt2=oFactory.newWasGeneratedBy(ambulanceUnburyCivilianArt,oFactory.newRole("result of action"),ambulanceUnburyCivilianProc2,black);
        objects.add(wgLoadArt2);
        
        //get the nodes and edges resulting from moving without the civilian
        Object[] ambulanceMoveWithoutCivilian = makeRobocupAmbulanceMoveWithoutCivilian(oFactory, black, ag1);
        Artifact ambulanceMoveWithoutCivilianArt = (Artifact)ambulanceMoveWithoutCivilian[0];
        Process ambulanceMoveWithoutCivilianProc = (Process)ambulanceMoveWithoutCivilian[1];
        //Now, link the artifact that influenced the action of one step to the process (action) of the previous state that resulted in it
        WasGeneratedBy wgUnburyArt=oFactory.newWasGeneratedBy(ambulanceUnburyCivilianArt2,oFactory.newRole("result of action"),ambulanceMoveWithoutCivilianProc,black);
        objects.add(wgUnburyArt);
        
        //get the nodes and edges resulting from moving without the civilian - 2nd iteration
        Object[] ambulanceMoveWithoutCivilian2 = makeRobocupAmbulanceMoveWithoutCivilian(oFactory, black, ag1);
        Artifact ambulanceMoveWithoutCivilianArt2 = (Artifact)ambulanceMoveWithoutCivilian2[0];
        Process ambulanceMoveWithoutCivilianProc2 = (Process)ambulanceMoveWithoutCivilian2[1];
        
        WasGeneratedBy wgMoveWithoutCivilian2=oFactory.newWasGeneratedBy(ambulanceMoveWithoutCivilianArt,oFactory.newRole("result of previous move"),ambulanceMoveWithoutCivilianProc2,black);
        objects.add(wgMoveWithoutCivilian2);
        
 /*     OTime otime0=oFactory.newInstantaneousTimeNow(); // testing with now
        Thread.sleep(1000);
         
        Date date2=new Date(); //get the current time
         
        OTime otime1=oFactory.newOTime("2010-01-15T08:25:03.204Z", "2010-01-15T08:25:05.204Z"); // testing with a string date
        u1.setTime(otime1);
         
        OTime otime3=oFactory.newInstantaneousTimeNow(); // testing with a now

        wc1.setStartTime(otime0);
        wc1.setEndTime(otime3);
 
 */
        
        OPMGraph graph=oFactory.newOPMGraph(black,null,processes,artifacts,agents,objects,null);

        return graph;
    }
    
 
    /** Produces a dot representation
     * of created graph. */
    public void testRobocupConversion() throws java.io.FileNotFoundException, java.io.IOException 
    {
        OPMToDot toDot=new OPMToDot(true); // with roles
        toDot.convert(graph1,"target/robocup.dot", "target/robocup.pdf");
    }
    
    /** Produce the artifacts and processes that make up the Action: Unload Civilian
     * Returns only the artifact (state) to be liked to the previous action resulting in it
     */
    public  Artifact makeRobocupAmbulanceUnload(OPMFactory oFactory, Collection<Account> accountColor, Agent agent) throws InterruptedException
    {    	
    	Collection<Account> black = accountColor;
    	Agent ag1 = agent;
    	
    	Artifact c1Rescued = oFactory.newArtifact("c1Rescued", black, "Civilian C1 Rescued");
    	artifacts.add(c1Rescued);
    	
        Process unload = oFactory.newProcess("unload", black, "Unload Civilian");
        processes.add(unload);
        
        WasGeneratedBy wg1=oFactory.newWasGeneratedBy(c1Rescued,oFactory.newRole("final step"),unload,black);
        objects.add(wg1);
        
        WasControlledBy wc1=oFactory.newWasControlledBy(unload,oFactory.newRole("Agent"),ag1,black);
        objects.add(wc1);
        
        Artifact ag1state1=oFactory.newArtifact("a1state1",black,"Agent state: has civilian & at refuge centre");
        artifacts.add(ag1state1);
        
        Used u1=oFactory.newUsed(unload,oFactory.newRole("final state"),ag1state1,black);
        objects.add(u1);
    	              
    	return ag1state1;
    }
    
    /** Produce the artifacts and processes that make up the Action: Move Carrying Civilian
     * Returns the artifact (state) to be liked to the previous action resulting in it
     * Also Returns the process (action) moveWithCivilian
     */
    public  Object[] makeRobocupAmbulanceMoveWithCivilian(OPMFactory oFactory, Collection<Account> accountColor, Agent agent) throws InterruptedException
    {    	
    	Collection<Account> black = accountColor;
    	Agent ag1 = agent;
    	
    	ArrayList procs = new ArrayList<Process>();
    	ArrayList artfs = new ArrayList<Artifacts>();
    	
    	procs.add(oFactory.newProcess("moveWithCivilian"+procsCount, black, "Move (Carrying Civilian)")); //moveWithCivilian
    	procsCount++;
    	processes.add((Process)procs.get(0));
    	 
    	WasControlledBy wc2=oFactory.newWasControlledBy((Process)procs.get(0),oFactory.newRole("Agent"),ag1,black);
    	objects.add(wc2);
    	
        artfs.add(oFactory.newArtifact("returnedPathFinal"+artfsCount,black,"Returned Path")); //returnedPathFinal
        artfsCount++;
        artifacts.add((Artifact) artfs.get(0));
        
        Used u2=oFactory.newUsed((Process)procs.get(0),oFactory.newRole("moveWithCivilian"),(Artifact) artfs.get(0),black);
        objects.add(u2);
        
        artfs.add(oFactory.newArtifact("a2state2"+artfsCount,black,"Agent state: has civilian & NOT at refuge centre")); //ag2state2
        artfsCount++;
        artifacts.add((Artifact)artfs.get(1));
        
        WasDerivedFrom wd1=oFactory.newWasDerivedFrom((Artifact) artfs.get(0),(Artifact)artfs.get(1),black);
        objects.add(wd1);
    	
    	return  new Object[]{(Artifact)artfs.get(1),(Process)procs.get(0)};
    }
    
    /** Produce the artifacts and processes that make up the Action: Load Civilian
     * Returns the artifact (state) to be liked to the previous action resulting in it
     * Also Returns the process (action) loadCivilian
     */
    public  Object[] makeRobocupAmbulanceLoadCivilian(OPMFactory oFactory, Collection<Account> accountColor, Agent agent) throws InterruptedException
    {
    	
    	Collection<Account> black = accountColor;
    	Agent ag1 = agent;
    	
    	Process loadCivilian = oFactory.newProcess("loadCivilian", black, "Load Civilian");
    	processes.add(loadCivilian);
    	
    	WasControlledBy wc3=oFactory.newWasControlledBy(loadCivilian,oFactory.newRole("Agent"),ag1,black);
        objects.add(wc3);
    	        
        Artifact ag1state3=oFactory.newArtifact("ag1state3",black,"Agent state: Civilian is agent's top priority, agent at same position of civilian, civilian is unburied");
        artifacts.add(ag1state3);
        Artifact returnedListCivilianListFinal=oFactory.newArtifact("returnedListCivilianListFinal",black,"Returned Sorted List of Civilians");
        artifacts.add(returnedListCivilianListFinal);
        Artifact ag1state4=oFactory.newArtifact("ag1state4",black,"Agent state: Agent does not have any civilians & target list is not empty");
        artifacts.add(ag1state4);
        
        Used u3=oFactory.newUsed(loadCivilian,oFactory.newRole("LoadCivilian"),ag1state3,black);
        objects.add(u3);
        WasDerivedFrom wd2=oFactory.newWasDerivedFrom(ag1state3,returnedListCivilianListFinal,black);
        objects.add(wd2);
        WasDerivedFrom wd3=oFactory.newWasDerivedFrom(returnedListCivilianListFinal,ag1state4,black);
        objects.add(wd3);
        
        return  new Object[]{ag1state4,loadCivilian};
    }
    
    /** Produce the artifacts and processes that make up the Action: Unbury Civilian
     * Returns the artifact (state) to be liked to the previous action resulting in it
     * Also Returns the process (action) unburyCivilian
     */
    public  Object[] makeRobocupAmbulanceUnburyCivilian(OPMFactory oFactory, Collection<Account> accountColor, Agent agent) throws InterruptedException
    {
    	Collection<Account> black = accountColor;
    	Agent ag1 = agent;
    	
    	ArrayList procs = new ArrayList<Process>();
    	ArrayList artfs = new ArrayList<Artifacts>();
    	
	    procs.add(oFactory.newProcess("unburyCivilian"+ procsCount, black, "Unbury Civilian")); //unburyCivilian
	    procsCount++;
	    processes.add((Process)procs.get(0));
	    WasControlledBy wc4=oFactory.newWasControlledBy((Process)procs.get(0),oFactory.newRole("Agent"),ag1,black);
	    objects.add(wc4);
	    
	    artfs.add(oFactory.newArtifact("ag1state5"+artfsCount,black,"Agent state: Civilian is agent's top priority, agent at same position of civilian, civilian is buried")); //ag1state5
	    artfsCount++;
	    artifacts.add((Artifact)artfs.get(0));
	    artfs.add(oFactory.newArtifact("returnedListCivilianList1"+artfsCount,black,"Returned Sorted List of Civilians")); //returnedListCivilianList1
	    artfsCount++;
	    artifacts.add((Artifact)artfs.get(1));
	    artfs.add(oFactory.newArtifact("ag1state6"+artfsCount,black,"Agent state: Agent does not have any civilians & target list is not empty")); //ag1state6
	    artfsCount++;
	    artifacts.add((Artifact)artfs.get(2));
	    Used u4=oFactory.newUsed((Process)procs.get(0),oFactory.newRole("UnburyCivilian"),(Artifact)artfs.get(0),black);
	    objects.add(u4);
	    WasDerivedFrom wd4=oFactory.newWasDerivedFrom((Artifact)artfs.get(0),(Artifact)artfs.get(1),black);
	    objects.add(wd4);
	    WasDerivedFrom wd5=oFactory.newWasDerivedFrom((Artifact)artfs.get(1),(Artifact)artfs.get(2),black);
	    objects.add(wd5);
	    
	    return  new Object[]{(Artifact)artfs.get(2),(Process)procs.get(0)};
    }
    
    /** Produce the artifacts and processes that make up the Action: Move Without Civilian
     * Returns the artifact (state) to be liked to the previous action resulting in it
     * Also Returns the process (action) moveWithoutCivilian
     */
    public  Object[] makeRobocupAmbulanceMoveWithoutCivilian(OPMFactory oFactory, Collection<Account> accountColor, Agent agent) throws InterruptedException
    {
    	Collection<Account> black = accountColor;
    	Agent ag1 = agent;
    	
    	ArrayList procs = new ArrayList<Process>();
    	ArrayList artfs = new ArrayList<Artifacts>();
    	
    	procs.add(oFactory.newProcess("moveWithoutCivilian" + procsCount, black, "Move Without Civilian")); //moveWithoutCivilian
    	procsCount++;
    	processes.add((Process)procs.get(0));
	    WasControlledBy wc5=oFactory.newWasControlledBy((Process)procs.get(0),oFactory.newRole("Agent"),ag1,black);
	    objects.add(wc5);
	    
	    artfs.add(oFactory.newArtifact("ag1stateLoad2"+artfsCount,black,"Agent state: Civilian is agent's top priority, agent not at same position of civilian")); //ag1stateLoad2
	    artfsCount++;
	    artfs.add(oFactory.newArtifact("returnedListCivilianListLoad"+artfsCount,black,"Returned Sorted List of Civilians")); //returnedListCivilianListLoad
	    artfsCount++;
	    artfs.add(oFactory.newArtifact("ag1stateLoad1"+artfsCount,black,"Agent state: Agent does not have any civilians & target list is not empty")); // ag1stateLoad1
	    artfsCount++;
	    artfs.add(oFactory.newArtifact("returnedPathMove"+artfsCount,black,"Returned Path")); //returnedPathMove
	    artfsCount++;
	    artifacts.add((Artifact) artfs.get(0));
	    artifacts.add((Artifact) artfs.get(1));
	    artifacts.add((Artifact) artfs.get(2));
	    artifacts.add((Artifact) artfs.get(3));
	    
	    Used u5=oFactory.newUsed((Process)procs.get(0),oFactory.newRole("LoadCivilian"),(Artifact) artfs.get(3),black);
	    WasDerivedFrom wd6=oFactory.newWasDerivedFrom((Artifact) artfs.get(0),(Artifact) artfs.get(1),black);
	    WasDerivedFrom wd7=oFactory.newWasDerivedFrom((Artifact) artfs.get(1),(Artifact) artfs.get(2),black);
	    WasDerivedFrom wd8=oFactory.newWasDerivedFrom((Artifact) artfs.get(3),(Artifact) artfs.get(0),black);
	    objects.add(u5);
	    objects.add(wd6);
	    objects.add(wd7);
	    objects.add(wd8);
	    
	    return  new Object[]{(Artifact) artfs.get(2),(Process)procs.get(0)};
	}
 
}
 