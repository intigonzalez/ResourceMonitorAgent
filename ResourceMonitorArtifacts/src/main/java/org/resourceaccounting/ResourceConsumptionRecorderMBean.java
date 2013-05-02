package org.resourceaccounting;

import org.resourceaccounting.ResourcePrincipal;

/**
 * Created with IntelliJ IDEA.
 * User: inti
 * Date: 4/23/13
 * Time: 5:24 PM
 * To change this template use File | Settings | File Templates.
 */
public interface ResourceConsumptionRecorderMBean {
    public long getMemoryConsumption(ResourcePrincipal appId);
    public long getExecutedInstruction(ResourcePrincipal appId);
    public ResourcePrincipal[] getApplications();
}
