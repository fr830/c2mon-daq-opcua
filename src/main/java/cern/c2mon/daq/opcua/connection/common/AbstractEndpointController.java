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

package cern.c2mon.daq.opcua.connection.common;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Timer;

import lombok.extern.slf4j.Slf4j;

import cern.c2mon.daq.common.IEquipmentMessageSender;
import cern.c2mon.daq.common.conf.equipment.ICommandTagChanger;
import cern.c2mon.daq.common.conf.equipment.IDataTagChanger;
import cern.c2mon.daq.opcua.EndpointEquipmentLogListener;
import cern.c2mon.daq.opcua.EndpointTypesUnknownException;
import cern.c2mon.daq.opcua.connection.common.IOPCEndpoint.STATE;
import cern.c2mon.daq.opcua.connection.common.impl.AliveWriter;
import cern.c2mon.daq.opcua.connection.common.impl.OPCCommunicationException;
import cern.c2mon.daq.opcua.connection.common.impl.OPCCriticalException;
import cern.c2mon.daq.opcua.connection.common.impl.StatusChecker;
import cern.c2mon.shared.common.command.ISourceCommandTag;
import cern.c2mon.shared.common.datatag.ISourceDataTag;
import cern.c2mon.shared.common.datatag.SourceDataQuality;
import cern.c2mon.shared.common.datatag.address.OPCHardwareAddress;
import cern.c2mon.shared.common.process.IEquipmentConfiguration;
import cern.c2mon.shared.daq.command.SourceCommandTagValue;
import cern.c2mon.shared.daq.config.ChangeReport;
import cern.c2mon.shared.daq.config.ChangeReport.CHANGE_STATE;

/**
 * AbstractEndpointController abstract class (no implementation of this class)
 *
 * @author vilches
 */
@Slf4j
public abstract class AbstractEndpointController implements IOPCEndpointListener, ICommandTagChanger, IDataTagChanger {

  /**
   * Properties for the opc endpoint.
   */
  protected List<? extends AbstractOPCUAAddress> opcAddresses = null;

  /**
   * The OPCAddress of the current endpoint.
   */
  protected AbstractOPCUAAddress currentAddress;

  /**
   * Factory to create OPCEndpoints.
   */
  protected IOPCEndpointFactory opcEndpointFactory = null;

  /**
   * The currently controlled endpoint.
   */
  protected IOPCEndpoint endpoint;

  /**
   * The EquipmentMessageSender to send updates to.
   */
  protected IEquipmentMessageSender sender;

  /**
   * The equipment specific log.
   */
//    private final Equipmentlog log;

  /**
   * Event listener and log for OPC endpoints to the C2MON equipment
   * log.
   */
  protected EndpointEquipmentLogListener logListener = null;

  /**
   * The alive writer to write to the OPC.
   */
  protected AliveWriter writer;

  /**
   * The equipment configuration for his controller.
   */
  protected IEquipmentConfiguration equipmentConfiguration;

  private Timer statusCheckTimer;

  /**
   * Reason why the connection cannot be done
   */
  private String noConnectionReason;

  /**
   * Starts this controllers endpoint for the first time. After this method is called the
   * controller will receive updates.
   *
   * @return True if the connection was successful or false in any other case
   */
  public synchronized boolean startEndpoint() {
    try {
      startProcedure();
    }
    catch (OPCCommunicationException e) {
      log.error("Endpoint creation failed. Controller will try again. ", e);
      // Restart Endpoint
      triggerEndpointRestart("Problems connecting to " + currentAddress.getUri().getHost() + ": " + e.getMessage());

      return false;
    }

    return true;
  }

  /**
   * Restart the controller endpoint if the first attempt fails
   *
   * @return True if the reconnection was successful or false in any other case
   */
  public synchronized boolean restartEndpoint() {
    try {
      startProcedure();
    }
    catch (OPCCommunicationException e) {
      log.error("Endpoint creation failed. Controller will try again. ", e);
      // Reason
      this.noConnectionReason = "Problems connecting to " + currentAddress.getUri().getHost() + ": " + e.getMessage();

      return false;
    }

    return true;
  }

