
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import junit.framework.TestCase;

import org.fireflow.engine.calendar.DefaultCalendarService;
import org.fireflow.model.Duration;

/**
 *
 * @author chennieyun
 */
public class TestCalendarService extends TestCase {

    public void testDateAfter() {
        Date t1 = new Date();
        DefaultCalendarService calService = new DefaultCalendarService();
        Properties prop = new Properties();
//        prop.setProperty(DefaultCalendarService.business_time_monday, "8:30-12:00");
        calService.setBusinessCalendarProperties(prop);

        SimpleDateFormat dFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date fromDate = null;
        try {
            fromDate = dFormat.parse("2008-10-31 16:10");
        } catch (ParseException ex) {
            Logger.getLogger(TestCalendarService.class.getName()).log(Level.SEVERE, null, ex);
        }
        Duration du = new Duration(2,Duration.DAY);
        du.setBusinessTime(true);
        Date newDate = calService.dateAfter(fromDate, du);
        System.out.println("2 biz day after 2008-10-31 16:10 is "+dFormat.format(newDate));
//
        Duration du2 = new Duration(3, Duration.HOUR);
        du2.setBusinessTime(true);
        Date newDate2 = calService.dateAfter(fromDate, du2);
        System.out.println("3 biz hour after 2008-10-31 16:10 is " + dFormat.format(newDate2));
//        
        Duration du3 = new Duration(1,Duration.WEEK);
        du3.setBusinessTime(false);
        Date newDate3 = calService.dateAfter(fromDate, du3);
        System.out.println("1week after 2008-10-31 16:10 is "+dFormat.format(newDate3));
//            
//        
        Duration du4 = new Duration(1,Duration.YEAR);
        du4.setBusinessTime(false);
        Date newDate4 = calService.dateAfter(fromDate, du4);
        System.out.println("1 year after 2008-10-31 10:10 is "+dFormat.format(newDate4));
//                   
        Date t2 = new Date();
        System.out.println("cal time is " + (t2.getTime() - t1.getTime()));
    }

   /*
    public void testGetTotalWorkingTime() {
        try {
            SimpleDateFormat dFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            Date d = dFormat.parse("2008-11-03 8:30");
            DefaultCalendarService calService = new DefaultCalendarService();
            System.out.println("Total working time is " + calService.getTotalWorkingTime(d));
        } catch (ParseException ex) {
            Logger.getLogger(TestCalendarService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }*/
}
