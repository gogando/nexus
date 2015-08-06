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

  long lastSendTime = 0;


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

      try
      {
        // System.out.println("AIS: " + line);
        parser.handleSensorData(p, line);
        
     
      }
      catch (Exception e)
      {
        System.out.println("SerialReaderThread::run() Invalid AIS data received: " + line);
      }

    }
  }


 

  public void stop()
  {
    // TODO: this may not actually stop the thread until it receives something new
    process = false;
  }



}