  /**
   * Procedure common to all starts (start and restart)
   */
  protected synchronized void startProcedure() throws OPCCommunicationException {
    // Create Endpoint
    createEndpoint();

    // Register Listeners
    this.endpoint.registerEndpointListener(this.logListener);
    this.endpoint.registerEndpointListener(this);

    // Add Tags to endpoint
    addTagsToEndpoint();

    // Send info message to Comm_fault tag
    this.sender.confirmEquipmentStateOK("Connected to " + currentAddress.getUri().getHost());

    startAliveTimer();
    setUpStatusChecker();

    // Change endpoint status to operational
    this.endpoint.setStateOperational();
  }

  /**
   * Add Data and Command tags to the the endpoint
   */
  protected void addTagsToEndpoint() {
    // Datatags
    this.endpoint.addDataTags(this.equipmentConfiguration.getSourceDataTags().values());
    // CommandTags
    this.endpoint.addCommandTags(this.equipmentConfiguration.getSourceCommandTags().values());
  }

  /**
   * Sets up and schedules a regular status check.
   */
  protected void setUpStatusChecker() {
    stopStatusChecker();
    statusCheckTimer = new Timer("OPCStatusChecker");
    int serverTimeout = getCurrentOPCAddress().getServerTimeout();
    log.info("Starting OPCStatusChecker for endpoint address: " + getCurrentOPCAddress().getUriString());
    statusCheckTimer.schedule(new StatusChecker(endpoint) {

      @Override
      public void onOPCUnknownException(
              final IOPCEndpoint endpoint, final Exception e) {
        // critical should shutdown
        log.error("Status of and endpoint could not be determined "
                + "because of an unexpected exception. Shutting down.", e);
        stop();
      }

      @Override
      public void onOPCCriticalException(
              final IOPCEndpoint endpoint, final OPCCriticalException e) {
        // critical should shutdown
        log.error("Status of and endpoint could not be determined "
                + "because of a critical OPC exception. Shutting down.", e);
        stop();
      }

      @Override
      public void onOPCCommunicationException(
              final IOPCEndpoint endpoint, final OPCCommunicationException e) {
        log.error("OPCCommunication exception try to restart.", e);
        triggerEndpointRestart(e.getMessage());
      }
    }, serverTimeout, serverTimeout);
  }

  /**
   *
   */
  public void stopStatusChecker() {
    if (statusCheckTimer != null) {
      log.info("Stopping OPCStatusChecker...");
      statusCheckTimer.cancel();
    }
  }

  /**
   * Starts the alive timer.
   */
  public synchronized void startAliveTimer() {
    ISourceDataTag targetTag = equipmentConfiguration.getSourceDataTag(
            equipmentConfiguration.getAliveTagId());
    boolean aliveWriterEnabled = getCurrentOPCAddress().isAliveWriterEnabled();
    if (!aliveWriterEnabled) {
      log.info("Equipment Alive Timer has been disabled in the configuration ==> Alive Timer has not been started.");
    }
    else if (targetTag == null) {
      log.error("No equipment alive tag is specified. Check the configuration! ==> Alive Timer has not been started.");
    }
    else {
      writer = new AliveWriter(endpoint, equipmentConfiguration.getAliveTagInterval() / 2, targetTag);
      writer.startWriter();
    }
  }

  /**
   * Stops the alive timer.
   */
  public synchronized void stopAliveTimer() {
    if (writer != null) {
      writer.stopWriter();
    }
  }


