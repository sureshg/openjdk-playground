// Generated by jextract

package org.unix;

import java.lang.foreign.FunctionDescriptor;
import java.lang.invoke.MethodHandle;

final class constants$7 {

    // Suppresses default constructor, ensuring non-instantiability.
    private constants$7() {}
    static final FunctionDescriptor pipe$FUNC = FunctionDescriptor.of(Constants$root.C_INT$LAYOUT,
        Constants$root.C_POINTER$LAYOUT
    );
    static final MethodHandle pipe$MH = RuntimeHelper.downcallHandle(
        "pipe",
        constants$7.pipe$FUNC
    );
    static final FunctionDescriptor read$FUNC = FunctionDescriptor.of(Constants$root.C_LONG_LONG$LAYOUT,
        Constants$root.C_INT$LAYOUT,
        Constants$root.C_POINTER$LAYOUT,
        Constants$root.C_LONG_LONG$LAYOUT
    );
    static final MethodHandle read$MH = RuntimeHelper.downcallHandle(
        "read",
        constants$7.read$FUNC
    );
    static final FunctionDescriptor rmdir$FUNC = FunctionDescriptor.of(Constants$root.C_INT$LAYOUT,
        Constants$root.C_POINTER$LAYOUT
    );
    static final MethodHandle rmdir$MH = RuntimeHelper.downcallHandle(
        "rmdir",
        constants$7.rmdir$FUNC
    );
    static final FunctionDescriptor setgid$FUNC = FunctionDescriptor.of(Constants$root.C_INT$LAYOUT,
        Constants$root.C_INT$LAYOUT
    );
    static final MethodHandle setgid$MH = RuntimeHelper.downcallHandle(
        "setgid",
        constants$7.setgid$FUNC
    );
    static final FunctionDescriptor setpgid$FUNC = FunctionDescriptor.of(Constants$root.C_INT$LAYOUT,
        Constants$root.C_INT$LAYOUT,
        Constants$root.C_INT$LAYOUT
    );
    static final MethodHandle setpgid$MH = RuntimeHelper.downcallHandle(
        "setpgid",
        constants$7.setpgid$FUNC
    );
    static final FunctionDescriptor setsid$FUNC = FunctionDescriptor.of(Constants$root.C_INT$LAYOUT);
    static final MethodHandle setsid$MH = RuntimeHelper.downcallHandle(
        "setsid",
        constants$7.setsid$FUNC
    );
}


