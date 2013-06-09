/**
 * 
 */
package io.pkts.packet.sip.impl;

import io.pkts.buffer.Buffer;
import io.pkts.frame.Layer7Frame;
import io.pkts.packet.TransportPacket;
import io.pkts.packet.sip.SipParseException;
import io.pkts.packet.sip.SipResponse;
import io.pkts.packet.sip.header.CSeqHeader;
import io.pkts.packet.sip.header.SipHeader;
import io.pkts.packet.sip.header.impl.CSeqHeaderImpl;


/**
 * @author jonas@jonasborjesson.com
 */
public final class SipResponseImpl extends SipMessageImpl implements SipResponse {

    private CSeqHeader cseq;

    private final SipResponseLine initialLine;

    /**
     * @param initialLine
     * @param headers
     * @param payload
     */
    public SipResponseImpl(final TransportPacket pkt, final SipResponseLine initialLine, final Buffer headers,
            final Buffer payload, final Layer7Frame sipFrame) {
        super(pkt, initialLine, headers, payload, sipFrame);
        this.initialLine = initialLine;
    }

    /**
     * {@inheritDoc}
     * 
     * @throws SipParseException
     */
    @Override
    public Buffer getMethod() throws SipParseException {
        if (this.cseq == null) {
            final SipHeader header = getHeader(CSEQ_HEADER);
            this.cseq = CSeqHeaderImpl.parseValue(header.getValue());
        }
        return this.cseq.getMethod();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getStatus() {
        return this.initialLine.getStatusCode();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isProvisional() {
        return getStatus() / 100 == 1;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isSuccess() {
        return getStatus() / 100 == 2;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isRedirect() {
        return getStatus() / 100 == 3;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isClientError() {
        return getStatus() / 100 == 4;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isServerError() {
        return getStatus() / 100 == 5;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isGlobalError() {
        return getStatus() / 100 == 6;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean is100Trying() {
        return getStatus() == 100;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isRinging() {
        return getStatus() == 180 || getStatus() == 183;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isTimeout() {
        return getStatus() == 480;
    }

    @Override
    public SipResponse toResponse() throws ClassCastException {
        return this;
    }

    @Override
    public SipResponse clone() {
        throw new RuntimeException("Sorry, not implemented right now");
    }

}
