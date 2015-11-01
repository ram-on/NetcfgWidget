package free.rm.netcfgwidget;

import android.util.Log;

/**
 * Represents an IP address.
 */
public class IPAddress {
	/** Network interface name */
	private String	interf;
	/** IP address */
	private String	ip;

	public IPAddress(String netcfgOuptputLine) throws Exception {
		String[] 	out = netcfgOuptputLine.split("\\s+");

		// netcfg output line:  <interface>  <up/down>  <ip addr>  <hex number>  <mac address>
		if (out.length == 5) {
			this.interf = out[0];
			this.ip = out[2];
		} else {
			throw new Exception(String.format("Netcfg output is not as expected (%d):  '%s'", out.length, netcfgOuptputLine));
		}
	}

	/**
	 * Converts the IP address to {@link String}.
	 *
	 * @param interfWhiteSpaces The number of white spaces to be added to the interface (interf)
	 * @return IP address as a {@link String}.
	 */
	public String toString(String interfWhiteSpaces) {
		return String.format("%-" + interfWhiteSpaces + "s%s", interf, ip);
	}

	public String getInterface() {
		return interf;
	}

	public String getIp() {
		return ip;
	}
}
