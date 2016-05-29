/**
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */

package com.audite;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

/**
 * This class lets the user select the way to interact with the robot. It's just
 * a list of items with a title, a small description of what the activity does
 * and the class name that will be started when the element gets clicked.
 */
public class ActionListActivity extends BluetoothActivity
{
	private ArrayList<Action> activityList = new ArrayList<Action>();
	private ListView lvActionList;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.action_select);

		lvActionList = (ListView) findViewById(R.id.lvActionList);

		// Remote control activities
		activityList.add(new Action("Voice Control", "Control robot with oral instructions", "VoiceControl"));

		activityList.add(new Action("Send Data", "Send custom commands to robot", "SendData"));

		lvActionList.setAdapter(new ActionListBaseAdapter(this, activityList));
		lvActionList.setOnItemClickListener(new OnItemClickListener()
		{
			public void onItemClick(AdapterView<?> arg0, View v, int position, long id)
			{
				String activity = activityList.get(position).getClassName();

				try
				{
					// Start the selected activity and prevent quitting
					preventCancel = true;
					Class<?> activityClass = Class.forName("com.audite.activities." + activity);
					Intent intent = new Intent(ActionListActivity.this, activityClass);
					startActivityForResult(intent, 0);
				}
				catch(ClassNotFoundException e)
				{
					e.printStackTrace();
				}
			}
		});
	}

	@Override
	public boolean handleMessage(Message msg)
	{
		super.handleMessage(msg);
		// When a child activity returns it passes ok or cancel message
		if(msg.what == BluetoothRemoteControlApp.MSG_OK)
		{
			// When quitting an activity automatically reset the robot
			write("r");
		}
		return false;
	}

	@Override
	public void onBackPressed()
	{
		// When quitting the activity select reset and disconnect from the device
		disconnect();
		super.onBackPressed();
	}
}
