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
package cern.c2mon.daq.opcua.connection.dcom;

import java.io.IOException;
import java.net.URI;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cern.c2mon.daq.opcua.connection.common.AbstractOPCUAAddress;
import cern.c2mon.daq.opcua.connection.common.IGroupProvider;
import cern.c2mon.daq.opcua.connection.common.IItemDefinitionFactory;
import cern.c2mon.daq.opcua.connection.common.impl.OPCCommunicationException;
import cern.c2mon.daq.opcua.connection.common.impl.OPCCriticalException;
import cern.c2mon.daq.opcua.connection.common.impl.OPCEndpoint;
import cern.c2mon.daq.opcua.connection.common.impl.SubscriptionGroup;
import cern.c2mon.daq.opcua.jintegraInterface.DIOPCGroupEventAdapter;
import cern.c2mon.daq.opcua.jintegraInterface.DIOPCGroupEventDataChangeEvent;
import cern.c2mon.daq.opcua.jintegraInterface.IOPCAutoServer;
import cern.c2mon.daq.opcua.jintegraInterface.IOPCGroup;
import cern.c2mon.daq.opcua.jintegraInterface.IOPCGroups;
import cern.c2mon.daq.opcua.jintegraInterface.OPCDataSource;
import cern.c2mon.daq.opcua.jintegraInterface.OPCGroup;
import cern.c2mon.daq.opcua.jintegraInterface.OPCItem;
import cern.c2mon.daq.opcua.jintegraInterface.OPCItems;
import cern.c2mon.daq.opcua.jintegraInterface.OPCServer;
import cern.c2mon.daq.opcua.jintegraInterface.OPCServerState;

import com.linar.jintegra.AuthInfo;
import com.linar.jintegra.AutomationException;

/**
 * The DADCOMEndpoint represents an endpoint to connect via DCOM to a classic
 * OPC server.
 *
 * @author Andreas Lang
 *
 */
public class DADCOMEndpoint extends OPCEndpoint<DADCOMItemDefintion> {

    /**
     * Timeout of the DCOM connection.
     */
    private static final String TIMEOUT = "10000";

    /**
     * The cancel ID for async refreshes
     */
    private static final int[] CANCEL_ID = new int[1];

    /**
     * The OPC server object.
     */
    private IOPCAutoServer server;

    /**
     * Groupname counter to create unique group names.
     */
    private Long groupNameCounter = 0L;

    /**
     * Map of client item handles to opc items.
     */
    private final Map<Integer, OPCItem> itemHandleOpcItems =
        new HashMap<Integer, OPCItem>();

    /**
     * Group for all OPCItems used as commands.
     */
    private OPCGroup opcCommandGroup;

    /**
     * The authentication info of this endpoint.
     */
    private AuthInfo authInfo;

    /**
     * Thread pool for incomming data.
     */
    private ExecutorService executorService;

    /**
     * Calendar to correct GMT time given by OPC server to local time
     */
    private final GregorianCalendar gregorianCalendar = new GregorianCalendar();

    /**
     * logger of this class.
     */
    private final static Logger logger = LoggerFactory.getLogger(DADCOMEndpoint.class);

    /**
     * Creates a new DADCOMEndpoint.
     *
     * @param itemDefinitionFactory
     *            The factory to create item defintions from data tags.
     * @param groupProvider
     *            The strategy how items should be grouped to subscriptions.
     */
    public DADCOMEndpoint(
            final IItemDefinitionFactory<DADCOMItemDefintion> itemDefinitionFactory,
            final IGroupProvider<DADCOMItemDefintion> groupProvider) {
        super(itemDefinitionFactory, groupProvider);
        System.setProperty("JINTEGRA_OUTGOING_CONNECTION_TIMEOUT", TIMEOUT);
        System.setProperty("JINTEGRA_INCOMING_CONNECTION_TIMEOUT", TIMEOUT);
    }

    /**
     * Initializes the endpoint with the properties for the connection.
     *
     * @param opcAddress
     *            The address of this endpoint.
     */
    @Override
    protected synchronized void onInit(final AbstractOPCUAAddress opcAddress) {
        URI uri = opcAddress.getUri();
        String domain = opcAddress.getDomain();
        String user = opcAddress.getUser();
        String password = opcAddress.getPassword();
        authInfo = new AuthInfo(domain, user, password);
        executorService = Executors.newFixedThreadPool(1);
        try {
            setUpConnection(uri);
        } catch (AutomationException e) {
            throw OPCDCOMFactory.createWrappedAutomationException(e);
        } catch (Exception e) {
            throw new OPCCommunicationException("Problems wih the DCOM connection occured", e);
        }
    }

