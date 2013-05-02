package org.resourceaccounting.binderinjector;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

/**
 * Created with IntelliJ IDEA.
 * User: inti
 * Date: 4/25/13
 * Time: 7:00 PM
 * To change this template use File | Settings | File Templates.
 */
public class FinalizeMethodInstrumentation extends AbstractMethodInstrumentation {

    private boolean needAccount = true;

    public FinalizeMethodInstrumentation(MethodVisitor methodVisitor, String className, String methodDescriptor, boolean hasFrames) {
        super(methodVisitor, className);
    }


    @Override
    public void visitMethodInsn(int opcode, String involvedClass, String calledMethod, String descriptor) {
        super.visitMethodInsn(opcode, involvedClass, calledMethod, descriptor);
        if (opcode == Opcodes.INVOKESPECIAL && calledMethod.equals("finalize") && calledMethod.equals("()V")) {
            needAccount = !ExtraInstrumentationRules.isInstrumentable(involvedClass);
        }
    }

    @Override
    public void visitInsn(int opcode) {
        boolean  b = (opcode == Opcodes.RETURN);
        if (b && needAccount) {
            registerMemoryDeallocation(this , -1);
        }
        // generate the normal instruction
        super.visitInsn(opcode);
    }

    public void visitEnd() {
        registerMemoryDeallocation(this , -1);
        super.visitEnd();
    }

}
