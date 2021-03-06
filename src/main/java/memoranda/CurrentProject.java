/**
 * CurrentProject.java
 * Created on 13.02.2003, 13:16:52 Alex
 * Package: net.sf.memoranda
 *
 * @author Alex V. Alishevskikh, alex@openmechanics.net
 * Copyright (c) 2003 Memoranda Team. http://memoranda.sf.net
 *
 */
package memoranda;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;
import java.util.Vector;

import memoranda.ui.AppFrame;
import memoranda.util.Context;
import memoranda.util.CurrentStorage;
import memoranda.util.Storage;
import memoranda.util.JsonBuilder;
import memoranda.util.JsonLoader;

/**
 *
 */
/*$Id: CurrentProject.java,v 1.6 2005/12/01 08:12:26 alexeya Exp $*/
public class CurrentProject {

    private static Project _project = null;
    private static LectureList _lecturelist = null;
    private static TaskList _tasklist = null;
    private static TaskList _assignlist = null;
    private static TaskList _studenttodo = null;
    private static TaskList _instrTodoList = null;
    private static TaskList _taTodoList = null;

    private static NoteList _notelist = null;
    private static ResourcesList _resources = null;
    private static Vector projectListeners = new Vector();

    private static final String PRJ_ID_KEY = "LAST_OPENED_PROJECT_ID";
    public enum TaskType {DEFAULT, INSTR_TODO_LIST, STUDENT_TODO,TA_TODO}
    public static TaskType currentTaskType = TaskType.DEFAULT;

    
    static {
        System.out.println("[DEBUG] Opening Everything !!!");

        // Check if there is some project that has been opened last.
        // If not, create a default.
        String prjId = (String)Context.get(PRJ_ID_KEY);
        if (prjId == null) {
            prjId = "__default";
            Context.put(PRJ_ID_KEY, prjId);
        }
        
        
        // Get the project. If there is no project associated with the
        // project id, get the first project that exists in storage (prjId=0)
        
        //ProjectManager.init();
        _project = ProjectManager.getProject(prjId);
        
        if (_project == null) {
            // alexeya: Fixed bug with NullPointer when LAST_OPENED_PROJECT_ID
            // references to missing project
            _project = ProjectManager.getProject("__default");
            if (_project == null) 
                _project = (Project)ProjectManager.getActiveProjects().get(0);                        
            Context.put(PRJ_ID_KEY, _project.getID());
            
        }        
        
        // Get the tasks, instructor todo lists, notes, 
        // and resources from the project
        _tasklist = CurrentStorage.get().openTaskList(_project);
        _instrTodoList = CurrentStorage.get().openInstrTodoList(_project);
        _taTodoList = CurrentStorage.get().openTaTodoList(_project);
        _studenttodo = CurrentStorage.get().openStudentTodo(_project);
        _lecturelist = CurrentStorage.get().openLectureList(_project);
        _notelist = CurrentStorage.get().openNoteList(_project);
        _resources = CurrentStorage.get().openResourcesList(_project);
        _assignlist = CurrentStorage.get().openAssignList(_project);
       
        // When exiting the application, save the current project
        AppFrame.addExitListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                save();
            }
        });
    }
        
    public static Project get() {
        return _project;
    }

    /**
     * Get the tasks associated with this project
     * @return the list of tasks associated with this project
     */
    public static TaskList getTaskList() {
        boolean emptyContext;
        try {
            emptyContext = Context.get("CURRENT_PANEL").equals("ASSIGN");
        } catch (NullPointerException contextFail) {
            emptyContext = false;
        }
        if(emptyContext) {
            return _assignlist;
        } else if (currentTaskType == TaskType.STUDENT_TODO) {
            return _studenttodo;
        } else if (currentTaskType == TaskType.INSTR_TODO_LIST) {
            final String DEBUG = "\t\t[DEBUG] Returning _instrTodoList";
            return _instrTodoList; 
        } else if (currentTaskType == TaskType.TA_TODO) {
            final String DEBUG = "\t\t[DEBUG] Returning _instrTodoList";
            return  _taTodoList; 
        } 
        else {
            return _tasklist;
        }
    }

    public static TaskList getAssignList() {
        return _assignlist;
    }
        
    /**
     * Get the lectures associated with this project
     * @return the list of lectures associated with this project
     */
    public static LectureList getLectureList() {
        return _lecturelist;
    }

    /**
     * This is a test usage method, not for usage in main code.
     */
    public static void setCurrentTaskType(TaskType type) {
        currentTaskType = type;
    }
    

    /**
     * Get the notes associated with this project
     * @return the list of notes associated with this project
     */
    public static NoteList getNoteList() {
            return _notelist;
    }
    
    /**
     * Get the list of resources associated with this project
     * @return the list of resources associated with this project
     */
    public static ResourcesList getResourcesList() {
            return _resources;
    }

    /**
     * Set the current project by retrieving the appropriate 
     * tasks, notes, and resources, notifying listeners, and
     * setting last opened project id to be the id of the project. 
     * TODO: Edit this to be able to insert the task list into the 
     * storage process
     * @param project the project to be made current
     */
    public static void set(Project project) {
        if (project.getID().equals(_project.getID())) return;
        
        JsonLoader jsonLoader = new JsonLoader();
        jsonLoader.loadFromJson();
        
        LectureList newlecturelist = CurrentStorage.get().openLectureList(project);
        TaskList newtasklist = CurrentStorage.get().openTaskList(project);
        TaskList newstudentodo = CurrentStorage.get().openStudentTodo(project);
        TaskList newinstrtodolist = CurrentStorage.get().openInstrTodoList(project);
        TaskList newtatodolist = CurrentStorage.get().openTaTodoList(project);
        NoteList newnotelist = CurrentStorage.get().openNoteList(project);
        ResourcesList newresources = CurrentStorage.get().openResourcesList(project);
        TaskList newassignlist = CurrentStorage.get().openAssignList(project);
        //notifyListenersBefore(project, newnotelist, newtasklist, newresources); //Old style change extent unknown
        notifyListenersBefore(project, newnotelist, newlecturelist, newinstrtodolist, newstudentodo, newtatodolist, newresources);

        _project = project;
        _lecturelist = newlecturelist;
        _tasklist = newtasklist;
        _studenttodo = newstudentodo;
        _instrTodoList = newinstrtodolist;
        _taTodoList = newtatodolist;
        _notelist = newnotelist;
        _resources = newresources;
        _assignlist = newassignlist;
        notifyListenersAfter();
        Context.put(PRJ_ID_KEY, project.getID());
    }

    /**
     * Add a listener to current project
     * @param pl the listener
     */
    public static void addProjectListener(ProjectListener pl) {
        projectListeners.add(pl);
    }

    /**
     * Get the set of listeners to the current project
     * @return the set of listeners to the current project
     */
    public static Collection getChangeListeners() {
        return projectListeners;
    }

    /**
     * Notify listeners before the current project is changed
     * @param project the new project
     * @param nl the new note list
     * @param tl the new task list
     * @param t2 the new instructor todo list
     * @param rl the new resource list
     */

    private static void notifyListenersBefore(Project project, NoteList nl, LectureList tl, TaskList t2, TaskList s1, TaskList s2, ResourcesList rl) {
        for (int i = 0; i < projectListeners.size(); i++) {
            ((ProjectListener)projectListeners.get(i)).projectChange(project, nl, tl, t2, s1, s2, rl);
            /*DEBUGSystem.out.println(projectListeners.get(i));*/
        }
    }
    
    /**
     * Notify listeners after the current project is changed
     */
    private static void notifyListenersAfter() {
        for (int i = 0; i < projectListeners.size(); i++) {
            ((ProjectListener)projectListeners.get(i)).projectWasChanged();            
        }
    }

    /**
     * Save all of the info associated with the current project
     */
    public static void save() {
        Storage storage = CurrentStorage.get();

        storage.storeNoteList(_notelist, _project);
        storage.storeAssignList(_assignlist, _project);
        storage.storeLectureList(_lecturelist, _project);
        storage.storeTaskList(_tasklist, _project); 
        storage.storeStudentTodo(_studenttodo, _project); 
        storage.storeInstrTodoList(_instrTodoList, _project);
        storage.storeTaTodoList(_taTodoList, _project);
        storage.storeResourcesList(_resources, _project);
        storage.storeProjectManager();

        JsonBuilder jsonBuilder = new JsonBuilder();
        jsonBuilder.exportMemoranda();        
    }
    
    /**
     * Reset the current project to null
     */
    public static void free() {
        _project = null;
        _lecturelist = null;
        _tasklist = null;
        _taTodoList = null;
        _assignlist = null;
        _notelist = null;
        _resources = null;
        _studenttodo = null;
        _instrTodoList = null;
    }

    /**
     * A way to update all listeners without calling set.
     * Calling this will make the main adgenda page respond to changes, given they
     * are exposed properly to some listener.
     * A hack, but a useful hack.
     */
    public static void updateAllListeners() {
        System.out.println("[DEBUG] - update all listeners called. CurrentProject.java - Line 192.\n");
        notifyListenersAfter();
    }
}
        