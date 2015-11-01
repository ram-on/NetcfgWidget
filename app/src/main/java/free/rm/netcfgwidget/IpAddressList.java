package free.rm.netcfgwidget;

import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * A list of {@link IPAddress}.
 */
public class IpAddressList extends ArrayList<IPAddress> {
	@Override
	public String toString() {
		StringBuilder buffer = new StringBuilder();
		String maxInterfaceLen = Integer.toString(getMaxInterfaceLength() + 1);

		for (IPAddress ipAddress : this) {
			buffer.append(ipAddress.toString(maxInterfaceLen));
			buffer.append('\n');
		}

		// deletes the last '\n' character and return it as a string
		return buffer.deleteCharAt(buffer.length() - 1).toString();
	}


	/**
	 * Returns the length of the widest interface name.
	 *
	 * @return Maximum expected length of any stored interface name.
	 */
	private int getMaxInterfaceLength() {
		int maxLen = 0;

		for (IPAddress ipAddress : this) {
			if (ipAddress.getInterface().length() > maxLen) {
				maxLen = ipAddress.getInterface().length();
			}
		}

		return maxLen;
	}
}
