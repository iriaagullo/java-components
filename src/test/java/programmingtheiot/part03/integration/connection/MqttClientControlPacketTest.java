/**
 * 
 * This class is part of the Programming the Internet of Things
 * project, and is available via the MIT License, which can be
 * found in the LICENSE file at the top level of this repository.
 * 
 * Copyright (c) 2020 by Andrew D. King
 */ 

package programmingtheiot.part03.integration.connection;

import java.util.logging.Logger;

import org.junit.After;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import org.junit.Before;
import org.junit.Test;

import programmingtheiot.common.ResourceNameEnum;
import programmingtheiot.gda.connection.MqttClientConnector;

/**
 * This test case class contains very basic integration tests for
 * MqttClientControlPacketTest. It should not be considered complete,
 * but serve as a starting point for the student implementing
 * additional functionality within their Programming the IoT
 * environment.
 *
 */
public class MqttClientControlPacketTest
{
	// static
	
	private static final Logger _Logger =
		Logger.getLogger(MqttClientControlPacketTest.class.getName());
	
	
	// member var's
	
	private MqttClientConnector mqttClient = null;
	
	
	// test setup methods
	
	@Before
	public void setUp() throws Exception
	{
		this.mqttClient = new MqttClientConnector();
	}
	
	@After
	public void tearDown() throws Exception
	{
	}
	
	// test methods
	
	@Test
	public void testConnectAndDisconnect()
	{
		// TODO: implement this test
		try {
			boolean connected = this.mqttClient.connectClient();
	
			assertTrue("MQTT client should connect successfully.", connected);
	
			Thread.sleep(2000); // espera corta para asegurarse de que CONNECT/CONNACK se completen
	
			boolean disconnected = this.mqttClient.disconnectClient();
	
			assertTrue("MQTT client should disconnect successfully.", disconnected);
		} catch (Exception e) {
			fail("Exception during connect/disconnect: " + e.getMessage());
		}
	}
	
	@Test
	public void testServerPing()
	{
		// TODO: implement this test
		try {
			boolean connected = this.mqttClient.connectClient();
			assertTrue(connected);
	
			// Forzar ping es indirecto: solo esperar sin enviar nada por un momento.
			Thread.sleep(65000); // Mayor al keep-alive para forzar el envío de PINGREQ
	
			this.mqttClient.disconnectClient();
		} catch (Exception e) {
			fail("Exception during ping test: " + e.getMessage());
		}
	}
	
	@Test
	public void testPubSub()
	{
		// TODO: implement this test
		// 
		// IMPORTANT: be sure to use QoS 1 and 2 to see ALL control packets
		try {
			boolean connected = this.mqttClient.connectClient();
			assertTrue(connected);
	
			String testPayload = "Control packet test payload";
			int qos = 2;
			this.mqttClient.subscribeToTopic(ResourceNameEnum.CDA_SENSOR_MSG_RESOURCE, qos);

			Thread.sleep(1000);
			
			this.mqttClient.publishMessage(ResourceNameEnum.CDA_SENSOR_MSG_RESOURCE, testPayload, qos);

			Thread.sleep(2000);

			this.mqttClient.unsubscribeFromTopic(ResourceNameEnum.CDA_SENSOR_MSG_RESOURCE);
	
			this.mqttClient.disconnectClient();
			
		} catch (Exception e) {
			fail("Exception during pub/sub test: " + e.getMessage());
		}
	
	}
	
}
