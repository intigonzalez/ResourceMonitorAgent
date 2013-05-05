package org.resourceaccounting.binderinjector;

import org.objectweb.asm.MethodVisitor;

public class NetworkAccessMethodInstrumentation extends AbstractMethodInstrumentation {
    public NetworkAccessMethodInstrumentation(MethodVisitor visitor, String className) {
        super(visitor, className);
    }

    @Override
    public void visitMethodInsn(int opcode, String involvedClass, String calledMethod, String s3) {

        if (calledMethod.equals("socketRead0")) {
            super.visitMethodInsn(opcode, involvedClass, calledMethod, s3);
            // generate call to the resource monitor
            this.dup();
            registerNetworkBytesReceived();
        }
        else if (calledMethod.equals("socketWrite0")) {
            this.dup();
            registerNetworkBytesSent();
            super.visitMethodInsn(opcode, involvedClass, calledMethod, s3);
        }
        else {
            super.visitMethodInsn(opcode, involvedClass, calledMethod, s3);
        }
    }

}