/**
 * Created with IntelliJ IDEA.
 * User: inti
 * Date: 4/23/13
 * Time: 3:09 PM
 * To change this template use File | Settings | File Templates.
 */

package org.resourceaccounting.binderinjector;


import org.resourceaccounting.ResourceConsumptionRecorder;

import javax.management.*;
import java.lang.instrument.Instrumentation;
import java.lang.management.ManagementFactory;

public class ResourceCounterAgent {
    public static void premain(String agentArgs, Instrumentation inst) {
        MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
        ObjectName name;
        try {
            name = new ObjectName("org.resourceaccounting:type=ResourceConsumptionRecorder");
            ResourceConsumptionRecorder mbean = new ResourceConsumptionRecorder();
            mbs.registerMBean(mbean, name);
        } catch (MalformedObjectNameException e) {
            e.printStackTrace();
        } catch (NotCompliantMBeanException e) {
            e.printStackTrace();
        } catch (InstanceAlreadyExistsException e) {
            e.printStackTrace();
        } catch (MBeanRegistrationException e) {
            e.printStackTrace();
        }
        boolean debug = agentArgs != null && agentArgs.length() > 0 &&  agentArgs.equals("debug");
        inst.addTransformer(new BinderClassTransformer(inst, debug));
    }
}
