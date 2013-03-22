/**
 * 
 */
package com.aboutsip.yajpcap.filters;

import com.aboutsip.yajpcap.frame.Frame;

/**
 * @author jonas@jonasborjesson.com
 * 
 */
public interface Filter {

    /**
     * Check whether this filter accepts the frame or not.
     * 
     * @param frame
     * @return
     * @throws FilterException
     *             in case something goes wrong when accessing data from the
     *             {@link Frame}. Whatever exception the {@link Frame} will
     *             throw will be wrapped in a {@link FilterException}.
     */
    boolean accept(Frame frame) throws FilterException;

}
