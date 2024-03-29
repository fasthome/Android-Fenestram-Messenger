/*
 * Copyright (C) 2009 The Sipdroid Open Source Project
 * 
 * This file is part of Sipdroid (http://www.sipdroid.org)
 * 
 * Sipdroid is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This source code is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this source code; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package io.fasthome.fenestram_messenger.call_impl.core.sipdroid.sipua.ui;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.IBinder;

import io.fasthome.fenestram_messenger.call_impl.core.sipdroid.media.RtpStreamReceiver;

public class RegisterService extends Service {
	Receiver m_receiver;
	Caller m_caller;
	
    public void onDestroy() {
		super.onDestroy();
		if (m_receiver != null) {
			unregisterReceiver(m_receiver);
			m_receiver = null;
		}
		Receiver.alarm(0, OneShotAlarm2.class);
	}
    
    @Override
    public void onCreate() {
    	super.onCreate();
    	Receiver.sContext = this;
    	if (Receiver.mContext == null) Receiver.mContext = this;
        if (m_receiver == null) {
			 IntentFilter intentfilter = new IntentFilter();
			 intentfilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
			 intentfilter.addAction(Receiver.ACTION_DATA_STATE_CHANGED);
			 intentfilter.addAction(Receiver.ACTION_PHONE_STATE_CHANGED);
			 intentfilter.addAction(Receiver.ACTION_DOCK_EVENT);
			 intentfilter.addAction(Intent.ACTION_HEADSET_PLUG);
			 intentfilter.addAction(Intent.ACTION_USER_PRESENT);
			 intentfilter.addAction(Intent.ACTION_SCREEN_OFF);
			 intentfilter.addAction(Intent.ACTION_SCREEN_ON);
			 intentfilter.addAction(Receiver.ACTION_VPN_CONNECTIVITY);
			 intentfilter.addAction(Receiver.ACTION_SCO_AUDIO_STATE_CHANGED);
			 intentfilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
	         registerReceiver(m_receiver = new Receiver(), intentfilter);      
	         intentfilter = new IntentFilter();
        }
        Receiver.engine(this).isRegistered();
    }
    
    @Override
    public void onStart(Intent intent, int id) {
         super.onStart(intent,id);
         RtpStreamReceiver.restoreSettings();
         Receiver.alarm(10*60, OneShotAlarm2.class);
    }

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}
	
}