  /**
   * Makes sure there is a created and initialized endpoint.
   */
  protected void createEndpoint() {
    if (this.endpoint == null || this.endpoint.getState() == STATE.NOT_INITIALIZED) {
      AbstractOPCUAAddress address = getNextOPCAddress();
      log.info("createEndpoint - Trying to create endpoint '" + address.getUriString() + "'");
      this.endpoint = this.opcEndpointFactory.createEndpoint(address);
      if (this.endpoint == null && opcAddresses.size() > 1) {
        log.warn("createEndpoint - Endpoint creation for '" + address.getUriString() + "' failed. Trying alternative address.");
        // try alternative address
        address = getNextOPCAddress();
        this.endpoint = this.opcEndpointFactory.createEndpoint(address);
      }
      if (this.endpoint != null) {
        this.endpoint.initialize(address);
        log.info("createEndpoint - Endpoint '" + address.getUriString() + "' created and initialized");
      }
      else {
        // There was no type matching an implemented endpoint
        log.error("createEndpoint - Endpoint creation for '" + address.getUriString() + "' failed. Stop Startup.");
        throw new EndpointTypesUnknownException();
      }
    }
  }

  /**
   * Stops this endpoint and goes back to the state before the start method
   * was called.
   */
  public synchronized void stop() {
    stopAliveTimer();
    stopStatusChecker();
    if (endpoint != null)
      endpoint.reset();
  }

  /**
   * Returns the next available OPCAddress. If there is no second address the
   * first one will be returned.
   *
   * @return The next available OPCAddress.
   */
  protected synchronized AbstractOPCUAAddress getNextOPCAddress() {
    if (this.currentAddress == null) {
      this.currentAddress = this.opcAddresses.get(0);
    }
    else if (this.opcAddresses.size() > 1) {
      if (this.opcAddresses.get(0).equals(this.currentAddress)) {
        this.currentAddress = this.opcAddresses.get(1);
      }
      else {
        this.currentAddress = this.opcAddresses.get(0);
      }
    }
    return this.currentAddress;
  }

  /**
   * Returns the current OPCUA Address
   *
   * @return The OPCUAAddress used at the moment.
   */
  protected synchronized AbstractOPCUAAddress getCurrentOPCAddress() {
    return this.currentAddress;
  }

  /**
   * Implementation of the IOPCEndpointListener interface. The endpoint
   * controller will forward updates to the core (EquipmentMessageSender).
   *
   * @param dataTag   The data tag which has a changed value.
   * @param timestamp The timestamp when the value was updated.
   * @param tagValue  The changed value.
   */
  @Override
  public void onNewTagValue(final ISourceDataTag dataTag, final long timestamp, final Object tagValue) {
    log.debug("onNewTagValue - New Tag value received for Tag #" + dataTag.getId());

    this.sender.sendTagFiltered(dataTag, tagValue, timestamp);

    if (log.isDebugEnabled()) {
      log.debug("onNewTagValue - Tag value " + tagValue + " sent for Tag #" + dataTag.getId());
    }
  }

  /**
   * Refreshes the values of all added data tags.
   */
  public synchronized void refresh() {
    log.info("refresh - Refreshing values of all data tags.");
    requiresEndpoint();
    this.endpoint.refreshDataTags(this.equipmentConfiguration.getSourceDataTags().values());
  }

  /**
   * Refreshes the values of the provided source data tag.
   *
   * @param sourceDataTag The source data tag to refresh the value for.
   */
  public synchronized void refresh(final ISourceDataTag sourceDataTag) {
    requiresEndpoint();
    Collection<ISourceDataTag> tags = new ArrayList<ISourceDataTag>(1);
    tags.add(sourceDataTag);
    log.info("Refreshing value of data tag with id '" + sourceDataTag.getId() + "'.");
    this.endpoint.refreshDataTags(tags);
  }

  /**
   * Invalidates the tag which caused an exception in an endpoint.
   *
   * @param dataTag The data tag which caused an exception.
   * @param cause   The exception which was caused in the endpoint.
   */
  @Override
  public void onTagInvalidException(final ISourceDataTag dataTag, final Throwable cause) {
    if (log.isDebugEnabled()) {
      log.debug("Tag invalid: " + cause.getClass().getSimpleName() + ": "
              + cause.getMessage());
    }
    this.sender.sendInvalidTag(dataTag, SourceDataQuality.DATA_UNAVAILABLE, cause.getMessage());
  }

