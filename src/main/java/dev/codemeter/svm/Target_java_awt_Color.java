package dev.codemeter.svm;

import com.oracle.svm.core.annotate.Substitute;
import com.oracle.svm.core.annotate.TargetClass;

@TargetClass(className = "java.awt.Color")
public final class Target_java_awt_Color {
    @Substitute
    private static void initIDs() {
        // Do nothing to avoid calling native method on Windows Native Image
    }
}
