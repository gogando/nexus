package thales.nexus.ais;

import java.awt.Image;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.fasterxml.jackson.annotation.JsonProperty;


import nl.esi.metis.aisparser.AISMessage;
import nl.esi.metis.aisparser.AISMessage05;
import nl.esi.metis.aisparser.AISMessage19;
import nl.esi.metis.aisparser.AISMessage21;
import nl.esi.metis.aisparser.AISMessage24;
import nl.esi.metis.aisparser.AISMessage24PartA;
import nl.esi.metis.aisparser.AISMessage24PartB;
import nl.esi.metis.aisparser.AISMessageClassBPositionReport;
import nl.esi.metis.aisparser.AISMessagePositionReport;
import nl.esi.metis.aisparser.HandleAISMessage;
import nl.esi.metis.aisparser.UtilsDimensions30;
import nl.esi.metis.aisparser.UtilsEta;
import nl.esi.metis.aisparser.UtilsNavStatus;
import nl.esi.metis.aisparser.UtilsShipType8;


class AISMessageHandler implements HandleAISMessage
{
  private final static boolean IS_WINDOWS = System.getProperty("os.name").startsWith("Windows");

  private TrackStore trackStore = null;

  // private AISJDBCInterface jdbcInterface = new AISJDBCInterface();

  // private AISSimTrackManager aisSimTrackManager = new AISSimTrackManager();

  /** The number of AIS messages that were found. */
  public long messagesCount = 0;

  /** The number of annotated AIS messages that were found. */
  public long annotatedMessagesCount = 0;

  /** specific annotation counts */
  public long nrOfFillBitsAnnotationCount = 0;

  public long channelIDAnnotationCount = 0;

  public long illegalValueAnnotationCount = 0;

  public long changedChannelIdAnnotationCount = 0;

  public long inconsistentLengthForTypeAnnotationCount = 0;

  public long AISHypothesisAnnotationCount = 0;

  public long otherCount = 0;


  public AISMessageHandler(TrackStore trackStore)
  {
    this.trackStore = trackStore;
  }


  public void checkIfImageExists(int mmsi)
  {
    if (!IS_WINDOWS) // TODO: need a windows way to do wget
    {
      String filename = Integer.toString(mmsi) + ".jpg";

      BufferedImage bufferedImage = null;

      try
      {
        bufferedImage = ImageIO.read(new File(filename).toURI().toURL());
      }
      catch (IOException e)

      {

        String cmd = "wget " + "http://photos.marinetraffic.com/ais/showphoto.aspx?mmsi=" + mmsi +
                     " -O " + mmsi + ".jpg";
        System.out.println("Running cmd: " + cmd);
        try
        {
          Runtime.getRuntime().exec(cmd);
        }
        catch (IOException e1)
        {
          // TODO Auto-generated catch block
          e1.printStackTrace();
        }
      }
    }
  }


