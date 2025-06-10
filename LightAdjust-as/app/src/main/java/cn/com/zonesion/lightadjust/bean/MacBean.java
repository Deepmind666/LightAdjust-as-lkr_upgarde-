package cn.com.zonesion.lightadjust.bean;

public class MacBean {
	private String sensora;
	private String sensorb;
	private String sensorc;

	public MacBean() {
		super();
	}

	public MacBean(String sensora, String sensorb, String sensorc) {
		super();
		this.sensora = sensora;
		this.sensorb = sensorb;
		this.sensorc = sensorc;
	}

	public String getSensora() {
		return sensora;
	}

	public void setSensora(String sensora) {
		this.sensora = sensora;
	}

	public String getSensorb() {
		return sensorb;
	}

	public void setSensorb(String sensorb) {
		this.sensorb = sensorb;
	}

	public String getSensorc() {
		return sensorc;
	}

	public void setSensorc(String sensorc) {
		this.sensorc = sensorc;
	}

}
