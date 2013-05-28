package com.aboutsip.yajpcap;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;

import com.aboutsip.buffer.Buffer;
import com.aboutsip.buffer.Buffers;
import com.aboutsip.yajpcap.frame.Frame;
import com.aboutsip.yajpcap.frame.IPv4Frame;
import com.aboutsip.yajpcap.frame.PcapFrame;
import com.aboutsip.yajpcap.frame.PcapGlobalHeader;
import com.aboutsip.yajpcap.frame.SipFrame;
import com.aboutsip.yajpcap.framer.FramerManager;
import com.aboutsip.yajpcap.framer.PcapFramer;
import com.aboutsip.yajpcap.packet.IPPacket;
import com.aboutsip.yajpcap.packet.Packet;
import com.aboutsip.yajpcap.packet.PacketParseException;
import com.aboutsip.yajpcap.packet.sip.SipMessage;
import com.aboutsip.yajpcap.protocol.Protocol;

/**
 * Test base for all tests regarding framing and parsing
 * 
 * @author jonas@jonasborjesson.com
 * 
 */
public class YajTestBase {

    protected FramerManager framerManager;

    /**
     * Default stream pointing to a pcap that contains some sip traffic
     */
    protected Buffer pcapStream;

    protected ByteOrder defaultByteOrder;

    /**
     * Default frame that most tests can use to test their basic framing
     * abilities
     */
    protected Buffer defaultFrame;

    /**
     * The default header for the default pcap stream
     */
    protected PcapGlobalHeader defaultPcapHeader;

    /**
     * The default pcap frame
     */
    protected PcapFrame defaultPcapFrame;

    /**
     * A full ethernet frame wrapped in a buffer. We will slice out the other
     * frames out of this one so that individual test cases can use the the raw
     * data with ease. All of the indices have been taken from wireshark
     */
    protected Buffer ethernetFrameBuffer;

    /**
     * A raw ipv4 frame buffer containing a UDP packet
     */
    protected Buffer ipv4FrameBuffer;

    /**
     * A raw ipv4 frame containing a TCP packet
     */
    protected Buffer ipv4TCPFrameBuffer;

    /**
     * A raw udp frame buffer.
     */
    protected Buffer udpFrameBuffer;

    /**
     * A raw tcp frame buffer.
     */
    protected Buffer tcpFrameBuffer;

    /**
     * A raw sip frame buffer.
     */
    protected Buffer sipFrameBuffer;

