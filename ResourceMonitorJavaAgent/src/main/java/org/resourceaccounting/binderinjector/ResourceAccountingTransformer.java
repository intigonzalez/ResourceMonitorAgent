package org.resourceaccounting.binderinjector;

import org.objectweb.asm.*;
import org.objectweb.asm.commons.LocalVariablesSorter;

/**
* Created with IntelliJ IDEA.
* User: inti
* Date: 4/22/13
* Time: 2:47 PM
* To change this template use File | Settings | File Templates.
*/
public class ResourceAccountingTransformer extends ClassVisitor {


    boolean  hasFinalize = false;
    private String className;
    private boolean shouldAddField;
    private final ClassLoader classLoader;
    private String superClass;

    public ResourceAccountingTransformer(ClassVisitor classVisitor, ClassLoader classLoader) {
        super(Opcodes.ASM4, classVisitor);
        this.classLoader = classLoader;
    }

    @Override
    public MethodVisitor visitMethod(int flags, String methodName, String signature, String s3, String[] strings) {
        if (methodName.equals("finalize") && signature.equals("()V")) {
            hasFinalize = true;
            AbstractMethodInstrumentation ami = getFinalizeMethodInstrumentation(flags, methodName, signature, s3, strings);
            return ami;
            //return super.visitMethod(flags, methodName, signature, s3, strings);
        }
        AbstractMethodInstrumentation mi = getMethodInstrumenting(flags, methodName, signature, s3, strings);
        return mi;
    }

    private AbstractMethodInstrumentation getMethodInstrumenting(int flags, String methodName, String signature, String s3, String[] strings) {
        MethodVisitor mv = super.visitMethod(flags, methodName, signature, s3, strings);
        DefaultMethodInstrumentation mi = new DefaultMethodInstrumentation(mv, className);
        return mi;
    }

    private AbstractMethodInstrumentation getFinalizeMethodInstrumentation(int flags, String methodName, String signature, String s3, String[] strings) {
        MethodVisitor mv = super.visitMethod(flags, methodName, signature, s3, strings);
        FinalizeMethodInstrumentation mi = new FinalizeMethodInstrumentation(mv, className, signature, false);
        return mi;
    }

    @Override
    public void visit(int classVersion, int flags, String name, String signature, String superclass, String[] strings) {
//        System.out.printf("Transforming class %s\n", s);
        className = name;//.replace('/', '.');
        superClass = superclass == null? "java/lang/Object":superclass;
        super.visit(classVersion, flags, name, signature, superclass, strings);
        hasFinalize = ((flags & Opcodes.ACC_ENUM) != 0) ||
                ((flags & Opcodes.ACC_INTERFACE) != 0) ||
                ((flags & Opcodes.ACC_ABSTRACT) != 0) ||
                ((flags & Opcodes.ACC_ANNOTATION) != 0);
        shouldAddField = ((flags & Opcodes.ACC_INTERFACE) == 0) &&
                ((flags & Opcodes.ACC_ABSTRACT) == 0) &&
                ((flags & Opcodes.ACC_ANNOTATION) == 0) ;
    }

    @Override
    public void visitEnd() {
        // add a finalize method if the class does not have one
        if (!hasFinalize) {
            AbstractMethodInstrumentation mv = getMethodInstrumenting(Opcodes.ACC_PRIVATE, "finalize","()V", null, null);
            mv.visitCode();
            mv.load(0, Type.getType(className));
            mv.invokespecial(superClass,"finalize", "()V");
            if (!ExtraInstrumentationRules.isInstrumentable(superClass))
                DefaultMethodInstrumentation.registerMemoryDeallocation(mv, -1);

            mv.visitInsn(Opcodes.RETURN);
            mv.visitMaxs(2, 1);
            mv.visitEnd();
        }
        if (shouldAddField) {
            // add a field to store the owner resource principal, used in finalize
            FieldVisitor fv = cv.visitField(Opcodes.ACC_PUBLIC, AbstractMethodInstrumentation.MONITOR_FIELD_WITH_OWNER_PRINCIPAL,
                    AbstractMethodInstrumentation.MONITOR_FIELD_WITH_OWNER_PRINCIPAL_DESC, null, null);
            if (fv != null) {
                fv.visitEnd();
            }
        }
        super.visitEnd();
    }
}
