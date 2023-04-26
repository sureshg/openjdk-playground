// Generated by jextract

package org.unix;

import java.lang.foreign.*;
import java.lang.invoke.VarHandle;

import static java.lang.foreign.ValueLayout.PathElement;
/**
 * {@snippet :
 * struct accessx_descriptor {
 *     unsigned int ad_name_offset;
 *     int ad_flags;
 *     int ad_pad[2];
 * };
 * }
 */
public class accessx_descriptor {

    static final StructLayout $struct$LAYOUT = MemoryLayout.structLayout(
        Constants$root.C_INT$LAYOUT.withName("ad_name_offset"),
        Constants$root.C_INT$LAYOUT.withName("ad_flags"),
        MemoryLayout.sequenceLayout(2, Constants$root.C_INT$LAYOUT).withName("ad_pad")
    ).withName("accessx_descriptor");
    public static MemoryLayout $LAYOUT() {
        return accessx_descriptor.$struct$LAYOUT;
    }
    static final VarHandle ad_name_offset$VH = $struct$LAYOUT.varHandle(PathElement.groupElement("ad_name_offset"));
    public static VarHandle ad_name_offset$VH() {
        return accessx_descriptor.ad_name_offset$VH;
    }
    /**
     * Getter for field:
     * {@snippet :
     * unsigned int ad_name_offset;
     * }
     */
    public static int ad_name_offset$get(MemorySegment seg) {
        return (int)accessx_descriptor.ad_name_offset$VH.get(seg);
    }
    /**
     * Setter for field:
     * {@snippet :
     * unsigned int ad_name_offset;
     * }
     */
    public static void ad_name_offset$set(MemorySegment seg, int x) {
        accessx_descriptor.ad_name_offset$VH.set(seg, x);
    }
    public static int ad_name_offset$get(MemorySegment seg, long index) {
        return (int)accessx_descriptor.ad_name_offset$VH.get(seg.asSlice(index*sizeof()));
    }
    public static void ad_name_offset$set(MemorySegment seg, long index, int x) {
        accessx_descriptor.ad_name_offset$VH.set(seg.asSlice(index*sizeof()), x);
    }
    static final VarHandle ad_flags$VH = $struct$LAYOUT.varHandle(PathElement.groupElement("ad_flags"));
    public static VarHandle ad_flags$VH() {
        return accessx_descriptor.ad_flags$VH;
    }
    /**
     * Getter for field:
     * {@snippet :
     * int ad_flags;
     * }
     */
    public static int ad_flags$get(MemorySegment seg) {
        return (int)accessx_descriptor.ad_flags$VH.get(seg);
    }
    /**
     * Setter for field:
     * {@snippet :
     * int ad_flags;
     * }
     */
    public static void ad_flags$set(MemorySegment seg, int x) {
        accessx_descriptor.ad_flags$VH.set(seg, x);
    }
    public static int ad_flags$get(MemorySegment seg, long index) {
        return (int)accessx_descriptor.ad_flags$VH.get(seg.asSlice(index*sizeof()));
    }
    public static void ad_flags$set(MemorySegment seg, long index, int x) {
        accessx_descriptor.ad_flags$VH.set(seg.asSlice(index*sizeof()), x);
    }
    public static MemorySegment ad_pad$slice(MemorySegment seg) {
        return seg.asSlice(8, 8);
    }
    public static long sizeof() { return $LAYOUT().byteSize(); }
    public static MemorySegment allocate(SegmentAllocator allocator) { return allocator.allocate($LAYOUT()); }
    public static MemorySegment allocateArray(long len, SegmentAllocator allocator) {
        return allocator.allocate(MemoryLayout.sequenceLayout(len, $LAYOUT()));
    }
    public static MemorySegment ofAddress(MemorySegment addr, SegmentScope scope) { return RuntimeHelper.asArray(addr, $LAYOUT(), 1, scope); }
}

