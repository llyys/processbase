package ee.kovmen.utils;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.concurrent.TimeUnit;

public class Isikukood
{
  private String code;
  int ageInYears = 0;
  int ageInMonths = 0;
  int ageInDays = 0;

  public Isikukood(String code)
  {
    this.code = code;

    if (code.length() != 11)
      throw new IllegalArgumentException("Vigane isikukood");
  }

  public char getSex()
  {
    int id = Integer.parseInt(this.code.substring(0, 1));
    char sex = id % 2 == 0 ? 'F' : 'M';
    return sex;
  }

  public int getAge()
  {
    getAgeDays();
    return ageInYears;
  }

  public int getAgeMonths()
  {
    getAgeDays();    
    return ageInMonths;
  }

  public int getAgeDays()
  {
    int year = Integer.parseInt(this.code.substring(1, 3));
    int month = Integer.parseInt(this.code.substring(3, 5)) - 1;
    int day = Integer.parseInt(this.code.substring(5, 7));

    int year_prefix = 0;

    int sex = Integer.parseInt(this.code.substring(0, 1));

    if ((sex == 3) || (sex == 4))
      year_prefix = 19;
    else if ((sex == 5) || (sex == 6)) {
      year_prefix = 20;
    }

    year = year_prefix * 100 + year;

    Calendar today = new GregorianCalendar();
    Calendar birth = new GregorianCalendar(year, month, day);
    calculateDays(birth, today);
    return ageInDays;
/*
    long diff = today.getTimeInMillis() - birth.getTimeInMillis();

    long age_days = TimeUnit.MILLISECONDS.toDays(diff);

    return (int)age_days;*/
  }
  
  private void calculateDays(Calendar dateOfBirth, Calendar today){
	  //DateTime dateOfBirth = new DateTime(2000, 4, 18);
      //Date currentDate = DateTime.Now;
	  

     
      
      ageInDays = today.get(Calendar.DAY_OF_MONTH) - dateOfBirth.get(Calendar.DAY_OF_MONTH);
      ageInMonths = today.get(Calendar.MONTH) - dateOfBirth.get(Calendar.MONTH);
      ageInYears = today.get(Calendar.YEAR) - dateOfBirth.get(Calendar.YEAR);

      if (ageInDays < 0)
      {
          ageInDays += today.getActualMaximum(Calendar.DAY_OF_MONTH);
          ageInMonths--;

          if (ageInMonths < 0)
          {
              ageInMonths += 12;
              ageInYears--;
          }
      }
      if (ageInMonths < 0)
      {
          ageInMonths += 12;
          ageInYears--;
      }

      
  }
}