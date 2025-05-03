/**
 * This class is part of the Programming the Internet of Things project.
 * 
 * It is provided as a simple shell to guide the student and assist with
 * implementation for the Programming the Internet of Things exercises,
 * and designed to be modified by the student as needed.
 */ 

package programmingtheiot.gda.connection;


import java.util.List;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.CoapServer;
import org.eclipse.californium.core.config.CoapConfig;
import org.eclipse.californium.core.network.Endpoint;
import org.eclipse.californium.core.network.interceptors.MessageTracer;
import org.eclipse.californium.core.server.resources.Resource;
import org.eclipse.californium.elements.config.UdpConfig;

import programmingtheiot.common.IDataMessageListener;
import programmingtheiot.common.ResourceNameEnum;
import programmingtheiot.gda.connection.handlers.GetActuatorCommandResourceHandler;
import programmingtheiot.gda.connection.handlers.UpdateSystemPerformanceResourceHandler;
import programmingtheiot.gda.connection.handlers.UpdateTelemetryResourceHandler;

/**
 * Shell representation of class for student implementation.
 * 
 */
public class CoapServerGateway
{
	// static
	static {
		CoapConfig.register();
		UdpConfig.register();
	}

	private static final Logger _Logger =
		Logger.getLogger(CoapServerGateway.class.getName());
	
	// params
	
	private CoapServer coapServer = null;
	
	private IDataMessageListener dataMsgListener = null;
	
	
	// constructors
	
	/**
	 * Constructor.
	 * 
	 * @param dataMsgListener
	 */
	public CoapServerGateway(IDataMessageListener dataMsgListener)
	{
		super();
		
		/*
		 * Basic constructor implementation provided. Change as needed.
		 */
		
		this.dataMsgListener = dataMsgListener;
		this.coapServer = new CoapServer();
		
		initServer();

	}
		
	// public methods
	
	public void addResource(ResourceNameEnum name, String endName, Resource resource)
	{
		if (name == null || resource == null) {
			_Logger.warning("Resource or name is null. Skipping add.");
			return;
		}
	
		String[] pathSegments = name.getResourceName().split("/");
	
		CoapResource current = null;
	
		// Si ya hay un recurso raíz con el primer nombre
		Resource existingRoot = this.coapServer.getRoot().getChild(pathSegments[0]);
	
		if (existingRoot instanceof CoapResource) {
			current = (CoapResource) existingRoot;
		} else {
			current = new CoapResource(pathSegments[0]);
			this.coapServer.add(current);
		}
	
		// Añadir el resto de los niveles en el path
		for (int i = 1; i < pathSegments.length - 1; i++) {
			Resource child = current.getChild(pathSegments[i]);
	
			if (child instanceof CoapResource) {
				current = (CoapResource) child;
			} else {
				CoapResource newChild = new CoapResource(pathSegments[i]);
				current.add(newChild);
				current = newChild;
			}
		}
	
		// Añadir el recurso real (handler) al último nivel
		resource.setName(endName != null ? endName : pathSegments[pathSegments.length - 1]);
		current.add(resource);
	
		_Logger.info("Added CoAP resource: " + name.getResourceName());

		if (name != null && resource != null) {
		// break out the hierarchy of names and build the resource
		// handler generation(s) as needed, checking if any parent already
		// exists - and if so, add to the existing resource
			createAndAddResourceChain(name, resource);
		}
		
		
		
	}
	
	public boolean hasResource(String name)
	{
		return false;
	}
	
	public void setDataMessageListener(IDataMessageListener listener)
	{
		if (listener != null) {
			this.dataMsgListener = listener;
		}
	}
	
	public boolean startServer()
	{
		try {
			if (this.coapServer != null) {
				this.coapServer.start();
	
				// for message logging
				for (Endpoint ep : this.coapServer.getEndpoints()) {
					ep.addInterceptor(new MessageTracer());
				}
	
				return true;
			} else {
				_Logger.warning("CoAP server START failed. Not yet initialized.");
			}
		} catch (Exception e) {
			_Logger.log(Level.SEVERE, "Failed to start CoAP server.", e);
		}
		
		return false;
	}
	
	public boolean stopServer()
	{
		try {
			if (this.coapServer != null) {
				this.coapServer.stop();
	
				return true;
			} else {
				_Logger.warning("CoAP server STOP failed. Not yet initialized.");
			}
		} catch (Exception e) {
			_Logger.log(Level.SEVERE, "Failed to stop CoAP server.", e);
		}
		
		return false;
	}
	
	
	// private methods
	
	private Resource createResourceChain(ResourceNameEnum resource)
	{
		return null;
	}
	
	private void initServer(ResourceNameEnum ...resources)
	{
		//addResource(ResourceNameEnum.GDA_SENSOR_MSG_RESOURCE, "sensor", new GenericCoapResourceHandler(dataMsgListener));
		this.coapServer = new CoapServer();
		initDefaultResources();
	}

	private void initDefaultResources()
	{
		// initialize pre-defined resources
		GetActuatorCommandResourceHandler getActuatorCmdResourceHandler =
		new GetActuatorCommandResourceHandler(
		ResourceNameEnum.CDA_ACTUATOR_CMD_RESOURCE.getResourceType());

		if (this.dataMsgListener !=null) {
		this.dataMsgListener.setActuatorDataListener(null,getActuatorCmdResourceHandler);
			}

		addResource(ResourceNameEnum.CDA_ACTUATOR_CMD_RESOURCE,null,getActuatorCmdResourceHandler);

		UpdateTelemetryResourceHandler updateTelemetryResourceHandler =
		new UpdateTelemetryResourceHandler(
		ResourceNameEnum.CDA_SENSOR_MSG_RESOURCE.getResourceType());

		updateTelemetryResourceHandler.setDataMessageListener(this.dataMsgListener);

		addResource(
		ResourceNameEnum.CDA_SENSOR_MSG_RESOURCE,null,updateTelemetryResourceHandler);

		UpdateSystemPerformanceResourceHandler updateSystemPerformanceResourceHandler =
		new UpdateSystemPerformanceResourceHandler(
		ResourceNameEnum.CDA_SYSTEM_PERF_MSG_RESOURCE.getResourceType());

		updateSystemPerformanceResourceHandler.setDataMessageListener(this.dataMsgListener);

		addResource(
		ResourceNameEnum.CDA_SYSTEM_PERF_MSG_RESOURCE,null,updateSystemPerformanceResourceHandler);
	}


	private void createAndAddResourceChain(ResourceNameEnum resourceType, Resource resource) {
		_Logger.info("Adding server resource handler chain: " +resourceType.getResourceName());

		List<String>resourceNames =resourceType.getResourceNameChain();
		Queue<String>queue =new ArrayBlockingQueue<>(resourceNames.size());

		queue.addAll(resourceNames);

		// check if we have a parent resource
		Resource parentResource =this.coapServer.getRoot();

		// if no parent resource, add it in now (should be named "PIOT")
		if (parentResource ==null) {
		parentResource =new CoapResource(queue.poll());
		this.coapServer.add(parentResource);
			}

		while (!queue.isEmpty()) {
		// get the next resource name
		String resourceName =queue.poll();
		Resource nextResource =parentResource.getChild(resourceName);

		if (nextResource ==null) {
		if (queue.isEmpty()) {
		nextResource =resource;
		nextResource.setName(resourceName);
					}else {
		nextResource =new CoapResource(resourceName);
					}

		parentResource.add(nextResource);
				}

		parentResource =nextResource;
			}
		}
}

