/******************************************************************************
 * Copyright (C) 2010-2016 CERN. All rights not expressly granted are reserved.
 * 
 * This file is part of the CERN Control and Monitoring Platform 'C2MON'.
 * C2MON is free software: you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the license.
 * 
 * C2MON is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for
 * more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with C2MON. If not, see <http://www.gnu.org/licenses/>.
 *****************************************************************************/
package cern.c2mon.daq.opcua.connection.soap;

import java.lang.Thread.UncaughtExceptionHandler;
import java.rmi.RemoteException;

import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.opcfoundation.xmlda.*;

import static org.easymock.classextension.EasyMock.*;

public class SoapLongPollRunnableTest {
    
    private static final String SUBHANDLE = "subhandle";

    private static final long SLEEP_TIME = 500L;

    private SoapLongPollRunnable poll;

    private OPCXML_DataAccessStub access = createMock(OPCXML_DataAccessStub.class);
    
    private volatile Throwable error;
    
    @Before
    public void setUp() throws SecurityException, NoSuchMethodException {
      poll = EasyMock.createMockBuilder(SoapLongPollRunnable.class)
              .withConstructor(SoapLongPollRunnable.class.getConstructor(
                      Integer.TYPE, Integer.TYPE, String.class,
                      OPCXML_DataAccessStub.class))
              .withArgs(100, 1000, "asd", access)
              .addMockedMethod(SoapLongPollRunnable.class.getMethod(
                      "newItemValues", SubscribePolledRefreshReplyItemList[].class))
              .addMockedMethod(SoapLongPollRunnable.class.getMethod(
                      "onError", Throwable.class))
              .createMock();
      Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler() {

        @Override
        public void uncaughtException(Thread t, Throwable e) {
          error = e;
        }
      });
    }
    
    @After
    public void tearDown() throws Throwable {
        if (error != null)
            throw error;
    }
    
//    @Test
//    public void testStartAndStop() throws RemoteException, InterruptedException {
//        GetStatusResponse statusResponse = new GetStatusResponse();
//        ReplyBase replyBase = new ReplyBase();
//        replyBase.setReplyTime(new GregorianCalendar());
//        statusResponse.setGetStatusResult(replyBase);
//        expect(access.getStatus(isA(GetStatus.class))).andReturn(statusResponse);
//        SubscriptionPolledRefreshResponse response = new SubscriptionPolledRefreshResponse();
//        response.setSubscriptionPolledRefreshResult(replyBase);
//        expect(access.subscriptionPolledRefresh(
//                isA(SubscriptionPolledRefresh.class))).andReturn(
//                        response ).atLeastOnce();
//        poll.newItemValues(null);
//        expectLastCall().atLeastOnce();
//        
//        replay(access, poll);
//        new Thread(poll).start();
//        Thread.sleep(SLEEP_TIME);
//        poll.stop();
//        verify(access, poll);
//        reset(access, poll);
//        replay(access, poll);
//        Thread.sleep(SLEEP_TIME);
//        // no more calls
//        verify(access, poll);
//    }
//    
//    @Test
//    public void testOPCErrors() throws RemoteException {
//        GetStatusResponse statusResponse = new GetStatusResponse();
//        ReplyBase replyBase = new ReplyBase();
//        replyBase.setReplyTime(new GregorianCalendar());
//        statusResponse.setGetStatusResult(replyBase);
//        expect(access.getStatus(isA(GetStatus.class))).andReturn(statusResponse);
//        OPCError error = new OPCError();
//        error.setText("asd");
//        OPCError[] errors = { error };
//        SubscriptionPolledRefreshResponse response = new SubscriptionPolledRefreshResponse();
//        response.setSubscriptionPolledRefreshResult(replyBase);
//        response.setErrors(errors);
//        expect(access.subscriptionPolledRefresh(
//                isA(SubscriptionPolledRefresh.class))).andReturn(
//                        response);
//        poll.onError(isA(OPCCommunicationException.class));
//        replay(access, poll);
//        poll.run();
//        verify(access, poll);
//    }

    @Test
    public void testStartPollingWithException() throws RemoteException, InterruptedException {
        GetStatusResponse statusResponse = new GetStatusResponse();
        statusResponse.setGetStatusResult(new ReplyBase());
        RuntimeException runtimeException = new RuntimeException();
        expect(access.getStatus(isA(GetStatus.class))).andThrow(runtimeException);
        poll.onError(runtimeException);

        replay(access, poll);
        poll.run();
        verify(access, poll);
    }
    
}
