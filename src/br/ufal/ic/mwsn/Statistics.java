package br.ufal.ic.mwsn;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

public class Statistics {
	private long saltos;
	private int posX;
	private long temp;
	private float speed;

	private static Statistics instance;

	private Statistics() {
	}

	public static Statistics getInstance() {
		if (instance == null)
			instance = new Statistics();
		return instance;
	}

	public long getSaltos() {
		return saltos;
	}

	public void incrementSaltos() {
		this.saltos += saltos;
	}

	private String getMD5Hash(String str) throws NoSuchAlgorithmException {

		MessageDigest md = MessageDigest.getInstance("MD5");
		md.update(str.getBytes());

		byte byteData[] = md.digest();

		// convert the byte to hex format method 1
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < byteData.length; i++) {
			sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
		}

		// System.out.println("Digest(in hex format):: " + sb.toString());

		// convert the byte to hex format method 2
		StringBuffer hexString = new StringBuffer();
		for (int i = 0; i < byteData.length; i++) {
			String hex = Integer.toHexString(0xff & byteData[i]);
			if (hex.length() == 1)
				hexString.append('0');
			hexString.append(hex);
		}
		return hexString.toString();

	}

	public float getSpeed(String dataArray[]) {
		Map<String, Statistics> statisticsSpeedMap = new HashMap<>();
		for (String string : dataArray) {
			String data[] = string.split(",");
			if (statisticsSpeedMap.containsKey(data[0])) {
				long tempo = Long.parseLong(data[1]);
				int posx = Integer.parseInt(data[2]);
				System.out.println("here: " + posx);
				Statistics aux = statisticsSpeedMap.get(data[0]);
				if (posx > (aux.posX - 25) && posx < (aux.posX + 25)) {
					aux.temp = (aux.temp + tempo) / 2;
					System.out.println("aux.temp 1: " + aux.temp);
					statisticsSpeedMap.put(data[0], aux);
				}
			} else {
				long tempo = Long.parseLong(data[1].replace(' ', '0'));
				int posx = Integer.parseInt(data[2]);
				Statistics aux = new Statistics();
				if (posx > (aux.posX - 25) && posx < (aux.posX + 25)) {
					aux.temp = (aux.temp + tempo) / 2;
					System.out.println("aux.temp 2: " + aux.temp);
					statisticsSpeedMap.put(data[0], aux);
				}
			}

		}
		for (Map.Entry<String, Statistics> entry : statisticsSpeedMap.entrySet()) {
			this.speed = entry.getValue().speed / statisticsSpeedMap.size();
		}

		return this.speed;
	}

	public Map<String, Statistics> getStatisticsPerData(String dataArray[]) throws NoSuchAlgorithmException {
		Map<String, Statistics> statiscsDataMap = new HashMap<>();

		for (String string : dataArray) {
			String key = getMD5Hash(string);
			if (statiscsDataMap.containsKey(key)) {
				statiscsDataMap.get(key).incrementSaltos();
			} else {
				Statistics statistics = new Statistics();
				statistics.incrementSaltos();
				statiscsDataMap.put(key, statistics);
			}

		}
		return statiscsDataMap;
	}

}
