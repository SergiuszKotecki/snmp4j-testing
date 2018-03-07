package com.printerservice.service.impl;

import com.printerservice.service.SnmpManager;
import org.snmp4j.*;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.smi.*;
import org.snmp4j.transport.DefaultUdpTransportMapping;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.SimpleDateFormat;
import java.util.Date;

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
    public String runSnmpManager() throws IOException {
        TransportMapping transportMapping = new DefaultUdpTransportMapping();
        transportMapping.listen();
        CommunityTarget communityTarget = setCommunityTarget(community, snmpVersion, ipAddress, port);
        PDU pdu = setPDU(MIB);
        Snmp snmp = new Snmp(transportMapping);
        String responseEvent = getResponseEvent(communityTarget, pdu, snmp);
        stopSnmpManager(snmp);
        return responseEvent;
    }

    private void stopSnmpManager(Snmp snmp) throws IOException {
        snmp.close();
    }

    private String getResponseEvent(CommunityTarget communityTarget, PDU pdu, Snmp snmp) throws IOException {
        ResponseEvent responseEvent = snmp.get(pdu, communityTarget);
        String response;
        PDU responsePDU = responseEvent.getResponse();
        if (responsePDU != null && responsePDU.getErrorStatus() == PDU.noError) {
            response = String.valueOf(responsePDU.getVariableBindings());
            saveResponseToFile(response);
            return response;
        } else {
            response = "[Error] Response PDU is null";
            saveResponseToFile(response);
            throw new MessageException(response);
        }
    }

    private void saveResponseToFile(String response) {
        String date = new SimpleDateFormat("dd/mm/yyyy HH:mm:ss").format(new Date());
        try {
            Files.write(Paths.get("response.txt"), ("[" + date + "] Response: " + response + "\r\n").getBytes(), StandardOpenOption.APPEND);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private CommunityTarget setCommunityTarget(String community, int snmpVersion, String ipAddress, String port) {
        CommunityTarget communityTarget = new CommunityTarget();
        communityTarget.setCommunity(new OctetString(community));
        communityTarget.setVersion(snmpVersion);
        communityTarget.setAddress(new UdpAddress(ipAddress + "/" + port));
        communityTarget.setRetries(1);
        communityTarget.setTimeout(1000);
        return communityTarget;
    }

    private PDU setPDU(String number) {
        PDU pdu = new PDU();
        pdu.add(new VariableBinding(new OID(number)));
        pdu.setType(PDU.GET);
        pdu.setRequestID(new Integer32(1));
        return pdu;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public String getPort() {
        return port;
    }

    public String getMIB() {
        return MIB;
    }

    public int getSnmpVersion() {
        return snmpVersion;
    }

    public String getCommunity() {
        return community;
    }
}