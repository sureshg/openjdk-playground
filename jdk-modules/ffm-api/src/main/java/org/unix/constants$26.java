// Generated by jextract

package org.unix;

import java.lang.foreign.FunctionDescriptor;
import java.lang.invoke.MethodHandle;

final class constants$26 {

    // Suppresses default constructor, ensuring non-instantiability.
    private constants$26() {}
    static final FunctionDescriptor setusershell$FUNC = FunctionDescriptor.ofVoid();
    static final MethodHandle setusershell$MH = RuntimeHelper.downcallHandle(
        "setusershell",
        constants$26.setusershell$FUNC
    );
    static final FunctionDescriptor setwgroups_np$FUNC = FunctionDescriptor.of(Constants$root.C_INT$LAYOUT,
        Constants$root.C_INT$LAYOUT,
        Constants$root.C_POINTER$LAYOUT
    );
    static final MethodHandle setwgroups_np$MH = RuntimeHelper.downcallHandle(
        "setwgroups_np",
        constants$26.setwgroups_np$FUNC
    );
    static final FunctionDescriptor strtofflags$FUNC = FunctionDescriptor.of(Constants$root.C_INT$LAYOUT,
        Constants$root.C_POINTER$LAYOUT,
        Constants$root.C_POINTER$LAYOUT,
        Constants$root.C_POINTER$LAYOUT
    );
    static final MethodHandle strtofflags$MH = RuntimeHelper.downcallHandle(
        "strtofflags",
        constants$26.strtofflags$FUNC
    );
    static final FunctionDescriptor swapon$FUNC = FunctionDescriptor.of(Constants$root.C_INT$LAYOUT,
        Constants$root.C_POINTER$LAYOUT
    );
    static final MethodHandle swapon$MH = RuntimeHelper.downcallHandle(
        "swapon",
        constants$26.swapon$FUNC
    );
    static final FunctionDescriptor ttyslot$FUNC = FunctionDescriptor.of(Constants$root.C_INT$LAYOUT);
    static final MethodHandle ttyslot$MH = RuntimeHelper.downcallHandle(
        "ttyslot",
        constants$26.ttyslot$FUNC
    );
    static final FunctionDescriptor undelete$FUNC = FunctionDescriptor.of(Constants$root.C_INT$LAYOUT,
        Constants$root.C_POINTER$LAYOUT
    );
    static final MethodHandle undelete$MH = RuntimeHelper.downcallHandle(
        "undelete",
        constants$26.undelete$FUNC
    );
}

