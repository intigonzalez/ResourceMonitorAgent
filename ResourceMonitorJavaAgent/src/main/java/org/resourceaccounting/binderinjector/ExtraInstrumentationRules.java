package org.resourceaccounting.binderinjector;

public class ExtraInstrumentationRules {
    public static boolean isInstrumentable(String className) {
//        System.out.printf("Asking if instrumentable %s\n", className);
        if (className.startsWith("java/applet/")
                || className.startsWith("java/awt/")
                || className.startsWith("java/math/")
                || className.startsWith("java/nio/")
                || className.startsWith("java/text/")
                || className.startsWith("java/sql/")
                || className.startsWith("java/rmi/")
                || className.startsWith("sun/applet/")
                || className.startsWith("sun/awt/")
                || className.startsWith("sun/math/")
                || className.startsWith("sun/nio/")
                || className.startsWith("sun/text/")
                || className.startsWith("sun/sql/")
                || className.startsWith("sun/rmi/")
                || className.startsWith("java/net/Socket")
                || className.startsWith("java/net/SocketInputStream")
                || className.startsWith("java/net/SocketOutputStream")
                )
        return true;
        if (className.startsWith("java/lang/")
                || className.startsWith("java/net/")
                || className.startsWith("java/beans/")
                || className.startsWith("java/util/")
                || className.startsWith("java/security/")
                || className.startsWith("java/io/")
                || className.startsWith("sun/")
                )
            return false;
        if (className.startsWith("org/")) {
            return !(className.startsWith("resourceaccounting/", 4) || className.startsWith("objectweb/asm", 4));
        } else if (className.startsWith("sun/reflect/")
                || className.startsWith("sun/misc/")
                || className.startsWith("com/sun/jmx/")
                || className.startsWith("javax/management/")
                || className.startsWith("java/security/")
                || className.startsWith("java/lang/")
                || className.startsWith("java/util/")
                ) {
            return false;
        }
        return true;
    }
}