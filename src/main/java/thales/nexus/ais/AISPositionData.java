package thales.nexus.ais;

import nl.esi.metis.aisparser.AISMessageClassBPositionReport;
import nl.esi.metis.aisparser.AISMessagePositionReport;


public class AISPositionData
{

  private int mmsi;

  private double longitude;

  private double latitude;

  // Speed in knots
  private double speed;

  // Course in degrees
  private double course;

  private boolean positionAccuracy;
  
  private boolean positionValid = true;


  public AISPositionData(AISMessageClassBPositionReport msg)
  {
    mmsi = msg.getUserID();
    setLongitudeFromAISData(msg.getLongitudeInDegrees());
    setLatitudeFromAISData(msg.getLatitudeInDegrees());
    setSpeedFromAISData(msg.getSpeedOverGround());
    setCourseFromAISData(msg.getCourseOverGround());
    positionAccuracy = msg.getPositionAccuracy();
  }

  public AISPositionData(AISMessagePositionReport msg)
  {
    mmsi = msg.getUserID();
    setLongitudeFromAISData(msg.getLongitudeInDegrees());
    setLatitudeFromAISData(msg.getLatitudeInDegrees());
    setSpeedFromAISData(msg.getSpeedOverGround());
    setCourseFromAISData(msg.getCourseOverGround());
    positionAccuracy = msg.getPositionAccuracy();
  }

  private void setLatitudeFromAISData(double latitudeInDegrees)
  {
    if(latitudeInDegrees == 91)
    {
      positionValid = false;
    }
    latitude = latitudeInDegrees;
  }

  /** Checks the longitude is valid and then stores it in the latitude member in degrees.
   * If the longitude is not valid, then the flag positionValid is set to false
   * @param longitudeInDegrees a double value representing the longitude in degrees.
   */
  private void setLongitudeFromAISData(double longitudeInDegrees)
  {
    if(longitudeInDegrees == 181)
    {
      positionValid = false;
    }
    longitude = longitudeInDegrees;
  }

  /** Converts the course from AIS scaling to degrees and stores the value in the course member.
   * @param courseOverGround an integer value representing the course over ground in 1/10� for values in the range of 0 to 3599.
   */
  private void setCourseFromAISData(int courseOverGround)
  {
    // 3600 (E10h) = not available.
    // 3601 or higher should not be used
    if(courseOverGround >= 0 && courseOverGround < 3600)
    {
      course = courseOverGround / 10.0;
    }
    else
    {
      course = 0.0;
    }
  }

  /** Converts the speed from AIS scaling to knots and stores the value in the speed member.
   * @param speedOverGround an integer value representing the speed over ground in 1/10 knots for values in the range of 0 to 3599.
   */
  private void setSpeedFromAISData(int speedOverGround)
  {
    // 1023 = not available
    // 1022 = 102.2 knots or higher
    if(speedOverGround >= 0 && speedOverGround < 1023)
    {
      speed = speedOverGround / 10.0;
    }
    else
    {
      speed = 0.0;
    }
  }


  public int getMMSI()
  {
    return mmsi;
  }



  /** Returns the longitude in degrees.
   * @return a double value representing the longitude in degrees (&plusmn;180&deg;, East = positive, West = negative; 181 not available).
   */
  public double getLongitude()
  {
    return longitude;
  }

  /** Returns the latitude in degrees.
   * @return a double value representing the latitude in degrees (&plusmn;90&deg;, North = positive, South = negative; 91 not available).
   */
  public double getLatitude()
  {
    return latitude;
  }


  // Speed in knots
  public double getSpeed()
  {
    return speed;
  }


  public double getCourse()
  {
    return course;
  }

  /** Returns the position accuracy.
   * @return a boolean value representing position accuracy: <br>
   * true = high (&le; 10 m) <br>
   * false = low (&gt; 10 m)
   */
  public boolean getPositionAccuracy()
  {
    return positionAccuracy;
  }

  /** Returns the position validity.
   * @return a boolean value representing position validity: <br>
   * true = the latitude and longitude contain valid data <br>
   * false = either latitude or longitude (or both) are not valid or known
   */
  public boolean getPositionValid()
  {
    return positionValid;
  }
}
