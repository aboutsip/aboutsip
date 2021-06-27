package io.pkts.packet;

import io.pkts.buffer.Buffer;
import io.pkts.packet.impl.SctpChunkImpl;

public interface SctpChunk {

    static SctpChunk frame(final Buffer buffer) {
        return SctpChunkImpl.frame(buffer);
    }

    Type getType();

    /**
     * RFC 4960 section 3.2 Chunk Field Descriptions and some additional copy/pasted from Wikipedia
     */
    enum Type {
        DATA((short) 0, "Payload data"),
        INIT((short) 1, "Initiation"),
        INIT_ACK((short) 2, "Initiation Acknowledgement"),
        SACK((short) 3, "Selective Acknowledgement"),
        HEARTBEAT((short) 4, "Heartbeat Request"),
        HEARTBEAT_ACK((short) 5, "Heartbeat Acknowledgement"),
        ABORT((short) 6, "Abort"),
        SHUTDOWN((short) 7, "Shutdown"),
        SHUTDOWN_ACK((short) 8, "Shutdown Acknowledgement"),
        ERROR((short) 9, "Operation Error"),
        COOKIE_ECHO((short) 10, "State Cookie"),
        COOKIE_ACK((short) 11, "State Acknowledgement"),
        ECNE((short) 12, "Explicit Congestion Notification Echo (reserved)"),
        CWR((short) 13, "Congestion Window Reduced  (reserved)"),
        SHUTDOWN_COMPLETE((short) 14, "Shutdown Complete"),
        AUTH((short) 15, "Authentication"),
        I_DATA((short) 64, "Payload data supporting packet interleaving"),
        ASCONF_ACK((short) 128, "Address configuration change acknowledgement"),
        RE_CONFIG((short) 130, "Stream reconfiguration"),
        PAD((short) 132, "Packet Padding"),
        FORWARD_TSN((short) 192, "Increment expected TSN"),
        ASCONF((short) 193, "Address configuration change"),
        I_FORWARD_TSN((short) 194, "Increment expected TSN, supporting packet interleaving");

        private final short type;
        private final String description;

        Type(final short type, final String description) {
            this.type = type;
            this.description = description;
        }

        public short getType() {
            return type;
        }

        public String getDescription() {
            return description;
        }

    }

    static Type lookup(final short type) {
        switch (type) {
            case 0: return Type.DATA;
            case 1: return Type.INIT;
            case 2: return Type.INIT_ACK;
            case 3: return Type.SACK;
            case 4: return Type.HEARTBEAT;
            case 5: return Type.HEARTBEAT_ACK;
            case 6: return Type.ABORT;
            case 7: return Type.SHUTDOWN;
            case 8: return Type.SHUTDOWN_ACK;
            case 9: return Type.ERROR;
            case 10: return Type.COOKIE_ECHO;
            case 11: return Type.COOKIE_ACK;
            case 12: return Type.ECNE;
            case 13: return Type.CWR;
            case 14: return Type.SHUTDOWN_COMPLETE;
            case 15: return Type.AUTH;
            case 64: return Type.I_DATA;
            case 128: return Type.ASCONF_ACK;
            case 130: return Type.RE_CONFIG;
            case 132: return Type.PAD;
            case 192: return Type.FORWARD_TSN;
            case 193: return Type.ASCONF;
            case 194: return Type.I_FORWARD_TSN;
            default: return null;
        }
    }

}