  /**
   * When this is called a serious error happened for our subscriptions. A
   * full restart is required.
   *
   * @param cause The cause of the subscription loss.
   */
  @Override
  public void onSubscriptionException(final Throwable cause) {
    log.error("Subscription failed. Restarting endpoint.", cause);
    triggerEndpointRestart(cause.getMessage());
  }


  /**
   * Triggers the restart of this endpoint.
   *
   * @param reason The reason of the restart, if any applicable.
   */
  protected synchronized void triggerEndpointRestart(final String reason) {
    this.noConnectionReason = reason;

    // Do while the endpoint state changes to OPERATOINAL
    do {
      // Stop endpoint and send message to COMM_FAULT tag
      try {
        AbstractEndpointController.this.stop();
      }
      catch (Exception ex) {
        log.warn("triggerEndpointRestart - Error stopping endpoint subscription for " +
                getCurrentOPCAddress().getUri().getHost(), ex);
      } finally {
        if (this.noConnectionReason == null || this.noConnectionReason.equalsIgnoreCase("")) {
          sender.confirmEquipmentStateIncorrect();
        }
        else {
          sender.confirmEquipmentStateIncorrect(this.noConnectionReason);
        }
      }

      // Sleep before retrying
      try {
        if (log.isDebugEnabled()) {
          log.debug("triggerEndpointRestart - Server " + getCurrentOPCAddress().getUri().getHost()
                  + " - Sleeping for " + getCurrentOPCAddress().getServerRetryTimeout() + " ms ...");
        }
        Thread.sleep(getCurrentOPCAddress().getServerRetryTimeout());
      }
      catch (InterruptedException e) {
        log.error("Subscription restart interrupted for " + getCurrentOPCAddress().getUri().getHost(), e);
      }

      // Retry
      try {
        if (!restartEndpoint()) {
          log.error("Error restarting Endpoint for " + getCurrentOPCAddress().getUri().getHost());
        }
        else {
          refresh();
        }
      }
      catch (Exception e) {
        log.error("Error restarting subscription for " + getCurrentOPCAddress().getUri().getHost(), e);
      }
    } while (endpoint.getState() != STATE.OPERATIONAL);
    log.info("triggerEndpointRestart - Exiting OPC Endpoint restart procedure for " + getCurrentOPCAddress().getUri().getHost());
  }


  /**
   * Runs a command on the current endpoint.
   *
   * @param commandTag            The command to run.
   * @param sourceCommandTagValue The value description of the command to run.
   */
  public void runCommand(final ISourceCommandTag commandTag, final SourceCommandTagValue sourceCommandTagValue) {
    requiresEndpoint();
    this.endpoint.executeCommand((OPCHardwareAddress) commandTag.getHardwareAddress(), sourceCommandTagValue);
  }

  /**
   * Adds a command tag to the controller.
   *
   * @param sourceCommandTag The command tag to add.
   * @param changeReport     The report to append information.
   */
  @Override
  public void onAddCommandTag(final ISourceCommandTag sourceCommandTag, final ChangeReport changeReport) {
    log.info("Adding command tag " + sourceCommandTag.getId());
    requiresEndpoint();
    this.endpoint.addCommandTag(sourceCommandTag);
    changeReport.appendInfo("CommandTag added.");
    changeReport.setState(CHANGE_STATE.SUCCESS);
    log.info("Added command tag " + sourceCommandTag.getId());
  }

  /**
   * Removes a command tag from the controller.
   *
   * @param sourceCommandTag The command tag to remove.
   * @param changeReport     The report to append information.
   */
  @Override
  public void onRemoveCommandTag(final ISourceCommandTag sourceCommandTag, final ChangeReport changeReport) {
    log.info("Removing command tag " + sourceCommandTag.getId());
    requiresEndpoint();
    this.endpoint.removeCommandTag(sourceCommandTag);
    changeReport.appendInfo("CommandTag removed.");
    changeReport.setState(CHANGE_STATE.SUCCESS);
    log.info("Removed command tag " + sourceCommandTag.getId());

  }