    /**
     * Sets up the OPC server connection.
     *
     * @param uri
     *            The URI of the OPC server.
     * @throws IOException
     *             Thrown if there is a connection error.
     */
    private void setUpConnection(final URI uri) throws IOException {
        logger.debug("Establishing NTdomain: " + AuthInfo.getCallerDomain() + ", NTuser: " + AuthInfo.getCallerUser() + ", NTpassword: ********");
        AuthInfo.setThreadDefault(authInfo);
        String host = uri.getHost();
        String opcServer = uri.getPath().replaceFirst("/", "");
        server = new OPCServer(host);
        if (opcServer != null && !opcServer.equals("")) {
            logger.info("Trying to connect to OPC Server " + opcServer + " on host " + host);
            server.connect(opcServer, host);
        } else {
            String[] servers = (String[]) server.getOPCServers(host);
            if (servers == null || servers.length == 0) {
                throw new OPCCommunicationException("No OPC servers on the"
                        + "provided host");
            } else {
                for (int i = 0; i < servers.length; i++) {
                  logger.info("Host " + host + " : Found OPC server named " + servers[i]);
                }
                logger.info("Trying to connect to OPC Server " + servers[0] + " on host " + host);
                server.connect(servers[0], host);
            }
        }
    }

    /**
     * Subscribes to the items in the subscription groups.
     *
     * @param subscriptionGroups
     *            The groups to subscribe to.
     */
    @Override
    protected synchronized void onSubscribe(final Collection<SubscriptionGroup<DADCOMItemDefintion>> subscriptionGroups) {
        AuthInfo.setThreadDefault(authInfo);
        try {
            if (subscriptionGroups.isEmpty()) {
              logger.warn("onSubscribe() - No subscription groups defined -> DAQ won't be subscribed to OPC items!");
            }
            for (SubscriptionGroup<DADCOMItemDefintion> subscritionGroup : subscriptionGroups) {
                processGroup(subscritionGroup);
            }
            server.getOPCGroups();
        } catch (AutomationException e) {
            throw OPCDCOMFactory.createWrappedAutomationException(e);
        } catch (Exception e) {
            throw new OPCCommunicationException(e);
        }
    }

    /**
     * Processes a subscription group which means subscribes for it and
     * adds a listener.
     *
     * @param subscritionGroup The group to subscribe to.
     * @return The OPCGroup which is used for the subscription.
     * @throws IOException The connection may have IOExceptions.
     * @throws AutomationException Server side automation errors can be thrown.
     */
    private IOPCGroup processGroup(
            final SubscriptionGroup<DADCOMItemDefintion> subscritionGroup)
            throws IOException, AutomationException {
        IOPCGroup group = subscribe(subscritionGroup);
        group.addDIOPCGroupEventListener(new DIOPCGroupEventAdapter() {
            private static final long serialVersionUID = 1L;

            @Override
            public void dataChange(final DIOPCGroupEventDataChangeEvent theEvent) throws IOException {
                notifyListeners(theEvent);
            }

        });
        group.setIsSubscribed(true);
        try {
          group.asyncRefresh((short) OPCDataSource.OPCDevice, 666, CANCEL_ID);
        }
        catch (Exception ex) {
          logger.warn("processGroup() - Asynchronous refresh failed!", ex);
        }
        return group;
    }

