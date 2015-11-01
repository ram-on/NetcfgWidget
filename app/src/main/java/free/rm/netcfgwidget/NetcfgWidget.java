package free.rm.netcfgwidget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * A widget that outputs a list of ip addresses together with their (network) interface name.
 */
public class NetcfgWidget extends AppWidgetProvider {

	private static final String TAG = "NetcfgWidget";
	private static final String REFRESH_CLICKED = "NetcfgWidget.REFRESH_CLICKED";
	private int appWidgetID = 0;
	
	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
		// there may be multiple widgets active, so update all of them
		final int N = appWidgetIds.length;
		for (int i = 0; i < N; i++) {
			updateAppWidget(context, appWidgetManager, appWidgetIds[i]);
		}
	}


	private void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
		Log.i(TAG, "Updating widget...");

		// construct the RemoteViews object:  push the ip addresses into ipAddressesTextView
		RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.netcfg_widget);
		views.setTextViewText(R.id.ipAddressesTextView, getIpAddressesString(context));

		// refresh this widget when clicked
		Intent intent = new Intent(context, getClass());
		intent.setAction(REFRESH_CLICKED);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
		views.setOnClickPendingIntent(R.id.ipAddressesTextView, pendingIntent);
		
		// instruct the widget manager to update the widget
		appWidgetManager.updateAppWidget(appWidgetId, views);
	}


	@Override
	public void onReceive(Context context, Intent intent) {
		super.onReceive(context, intent);

		// will be called when the user clicks on the widget
		if (REFRESH_CLICKED.equals(intent.getAction())) {
			Log.i(TAG, "Widget clicked");
			Toast.makeText(context, context.getString(R.string.refreshing_ips), Toast.LENGTH_LONG).show();

			// forces the AppWidgetManager to refresh the widget
			AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
			ComponentName name = new ComponentName(context, NetcfgWidget.class);
			int[] widgetIDs = AppWidgetManager.getInstance(context).getAppWidgetIds(name);
			onUpdate(context, appWidgetManager, widgetIDs);
		}
	}


	/**
	 * Returns IP addresses, that this device has, as a formatted string.
	 *
	 * @param context
	 * @return IP addresses string
	 */
	private String getIpAddressesString(Context context) {
		String ipAddressesString;

		// get ip addresses and save them as a string in ipAddressesString
		try {
			ipAddressesString = getIPAddresses().toString();
		} catch (Exception e) {
			String message = context.getString(R.string.netcfg_error);
			Log.e(TAG, message, e);
			ipAddressesString = message + ":\n➥ " + e.getLocalizedMessage();
		}

		return ipAddressesString;
	}


	/**
	 * Returns a list of {@link IPAddress} by executing the 'netcfg' external command.
	 *
	 * @return IP addresses
	 */
	private ArrayList<IPAddress> getIPAddresses() throws Exception {
		IpAddressList ipAddresses = new IpAddressList();

		Process process = Runtime.getRuntime().exec("netcfg");
		process.waitFor();

		BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

		String line;
		while ((line = reader.readLine())!= null) {
			// append the ip address if the interface is UP and running
			if (line.contains("UP")) {
				ipAddresses.add( new IPAddress(line) );
			}
		}

		return ipAddresses;
	}

}
