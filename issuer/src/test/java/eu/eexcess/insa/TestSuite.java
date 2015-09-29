package eu.eexcess.insa;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ TestExternalServices.class, TestLoggingServices.class, TestPeasServices.class, TestRecommenderServices.class })
public class TestSuite {

}