    /**
     * Subscribes to all the items in the provided SubscriptionGroup.
     *
     * @param subscriptionGroup
     *            The SubscriptionGroup with the items to connect to.
     * @return The created group.
     * @throws IOException
     *             Throws an IOException if the subscription to the server fails
     *             due to an I/O error.
     */
    private IOPCGroup subscribe(final SubscriptionGroup<DADCOMItemDefintion> subscriptionGroup) throws IOException {
        IOPCGroups opcGroups = server.getOPCGroups();
        int updateRate = subscriptionGroup.getTimeDeadband();
        float valueDeadband = subscriptionGroup.getValueDeadband();
        IOPCGroup group = OPCDCOMFactory.createOPCGroup(opcGroups, getNewGroupName(), updateRate, valueDeadband);
        OPCItems items = group.getOPCItems();
        for (DADCOMItemDefintion definition : subscriptionGroup.getUnsubscribedDefinitions()) {
            long itemDefinitionId = definition.getId();
            int clientHandle = Long.valueOf(itemDefinitionId).intValue();
            String itemAddress = definition.getAddress();

            // Mark this item as subscribed, in order to prevent re-creating it
            // unnecessarily (see TIMD-140)
            definition.setSubscribed(true);

            try {
                OPCItem item = OPCDCOMFactory.createOPCItem(items, clientHandle, itemAddress);
                itemHandleOpcItems.put(clientHandle, item);
                if (definition.hasRedundantAddress()) {
                    // use just the negative value makes it easy to map back
                    int redundantClientHandle = -clientHandle;
                    String redundantItemAddress = definition.getRedundantAddress();
                    item = OPCDCOMFactory.createOPCItem(items, redundantClientHandle, redundantItemAddress);
                    itemHandleOpcItems.put(redundantClientHandle, item);
                }
            } catch (AutomationException e) {
                RuntimeException ex = OPCDCOMFactory.createWrappedAutomationException(e, itemAddress);
                notifyEndpointListenersItemError(itemDefinitionId, ex);
            } catch (Throwable e) {
                notifyEndpointListenersItemError(itemDefinitionId, e);
            }
        }
        return group;
    }

    /**
     * Returns a new unique group name.
     *
     * @return The new group name.
     */
    private String getNewGroupName() {
        String groupName = "Group" + groupNameCounter;
        groupNameCounter = (groupNameCounter + 1) % Integer.MAX_VALUE;
        return groupName;
    }

    /**
     * Notifies all listeners about the updates in the event.
     *
     * @param theEvent
     *            The event with the updates.
     */
    private void notifyListeners(final DIOPCGroupEventDataChangeEvent theEvent) {
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                int[] clientHandles = theEvent.getClientHandles();
                Date[] timestamps = theEvent.getTimeStamps();
                Object[] values = theEvent.getItemValues();
                int[] qualities = theEvent.getQualities();
                for (int i = 0; i < clientHandles.length; i++) {
                    // redundant addresses have negative id
                    long itemAdressId = Math.abs(clientHandles[i]);
                    if (isGoodQuality(qualities[i])) {
                        Object value = values[i];
                        long timestamp = timestamps[i].getTime();
                        notifyEndpointListenersValueChange(itemAdressId, getAdjustedTimestamp(timestamp), value);
                    } else {
                        OPCCommunicationException ex = OPCDCOMFactory.createQualityException(qualities[i]);
                        notifyEndpointListenersItemError(itemAdressId, ex);
                    }
                }
            }
        });
    }

    /**
     * Returns the adjusted OPC timestamp to the time zone
     * of the DAQ server.
     *
     * @param opcTimestamp The GMT timestamp received from the OPC
     * @return The adjusted timestamp in milliseconds.
     */
    private synchronized long getAdjustedTimestamp(long opcTimestamp) {
      gregorianCalendar.setTimeInMillis(opcTimestamp);
      int timezoneAdjustment =
        gregorianCalendar.get(GregorianCalendar.ZONE_OFFSET) + gregorianCalendar.get(GregorianCalendar.DST_OFFSET);
      return opcTimestamp + (long) timezoneAdjustment;
    }

    /**
     * True for a good quality.
     *
     * @param quality The quality to check.
     * @return True if the quality is good else false.
     */
    private boolean isGoodQuality(final int quality) {
        /*
         * To decide if the quality is good it is only important to know the
         * first two bits of the last byte. They should both be 1. 1100 0000
         */
        return (quality & 0xC0) == 0xC0;
    }

