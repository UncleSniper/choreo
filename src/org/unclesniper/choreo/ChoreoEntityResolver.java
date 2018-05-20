package org.unclesniper.choreo;

import java.io.IOException;
import org.xml.sax.InputSource;

public interface ChoreoEntityResolver {

	InputSource resolveEntity(String choreoID) throws IOException, ChoreoException;

}
