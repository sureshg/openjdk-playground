// Generated by jextract

package org.unix;

import java.lang.foreign.FunctionDescriptor;
import java.lang.foreign.MemoryLayout;
import java.lang.foreign.StructLayout;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.VarHandle;

import static java.lang.foreign.ValueLayout.JAVA_SHORT;
import static java.lang.foreign.ValueLayout.PathElement;
final class constants$18 {

    // Suppresses default constructor, ensuring non-instantiability.
    private constants$18() {}
    static final FunctionDescriptor const$0 = FunctionDescriptor.of(RuntimeHelper.POINTER);
    static final MethodHandle const$1 = RuntimeHelper.downcallHandle(
        "__error",
        constants$18.const$0
    );
    static final StructLayout const$2 = MemoryLayout.structLayout(
        JAVA_SHORT.withName("ws_row"),
        JAVA_SHORT.withName("ws_col"),
        JAVA_SHORT.withName("ws_xpixel"),
        JAVA_SHORT.withName("ws_ypixel")
    ).withName("winsize");
    static final VarHandle const$3 = constants$18.const$2.varHandle(PathElement.groupElement("ws_row"));
    static final VarHandle const$4 = constants$18.const$2.varHandle(PathElement.groupElement("ws_col"));
    static final VarHandle const$5 = constants$18.const$2.varHandle(PathElement.groupElement("ws_xpixel"));
}