  /**
   * Handles a single AIS message. In this case, if the message has type 1 the count is incremented.
   * 
   * @param message the message to be handled
   */
  public void handleAISMessage(AISMessage message)
  {
    Track track = null;
    // System.out.println(message);
    //
    // System.out.println("Got an AIS message of type " + message.getMessageID());

    // Definitions of each message is available here: http://gpsd.berlios.de/AIVDM.html
    switch (message.getMessageID())
    {
      case 1: // Types 1, 2 and 3: Position Report Class A
      case 2:
      case 3:
        // These are all position messages
        AISMessagePositionReport posReport = (AISMessagePositionReport) message;
        track = updatePosition(new AISPositionData(posReport));
        track.setNavStatus(UtilsNavStatus.toString(posReport.getNavigationalStatus()));
        checkIfImageExists(posReport.getUserID());
        break;

      // Type 4: Base Station Report (currently ignoring)

      case 5: // Type 5: Static and Voyage Related Data
        AISMessage05 msg5 = (AISMessage05) message;
        // aisSimTrackManager.updateSimTrackName(msg5.getUserID(), msg5.getName());
        // aisSimTrackManager.updateSimTrackType(msg5.getUserID(),
        // msg5.getTypeOfShipAndCargoType());
        // jdbcInterface.update(new AISVesselData(msg5));

        track = trackStore.get(Integer.toString(msg5.getUserID()));

        if (track != null)
        {
          track.setMarking(msg5.getName());
          track.setImo(msg5.getImoNumber());
          track.setShipAndCargoType(msg5.getTypeOfShipAndCargoType());
          track.setCallSign(msg5.getCallSign());
          track.setLength(UtilsDimensions30.getBow(msg5.getDimension()) +
                          UtilsDimensions30.getStern(msg5.getDimension()));
          track.setWidth(UtilsDimensions30.getStarboard(msg5.getDimension()) +
                         UtilsDimensions30.getPort(msg5.getDimension()));
          track.setDraught(msg5.getMaximumPresentStaticDraught() / 10.0);
          track.setDestination(msg5.getDestination());
          // TODO track.setEta(
          if(UtilsEta.isSemanticallyCorrectValue(msg5.getEta()))
          {            
            track.setEta(UtilsEta.convertToTime(msg5.getEta(), System.currentTimeMillis()/1000.0));
          }

        }
        break;

      // Type 6: Binary Addressed Message (currently ignoring)
      // Type 7: Binary Acknowledge (currently ignoring)
      // Type 8: Binary Broadcast Message (currently ignoring)
      // Type 9: Standard SAR Aircraft Position Report (currently ignoring)
      // Type 10: UTC/Date Inquiry (currently ignoring)
      // Type 11: UTC/Date Response (currently ignoring)
      // Type 12: Addressed Safety-Related Message (currently ignoring)
      // Type 13: Safety-Related Acknowledgement (currently ignoring)
      // Type 14: Safety-Related Broadcast Message (currently ignoring)
      // Type 15: Interrogation (currently ignoring)
      // Type 16: Assignment Mode Command (currently ignoring)
      // Type 17: DGNSS Broadcast Binary Message (currently ignoring)

      case 18: // Type 18: Standard Class B CS Position Report
        track = updatePosition(new AISPositionData((AISMessageClassBPositionReport) message));
        break;

      case 19: // Type 19: Extended Class B CS Position Report
        AISMessage19 msg19 = (AISMessage19) message;
        // aisSimTrackManager.updateSimTrackName(msg19.getUserID(), msg19.getName());
        // aisSimTrackManager.updateSimTrackType(msg19.getUserID(),
        // msg19.getTypeOfShipAndCargoType());
        // jdbcInterface.update(new AISVesselData(msg19));
        track = updatePosition(new AISPositionData((AISMessageClassBPositionReport) message));
        if (track != null)
        {
          track.setMarking(msg19.getName());
        }
        break;

      // Type 20: Data Link Management Message (currently ignoring)
        
      // Type 21: Aid-to-Navigation Report
      // Identification and location message to be emitted by aids to navigation such as buoys and lighthouses.
      case 21:
        AISMessage21 msg21 = (AISMessage21) message;
                
        NavigationAid originatingAid = navAidStore.get(msg21.getUserID());

        if (originatingAid == null)
        {
          System.out.println("New track: " + msg21.getUserID());
          NavigationAid aid = new NavigationAid();
          aid.setMmsi(msg21.getUserID());
          aid.setLatitude(msg21.getLatitudeInDegrees());
          aid.setLongitude(msg21.getLongitudeInDegrees());
          aid.setName(msg21.getNameOfAtoN());
          aid.setTimestamp(System.currentTimeMillis());
          navAidStore.add(aid);          
        }
        else
        {
          originatingAid.setMmsi(msg21.getUserID());
          originatingAid.setLatitude(msg21.getLatitudeInDegrees());
          originatingAid.setLongitude(msg21.getLongitudeInDegrees());
          originatingAid.setName(msg21.getNameOfAtoN());
          originatingAid.setTimestamp(System.currentTimeMillis());
        }
        
        break;
        
      // Type 22: Channel Management (currently ignoring)
      // Type 23: Group Assignment Command (currently ignoring)

      case 24: // Type 24: Static Data Report
        AISMessage24 msg24 = (AISMessage24) message;
        track = trackStore.get(Integer.toString(msg24.getUserID()));
        
        if (msg24.getPartNumber() == 0) // Part A
        {
          AISMessage24PartA msg24a = (AISMessage24PartA) msg24;
          // jdbcInterface.update(new AISVesselData(msg24a));        

          if (track != null)
          {
            track.setMarking(msg24a.getName());            
          }
        }
        else
        // Part B
        {
          AISMessage24PartB msg24b = (AISMessage24PartB) msg24;
          // jdbcInterface.update(new AISVesselData(msg24b));
          if (track != null)
          {
            track.setShipAndCargoType(msg24b.getTypeOfShipAndCargoType());            
          }
        }
        break;

      // Type 25: Single Slot Binary Message (currently ignoring)
      // Type 26: Multiple Slot Binary Message (currently ignoring)
      // Type 27: Long Range AIS Broadcast message (currently ignoring)

      default:
//        System.out.println("Received an unhandled AIS message [" + message.getMessageID() + "]");
    }

  }


  private Track updatePosition(AISPositionData data)
  {
    // aisSimTrackManager.update(data);
    Track originatingTrack = trackStore.get(Integer.toString(data.getMMSI()));

    if (originatingTrack == null)
    {
      System.out.println("New track: " + data.getMMSI());
      Track track = new Track();
      track.setId(Integer.toString(data.getMMSI()));
      track.setLatitude(data.getLatitude());
      track.setLongitude(data.getLongitude());
      track.setElevation(0.0);

      track.setCourse(data.getCourse());
      track.setVelocity(data.getSpeed());
      track.setPlatform(3); // 3 = surface
      track.setTimestamp(System.currentTimeMillis());
      track.setMmsi(data.getMMSI());
      
      // track.setMarking(Integer.toString(data.getMMSI())); // will be updated
      trackStore.add(track);
      return track;
    }
    else
    {
      originatingTrack.setLatitude(data.getLatitude());
      originatingTrack.setLongitude(data.getLongitude());
      originatingTrack.setCourse(data.getCourse());
      originatingTrack.setVelocity(data.getSpeed());
      originatingTrack.setTimestamp(System.currentTimeMillis());
      return originatingTrack;
    }
  }
}
