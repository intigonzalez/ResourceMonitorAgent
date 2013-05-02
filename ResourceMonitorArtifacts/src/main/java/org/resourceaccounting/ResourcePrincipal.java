package org.resourceaccounting;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: inti
 * Date: 4/25/13
 * Time: 11:02 AM
 * To change this template use File | Settings | File Templates.
 */
public interface ResourcePrincipal extends Serializable {
    void increaseExecutedInstructions(int n);
    void increaseOwnedObjects(int n);
    long getExecutedInstructions();
    long getAllocatedObjects();
}
