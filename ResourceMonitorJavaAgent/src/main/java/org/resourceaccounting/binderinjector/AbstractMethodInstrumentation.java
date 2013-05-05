package org.resourceaccounting.binderinjector;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.InstructionAdapter;

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
    private static final String MONITOR_NETWORK_IN_EVENT_NAME = "increaseBytesReceived";
    private static final String MONITOR_NETWORK_EVENT_SIG = "(ILorg/resourceaccounting/ResourcePrincipal;)V";
    private static final String MONITOR_NETWORK_OUT_EVENT_NAME = "increaseBytesSent";

    /**
     * Name of the class
     */
    protected String className;

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

    protected void loadResourcePrincipal() {
        //this.load(principalIndex, Type.getType("org/resourceaccounting/ResourcePrincipal"));
        strategy.generateByteCodeToGetId();
    }

    /**
     * Generate the code to send a notification about Memory allocation
     * @param count
     */
    public static void registerMemoryAllocation(DefaultMethodInstrumentation adapter,int count) {
        if (count == 0) return;
        adapter.iconst(count);
        adapter.loadResourcePrincipal();
        adapter.invokestatic(MONITOR_CLASS_NAME, MONITOR_OBJECT_EVENT_NAME, MONITOR_OBJECT_EVENT_SIG);
    }

    public static void registerMemoryDeallocation(AbstractMethodInstrumentation mv, int count) {
        mv.iconst(count);
        mv.load(0, Type.getObjectType(mv.className.replace('.','/')));
        mv.getfield(mv.className.replace('.','/'), MONITOR_FIELD_WITH_OWNER_PRINCIPAL, MONITOR_FIELD_WITH_OWNER_PRINCIPAL_DESC);
        mv.invokestatic(MONITOR_CLASS_NAME, MONITOR_OBJECT_EVENT_NAME, MONITOR_OBJECT_EVENT_SIG);
    }

    protected void registerNetworkBytesReceived() {
        loadResourcePrincipal();
        this.invokestatic(MONITOR_CLASS_NAME, MONITOR_NETWORK_IN_EVENT_NAME, MONITOR_NETWORK_EVENT_SIG);
    }

    protected void registerNetworkBytesSent() {
        loadResourcePrincipal();
        this.invokestatic(MONITOR_CLASS_NAME, MONITOR_NETWORK_OUT_EVENT_NAME, MONITOR_NETWORK_EVENT_SIG);
    }

    /**
     * Generate the code to send a notification about CPU consumption
     * @param count
     */
    protected void registerCPUConsumption(int count) {
        if (count == 0) return; // TODO : Add COMPUTE_FRAMES to ClassWriter constructor
        this.iconst(count);
        loadResourcePrincipal();
        this.invokestatic(MONITOR_CLASS_NAME, MONITOR_INSTRUCTIONS_EVENT_NAME, MONITOR_INSTRUCTIONS_EVENT_SIG);
    }
}