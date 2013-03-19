/**
 * 
 */
package com.aboutsip.yajpcap.packet.sip.header;

import com.aboutsip.buffer.Buffer;
import com.aboutsip.buffer.Buffers;
import com.aboutsip.yajpcap.packet.sip.SipHeader;

/**
 * Source rfc 3261 section 8.1.1.7
 * 
 * <p>
 * The Via header field indicates the transport used for the transaction and
 * identifies the location where the response is to be sent. A Via header field
 * value is added only after the transport that will be used to reach the next
 * hop has been selected (which may involve the usage of the procedures in [4]).
 * </p>
 * 
 * <p>
 * When the UAC creates a request, it MUST insert a Via into that request. The
 * protocol name and protocol version in the header field MUST be SIP and 2.0,
 * respectively. The Via header field value MUST contain a branch parameter.
 * This parameter is used to identify the transaction created by that request.
 * This parameter is used by both the client and the server.
 * </p>
 * 
 * <p>
 * The branch parameter value MUST be unique across space and time for all
 * requests sent by the UA. The exceptions to this rule are CANCEL and ACK for
 * non-2xx responses. As discussed below, a CANCEL request will have the same
 * value of the branch parameter as the request it cancels. As discussed in
 * Section 17.1.1.3, an ACK for a non-2xx response will also have the same
 * branch ID as the INVITE whose response it acknowledges.
 * </p>
 * 
 * <p>
 * The uniqueness property of the branch ID parameter, to facilitate its use as
 * a transaction ID, was not part of RFC 2543.
 * </p>
 * 
 * <p>
 * The branch ID inserted by an element compliant with this specification MUST
 * always begin with the characters "z9hG4bK". These 7 characters are used as a
 * magic cookie (7 is deemed sufficient to ensure that an older RFC 2543
 * implementation would not pick such a value), so that servers receiving the
 * request can determine that the branch ID was constructed in the fashion
 * described by this specification (that is, globally unique). Beyond this
 * requirement, the precise format of the branch token is
 * implementation-defined.
 * </p>
 * 
 * <p>
 * The Via header maddr, ttl, and sent-by components will be set when the
 * request is processed by the transport layer (Section 18).
 * </p>
 * 
 * <p>
 * Via processing for proxies is described in Section 16.6 Item 8 and Section
 * 16.7 Item 3.
 * </p>
 * 
 * @author jonas@jonasborjesson.com
 * 
 */
public interface ViaHeader extends Parameters, SipHeader {

    Buffer NAME = Buffers.wrap("Via");

    /**
     * The protocol, which typically is "UDP", "TCP" or "TLS" but can really be
     * anything according to RFC3261.
     * 
     * @return
     */
    Buffer getTransport();

    Buffer getHost();

    int getPort();

    Buffer getReceived();

    /**
     * For a request, the rport value will not be filled out since the
     * downstream element will do so when it discovers the rport parameter on a
     * {@link ViaHeader}. Hence, if you use {@link #getRPort()} you will not
     * correctly be able to determine whether this {@link ViaHeader} actually
     * has the rport parameter present or if it is simply not set yet. However,
     * this method will return true if the rport parameter exists on the
     * {@link ViaHeader}, irrespectively whether it has a value or not.
     * 
     * @return
     */
    boolean hasRPort();

    /**
     * Get the value of the rport parameter. -1 (negative one) will be returned
     * if the value is not set. Note, if you get -1 that doesn't mean that the
     * rport is not present on the {@link ViaHeader}. To make sure that the
     * {@link ViaHeader} indeed has the rport parameter set, use the
     * {@link #hasRPort()}.
     * 
     * @return
     */
    int getRPort();


    /**
     * The branch-parameter is mandatory and as such should always be there.
     * However, everything is done lazily in this library so there is not a 100%
     * guarantee that the branch header actually is present. Hence, you MUST be
     * prepared to check for null in case the Via-header is bad. If important to
     * your application (and if you are building a stack it probably will be)
     * then please call {@link #verify()} on your headers since that will
     * guarantee that they conform to whatever the various RFC's mandates.
     * 
     * @return
     */
    Buffer getBranch();

    int getTTL();

    /**
     * Convenience method for checking whether the protocol is UDP or not.
     * 
     * @return
     */
    boolean isUDP();

    /**
     * Convenience method for checking whether the protocol is TCP or not.
     * 
     * @return
     */
    boolean isTCP();

    /**
     * Convenience method for checking whether the protocol is TLS or not.
     * 
     * @return
     */
    boolean isTLS();

    /**
     * Convenience method for checking whether the protocol is SCTP or not.
     * 
     * @return
     */
    boolean isSCTP();

}