    /**
     * A raw sip frame buffer containing a 180 response
     */
    protected Buffer sipFrameBuffer180Response;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
    }

    @Before
    public void setUp() throws Exception {
        this.framerManager = FramerManager.getInstance();
        final InputStream stream = YajTestBase.class.getResourceAsStream("sipp.pcap");

        this.pcapStream = Buffers.wrap(stream);

        // Get the first frame for tests to use. Since this actually will
        // use the PcapFramer to frame it, make sure the PcapFramer isn't
        // broken so do some assertions on it...
        this.defaultPcapHeader = PcapGlobalHeader.parse(this.pcapStream);
        this.defaultByteOrder = this.defaultPcapHeader.getByteOrder();
        final PcapFramer framer = new PcapFramer(this.defaultPcapHeader, this.framerManager);
        this.defaultPcapFrame = framer.frame(null, this.pcapStream);
        this.defaultFrame = this.defaultPcapFrame.getPayload();
        assertThat(547, is(this.defaultFrame.capacity()));

        this.ethernetFrameBuffer = Buffers.wrap(RawData.rawEthernetFrame);

        // slice out the individual payloads so that our tests can work
        // directly on this raw data.
        this.ipv4FrameBuffer = this.ethernetFrameBuffer.slice(14, this.ethernetFrameBuffer.capacity());
        this.udpFrameBuffer = this.ethernetFrameBuffer.slice(34, this.ethernetFrameBuffer.capacity());
        this.sipFrameBuffer = this.ethernetFrameBuffer.slice(42, this.ethernetFrameBuffer.capacity());

        final Buffer buf = Buffers.wrap(RawData.tcpFrame);
        this.ipv4TCPFrameBuffer = buf.slice(14, buf.capacity());
        this.tcpFrameBuffer = buf.slice(34, buf.capacity());

        final Buffer ethernetFrame = Buffers.wrap(RawData.rawEthernetFrame2);
        this.sipFrameBuffer180Response = ethernetFrame.slice(42, ethernetFrame.capacity());
    }

    @After
    public void tearDown() throws Exception {
    }

    public List<Frame> loadStream(final String streamName) throws Exception {
        final InputStream stream = YajTestBase.class.getResourceAsStream(streamName);
        final Pcap pcap = Pcap.openStream(stream);
        final List<Frame> frames = new ArrayList<Frame>();
        pcap.loop(new FrameHandler() {
            @Override
            public void nextFrame(final Frame frame) {
                frames.add(frame);
            }
        });
        pcap.close();
        return frames;
    }

    public List<IPPacket> loadIPPackets(final String streamName) throws Exception {
        final List<Frame> frames = loadStream(streamName);
        final List<IPPacket> packets = new ArrayList<IPPacket>();
        for (final Frame frame : frames) {
            final IPv4Frame ipv4Frame = (IPv4Frame) frame.getFrame(Protocol.IPv4);
            final IPPacket ip = ipv4Frame.parse();
            packets.add(ip);
        }
        return packets;
    }

    /**
     * Helper class that simply just counts the number of SIP requests.
     * 
     */
    public static class MethodCalculator implements FrameHandler {
        public int total;
        public int invite;
        public int bye;
        public int ack;
        public int cancel;

        @Override
        public void nextFrame(final Frame frame) {
            try {
                final SipFrame sipFrame = (SipFrame) frame.getFrame(Protocol.SIP);
                final SipMessage msg = sipFrame.parse();
                ++this.total;
                if (msg.isRequest()) {
                    if (msg.isInvite()) {
                        ++this.invite;
                    } else if (msg.isBye()) {
                        ++this.bye;
                    } else if (msg.isAck()) {
                        ++this.ack;
                    } else if (msg.isCancel()) {
                        ++this.cancel;
                    }
                }
            } catch (final IOException e) {
                fail("Got an IOException in my test " + e.getMessage());
            } catch (final PacketParseException e) {
                fail("Got a PacketParseException in my test " + e.getMessage());
            }

        }

    }

    /**
     * Helper class that will write either {@link Frame}s or {@link Packet} to
     * the output stream. It will ONLY write INVITE and BYE messages.
     */
    public static class TestWriteStreamHandler implements FrameHandler {

        private final PcapOutputStream out;
        private final int count = 0;
        private final boolean writePackets;

        /**
         * 
         * @param out
         *            the output stream to write to
         * @param writePackets
         *            flag indicating whether we should be writing the
         *            {@link Packet}s or the {@link Frame}s
         */
        public TestWriteStreamHandler(final PcapOutputStream out, final boolean writePackets) {
            this.out = out;
            this.writePackets = writePackets;
        }

        @Override
        public void nextFrame(final Frame frame) {
            try {
                // only write out INVITE and BYE requests
                final SipFrame sipFrame = (SipFrame) frame.getFrame(Protocol.SIP);
                final SipMessage msg = sipFrame.parse();
                final String method = msg.getMethod().toString();
                final boolean isInviteOrBye = "INVITE".equals(method) || "BYE".equals(method);
                if (msg.isRequest() && isInviteOrBye) {
                    if (this.writePackets) {
                        this.out.write(msg);
                    } else {
                        this.out.write(sipFrame);
                    }
                }
            } catch (final IOException e) {
                fail("Got an IOException in my test " + e.getMessage());
            } catch (final PacketParseException e) {
                fail("Got a PacketParseException in my test " + e.getMessage());
            }
        }
    }

}
