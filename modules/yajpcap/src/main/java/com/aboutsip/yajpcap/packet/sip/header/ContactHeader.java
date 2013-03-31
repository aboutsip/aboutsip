package com.aboutsip.yajpcap.packet.sip.header;

import com.aboutsip.buffer.Buffer;
import com.aboutsip.buffer.Buffers;
import com.aboutsip.yajpcap.packet.sip.SipHeader;

/**
 * @author jonas@jonasborjesson.com
 */
public interface ContactHeader extends HeaderAddress, SipHeader, Parameters {

    Buffer NAME = Buffers.wrap("Contact");

}
