package thales.nexus.ais;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.Scanner;

import thales.nexus.TrackStore;
import nl.esi.metis.aisparser.AISParser;
import nl.esi.metis.aisparser.provenance.Provenance;


/** */
public class AISReaderThread implements Runnable
{
  private AISParser parser;

  private Scanner scanner;

  private boolean process = true;

  // Used by the test data inputs. Test data from a file is in the format "timestamp,RAW_AIS_DATA",
  // where timestamp is in ms since epoch
  private boolean inputStreamContainsTimestamp = false;

  long lastSendTime = 0;
  
  private boolean writeRawSensorDataToFile = false;

  private PrintWriter aisOutputWriter = null;


  public AISReaderThread(InputStream in, TrackStore trackStore)
  {
    this.scanner = new Scanner(in);
    AISMessageHandler messageHandler = new AISMessageHandler(trackStore);
    AISErrorHandler errorHandler = new AISErrorHandler();
    parser = new AISParser(messageHandler, errorHandler);

  }


  private void usleep(long ms)
  {
    try
    {
      if (ms > 0)
      {
        Thread.sleep(ms);
      }
    }
    catch (InterruptedException e)
    {

    }
  }


  // This method processes AIS data that is read in from a text file.
  // This data is in the format "timestamp, AIS_RAW_SENTANCE"
  private String preProcessTestFile(String line)
  {
    // 1. Sleep the amount of ms between this sample and the previous one
    String timestampStr = line.split(",")[0];
    long timestamp = Long.valueOf(timestampStr).longValue();
    if (lastSendTime > 0)
    {
      usleep(timestamp - lastSendTime);
    }
    lastSendTime = timestamp;

    // 2. Strip of timestamp field
    // System.out.println(" PRE:" + line);
    line = line.split("!")[1]; // line.replaceFirst("^*!", "!");
    line = "!" + line;
    // System.out.println("POST:" + line);

    return line;
  }


  public void run()
  {
    Provenance p = new MyProvenance();

    while (process && scanner.hasNext())
    {
      String line = scanner.nextLine();

      // We must be using test data file, so wait the required amount of ms and strip of the
      // timestamp field
      if (inputStreamContainsTimestamp)
      {
        line = preProcessTestFile(line);
      }

      try
      {
        // System.out.println("AIS: " + line);
        parser.handleSensorData(p, line);
        
        // Write the sensor data to file
        if(writeRawSensorDataToFile )
        {
          writeAISLineToFile(line);
        }
      }
      catch (Exception e)
      {
        System.out.println("SerialReaderThread::run() Invalid AIS data received: " + line);
      }

    }
  }


  private void writeAISLineToFile(String line)
  {
    if(aisOutputWriter == null)
    {
      try
      {         
        aisOutputWriter  = new PrintWriter("ais.txt");
      }
      catch (FileNotFoundException e)
      {        
        e.printStackTrace();
        return;
      }
    }
    aisOutputWriter.println(System.currentTimeMillis() + "," + line);
    aisOutputWriter.flush();
  }


  public void stop()
  {
    // TODO: this may not actually stop the thread until it receives something new
    process = false;
  }


  public void setInputStreamContainsTimestamps(boolean val)
  {
    inputStreamContainsTimestamp = val;
  }

}