// Generated by jextract

package org.unix;

import java.lang.foreign.FunctionDescriptor;
import java.lang.invoke.MethodHandle;

final class constants$14 {

    // Suppresses default constructor, ensuring non-instantiability.
    private constants$14() {}
    static final FunctionDescriptor setreuid$FUNC = FunctionDescriptor.of(Constants$root.C_INT$LAYOUT,
        Constants$root.C_INT$LAYOUT,
        Constants$root.C_INT$LAYOUT
    );
    static final MethodHandle setreuid$MH = RuntimeHelper.downcallHandle(
        "setreuid",
        constants$14.setreuid$FUNC
    );
    static final FunctionDescriptor swab$FUNC = FunctionDescriptor.ofVoid(
        Constants$root.C_POINTER$LAYOUT,
        Constants$root.C_POINTER$LAYOUT,
        Constants$root.C_LONG_LONG$LAYOUT
    );
    static final MethodHandle swab$MH = RuntimeHelper.downcallHandle(
        "swab",
        constants$14.swab$FUNC
    );
    static final FunctionDescriptor sync$FUNC = FunctionDescriptor.ofVoid();
    static final MethodHandle sync$MH = RuntimeHelper.downcallHandle(
        "sync",
        constants$14.sync$FUNC
    );
    static final FunctionDescriptor truncate$FUNC = FunctionDescriptor.of(Constants$root.C_INT$LAYOUT,
        Constants$root.C_POINTER$LAYOUT,
        Constants$root.C_LONG_LONG$LAYOUT
    );
    static final MethodHandle truncate$MH = RuntimeHelper.downcallHandle(
        "truncate",
        constants$14.truncate$FUNC
    );
    static final FunctionDescriptor ualarm$FUNC = FunctionDescriptor.of(Constants$root.C_INT$LAYOUT,
        Constants$root.C_INT$LAYOUT,
        Constants$root.C_INT$LAYOUT
    );
    static final MethodHandle ualarm$MH = RuntimeHelper.downcallHandle(
        "ualarm",
        constants$14.ualarm$FUNC
    );
    static final FunctionDescriptor usleep$FUNC = FunctionDescriptor.of(Constants$root.C_INT$LAYOUT,
        Constants$root.C_INT$LAYOUT
    );
    static final MethodHandle usleep$MH = RuntimeHelper.downcallHandle(
        "usleep",
        constants$14.usleep$FUNC
    );
}


