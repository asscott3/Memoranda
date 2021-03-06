package memoranda.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerDateModel;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import memoranda.Project;
import memoranda.ProjectManager;
import memoranda.date.CalendarDate;
import memoranda.util.CurrentStorage;
import memoranda.util.Local;

import memoranda.CurrentProject;
import memoranda.LectureTime;
import memoranda.SpecialCalendarDate;
import memoranda.Task;
import memoranda.TaskList;
import memoranda.TaskListImpl;

/*$Id: ProjectDialog.java,v 1.26 2004/10/18 19:09:10 ivanrise Exp $*/
public class ProjectDialog extends JDialog {
    public boolean CANCELLED = true;
    boolean ignoreStartChanged = false;
    boolean ignoreEndChanged = false;
    CalendarFrame endCalFrame = new CalendarFrame();
    CalendarFrame startCalFrame = new CalendarFrame();
    GridBagConstraints gbc;
    GridBagConstraints gbc1;
    JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    JLabel header = new JLabel();
    JPanel centerPanel = new JPanel(new GridBagLayout());
    //
    JPanel setTimesPanel = new JPanel(new GridBagLayout());
    //
    JLabel titleLabel = new JLabel();
    public JTextField prTitleField = new JTextField();
    JLabel sdLabel = new JLabel();
    public JSpinner startDate = new JSpinner(new SpinnerDateModel());
    JButton sdButton = new JButton();
    public JCheckBox endDateChB = new JCheckBox();
    public JSpinner endDate = new JSpinner(new SpinnerDateModel());
    JButton edButton = new JButton();
    
    JLabel ldLabel = new JLabel();
    public JSpinner lectureDays = new JSpinner(new SpinnerDateModel());
    JButton ldButton = new JButton();
    
    JLabel finalExamLabel = new JLabel();
    public JSpinner finalExam = new JSpinner(new SpinnerDateModel());
    JButton feButton = new JButton();
    
    JButton setLectureDays = new JButton();
    JButton setFreeDays = new JButton();
    JButton setHolidays = new JButton();
    JButton setBreaks = new JButton();
    JTextField todoField = new JTextField();
    
    //public JCheckBox freezeChB = new JCheckBox();
    //JPanel setTimesPanel = new JPanel();
    JPanel bottomPanel = new JPanel();
    JButton okButton = new JButton();
    JButton cancelButton = new JButton();


    //New for adding tasks into, US90 specific
    public ArrayList<LectureTime> lectureTimes = new ArrayList<LectureTime>();
    public ArrayList<SpecialCalendarDate> freeDays = new ArrayList<SpecialCalendarDate>();
    public ArrayList<SpecialCalendarDate> breakDays = new ArrayList<SpecialCalendarDate>();
    public ArrayList<SpecialCalendarDate> holidays = new ArrayList<SpecialCalendarDate>();
    public ArrayList<SpecialCalendarDate> breaks = new ArrayList<SpecialCalendarDate>();

    public ProjectDialog(Frame frame, String title) {
        super(frame, title, true);
        try {
            jbInit();
            pack();
        }
        catch(Exception ex) {
            new ExceptionDialog(ex);
        }
    }

    void jbInit() throws Exception {
    	
    	this.setResizable(false);
        getContentPane().setLayout(new GridBagLayout());
        topPanel.setBorder(new EmptyBorder(new Insets(0, 5, 0, 5)));
        topPanel.setBackground(Color.WHITE);        
        header.setFont(new java.awt.Font("Dialog", 0, 20));
        header.setForeground(new Color(0, 0, 124));
        header.setText(Local.getString("Course"));
        //header.setHorizontalAlignment(SwingConstants.CENTER);
        header.setIcon(new ImageIcon(memoranda.ui.ProjectDialog.class.getResource(
            "/ui/icons/project48.png")));
        topPanel.add(header);
        
        centerPanel.setBorder(new EtchedBorder());
        titleLabel.setText(Local.getString("Course Name"));
        gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.gridwidth = 5;
        gbc.insets = new Insets(5, 10, 5, 10);
        //gbc.anchor = GridBagConstraints.WEST;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        centerPanel.add(titleLabel, gbc);
        
        
        //prTitleField.setPreferredSize(new Dimension(270, 20));
        gbc = new GridBagConstraints();
        gbc.gridwidth = 5;
        gbc.gridx = 0; gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 10, 5, 0);
        //gbc.anchor = GridBagConstraints.EAST;
        gbc.anchor = GridBagConstraints.CENTER;
        centerPanel.add(prTitleField, gbc);
        
