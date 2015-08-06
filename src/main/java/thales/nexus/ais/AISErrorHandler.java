package thales.nexus.ais;

import nl.esi.metis.aisparser.HandleInvalidInput;
import nl.esi.metis.aisparser.VDMLine;
import nl.esi.metis.aisparser.VDMMessage;
import nl.esi.metis.aisparser.provenance.Provenance;


public class AISErrorHandler implements HandleInvalidInput
{
  @Override
  public void handleInvalidVDMMessage(VDMMessage invalidVDMMessage)
  {
    // System.err.println("Error VDM Message : "+invalidVDMMessage.getProvenance().getProvenanceTree(""));
  }


  @Override
  public void handleInvalidVDMLine(VDMLine invalidVDMLine)
  {
    // System.err.println("Error VDM Line : "+
    // invalidVDMLine.getProvenance().getProvenanceTree(""));
  }


  @Override
  public void handleInvalidSensorData(Provenance source, String sensorData)
  {
    // System.err.println("Error sensor data : "+ sensorData);
  }
}
