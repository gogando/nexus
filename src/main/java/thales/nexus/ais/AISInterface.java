package thales.nexus.ais;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;

import thales.nexus.TrackStore;




public class AISInterface
{
  private AISReaderThread readerThread = null;

  private Thread thread = null;


  public AISInterface(String address, int port, TrackStore trackStore) throws UnknownHostException, IOException
  {
    Socket socket = new Socket(address, port);
    readerThread = new AISReaderThread(socket.getInputStream(), trackStore);
  }



  public void startReaderThread()
  {
    thread = new Thread(readerThread);
    thread.start();
  }


  public void stopReaderThread()
  {
    readerThread.stop();
    try
    {
      thread.join();
    }
    catch (InterruptedException e)
    {
      System.out.println("AISInterface failed to rejoin thread. " + e.toString());
    }
  }



}