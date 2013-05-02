package org.resourceaccounting.binderinjector;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.InstructionAdapter;
import org.objectweb.asm.commons.LocalVariablesSorter;

public abstract class AbstractMethodInstrumentation extends InstructionAdapter {
    final static String MONITOR_CLASS_NAME = "org/resourceaccounting/binder/ResourceCounter";
    final static String MONITOR_OBJECT_EVENT_NAME = "increaseObjects";
    final static String MONITOR_OBJECT_EVENT_SIG = "(ILorg/resourceaccounting/ResourcePrincipal;)V";
    final static String MONITOR_INSTRUCTIONS_EVENT_NAME = "increaseInstructions";
    final static String MONITOR_INSTRUCTIONS_EVENT_SIG = "(ILorg/resourceaccounting/ResourcePrincipal;)V";
    final static String MONITOR_INST_OBJ_EVENT_NAME = "increaseObjectsAndInstructions";
    final static String MONITOR_INST_OBJ_EVENT_SIG = "(IILorg/resourceaccounting/ResourcePrincipal;)V";

    final static String MONITOR_FIELD_WITH_OWNER_PRINCIPAL = "_secretFieldWithOwnerResourcePrincipal_";
    final static String MONITOR_FIELD_WITH_OWNER_PRINCIPAL_DESC = "Lorg/resourceaccounting/ResourcePrincipal;";

    /**
     * Name of the class
     */
    protected String className;

    /**
     * Adapter to add new variables
     */
    public LocalVariablesSorter lvs;

    /**
     * Index of the local variable to store the ResourcePrincipal which is calling the method
     */
    protected int principalIndex;
    private final IdRetrieveStrategy strategy;
    public ClassLoader loader;


    protected AbstractMethodInstrumentation(MethodVisitor visitor, String className) {
        super(Opcodes.ASM4, visitor);
        this.className = className.replace('/','.');
        this.strategy = new IdRetrieveStrategyBasedOnThreadGroup(this);
    }


//    @Override
//    public void visitFrame(int i, int i2, Object[] objects, int i3, Object[] objects2) {
//        super.visitFrame(Opcodes.F_NEW, i2, objects, i3, objects2);
//    }

    @Override
    public void visitCode() {
        super.visitCode();

//        Type[] types = Type.getArgumentTypes(methodDescriptor);
//        Object[] s = new Object[types.length];
//        for (int i = 0 ; i < types.length ; i++) {
//
//            if (types[i].equals(Type.INT_TYPE))
//                s[i] = Opcodes.INTEGER;
//            else if (types[i].equals(Type.DOUBLE_TYPE))
//                s[i] = Opcodes.DOUBLE;
//            else if (types[i].equals(Type.LONG_TYPE))
//                s[i] = Opcodes.LONG;
//            else if (types[i].equals(Type.FLOAT_TYPE))
//                s[i] = Opcodes.FLOAT;
//            else if (types[i].getInternalName().length() == 1)
//                s[i] = Opcodes.TOP;
//            else
//                s[i] = types[i].getInternalName();
//        }
//        //super.visitFrame(Opcodes.F_NEW, s.length, s, 0, new Object[]{});
//        visitLdcInsn(className);
//        invokestatic("java/lang/Class", "forName", "(Ljava/lang/String;)Ljava/lang/Class;");
//        invokevirtual("java/lang/Class", "getClassLoader" , "()Ljava/lang/ClassLoader;");
//        invokestatic("org/resourceaccounting/binder/ClassLoaderResourcePrincipal", "get", "(Ljava/lang/ClassLoader;)Lorg/resourceaccounting/ResourcePrincipal;");
//        principalIndex = lvs.newLocal(Type.getType("org/resourceaccounting/ResourcePrincipal"));
//        store(principalIndex, Type.getType("org/resourceaccounting/ResourcePrincipal"));
    }

    protected void loadResourcePrincipal() {
        //this.load(principalIndex, Type.getType("org/resourceaccounting/ResourcePrincipal"));
        strategy.generateByteCodeToGetId();
    }

    public static void registerMemoryDeallocation(AbstractMethodInstrumentation mv, int count) {
        mv.iconst(count);
        mv.load(0, Type.getType(mv.className.replace('.','/')));
        mv.getfield(mv.className.replace('.','/'), MONITOR_FIELD_WITH_OWNER_PRINCIPAL, MONITOR_FIELD_WITH_OWNER_PRINCIPAL_DESC);
        mv.invokestatic(MONITOR_CLASS_NAME, MONITOR_OBJECT_EVENT_NAME, MONITOR_OBJECT_EVENT_SIG);
    }
}