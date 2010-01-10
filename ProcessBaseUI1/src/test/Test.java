package test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

/**
 *
 * @author mgubaidullin
 */
public class Test {

    public static void main(String[] args) throws URISyntaxException, IOException {
        Date currentDate = new Date(System.currentTimeMillis());
        String createdDate = null;
        GregorianCalendar c = new GregorianCalendar(currentDate.getYear()+1900, currentDate.getMonth(), currentDate.getDate());

        System.out.println(String.format(new Locale("RU"), "%1$ta %1$tb %1$td %1$tT %1$tZ %1$tY", c));

    }
}