  /**
   * Updates a command tag.
   *
   * @param sourceCommandTag    The command tag to updates.
   * @param oldSourceCommandTag A copy of the former command tag state.
   * @param changeReport        The report to append information.
   */
  @Override
  public void onUpdateCommandTag(final ISourceCommandTag sourceCommandTag, final ISourceCommandTag oldSourceCommandTag, final ChangeReport changeReport) {
    log.info("Updating command tag " + sourceCommandTag.getId());
    requiresEndpoint();
    if (!sourceCommandTag.getHardwareAddress().equals(oldSourceCommandTag.getHardwareAddress())) {
      this.endpoint.removeCommandTag(oldSourceCommandTag);
      this.endpoint.addCommandTag(sourceCommandTag);
      changeReport.appendInfo("CommandTag updated.");
    }
    else {
      changeReport.appendInfo("No changes for OPC necessary.");
    }
    changeReport.setState(CHANGE_STATE.SUCCESS);
    log.info("Updated command tag " + sourceCommandTag.getId());
  }

  /**
   * Adds a data tag.
   *
   * @param sourceDataTag The data tag to add.
   * @param changeReport  The change report object which needs to be filled.
   */
  @Override
  public void onAddDataTag(final ISourceDataTag sourceDataTag, final ChangeReport changeReport) {
    log.info("Adding data tag " + sourceDataTag.getId());
    requiresEndpoint();
    this.endpoint.addDataTag(sourceDataTag);
    refresh(sourceDataTag);
    changeReport.appendInfo("DataTag added.");
    changeReport.setState(CHANGE_STATE.SUCCESS);
    log.info("Added data tag " + sourceDataTag.getId());
  }

  /**
   * Removes a data tag.
   *
   * @param sourceDataTag The data tag to remove.
   * @param changeReport  The change report object which needs to be filled.
   */
  @Override
  public void onRemoveDataTag(final ISourceDataTag sourceDataTag, final ChangeReport changeReport) {
    log.info("Removing data tag " + sourceDataTag.getId());
    requiresEndpoint();
    this.endpoint.removeDataTag(sourceDataTag);
    changeReport.appendInfo("DataTag removed.");
    changeReport.setState(CHANGE_STATE.SUCCESS);
    log.info("Removed data tag " + sourceDataTag.getId());
  }

  /**
   * Updates a data tag.
   *
   * @param sourceDataTag    The data tag to update.
   * @param oldSourceDataTag A copy of the former state of the data tag.
   * @param changeReport     The change report object which needs to be filled.
   */
  @Override
  public void onUpdateDataTag(final ISourceDataTag sourceDataTag, final ISourceDataTag oldSourceDataTag, final ChangeReport changeReport) {
    log.info("Updating data tag " + sourceDataTag.getId());
    requiresEndpoint();
    if (!sourceDataTag.getHardwareAddress().equals(oldSourceDataTag.getHardwareAddress())) {
      this.endpoint.removeDataTag(oldSourceDataTag);
      this.endpoint.addDataTag(sourceDataTag);
      changeReport.appendInfo("Data tag updated.");
    }
    else {
      changeReport.appendInfo("No changes for OPC necessary.");
    }
    changeReport.setState(CHANGE_STATE.SUCCESS);
    log.info("Updated data tag " + sourceDataTag.getId());
  }

  /**
   * Checks if there is an endpoint created. If not an exception is thrown.
   */
  private void requiresEndpoint() {
    if (this.endpoint == null || this.endpoint.getState() == STATE.NOT_INITIALIZED)
      throw new OPCCriticalException(
              "No Endpoint was created or Endpoint was not initialized/started.");
  }

  /**
   * @return the writer
   */
  public AliveWriter getWriter() {
    return writer;
  }
}
