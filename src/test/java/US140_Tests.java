package test.java;

import static org.junit.Assert.*;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.junit.Before;

import org.junit.Test;

import main.java.memoranda.Lecture;
import main.java.memoranda.LectureImpl;
import main.java.memoranda.LectureList;
import main.java.memoranda.LectureListImpl;
import main.java.memoranda.date.CalendarDate;

public class US140_Tests {
    @Before
    public void setUp() {   
    }

    
    @Test
    public void testCreateLecture() {
        LectureList lectList = new LectureListImpl(null);
        Calendar cal = new GregorianCalendar();
        cal.set(Calendar.HOUR_OF_DAY, 12);
        cal.set(Calendar.MINUTE, 30);
        Lecture lect = lectList.createLecture(CalendarDate.today(), cal, cal, "Lecture 1");
        
        assertTrue(lect.getDate().equals(CalendarDate.today()));
        assertTrue(lect.getStartHour() == 12);
        assertTrue(lect.getStartMin() == 30);
        assertTrue(lect.getEndHour() == 12);
        assertTrue(lect.getEndMin() == 30);
        assertTrue(lect.getTopic().equals("Lecture 1"));
    }
    
    @Test
    public void testCompareTo() {
        LectureList lectList = new LectureListImpl(null);
        
        // Test w/ same date and time
        Calendar cal1 = new GregorianCalendar();
        cal1.set(Calendar.HOUR_OF_DAY, 12);
        cal1.set(Calendar.MINUTE, 30);
        LectureImpl lect1 = (LectureImpl) lectList.createLecture(CalendarDate.today(), cal1, cal1, "Lecture");
        LectureImpl lect2 = (LectureImpl) lectList.createLecture(CalendarDate.today(), cal1, cal1, "Lecture");
        assertTrue(lect1.compareTo(lect2) == 0);
        
        // Test w/ one day apart
        LectureImpl lect3 = (LectureImpl) lectList.createLecture(CalendarDate.today(), cal1, cal1, "Lecture");
        LectureImpl lect4 = (LectureImpl) lectList.createLecture(CalendarDate.tomorrow(), cal1, cal1, "Lecture");
        assertTrue(lect3.compareTo(lect4) == -1);
        assertTrue(lect4.compareTo(lect3) == 1);
        
        // Test w/ one month apart
        CalendarDate cDate1 = new CalendarDate(1, 1, 2020);
        CalendarDate cDate2 = new CalendarDate(1, 2, 2020);
        LectureImpl lect5 = (LectureImpl) lectList.createLecture(cDate1, cal1, cal1, "Lecture");
        LectureImpl lect6 = (LectureImpl) lectList.createLecture(cDate2, cal1, cal1, "Lecture");
        assertTrue(lect5.compareTo(lect6) == -1);
        assertTrue(lect6.compareTo(lect5) == 1);
        
        // Test /w one year apart
        CalendarDate cDate3 = new CalendarDate(1, 1, 2020);
        CalendarDate cDate4 = new CalendarDate(1, 1, 2021);
        LectureImpl lect7 = (LectureImpl) lectList.createLecture(cDate3, cal1, cal1, "Lecture");
        LectureImpl lect8 = (LectureImpl) lectList.createLecture(cDate4, cal1, cal1, "Lecture");
        assertTrue(lect7.compareTo(lect8) == -1);
        assertTrue(lect8.compareTo(lect7) == 1);
    }
}