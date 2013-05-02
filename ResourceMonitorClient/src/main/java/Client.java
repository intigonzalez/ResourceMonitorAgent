
import org.resourceaccounting.ResourceConsumptionRecorderMBean;

import javax.management.JMX;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import java.io.IOException;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: inti
 * Date: 4/25/13
 * Time: 1:44 PM
 * To change this template use File | Settings | File Templates.
 */
public class Client {

    public static void main(String[] args) throws IOException, InterruptedException {
        JMXServiceURL url =
                new JMXServiceURL("service:jmx:rmi:///jndi/rmi://:9999/jmxrmi");
        JMXConnector jmxc = JMXConnectorFactory.connect(url, null);
        MBeanServerConnection mbsc =
                jmxc.getMBeanServerConnection();
        echo("\nDomains:");
        String domains[] = mbsc.getDomains();
        Arrays.sort(domains);
        for (String domain : domains) {
            echo("\tDomain = " + domain);
        }

        echo("\nMBeanServer default domain = " + mbsc.getDefaultDomain());

        echo("\nMBean count = " + mbsc.getMBeanCount());
        echo("\nQuery MBeanServer MBeans:");
        Set<ObjectName> names =
                new TreeSet<ObjectName>(mbsc.queryNames(null, null));
        ObjectName resourceBean = null;
        for (ObjectName name : names) {
            echo("\tObjectName = " + name);
            if (name.getCanonicalName().startsWith("org.resourceaccounting"))
                resourceBean = name;
        }

        ResourceConsumptionRecorderMBean beanProxy = JMX.newMBeanProxy(mbsc,
                resourceBean, ResourceConsumptionRecorderMBean.class);

        MBeanPollster th = new MBeanPollster(beanProxy);
        AppViewer appViewer = new AppViewer();
        th.setAppBehaviorObserver(appViewer);
        th.start();
        th.join();

        jmxc.close();
        System.exit(0);
    }

    private static void echo(String s) {
        System.out.println(s);
    }
}