//    /**
//     * Refreshes the values of a collection of item definitions.
//     *
//     * @param itemDefintions
//     *            The item definitions to refresh.
//     */
//    @Override
//    protected synchronized void onRefresh(
//            final Collection<DADCOMItemDefintion> itemDefintions) {
//        AuthInfo.setThreadDefault(authInfo);
//        logger.debug("Enter refresh");
//        for (DADCOMItemDefintion definition : itemDefintions) {
//            long itemDefinitionId = definition.getId();
//            int clientHandle = Long.valueOf(itemDefinitionId).intValue();
//            OPCItem item = itemHandleOpcItems.get(clientHandle);
//            if (item != null) {
//                Object[] value = new Object[1];
//                Object[] quality = new Object[1];
//                Object[] timeStamp = new Object[1];
//                try {
//                	logger.debug("Reading from OPC for item: " + item.getItemID());
//                    item.read((short) OPCDataSource.OPCCache, value, quality, timeStamp);
//                    notifyEndpointListenersValueChange(
//                            itemDefinitionId, getAdjustedTimestamp(((Date) timeStamp[0]).getTime()), value[0]);
//                    if (definition.hasRedundantAddress()) {
//                        clientHandle = -Long.valueOf(definition.getId()).intValue();
//                        item = itemHandleOpcItems.get(clientHandle);
//                        item.read((short) OPCDataSource.OPCCache, value, quality, timeStamp);
//                        notifyEndpointListenersValueChange(itemDefinitionId, getAdjustedTimestamp(((Date) timeStamp[0]).getTime()), value[0]);
//                    }
//                } catch (AutomationException e) {
//                    RuntimeException ex = OPCDCOMFactory.createWrappedAutomationException(e, definition.getAddress());
//                    notifyEndpointListenersItemError(itemDefinitionId, ex);
//                } catch (Exception e) {
//                    notifyEndpointListenersItemError(itemDefinitionId, e);
//                }
//            }
//        }
//        logger.debug("Finished refresh");
//    }

    /**
     * Refreshes the values of a collection of item definitions.
     * TODO: This method needs to be rewritten since it does not work
     *       most of the time, especially after an OPC reboot!
     *
     * @param itemDefintions
     *            The item definitions to refresh.
     */
    @Override
    protected synchronized void onRefresh(
            final Collection<DADCOMItemDefintion> itemDefintions) {

      logger.debug("onRefresh() - Method call is disabled for DCOM. An async refresh is triggered at initialization by processGroup() method");

//        AuthInfo.setThreadDefault(authInfo);
//        logger.debug("Enter refresh");
//        Set<OPCGroup> opcGroups = new HashSet<OPCGroup>();
//        for (DADCOMItemDefintion definition : itemDefintions) {
//            logger.debug("onRefresh() - Trying to determine group for definition " + definition.getAddress());
//            long itemDefinitionId = definition.getId();
//            int clientHandle = Long.valueOf(itemDefinitionId).intValue();
//            OPCItem item = itemHandleOpcItems.get(clientHandle);
//            if (item != null) {
//                try {
//                  OPCGroup group = item.getParent();
//                  if (opcGroups.add(group)) {
//                    logger.debug("onRefresh() - Added group " + group.getName());
//                  }
//                } catch (AutomationException e) {
//                    RuntimeException ex = OPCDCOMFactory.createWrappedAutomationException(e, definition.getAddress());
//                    notifyEndpointListenersItemError(itemDefinitionId, ex);
//                } catch (Exception e) {
//                    notifyEndpointListenersItemError(itemDefinitionId, e);
//                }
//            }
//        }
//        for (OPCGroup group : opcGroups) {
//          try {
//            logger.debug("onRefresh() - trigger async refresh for group " + group.getName());
//            group.asyncRefresh((short) OPCDataSource.OPCDevice, 666, CANCEL_ID);
//          } catch (AutomationException e) {
//              throw OPCDCOMFactory.createWrappedAutomationException(e);
//          } catch (Exception e) {
//              throw new OPCCommunicationException("Problems wih the async DCOM group refresh occured", e);
//          }
//        }
//        logger.debug("Finished refresh");
    }

    /**
     * Calling a method is not supported by a DADCOMEndpoint.
     *
     * @param itemDefintion
     *            ignored.
     * @param value
     *            ignored.
     */
    @Override
    protected synchronized void onCallMethod(
            final DADCOMItemDefintion itemDefintion, final Object... value) {
        throw new OPCCommunicationException(
                "Methods are not supported by the OPC DA" + "DCOM endpoint.");
    }

    /**
     * Writes a value to the OPC server.
     *
     * @param itemDefintion
     *            Defines where to write.
     * @param value
     *            The value to write to the item.
     */
    @Override
    protected synchronized void onWrite(
            final DADCOMItemDefintion itemDefintion, final Object value) {
        AuthInfo.setThreadDefault(authInfo);
        try {
            OPCItems items = getCommandGroup().getOPCItems();
            int clientHandle = Long.valueOf(itemDefintion.getId()).intValue();
            OPCItem item = itemHandleOpcItems.get(clientHandle);
            if (item == null) {
                item = items.addItem(itemDefintion.getAddress(), clientHandle);
                itemHandleOpcItems.put(clientHandle, item);
            }
            item.write(value);
        } catch (AutomationException e) {
            throw OPCDCOMFactory.createWrappedAutomationException(e, itemDefintion.getAddress());
        } catch (Exception e) {
            throw new OPCCommunicationException("Problems wih the DCOM connection occured", e);
        }

    }

    /**
     * Returns the command group of this endpoint.
     *
     * @return The new OPCGroup.
     * @throws IOException
     *             Throws an IOException if the DCOM connection fails.
     */
    private OPCGroup getCommandGroup() throws IOException {
        if (opcCommandGroup == null) {
            opcCommandGroup = server.getOPCGroups().add("CommandGroup");
        }
        return opcCommandGroup;
    }

    /**
     * Stops this endpoint and resets it to pre initialization state.
     */
    @Override
    protected synchronized void onStop() {
        AuthInfo.setThreadDefault(authInfo);
        try {
            server.getOPCGroups().removeAll();
            server.disconnect();
            server.release();
        } catch (AutomationException e) {
            logger.error("Exception disconnecting from OPC server: "
                + OPCDCOMFactory.createWrappedAutomationException(e));
        }
        catch (Exception ex) {
            logger.error("Exception disconnecting from OPC server: " + ex);
        }

        try {
            opcCommandGroup = null;
            executorService.shutdown();
        } catch (Exception e) {
            throw new OPCCommunicationException(e);
        }
        finally {
            server = null;
            itemHandleOpcItems.clear();
        }
    }

    /**
     * Called when a subsription is removed.
     *
     * @param subscriptionGroup The subscription to remove.
     * @param removedDefinition The removed subscription.
     */
    @Override
    protected synchronized void onRemove(
            final SubscriptionGroup<DADCOMItemDefintion> subscriptionGroup,
            final DADCOMItemDefintion removedDefinition) {
        AuthInfo.setThreadDefault(authInfo);
        if (itemHandleOpcItems.containsKey(removedDefinition.getId())) {
            OPCItem item = itemHandleOpcItems.get(removedDefinition.getId());
            try {
                item.setIsActive(false);
                IOPCGroup group = item.getParent();
                int[] serverHandles = { item.getServerHandle() };
                group.getOPCItems().remove(1, serverHandles, new int[0][0]);
            } catch (AutomationException e) {
                throw OPCDCOMFactory.createWrappedAutomationException(e, removedDefinition.getAddress());
            } catch (Exception e) {
                throw new OPCCommunicationException(
                        "Problems wih the DCOM connection occured", e);
            }
        }

    }

    /**
     * Subscribes for a subscription group.
     *
     * @param subscriptionGroup The subscription group to subscribe.
     */
    @Override
    protected synchronized void onSubscribe(
            final SubscriptionGroup<DADCOMItemDefintion> subscriptionGroup) {
        AuthInfo.setThreadDefault(authInfo);
        try {
            processGroup(subscriptionGroup);
        } catch (AutomationException e) {
            throw OPCDCOMFactory.createWrappedAutomationException(e);
        } catch (Exception e) {
            throw new OPCCommunicationException("Problems wih the DCOM connection occured", e);
        }
    }

    /**
     * Checks the status of the enpoint. It will throw an exception if something
     * is wrong.
     *
     * @throws OPCCommunicationException Thrown if the connection is not
     * reachable but might be back later on.
     * @throws OPCCriticalException Thrown if the connection is not
     * reachable and can most likely not be restored.
     */
    @Override
    protected void checkStatus() {
        AuthInfo.setThreadDefault(authInfo);
        try {
            String state = "OK";
            int stateId = server.getServerState();
            switch (stateId) {
              case OPCServerState.OPCRunning:
                // fine do nothing
                break;
              case OPCServerState.OPCDisconnected:
                state = "OPC disconnected";
                break;
              case OPCServerState.OPCFailed:
                state = "OPC failed";
                break;
              case OPCServerState.OPCNoconfig:
                state = "OPC has no configuration";
                break;
              case OPCServerState.OPCSuspended:
                state = "OPC suspended";
                break;
              case OPCServerState.OPCTest:
                state = "OPC in TEST state";
                break;
              default:
                state = "OPC state unknown";
            }

            if (stateId != OPCServerState.OPCRunning) {
              // not fine throw exception
              throw new OPCCommunicationException("OPC server state wrong: " + state);
            }
        } catch (AutomationException e) {
            throw OPCDCOMFactory.createWrappedAutomationException(e);
        } catch (IOException e) {
            throw new OPCCommunicationException(e);
        }
    }

}
