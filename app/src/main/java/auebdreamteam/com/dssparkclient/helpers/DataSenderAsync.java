/*
 * Copyright (c) 2019.
 * Created for MSc in Computer Science - Distributed Systems
 * All right reserved except otherwise noted
 */

package auebdreamteam.com.dssparkclient.helpers;

import android.content.Context;
import android.os.AsyncTask;
import android.os.PowerManager;
import android.support.v4.app.Fragment;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Base64;
import java.util.List;

import auebdreamteam.com.dssparkclient.entities.BaseQueryClass;
import auebdreamteam.com.dssparkclient.ui.BaseFragment;

public class DataSenderAsync extends AsyncTask<BaseQueryClass, String, List<Object>> {

	private PowerManager.WakeLock mWakeLock;
	private Context context;
	private BaseFragment fragment;

	public DataSenderAsync(BaseFragment fragment, Context context) {
		this.fragment = fragment;
		this.context = context;
	}

	@Override
	protected List<Object> doInBackground(BaseQueryClass... query) {
		PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
		mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "ds:sendWakelock");
		mWakeLock.acquire();
		List<Object> toReturn = null;

		try (
				Socket requestSocket = new Socket(query[0].getServerIP(), 4322);
				ObjectOutputStream out = new ObjectOutputStream(requestSocket.getOutputStream());
				ObjectInputStream in = new ObjectInputStream(requestSocket.getInputStream())
		) {
			System.out.println("Connection established!");
			out.writeObject(query[0]);
			out.flush();
			System.err.println("Item sent!");

			Object obj = in.readObject();
			System.err.println("Item received!");
			if(obj instanceof List<?>) {
				toReturn = (List<Object>) obj;
			}

		} catch (UnknownHostException unknownHost) {
			System.err.println("You are trying to connect to an unknown host!");
		} catch (IOException ioException) {
			ioException.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return toReturn;
	}

	@Override
	protected void onPostExecute(List<Object> toReturn) {
		fragment.onResult(toReturn);
	}
}
