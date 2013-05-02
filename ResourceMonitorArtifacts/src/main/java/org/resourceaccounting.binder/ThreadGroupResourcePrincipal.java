package org.resourceaccounting.binder;

import org.resourceaccounting.ResourcePrincipal;

import java.util.HashMap;

/**
 * Created with IntelliJ IDEA.
 * User: inti
 * Date: 4/30/13
 * Time: 6:07 PM
 * To change this template use File | Settings | File Templates.
 */
public class ThreadGroupResourcePrincipal extends AbstractResourcePrincipal<Long> {

    private String text = "MainThreadGroup";

    /**
     * This is not thread safe
     *
     * @param object
     */
    protected ThreadGroupResourcePrincipal(Long object) {
        super(object);
    }

    private static HashMap<String, ResourcePrincipal> map = new HashMap<String, ResourcePrincipal>();

    private static ResourcePrincipal unique = new ThreadGroupResourcePrincipal(-1L);


    public static String locateGroup(Thread th, String prefix) {
        String r = null;
        ThreadGroup tg = th.getThreadGroup();
        while (tg != null && !tg.getName().startsWith(prefix)) {
            tg = tg.getParent();
        }
        return (tg == null)? null : tg.getName();
    }

    public static ResourcePrincipal get(Thread thread) {
        //return unique;
        synchronized (map) {

            String nameOfGroup = locateGroup(thread, "kev/");
            if (nameOfGroup == null)
                return unique;

            if (map.containsKey(nameOfGroup))
                return map.get(nameOfGroup);

            ThreadGroupResourcePrincipal tmp = new ThreadGroupResourcePrincipal(thread.getId());
            tmp.text = nameOfGroup;
            map.put(nameOfGroup, tmp);
            return tmp;
        }
    }

    @Override
    public String toString() {
        return text;    //To change body of overridden methods use File | Settings | File Templates.
    }
}
