package eu.eexcess.insa;

/**
 * Enumeration of the three query formats:  
 * <ul>
 * 	<li>QF1: allows to represent queries of the form q = (t1 t2). This format is the only one handled by the federated recommender.</li>
 * 	<li>QF2: allows to represent composed queries of the form q = [(t1 t2), (t3 t4), (t5 t6)]. </li> 
 *  <li>QF3: allows to represent queries of the form q = (id1 id2 id3). Each idX corresponds to the identifier of a resource. </li>
 * </ul>
 * @author Thomas Cerqueus
 */
public enum QueryFormats {
	QF1, 
	QF2, 
	QF3; 
}
