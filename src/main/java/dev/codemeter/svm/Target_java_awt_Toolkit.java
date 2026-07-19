package dev.codemeter.svm;

import com.oracle.svm.core.annotate.Substitute;
import com.oracle.svm.core.annotate.TargetClass;

@TargetClass(className = "java.awt.Toolkit")
public final class Target_java_awt_Toolkit {
    @Substitute
    public static void loadLibraries() {
        // Do nothing to avoid UnsatisfiedLinkError on Windows Native Image
    }
}
