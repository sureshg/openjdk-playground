// Generated by jextract

package org.unix;

import java.lang.foreign.FunctionDescriptor;
import java.lang.invoke.MethodHandle;

import static java.lang.foreign.ValueLayout.JAVA_INT;
final class constants$49 {

    // Suppresses default constructor, ensuring non-instantiability.
    private constants$49() {}
    static final MethodHandle const$0 = RuntimeHelper.downcallHandle(
        "mktemp",
        constants$10.const$4
    );
    static final MethodHandle const$1 = RuntimeHelper.downcallHandle(
        "mkostemp",
        constants$24.const$2
    );
    static final MethodHandle const$2 = RuntimeHelper.downcallHandle(
        "mkostemps",
        constants$25.const$0
    );
    static final MethodHandle const$3 = RuntimeHelper.downcallHandle(
        "mkstemp_dprotected_np",
        constants$25.const$0
    );
    static final FunctionDescriptor const$4 = FunctionDescriptor.of(RuntimeHelper.POINTER,
        JAVA_INT,
        RuntimeHelper.POINTER
    );
    static final MethodHandle const$5 = RuntimeHelper.downcallHandle(
        "mkdtempat_np",
        constants$49.const$4
    );
}


