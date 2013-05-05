/**
 * Created with IntelliJ IDEA.
 * User: inti
 * Date: 4/22/13
 * Time: 2:57 PM
 * To change this template use File | Settings | File Templates.
 */
package org.resourceaccounting.binder;

import org.resourceaccounting.ResourcePrincipal;

public class ResourceCounter {
    private static ResourceCounter ourInstance = new ResourceCounter();

    private static ResourceCounter getInstance() {
        return ourInstance;
    }

    private ResourceCounter() {
    }

    private int count = 0;
    private ResourcePrincipal[] loaders = new ResourcePrincipal[1000];

    private ResourcePrincipal[] innerGetApplications() {
        synchronized (this) {
            ResourcePrincipal[] pTemp = new ResourcePrincipal[count];
            System.arraycopy(loaders, 0 , pTemp, 0, count);
            return pTemp;
        }
    }

    private final void innerIncreaseInstructions(int n, int index) {
        synchronized (loaders[index]) {
            loaders[index].increaseExecutedInstructions(n);
        }
    }

    private final void innerIncreaseObjects(int n, int index) {
        synchronized (loaders[index]) {
            loaders[index].increaseOwnedObjects(n);
        }
    }

    private void innerIncreaseBytesSent(int n, int index) {
        synchronized (loaders[index]) {
            loaders[index].increaseBytesSent(n);
        }
    }

    private void innerIncreaseBytesReceived(int n, int index) {
        synchronized (loaders[index]) {
            loaders[index].increaseBytesReceived(n);
        }
    }

    private final long innerGetNbObjects(int index) {
        synchronized (loaders[index]) {
            return loaders[index].getAllocatedObjects();
        }
    }

    private final long innerGetNbInstructions(int index) {
        synchronized (loaders[index]) {
            return loaders[index].getExecutedInstructions();
        }
    }

    private long innerGetNbBytesSent(int index) {
        synchronized (loaders[index]) {
            return loaders[index].getBytesSent();
        }
    }

    private long innerGetNbBytesReceived(int index) {
        synchronized (loaders[index]) {
            return loaders[index].getBytesReceived();
        }
    }

    private static void increaseInstructions(int n, int index) {

        ourInstance.innerIncreaseInstructions(n, index);
    }

    private static void increaseObjects(int n, int index) {
        ourInstance.innerIncreaseObjects(n, index);
    }

    /**
     * Find or create a ResourcePrincipal. This method is doing a very important assumption:
     * ResourcePrincipals are never removed from the set; hence, any new ResourcePrincipal must be
     * different to all previous
     * @param principal
     * @return
     */
    private int search(ResourcePrincipal principal) {
        synchronized (this) {
            //assert principal != null;
//            if (principal == null && count == 0) {
//                principal = new ClassLoaderResourcePrincipal(null);
//                loaders[count++] = principal;
//                return 0;
//            }
//            else
            if (principal == null) {
                return 0;
            }

            for (int i = 0 ; i < count ; i++) {
                if (loaders[i].equals(principal))
                    return i;
            }
            if (count == loaders.length) {
                ResourcePrincipal[] pTmp = new ResourcePrincipal[count*2];
                System.arraycopy(loaders, 0 , pTmp, 0, count);
                loaders = pTmp;
            }
            loaders[count] = principal;
            return count++;
        }
    }

    public static ResourcePrincipal[] getApplications() {
        return ourInstance.innerGetApplications();
    }

    public static void increaseInstructions(int n, ResourcePrincipal principal) {
        int index = ourInstance.search(principal);
        ourInstance.innerIncreaseInstructions(n, index);
    }

    public static void increaseObjects(int n, ResourcePrincipal principal) {
        int index = ourInstance.search(principal);
        ourInstance.innerIncreaseObjects(n, index);
    }

    public static void increaseObjectsAndInstructions(int nbObjects, int nbInstructions, ResourcePrincipal principal) {
        int index = ourInstance.search(principal);
        ourInstance.innerIncreaseInstructions(nbInstructions, index);
        ourInstance.innerIncreaseObjects(nbObjects, index);
    }

    public static void increaseBytesSent(int n, ResourcePrincipal principal) {
        int index = ourInstance.search(principal);
        ourInstance.innerIncreaseBytesSent(n, index);
    }

    public static void increaseBytesReceived(int n, ResourcePrincipal principal) {
        int index = ourInstance.search(principal);
        ourInstance.innerIncreaseBytesReceived(n, index);
    }

    public static long getNbObjects(ResourcePrincipal principal) {
        int index = ourInstance.search(principal);
        return ourInstance.innerGetNbObjects(index);
    }

    public static long getNbInstructions(ResourcePrincipal principal) {
        int index = ourInstance.search(principal);
        return ourInstance.innerGetNbInstructions(index);
    }

    public static long getNbBytesSent(ResourcePrincipal principal) {
        int index = ourInstance.search(principal);
        return ourInstance.innerGetNbBytesSent(index);
    }

    public static long getNbBytesReceived(ResourcePrincipal principal) {
        int index = ourInstance.search(principal);
        return ourInstance.innerGetNbBytesReceived(index);
    }
}
