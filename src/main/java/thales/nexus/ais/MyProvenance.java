package thales.nexus.ais;

import java.util.List;

import nl.esi.metis.aisparser.annotations.Annotation;
import nl.esi.metis.aisparser.provenance.Provenance;

public class MyProvenance implements Provenance
{

  public List<Annotation> getAnnotations()
  {
    // TODO Auto-generated method stub
    return null;
  }



  public String getProvenanceTree(String arg0)
  {
    // TODO Auto-generated method stub
    return "AIS";
  }



  public double getTime()
  {
    // TODO Auto-generated method stub
    return System.currentTimeMillis()/1000;
  }

}
