package org.resourceaccounting.binderinjector;

import org.objectweb.asm.*;

/**
* Created with IntelliJ IDEA.
* User: inti
* Date: 4/22/13
* Time: 2:48 PM
* To change this template use File | Settings | File Templates.
*/
public class DefaultMethodInstrumentation extends AbstractMethodInstrumentation {

    /**
     * A very rough way of finding basic block.
     */
    private int lastMarkOfBasicBlockBorder;

    /**
     * Current opcode index in bytecode array for this method
     */
    private int readInstructions;

    /**
     * Indicate the number of new objects in the basic block
     */
    private int nbNewObjectsInBasicBlock;

    private int additionalsDup = 0;

    /**
     *
     */
    private int countOfNew = 0;


    public DefaultMethodInstrumentation(MethodVisitor methodVisitor, String className) {
        super(methodVisitor, className);
        lastMarkOfBasicBlockBorder = 0;
        readInstructions = 0;
        nbNewObjectsInBasicBlock = 0;
    }

    @Override
    public void visitFieldInsn(int i, String s, String s2, String s3) {
        super.visitFieldInsn(i, s, s2, s3);
        readInstructions++;
    }

    @Override
    public void visitMethodInsn(int opcode, String involvedClass, String calledMethod, String s3) {
        super.visitMethodInsn(opcode, involvedClass, calledMethod, s3);
        if (opcode == Opcodes.INVOKESPECIAL && ExtraInstrumentationRules.isInstrumentable(involvedClass)
                && calledMethod.equals("<init>")) {
            if (countOfNew > 0) {
//                System.out.printf("Accessing %s\n", involvedClass);
                loadResourcePrincipal();
                this.putfield(involvedClass, MONITOR_FIELD_WITH_OWNER_PRINCIPAL, MONITOR_FIELD_WITH_OWNER_PRINCIPAL_DESC);
                countOfNew --;
            }
        }
        readInstructions++;
    }

    @Override
    public void visitTypeInsn(int opcode, String involvedClass) {
        // execute the new
        super.visitTypeInsn(opcode, involvedClass);
        if (opcode == Opcodes.NEW && ExtraInstrumentationRules.isInstrumentable(involvedClass)) {
            // register the creation of the new object
            this.dup();
            additionalsDup++;
            countOfNew++;
            nbNewObjectsInBasicBlock++;
        }
        else if (opcode == Opcodes.ANEWARRAY) {
            //nbNewObjectsInBasicBlock++;
        }
        readInstructions++;
    }

    @Override
    public void visitInsn(int opcode) {
        boolean  b = (opcode >= Opcodes.IRETURN && opcode <= Opcodes.RETURN);
        if (b) {
            // register the consumption of resources
            registerConsumption();
        }
        // generate he normal instruction
        super.visitInsn(opcode);
        readInstructions++;
    }

    /**
     * Visit all JMP instrucions
     * @param opcode
     * @param label
     */
    @Override
    public void visitJumpInsn(int opcode, Label label) {
        readInstructions++;
        // register the consumption of resources
        registerConsumption();
        // generate the jmp instruction
        super.visitJumpInsn(opcode, label);
    }

    @Override
    public void visitLabel(Label label) {
        // register the consumption of resources
        registerConsumption();
        super.visitLabel(label);
    }

    /**
     * Visit IINC instruction
     * @param opcode
     * @param amount
     */
    @Override
    public void visitIincInsn(int opcode, int amount) {
        readInstructions++;
        super.visitIincInsn(opcode, amount);
    }

    @Override
    public void visitIntInsn(int opcode, int operand) {
        super.visitIntInsn(opcode, operand);
        readInstructions++;
    }

    @Override
    public void visitVarInsn(int opcode, int i2) {
        super.visitVarInsn(opcode, i2);
        readInstructions++;
    }

    @Override
    public void visitInvokeDynamicInsn(String s, String s2, Handle handle, Object... objects) {
        super.visitInvokeDynamicInsn(s, s2, handle, objects);
        readInstructions++;
    }

    @Override
    public void visitLdcInsn(Object o) {
        super.visitLdcInsn(o);
        readInstructions++;
    }

    @Override
    public void visitTableSwitchInsn(int i, int i2, Label label, Label... labels) {
        readInstructions++;
        // register the consumption of resources
        registerConsumption();
        super.visitTableSwitchInsn(i, i2, label, labels);
    }

    @Override
    public void visitLookupSwitchInsn(Label label, int[] ints, Label[] labels) {
        readInstructions++;
        // register the consumption of resources
        registerConsumption();
        super.visitLookupSwitchInsn(label, ints, labels);
    }

    @Override
    public void visitMultiANewArrayInsn(String s, int i) {
        super.visitMultiANewArrayInsn(s, i);
        //nbNewObjectsInBasicBlock++;
        readInstructions++;
    }

    @Override
    public void visitMaxs(int maxStack, int maxLocals) {
        super.visitMaxs(maxStack + 3 + additionalsDup, maxLocals);
    }

    /**
     * Generate the code to send a notification about CPU consumption
     * @param count
     */
    private void registerCPUConsumption(int count) {
        if (count == 0) return;
        this.iconst(count);
        loadResourcePrincipal();
        this.invokestatic(MONITOR_CLASS_NAME, MONITOR_INSTRUCTIONS_EVENT_NAME, MONITOR_INSTRUCTIONS_EVENT_SIG);
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

    /**
     * Generate the code to notify about CPU and Memory Allocation
     */
    private void registerConsumption() {
        int countI = readInstructions - lastMarkOfBasicBlockBorder;
        if (countI != 0 && nbNewObjectsInBasicBlock != 0) {
            this.iconst(nbNewObjectsInBasicBlock);
            this.iconst(countI);
            loadResourcePrincipal();
            this.invokestatic(MONITOR_CLASS_NAME, MONITOR_INST_OBJ_EVENT_NAME, MONITOR_INST_OBJ_EVENT_SIG);
        }
        else {
            registerCPUConsumption(countI);
            registerMemoryAllocation(this, nbNewObjectsInBasicBlock);
        }
        lastMarkOfBasicBlockBorder = readInstructions;
        nbNewObjectsInBasicBlock = 0;
    }
}
