// Generated by jextract

package org.unix;

import java.lang.foreign.*;
import java.lang.invoke.VarHandle;

import static java.lang.foreign.ValueLayout.PathElement;
/**
 * {@snippet :
 * struct _opaque_pthread_mutex_t {
 *     long __sig;
 *     char __opaque[56];
 * };
 * }
 */
public class _opaque_pthread_mutex_t {

    static final StructLayout $struct$LAYOUT = MemoryLayout.structLayout(
        Constants$root.C_LONG_LONG$LAYOUT.withName("__sig"),
        MemoryLayout.sequenceLayout(56, Constants$root.C_CHAR$LAYOUT).withName("__opaque")
    ).withName("_opaque_pthread_mutex_t");
    public static MemoryLayout $LAYOUT() {
        return _opaque_pthread_mutex_t.$struct$LAYOUT;
    }
    static final VarHandle __sig$VH = $struct$LAYOUT.varHandle(PathElement.groupElement("__sig"));
    public static VarHandle __sig$VH() {
        return _opaque_pthread_mutex_t.__sig$VH;
    }
    /**
     * Getter for field:
     * {@snippet :
     * long __sig;
     * }
     */
    public static long __sig$get(MemorySegment seg) {
        return (long)_opaque_pthread_mutex_t.__sig$VH.get(seg);
    }
    /**
     * Setter for field:
     * {@snippet :
     * long __sig;
     * }
     */
    public static void __sig$set(MemorySegment seg, long x) {
        _opaque_pthread_mutex_t.__sig$VH.set(seg, x);
    }
    public static long __sig$get(MemorySegment seg, long index) {
        return (long)_opaque_pthread_mutex_t.__sig$VH.get(seg.asSlice(index*sizeof()));
    }
    public static void __sig$set(MemorySegment seg, long index, long x) {
        _opaque_pthread_mutex_t.__sig$VH.set(seg.asSlice(index*sizeof()), x);
    }
    public static MemorySegment __opaque$slice(MemorySegment seg) {
        return seg.asSlice(8, 56);
    }
    public static long sizeof() { return $LAYOUT().byteSize(); }
    public static MemorySegment allocate(SegmentAllocator allocator) { return allocator.allocate($LAYOUT()); }
    public static MemorySegment allocateArray(long len, SegmentAllocator allocator) {
        return allocator.allocate(MemoryLayout.sequenceLayout(len, $LAYOUT()));
    }
    public static MemorySegment ofAddress(MemorySegment addr, SegmentScope scope) { return RuntimeHelper.asArray(addr, $LAYOUT(), 1, scope); }
}


