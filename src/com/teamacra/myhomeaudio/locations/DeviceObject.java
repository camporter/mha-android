package com.teamacra.myhomeaudio.locations;

public class DeviceObject{
		public final int id;
		public final int rssi;

		public DeviceObject(int id, int rssi) {
			this.id = id;
			this.rssi = rssi;
		}

		public String toJSONString() {
			String jsonOutput = "";
			
			jsonOutput += "{ \"name\" : \""+id+"\", ";
			jsonOutput += "\"rssi\" : "+rssi +" }";
		
			//jsonOutput += " ]";
			return jsonOutput;
		}
}
