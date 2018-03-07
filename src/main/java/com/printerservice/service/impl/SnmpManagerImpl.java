package com.printerservice.service.impl;

import com.printerservice.service.SnmpManager;
import org.snmp4j.CommunityTarget;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.TransportMapping;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.smi.*;
import org.snmp4j.transport.DefaultUdpTransportMapping;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

public class SnmpManagerImpl implements SnmpManager {

    private String ipAddress;
    private String port;
    private String MIB;
    private int snmpVersion;
    private String community;

    public SnmpManagerImpl(String ipAddress, String port, String MIB, String community) {
        this.ipAddress = ipAddress;
        this.port = port;
        this.MIB = MIB;
        this.snmpVersion = SnmpConstants.version2c;
        this.community = community;
    }

    @Override
    public void runSnmpManager() throws IOException {
        TransportMapping transportMapping = new DefaultUdpTransportMapping();
        transportMapping.listen();
        CommunityTarget communityTarget = setCommunityTarget(community, snmpVersion, ipAddress, port);
        PDU pdu = setPDU(MIB);
        Snmp snmp = new Snmp(transportMapping);
        getResponseEvent(communityTarget, pdu, snmp);
    }

    @Override
    public void getResponseEvent(CommunityTarget communityTarget, PDU pdu, Snmp snmp) throws IOException {
        ResponseEvent responseEvent = snmp.get(pdu, communityTarget);
        if (responseEvent != null) {
            PDU responsePDU = responseEvent.getResponse();
            if (responsePDU != null) {
                if (responsePDU.getErrorStatus() == PDU.noError) {
                    saveResponseToFile(String.valueOf(responsePDU.getVariableBindings()));
                } else {
                    saveResponseToFile("[Error] Request Failed" +
                            " Error Status = " + responsePDU.getErrorStatus() +
                            " Error Index = " + responsePDU.getErrorIndex() +
                            " Error Status Text = " + responsePDU.getErrorStatusText());
                }
            } else {
                saveResponseToFile("[Error] Response PDU is null");
            }
        } else {
            saveResponseToFile("[Error] Agent Timeout");
        }
        snmp.close();
    }

    @Override
    public void saveResponseToFile(String response) {
        try (PrintStream out = new PrintStream(new FileOutputStream("response.txt"))) {
            out.print(response);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public CommunityTarget setCommunityTarget(String community, int snmpVersion, String ipAddress, String port) {
        CommunityTarget communityTarget = new CommunityTarget();
        communityTarget.setCommunity(new OctetString(community));
        communityTarget.setVersion(snmpVersion);
        communityTarget.setAddress(new UdpAddress(ipAddress + "/" + port));
        communityTarget.setRetries(5);
        communityTarget.setTimeout(3000);
        return communityTarget;
    }

    @Override
    public PDU setPDU(String number) {
        PDU pdu = new PDU();
        pdu.add(new VariableBinding(new OID(number)));
        pdu.setType(PDU.GET);
        pdu.setRequestID(new Integer32(1));
        return pdu;
    }
}