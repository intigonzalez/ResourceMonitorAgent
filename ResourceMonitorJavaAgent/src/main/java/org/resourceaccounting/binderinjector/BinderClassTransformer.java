package org.resourceaccounting.binderinjector;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.util.TraceClassVisitor;

import java.io.PrintWriter;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;

/**
 * Created with IntelliJ IDEA.
 * User: inti
 * Date: 4/23/13
 * Time: 3:19 PM
 * To change this template use File | Settings | File Templates.
 */
public class BinderClassTransformer implements ClassFileTransformer {
//    HashMap<ClassLoader, ClassLoaderResourcePrincipal> loaders = new HashMap<ClassLoader, ClassLoaderResourcePrincipal>();
    boolean started = true;
    boolean debug = false;
    public BinderClassTransformer(Instrumentation inst, boolean debug) {
        this.debug = debug;
    }

    @Override
    public byte[] transform(ClassLoader classLoader, String s, Class<?> aClass, ProtectionDomain protectionDomain, byte[] bytes) throws IllegalClassFormatException {
//        System.out.printf("Instrumenting class %s\n", s);
        if (!started
                || !ExtraInstrumentationRules.isInstrumentable(s)) {
            return bytes;
        }

        ClassReader reader = new ClassReader(bytes);
        ClassWriter writer = new ClassWriter(reader, 0);
        ClassVisitor tmp = writer;
        if (debug) {
            tmp = new TraceClassVisitor(writer, new PrintWriter(System.out));
            //tmp = new CheckClassAdapter(tmp,true);
        }
        ClassVisitor visitor = new ResourceAccountingTransformer(tmp, classLoader);
        reader.accept(visitor, ClassReader.EXPAND_FRAMES | ClassReader.SKIP_DEBUG);
        return writer.toByteArray();
    }

}
