/**
 * 
 */
package com.aboutsip.yajpcap.packet;

import java.io.IOException;
import java.io.OutputStream;

import com.aboutsip.buffer.Buffer;

/**
 * @author jonas@jonasborjesson.com
 */
public final class IPPacketImpl implements IPPacket {

    private final MACPacket parent;

    private final Buffer headers;

    private final int options;

    // private final String sourceIp;
    // private final String destinationIp;

    /**
     * 
     */
    public IPPacketImpl(final MACPacket parent, final Buffer headers, final int options) {
        assert parent != null;
        assert headers != null;
        this.parent = parent;
        this.headers = headers;
        this.options = options;
        // assert (sourceIp != null) && !sourceIp.isEmpty();
        // assert (destinationIp != null) && !destinationIp.isEmpty();

        // this.sourceIp = sourceIp;
        // this.destinationIp = this.destinationIp;
    }

    /**
     * Get the raw source ip.
     * 
     * Note, these are the raw bits and should be treated as such. If you really
     * want to print it, then you should treat it as unsigned
     * 
     * @return
     */
    public int getRawSourceIp() {
        return this.headers.getInt(12);
    }

    /**
     * 
     * {@inheritDoc}
     */
    @Override
    public String getSourceIP() {
        final short a = this.headers.getUnsignedByte(12);
        final short b = this.headers.getUnsignedByte(13);
        final short c = this.headers.getUnsignedByte(14);
        final short d = this.headers.getUnsignedByte(15);
        return a + "." + b + "." + c + "." + d;
    }

    /**
     * Get the raw destination ip.
     * 
     * Note, these are the raw bits and should be treated as such. If you really
     * want to print it, then you should treat it as unsigned
     * 
     * @return
     */
    public int getRawDestinationIp() {
        return this.headers.getInt(16);
    }

    /**
     * 
     * {@inheritDoc}
     */
    @Override
    public String getDestinationIP() {
        final short a = this.headers.getUnsignedByte(16);
        final short b = this.headers.getUnsignedByte(17);
        final short c = this.headers.getUnsignedByte(18);
        final short d = this.headers.getUnsignedByte(19);
        return a + "." + b + "." + c + "." + d;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void verify() {
        // nothing to do for ip packets
    }

    @Override
    public long getArrivalTime() {
        return this.parent.getArrivalTime();
    }

    @Override
    public String getSourceMacAddress() {
        return this.parent.getSourceMacAddress();
    }

    @Override
    public String getDestinationMacAddress() {
        return this.parent.getDestinationMacAddress();
    }

    @Override
    public void write(final OutputStream out) throws IOException {
        throw new RuntimeException("Sorry, not implemented just yet.");
    }

    @Override
    public int getTotalLength() {
        // byte 3 - 4
        return this.headers.getUnsignedShort(3);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setSourceMacAddress(final String macAddress) {
        this.parent.setSourceMacAddress(macAddress);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setDestinationMacAddress(final String macAddress) {
        this.parent.setDestinationMacAddress(macAddress);
    }


    @Override
    public void setSourceIP(final int a, final int b, final int c, final int d) {
        this.headers.setByte(12, (byte) a);
        this.headers.setByte(13, (byte) b);
        this.headers.setByte(14, (byte) c);
        this.headers.setByte(15, (byte) d);
    }

    @Override
    public void setDestinationIP(final int a, final int b, final int c, final int d) {
        this.headers.setByte(16, (byte) a);
        this.headers.setByte(17, (byte) b);
        this.headers.setByte(18, (byte) c);
        this.headers.setByte(19, (byte) d);
    }

    @Override
    public void setSourceIP(final String sourceIp) {
        setIP(12, sourceIp);
    }

    @Override
    public void setDestinationIP(final String destinationIP) {
        setIP(16, destinationIP);
    }

    /**
     * Very naive initial implementation. Should be changed to do a better job
     * and its performance probably can go up a lot as well.
     * 
     * @param startIndex
     * @param address
     */
    private void setIP(final int startIndex, final String address) {
        final String[] parts = address.split("\\.");
        this.headers.setByte(startIndex + 0, (byte) Integer.parseInt(parts[0]));
        this.headers.setByte(startIndex + 1, (byte) Integer.parseInt(parts[1]));
        this.headers.setByte(startIndex + 2, (byte) Integer.parseInt(parts[2]));
        this.headers.setByte(startIndex + 3, (byte) Integer.parseInt(parts[3]));
    }

}
