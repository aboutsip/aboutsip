/**
 * 
 */
package io.pkts.framer;

import io.pkts.buffer.Buffer;
import io.pkts.frame.EthernetFrame;
import io.pkts.frame.Layer1Frame;
import io.pkts.frame.UnknownEtherType;
import io.pkts.frame.EthernetFrame.EtherType;
import io.pkts.protocol.Protocol;

import java.io.IOException;


/**
 * Simple framer for framing Ethernet frames
 * 
 * @author jonas@jonasborjesson.com
 */
public class EthernetFramer implements Layer2Framer {

    private final FramerManager framerManager;

    public EthernetFramer(final FramerManager framerManager) {
        this.framerManager = framerManager;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Protocol getProtocol() {
        return Protocol.ETHERNET_II;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public EthernetFrame frame(final Layer1Frame parent, final Buffer buffer) throws IOException {
        if (parent == null) {
            throw new IllegalArgumentException("The parent frame cannot be null");
        }

        // final Buffer destMacAddress = buffer.readBytes(6);
        // final Buffer srcMacAddress = buffer.readBytes(6);
        // final byte b1 = buffer.readByte();
        // final byte b2 = buffer.readByte();

        final Buffer headers = buffer.readBytes(14);
        final byte b1 = headers.getByte(12);
        final byte b2 = headers.getByte(13);

        EtherType etherType = null;
        try {
            etherType = getEtherType(b1, b2);
        } catch (final UnknownEtherType e) {
            throw new RuntimeException("uknown ether type");
        }

        final Buffer data = buffer.slice(buffer.capacity());

        // return new EthernetFrame(this.framerManager, parent, destMacAddress, srcMacAddress, etherType, data);
        return new EthernetFrame(this.framerManager, parent.getPcapGlobalHeader(), parent, headers, data);
    }

    public static EthernetFrame.EtherType getEtherType(final byte b1, final byte b2) throws UnknownEtherType {
        final EthernetFrame.EtherType type = getEtherTypeSafe(b1, b2);
        if (type != null) {
            return type;
        }

        // will implement as we need to
        throw new UnknownEtherType(b1, b2);
    }

    public static EthernetFrame.EtherType getEtherTypeSafe(final byte b1, final byte b2) {
        if (b1 == (byte) 0x08 && b2 == (byte) 0x00) {
            return EthernetFrame.EtherType.IPv4;
        } else if (b1 == (byte) 0x86 && b2 == (byte) 0xdd) {
            System.err.println("queue");
            return EthernetFrame.EtherType.IPv6;
        }

        return null;
    }

    @Override
    public boolean accept(final Buffer data) {
        return false;
    }

}
