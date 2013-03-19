/**
 * 
 */
package com.aboutsip.yajpcap.packet.sip.header;

import com.aboutsip.buffer.Buffer;
import com.aboutsip.buffer.Buffers;
import com.aboutsip.yajpcap.packet.sip.SipHeader;

/**
 * 
 * Source: RFC 3261 section 20.30
 * 
 * <p>
 * The Record-Route header field is inserted by proxies in a request to force
 * future requests in the dialog to be routed through the proxy.
 * </p>
 * 
 * <p>
 * Examples of its use with the Route header field are described in Sections
 * 16.12.1.
 * </p>
 * <p>
 * Example:
 * 
 * <pre>
 *    Record-Route: &lt;sip:server10.biloxi.com;lr&gt;,
 *                  &lt;sip:bigbox3.site3.atlanta.com;lr&gt;
 * </pre>
 * </p>
 * 
 * @author jonas@jonasborjesson.com
 */
public interface RecordRouteHeader extends HeaderAddress, SipHeader, Parameters {

    Buffer NAME = Buffers.wrap("Record-Route");

}
