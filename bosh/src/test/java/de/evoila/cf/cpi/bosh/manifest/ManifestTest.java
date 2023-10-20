package de.evoila.cf.cpi.bosh.manifest;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.core.io.ClassPathResource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.util.Properties;

public abstract class ManifestTest {
    public static final String DEPLOYMENT_NAME = "deployment-name";
    public static final String RELEASE_NAME = "release";
    public static final String RELEASE_VERSION = "latest";

    public static final int CANARIES = 1;
    public static final String UPDATE_WATCH_TIME = "15000-30000";
    public static final String CANARY_WATCH_TIME = "9000-55000";
    public static final int MAX_IN_FLIGHT = 2;

    public static final String R_POOL_NAME = "R-Pool";
    public static final String R_NETWORK_NAME = "default";

    public static final String STEMCELL_NAME = "bosh-warden-boshlite-ubuntu-trusty-go_agent";
    public static final String STEMCELL_VERSION = "latest";

    public static final String JOB_NAME = "job1";
    public static final int INSTANCES = 3;
    public static final int JOB_PERSISTENT_DISK = 10240;
    public static final String JOB_RESOURCE_POOL_NAME = "R-Pool";

    public static final String TEMPLATE1 = "default";
    public static final String TEMPLATE2 = "mongodb3";

    public static final String GATEWAY = "10.241.143.1";
    public static final String RANGE = "10.241.143.0/24";
    public static final String DNS = "10.254.174.10";
    public static final String N_CP_NAME = "Network";
    public static final String NETWORK_NAME = "Network";
    public static final String STATIC_IP = "10.241.143.44";
    public static final String RESERVED = "10.241.143.44";

    public static final int COMP_WORKERS = 1;
    public static final String COMP_NETWORK = "default";

    static boolean propertiesSet = false;

    public String readFile(String path) throws IOException, URISyntaxException {
        InputStream inputStream = new ClassPathResource(path).getInputStream();
        return readFile(inputStream);
    }

    protected String readFile(InputStream inputStream) throws IOException, URISyntaxException {
        BufferedReader reader =new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder stringBuilder = new StringBuilder();

        String line = reader.readLine();
        while (line != null) {
            stringBuilder.append(line);
            stringBuilder.append("\n");
            line = reader.readLine();
        }

        return stringBuilder.toString();
    }

    /**
     * Checks whether any of the necessary environment variables are set for the BoshProperties class.
     * If this is not the case, dummy values will be written into all necessary variables.
     * Furthermore {@linkplain #propertiesSet} will be set to true, to indicate that dummy variables are set.
     * For cleanup see {@linkplain #cleanupEnvVars()}.
     */
    @BeforeAll
    public static void setupEnvVarsForBoshProperties() {
        // Needed because the BoshProperties class is depending on these environment variables
        // and the DeploymentManager class has a dependency on BoshProperties.
        // If more configuration beans need environment variables,
        // a test configuration file would be a more elegant way to solve this problem.

        Properties props = System.getProperties();

        if (! (props.containsKey("bosh.authentication")
                || props.containsKey("bosh.authentication")
                || props.containsKey("bosh.host")
                || props.containsKey("bosh.stemcellOs")
                || props.containsKey("bosh.stemcellVersion")
                || props.containsKey("bosh.username")
                || props.containsKey("bosh.password"))) {

            propertiesSet = true;
            props.put("bosh.authentication", "dummy-authentication");
            props.put("bosh.host", "dummy-host");
            props.put("bosh.stemcellOs", "dummy-stemcellOs");
            props.put("bosh.stemcellVersion", "dummy-stemcellVersion");
            props.put("bosh.username", "dummy-username");
            props.put("bosh.password", "dummy-password");
            System.setProperties(props);
        }
    }

    /**
     *  Checks via {@linkplain #propertiesSet} whether dummy environment variables were set before.
     *  If this is the case, these variables are removed from the properties.
     */
    @AfterAll
    public static void cleanupEnvVars() {
        if (propertiesSet) {
            Properties props = System.getProperties();
            props.remove("bosh.authentication");
            props.remove("bosh.host");
            props.remove("bosh.stemcellOs");
            props.remove("bosh.stemcellVersion");
            props.remove("bosh.username");
            props.remove("bosh.password");
            System.setProperties(props);
        }
    }

}
