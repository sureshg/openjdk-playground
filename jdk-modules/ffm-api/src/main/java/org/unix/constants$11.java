// Generated by jextract

package org.unix;

import java.lang.foreign.FunctionDescriptor;
import java.lang.invoke.MethodHandle;

final class constants$11 {

    // Suppresses default constructor, ensuring non-instantiability.
    private constants$11() {}
    static final FunctionDescriptor crypt$FUNC = FunctionDescriptor.of(Constants$root.C_POINTER$LAYOUT,
        Constants$root.C_POINTER$LAYOUT,
        Constants$root.C_POINTER$LAYOUT
    );
    static final MethodHandle crypt$MH = RuntimeHelper.downcallHandle(
        "crypt",
        constants$11.crypt$FUNC
    );
    static final FunctionDescriptor encrypt$FUNC = FunctionDescriptor.ofVoid(
        Constants$root.C_POINTER$LAYOUT,
        Constants$root.C_INT$LAYOUT
    );
    static final MethodHandle encrypt$MH = RuntimeHelper.downcallHandle(
        "encrypt",
        constants$11.encrypt$FUNC
    );
    static final FunctionDescriptor fchdir$FUNC = FunctionDescriptor.of(Constants$root.C_INT$LAYOUT,
        Constants$root.C_INT$LAYOUT
    );
    static final MethodHandle fchdir$MH = RuntimeHelper.downcallHandle(
        "fchdir",
        constants$11.fchdir$FUNC
    );
    static final FunctionDescriptor gethostid$FUNC = FunctionDescriptor.of(Constants$root.C_LONG_LONG$LAYOUT);
    static final MethodHandle gethostid$MH = RuntimeHelper.downcallHandle(
        "gethostid",
        constants$11.gethostid$FUNC
    );
    static final FunctionDescriptor getpgid$FUNC = FunctionDescriptor.of(Constants$root.C_INT$LAYOUT,
        Constants$root.C_INT$LAYOUT
    );
    static final MethodHandle getpgid$MH = RuntimeHelper.downcallHandle(
        "getpgid",
        constants$11.getpgid$FUNC
    );
    static final FunctionDescriptor getsid$FUNC = FunctionDescriptor.of(Constants$root.C_INT$LAYOUT,
        Constants$root.C_INT$LAYOUT
    );
    static final MethodHandle getsid$MH = RuntimeHelper.downcallHandle(
        "getsid",
        constants$11.getsid$FUNC
    );
}


