// Generated by jextract

package org.unix;

import java.lang.foreign.FunctionDescriptor;
import java.lang.invoke.MethodHandle;

final class constants$1 {

    // Suppresses default constructor, ensuring non-instantiability.
    private constants$1() {}
    static final FunctionDescriptor fchownat$FUNC = FunctionDescriptor.of(Constants$root.C_INT$LAYOUT,
        Constants$root.C_INT$LAYOUT,
        Constants$root.C_POINTER$LAYOUT,
        Constants$root.C_INT$LAYOUT,
        Constants$root.C_INT$LAYOUT,
        Constants$root.C_INT$LAYOUT
    );
    static final MethodHandle fchownat$MH = RuntimeHelper.downcallHandle(
        "fchownat",
        constants$1.fchownat$FUNC
    );
    static final FunctionDescriptor linkat$FUNC = FunctionDescriptor.of(Constants$root.C_INT$LAYOUT,
        Constants$root.C_INT$LAYOUT,
        Constants$root.C_POINTER$LAYOUT,
        Constants$root.C_INT$LAYOUT,
        Constants$root.C_POINTER$LAYOUT,
        Constants$root.C_INT$LAYOUT
    );
    static final MethodHandle linkat$MH = RuntimeHelper.downcallHandle(
        "linkat",
        constants$1.linkat$FUNC
    );
    static final FunctionDescriptor readlinkat$FUNC = FunctionDescriptor.of(Constants$root.C_LONG_LONG$LAYOUT,
        Constants$root.C_INT$LAYOUT,
        Constants$root.C_POINTER$LAYOUT,
        Constants$root.C_POINTER$LAYOUT,
        Constants$root.C_LONG_LONG$LAYOUT
    );
    static final MethodHandle readlinkat$MH = RuntimeHelper.downcallHandle(
        "readlinkat",
        constants$1.readlinkat$FUNC
    );
    static final FunctionDescriptor symlinkat$FUNC = FunctionDescriptor.of(Constants$root.C_INT$LAYOUT,
        Constants$root.C_POINTER$LAYOUT,
        Constants$root.C_INT$LAYOUT,
        Constants$root.C_POINTER$LAYOUT
    );
    static final MethodHandle symlinkat$MH = RuntimeHelper.downcallHandle(
        "symlinkat",
        constants$1.symlinkat$FUNC
    );
    static final FunctionDescriptor unlinkat$FUNC = FunctionDescriptor.of(Constants$root.C_INT$LAYOUT,
        Constants$root.C_INT$LAYOUT,
        Constants$root.C_POINTER$LAYOUT,
        Constants$root.C_INT$LAYOUT
    );
    static final MethodHandle unlinkat$MH = RuntimeHelper.downcallHandle(
        "unlinkat",
        constants$1.unlinkat$FUNC
    );
    static final FunctionDescriptor _exit$FUNC = FunctionDescriptor.ofVoid(
        Constants$root.C_INT$LAYOUT
    );
    static final MethodHandle _exit$MH = RuntimeHelper.downcallHandle(
        "_exit",
        constants$1._exit$FUNC
    );
}