        sdLabel.setText(Local.getString("Start date"));
        sdLabel.setPreferredSize(new Dimension(70, 20));
        sdLabel.setMinimumSize(new Dimension(70, 20));
        sdLabel.setMaximumSize(new Dimension(70, 20));
        gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = 2;
        gbc.insets = new Insets(5, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;
        centerPanel.add(sdLabel, gbc);

        startDate.setPreferredSize(new Dimension(80, 20));
        startDate.setLocale(Local.getCurrentLocale());
		//Added by (jcscoobyrs) on 17-Nov-2003 at 14:24:43 PM
		//---------------------------------------------------
		SimpleDateFormat sdf = new SimpleDateFormat();
		sdf = (SimpleDateFormat)DateFormat.getDateInstance(DateFormat.SHORT);
		startDate.setEditor(new JSpinner.DateEditor(startDate, 
			sdf.toPattern()));
		//---------------------------------------------------
        startDate.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                if (ignoreStartChanged) return;
                ignoreStartChanged = true;
                Date sd = (Date) startDate.getModel().getValue();
                if (endDate.isEnabled()) {
                  Date ed = (Date) endDate.getModel().getValue();
                  if (sd.after(ed)) {
                    startDate.getModel().setValue(ed);
                    sd = ed;
                  }
                }
                startCalFrame.cal.set(new CalendarDate(sd));
                ignoreStartChanged = false;
            }
        });
        gbc = new GridBagConstraints();
        gbc.gridx = 1; gbc.gridy = 2;
        gbc.insets = new Insets(5, 0, 10, 5);
        gbc.anchor = GridBagConstraints.WEST;
        centerPanel.add(startDate, gbc);
        
        sdButton.setMinimumSize(new Dimension(20, 20));
        sdButton.setPreferredSize(new Dimension(20, 20));
        sdButton.setIcon(new ImageIcon(memoranda.ui.AppFrame.class.getResource("/ui/icons/calendar.png")));
        sdButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                sdButton_actionPerformed(e);
            }
        });
        gbc = new GridBagConstraints();
        gbc.gridx = 2; gbc.gridy = 2;
        gbc.insets = new Insets(5, 0, 10, 5);
        gbc.anchor = GridBagConstraints.WEST;
        centerPanel.add(sdButton, gbc);
        
        
        
        // Start of end date field
        endDateChB.setForeground(Color.gray);
        endDateChB.setText(Local.getString("End date"));
        endDateChB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                endDateChB_actionPerformed(e);
            }
        });
        
        
        gbc = new GridBagConstraints();
        gbc.gridx = 3; gbc.gridy = 2;
        gbc.insets = new Insets(5, 0, 10, 5);
        gbc.anchor = GridBagConstraints.WEST;
//        centerPanel.add(endDateChB, gbc);
        
