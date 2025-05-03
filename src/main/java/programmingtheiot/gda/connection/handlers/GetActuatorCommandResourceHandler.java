package programmingtheiot.gda.connection.handlers;


import java.util.logging.Logger;

import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.coap.CoAP.ResponseCode;
import org.eclipse.californium.core.server.resources.CoapExchange;

import programmingtheiot.common.IActuatorDataListener;
import programmingtheiot.data.ActuatorData;
import programmingtheiot.data.DataUtil;


public class GetActuatorCommandResourceHandler extends CoapResource implements IActuatorDataListener {
    // static

	// logging infrastructure - should already be defined, although you'll need
	// to update the class name as shown below
    private static final Logger _Logger = Logger.getLogger(GetActuatorCommandResourceHandler.class.getName());

    // params
    private ActuatorData actuatorData = new ActuatorData();


    // constructors

	// ... add constructor and method implementations here ...

    public GetActuatorCommandResourceHandler(String resourceName)
    {
        super(resourceName);
        super.setObservable(true); // permite que clientes se suscriban con OBSERVE
    }


    @Override
    public boolean onActuatorDataUpdate(ActuatorData data)
    {
        if (data != null && this.actuatorData != null) {
            this.actuatorData.updateData(data);
            super.changed(); // notifica a los observadores

            _Logger.fine("Actuator data updated for URI: " + super.getURI() + ": Data value = " + this.actuatorData.getValue());

            return true;
        }

        return false;
    }

    @Override
	public void handleGET(CoapExchange context)
	{
		_Logger.info("GET request received on: " + super.getURI());

		context.accept();

		String jsonData = DataUtil.getInstance().actuatorDataToJson(this.actuatorData);

		context.respond(ResponseCode.CONTENT, jsonData);
	}
}
