package io.pkts.packet.impl;

import io.pkts.buffer.Buffer;
import io.pkts.packet.SctpChunk;
import io.pkts.packet.SctpParseException;

import java.io.IOException;

import static io.pkts.packet.sip.impl.PreConditions.assertArgument;
import static io.pkts.packet.sip.impl.PreConditions.assertNotEmpty;

public class SctpChunkImpl implements SctpChunk {

    private final SctpChunk.Type type;
    private final Buffer header;
    private final Buffer value;

    public static SctpChunk frame(final Buffer buffer) {
        try {
            assertNotEmpty(buffer, "The buffer cannot be null or empty");
            assertArgument(buffer.getReadableBytes() >= 4, "There must be at least 4 bytes to read");
            final Buffer header = buffer.readBytes(4);
            final SctpChunk.Type type = SctpChunk.lookup(header.getUnsignedByte(0));
            final int length = header.getUnsignedShort(2) - 4;
            final int padding = calculatePadding(length);

            assertArgument(buffer.getReadableBytes() >= length + padding, "Unable to read Chunk Value. Not enough bytes. Needed "
                    + (length + padding) + " bytes but only " + buffer.getReadableBytes() + " bytes available");
            final Buffer value = buffer.readBytes(length);
            buffer.readBytes(padding);
            return new SctpChunkImpl(type, header, value);
        } catch (final IOException e) {
            throw new SctpParseException(buffer.getReaderIndex(), "Unable to read from buffer. Message (if any) " + e.getMessage());
        }

    }

    private static int calculatePadding(final int length) {
        final int padding = length % 4;
        if (padding != 0) {
            return 4 - padding;
        }
        return 0;
    }


    private SctpChunkImpl(final Type type, final Buffer header, final Buffer value) {
        this.type = type;
        this.header = header;
        this.value = value;
    }


    @Override
    public Type getType() {
        return type;
    }

    public Buffer getValue() {
        return value;
    }

    public int getFlags() {
        return header.getUnsignedByte(1);
    }

    /**
     * The length of the value of the chunk.
     * <p>
     * Note that the length as encoded into the Chunk "header" includes the 4 bytes containing the size of
     * the fields making up the header. This length is JUST the length of the actual value. Also, it does not
     * include padding.
     */
    public int getValueLength() {
        return value.capacity();
    }

    public int getPadding() {
        throw new RuntimeException("Not yet implemented");
    }


}
