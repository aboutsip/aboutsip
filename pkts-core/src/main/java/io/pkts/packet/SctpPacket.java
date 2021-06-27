package io.pkts.packet;

import io.pkts.buffer.Buffer;
import io.pkts.packet.impl.SctpPacketImpl;

import java.util.List;

public interface SctpPacket extends TransportPacket {

    static SctpPacket frame(final IPPacket ipPacket, final Buffer buffer) {
        return SctpPacketImpl.frame(ipPacket, buffer);
    }

    List<SctpChunk> getChunks();
}
