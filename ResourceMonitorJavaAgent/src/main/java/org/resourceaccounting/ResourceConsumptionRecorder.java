package org.resourceaccounting;

import org.resourceaccounting.ResourceConsumptionRecorderMBean;
import org.resourceaccounting.ResourcePrincipal;
import org.resourceaccounting.binder.ResourceCounter;

/**
 * Created with IntelliJ IDEA.
 * User: inti
 * Date: 4/23/13
 * Time: 5:24 PM
 * To change this template use File | Settings | File Templates.
 */
public class ResourceConsumptionRecorder implements ResourceConsumptionRecorderMBean {
    @Override
    public long getMemoryConsumption(ResourcePrincipal appId) {
        return ResourceCounter.getNbObjects(appId);
    }

    @Override
    public long getExecutedInstruction(ResourcePrincipal appId) {
        return ResourceCounter.getNbInstructions(appId);
    }

    @Override
    public ResourcePrincipal[] getApplications() {
        return ResourceCounter.getApplications();
    }
}
