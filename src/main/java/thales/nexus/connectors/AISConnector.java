package thales.nexus.connectors;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

import javax.inject.Named;

import nl.esi.metis.aisparser.AISMessage;
import nl.esi.metis.aisparser.AISMessage05;
import nl.esi.metis.aisparser.AISMessage19;
import nl.esi.metis.aisparser.AISMessage21;
import nl.esi.metis.aisparser.AISMessage24;
import nl.esi.metis.aisparser.AISMessage24PartA;
import nl.esi.metis.aisparser.AISMessage24PartB;
import nl.esi.metis.aisparser.AISMessageClassBPositionReport;
import nl.esi.metis.aisparser.AISMessagePositionReport;
import nl.esi.metis.aisparser.AISParser;
import nl.esi.metis.aisparser.HandleAISMessage;
import nl.esi.metis.aisparser.HandleInvalidInput;
import nl.esi.metis.aisparser.UtilsDimensions30;
import nl.esi.metis.aisparser.UtilsEta;
import nl.esi.metis.aisparser.UtilsNavStatus;
import nl.esi.metis.aisparser.VDMLine;
import nl.esi.metis.aisparser.VDMMessage;
import nl.esi.metis.aisparser.provenance.Provenance;
import thales.nexus.Connector;
import thales.nexus.SurfaceTrack;
import thales.nexus.Track;
import thales.nexus.TrackStore;
import thales.nexus.ais.AISPositionData;
import thales.nexus.ais.MyProvenance;

import com.google.inject.Inject;
import com.google.inject.Singleton;


@Singleton
@Named("ais")
public class AISConnector implements Connector, HandleAISMessage, HandleInvalidInput
{

  private final Provenance provenance = new MyProvenance();

  private final AISParser parser = new AISParser(this, this);

  private final TrackStore trackStore;

  private boolean running = false;


  @Inject
  public AISConnector(TrackStore trackStore)
  {
    this.trackStore = trackStore;
  }


  @Inject
  public void start()
  {
    this.running = true;
    new Server().start();
  }


  public void stop()
  {
    this.running = false;
  }

  private class Server extends Thread
  {

    public void run()
    {
      try (ServerSocket server = new ServerSocket(30001))
      {
        while (running)
        {
          new Client(server.accept()).start();
        }
      }
      catch (IOException e)
      {
        e.printStackTrace();
      }
    }
  }

  private class Client extends Thread
  {

    private final Socket client;


    public Client(Socket client)
    {
      this.client = client;
    }


    @Override
    public void run()
    {
      try (InputStream in = client.getInputStream())
      {
        InputStreamReader streamReader = new InputStreamReader(in);
        BufferedReader bufferedReader = new BufferedReader(streamReader);
        while (running)
        {
          String line = bufferedReader.readLine();
          parser.handleSensorData(provenance, line);
        }
      }
      catch (IOException e)
      {
        e.printStackTrace();
      }
    }

  }


  private Track updatePosition(AISPositionData data)
  {
    // aisSimTrackManager.update(data);
    SurfaceTrack originatingTrack = (SurfaceTrack) trackStore.get(Integer.toString(data.getMMSI()));

    if (originatingTrack == null)
    {
      SurfaceTrack track = new SurfaceTrack();
      track.setMmsi(Integer.toString(data.getMMSI()));
      track.latitude = data.getLatitude();
      track.longitude = data.getLongitude();
      track.course = data.getCourse();
      track.speed = data.getSpeed();
      // track.setTimestamp(System.currentTimeMillis());
      
      trackStore.put(String.valueOf(data.getMMSI()), (Track) track);
      return track;
    }
    else
    {
      originatingTrack.latitude = data.getLatitude();
      originatingTrack.longitude = data.getLongitude();
      originatingTrack.course = data.getCourse();
      originatingTrack.speed = data.getSpeed();
      // originatingTrack.setTimestamp(System.currentTimeMillis());
      return originatingTrack;
    }
  }


