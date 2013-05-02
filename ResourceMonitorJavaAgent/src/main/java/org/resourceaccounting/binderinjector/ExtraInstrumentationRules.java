package org.resourceaccounting.binderinjector;

public class ExtraInstrumentationRules {
    public static boolean isInstrumentable(String className) {
//        System.out.printf("Asking if instrumentable %s\n", className);
        if (className.startsWith("java/")
                || className.startsWith("sun/"))
            return false;
//        if (className.startsWith("java/")) {
//            return !(className.startsWith("lang/", 5) || className.startsWith("util/", 5));
//        }
//        else
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