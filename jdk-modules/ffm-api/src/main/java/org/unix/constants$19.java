// Generated by jextract

package org.unix;

import java.lang.foreign.FunctionDescriptor;
import java.lang.invoke.MethodHandle;

final class constants$19 {

    // Suppresses default constructor, ensuring non-instantiability.
    private constants$19() {}
    static final FunctionDescriptor getpeereid$FUNC = FunctionDescriptor.of(Constants$root.C_INT$LAYOUT,
        Constants$root.C_INT$LAYOUT,
        Constants$root.C_POINTER$LAYOUT,
        Constants$root.C_POINTER$LAYOUT
    );
    static final MethodHandle getpeereid$MH = RuntimeHelper.downcallHandle(
        "getpeereid",
        constants$19.getpeereid$FUNC
    );
    static final FunctionDescriptor getsgroups_np$FUNC = FunctionDescriptor.of(Constants$root.C_INT$LAYOUT,
        Constants$root.C_POINTER$LAYOUT,
        Constants$root.C_POINTER$LAYOUT
    );
    static final MethodHandle getsgroups_np$MH = RuntimeHelper.downcallHandle(
        "getsgroups_np",
        constants$19.getsgroups_np$FUNC
    );
    static final FunctionDescriptor getusershell$FUNC = FunctionDescriptor.of(Constants$root.C_POINTER$LAYOUT);
    static final MethodHandle getusershell$MH = RuntimeHelper.downcallHandle(
        "getusershell",
        constants$19.getusershell$FUNC
    );
    static final FunctionDescriptor getwgroups_np$FUNC = FunctionDescriptor.of(Constants$root.C_INT$LAYOUT,
        Constants$root.C_POINTER$LAYOUT,
        Constants$root.C_POINTER$LAYOUT
    );
    static final MethodHandle getwgroups_np$MH = RuntimeHelper.downcallHandle(
        "getwgroups_np",
        constants$19.getwgroups_np$FUNC
    );
    static final FunctionDescriptor initgroups$FUNC = FunctionDescriptor.of(Constants$root.C_INT$LAYOUT,
        Constants$root.C_POINTER$LAYOUT,
        Constants$root.C_INT$LAYOUT
    );
    static final MethodHandle initgroups$MH = RuntimeHelper.downcallHandle(
        "initgroups",
        constants$19.initgroups$FUNC
    );
    static final FunctionDescriptor issetugid$FUNC = FunctionDescriptor.of(Constants$root.C_INT$LAYOUT);
    static final MethodHandle issetugid$MH = RuntimeHelper.downcallHandle(
        "issetugid",
        constants$19.issetugid$FUNC
    );
}


