package org.resourceaccounting.binder;

import org.resourceaccounting.ResourcePrincipal;

/**
 * Created with IntelliJ IDEA.
 * User: inti
 * Date: 4/29/13
 * Time: 5:28 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class AbstractResourcePrincipal<T> implements ResourcePrincipal {

    private transient T associatedObject;

    private static int lastID = 1;
    protected long nbInstructions;
    protected long nbObjects;
    protected int id;
    protected int nbBytesSent;
    protected int nbBytesReceived;

    /**
     * This is not thread safe
     * @param object
     */
    protected AbstractResourcePrincipal(T object) {
        associatedObject = object;
        id = lastID++;
    }

    @Override
    public final void increaseExecutedInstructions(int n) {
        nbInstructions += n;
    }

    @Override
    public final void increaseOwnedObjects(int n) {
        nbObjects += n;
    }

    @Override
    public void increaseBytesSent(int n) {
        nbBytesSent += n;
    }

    @Override
    public void increaseBytesReceived(int n) {
        nbBytesReceived += n;
    }

    @Override
    public final long getExecutedInstructions() {
        return nbInstructions;
    }

    @Override
    public final long getAllocatedObjects() {
        return nbObjects;
    }

    @Override
    public long getBytesSent() {
        return nbBytesSent;
    }

    @Override
    public long getBytesReceived() {
        return nbBytesReceived;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AbstractResourcePrincipal that = (AbstractResourcePrincipal) o;

        if (id != that.id) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id;
    }


}