//        endDate.setEnabled(false);
        endDate.setPreferredSize(new Dimension(80, 20));
        endDate.setLocale(Local.getCurrentLocale());
		//Added by (jcscoobyrs) on 17-Nov-2003 at 14:24:43 PM
		//---------------------------------------------------
		endDate.setEditor(new JSpinner.DateEditor(endDate, 
			sdf.toPattern()));
		//---------------------------------------------------
        endDate.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                if (ignoreEndChanged) return;
                ignoreEndChanged = true;
                Date sd = (Date) startDate.getModel().getValue();
                Date ed = (Date) endDate.getModel().getValue();
                if (sd.after(ed)) {
                    endDate.getModel().setValue(sd);
                    ed = sd;
                }
                endCalFrame.cal.set(new CalendarDate(ed));
                ignoreEndChanged = false;
            }
        });
        //((JSpinner.DateEditor) endDate.getEditor()).setLocale(Local.getCurrentLocale());
        gbc = new GridBagConstraints();
        gbc.gridx = 4; gbc.gridy = 2;
        gbc.insets = new Insets(5, 0, 10, 5);
        gbc.anchor = GridBagConstraints.WEST;
        centerPanel.add(endDate, gbc);
        
    
        
        edButton.setEnabled(false);
        edButton.setMinimumSize(new Dimension(20, 20));
        edButton.setMaximumSize(new Dimension(20, 20));
        edButton.setPreferredSize(new Dimension(20, 20));
        edButton.setIcon(new ImageIcon(memoranda.ui.AppFrame.class.getResource("/ui/icons/calendar.png")));
        edButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                edButton_actionPerformed(e);
            }
        });
        gbc = new GridBagConstraints();
        gbc.gridx = 5; gbc.gridy = 2;
        gbc.insets = new Insets(5, 0, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;
        centerPanel.add(edButton, gbc);
        
       
        //Final exam field
        finalExamLabel.setText(Local.getString("Final exam"));
        finalExamLabel.setPreferredSize(new Dimension(70, 20));
        finalExamLabel.setMinimumSize(new Dimension(70, 20));
        finalExamLabel.setMaximumSize(new Dimension(70, 20));
        gbc = new GridBagConstraints();
        gbc.gridx = 6; gbc.gridy = 2;
        gbc.insets = new Insets(5, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;
        centerPanel.add(finalExamLabel, gbc);

        finalExam.setPreferredSize(new Dimension(80, 20));
        finalExam.setLocale(Local.getCurrentLocale());

		SimpleDateFormat sdf1 = new SimpleDateFormat();
		sdf1 = (SimpleDateFormat)DateFormat.getDateInstance(DateFormat.SHORT);
		finalExam.setEditor(new JSpinner.DateEditor(finalExam, 
			sdf1.toPattern()));
		//---------------------------------------------------
//		finalExam.addChangeListener(new ChangeListener() {
//            public void stateChanged(ChangeEvent e) {
//                if (ignoreStartChanged) return;
//                ignoreStartChanged = true;
//                Date sd = (Date) finalExam.getModel().getValue();
//                if (endDate.isEnabled()) {
//                  Date ed = (Date) endDate.getModel().getValue();
//                  if (sd.after(ed)) {
//                    startDate.getModel().setValue(ed);
//                    sd = ed;
//                  }
//                }
//                startCalFrame.cal.set(new CalendarDate(sd));
//                ignoreStartChanged = false;
//            }
//        });
        gbc = new GridBagConstraints();
        gbc.gridx = 7; gbc.gridy = 2;
        gbc.insets = new Insets(5, 0, 10, 5);
        gbc.anchor = GridBagConstraints.WEST;
        centerPanel.add(finalExam, gbc);
        
        feButton.setMinimumSize(new Dimension(20, 20));
        feButton.setPreferredSize(new Dimension(20, 20));
        feButton.setIcon(new ImageIcon(memoranda.ui.AppFrame.class.getResource("/ui/icons/calendar.png")));
        feButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                sdButton_actionPerformed(e);
            }
        });
        gbc = new GridBagConstraints();
        gbc.gridx = 8; 
        gbc.gridy = 2;
        gbc.insets = new Insets(5, 0, 10, 5);
        gbc.anchor = GridBagConstraints.WEST;
        centerPanel.add(feButton, gbc);
               
        
        gbc = new GridBagConstraints();
        gbc.gridx = 4; 
        gbc.gridy = 3;
        gbc.insets = new Insets(5, 0, 10, 5);
        gbc.anchor = GridBagConstraints.CENTER;
        setTimesPanel.add(setLectureDays, gbc); 
        
        //Call for action - open events window upon pressing 
        setLectureDays.setText(Local.getString("Set Lecture Times"));
        setLectureDays.setIcon(
            new ImageIcon(memoranda.ui.AppFrame.class.getResource("/ui/icons/notify.png")));
        setLectureDays.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	setLectureDays_actionPerformed(e);
            }
        });
        //------------------------------  
        
        setFreeDays.setText(Local.getString("Set Free Days"));
        setFreeDays.setIcon(
            new ImageIcon(memoranda.ui.AppFrame.class.getResource("/ui/icons/notify.png")));
        
        gbc = new GridBagConstraints();
        gbc.gridx = 4; 
        gbc.gridy = 3;
        gbc.insets = new Insets(5, 0, 10, 10);
        gbc.anchor = GridBagConstraints.CENTER;
        setTimesPanel.add(setFreeDays, gbc);
        
        //Call for action - open events window upon pressing 
        setFreeDays.setText(Local.getString("set Free Days"));
        setFreeDays.setIcon(
            new ImageIcon(memoranda.ui.AppFrame.class.getResource("/ui/icons/notify.png")));
        setFreeDays.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	setFreeDays_actionPerformed(e);
            }
        });
        
        
        //-----------------------------
        
        
        
        setHolidays.setText(Local.getString("Add Holidays"));
        setHolidays.setIcon(
            new ImageIcon(memoranda.ui.AppFrame.class.getResource("/ui/icons/notify.png")));
        
        gbc = new GridBagConstraints();
        gbc.gridx = 4; 
        gbc.gridy = 3;
        gbc.insets = new Insets(5, 0, 10, 10);
        gbc.anchor = GridBagConstraints.CENTER;
        setTimesPanel.add(setHolidays, gbc);
        
        //Call for action - open events window upon pressing 
        setHolidays.setText(Local.getString("Add Holidays"));
        setHolidays.setIcon(
            new ImageIcon(memoranda.ui.AppFrame.class.getResource("/ui/icons/notify.png")));
        setHolidays.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	setHolidays_actionPerformed(e);
            }
        });

        //-----------------------------
        
        setBreaks.setText(Local.getString("Add Breaks"));
        setBreaks.setIcon(new ImageIcon(memoranda.ui.AppFrame.class.getResource("/ui/icons/notify.png")));

        gbc = new GridBagConstraints();
        gbc.gridx = 4; 
        gbc.gridy = 3;
        gbc.insets = new Insets(5, 0, 10, 10);
        gbc.anchor = GridBagConstraints.CENTER;
        setTimesPanel.add(setBreaks, gbc); // UNKNOWN LAYOUT BEHAVIOR
        
        //Call for action - open events window upon pressing 
        setBreaks.setText(Local.getString("Add Breaks"));
        setBreaks.setIcon(new ImageIcon(memoranda.ui.AppFrame.class.getResource("/ui/icons/notify.png")));
        setBreaks.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	setBreaks_actionPerformed(e);
            }
        });

        //Sets the layout of the buttons
        setTimesPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        
        bottomPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
        okButton.setMaximumSize(new Dimension(100, 25));
        okButton.setMinimumSize(new Dimension(100, 25));
        okButton.setPreferredSize(new Dimension(100, 25));
        okButton.setText(Local.getString("Add"));
        okButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                okButton_actionPerformed(e);
            }
        });
        
        
        this.getRootPane().setDefaultButton(okButton);
        cancelButton.setMaximumSize(new Dimension(100, 25));
        cancelButton.setMinimumSize(new Dimension(100, 25));
        cancelButton.setPreferredSize(new Dimension(100, 25));
        cancelButton.setText(Local.getString("Cancel"));
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                cancelButton_actionPerformed(e);
            }
        });
        
        
        bottomPanel.add(okButton);
        bottomPanel.add(cancelButton);
        
        gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        getContentPane().add(topPanel, gbc);
        
        gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = 1;
        gbc.insets = new Insets(5, 5, 5, 5);
        getContentPane().add(centerPanel, gbc);
        
        
        gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = 2;
        gbc.insets = new Insets(5, 0, 5, 5);
        gbc.anchor = GridBagConstraints.CENTER;
        getContentPane().add(setTimesPanel, gbc);
        
        gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = 3;
        gbc.insets = new Insets(5, 0, 5, 5);
        gbc.anchor = GridBagConstraints.EAST;
        getContentPane().add(bottomPanel, gbc);
    
        startCalFrame.cal.addSelectionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (ignoreStartChanged)
                    return;
                startDate.getModel().setValue(startCalFrame.cal.get().getCalendar().getTime());
            }
        });
        endCalFrame.cal.addSelectionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (ignoreEndChanged)
                    return;
                endDate.getModel().setValue(endCalFrame.cal.get().getCalendar().getTime());
            }
        });
    }
    
    
   
    
    void okButton_actionPerformed(ActionEvent e) {
        CANCELLED = false;
        this.dispose();
    }
    
    void cancelButton_actionPerformed(ActionEvent e) {
        this.dispose();
    }
    
    void endDateChB_actionPerformed(ActionEvent e) {
        endDate.setEnabled(endDateChB.isSelected());
        edButton.setEnabled(endDateChB.isSelected());
        if (endDateChB.isSelected()) {
            endDateChB.setForeground(Color.BLACK);
            endDate.getModel().setValue(startDate.getModel().getValue());
        }
        else endDateChB.setForeground(Color.GRAY);
    }
    
    void sdButton_actionPerformed(ActionEvent e) {
        //startCalFrame.setLocation(sdButton.getLocation());
        startCalFrame.setLocation(0, 0);
        startCalFrame.setSize((this.getContentPane().getWidth() / 2), 
            this.getContentPane().getHeight());
        this.getLayeredPane().add(startCalFrame);
        startCalFrame.setTitle(Local.getString("Start date"));
        startCalFrame.show();
    }
    
    void edButton_actionPerformed(ActionEvent e) {
        endCalFrame.setLocation((this.getContentPane().getWidth() / 2),0);
        endCalFrame.setSize((this.getContentPane().getWidth() / 2), 
            this.getContentPane().getHeight());
        this.getLayeredPane().add(endCalFrame);
        endCalFrame.setTitle(Local.getString("End date"));
        endCalFrame.show();
    }
    
    // Perform action for set lecture days
    void setLectureDays_actionPerformed(ActionEvent e) {
        LectureTime posTime = ((AppFrame)App.getFrame()).workPanel.dailyItemsPanel.tasksPanel.newLectureTime_actionPerformed();
        if(posTime != null) {
            lectureTimes.add(posTime);
        }
    }
    
    // Perform action for set free days
    void setFreeDays_actionPerformed(ActionEvent e) {

        SpecialCalendarDate freeDay = ((AppFrame)App.getFrame()).workPanel.dailyItemsPanel.tasksPanel.newFreeDay_actionPerformed();
        if(freeDay != null) {
            freeDays.add(freeDay);
        }
    }
    
    // Perform action for set holidays
    void setHolidays_actionPerformed(ActionEvent e) {
        SpecialCalendarDate holiday = ((AppFrame)App.getFrame()).workPanel.dailyItemsPanel.tasksPanel.newHoliday_actionPerformed();
        if(holiday != null) {
            holidays.add(holiday);
        }
    }

    // Perform action for set breaks
    void setBreaks_actionPerformed(ActionEvent e) {
        SpecialCalendarDate courseBreak = ((AppFrame)App.getFrame()).workPanel.dailyItemsPanel.tasksPanel.newBreak_actionPerformed();
        if(courseBreak != null) {
            breaks.add(courseBreak);
        }
    }
    
    public static void newProject() {
        ProjectDialog dlg = new ProjectDialog(null, Local.getString("New course"));
        
        Dimension dlgSize = dlg.getSize();

        Dimension frmSize = App.getFrame().getSize();
        Point loc = App.getFrame().getLocation();
        dlg.setLocation((frmSize.width - dlgSize.width) / 2 + loc.x, (frmSize.height - dlgSize.height) / 2 + loc.y);
        dlg.setVisible(true);

        if (dlg.CANCELLED)
            return;
        
        String title = dlg.prTitleField.getText();
        CalendarDate startD = new CalendarDate((Date) dlg.startDate.getModel().getValue());

        CalendarDate endD = new CalendarDate((Date) dlg.endDate.getModel().getValue());
        //new for final exam date
        CalendarDate FinalExamDate = new CalendarDate((Date) dlg.finalExam.getModel().getValue());

        Project prj = ProjectManager.createProject(title, startD, endD, FinalExamDate);
        CurrentStorage.get().storeProjectManager(); //does this set the current project? If not set it before setTasks is called
        CurrentProject.set(prj);

        CurrentProject.currentTaskType = CurrentProject.TaskType.DEFAULT;

        for(LectureTime lt : dlg.lectureTimes) {
            Task newTask = CurrentProject.getTaskList().createLectureTask(lt.day, lt.hour, lt.min, "Lecture");
        }
        for(SpecialCalendarDate fd : dlg.freeDays) {
            Task newTask = CurrentProject.getTaskList().createSingleEventTask(fd.getName(), fd.getDate(), "Free Day");
        }
        for(SpecialCalendarDate hd : dlg.holidays) {
            Task newTask = CurrentProject.getTaskList().createSingleEventTask(hd.getName(), hd.getDate(), "Holiday");
        }
        for(SpecialCalendarDate br : dlg.breaks) {
            Task newTask = CurrentProject.getTaskList().createSingleEventTask(br.getName(), br.getDate(), "Break");
        }
       
        CurrentStorage.get().storeTaskList(CurrentProject.getTaskList(), CurrentProject.get());

        // TODO store freeDay, holiday, and break
        
        CurrentProject.updateAllListeners();

    }    
}
