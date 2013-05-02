import org.resourceaccounting.ResourceConsumptionRecorderMBean;
import org.resourceaccounting.ResourcePrincipal;

import java.util.HashSet;
import java.util.Set;

/**
* Created with IntelliJ IDEA.
* User: inti
* Date: 4/27/13
* Time: 2:23 PM
* To change this template use File | Settings | File Templates.
*/
class MBeanPollster extends Thread {
    private final ResourceConsumptionRecorderMBean bean;
    private Set<ResourcePrincipal> principals;

    private AppBehaviorObserver appBehaviorObserver = null;


    MBeanPollster(ResourceConsumptionRecorderMBean beanProxy) {
        this.bean = beanProxy;
        principals = new HashSet<ResourcePrincipal>();
    }

    @Override
    public void run() {
        boolean stop = false;
        while (!isInterrupted() && !stop) try {
            Thread.sleep(1000);
            if (appBehaviorObserver == null) continue;
            ResourcePrincipal[] tmp = bean.getApplications();

            Set<ResourcePrincipal> newSet = new HashSet<ResourcePrincipal>(tmp.length);

            for (int i = 0; i < tmp.length; i++) {
                if (!principals.contains(tmp[i])) appBehaviorObserver.newApp(tmp[i]);
                else appBehaviorObserver.informationChange(tmp[i]);
                newSet.add(tmp[i]);
            }

            for (ResourcePrincipal rp : principals)
                if (!newSet.contains(rp))
                    appBehaviorObserver.removeApp(rp);

            principals = newSet;

            stop = appBehaviorObserver.isStopped();

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void setAppBehaviorObserver(AppBehaviorObserver appBehaviorObserver) {
        this.appBehaviorObserver = appBehaviorObserver;
    }
}
