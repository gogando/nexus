package thales.nexus.ais;

import nl.esi.metis.aisparser.HandleInvalidInput;
import nl.esi.metis.aisparser.VDMLine;
import nl.esi.metis.aisparser.VDMMessage;
import nl.esi.metis.aisparser.provenance.Provenance;


public class AISErrorHandler implements HandleInvalidInput
{
  public void handleInvalidVDMMessage(VDMMessage invalidVDMMessage)
  {
    // System.err.println("Error VDM Message : "+invalidVDMMessage.getProvenance().getProvenanceTree(""));
  }


  public void handleInvalidVDMLine(VDMLine invalidVDMLine)
  {
    // System.err.println("Error VDM Line : "+
    // invalidVDMLine.getProvenance().getProvenanceTree(""));
  }


  public void handleInvalidSensorData(Provenance source, String sensorData)
  {
    // System.err.println("Error sensor data : "+ sensorData);
  }
}
