/**
 * 
 */
package com.aboutsip.yajpcap.packet.sip.impl;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.aboutsip.buffer.Buffer;
import com.aboutsip.buffer.Buffers;
import com.aboutsip.yajpcap.packet.sip.SipParseException;
import com.aboutsip.yajpcap.packet.sip.address.SipURI;

/**
 * @author jonas@jonasborjesson.com
 */
public class SipInitialLineTest {

    /**
     * @throws java.lang.Exception
     */
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
    }

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
    }

    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception {
    }

    /**
     * Test so that we correctly can parse a request line
     */
    @Test
    public void testParseRequestLine() throws Exception {
        final String s = "INVITE sip:alice@example.com SIP/2.0";
        final SipInitialLine initialLine = parseRequestLine(s);
        assertThat(initialLine.isRequestLine(), is(true));
        assertThat(initialLine.isResponseLine(), is(false));

        Buffer copy = Buffers.createBuffer(100);
        initialLine.getBytes(copy);
        assertThat(copy.toString(), is(s));

        final SipRequestLine requestLine = (SipRequestLine) initialLine;
        assertThat(requestLine.getMethod().toString(), is("INVITE"));
        assertThat(requestLine.getRequestUri().toString(), is("sip:alice@example.com"));

        copy = Buffers.createBuffer(100);
        initialLine.getBytes(copy);
        assertThat(copy.toString(), is(s));
    }

    /**
     * Make sure that cloning works and that we do a deep cloning so they are
     * totally seperated.
     * 
     * @throws Exception
     */
    @Test
    public void testCloneRequestLine() throws Exception {
        final String s = "INVITE sip:hello@aboutsip.com;transport=udp SIP/2.0";
        final SipRequestLine line1 = parseRequestLine(s);
        final SipRequestLine line2 = line1.clone();
        assertThat(line1.toString(), is(line2.toString()));

        final SipURI uri1 = (SipURI) line1.getRequestUri();
        final SipURI uri2 = (SipURI) line2.getRequestUri();

        assertThat(uri1.getPort(), is(uri2.getPort()));
        assertThat(uri1.getHost(), is(uri2.getHost()));
        assertThat(uri1.getHost().toString(), is(uri2.getHost().toString()));

        uri1.setPort(1111);
        assertThat(uri1.getPort(), is(1111));
        assertThat(uri2.getPort(), not(1111));

        assertThat(line1.toString(), not(line2.toString()));

    }

    private SipRequestLine parseRequestLine(final String s) throws SipParseException {
        final Buffer buffer = Buffers.wrap(s);
        return (SipRequestLine) SipInitialLine.parse(buffer);
    }

    /**
     * Basic response line test
     * 
     * @throws Exception
     */
    @Test
    public void testParseResponseLine() throws Exception {
        final String s = "SIP/2.0 200 OK";
        final Buffer buffer = Buffers.wrap(s);
        final SipInitialLine initialLine = SipInitialLine.parse(buffer);
        assertThat(initialLine.isRequestLine(), is(false));
        assertThat(initialLine.isResponseLine(), is(true));

        final SipResponseLine requestLine = (SipResponseLine) initialLine;
        assertThat(requestLine.getStatusCode(), is(200));
        assertThat(requestLine.getReason().toString(), is("OK"));

        final Buffer copy = Buffers.createBuffer(100);
        initialLine.getBytes(copy);
        assertThat(copy.toString(), is(s));
    }

    /**
     * If we get back something like "INVITE sip:hello" then this should error
     * out
     * 
     * @throws Exception
     */
    @Test
    public void testBadInitialLineNotEnoughSections() throws Exception {
        final Buffer buffer = Buffers.wrap("INVITE sip:alice@example");
        try {
            SipInitialLine.parse(buffer);
            fail("Expected SipParseException");
        } catch (final SipParseException e) {
            // the error offset should be set to the very end of the buffer
            // since that is where we bailed out.
            // assertThat(e.getErroOffset(), is(24));

            // due to some re-shuffling this is not true anymore
            // Need to figure out what the best way of doing this
            // is now since as part of this re-shuffling I cleaned
            // up some other stuff that was worse...
            assertThat(e.getErroOffset(), is(7));
        }
    }

    /**
     * Wrong SIP version
     * 
     * @throws Exception
     */
    @Test
    public void testBadInitialLineWrongSipVersion() throws Exception {
        final Buffer buffer = Buffers.wrap("INVITE sip:alice@example.com SIP/3.0");
        try {
            SipInitialLine.parse(buffer);
            fail("Expected SipParseException");
        } catch (final SipParseException e) {
            // the error offset should be set to the very and of the buffer
            // since that is where we bailed out.
            assertThat(e.getErroOffset(), is(33));
        }
    }

    /**
     * Wrong Status code
     * 
     * @throws Exception
     */
    @Test
    public void testBadInitialLineBadStatusCode() throws Exception {
        final Buffer buffer = Buffers.wrap("SIP/2.0 twohundred OK");
        try {
            SipInitialLine.parse(buffer);
            fail("Expected SipParseException");
        } catch (final SipParseException e) {
            // the error offset should be set to the very and of the buffer
            // since that is where we bailed out.
            assertThat(e.getErroOffset(), is(8));
        }
    }

    @Test
    public void testBufferThrowsIOException() throws Exception {
        final Buffer buffer = mock(Buffer.class);
        when(buffer.readUntil(SipParser.SP)).thenThrow(new IOException("From unit tests"));
        when(buffer.getReaderIndex()).thenReturn(5);
        try {
            SipInitialLine.parse(buffer);
            fail("Expected SipParseException");
        } catch (final SipParseException e) {
            // five because that is what we mocked it to return
            assertThat(e.getErroOffset(), is(5));
        }

    }

}
