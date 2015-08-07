package thales.nexus;

public class SurfaceTrack extends Track 
{
  
  public int navStatus;
  public String marking;
  public int imo;
  public int shipAndCargoType;
  public String callSign;
  public int length;
  public int width;
  public double draught;
  public String destination;
  public long eta;
  private String mmsi;

  public SurfaceTrack()
  {
    type = "surface";
    altitude = 0.0;
  }

  /**
   * @return the mmsi
   */
  public String getMmsi()
  {
    return mmsi;
  }

  /**
   * @param mmsi the mmsi to set
   */
  public void setMmsi(String mmsi)
  {
    this.mmsi = mmsi;
    // For AIS we use MMSI as unique identifier
    id = mmsi;
  }

}
