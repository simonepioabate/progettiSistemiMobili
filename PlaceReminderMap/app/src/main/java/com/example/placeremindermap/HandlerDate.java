package com.example.placeremindermap;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;

public class HandlerDate {


    public static class DateComparator implements Comparator<PoiDescriptor> {
        @Override
        public int compare(PoiDescriptor poi1, PoiDescriptor poi2) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.ITALY);

            try {
                Date date1 = dateFormat.parse(poi1.getLastUpdate());
                Date date2 = dateFormat.parse(poi2.getLastUpdate());

                return date2.compareTo(date1);
            } catch (ParseException e) {
                e.printStackTrace();
                return 0; // In caso di errore, considera le date uguali
            }
        }
    }


    public static String dateNow(){
        Calendar calendar = Calendar.getInstance();

        // Get current date and time as Date object
        Date currentDate = calendar.getTime();

        // Format date and time as string
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.ITALY);
        return formatter.format(currentDate);
    }
}