  @Override
  public void handleAISMessage(AISMessage message)
  {
    SurfaceTrack track = (SurfaceTrack) trackStore.getOrDefault(message.getUserID(),
                                                                new SurfaceTrack());
    switch (message.getMessageID())
    {
      case 1: // Types 1, 2 and 3: Position Report Class A
      case 2:
      case 3:
        // These are all position messages
        AISMessagePositionReport posReport = (AISMessagePositionReport) message;
        track = (SurfaceTrack) updatePosition(new AISPositionData(posReport));
        track.navStatus = posReport.getNavigationalStatus();
        // checkIfImageExists(posReport.getUserID());
        break;

      // Type 4: Base Station Report (currently ignoring)

      case 5: // Type 5: Static and Voyage Related Data
        AISMessage05 msg5 = (AISMessage05) message;
        // aisSimTrackManager.updateSimTrackName(msg5.getUserID(),
        // msg5.getName());
        // aisSimTrackManager.updateSimTrackType(msg5.getUserID(),
        // msg5.getTypeOfShipAndCargoType());
        // jdbcInterface.update(new AISVesselData(msg5));

        track = (SurfaceTrack) trackStore.get(Integer.toString(msg5.getUserID()));

        if (track != null)
        {
          track.marking = msg5.getName();
          track.imo = msg5.getImoNumber();
          track.shipAndCargoType = msg5.getTypeOfShipAndCargoType();
          track.callSign = msg5.getCallSign();
          track.length = UtilsDimensions30.getBow(msg5.getDimension()) +
                         UtilsDimensions30.getStern(msg5.getDimension());
          track.width = UtilsDimensions30.getStarboard(msg5.getDimension()) +
                        UtilsDimensions30.getPort(msg5.getDimension());
          track.draught = msg5.getMaximumPresentStaticDraught() / 10.0;
          track.destination = msg5.getDestination();
          // TODO track.setEta(
          if (UtilsEta.isSemanticallyCorrectValue(msg5.getEta()))
          {
            track.eta = UtilsEta.convertToTime(msg5.getEta(), System.currentTimeMillis() / 1000.0);
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
        track = (SurfaceTrack) updatePosition(new AISPositionData(
                                                                  (AISMessageClassBPositionReport) message));
        break;

      case 19: // Type 19: Extended Class B CS Position Report
        AISMessage19 msg19 = (AISMessage19) message;
        // aisSimTrackManager.updateSimTrackName(msg19.getUserID(),
        // msg19.getName());
        // aisSimTrackManager.updateSimTrackType(msg19.getUserID(),
        // msg19.getTypeOfShipAndCargoType());
        // jdbcInterface.update(new AISVesselData(msg19));
        track = (SurfaceTrack) updatePosition(new AISPositionData(
                                                                  (AISMessageClassBPositionReport) message));
        if (track != null)
        {
          track.marking = msg19.getName();
        }
        break;

      // Type 20: Data Link Management Message (currently ignoring)

      // Type 21: Aid-to-Navigation Report
      // Identification and location message to be emitted by aids to
      // navigation such as buoys and lighthouses.
      case 21:
        AISMessage21 msg21 = (AISMessage21) message;

        // NavigationAid originatingAid =
        // navAidStore.get(msg21.getUserID());
        //
        // if (originatingAid == null)
        // {
        // System.out.println("New track: " + msg21.getUserID());
        // NavigationAid aid = new NavigationAid();
        // aid.setMmsi(msg21.getUserID());
        // aid.setLatitude(msg21.getLatitudeInDegrees());
        // aid.setLongitude(msg21.getLongitudeInDegrees());
        // aid.setName(msg21.getNameOfAtoN());
        // aid.setTimestamp(System.currentTimeMillis());
        // navAidStore.add(aid);
        // }
        // else
        // {
        // originatingAid.setMmsi(msg21.getUserID());
        // originatingAid.setLatitude(msg21.getLatitudeInDegrees());
        // originatingAid.setLongitude(msg21.getLongitudeInDegrees());
        // originatingAid.setName(msg21.getNameOfAtoN());
        // originatingAid.setTimestamp(System.currentTimeMillis());
        // }

        break;

      // Type 22: Channel Management (currently ignoring)
      // Type 23: Group Assignment Command (currently ignoring)

      case 24: // Type 24: Static Data Report
        AISMessage24 msg24 = (AISMessage24) message;
        track = (SurfaceTrack) trackStore.get(Integer.toString(msg24.getUserID()));

        if (msg24.getPartNumber() == 0) // Part A
        {
          AISMessage24PartA msg24a = (AISMessage24PartA) msg24;
          // jdbcInterface.update(new AISVesselData(msg24a));

          if (track != null)
          {
            track.marking = msg24a.getName();
          }
        }
        else
        // Part B
        {
          AISMessage24PartB msg24b = (AISMessage24PartB) msg24;
          // jdbcInterface.update(new AISVesselData(msg24b));
          if (track != null)
          {
            track.shipAndCargoType = msg24b.getTypeOfShipAndCargoType();
          }
        }
        break;

      // Type 25: Single Slot Binary Message (currently ignoring)
      // Type 26: Multiple Slot Binary Message (currently ignoring)
      // Type 27: Long Range AIS Broadcast message (currently ignoring)

      default:
        // System.out.println("Received an unhandled AIS message [" +
        // message.getMessageID() + "]");
    }

    trackStore.put(Integer.toString(message.getUserID()), track);
  }


  @Override
  public void handleInvalidVDMMessage(VDMMessage invalidVDMMessage)
  {

  }


  @Override
  public void handleInvalidVDMLine(VDMLine invalidVDMLine)
  {

  }


  @Override
  public void handleInvalidSensorData(Provenance source, String sensorData)
  {

  }

}
