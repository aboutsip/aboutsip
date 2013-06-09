/**
 * 
 */
package io.pkts.frame;

import io.pkts.buffer.Buffer;
import io.pkts.framer.Framer;
import io.pkts.framer.FramerManager;
import io.pkts.packet.IPPacket;
import io.pkts.packet.IPPacketImpl;
import io.pkts.packet.MACPacket;
import io.pkts.packet.PacketParseException;
import io.pkts.protocol.Protocol;

import java.io.IOException;
import java.io.ObjectOutput;
import java.io.OutputStream;


/**
 * An IP Frame
 * 
 * @author jonas@jonasborjesson.com
 * 
 */
public final class IPv4Frame extends AbstractFrame implements IPFrame {

    /**
     * The parent frame
     */
    private final Layer2Frame parentFrame;

    /**
     * Internet Header Length (IHL), which is the number of 32-bit words in the
     * header.
     */
    private final int length;

    /**
     * All the headers of this ip packet
     */
    private final Buffer headers;

    /**
     * If the {@code #length} is greater than 5, then this header carries some
     * extra options
     */
    private final int options;

    /**
     * The protocol contained in this ip packet. I.e., the
     */
    private final Protocol protocol;

    /**
     * 
     * @param length
     *            the header length
     * @param headers
     *            all the ipv4 headers in a buffer.
     * @param options
     *            if header length > 5, then we have a set of options as well
     * @param data
     *            the payload of the ip4v frame
     */
    public IPv4Frame(final FramerManager framerManager, final PcapGlobalHeader header, final Layer2Frame parent,
            final int length,
            final Buffer headers, final int options,
            final Buffer payload) throws IOException {
        super(framerManager, header, Protocol.IPv4, payload);
        assert parent != null;

        this.parentFrame = parent;
        this.length = length;
        this.headers = headers;
        this.options = options;

        // the protocol is in byte 10
        final byte code = headers.getByte(9);
        this.protocol = Protocol.valueOf(code);
    }

    /**
     * Check out http://en.wikipedia.org/wiki/IPv4 for a good explanation of the
     * IPv4 header frame
     * 
     * @param payload
     *            the total payload of the previous frame, which contains the
     *            ipv4 headers and its payload
     * @return
     */
    // public static IPv4Frame frame(final Buffer payload) {

    // }

    /**
     * The version of this ip frame, will always be 4
     * 
     * @return
     */
    @Override
    public int getVersion() {
        return 4;
    }

    /**
     * The length of the ipv4 headers
     * 
     * @return
     */
    @Override
    public int getHeaderLength() {
        return this.length;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void writeExternal(final ObjectOutput out) throws IOException {
        // frames are easy to write out, just ask the top level
        // frame to persist all its payload since that will
        // capture everything...
        this.parentFrame.writeExternal(out);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IPPacket parse() throws PacketParseException {
        final MACPacket packet = this.parentFrame.parse();
        return new IPPacketImpl(packet, this.headers, this.options);
    }

    @Override
    protected Frame framePayload(final FramerManager framerManager, final Buffer payload) throws IOException {
        final Framer framer = framerManager.getFramer(this.protocol);
        if (framer != null) {
            return framer.frame(this, payload);
        }

        throw new RuntimeException("Unknown protocol " + this.protocol);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void write(final OutputStream out) throws IOException {
        this.parentFrame.write(out);
    }

    @Override
    public long getArrivalTime() {
        return this.parentFrame.getArrivalTime();
    }

    @Override
    public int getTotalLength() {
        return this.headers.getUnsignedShort(2);
    }

    /**
     * 
     * {@inheritDoc}
     */
    @Override
    public boolean isFragmented() {
        return isMoreFragmentsSet() || getFragmentOffset() > 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isReservedFlagSet() {
        try {
            final byte b = this.headers.getByte(6);
            return (b & 0x80) == 0x80;
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isDontFragmentSet() {
        try {
            final byte b = this.headers.getByte(6);
            return (b & 0x40) == 0x40;
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isMoreFragmentsSet() {
        try {
            final byte b = this.headers.getByte(6);
            return (b & 0x20) == 0x20;
        } catch (final IOException e) {
            throw new RuntimeException();
        }
    }

    @Override
    public short getFragmentOffset() {
        try {
            final byte a = this.headers.getByte(6);
            final byte b = this.headers.getByte(7);
            return (short) ((a & 0x1F) << 8 | b & 0xFF);
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int getIdentification() {
        return this.headers.getUnsignedShort(4);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("IPv4 ");
        sb.append(" Total Length: ").append(getTotalLength());
        sb.append(" ID: ").append(getIdentification());
        sb.append(" DF: ").append(isDontFragmentSet() ? "Set" : "Not Set");
        sb.append(" MF: ").append(isMoreFragmentsSet() ? "Set" : "Not Set");
        sb.append(" Fragment Offset: ").append(getFragmentOffset());

        return sb.toString();
    }

}
