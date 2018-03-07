package com.printerservice.service;

import org.snmp4j.CommunityTarget;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;

import java.io.IOException;

public interface SnmpManager {
    void runSnmpManager() throws IOException;

    void getResponseEvent(CommunityTarget communityTarget, PDU pdu, Snmp snmp) throws IOException;

    void saveResponseToFile(String response);

    CommunityTarget setCommunityTarget(String community, int snmpVersion, String ipAddress, String port);

    PDU setPDU(String number);


}
