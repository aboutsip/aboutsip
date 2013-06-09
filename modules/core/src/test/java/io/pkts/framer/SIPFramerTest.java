package io.pkts.framer;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import io.pkts.YajTestBase;
import io.pkts.buffer.Buffer;
import io.pkts.frame.Layer4Frame;
import io.pkts.frame.SipFrame;
import io.pkts.framer.SIPFramer;
import io.pkts.packet.TransportPacket;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class SIPFramerTest extends YajTestBase {

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
    }

    @Override
    @After
    public void tearDown() throws Exception {
        super.tearDown();
    }

    /**
     * Make sure that we can frame a SIP request
     * 
     * @throws Exception
     */
    @Test
    public void testFrameSipRequest() throws Exception {
        final Layer4Frame layer4Frame = mock(Layer4Frame.class);
        final TransportPacket pkt = mock(TransportPacket.class);
        when(layer4Frame.parse()).thenReturn(pkt);

        final SIPFramer framer = new SIPFramer(this.framerManager);
        final SipFrame frame = framer.frame(layer4Frame, this.sipFrameBuffer);

        // this is the real request line from the above sip message
        final String requestLine = "INVITE sip:service@127.0.0.1:5090 SIP/2.0";
        assertThat(frame.getInitialLine().toString(), is(requestLine));

        // the first header happens to be the via header
        String h = "Via: SIP/2.0/UDP 127.0.1.1:5060;branch=z9hG4bK-16732-1-0";
        assertThat(frame.getHeaders().readLine().toString(), is(h));

        h = "From: sipp <sip:sipp@127.0.1.1:5060>;tag=16732SIPpTag001";
        assertThat(frame.getHeaders().readLine().toString(), is(h));

        h = "To: sut <sip:service@127.0.0.1:5090>";
        assertThat(frame.getHeaders().readLine().toString(), is(h));

        h = "Call-ID: 1-16732@127.0.1.1";
        assertThat(frame.getHeaders().readLine().toString(), is(h));

        h = "CSeq: 1 INVITE";
        assertThat(frame.getHeaders().readLine().toString(), is(h));

        h = "Contact: sip:sipp@127.0.1.1:5060";
        assertThat(frame.getHeaders().readLine().toString(), is(h));

        h = "Max-Forwards: 70";
        assertThat(frame.getHeaders().readLine().toString(), is(h));

        h = "Subject: Performance Test";
        assertThat(frame.getHeaders().readLine().toString(), is(h));

        h = "Content-Type: application/sdp";
        assertThat(frame.getHeaders().readLine().toString(), is(h));

        h = "Content-Length:   129";
        assertThat(frame.getHeaders().readLine().toString(), is(h));

        // currently, we do frame the header-body separator, perhaps
        // we dont want to do that? For now, it is what it is...
        assertThat(frame.getHeaders().readLine().toString(), is(""));

        // the end...
        assertThat(frame.getHeaders().readLine(), is((Buffer) null));

        // check the payload
        String sdp = "v=0";
        assertThat(frame.getPayload().readLine().toString(), is(sdp));

        sdp = "o=user1 53655765 2353687637 IN IP4 127.0.1.1";
        assertThat(frame.getPayload().readLine().toString(), is(sdp));

        sdp = "s=-";
        assertThat(frame.getPayload().readLine().toString(), is(sdp));

        sdp = "c=IN IP4 127.0.1.1";
        assertThat(frame.getPayload().readLine().toString(), is(sdp));

        sdp = "t=0 0";
        assertThat(frame.getPayload().readLine().toString(), is(sdp));

        sdp = "m=audio 6001 RTP/AVP 0";
        assertThat(frame.getPayload().readLine().toString(), is(sdp));

        sdp = "a=rtpmap:0 PCMU/8000";
        assertThat(frame.getPayload().readLine().toString(), is(sdp));

        // nothing left
        assertThat(frame.getPayload().readLine(), is((Buffer) null));
    }

}
