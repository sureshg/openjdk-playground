// Generated by jextract

package org.unix;

import java.lang.foreign.FunctionDescriptor;
import java.lang.invoke.MethodHandle;

import static java.lang.foreign.ValueLayout.JAVA_LONG;
final class constants$13 {

    // Suppresses default constructor, ensuring non-instantiability.
    private constants$13() {}
    static final FunctionDescriptor const$0 = FunctionDescriptor.ofVoid(
        RuntimeHelper.POINTER,
        RuntimeHelper.POINTER,
        JAVA_LONG
    );
    static final MethodHandle const$1 = RuntimeHelper.downcallHandle(
        "memset_pattern4",
        constants$13.const$0
    );
    static final MethodHandle const$2 = RuntimeHelper.downcallHandle(
        "memset_pattern8",
        constants$13.const$0
    );
    static final MethodHandle const$3 = RuntimeHelper.downcallHandle(
        "memset_pattern16",
        constants$13.const$0
    );
    static final MethodHandle const$4 = RuntimeHelper.downcallHandle(
        "strcasestr",
        constants$6.const$0
    );
    static final MethodHandle const$5 = RuntimeHelper.downcallHandle(
        "strnstr",
        constants$5.const$2
    );
}


