package thales.nexus;

public class AirTrack extends Track {

	private String flightId;
	public int altitude;
	public String icao;
	public boolean onGround;
	public String squawk;
	
	public AirTrack()
	{
	  type = "air";
	}

  /**
   * @return the flightId
   */
  public String getFlightId()
  {
    return flightId;
  }

  /**
   * @param flightId the flightId to set
   */
  public void setFlightId(String flightId)
  {
    this.flightId = flightId;
    // For ADS-B we use Flight ID as unique identifier
    id = flightId;
  }

}
