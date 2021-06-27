package io.pkts.framer;

import io.pkts.PktsTestBase;
import io.pkts.packet.SctpChunk;
import io.pkts.packet.SctpPacket;
import org.junit.Test;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class SctpFramerTest extends PktsTestBase {

    @Test
    public void testFrameSctp() throws Exception {
        final List<SctpPacket> packets = loadSctpPackets("sctp001.pcap");
        assertThat(packets.size(), is(12));
        assertChunk(packets.get(0), SctpChunk.Type.INIT);
        assertChunk(packets.get(1), SctpChunk.Type.INIT_ACK);
        assertChunk(packets.get(2), SctpChunk.Type.COOKIE_ECHO);
        assertChunk(packets.get(3), SctpChunk.Type.COOKIE_ACK);
        assertChunk(packets.get(4), SctpChunk.Type.DATA);
        assertChunk(packets.get(5), SctpChunk.Type.SACK);
        assertChunk(packets.get(6), SctpChunk.Type.DATA);
        assertChunk(packets.get(7), SctpChunk.Type.SACK);
        assertChunk(packets.get(8), SctpChunk.Type.SACK);
        assertChunk(packets.get(9), SctpChunk.Type.SHUTDOWN);
        assertChunk(packets.get(10), SctpChunk.Type.SHUTDOWN_ACK);
        assertChunk(packets.get(11), SctpChunk.Type.SHUTDOWN_COMPLETE);

        assertThat(packets.get(0).getSourcePort(), is(37188));
        assertThat(packets.get(0).getDestinationPort(), is(1234));

        assertThat(packets.get(1).getSourcePort(), is(1234));
        assertThat(packets.get(1).getDestinationPort(), is(37188));
    }

    private void assertChunk(final SctpPacket pkt, final SctpChunk.Type expectedType) {
        assertThat(pkt.getChunks().size(), is(1));
        assertThat(pkt.getChunks().get(0).getType(), is(expectedType));
    }
}
