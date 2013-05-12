/**
 * 
 */
package com.aboutsip.yajpcap.packet.sip.impl;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.aboutsip.buffer.Buffers;
import com.aboutsip.yajpcap.YajTestBase;
import com.aboutsip.yajpcap.packet.sip.SipRequest;
import com.aboutsip.yajpcap.packet.sip.address.SipURI;
import com.aboutsip.yajpcap.packet.sip.header.impl.SipHeaderImpl;
import com.aboutsip.yajpcap.protocol.Protocol;

/**
 * @author jonas
 * 
 */
public class SipRequestTest extends YajTestBase {

    /*
     * (non-Javadoc)
     * 
     * @see com.aboutsip.yajpcap.YajTestBase#setUp()
     */
    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.aboutsip.yajpcap.YajTestBase#tearDown()
     */
    @Override
    @After
    public void tearDown() throws Exception {
        super.tearDown();
    }

    /**
     * Simple test for making sure that the payload makes it into the toString
     * stuff.
     * 
     * @throws Exception
     */
    @Test
    public void testToString() throws Exception {
        final SipRequest req = (SipRequest) loadStream("sipp.pcap").get(0).getFrame(Protocol.SIP).parse();
        assertThat(req.toString().contains("o=user1 53655765 2353687637 IN IP4 127.0.1.1"), is(true));
    }

    /**
     * Make sure that when we clone a request that the clone and the original
     * indeed are truly separated.
     * 
     * @throws Exception
     */
    @Test
    public void testClone() throws Exception {
        final SipRequest req = (SipRequest) loadStream("sipp.pcap").get(0).getFrame(Protocol.SIP).parse();
        final SipRequest clone = req.clone();
        assertThat(req.toString(), is(clone.toString()));
        assertThat(req.toBuffer(), is(clone.toBuffer()));
        req.addHeader(new SipHeaderImpl(Buffers.wrap("Hello"), Buffers.wrap("world")));

        assertThat(req.toString().contains("Hello: world"), is(true));
        assertThat(clone.toString().contains("Hello: world"), is(false));

        ((SipURI) req.getRequestUri()).setPort(7777);
        ((SipURI) clone.getRequestUri()).setPort(8888);

        assertThat(req.toString().contains("sip:service@127.0.0.1:7777"), is(true));
        assertThat(clone.toString().contains("sip:service@127.0.0.1:8888"), is(true));
    }
}
