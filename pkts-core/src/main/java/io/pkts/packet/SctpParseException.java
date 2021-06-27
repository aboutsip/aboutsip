package io.pkts.packet;

public class SctpParseException extends PacketParseException {

    public SctpParseException(final int errorOffset, final String message) {
        super(errorOffset, message);
    }
}
