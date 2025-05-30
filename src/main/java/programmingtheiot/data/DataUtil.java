/**
 * This class is part of the Programming the Internet of Things project.
 * 
 * It is provided as a simple shell to guide the student and assist with
 * implementation for the Programming the Internet of Things exercises,
 * and designed to be modified by the student as needed.
 */ 

package programmingtheiot.data;

import java.util.Iterator;
import java.util.logging.Logger;

import org.json.JSONObject;

import com.google.gson.Gson;
/**
 * Shell representation of class for student implementation.
 *
 */
public class DataUtil
{
	// static
	private static final Logger _Logger  =
	Logger.getLogger(DataUtil.class.getName());

	private static final DataUtil _Instance = new DataUtil();
	/**
	 * Returns the Singleton instance of this class.
	 * 
	 * @return ConfigUtil
	 */
	public static final DataUtil getInstance()
	{
		return _Instance;
	}
	
	
	// private var's
	
	
	// constructors
	
	/**
	 * Default (private).
	 * 
	 */
	private DataUtil()
	{
		super();
	}
	
	
	// public methods
	
	public String actuatorDataToJson(ActuatorData actuatorData)
	{
		String jsonData =null;

		if (actuatorData !=null) {
			Gson gson = new Gson();
			jsonData =gson.toJson(actuatorData);
				}

		return jsonData;
	}
	
	public String sensorDataToJson(SensorData sensorData)
	{
		String jsonData =null;

		if (sensorData !=null) {
			Gson gson = new Gson();
			jsonData =gson.toJson(sensorData);
				}

		return jsonData;
	}
	
	public String systemPerformanceDataToJson(SystemPerformanceData sysPerfData)
	{
		String jsonData =null;

		if (sysPerfData !=null) {
			Gson gson = new Gson();
			jsonData =gson.toJson(sysPerfData);
				}

		return jsonData;
	}
	
	public String systemStateDataToJson(SystemStateData sysStateData)
	{
		String jsonData = null;
		
		if (sysStateData != null) {	
			Gson gson = new Gson();		
			jsonData = gson.toJson(sysStateData);
		}
		return jsonData;
	}
	
	public ActuatorData jsonToActuatorData(String jsonData)
	{
		ActuatorData actuatorData =null;

		if (jsonData !=null &&jsonData.trim().length() >0) {
			Gson gson =new Gson();
			actuatorData =gson.fromJson(jsonData,ActuatorData.class);
				}

		return actuatorData;
	}
	
	public SensorData jsonToSensorData(String jsonData)
	{
		SensorData sensorData =null;

		if (jsonData !=null &&jsonData.trim().length() >0) {
			Gson gson =new Gson();
			sensorData =gson.fromJson(jsonData,SensorData.class);
				}

		return sensorData;
	}
	
	public SystemPerformanceData jsonToSystemPerformanceData(String jsonData)
	{
		SystemPerformanceData sysPerfData =null;

		if (jsonData !=null &&jsonData.trim().length() >0) {
			Gson gson = new Gson();
			sysPerfData =gson.fromJson(jsonData,SystemPerformanceData.class);
				}

		return sysPerfData;
	}
	
	public SystemStateData jsonToSystemStateData(String jsonData)
	{
		SystemStateData sysStateData = null;
		try {
			Gson gson =new Gson();
			sysStateData = gson.fromJson(jsonData, SystemStateData.class);
		} catch (Exception e) {
			_Logger.warning("Error parsing JSON: " + e.getMessage());
		}
		return sysStateData;
	}
	

	public String payloadToCloudPayload(String payload){
		
		String cloudPayload = null;
		JSONObject oldPayloadjson = new JSONObject(payload);
		JSONObject cloudPayloadJson = new JSONObject();
		cloudPayloadJson.put("value", oldPayloadjson.get("value"));
		//cloudPayloadJson.put("timestamp", oldPayloadjson.get("timeStampMillis"));

        // Copy the rest of old payload in "context"
        JSONObject context = new JSONObject();
        Iterator<String> keys = oldPayloadjson.keys();
        while (keys.hasNext()) {
            String key = keys.next();
            if (!key.equals("value") && !key.equals("timeStampMillis")) {
                context.put(key, oldPayloadjson.get(key));
            }
        }

        cloudPayloadJson.put("context", context);

		// Convert the cloud payload to a string
		_Logger.info("Cloud payload: " + cloudPayloadJson.toString(2));
		cloudPayload = cloudPayloadJson.toString();

		return cloudPayload;
	}

	public String cloudPayloadToPayload(String payload){
		// Convert the Cloud payload to a format that can be translate to BaseIoTData
		// Parse the cloud-style JSON
		JSONObject cloudPayloadJson = new JSONObject(payload);

		JSONObject originalPayloadJson = new JSONObject();
	
		originalPayloadJson.put("value", cloudPayloadJson.get("value"));
		//originalPayloadJson.put("timeStampMillis", cloudPayloadJson.get("timestamp"));

		if (cloudPayloadJson.has("context")) {
			JSONObject context = cloudPayloadJson.getJSONObject("context");
	
			Iterator<String> keys = context.keys();
			while (keys.hasNext()) {
				String key = keys.next();
				originalPayloadJson.put(key, context.get(key));
			}
		}
	
		return originalPayloadJson.toString();
	}


}
